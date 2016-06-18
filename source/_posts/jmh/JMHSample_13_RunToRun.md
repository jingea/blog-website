category: JMH
date: 2016-06-17
title: Fork统计
---
使用Forking可能看到每次运行导致结果都不一样的情况. JMH可以将不同的fork出来的结果放到一起进行统计.

为了更加清晰地看出fork带来的影响, 在下面的测试中我们特意地加大了每次结果的不同
```java
package testJMH;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
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

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMHSample_13_RunToRun {

    @State(Scope.Thread)
    public static class SleepyState {
        public long sleepTime;

        @Setup
        public void setup() {
            sleepTime = (long) (Math.random() * 1000);
        }
    }

    @Benchmark
    @Fork(1)
    public void baseline(SleepyState s) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(s.sleepTime);
    }

    @Benchmark
    @Fork(3)
    public void fork_1(SleepyState s) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(s.sleepTime);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_13_RunToRun.class.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }
}
```
执行结果
```java
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_13_RunToRun.baseline

# Run progress: 0.00% complete, ETA 00:00:20
# Fork: 1 of 1
Iteration   1: 419.858 ms/op
Iteration   2: 420.190 ms/op
Iteration   3: 420.376 ms/op
Iteration   4: 420.728 ms/op
Iteration   5: 421.622 ms/op


Result "baseline":
  420.555 ±(99.9%) 2.597 ms/op [Average]
  (min, avg, max) = (419.858, 420.555, 421.622), stdev = 0.675
  CI (99.9%): [417.958, 423.152] (assumes normal distribution)


# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_13_RunToRun.fork_1

# Run progress: 25.00% complete, ETA 00:00:20
# Fork: 1 of 3
Iteration   1: 817.066 ms/op
Iteration   2: 820.061 ms/op
Iteration   3: 818.648 ms/op
Iteration   4: 818.029 ms/op
Iteration   5: 819.518 ms/op

# Run progress: 50.00% complete, ETA 00:00:16
# Fork: 2 of 3
Iteration   1: 406.259 ms/op
Iteration   2: 406.128 ms/op
Iteration   3: 405.895 ms/op
Iteration   4: 406.083 ms/op
Iteration   5: 405.879 ms/op

# Run progress: 75.00% complete, ETA 00:00:07
# Fork: 3 of 3
Iteration   1: 149.923 ms/op
Iteration   2: 150.784 ms/op
Iteration   3: 150.753 ms/op
Iteration   4: 150.717 ms/op
Iteration   5: 150.718 ms/op


Result "fork_1":
  458.431 ±(99.9%) 304.586 ms/op [Average]
  (min, avg, max) = (149.923, 458.431, 820.061), stdev = 284.910
  CI (99.9%): [153.845, 763.016] (assumes normal distribution)


# Run complete. Total time: 00:00:28

Benchmark                               Mode  Cnt    Score     Error  Units
testJMH.JMHSample_13_RunToRun.baseline  avgt    5  420.555 ±   2.597  ms/op
testJMH.JMHSample_13_RunToRun.fork_1    avgt   15  458.431 ± 304.586  ms/op
```
从上面的结果中我们可以看到, 在fork_1中, 运行了三次, 每次随机出的sleep时间都不一样, 而最后统计的时候, 却统计的是三次结果的合
