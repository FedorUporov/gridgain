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

package org.apache.ignite.internal.processors.hadoop.impl;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Test;

/**
 * Configuration validation tests.
 */
public class HadoopValidationSelfTest extends HadoopAbstractSelfTest {
    /** Peer class loading enabled flag. */
    public boolean peerClassLoading;

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids(true);

        peerClassLoading = false;
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        cfg.setPeerClassLoadingEnabled(peerClassLoading);

        return cfg;
    }

    /**
     * Ensure that Grid starts when all configuration parameters are valid.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testValid() throws Exception {
        startGrids(1);
    }
}
