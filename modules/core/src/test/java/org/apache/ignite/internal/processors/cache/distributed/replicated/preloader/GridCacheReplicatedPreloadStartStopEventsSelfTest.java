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

package org.apache.ignite.internal.processors.cache.distributed.replicated.preloader;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.Event;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.apache.ignite.cache.CacheMode.REPLICATED;
import static org.apache.ignite.events.EventType.EVT_CACHE_REBALANCE_STARTED;
import static org.apache.ignite.events.EventType.EVT_CACHE_REBALANCE_STOPPED;

/**
 * Tests that preload start/preload stop events are fired only once for replicated cache.
 */
@RunWith(JUnit4.class)
public class GridCacheReplicatedPreloadStartStopEventsSelfTest extends GridCommonAbstractTest {
    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        super.afterTest();

        stopAllGrids();
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        CacheConfiguration ccfg = new CacheConfiguration(DEFAULT_CACHE_NAME);

        ccfg.setCacheMode(REPLICATED);

        cfg.setCacheConfiguration(ccfg);

        return cfg;
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testStartStopEvents() throws Exception {
        Ignite ignite = startGrid(0);

        final AtomicInteger preloadStartCnt = new AtomicInteger();
        final AtomicInteger preloadStopCnt = new AtomicInteger();

        ignite.events().localListen(new IgnitePredicate<Event>() {
            @Override public boolean apply(Event e) {
                if (e.type() == EVT_CACHE_REBALANCE_STARTED)
                    preloadStartCnt.incrementAndGet();
                else if (e.type() == EVT_CACHE_REBALANCE_STOPPED)
                    preloadStopCnt.incrementAndGet();
                else
                    fail("Unexpected event type: " + e.type());

                return true;
            }
        }, EVT_CACHE_REBALANCE_STARTED, EVT_CACHE_REBALANCE_STOPPED);

        startGrid(1);

        startGrid(2);

        startGrid(3);

        assertTrue("Unexpected start count: " + preloadStartCnt.get(), preloadStartCnt.get() <= 1);
        assertTrue("Unexpected stop count: " + preloadStopCnt.get(), preloadStopCnt.get() <= 1);
    }
}
