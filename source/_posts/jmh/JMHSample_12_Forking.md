category: JMH
date: 2016-06-17
title: Fork机制
---
JVM众所周知的一个特性是, 它为我们提供了各种优化配置. 但是这个特性却为基准测试带来了一些影响.
因为不同基准测试在不同的JVM配置里得到的结果可能大相径庭, 那么在同一个JVM跑不同的基准测试, 那么这些
基准测试之间可能会相互影响. 因此JVM为我们提供了fork机制, 我们可以为每个基准测试都fork出一个新的
JVM来跑基准测试.

```java
package testJMH;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_12_Forking {

    public interface Counter {
        int inc();
    }

    public class Counter1 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    public class Counter2 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    public int measure(Counter c) {
        int s = 0;
        for (int i = 0; i < 10; i++) {
            s += c.inc();
        }
        return s;
    }

    Counter c1 = new Counter1();
    Counter c2 = new Counter2();

    @Benchmark
    @Fork(0)
    public int measure_1_c1() {
        return measure(c1);
    }

    @Benchmark
    @Fork(0)
    public int measure_2_c2() {
        return measure(c2);
    }

    @Benchmark
    @Fork(0)
    public int measure_3_c1_again() {
        return measure(c1);
    }

    @Benchmark
    @Fork(1)
    public int measure_4_forked_c1() {
        return measure(c1);
    }

    @Benchmark
    @Fork(1)
    public int measure_5_forked_c2() {
        return measure(c2);
    }

    @Benchmark
    @Fork(2)
    public int measure_6_forked_c1() {
        return measure(c1);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_12_Forking.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }
}
```
从下面的结果中我们也可以看到, measure_2_c2 和 measure_3_c1_again这俩个基准测试明显的基准测试时间要长于其他的基准测试. 这是因为这俩个都是在measure_1_c1的这个JVM里运行的(也就是fork(0)起的作用). 但是其他的不管我们是fork(1)还是fork(2)都和基准相近.
```java
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_4_forked_c1

# Run progress: 0.00% complete, ETA 00:01:10
# Fork: 1 of 1
# Warmup Iteration   1: 7.696 ns/op
# Warmup Iteration   2: 6.878 ns/op
# Warmup Iteration   3: 5.862 ns/op
# Warmup Iteration   4: 5.858 ns/op
# Warmup Iteration   5: 5.779 ns/op
Iteration   1: 5.798 ns/op
Iteration   2: 5.739 ns/op
Iteration   3: 5.966 ns/op
Iteration   4: 5.826 ns/op
Iteration   5: 5.828 ns/op


Result "measure_4_forked_c1":
  5.831 ±(99.9%) 0.320 ns/op [Average]
  (min, avg, max) = (5.739, 5.831, 5.966), stdev = 0.083
  CI (99.9%): [5.511, 6.152] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_5_forked_c2

# Run progress: 14.29% complete, ETA 00:01:04
# Fork: 1 of 1
# Warmup Iteration   1: 6.429 ns/op
# Warmup Iteration   2: 6.341 ns/op
# Warmup Iteration   3: 5.787 ns/op
# Warmup Iteration   4: 5.881 ns/op
# Warmup Iteration   5: 5.921 ns/op
Iteration   1: 5.890 ns/op
Iteration   2: 5.816 ns/op
Iteration   3: 5.916 ns/op
Iteration   4: 5.940 ns/op
Iteration   5: 5.984 ns/op


Result "measure_5_forked_c2":
  5.909 ±(99.9%) 0.241 ns/op [Average]
  (min, avg, max) = (5.816, 5.909, 5.984), stdev = 0.062
  CI (99.9%): [5.669, 6.150] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_6_forked_c1

# Run progress: 28.57% complete, ETA 00:00:53
# Fork: 1 of 2
# Warmup Iteration   1: 6.401 ns/op
# Warmup Iteration   2: 6.341 ns/op
# Warmup Iteration   3: 5.703 ns/op
# Warmup Iteration   4: 5.844 ns/op
# Warmup Iteration   5: 5.828 ns/op
Iteration   1: 5.890 ns/op
Iteration   2: 6.104 ns/op
Iteration   3: 5.831 ns/op
Iteration   4: 5.788 ns/op
Iteration   5: 5.850 ns/op

# Run progress: 42.86% complete, ETA 00:00:42
# Fork: 2 of 2
# Warmup Iteration   1: 6.368 ns/op
# Warmup Iteration   2: 6.351 ns/op
# Warmup Iteration   3: 5.798 ns/op
# Warmup Iteration   4: 5.875 ns/op
# Warmup Iteration   5: 5.796 ns/op
Iteration   1: 5.839 ns/op
Iteration   2: 5.796 ns/op
Iteration   3: 5.804 ns/op
Iteration   4: 6.101 ns/op
Iteration   5: 5.916 ns/op


Result "measure_6_forked_c1":
  5.892 ±(99.9%) 0.179 ns/op [Average]
  (min, avg, max) = (5.788, 5.892, 6.104), stdev = 0.118
  CI (99.9%): [5.713, 6.070] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_1_c1

# Run progress: 57.14% complete, ETA 00:00:31
# Fork: N/A, test runs in the existing VM
# Warmup Iteration   1: 3.600 ns/op
# Warmup Iteration   2: 3.526 ns/op
# Warmup Iteration   3: 3.444 ns/op
# Warmup Iteration   4: 3.523 ns/op
# Warmup Iteration   5: 3.485 ns/op
Iteration   1: 3.549 ns/op
Iteration   2: 3.471 ns/op
Iteration   3: 3.510 ns/op
Iteration   4: 3.539 ns/op
Iteration   5: 3.501 ns/op

Result "measure_1_c1":
  3.514 ±(99.9%) 0.120 ns/op [Average]
  (min, avg, max) = (3.471, 3.514, 3.549), stdev = 0.031
  CI (99.9%): [3.394, 3.634] (assumes normal distribution)


# JMH 1.11.2 (released 233 days ago, please consider updating!)
# VM version: JDK 1.8.0_05, VM 25.5-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/bin/java
# VM options: -Didea.launcher.port=7536 -Didea.launcher.bin.path=/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_2_c2

# Run progress: 71.43% complete, ETA 00:00:21
# Fork: N/A, test runs in the existing VM
# Warmup Iteration   1: 24.203 ns/op
# Warmup Iteration   2: 23.926 ns/op
# Warmup Iteration   3: 24.443 ns/op
# Warmup Iteration   4: 24.586 ns/op
# Warmup Iteration   5: 24.770 ns/op
Iteration   1: 24.464 ns/op
Iteration   2: 24.401 ns/op
Iteration   3: 24.910 ns/op
Iteration   4: 24.466 ns/op
Iteration   5: 25.091 ns/op

Result "measure_2_c2":
  24.666 ±(99.9%) 1.204 ns/op [Average]
  (min, avg, max) = (24.401, 24.666, 25.091), stdev = 0.313
  CI (99.9%): [23.462, 25.871] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_3_c1_again

# Run progress: 85.71% complete, ETA 00:00:10
# Fork: N/A, test runs in the existing VM
# Warmup Iteration   1: 25.256 ns/op
# Warmup Iteration   2: 24.469 ns/op
# Warmup Iteration   3: 24.926 ns/op
# Warmup Iteration   4: 24.819 ns/op
# Warmup Iteration   5: 24.963 ns/op
Iteration   1: 24.882 ns/op
Iteration   2: 24.733 ns/op
Iteration   3: 25.131 ns/op
Iteration   4: 24.770 ns/op
Iteration   5: 25.195 ns/op

Result "measure_3_c1_again":
  24.942 ±(99.9%) 0.809 ns/op [Average]
  (min, avg, max) = (24.733, 24.942, 25.195), stdev = 0.210
  CI (99.9%): [24.133, 25.752] (assumes normal distribution)


# Run complete. Total time: 00:01:12

Benchmark                                         Mode  Cnt   Score   Error  Units
testJMH.JMHSample_12_Forking.measure_1_c1         avgt    5   3.514 ± 0.120  ns/op
testJMH.JMHSample_12_Forking.measure_2_c2         avgt    5  24.666 ± 1.204  ns/op
testJMH.JMHSample_12_Forking.measure_3_c1_again   avgt    5  24.942 ± 0.809  ns/op
testJMH.JMHSample_12_Forking.measure_4_forked_c1  avgt    5   5.831 ± 0.320  ns/op
testJMH.JMHSample_12_Forking.measure_5_forked_c2  avgt    5   5.909 ± 0.241  ns/op
testJMH.JMHSample_12_Forking.measure_6_forked_c1  avgt   10   5.892 ± 0.179  ns/op
```
