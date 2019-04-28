package org.apache.ignite.internal.processors.cache.persistence.diagnostic.stack;

import java.nio.LongBuffer;
import org.apache.ignite.internal.util.GridUnsafe;

import static java.util.Arrays.copyOf;
import static org.apache.ignite.internal.pagemem.PageIdUtils.pageId;

public class OffHeapLockStack extends LockStack {

    private final int stackSize;

    private final long ptr;

    public OffHeapLockStack(String name, int size) {
        super(name, size);

        this.stackSize = capacity * 8;

        this.ptr = allocate(stackSize);
    }

    @Override public int capacity() {
        return capacity;
    }

    @Override protected long getByIndex(int idx) {
        return GridUnsafe.getLong(ptr + offset(idx));
    }

    @Override protected void setByIndex(int idx, long val) {
        GridUnsafe.putLong(ptr + offset(idx), val);
    }

    private long offset(long headIdx) {
        return headIdx * 8;
    }

    private long allocate(int size) {
        long ptr = GridUnsafe.allocateMemory(size);

        GridUnsafe.setMemory(ptr, stackSize, (byte)0);

        return ptr;
    }

    @Override public LockStackSnapshot snapshot() {
        LongBuffer buf = LongBuffer.allocate(stackSize);

        GridUnsafe.copyMemory(null, ptr, buf.array(), GridUnsafe.LONG_ARR_OFF, stackSize);

        long[] stack = buf.array();

        assert stack.length == stackSize;

        return new LockStackSnapshot(
            name,
            System.currentTimeMillis(),
            headIdx,
            stack,
            nextOp,
            nextOpStructureId,
            nextOpPageId
        );
    }
}
