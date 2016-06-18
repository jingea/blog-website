category: JMH
date: 2016-06-14
title: 在基准测试时不要自己循环测试
---
在benchmarked自己实现循环是一个非常糟糕的主意. 具体看下面的例子
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
public class TestLoops {

    int x = 1;
    int y = 2;

    @Benchmark
    public int measureRight() {
        return (x + y);
    }

    private int reps(int reps) {
        int s = 0;
        for (int i = 0; i < reps; i++) {
            s += (x + y);
        }
        return s;
    }

    /*
     * 我们会测试多种不同循环次数下的(x + y)测试.
     * 我们使用OperationsPerInvocation来增加相应的每次调用时的执行次数
     * 例如, 循环100次的话, 则这个值为100, 这么着可以算出, 每个计算所耗时间
     */

    @Benchmark
    @OperationsPerInvocation(1)
    public int measureWrong_1() {
        return reps(1);
    }

    @Benchmark
    @OperationsPerInvocation(10)
    public int measureWrong_10() {
        return reps(10);
    }

    @Benchmark
    @OperationsPerInvocation(100)
    public int measureWrong_100() {
        return reps(100);
    }

    @Benchmark
    @OperationsPerInvocation(1000)
    public int measureWrong_1000() {
        return reps(1000);
    }

    @Benchmark
    @OperationsPerInvocation(10000)
    public int measureWrong_10000() {
        return reps(10000);
    }

    @Benchmark
    @OperationsPerInvocation(100000)
    public int measureWrong_100000() {
        return reps(100000);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestLoops.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
```
执行结果
```java
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.TestLoops.measureRight

# Run progress: 0.00% complete, ETA 00:01:10
# Fork: 1 of 1
# Warmup Iteration   1: 5.116 ns/op
# Warmup Iteration   2: 3.774 ns/op
# Warmup Iteration   3: 3.329 ns/op
# Warmup Iteration   4: 3.669 ns/op
# Warmup Iteration   5: 3.487 ns/op
Iteration   1: 3.291 ns/op
Iteration   2: 3.383 ns/op
Iteration   3: 3.353 ns/op
Iteration   4: 4.482 ns/op
Iteration   5: 4.652 ns/op


Result "measureRight":
  3.832 ±(99.9%) 2.597 ns/op [Average]
  (min, avg, max) = (3.291, 3.832, 4.652), stdev = 0.675
  CI (99.9%): [1.235, 6.429] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.TestLoops.measureWrong_1

# Run progress: 14.29% complete, ETA 00:01:11
# Fork: 1 of 1
# Warmup Iteration   1: 3.530 ns/op
# Warmup Iteration   2: 3.631 ns/op
# Warmup Iteration   3: 3.912 ns/op
# Warmup Iteration   4: 3.682 ns/op
# Warmup Iteration   5: 3.366 ns/op
Iteration   1: 3.499 ns/op
Iteration   2: 3.261 ns/op
Iteration   3: 3.267 ns/op
Iteration   4: 3.285 ns/op
Iteration   5: 3.233 ns/op


Result "measureWrong_1":
  3.309 ±(99.9%) 0.416 ns/op [Average]
  (min, avg, max) = (3.233, 3.309, 3.499), stdev = 0.108
  CI (99.9%): [2.893, 3.725] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.TestLoops.measureWrong_10

# Run progress: 28.57% complete, ETA 00:00:57
# Fork: 1 of 1
# Warmup Iteration   1: 0.402 ns/op
# Warmup Iteration   2: 0.408 ns/op
# Warmup Iteration   3: 0.415 ns/op
# Warmup Iteration   4: 0.408 ns/op
# Warmup Iteration   5: 0.398 ns/op
Iteration   1: 0.392 ns/op
Iteration   2: 0.395 ns/op
Iteration   3: 0.396 ns/op
Iteration   4: 0.387 ns/op
Iteration   5: 0.398 ns/op


Result "measureWrong_10":
  0.394 ±(99.9%) 0.017 ns/op [Average]
  (min, avg, max) = (0.387, 0.394, 0.398), stdev = 0.004
  CI (99.9%): [0.377, 0.410] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.TestLoops.measureWrong_100

# Run progress: 42.86% complete, ETA 00:00:44
# Fork: 1 of 1
# Warmup Iteration   1: 0.049 ns/op
# Warmup Iteration   2: 0.047 ns/op
# Warmup Iteration   3: 0.046 ns/op
# Warmup Iteration   4: 0.045 ns/op
# Warmup Iteration   5: 0.046 ns/op
Iteration   1: 0.046 ns/op
Iteration   2: 0.046 ns/op
Iteration   3: 0.046 ns/op
Iteration   4: 0.045 ns/op
Iteration   5: 0.046 ns/op


Result "measureWrong_100":
  0.046 ±(99.9%) 0.002 ns/op [Average]
  (min, avg, max) = (0.045, 0.046, 0.046), stdev = 0.001
  CI (99.9%): [0.044, 0.047] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.TestLoops.measureWrong_1000

# Run progress: 57.14% complete, ETA 00:00:33
# Fork: 1 of 1
# Warmup Iteration   1: 0.057 ns/op
# Warmup Iteration   2: 0.057 ns/op
# Warmup Iteration   3: 0.055 ns/op
# Warmup Iteration   4: 0.057 ns/op
# Warmup Iteration   5: 0.056 ns/op
Iteration   1: 0.057 ns/op
Iteration   2: 0.057 ns/op
Iteration   3: 0.057 ns/op
Iteration   4: 0.058 ns/op
Iteration   5: 0.058 ns/op


Result "measureWrong_1000":
  0.057 ±(99.9%) 0.002 ns/op [Average]
  (min, avg, max) = (0.057, 0.057, 0.058), stdev = 0.001
  CI (99.9%): [0.055, 0.059] (assumes normal distribution)


# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.TestLoops.measureWrong_10000

# Run progress: 71.43% complete, ETA 00:00:22
# Fork: 1 of 1
# Warmup Iteration   1: 0.031 ns/op
# Warmup Iteration   2: 0.031 ns/op
# Warmup Iteration   3: 0.032 ns/op
# Warmup Iteration   4: 0.033 ns/op
# Warmup Iteration   5: 0.034 ns/op
Iteration   1: 0.036 ns/op
Iteration   2: 0.033 ns/op
Iteration   3: 0.033 ns/op
Iteration   4: 0.033 ns/op
Iteration   5: 0.033 ns/op


Result "measureWrong_10000":
  0.034 ±(99.9%) 0.005 ns/op [Average]
  (min, avg, max) = (0.033, 0.034, 0.036), stdev = 0.001
  CI (99.9%): [0.029, 0.038] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.TestLoops.measureWrong_100000

# Run progress: 85.71% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 0.028 ns/op
# Warmup Iteration   2: 0.028 ns/op
# Warmup Iteration   3: 0.026 ns/op
# Warmup Iteration   4: 0.027 ns/op
# Warmup Iteration   5: 0.027 ns/op
Iteration   1: 0.027 ns/op
Iteration   2: 0.028 ns/op
Iteration   3: 0.027 ns/op
Iteration   4: 0.027 ns/op
Iteration   5: 0.028 ns/op


Result "measureWrong_100000":
  0.027 ±(99.9%) 0.002 ns/op [Average]
  (min, avg, max) = (0.027, 0.027, 0.028), stdev = 0.001
  CI (99.9%): [0.026, 0.029] (assumes normal distribution)


# Run complete. Total time: 00:01:16

Benchmark                              Mode  Cnt  Score   Error  Units
testJMH.TestLoops.measureRight         avgt    5  3.832 ± 2.597  ns/op
testJMH.TestLoops.measureWrong_1       avgt    5  3.309 ± 0.416  ns/op
testJMH.TestLoops.measureWrong_10      avgt    5  0.394 ± 0.017  ns/op
testJMH.TestLoops.measureWrong_100     avgt    5  0.046 ± 0.002  ns/op
testJMH.TestLoops.measureWrong_1000    avgt    5  0.057 ± 0.002  ns/op
testJMH.TestLoops.measureWrong_10000   avgt    5  0.034 ± 0.005  ns/op
testJMH.TestLoops.measureWrong_100000  avgt    5  0.027 ± 0.002  ns/op
```
从上面的结果我们可以看出, 随着循环次数的增加, 每个基本运算的时间也越来越少. 因此在基准测试的时候, 是不建议使用循环的, 我们应该依赖JMH提供的机制来实现多次运算
