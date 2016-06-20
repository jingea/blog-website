category: JMH
date: 2016-06-20
title: 编译控制
---
在编译基准测试时,我们可以设置或者跳过HotSpot的一些功能．

```java
package testJMH;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_16_CompilerControl {

    public void target_blank() {
        // 特意将方法留空
    }
	
	// 下面的基准测试方法禁止使用HotSpot方法内联
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void target_dontInline() {
        // this method was intentionally left blank
    }

	// 下面的基准测试方法强制使用方法内联
    @CompilerControl(CompilerControl.Mode.INLINE)
    public void target_inline() {
        // 特意将方法留空
    }

	// 下面的基准测试方法禁止编译
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public void target_exclude() {
       // 特意将方法留空
    }

    @Benchmark
    public void baseline() {
        // 特意将方法留空
    }

    @Benchmark
    public void blank() {
        target_blank();
    }

    @Benchmark
    public void dontinline() {
        target_dontInline();
    }

    @Benchmark
    public void inline() {
        target_inline();
    }

    @Benchmark
    public void exclude() {
        target_exclude();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_16_CompilerControl.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(3)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
```
执行结果
```java
# Warmup: 1 iterations, 1 s each
# Measurement: 3 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_16_CompilerControl.baseline

# Run progress: 0.00% complete, ETA 00:00:20
# Fork: 1 of 1
# Warmup Iteration   1: 0.386 ns/op
Iteration   1: 0.360 ns/op
Iteration   2: 0.337 ns/op
Iteration   3: 0.340 ns/op


Result "baseline":
  0.346 ±(99.9%) 0.222 ns/op [Average]
  (min, avg, max) = (0.337, 0.346, 0.360), stdev = 0.012
  CI (99.9%): [0.124, 0.567] (assumes normal distribution)


# Warmup: 1 iterations, 1 s each
# Measurement: 3 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_16_CompilerControl.blank

# Run progress: 20.00% complete, ETA 00:00:18
# Fork: 1 of 1
# Warmup Iteration   1: 0.337 ns/op
Iteration   1: 0.346 ns/op
Iteration   2: 0.332 ns/op
Iteration   3: 0.332 ns/op


Result "blank":
  0.337 ±(99.9%) 0.155 ns/op [Average]
  (min, avg, max) = (0.332, 0.337, 0.346), stdev = 0.008
  CI (99.9%): [0.182, 0.491] (assumes normal distribution)


# Warmup: 1 iterations, 1 s each
# Measurement: 3 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_16_CompilerControl.dontinline

# Run progress: 40.00% complete, ETA 00:00:13
# Fork: 1 of 1
# Warmup Iteration   1: 2.481 ns/op
Iteration   1: 2.526 ns/op
Iteration   2: 2.213 ns/op
Iteration   3: 2.201 ns/op


Result "dontinline":
  2.313 ±(99.9%) 3.360 ns/op [Average]
  (min, avg, max) = (2.201, 2.313, 2.526), stdev = 0.184
  CI (99.9%): [≈ 0, 5.673] (assumes normal distribution)


# Warmup: 1 iterations, 1 s each
# Measurement: 3 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_16_CompilerControl.exclude

# Run progress: 60.00% complete, ETA 00:00:09
# Fork: 1 of 1
# Warmup Iteration   1: 17.919 ns/op
Iteration   1: 17.535 ns/op
Iteration   2: 16.820 ns/op
Iteration   3: 16.375 ns/op


Result "exclude":
  16.910 ±(99.9%) 10.676 ns/op [Average]
  (min, avg, max) = (16.375, 16.910, 17.535), stdev = 0.585
  CI (99.9%): [6.234, 27.585] (assumes normal distribution)


# Warmup: 1 iterations, 1 s each
# Measurement: 3 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_16_CompilerControl.inline

# Run progress: 80.00% complete, ETA 00:00:04
# Fork: 1 of 1
# Warmup Iteration   1: 0.336 ns/op
Iteration   1: 0.334 ns/op
Iteration   2: 0.331 ns/op
Iteration   3: 0.345 ns/op


Result "inline":
  0.337 ±(99.9%) 0.132 ns/op [Average]
  (min, avg, max) = (0.331, 0.337, 0.345), stdev = 0.007
  CI (99.9%): [0.204, 0.469] (assumes normal distribution)


# Run complete. Total time: 00:00:22

Benchmark                                        Mode  Cnt   Score    Error  Units
testJMH.JMHSample_16_CompilerControl.baseline    avgt    3   0.346 ±  0.222  ns/op
testJMH.JMHSample_16_CompilerControl.blank       avgt    3   0.337 ±  0.155  ns/op
testJMH.JMHSample_16_CompilerControl.dontinline  avgt    3   2.313 ±  3.360  ns/op
testJMH.JMHSample_16_CompilerControl.exclude     avgt    3  16.910 ± 10.676  ns/op
testJMH.JMHSample_16_CompilerControl.inline      avgt    3   0.337 ±  0.132  ns/op
```
