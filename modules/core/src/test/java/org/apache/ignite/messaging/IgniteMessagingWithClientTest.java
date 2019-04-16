/*
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

package org.apache.ignite.messaging;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.binary.BinaryMarshaller;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;

/**
 *
 */
public class IgniteMessagingWithClientTest extends GridCommonAbstractTest implements Serializable {
    /** Message topic. */
    private enum TOPIC {
        /** */
        ORDERED
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        cfg.setMarshaller(new BinaryMarshaller());

        if (igniteInstanceName.equals(getTestIgniteInstanceName(2))) {
            cfg.setClientMode(true);

            ((TcpDiscoverySpi)cfg.getDiscoverySpi()).setForceServerMode(true);
        }

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        super.afterTest();

        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testMessageSendWithClientJoin() throws Exception {
        startGrid(0);

        Ignite ignite1 = startGrid(1);

        ClusterGroup rmts = ignite1.cluster().forRemotes();

        IgniteMessaging msg = ignite1.message(rmts);

        msg.localListen(TOPIC.ORDERED, new LocalListener());

        msg.remoteListen(TOPIC.ORDERED, new RemoteListener());

        final AtomicBoolean stop = new AtomicBoolean();

        IgniteInternalFuture<?> fut = GridTestUtils.runMultiThreadedAsync(new Callable<Object>() {
            @Override public Object call() throws Exception {
                int iter = 0;

                while (!stop.get()) {
                    if (iter % 10 == 0)
                        log.info("Client start/stop iteration: " + iter);

                    iter++;

                    try (Ignite ignite = startGrid(2)) {
                        assertTrue(ignite.configuration().isClientMode());
                    }
                }

                return null;
            }
        }, 1, "client-start-stop");

        try {
            long stopTime = U.currentTimeMillis() + 30_000;

            int iter = 0;

            while (System.currentTimeMillis() < stopTime) {
                try {
                    ignite1.message(rmts).sendOrdered(TOPIC.ORDERED, Integer.toString(iter), 0);
                }
                catch (IgniteException e) {
                    log.info("Message send failed: " + e);
                }

                iter++;

                if (iter % 100 == 0)
                    Thread.sleep(5);
            }
        }
        finally {
            stop.set(true);
        }

        fut.get();
    }

    /**
     *
     */
    private static class LocalListener implements IgniteBiPredicate<UUID, String> {
        /** {@inheritDoc} */
        @Override public boolean apply(UUID uuid, String s) {
            return true;
        }
    }

    /**
     *
     */
    private static class RemoteListener implements IgniteBiPredicate<UUID, String> {
        /** */
        @IgniteInstanceResource
        private Ignite ignite;

        /** {@inheritDoc} */
        @Override public boolean apply(UUID nodeId, String msg) {
            ignite.message(ignite.cluster().forNodeId(nodeId)).send(TOPIC.ORDERED, msg);

            return true;
        }
    }
}
