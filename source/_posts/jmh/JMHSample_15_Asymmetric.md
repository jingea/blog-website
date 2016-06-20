category: JMH
date: 2016-06-20
title: 基准测试分组
---
## 基本用法
到目前为止, 我们的每个基准测试会在所有的线程中执行. 但是有时候你也许不想这样. JMH提供了`@Group`注解, 这个注解可以帮我们将一组基准测试放到同一个Group下, 每个Group下的基准测试被不同数量的线程执行

我们可以指定Group中运行线程的数量. Group中的每一个线程都会执行被`@Group-annotated @Benchmark`标注的方法。

注意在`Scope.Benchmark`和`Scope.Thread`这俩种作用域中, 你要不然将所有的都在state中共享出来，要不然就什么都不共享，也就是说你不能选择，我能否只在某些state中共享一些东西？答案是不。但幸运的是JMH为我们提供了`Scope.Group`这种中间态. Group可以让你只在一个Group内部进行share, Group之间看不到你share的东西.

我们来看一下下面的例子：
1. 我们定义了一个Group--`g`, 在该Group中, 有三个线程执行`inc()`方法, 有一个线程执行`get()`方法, 因此在每个Group中总共有4个线程.
2. 每一个Group内部都会有一个`@State`实例(该实例只在Group内部共享, 在Group间是不共享的)

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
```

## 稳定性能
事实上，当你使用多个线程运行基准测试时, 每个线程的创建和销毁都会带来性能消耗.

一般比较理想的情况是暂停所有的线程, 然后让其全部一起启动.但是, 我们很难实现这一点. 相比那个方案,更好的解决方案是随着iterations的执行逐渐增加线程, 然后让系统自动地开始测量工作.
> 除了逐渐加大线程外,还可以逐渐降低线程

```java
package testJMH;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMHSample_17_SyncIterations {

    private double src;

    @Benchmark
    public double test() {
        double s = src;
        for (int i = 0; i < 1000; i++) {
            s = Math.sin(s);
        }
        return s;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_17_SyncIterations.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(20)
                .threads(Runtime.getRuntime().availableProcessors()*16)
                .forks(1)
                .syncIterations(true) // try to switch to "false"
                .build();

        new Runner(opt).run();
    }
}
```
执行结果
```java
# Warmup: 1 iterations, 1 s each
# Measurement: 20 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 64 threads, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: testJMH.JMHSample_17_SyncIterations.test

# Run progress: 0.00% complete, ETA 00:00:21
# Fork: 1 of 1
# Warmup Iteration   1: 181.766 ops/ms
Iteration   1: 180.826 ops/ms
Iteration   2: 181.056 ops/ms
Iteration   3: 185.726 ops/ms
Iteration   4: 182.754 ops/ms
Iteration   5: 184.015 ops/ms
Iteration   6: 181.900 ops/ms
Iteration   7: 183.166 ops/ms
Iteration   8: 181.252 ops/ms
Iteration   9: 180.657 ops/ms
Iteration  10: 177.032 ops/ms
Iteration  11: 183.079 ops/ms
Iteration  12: 180.797 ops/ms
Iteration  13: 181.203 ops/ms
Iteration  14: 183.192 ops/ms
Iteration  15: 179.436 ops/ms
Iteration  16: 178.482 ops/ms
Iteration  17: 167.333 ops/ms
Iteration  18: 171.103 ops/ms
Iteration  19: 152.220 ops/ms
Iteration  20: 183.191 ops/ms


Result "test":
  178.921 ±(99.9%) 6.623 ops/ms [Average]
  (min, avg, max) = (152.220, 178.921, 185.726), stdev = 7.627
  CI (99.9%): [172.298, 185.544] (assumes normal distribution)


# Run complete. Total time: 00:01:22

Benchmark                                  Mode  Cnt    Score   Error   Units
testJMH.JMHSample_17_SyncIterations.test  thrpt   20  178.921 ± 6.623  ops/ms
```

## 状态监控

Sometimes you need the tap into the harness mind to get the info on the transition change. For this, we have the experimental state object,
Control, which is updated by JMH as we go.


In this example, we want to estimate the ping-pong speed for the simple AtomicBoolean. Unfortunately, doing that in naive manner will livelock
one of the threads, because the executions of ping/pong are not paired perfectly. We need the escape hatch to terminate the loop if threads are about to leave the measurement.

```java
package testJMH;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

@State(Scope.Group)
public class JMHSample_18_Control {

    public final AtomicBoolean flag = new AtomicBoolean();

    @Benchmark
    @Group("pingpong")
    public void ping(Control cnt) {
        while (!cnt.stopMeasurement && !flag.compareAndSet(false, true)) {
            // this body is intentionally left blank
        }
    }

    @Benchmark
    @Group("pingpong")
    public void pong(Control cnt) {
        while (!cnt.stopMeasurement && !flag.compareAndSet(true, false)) {
            // this body is intentionally left blank
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_18_Control.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(5)
                .threads(2)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
```
执行结果
```java
# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 2 threads, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: testJMH.JMHSample_18_Control.pingpong

# Run progress: 0.00% complete, ETA 00:00:06
# Fork: 1 of 1
# Warmup Iteration   1: 42280310.914 ops/s
Iteration   1: 42949704.998 ops/s
                 ping: 21473046.746 ops/s
                 pong: 21476658.252 ops/s

Iteration   2: 48915249.149 ops/s
                 ping: 24457648.469 ops/s
                 pong: 24457600.680 ops/s

Iteration   3: 52501793.545 ops/s
                 ping: 26250454.343 ops/s
                 pong: 26251339.202 ops/s

Iteration   4: 48114008.316 ops/s
                 ping: 24054823.143 ops/s
                 pong: 24059185.172 ops/s

Iteration   5: 46195152.007 ops/s
                 ping: 23088596.608 ops/s
                 pong: 23106555.399 ops/s



Result "ping":
  47735181.603 ±(99.9%) 13549826.720 ops/s [Average]
  (min, avg, max) = (42949704.998, 47735181.603, 52501793.545), stdev = 3518846.970
  CI (99.9%): [34185354.883, 61285008.323] (assumes normal distribution)

Secondary result "ping":
  23864913.862 ±(99.9%) 6780277.646 ops/s [Average]
  (min, avg, max) = (21473046.746, 23864913.862, 26250454.343), stdev = 1760816.573
  CI (99.9%): [17084636.216, 30645191.508] (assumes normal distribution)

Secondary result "pong":
  23870267.741 ±(99.9%) 6769573.852 ops/s [Average]
  (min, avg, max) = (21476658.252, 23870267.741, 26251339.202), stdev = 1758036.832
  CI (99.9%): [17100693.889, 30639841.593] (assumes normal distribution)


# Run complete. Total time: 00:00:06

Benchmark                                    Mode  Cnt         Score          Error  Units
testJMH.JMHSample_18_Control.pingpong       thrpt    5  47735181.603 ± 13549826.720  ops/s
testJMH.JMHSample_18_Control.pingpong:ping  thrpt    5  23864913.862 ±  6780277.646  ops/s
testJMH.JMHSample_18_Control.pingpong:pong  thrpt    5  23870267.741 ±  6769573.852  ops/s
```
