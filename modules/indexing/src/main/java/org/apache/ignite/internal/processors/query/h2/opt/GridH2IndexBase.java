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

package org.apache.ignite.internal.processors.query.h2.opt;

import org.apache.ignite.internal.processors.cache.CacheObject;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.query.QueryUtils;
import org.apache.ignite.internal.processors.query.h2.opt.join.CollocationModelMultiplier;
import org.apache.ignite.internal.processors.query.h2.opt.join.CollocationModel;
import org.h2.engine.Session;
import org.h2.index.BaseIndex;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.table.TableFilter;
import org.h2.value.Value;

/**
 * Index base.
 */
public abstract class GridH2IndexBase extends BaseIndex {
    /** Underlying table. */
    private final GridH2Table tbl;

    /**
     * Constructor.
     *
     * @param tbl Table.
     */
    protected GridH2IndexBase(GridH2Table tbl) {
        this.tbl = tbl;
    }

    /** {@inheritDoc} */
    @Override public final void close(Session ses) {
        // No-op. Actual index destruction must happen in method destroy.
    }

    /**
     * Attempts to destroys index and release all the resources.
     * We use this method instead of {@link #close(Session)} because that method
     * is used by H2 internally.
     *
     * @param rmv Flag remove.
     */
    public void destroy(boolean rmv) {
        // No-op.
    }

    /**
     * @return Index segment ID for current query context.
     */
    protected int threadLocalSegment() {
        if(segmentsCount() == 1)
            return 0;

        QueryContext qctx = queryContextRegistry().getThreadLocal();

        if(qctx == null)
            throw new IllegalStateException("GridH2QueryContext is not initialized.");

        return qctx.segment();
    }

    /**
     * Puts row.
     *
     * @param row Row.
     * @return Existing row or {@code null}.
     */
    public abstract H2CacheRow put(H2CacheRow row);

    /**
     * Puts row.
     *
     * @param row Row.
     * @return {@code True} if existing row row has been replaced.
     */
    public abstract boolean putx(H2CacheRow row);

    /**
     * Removes row from index.
     *
     * @param row Row.
     * @return {@code True} if row has been removed.
     */
    public abstract boolean removex(SearchRow row);

    /**
     * @param ses Session.
     * @param filters All joined table filters.
     * @param filter Current filter.
     * @return Multiplier.
     */
    public final int getDistributedMultiplier(Session ses, TableFilter[] filters, int filter) {
        CollocationModelMultiplier mul = CollocationModel.distributedMultiplier(ses, filters, filter);

        return mul.multiplier();
    }

    /** {@inheritDoc} */
    @Override public GridH2Table getTable() {
        return (GridH2Table)super.getTable();
    }

    /** {@inheritDoc} */
    @Override public long getDiskSpaceUsed() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override public void checkRename() {
        throw DbException.getUnsupportedException("rename");
    }

    /** {@inheritDoc} */
    @Override public void add(Session ses, Row row) {
        throw DbException.getUnsupportedException("add");
    }

    /** {@inheritDoc} */
    @Override public void remove(Session ses, Row row) {
        throw DbException.getUnsupportedException("remove row");
    }

    /** {@inheritDoc} */
    @Override public void remove(Session ses) {
        // No-op: destroyed from owning table.
    }

    /** {@inheritDoc} */
    @Override public void truncate(Session ses) {
        throw DbException.getUnsupportedException("truncate");
    }

    /** {@inheritDoc} */
    @Override public boolean needRebuild() {
        return false;
    }

    /** {@inheritDoc} */
    @Override public void removeChildrenAndResources(Session session) {
        // The sole purpose of this override is to pass session to table.removeIndex
        assert table instanceof GridH2Table;

        ((GridH2Table)table).removeIndex(session, this);

        remove(session);

        database.removeMeta(session, getId());
    }

    /**
     * @return Index segments count.
     */
    public abstract int segmentsCount();

    /**
     * @param partition Partition idx.
     * @return Segment ID for given key
     */
    public int segmentForPartition(int partition){
        return segmentsCount() == 1 ? 0 : (partition % segmentsCount());
    }

    /**
     * @param row Table row.
     * @return Segment ID for given row.
     */
    @SuppressWarnings("IfMayBeConditional")
    protected int segmentForRow(GridCacheContext ctx, SearchRow row) {
        assert row != null;

        if (segmentsCount() == 1 || ctx == null)
            return 0;

        CacheObject key;

        final Value keyColValue = row.getValue(QueryUtils.KEY_COL);

        assert keyColValue != null;

        final Object o = keyColValue.getObject();

        if (o instanceof CacheObject)
            key = (CacheObject)o;
        else
            key = ctx.toCacheKeyObject(o);

        return segmentForPartition(ctx.affinity().partition(key));
    }

    /**
     * Re-assign column ids after removal of column(s).
     */
    public void refreshColumnIds() {
        assert columnIds.length == columns.length;

        for (int pos = 0; pos < columnIds.length; ++pos)
            columnIds[pos] = columns[pos].getColumnId();
    }

    /**
     * @return Row descriptor.
     */
    protected GridH2RowDescriptor rowDescriptor() {
        return tbl.rowDescriptor();
    }

    /**
     * @return Query context registry.
     */
    protected QueryContextRegistry queryContextRegistry() {
        return tbl.rowDescriptor().indexing().queryContextRegistry();
    }
}
