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

package org.apache.ignite.internal.encryption;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.spi.encryption.keystore.KeystoreEncryptionSpi;
import org.junit.Test;

import static org.apache.ignite.testframework.GridTestUtils.assertThrowsWithCause;

/**
 */
public class EncryptedCacheNodeJoinTest extends AbstractEncryptionTest {
    /** */
    private static final String GRID_2 = "grid-2";

    /** */
    private static final String GRID_3 = "grid-3";

    /** */
    private static final String GRID_4 = "grid-4";

    /** */
    private static final String GRID_5 = "grid-5";

    /** */
    public static final String CLIENT = "client";

    /** */
    private boolean configureCache;

    /** */
    private static final String KEYSTORE_PATH_2 =
        IgniteUtils.resolveIgnitePath("modules/core/src/test/resources/other_tde_keystore.jks").getAbsolutePath();

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        cleanPersistenceDir();
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();

        cleanPersistenceDir();

        configureCache = false;
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String grid) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(grid);

        cfg.setConsistentId(grid);

        if (grid.equals(GRID_0) ||
            grid.equals(GRID_2) ||
            grid.equals(GRID_3) ||
            grid.equals(GRID_4) ||
            grid.equals(GRID_5)) {
            KeystoreEncryptionSpi encSpi = new KeystoreEncryptionSpi();

            encSpi.setKeyStorePath(grid.equals(GRID_2) ? KEYSTORE_PATH_2 : KEYSTORE_PATH);
            encSpi.setKeyStorePassword(KEYSTORE_PASSWORD.toCharArray());

            cfg.setEncryptionSpi(encSpi);
        }
        else
            cfg.setEncryptionSpi(null);

        cfg.setClientMode(grid.equals(CLIENT));

        if (configureCache)
            cfg.setCacheConfiguration(cacheConfiguration(grid));

        return cfg;
    }

    /** */
    protected CacheConfiguration cacheConfiguration(String gridName) {
        CacheConfiguration ccfg = defaultCacheConfiguration();

        ccfg.setName(cacheName());
        ccfg.setEncryptionEnabled(gridName.equals(GRID_0));

        return ccfg;
    }

    /** */
    @Test
    public void testNodeCantJoinWithoutEncryptionSpi() throws Exception {
        startGrid(GRID_0);

        assertThrowsWithCause(() -> {
            try {
                startGrid(GRID_1);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, IgniteCheckedException.class);
    }

    /** */
    @Test
    public void testNodeCantJoinWithDifferentKeyStore() throws Exception {
        startGrid(GRID_0);

        assertThrowsWithCause(() -> {
            try {
                startGrid(GRID_2);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, IgniteCheckedException.class);
    }

    /** */
    @Test
    public void testNodeCanJoin() throws Exception {
        startGrid(GRID_0);

        startGrid(GRID_3).cluster().active(true);
    }

    /** */
    @Test
    public void testNodeCantJoinWithDifferentCacheKeys() throws Exception {
        IgniteEx grid0 = startGrid(GRID_0);
        startGrid(GRID_3);

        grid0.cluster().active(true);

        stopGrid(GRID_3, false);

        createEncryptedCache(grid0, null, cacheName(), null, false);

        stopGrid(GRID_0, false);
        IgniteEx grid3 = startGrid(GRID_3);

        grid3.cluster().active(true);

        createEncryptedCache(grid3, null, cacheName(), null, false);

        assertThrowsWithCause(() -> {
            try {
                startGrid(GRID_0);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, IgniteCheckedException.class);
    }

    /** */
    @Test
    public void testThirdNodeCanJoin() throws Exception {
        IgniteEx grid0 = startGrid(GRID_0);

        IgniteEx grid3 = startGrid(GRID_3);

        grid3.cluster().active(true);

        createEncryptedCache(grid0, grid3, cacheName(), null);

        checkEncryptedCaches(grid0, grid3);

        IgniteEx grid4 = startGrid(GRID_4);

        awaitPartitionMapExchange();

        checkEncryptedCaches(grid0, grid4);
    }

    /** */
    @Test
    public void testClientNodeJoin() throws Exception {
        IgniteEx grid0 = startGrid(GRID_0);

        IgniteEx grid3 = startGrid(GRID_3);

        grid3.cluster().active(true);

        IgniteEx client = startGrid(CLIENT);

        createEncryptedCache(client, grid0, cacheName(), null);
    }

    /** */
    @Test
    public void testNodeCantJoinWithSameNameButNotEncCache() throws Exception {
        configureCache = true;

        IgniteEx grid0 = startGrid(GRID_0);

        grid0.cluster().active(true);

        assertThrowsWithCause(() -> {
            try {
                startGrid(GRID_5);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, IgniteCheckedException.class);
    }

    /** */
    @Test
    public void testNodeCantJoinWithSameNameButEncCache() throws Exception {
        configureCache = true;

        IgniteEx grid0 = startGrid(GRID_5);

        grid0.cluster().active(true);

        assertThrowsWithCause(() -> {
            try {
                startGrid(GRID_0);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, IgniteCheckedException.class);
    }
}
