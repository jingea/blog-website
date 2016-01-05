category: jmh
date: 2015-01-05
title: 10_ConstantFold 
---
```java
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_10_ConstantFold {

    /*
     * dead-code elimination另一种方式是常量展开.
     *
     * 如果JVM意识到基准测试的结果总是相同的, 那么JVM就会对其进行优化.
     *
     * 避免常量展开的一种方式是在@State对象中使用非final成员, 在进行基准测试计算中,使用这些非final成员进行计算
     */

	private double x = Math.PI;

	private final double wrongX = Math.PI;

	@Benchmark
	public double baseline() {
		// simply return the value, this is a baseline
		return Math.PI;
	}

	@Benchmark
	public double measureWrong_1() {
		// 这个基准会进行优化, 因为JVM会预料到这个代码产生的结果总是相同的
		return Math.log(Math.PI);
	}

	@Benchmark
	public double measureWrong_2() {
		// 这个基准会进行优化, 因为JVM会预料到这个代码产生的结果总是相同的
		return Math.log(wrongX);
	}

	@Benchmark
	public double measureRight() {
		// 因为x是一个变量, 因此每次执行基准测试产生的结果可能都是不相同的
		return Math.log(x);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JMHSample_10_ConstantFold.class.getSimpleName())
				.warmupIterations(5)
				.measurementIterations(5)
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}
```
执行结果
```
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_10_ConstantFold.baseline

# Run progress: 0.00% complete, ETA 00:00:40
# Fork: 1 of 1
# Warmup Iteration   1: 3.483 ns/op
# Warmup Iteration   2: 3.215 ns/op
# Warmup Iteration   3: 2.551 ns/op
# Warmup Iteration   4: 2.561 ns/op
# Warmup Iteration   5: 2.551 ns/op
Iteration   1: 2.569 ns/op
Iteration   2: 2.610 ns/op
Iteration   3: 2.578 ns/op
Iteration   4: 2.570 ns/op
Iteration   5: 2.540 ns/op


Result "baseline":
  2.573 ±(99.9%) 0.097 ns/op [Average]
  (min, avg, max) = (2.540, 2.573, 2.610), stdev = 0.025
  CI (99.9%): [2.476, 2.671] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_10_ConstantFold.measureRight

# Run progress: 25.00% complete, ETA 00:00:31
# Fork: 1 of 1
# Warmup Iteration   1: 21.162 ns/op
# Warmup Iteration   2: 21.094 ns/op
# Warmup Iteration   3: 20.798 ns/op
# Warmup Iteration   4: 21.164 ns/op
# Warmup Iteration   5: 20.823 ns/op
Iteration   1: 20.857 ns/op
Iteration   2: 20.809 ns/op
Iteration   3: 21.032 ns/op
Iteration   4: 20.807 ns/op
Iteration   5: 21.207 ns/op


Result "measureRight":
  20.942 ±(99.9%) 0.671 ns/op [Average]
  (min, avg, max) = (20.807, 20.942, 21.207), stdev = 0.174
  CI (99.9%): [20.271, 21.614] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_10_ConstantFold.measureWrong_1

# Run progress: 50.00% complete, ETA 00:00:21
# Fork: 1 of 1
# Warmup Iteration   1: 3.213 ns/op
# Warmup Iteration   2: 3.204 ns/op
# Warmup Iteration   3: 2.527 ns/op
# Warmup Iteration   4: 2.511 ns/op
# Warmup Iteration   5: 2.518 ns/op
Iteration   1: 2.514 ns/op
Iteration   2: 2.513 ns/op
Iteration   3: 2.517 ns/op
Iteration   4: 2.513 ns/op
Iteration   5: 2.518 ns/op


Result "measureWrong_1":
  2.515 ±(99.9%) 0.010 ns/op [Average]
  (min, avg, max) = (2.513, 2.515, 2.518), stdev = 0.003
  CI (99.9%): [2.505, 2.525] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_10_ConstantFold.measureWrong_2

# Run progress: 75.00% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 3.215 ns/op
# Warmup Iteration   2: 3.201 ns/op
# Warmup Iteration   3: 2.537 ns/op
# Warmup Iteration   4: 2.548 ns/op
# Warmup Iteration   5: 2.542 ns/op
Iteration   1: 2.532 ns/op
Iteration   2: 2.533 ns/op
Iteration   3: 2.533 ns/op
Iteration   4: 2.531 ns/op
Iteration   5: 2.532 ns/op


Result "measureWrong_2":
  2.532 ±(99.9%) 0.004 ns/op [Average]
  (min, avg, max) = (2.531, 2.532, 2.533), stdev = 0.001
  CI (99.9%): [2.528, 2.537] (assumes normal distribution)


# Run complete. Total time: 00:00:41

Benchmark                                     Mode  Cnt   Score   Error  Units
JMH.JMHSample_10_ConstantFold.baseline        avgt    5   2.573 ± 0.097  ns/op
JMH.JMHSample_10_ConstantFold.measureRight    avgt    5  20.942 ± 0.671  ns/op
JMH.JMHSample_10_ConstantFold.measureWrong_1  avgt    5   2.515 ± 0.010  ns/op
JMH.JMHSample_10_ConstantFold.measureWrong_2  avgt    5   2.532 ± 0.004  ns/op
```
我们看到measureWrong_1和measureWrong_2都进行了常量展开, 只有measureRight才每次都进行了计算