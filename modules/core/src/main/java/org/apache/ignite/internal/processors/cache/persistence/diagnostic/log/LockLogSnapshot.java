package org.apache.ignite.internal.processors.cache.persistence.diagnostic.log;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.ignite.internal.processors.cache.persistence.diagnostic.Dump;
import org.apache.ignite.internal.util.typedef.internal.SB;

import static org.apache.ignite.internal.pagemem.PageIdUtils.flag;
import static org.apache.ignite.internal.pagemem.PageIdUtils.pageId;
import static org.apache.ignite.internal.pagemem.PageIdUtils.pageIndex;
import static org.apache.ignite.internal.processors.cache.persistence.diagnostic.PageLockTracker.BEFORE_READ_LOCK;
import static org.apache.ignite.internal.processors.cache.persistence.diagnostic.PageLockTracker.BEFORE_WRITE_LOCK;
import static org.apache.ignite.internal.processors.cache.persistence.diagnostic.PageLockTracker.READ_LOCK;
import static org.apache.ignite.internal.processors.cache.persistence.diagnostic.PageLockTracker.READ_UNLOCK;
import static org.apache.ignite.internal.processors.cache.persistence.diagnostic.PageLockTracker.WRITE_LOCK;
import static org.apache.ignite.internal.processors.cache.persistence.diagnostic.PageLockTracker.WRITE_UNLOCK;
import static org.apache.ignite.internal.processors.cache.persistence.diagnostic.log.LockLog.LOCK_IDX_MASK;
import static org.apache.ignite.internal.processors.cache.persistence.diagnostic.log.LockLog.LOCK_OP_MASK;
import static org.apache.ignite.internal.processors.cache.persistence.diagnostic.log.LockLog.OP_OFFSET;
import static org.apache.ignite.internal.util.IgniteUtils.hexInt;
import static org.apache.ignite.internal.util.IgniteUtils.hexLong;

public class LockLogSnapshot implements Dump {
    public final String name;

    public final int headIdx;

    public final long[] pageIdsLockLog;

    public final int nextOp;
    public final int nextOpStructureId;
    public final long nextOpPageId;

    public LockLogSnapshot(
        String name,
        int headIdx,
        long[] pageIdsLockLog,
        int nextOp,
        int nextOpStructureId,
        long nextOpPageId
    ) {
        this.name = name;
        this.headIdx = headIdx;
        this.pageIdsLockLog = pageIdsLockLog;
        this.nextOp = nextOp;
        this.nextOpStructureId = nextOpStructureId;
        this.nextOpPageId = nextOpPageId;
    }

    @Override public String toString() {
        SB res = new SB();

        res.a(name).a("\n");

        Map<Long, LockState> holdetLocks = new LinkedHashMap<>();

        SB logLocksStr = new SB();

        for (int i = 0; i < headIdx; i += 2) {
            long metaOnLock = pageIdsLockLog[i + 1];

            assert metaOnLock != 0;

            int idx = ((int)(metaOnLock >> 32) & LOCK_IDX_MASK) >> OP_OFFSET;

            assert idx >= 0;

            long pageId = pageIdsLockLog[i];

            int op = (int)((metaOnLock >> 32) & LOCK_OP_MASK);
            int cacheId = (int)(metaOnLock);

            String opStr = "N/A";

            switch (op) {
                case READ_LOCK:
                    opStr = "Read lock    ";
                    break;
                case READ_UNLOCK:
                    opStr = "Read unlock  ";
                    break;
                case WRITE_LOCK:
                    opStr = "Write lock    ";
                    break;
                case WRITE_UNLOCK:
                    opStr = "Write unlock  ";
                    break;
            }

            if (op == READ_LOCK || op == WRITE_LOCK || op == BEFORE_READ_LOCK) {
                LockState state = holdetLocks.get(pageId);

                if (state == null)
                    holdetLocks.put(pageId, state = new LockState());

                if (op == READ_LOCK)
                    state.readlock++;

                if (op == WRITE_LOCK)
                    state.writelock++;

                logLocksStr.a("L=" + idx + " -> " + opStr + " nextOpPageId=" + pageId + ", nextOpCacheId=" + cacheId
                    + " [pageIdxHex=" + hexLong(pageId)
                    + ", partId=" + pageId(pageId) + ", pageIdx=" + pageIndex(pageId)
                    + ", flags=" + hexInt(flag(pageId)) + "]\n");
            }

            if (op == READ_UNLOCK || op == WRITE_UNLOCK) {
                LockState state = holdetLocks.get(pageId);

                if (op == READ_UNLOCK)
                    state.readlock--;

                if (op == WRITE_UNLOCK)
                    state.writelock--;

                if (state.readlock == 0 && state.writelock == 0)
                    holdetLocks.remove(pageId);

                logLocksStr.a("L=" + idx + " <- " + opStr + " nextOpPageId=" + pageId + ", nextOpCacheId=" + cacheId
                    + " [pageIdxHex=" + hexLong(pageId)
                    + ", partId=" + pageId(pageId) + ", pageIdx=" + pageIndex(pageId)
                    + ", flags=" + hexInt(flag(pageId)) + "]\n");
            }
        }

        if (nextOpPageId != 0) {
            String opStr = "N/A";

            switch (nextOp) {
                case BEFORE_READ_LOCK:
                    opStr = "Try read lock    ";
                    break;
                case BEFORE_WRITE_LOCK:
                    opStr = "Try write lock  ";
                    break;
            }

            logLocksStr.a("-> " + opStr + " nextOpPageId=" + nextOpPageId +
                ", nextOpStructureId=" + nextOpStructureId
                + " [pageIdxHex=" + hexLong(nextOpPageId)
                + ", partId=" + pageId(nextOpPageId) + ", pageIdx=" + pageIndex(nextOpPageId)
                + ", flags=" + hexInt(flag(nextOpPageId)) + "]\n");
        }

        SB holdetLocksStr = new SB();

        holdetLocksStr.a("locked pages = [");

        boolean first = true;

        for (Map.Entry<Long, LockState> entry : holdetLocks.entrySet()) {
            Long pageId = entry.getKey();
            LockState lockState = entry.getValue();

            if (!first)
                holdetLocksStr.a(",");
            else
                first = false;

            holdetLocksStr.a(pageId).a("(r=" + lockState.readlock + "|w=" + lockState.writelock + ")");
        }

        holdetLocksStr.a("]\n");

        res.a(holdetLocksStr);
        res.a(logLocksStr);

        return res.toString();
    }

    private static class LockState {
        int readlock;
        int writelock;
    }
}