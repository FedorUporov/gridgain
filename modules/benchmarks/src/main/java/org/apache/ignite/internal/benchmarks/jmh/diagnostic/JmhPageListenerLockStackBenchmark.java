package org.apache.ignite.internal.benchmarks.jmh.diagnostic;

import org.apache.ignite.internal.benchmarks.jmh.JmhAbstractBenchmark;
import org.apache.ignite.internal.benchmarks.jmh.runner.JmhIdeBenchmarkRunner;
import org.apache.ignite.internal.processors.cache.persistence.diagnostic.HeapArrayLockLog;
import org.apache.ignite.internal.processors.cache.persistence.diagnostic.HeapArrayLockStack;
import org.apache.ignite.internal.processors.cache.persistence.diagnostic.OffHeapLockStack;
import org.apache.ignite.internal.processors.cache.persistence.tree.util.PageLockListener;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

public class JmhPageListenerLockStackBenchmark extends JmhAbstractBenchmark {
    // without sync
    //Benchmark                                     (stackSize)              (type)   Mode  Cnt          Score         Error  Units
    //JmhPageListenerLockStackBenchmark.lockUnlock            2  HeapArrayLockStack  thrpt   10  112220367.940 ± 3489526.518  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            4  HeapArrayLockStack  thrpt   10   70455560.182 ± 3312950.796  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            8  HeapArrayLockStack  thrpt   10   44866501.588 ±  624434.909  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock           16  HeapArrayLockStack  thrpt   10   24397179.687 ±  469696.820  ops/s

    // 2 volatile
    //Benchmark                                     (stackSize)              (type)   Mode  Cnt         Score        Error  Units
    //JmhPageListenerLockStackBenchmark.lockUnlock            2  HeapArrayLockStack  thrpt   10  14279323.516 ± 141363.335  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            4  HeapArrayLockStack  thrpt   10   7076342.539 ±  90446.056  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            8  HeapArrayLockStack  thrpt   10   3546973.366 ±  59300.908  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock           16  HeapArrayLockStack  thrpt   10   1759634.526 ±  19412.998  ops/s

    // AtomicBoolean
    //Benchmark                                     (stackSize)              (type)   Mode  Cnt         Score         Error  Units
    //JmhPageListenerLockStackBenchmark.lockUnlock            2  HeapArrayLockStack  thrpt   10  13143263.758 ± 1241703.543  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            4  HeapArrayLockStack  thrpt   10   6666108.001 ±   94336.397  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            8  HeapArrayLockStack  thrpt   10   3235242.435 ±  147932.557  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock           16  HeapArrayLockStack  thrpt   10   1680152.792 ±   12182.147  ops/s

    // IntegeFieldUpdate
    //Benchmark                                     (stackSize)              (type)   Mode  Cnt         Score        Error  Units
    //JmhPageListenerLockStackBenchmark.lockUnlock            2  HeapArrayLockStack  thrpt   10  12159220.886 ± 636390.067  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            4  HeapArrayLockStack  thrpt   10   6218274.277 ± 196543.116  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            8  HeapArrayLockStack  thrpt   10   2765940.852 ± 145926.320  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock           16  HeapArrayLockStack  thrpt   10   1549256.460 ±  97680.674  ops/s

    // RWlock
    //Benchmark                                     (stackSize)              (type)   Mode  Cnt         Score         Error  Units
    //JmhPageListenerLockStackBenchmark.lockUnlock            2  HeapArrayLockStack  thrpt   10   9633344.414 ±  728236.796  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            4  HeapArrayLockStack  thrpt   10   4852075.852 ±  255843.100  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock            8  HeapArrayLockStack  thrpt   10   2399365.642 ±   68528.195  ops/s
    //JmhPageListenerLockStackBenchmark.lockUnlock           16  HeapArrayLockStack  thrpt   10   1165728.828 ±  120575.800  ops/s

    public static void main(String[] args) throws Exception {
        JmhIdeBenchmarkRunner.create()
            .forks(1)
            .threads(1)
            .warmupIterations(10)
            .measurementIterations(10)
            .benchmarks(JmhPageListenerLockStackBenchmark.class.getSimpleName())
            .jvmArguments("-Xms4g", "-Xmx4g")
            .run();

    }

    @State(Scope.Thread)
    public static class ThreadLocalState {
        PageLockListener pl;

        @Param({"2", "4", "8", "16"})
        int stackSize;

        @Param({"HeapArrayLockStack"})
        String type;

        int cacheId = 123;

        @Setup
        public void doSetup() {
            pl = create(Thread.currentThread().getName(), type);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void lockUnlock(ThreadLocalState localState) {
        PageLockListener pl = localState.pl;

        for (int i = 0; i < localState.stackSize; i++) {
            int pageId = i + 1;

            pl.onBeforeReadLock(localState.cacheId, pageId, pageId);

            pl.onReadLock(localState.cacheId, pageId, pageId, pageId);
        }

        for (int i = localState.stackSize; i > 0; i--) {
            int pageId = i;

            pl.onReadUnlock(localState.cacheId, pageId, pageId, pageId);
        }
    }

    private static PageLockListener create(String name, String type) {
        switch (type) {
            case "HeapArrayLockStack":
                return new HeapArrayLockStack(name);
            case "HeapArrayLockLog":
                return new HeapArrayLockLog(name);
            case "OffHeapLockStack":
                return new OffHeapLockStack();
        }

        return null;
    }
}
