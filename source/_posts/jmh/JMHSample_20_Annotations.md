category: JMH
date: 2016-06-20
title: 基准测试注解
---
在前面的基准测试中, 我们将热身的设置都是在main方法中指定的，但是其实这些东西也可以使用注解的方式，注解到类上或者基准测试方法上
> 当类中和基准测试方法中都使用相同的注解时, 基准方法的会覆盖类上面的注解设置
```java
package testJMH;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
public class JMHSample_20_Annotations {

    double x1 = Math.PI;

    @Benchmark
    @Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public double measure() {
        return Math.log(x1);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_20_Annotations.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
```
执行结果
```java
# Warmup: 5 iterations, 100 ms each
# Measurement: 5 iterations, 100 ms each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: testJMH.JMHSample_20_Annotations.measure

# Run progress: 0.00% complete, ETA 00:00:01
# Fork: 1 of 1
# Warmup Iteration   1: 35.599 ops/us
# Warmup Iteration   2: 35.535 ops/us
# Warmup Iteration   3: 38.034 ops/us
# Warmup Iteration   4: 39.774 ops/us
# Warmup Iteration   5: 40.093 ops/us
Iteration   1: 37.509 ops/us
Iteration   2: 35.048 ops/us
Iteration   3: 34.100 ops/us
Iteration   4: 37.779 ops/us
Iteration   5: 39.088 ops/us


Result "measure":
  36.705 ±(99.9%) 7.940 ops/us [Average]
  (min, avg, max) = (34.100, 36.705, 39.088), stdev = 2.062
  CI (99.9%): [28.765, 44.644] (assumes normal distribution)


# Run complete. Total time: 00:00:02

Benchmark                                  Mode  Cnt   Score   Error   Units
testJMH.JMHSample_20_Annotations.measure  thrpt    5  36.705 ± 7.940  ops/us
```
