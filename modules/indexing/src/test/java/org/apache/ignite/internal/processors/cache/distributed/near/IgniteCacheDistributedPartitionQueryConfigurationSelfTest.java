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

package org.apache.ignite.internal.processors.cache.distributed.near;

import java.util.Arrays;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;

/**
 * Tests cache query configuration.
 */
public class IgniteCacheDistributedPartitionQueryConfigurationSelfTest extends GridCommonAbstractTest {
    /** Tests partition validation. */
    @Test
    public void testPartitions() {
        final SqlFieldsQuery qry = new SqlFieldsQuery("select 1");

        // Empty set is not allowed.
        failIfNotThrown(new Runnable() {
            @Override public void run() {
                qry.setPartitions();
            }
        });

        // Duplicates are not allowed.
        failIfNotThrown(new Runnable() {
            @Override public void run() {
                qry.setPartitions(0, 1, 2, 1);
            }
        });

        // Values out of range are not allowed.
        failIfNotThrown(new Runnable() {
            @Override public void run() {
                qry.setPartitions(-1, 0, 1);
            }
        });

        // Duplicates with unordered input are not allowed.
        failIfNotThrown(new Runnable() {
            @Override public void run() {
                qry.setPartitions(3, 2, 2);
            }
        });

        // Values out of range are not allowed.
        failIfNotThrown(new Runnable() {
            @Override public void run() {
                qry.setPartitions(-1, 0, 1);
            }
        });

        // Expecting ordered set.
        int[] tmp = new int[] {6, 2 ,3};
        qry.setPartitions(tmp);

        assertTrue(Arrays.equals(new int[]{2, 3, 6}, tmp));

        // If already ordered expecting same instance.
        qry.setPartitions((tmp = new int[] {0, 1, 2}));

        assertTrue(tmp == qry.getPartitions());
    }

    /**
     * @param r Runnable.
     */
    private void failIfNotThrown(Runnable r) {
        try {
            r.run();

            fail();
        }
        catch (Exception ignored) {
            // No-op.
        }
    }
}
