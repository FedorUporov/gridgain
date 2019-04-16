﻿/*
 *                   GridGain Community Edition Licensing
 *                   Copyright 2019 GridGain Systems, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License") modified with Commons Clause
 * Restriction; you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * Commons Clause Restriction
 * 
 * The Software is provided to you by the Licensor under the License, as defined below, subject to
 * the following condition.
 * 
 * Without limiting other conditions in the License, the grant of rights under the License will not
 * include, and the License does not grant to you, the right to Sell the Software.
 * For purposes of the foregoing, “Sell” means practicing any or all of the rights granted to you
 * under the License to provide to third parties, for a fee or other consideration (including without
 * limitation fees for hosting or consulting/ support services related to the Software), a product or
 * service whose value derives, entirely or substantially, from the functionality of the Software.
 * Any license notice or attribution required by the License must also include this Commons Clause
 * License Condition notice.
 * 
 * For purposes of the clause above, the “Licensor” is Copyright 2019 GridGain Systems, Inc.,
 * the “License” is the Apache License, Version 2.0, and the Software is the GridGain Community
 * Edition software provided with this notice.
 */

namespace Apache.Ignite.Core.Tests.Binary
{
    using System.Collections;
    using System.Linq;
    using Apache.Ignite.Core.Binary;
    using Apache.Ignite.Core.Tests.Compute;
    using NUnit.Framework;

    /// <summary>
    /// Tests compact footer mode interop with Java.
    /// </summary>
    public class BinaryCompactFooterInteropTest
    {
        /** */
        public const string PlatformSqlQueryTask = "org.apache.ignite.platform.PlatformSqlQueryTask";

        /** */
        private IIgnite _grid;

        /** */
        private IIgnite _clientGrid;

        /// <summary>
        /// Sets up the test.
        /// </summary>
        [SetUp]
        public void TestSetUp()
        {
            // Start fresh cluster for each test
            _grid = Ignition.Start(Config("config\\compute\\compute-grid1.xml"));
            _clientGrid = Ignition.Start(Config("config\\compute\\compute-grid3.xml"));
        }

        /// <summary>
        /// Tears down the test.
        /// </summary>
        [TearDown]
        public void TestTearDown()
        {
            Ignition.StopAll(true);
        }

        /// <summary>
        /// Tests an object that comes from Java.
        /// </summary>
        [Test]
        public void TestFromJava([Values(true, false)] bool client)
        {
            var grid = client ? _clientGrid : _grid;

            grid.GetOrCreateCache<int, int>("default").Put(ComputeApiTest.EchoTypeBinarizable, 99);

            var fromJava = grid.GetCompute().ExecuteJavaTask<PlatformComputeBinarizable>(
                ComputeApiTest.EchoTask, ComputeApiTest.EchoTypeBinarizable);

            Assert.AreEqual(99, fromJava.Field);
        }

        /// <summary>
        /// Tests an object that comes from .NET in Java.
        /// </summary>
        [Test]
        public void TestFromDotNet([Values(true, false)] bool client)
        {
            var grid = client ? _clientGrid : _grid;

            var compute = grid.GetCompute().WithKeepBinary();

            var arg = new PlatformComputeNetBinarizable {Field = 100};

            var res = compute.ExecuteJavaTask<int>(ComputeApiTest.BinaryArgTask, arg);

            Assert.AreEqual(arg.Field, res);
        }

        /// <summary>
        /// Tests the indexing.
        /// </summary>
        [Test]
        public void TestIndexing([Values(true, false)] bool client)
        {
            var grid = client ? _clientGrid : _grid;

            var cache = grid.GetCache<int, PlatformComputeBinarizable>("default");

            // Populate cache in .NET
            for (var i = 0; i < 100; i++)
                cache[i] = new PlatformComputeBinarizable {Field = i};

            // Run SQL query on Java side
            var qryRes = grid.GetCompute().ExecuteJavaTask<IList>(PlatformSqlQueryTask, "Field < 10");

            Assert.AreEqual(10, qryRes.Count);
            Assert.IsTrue(qryRes.OfType<PlatformComputeBinarizable>().All(x => x.Field < 10));
        }

        /// <summary>
        /// Gets the config.
        /// </summary>
        private static IgniteConfiguration Config(string springUrl)
        {
            return new IgniteConfiguration(TestUtils.GetTestConfiguration())
            {
                SpringConfigUrl = springUrl,
                BinaryConfiguration = new BinaryConfiguration(
                    typeof (PlatformComputeBinarizable),
                    typeof (PlatformComputeNetBinarizable))
                {
                    NameMapper = BinaryBasicNameMapper.SimpleNameInstance
                }
            };
        }
    }
}
