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

package org.apache.ignite.testsuites;

import org.apache.ignite.internal.processors.cache.GridCacheClearSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCacheAtomicFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCacheAtomicNearEnabledFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCacheAtomicReloadAllSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCacheColocatedReloadAllSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCachePartitionedNearDisabledAtomicOnheapFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCachePartitionedNearDisabledAtomicOnheapMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCachePartitionedNearDisabledFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCachePartitionedNearDisabledMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCachePartitionedNearDisabledMultiNodeP2PDisabledFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCachePartitionedNearDisabledMultiNodeWithGroupFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCachePartitionedNearDisabledOnheapFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridCachePartitionedNearDisabledOnheapMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.CachePartitionedMultiNodeLongTxTimeout2FullApiTest;
import org.apache.ignite.internal.processors.cache.distributed.near.CachePartitionedMultiNodeLongTxTimeoutFullApiTest;
import org.apache.ignite.internal.processors.cache.distributed.near.CachePartitionedNearEnabledMultiNodeLongTxTimeoutFullApiTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicClientOnlyMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicClientOnlyMultiNodeP2PDisabledFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicCopyOnReadDisabledMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicMultiNodeP2PDisabledFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicMultiNodeWithGroupFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicNearEnabledMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicNearEnabledMultiNodeWithGroupFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicNearOnlyMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicNearOnlyMultiNodeP2PDisabledFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicOnheapFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicOnheapMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheNearOnlyMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheNearOnlyMultiNodeP2PDisabledFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheNearReloadAllSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheNearTxMultiNodeSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedAtomicOnheapFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedAtomicOnheapMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedClientOnlyNoPrimaryFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedCopyOnReadDisabledMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedFilteredPutSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedFullApiMultithreadedSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedMultiNodeCounterSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedMultiNodeP2PDisabledFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedMultiNodeWithGroupFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedNearOnlyNoPrimaryFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedOnheapFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCachePartitionedOnheapMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.CacheReplicatedRendezvousAffinityExcludeNeighborsMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.CacheReplicatedRendezvousAffinityMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.GridCacheReplicatedAtomicFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.GridCacheReplicatedAtomicMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.GridCacheReplicatedFullApiMultithreadedSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.GridCacheReplicatedFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.GridCacheReplicatedMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.GridCacheReplicatedMultiNodeP2PDisabledFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.distributed.replicated.GridCacheReplicatedNearOnlyMultiNodeFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.local.GridCacheLocalAtomicFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.local.GridCacheLocalAtomicWithGroupFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.local.GridCacheLocalFullApiMultithreadedSelfTest;
import org.apache.ignite.internal.processors.cache.local.GridCacheLocalFullApiSelfTest;
import org.apache.ignite.internal.processors.cache.local.GridCacheLocalWithGroupFullApiSelfTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for cache API.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    GridCacheLocalFullApiSelfTest.class,
    GridCacheLocalAtomicFullApiSelfTest.class,
    GridCacheReplicatedFullApiSelfTest.class,
    GridCachePartitionedFullApiSelfTest.class,
    GridCacheAtomicFullApiSelfTest.class,
    GridCachePartitionedNearDisabledFullApiSelfTest.class,
    GridCachePartitionedFilteredPutSelfTest.class,
    GridCacheReplicatedAtomicFullApiSelfTest.class,
    GridCacheAtomicNearEnabledFullApiSelfTest.class,
    GridCacheAtomicOnheapFullApiSelfTest.class,

    GridCachePartitionedOnheapFullApiSelfTest.class,
    GridCachePartitionedAtomicOnheapFullApiSelfTest.class,
    GridCachePartitionedNearDisabledOnheapFullApiSelfTest.class,
    GridCachePartitionedNearDisabledAtomicOnheapFullApiSelfTest.class,

    // No primary.
    GridCachePartitionedClientOnlyNoPrimaryFullApiSelfTest.class,
    GridCachePartitionedNearOnlyNoPrimaryFullApiSelfTest.class,

    // Multi-node.
    GridCacheReplicatedMultiNodeFullApiSelfTest.class,
    GridCacheReplicatedMultiNodeP2PDisabledFullApiSelfTest.class,
    GridCacheReplicatedAtomicMultiNodeFullApiSelfTest.class,

    GridCachePartitionedMultiNodeFullApiSelfTest.class,
    GridCachePartitionedCopyOnReadDisabledMultiNodeFullApiSelfTest.class,
    GridCacheAtomicMultiNodeFullApiSelfTest.class,
    GridCacheAtomicCopyOnReadDisabledMultiNodeFullApiSelfTest.class,
    GridCachePartitionedMultiNodeP2PDisabledFullApiSelfTest.class,
    GridCacheAtomicMultiNodeP2PDisabledFullApiSelfTest.class,
    GridCacheAtomicNearEnabledMultiNodeFullApiSelfTest.class,
    CachePartitionedMultiNodeLongTxTimeoutFullApiTest.class,
    CachePartitionedMultiNodeLongTxTimeout2FullApiTest.class,
    CachePartitionedNearEnabledMultiNodeLongTxTimeoutFullApiTest.class,

    GridCachePartitionedNearDisabledMultiNodeFullApiSelfTest.class,
    GridCachePartitionedNearDisabledMultiNodeP2PDisabledFullApiSelfTest.class,

    GridCacheNearOnlyMultiNodeFullApiSelfTest.class,
    GridCacheNearOnlyMultiNodeP2PDisabledFullApiSelfTest.class,
    GridCacheReplicatedNearOnlyMultiNodeFullApiSelfTest.class,

    GridCacheAtomicClientOnlyMultiNodeFullApiSelfTest.class,
    GridCacheAtomicClientOnlyMultiNodeP2PDisabledFullApiSelfTest.class,

    GridCacheAtomicNearOnlyMultiNodeFullApiSelfTest.class,
    GridCacheAtomicNearOnlyMultiNodeP2PDisabledFullApiSelfTest.class,

    CacheReplicatedRendezvousAffinityExcludeNeighborsMultiNodeFullApiSelfTest.class,
    CacheReplicatedRendezvousAffinityMultiNodeFullApiSelfTest.class,

    GridCacheNearReloadAllSelfTest.class,
    GridCacheColocatedReloadAllSelfTest.class,
    GridCacheAtomicReloadAllSelfTest.class,
    GridCacheNearTxMultiNodeSelfTest.class,
    GridCachePartitionedMultiNodeCounterSelfTest.class,

    GridCachePartitionedOnheapMultiNodeFullApiSelfTest.class,
    GridCachePartitionedAtomicOnheapMultiNodeFullApiSelfTest.class,
    GridCachePartitionedNearDisabledOnheapMultiNodeFullApiSelfTest.class,
    GridCachePartitionedNearDisabledAtomicOnheapMultiNodeFullApiSelfTest.class,
    GridCacheAtomicOnheapMultiNodeFullApiSelfTest.class,

    // Multithreaded.
    GridCacheLocalFullApiMultithreadedSelfTest.class,
    GridCacheReplicatedFullApiMultithreadedSelfTest.class,
    GridCachePartitionedFullApiMultithreadedSelfTest.class,

    // Other.
    GridCacheClearSelfTest.class,

    GridCacheLocalWithGroupFullApiSelfTest.class,
    GridCacheLocalAtomicWithGroupFullApiSelfTest.class,
    GridCacheAtomicMultiNodeWithGroupFullApiSelfTest.class,
    GridCacheAtomicNearEnabledMultiNodeWithGroupFullApiSelfTest.class,
    GridCachePartitionedMultiNodeWithGroupFullApiSelfTest.class,
    GridCachePartitionedNearDisabledMultiNodeWithGroupFullApiSelfTest.class,

    //suite.addTest(new JUnit4TestAdapter(GridActivateExtensionTest.class));
})
public class IgniteCacheFullApiSelfTestSuite {
}
