category: jmh
date: 2015-01-05
title: 08_DeadCode  
---
下面的benchmark中有许多Dead-Code Elimination, 编译器能够发现那些冗余计算并且消除他们. 但是如果被消除的部分是我们的基准测试部分, 则会引发问题.幸运的的是, JMH提供了一些基础服务来解决这些问题:带有返回结果的基准测试会强制JMH不进行Dead-Code Elimination
```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_08_DeadCode {

	private double x = Math.PI;

	@Benchmark
	public void baseline() {
		// 什么都不做，我们将其作为这次基准测试的基准线
	}

	@Benchmark
	public void measureWrong() {
		// 从结果中我们会观察出, 这个基准测试会进行优化
		Math.log(x);
	}

	@Benchmark
	public double measureRight() {
		// This is correct: the result is being used.
		return Math.log(x);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JMHSample_08_DeadCode.class.getSimpleName())
				.warmupIterations(5)
				.measurementIterations(5)
				.forks(1)
				.build();

		new Runner(opt).run();
	}

}

```
运行结果为
```
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_08_DeadCode.measureWrong

# Run progress: 66.67% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 0.281 ns/op
# Warmup Iteration   2: 0.281 ns/op
# Warmup Iteration   3: 0.278 ns/op
# Warmup Iteration   4: 0.280 ns/op
# Warmup Iteration   5: 0.278 ns/op
Iteration   1: 0.278 ns/op
Iteration   2: 0.278 ns/op
Iteration   3: 0.278 ns/op
Iteration   4: 0.277 ns/op
Iteration   5: 0.278 ns/op


Result "measureWrong":
  0.278 ±(99.9%) 0.002 ns/op [Average]
  (min, avg, max) = (0.277, 0.278, 0.278), stdev = 0.001
  CI (99.9%): [0.276, 0.280] (assumes normal distribution)


# Run complete. Total time: 00:00:31

Benchmark                               Mode  Cnt   Score   Error  Units
JMH.JMHSample_08_DeadCode.baseline      avgt    5   0.279 ± 0.004  ns/op
JMH.JMHSample_08_DeadCode.measureRight  avgt    5  21.067 ± 0.288  ns/op
JMH.JMHSample_08_DeadCode.measureWrong  avgt    5   0.278 ± 0.002  ns/op
```
我们可以看到baseline和measureWrong的测试结果是相近的