category: JMH
date: 2016-06-
title:
---
在有的基准测试中, 你只需要运行一些空循环。 也就是说我们想要线程一直在运行着CPU, 而不是让出CPU进入等待模式. JMH为我们提供了`Blackholes`来完成这件事.
```java
package testJMH;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_21_ConsumeCPU {

    @Benchmark
    public void consume_0000() {
        Blackhole.consumeCPU(0);
    }

    @Benchmark
    public void consume_0001() {
        Blackhole.consumeCPU(1);
    }

    @Benchmark
    public void consume_0002() {
        Blackhole.consumeCPU(2);
    }

    @Benchmark
    public void consume_0004() {
        Blackhole.consumeCPU(4);
    }

    @Benchmark
    public void consume_0008() {
        Blackhole.consumeCPU(8);
    }

    @Benchmark
    public void consume_0016() {
        Blackhole.consumeCPU(16);
    }

    @Benchmark
    public void consume_0032() {
        Blackhole.consumeCPU(32);
    }

    @Benchmark
    public void consume_0064() {
        Blackhole.consumeCPU(64);
    }

    @Benchmark
    public void consume_0128() {
        Blackhole.consumeCPU(128);
    }

    @Benchmark
    public void consume_0256() {
        Blackhole.consumeCPU(256);
    }

    @Benchmark
    public void consume_0512() {
        Blackhole.consumeCPU(512);
    }

    @Benchmark
    public void consume_1024() {
        Blackhole.consumeCPU(1024);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_21_ConsumeCPU.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(5)
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
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0000

# Run progress: 0.00% complete, ETA 00:01:12
# Fork: 1 of 1
# Warmup Iteration   1: 3.272 ns/op
Iteration   1: 2.807 ns/op
Iteration   2: 2.243 ns/op
Iteration   3: 2.236 ns/op
Iteration   4: 2.259 ns/op
Iteration   5: 2.327 ns/op


Result "consume_0000":
  2.374 ±(99.9%) 0.941 ns/op [Average]
  (min, avg, max) = (2.236, 2.374, 2.807), stdev = 0.244
  CI (99.9%): [1.433, 3.316] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0001

# Run progress: 8.33% complete, ETA 00:01:14
# Fork: 1 of 1
# Warmup Iteration   1: 4.059 ns/op
Iteration   1: 3.372 ns/op
Iteration   2: 3.707 ns/op
Iteration   3: 3.252 ns/op
Iteration   4: 3.356 ns/op
Iteration   5: 3.343 ns/op


Result "consume_0001":
  3.406 ±(99.9%) 0.672 ns/op [Average]
  (min, avg, max) = (3.252, 3.406, 3.707), stdev = 0.175
  CI (99.9%): [2.733, 4.078] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0002

# Run progress: 16.67% complete, ETA 00:01:06
# Fork: 1 of 1
# Warmup Iteration   1: 4.948 ns/op
Iteration   1: 4.641 ns/op
Iteration   2: 4.366 ns/op
Iteration   3: 4.357 ns/op
Iteration   4: 4.319 ns/op
Iteration   5: 4.447 ns/op


Result "consume_0002":
  4.426 ±(99.9%) 0.497 ns/op [Average]
  (min, avg, max) = (4.319, 4.426, 4.641), stdev = 0.129
  CI (99.9%): [3.930, 4.923] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0004

# Run progress: 25.00% complete, ETA 00:00:59
# Fork: 1 of 1
# Warmup Iteration   1: 7.068 ns/op
Iteration   1: 6.780 ns/op
Iteration   2: 6.286 ns/op
Iteration   3: 6.295 ns/op
Iteration   4: 6.314 ns/op
Iteration   5: 6.298 ns/op


Result "consume_0004":
  6.395 ±(99.9%) 0.831 ns/op [Average]
  (min, avg, max) = (6.286, 6.395, 6.780), stdev = 0.216
  CI (99.9%): [5.564, 7.226] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0008

# Run progress: 33.33% complete, ETA 00:00:52
# Fork: 1 of 1
# Warmup Iteration   1: 12.439 ns/op
Iteration   1: 12.165 ns/op
Iteration   2: 11.607 ns/op
Iteration   3: 11.737 ns/op
Iteration   4: 11.644 ns/op
Iteration   5: 12.074 ns/op


Result "consume_0008":
  11.845 ±(99.9%) 0.988 ns/op [Average]
  (min, avg, max) = (11.607, 11.845, 12.165), stdev = 0.257
  CI (99.9%): [10.857, 12.833] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0016

# Run progress: 41.67% complete, ETA 00:00:45
# Fork: 1 of 1
# Warmup Iteration   1: 26.978 ns/op
Iteration   1: 27.067 ns/op
Iteration   2: 26.376 ns/op
Iteration   3: 26.831 ns/op
Iteration   4: 26.890 ns/op
Iteration   5: 27.059 ns/op


Result "consume_0016":
  26.845 ±(99.9%) 1.084 ns/op [Average]
  (min, avg, max) = (26.376, 26.845, 27.067), stdev = 0.282
  CI (99.9%): [25.760, 27.929] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0032

# Run progress: 50.00% complete, ETA 00:00:39
# Fork: 1 of 1
# Warmup Iteration   1: 64.409 ns/op
Iteration   1: 64.341 ns/op
Iteration   2: 65.125 ns/op
Iteration   3: 63.601 ns/op
Iteration   4: 63.439 ns/op
Iteration   5: 64.124 ns/op


Result "consume_0032":
  64.126 ±(99.9%) 2.578 ns/op [Average]
  (min, avg, max) = (63.439, 64.126, 65.125), stdev = 0.670
  CI (99.9%): [61.548, 66.704] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0064

# Run progress: 58.33% complete, ETA 00:00:32
# Fork: 1 of 1
# Warmup Iteration   1: 145.775 ns/op
Iteration   1: 143.749 ns/op
Iteration   2: 142.517 ns/op
Iteration   3: 140.675 ns/op
Iteration   4: 140.859 ns/op
Iteration   5: 140.396 ns/op


Result "consume_0064":
  141.639 ±(99.9%) 5.549 ns/op [Average]
  (min, avg, max) = (140.396, 141.639, 143.749), stdev = 1.441
  CI (99.9%): [136.091, 147.188] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0128

# Run progress: 66.67% complete, ETA 00:00:26
# Fork: 1 of 1
# Warmup Iteration   1: 297.008 ns/op
Iteration   1: 290.565 ns/op
Iteration   2: 291.462 ns/op
Iteration   3: 291.987 ns/op
Iteration   4: 288.341 ns/op
Iteration   5: 295.416 ns/op


Result "consume_0128":
  291.554 ±(99.9%) 9.895 ns/op [Average]
  (min, avg, max) = (288.341, 291.554, 295.416), stdev = 2.570
  CI (99.9%): [281.659, 301.450] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0256

# Run progress: 75.00% complete, ETA 00:00:19
# Fork: 1 of 1
# Warmup Iteration   1: 588.822 ns/op
Iteration   1: 601.662 ns/op
Iteration   2: 593.592 ns/op
Iteration   3: 614.124 ns/op
Iteration   4: 594.434 ns/op
Iteration   5: 609.548 ns/op


Result "consume_0256":
  602.672 ±(99.9%) 34.962 ns/op [Average]
  (min, avg, max) = (593.592, 602.672, 614.124), stdev = 9.080
  CI (99.9%): [567.710, 637.634] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_0512

# Run progress: 83.33% complete, ETA 00:00:13
# Fork: 1 of 1
# Warmup Iteration   1: 1224.166 ns/op
Iteration   1: 1345.150 ns/op
Iteration   2: 1212.876 ns/op
Iteration   3: 1193.632 ns/op
Iteration   4: 1216.793 ns/op
Iteration   5: 1195.562 ns/op


Result "consume_0512":
  1232.803 ±(99.9%) 245.027 ns/op [Average]
  (min, avg, max) = (1193.632, 1232.803, 1345.150), stdev = 63.633
  CI (99.9%): [987.775, 1477.830] (assumes normal distribution)



# Warmup: 1 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_21_ConsumeCPU.consume_1024

# Run progress: 91.67% complete, ETA 00:00:06
# Fork: 1 of 1
# Warmup Iteration   1: 2449.789 ns/op
Iteration   1: 2485.108 ns/op
Iteration   2: 2382.116 ns/op
Iteration   3: 2368.816 ns/op
Iteration   4: 2389.495 ns/op
Iteration   5: 2359.214 ns/op


Result "consume_1024":
  2396.950 ±(99.9%) 195.047 ns/op [Average]
  (min, avg, max) = (2359.214, 2396.950, 2485.108), stdev = 50.653
  CI (99.9%): [2201.902, 2591.997] (assumes normal distribution)


# Run complete. Total time: 00:01:18

Benchmark                                     Mode  Cnt     Score     Error  Units
testJMH.JMHSample_21_ConsumeCPU.consume_0000  avgt    5     2.374 ±   0.941  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0001  avgt    5     3.406 ±   0.672  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0002  avgt    5     4.426 ±   0.497  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0004  avgt    5     6.395 ±   0.831  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0008  avgt    5    11.845 ±   0.988  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0016  avgt    5    26.845 ±   1.084  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0032  avgt    5    64.126 ±   2.578  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0064  avgt    5   141.639 ±   5.549  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0128  avgt    5   291.554 ±   9.895  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0256  avgt    5   602.672 ±  34.962  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_0512  avgt    5  1232.803 ± 245.027  ns/op
testJMH.JMHSample_21_ConsumeCPU.consume_1024  avgt    5  2396.950 ± 195.047  ns/op
```
