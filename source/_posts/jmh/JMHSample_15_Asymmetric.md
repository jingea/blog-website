category: JMH
date: 2016-06-
title:
---

```java
package testJMH;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Group)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_15_Asymmetric {

    /*
     * So far all the tests were symmetric: the same code was executed in all the threads.
     * At times, you need the asymmetric test. JMH provides this with the notion of @Group,
     * which can bind several methods together, and all the threads are distributed among
     * the test methods.
     *
     * Each execution group contains of one or more threads. Each thread within a particular
     * execution group executes one of @Group-annotated @Benchmark methods. Multiple execution
     * groups may participate in the run. The total thread count in the run is rounded to the
     * execution group size, which will only allow the full execution groups.
     *
     * Note that two state scopes: Scope.Benchmark and Scope.Thread are not covering all
     * the use cases here -- you either share everything in the state, or share nothing.
     * To break this, we have the middle ground Scope.Group, which marks the state to be
     * shared within the execution group, but not among the execution groups.
     *
     * Putting this all together, the example below means:
     *  a) define the execution group "g", with 3 threads executing inc(), and 1 thread
     *     executing get(), 4 threads per group in total;
     *  b) if we run this test case with 4 threads, then we will have a single execution
     *     group. Generally, running with 4*N threads will create N execution groups, etc.;
     *  c) each execution group has one @State instance to share: that is, execution groups
     *     share the counter within the group, but not across the groups.
     */

    private AtomicInteger counter;

    @Setup
    public void up() {
        counter = new AtomicInteger();
    }

    @Benchmark
    @Group("g")
    @GroupThreads(3)
    public int inc() {
        return counter.incrementAndGet();
    }

    @Benchmark
    @Group("g")
    @GroupThreads(1)
    public int get() {
        return counter.get();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_15_Asymmetric.class.getSimpleName())
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
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_15_Asymmetric.g

# Run progress: 0.00% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 68.093 ±(99.9%) 81.322 ns/op
# Warmup Iteration   2: 60.610 ±(99.9%) 88.780 ns/op
# Warmup Iteration   3: 66.626 ±(99.9%) 93.256 ns/op
# Warmup Iteration   4: 69.567 ±(99.9%) 120.424 ns/op
# Warmup Iteration   5: 62.893 ±(99.9%) 113.488 ns/op
Iteration   1: 68.018 ±(99.9%) 122.220 ns/op
                 get: 39.831 ns/op
                 inc: 77.413 ±(99.9%) 47.962 ns/op

Iteration   2: 69.857 ±(99.9%) 121.449 ns/op
                 get: 42.502 ns/op
                 inc: 78.976 ±(99.9%) 101.521 ns/op

Iteration   3: 55.663 ±(99.9%) 95.740 ns/op
                 get: 33.440 ns/op
                 inc: 63.071 ±(99.9%) 2.406 ns/op

Iteration   4: 65.669 ±(99.9%) 99.512 ns/op
                 get: 43.453 ns/op
                 inc: 73.075 ±(99.9%) 94.211 ns/op

Iteration   5: 65.077 ±(99.9%) 108.201 ns/op
                 get: 40.545 ns/op
                 inc: 73.255 ±(99.9%) 80.184 ns/op



Result "get":
  64.857 ±(99.9%) 21.111 ns/op [Average]
  (min, avg, max) = (55.663, 64.857, 69.857), stdev = 5.482
  CI (99.9%): [43.746, 85.968] (assumes normal distribution)

Secondary result "get":
  39.954 ±(99.9%) 15.102 ns/op [Average]
  (min, avg, max) = (33.440, 39.954, 43.453), stdev = 3.922
  CI (99.9%): [24.852, 55.056] (assumes normal distribution)

Secondary result "inc":
  73.158 ±(99.9%) 23.871 ns/op [Average]
  (min, avg, max) = (63.071, 73.158, 78.976), stdev = 6.199
  CI (99.9%): [49.287, 97.029] (assumes normal distribution)


# Run complete. Total time: 00:00:11

Benchmark                              Mode  Cnt   Score    Error  Units
testJMH.JMHSample_15_Asymmetric.g      avgt    5  64.857 ± 21.111  ns/op
testJMH.JMHSample_15_Asymmetric.g:get  avgt    5  39.954 ± 15.102  ns/op
testJMH.JMHSample_15_Asymmetric.g:inc  avgt    5  73.158 ± 23.871  ns/op

Process finished with exit code 0


```
