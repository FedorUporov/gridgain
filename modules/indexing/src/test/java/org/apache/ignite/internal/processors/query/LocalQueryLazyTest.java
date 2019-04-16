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

package org.apache.ignite.internal.processors.query;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.testframework.GridTestUtils;
import org.h2.result.LazyResult;
import org.h2.result.ResultInterface;
import org.junit.Test;

/**
 * Tests for local query execution in lazy mode.
 */
public class LocalQueryLazyTest extends AbstractIndexingCommonTest {
    /** Keys count. */
    private static final int KEY_CNT = 10;

    /** Queries count. */
    private static final int QRY_CNT = 10;

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        super.beforeTest();

        startGrid();

        IgniteCache<Long, Long> c = grid().createCache(new CacheConfiguration<Long, Long>()
            .setName("test")
            .setSqlSchema("TEST")
            .setQueryEntities(Collections.singleton(new QueryEntity(Long.class, Long.class)
                .setTableName("test")
                .addQueryField("id", Long.class.getName(), null)
                .addQueryField("val", Long.class.getName(), null)
                .setKeyFieldName("id")
                .setValueFieldName("val")
            ))
            .setAffinity(new RendezvousAffinityFunction(false, 10)));

        for (long i = 0; i < KEY_CNT; ++i)
            c.put(i, i);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();

        super.afterTest();
    }

    /**
     * Test local query execution.
     */
    @Test
    public void testLocalLazyQuery() {
        Iterator[] iters = new Iterator[QRY_CNT];

        for (int i = 0; i < QRY_CNT; ++i) {
            iters[i] = sql("SELECT * FROM test").iterator();

            ResultInterface res = GridTestUtils.getFieldValueHierarchy(iters[i], "iter", "iter", "res");

            assertTrue("Unexpected result type " + res.getClass(), res instanceof LazyResult);
        }

        // Scan and close iterator in reverse order.
        for (int i = QRY_CNT - 1; i >= 0; --i) {
            while (iters[i].hasNext())
                iters[i].next();
        }
    }

    /**
     * @param sql SQL query.
     * @param args Query parameters.
     * @return Results cursor.
     */
    private FieldsQueryCursor<List<?>> sql(String sql, Object ... args) {
        return grid().context().query().querySqlFields(new SqlFieldsQuery(sql)
            .setLocal(true)
            .setLazy(true)
            .setSchema("TEST")
            .setArgs(args), false);
    }
}
