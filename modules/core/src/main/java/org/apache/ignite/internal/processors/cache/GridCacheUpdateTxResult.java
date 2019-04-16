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

package org.apache.ignite.internal.processors.cache;

import java.util.List;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.pagemem.wal.WALPointer;
import org.apache.ignite.internal.processors.cache.tree.mvcc.search.MvccLinkAwareSearchRow;
import org.apache.ignite.internal.util.future.GridFutureAdapter;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.jetbrains.annotations.Nullable;

/**
 * Cache entry transactional update result.
 */
public class GridCacheUpdateTxResult {
    /** Success flag. */
    private final boolean success;

    /** Partition update counter. */
    private long updateCntr;

    /** */
    private GridFutureAdapter<GridCacheUpdateTxResult> fut;

    /** */
    private WALPointer logPtr;

    /** Mvcc history. */
    private List<MvccLinkAwareSearchRow> mvccHistory;

    /** Previous value. */
    private CacheObject prevVal;

    /** Invoke result. */
    private CacheInvokeResult invokeRes;

    /** New value. */
    private CacheObject newVal;

    /** Value before the current tx. */
    private CacheObject oldVal;

    /** Filtered flag. */
    private boolean filtered;

    /**
     * Constructor.
     *
     * @param success Success flag.
     */
    GridCacheUpdateTxResult(boolean success) {
        this.success = success;
    }

    /**
     * Constructor.
     *
     * @param success Success flag.
     * @param logPtr Logger WAL pointer for the update.
     */
    GridCacheUpdateTxResult(boolean success, WALPointer logPtr) {
        this.success = success;
        this.logPtr = logPtr;
    }

    /**
     * Constructor.
     *
     * @param success Success flag.
     * @param fut Update future.
     */
    GridCacheUpdateTxResult(boolean success, GridFutureAdapter<GridCacheUpdateTxResult> fut) {
        this.success = success;
        this.fut = fut;
    }

    /**
     * Constructor.
     *
     * @param success Success flag.
     * @param updateCntr Update counter.
     * @param logPtr Logger WAL pointer for the update.
     */
    GridCacheUpdateTxResult(boolean success, long updateCntr, WALPointer logPtr) {
        this.success = success;
        this.updateCntr = updateCntr;
        this.logPtr = logPtr;
    }

    /**
     * @return Partition update counter.
     */
    public long updateCounter() {
        return updateCntr;
    }

    /**
     * @return Success flag.
     */
    public boolean success() {
        return success;
    }

    /**
     * @return Logged WAL pointer for the update if persistence is enabled.
     */
    public WALPointer loggedPointer() {
        return logPtr;
    }

    /**
     * @return Update future.
     */
    @Nullable public IgniteInternalFuture<GridCacheUpdateTxResult> updateFuture() {
        return fut;
    }

    /**
     * @return Mvcc history rows.
     */
    @Nullable public List<MvccLinkAwareSearchRow> mvccHistory() {
        return mvccHistory;
    }

    /**
     * @param mvccHistory Mvcc history rows.
     */
    public void mvccHistory(List<MvccLinkAwareSearchRow> mvccHistory) {
        this.mvccHistory = mvccHistory;
    }

    /**
     * @return Previous value.
     */
    @Nullable public CacheObject prevValue() {
        return prevVal;
    }

    /**
     * @param prevVal Previous value.
     */
    public void prevValue(@Nullable CacheObject prevVal) {
        this.prevVal = prevVal;
    }

    /**
     * @param result Entry processor invoke result.
     */
    public void invokeResult(CacheInvokeResult result) {
        invokeRes = result;
    }

    /**
     * @return Invoke result.
     */
    public CacheInvokeResult invokeResult() {
        return invokeRes;
    }

    /**
     * @return New value.
     */
    public CacheObject newValue() {
        return newVal;
    }

    /**
     * @return Old value.
     */
    public CacheObject oldValue() {
        return oldVal;
    }

    /**
     * @param newVal New value.
     */
    public void newValue(CacheObject newVal) {
        this.newVal = newVal;
    }

    /**
     * @param oldVal Old value.
     */
    public void oldValue(CacheObject oldVal) {
        this.oldVal = oldVal;
    }

    /**
     * @return Filtered flag.
     */
    public boolean filtered() {
        return filtered;
    }

    /**
     * @param filtered Filtered flag.
     */
    public void filtered(boolean filtered) {
        this.filtered = filtered;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(GridCacheUpdateTxResult.class, this);
    }
}
