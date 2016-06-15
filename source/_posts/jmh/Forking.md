category: JMH
date: 2016-06-
title: Forking
---
/*
     * JVMs are notoriously good at profile-guided optimizations. This is bad
     * for benchmarks, because different tests can mix their profiles together,
     * and then render the "uniformly bad" code for every test. Forking (running
     * in a separate process) each test can help to evade this issue.
     *
     * JMH will fork the tests by default.
     */

    /*
     * Suppose we have this simple counter interface, and two implementations.
     * Even though those are semantically the same, from the JVM standpoint,
     * those are distinct classes.
     */
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

    /*
     * And this is how we measure it.
     * Note this is susceptible for same issue with loops we mention in previous examples.
     */

    public int measure(Counter c) {
        int s = 0;
        for (int i = 0; i < 10; i++) {
            s += c.inc();
        }
        return s;
    }

    /*
     * These are two counters.
     */
    Counter c1 = new Counter1();
    Counter c2 = new Counter2();

    /*
     * We first measure the Counter1 alone...
     * Fork(0) helps to run in the same JVM.
     */

    @Benchmark
    @Fork(0)
    public int measure_1_c1() {
        return measure(c1);
    }

    /*
     * Then Counter2...
     */

    @Benchmark
    @Fork(0)
    public int measure_2_c2() {
        return measure(c2);
    }

    /*
     * Then Counter1 again...
     */

    @Benchmark
    @Fork(0)
    public int measure_3_c1_again() {
        return measure(c1);
    }

    /*
     * These two tests have explicit @Fork annotation.
     * JMH takes this annotation as the request to run the test in the forked JVM.
     * It's even simpler to force this behavior for all the tests via the command
     * line option "-f". The forking is default, but we still use the annotation
     * for the consistency.
     *
     * This is the test for Counter1.
     */

    @Benchmark
    @Fork(1)
    public int measure_4_forked_c1() {
        return measure(c1);
    }

    /*
     * ...and this is the test for Counter2.
     */

    @Benchmark
    @Fork(1)
    public int measure_5_forked_c2() {
        return measure(c2);
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
执行结果
```java
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_4_forked_c1

# Run progress: 0.00% complete, ETA 00:00:50
# Fork: 1 of 1
# Warmup Iteration   1: 34.317 ns/op
# Warmup Iteration   2: 23.322 ns/op
# Warmup Iteration   3: 7.146 ns/op
# Warmup Iteration   4: 5.793 ns/op
# Warmup Iteration   5: 6.199 ns/op
Iteration   1: 7.197 ns/op
Iteration   2: 7.556 ns/op
Iteration   3: 5.822 ns/op
Iteration   4: 5.889 ns/op
Iteration   5: 5.809 ns/op


Result "measure_4_forked_c1":
  6.455 ±(99.9%) 3.279 ns/op [Average]
  (min, avg, max) = (5.809, 6.455, 7.556), stdev = 0.852
  CI (99.9%): [3.175, 9.734] (assumes normal distribution)


# JMH 1.11.2 (released 230 days ago, please consider updating!)
# VM version: JDK 1.8.0_05, VM 25.5-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_5_forked_c2

# Run progress: 20.00% complete, ETA 00:00:47
# Fork: 1 of 1
# Warmup Iteration   1: 7.388 ns/op
# Warmup Iteration   2: 6.394 ns/op
# Warmup Iteration   3: 6.015 ns/op
# Warmup Iteration   4: 9.844 ns/op
# Warmup Iteration   5: 8.153 ns/op
Iteration   1: 6.856 ns/op
Iteration   2: 7.255 ns/op
Iteration   3: 5.946 ns/op
Iteration   4: 5.846 ns/op
Iteration   5: 5.799 ns/op


Result "measure_5_forked_c2":
  6.340 ±(99.9%) 2.579 ns/op [Average]
  (min, avg, max) = (5.799, 6.340, 7.255), stdev = 0.670
  CI (99.9%): [3.761, 8.920] (assumes normal distribution)


# JMH 1.11.2 (released 230 days ago, please consider updating!)
# VM version: JDK 1.8.0_05, VM 25.5-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_1_c1

# Run progress: 40.00% complete, ETA 00:00:33
# Fork: N/A, test runs in the existing VM
# Warmup Iteration   1: 3.637 ns/op
# Warmup Iteration   2: 3.603 ns/op
# Warmup Iteration   3: 3.529 ns/op
# Warmup Iteration   4: 3.575 ns/op
# Warmup Iteration   5: 3.568 ns/op
Iteration   1: 3.574 ns/op
Iteration   2: 3.578 ns/op
Iteration   3: 3.533 ns/op
Iteration   4: 3.513 ns/op
Iteration   5: 3.450 ns/op

Result "measure_1_c1":
  3.530 ±(99.9%) 0.201 ns/op [Average]
  (min, avg, max) = (3.450, 3.530, 3.578), stdev = 0.052
  CI (99.9%): [3.329, 3.731] (assumes normal distribution)


# JMH 1.11.2 (released 230 days ago, please consider updating!)
# VM version: JDK 1.8.0_05, VM 25.5-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_2_c2

# Run progress: 60.00% complete, ETA 00:00:21
# Fork: N/A, test runs in the existing VM
# Warmup Iteration   1: 23.849 ns/op
# Warmup Iteration   2: 24.086 ns/op
# Warmup Iteration   3: 25.127 ns/op
# Warmup Iteration   4: 25.316 ns/op
# Warmup Iteration   5: 24.769 ns/op
Iteration   1: 24.775 ns/op
Iteration   2: 24.572 ns/op
Iteration   3: 24.591 ns/op
Iteration   4: 25.049 ns/op
Iteration   5: 25.740 ns/op

Result "measure_2_c2":
  24.945 ±(99.9%) 1.863 ns/op [Average]
  (min, avg, max) = (24.572, 24.945, 25.740), stdev = 0.484
  CI (99.9%): [23.083, 26.808] (assumes normal distribution)


# JMH 1.11.2 (released 230 days ago, please consider updating!)
# VM version: JDK 1.8.0_05, VM 25.5-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_3_c1_again

# Run progress: 80.00% complete, ETA 00:00:10
# Fork: N/A, test runs in the existing VM
# Warmup Iteration   1: 24.785 ns/op
# Warmup Iteration   2: 26.385 ns/op
# Warmup Iteration   3: 25.089 ns/op
# Warmup Iteration   4: 24.897 ns/op
# Warmup Iteration   5: 25.518 ns/op
Iteration   1: 24.931 ns/op
Iteration   2: 24.330 ns/op
Iteration   3: 24.754 ns/op
Iteration   4: 25.231 ns/op
Iteration   5: 32.702 ns/op

Result "measure_3_c1_again":
  26.390 ±(99.9%) 13.645 ns/op [Average]
  (min, avg, max) = (24.330, 26.390, 32.702), stdev = 3.544
  CI (99.9%): [12.744, 40.035] (assumes normal distribution)


# Run complete. Total time: 00:00:52

Benchmark                                         Mode  Cnt   Score    Error  Units
testJMH.JMHSample_12_Forking.measure_1_c1         avgt    5   3.530 ±  0.201  ns/op
testJMH.JMHSample_12_Forking.measure_2_c2         avgt    5  24.945 ±  1.863  ns/op
testJMH.JMHSample_12_Forking.measure_3_c1_again   avgt    5  26.390 ± 13.645  ns/op
testJMH.JMHSample_12_Forking.measure_4_forked_c1  avgt    5   6.455 ±  3.279  ns/op
testJMH.JMHSample_12_Forking.measure_5_forked_c2  avgt    5   6.340 ±  2.579  ns/op

Process finished with exit code 0


```
