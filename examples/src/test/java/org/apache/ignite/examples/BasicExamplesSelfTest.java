/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.examples;

import org.apache.ignite.examples.computegrid.ComputeBroadcastExample;
import org.apache.ignite.examples.computegrid.ComputeCallableExample;
import org.apache.ignite.examples.computegrid.ComputeClosureExample;
import org.apache.ignite.examples.computegrid.ComputeRunnableExample;
import org.apache.ignite.examples.datastructures.IgniteExecutorServiceExample;
import org.apache.ignite.testframework.junits.common.GridAbstractExamplesTest;
import org.junit.Test;

/**
 * Closure examples self test.
 */
public class BasicExamplesSelfTest extends GridAbstractExamplesTest {
    /**
     * @throws Exception If failed.
     */
    @Test
    public void testBroadcastExample() throws Exception {
        ComputeBroadcastExample.main(EMPTY_ARGS);
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testCallableExample() throws Exception {
        ComputeCallableExample.main(EMPTY_ARGS);
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testClosureExample() throws Exception {
        ComputeClosureExample.main(EMPTY_ARGS);
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testExecutorExample() throws Exception {
        IgniteExecutorServiceExample.main(EMPTY_ARGS);
    }

//    TODO: IGNITE-711 next example(s) should be implemented for java 8 or testing method(s) should be removed if example(s) does not applicable for java 8.
//    /**
//     * @throws Exception If failed.
//     */
//    public void testReducerExample() throws Exception {
//        ComputeReducerExample.main(EMPTY_ARGS);
//    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testRunnableExample() throws Exception {
        ComputeRunnableExample.main(EMPTY_ARGS);
    }

//    TODO: IGNITE-711 next example(s) should be implemented for java 8
//    or testing method(s) should be removed if example(s) does not applicable for java 8.
//    /**
//     * @throws Exception If failed.
//     */
//    public void testTaskMapExample() throws Exception {
//        ComputeTaskMapExample.main(EMPTY_ARGS);
//    }
//
//    /**
//     * @throws Exception If failed.
//     */
//    public void testTaskSplitExample() throws Exception {
//        ComputeTaskSplitExample.main(EMPTY_ARGS);
//    }
}
