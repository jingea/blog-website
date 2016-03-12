category: JMH
date: 2016-01-04
title: 06_FixtureLevel
---
在演示这个示例之前我们先说一下`@State`里Fixture methods的运行级别含义
* Level.Trial: 整个benchmark, 其含义是iteration的序列集合
* Level.Iteration: benchmark里单个iteration,　包含统计内调用方法的集合
* Level.Invocation:　单个benchmark方法的调用

```java
@State(Scope.Thread)
public class JMHSample_06_FixtureLevel {

	double x;

    /* Fixture methods有三种不同的运行级别:
     *
     * Level.Trial: 在整个benchmark之前或者之后运行
     * Level.Iteration: 在每个benchmark iteration 之前或者之后运行
     * Level.Invocation: 在每个 benchmark method 调用之前或者之后运行
     *
     * fixture methods 所消耗的事件并不会统计在最终结果中, 因此我们可以在这些方法中做一些耗时操作
     */

	@TearDown(Level.Iteration)
	public void check() {
		assert x > Math.PI : "Nothing changed?";
	}

	@Benchmark
	public void measureRight() {
		x++;
	}

	@Benchmark
	public void measureWrong() {
		double x = 0;
		x++;
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JMHSample_06_FixtureLevel.class.getSimpleName())
				.warmupIterations(3)
				.measurementIterations(3)
				.forks(1)
				.jvmArgs("-ea")
				.shouldFailOnError(false) // switch to "true" to fail the complete run
				.build();

		new Runner(opt).run();
	}

}
```
我们观察到的结果是
```java
# Warmup: 3 iterations, 1 s each
# Measurement: 3 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: JMH.JMHSample_06_FixtureLevel.measureRight

# Run progress: 0.00% complete, ETA 00:00:12
# Fork: 1 of 1
# Warmup Iteration   1: 399616586.177 ops/s
# Warmup Iteration   2: 399871057.355 ops/s
# Warmup Iteration   3: 400062522.871 ops/s
Iteration   1: 399026547.186 ops/s
Iteration   2: 400226493.878 ops/s
Iteration   3: 398754925.120 ops/s


Result "measureRight":
  399335988.728 ±(99.9%) 14286060.402 ops/s [Average]
  (min, avg, max) = (398754925.120, 399335988.728, 400226493.878), stdev = 783067.177
  CI (99.9%): [385049928.326, 413622049.130] (assumes normal distribution)


# Warmup: 3 iterations, 1 s each
# Measurement: 3 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: JMH.JMHSample_06_FixtureLevel.measureWrong

# Run progress: 50.00% complete, ETA 00:00:06
# Fork: 1 of 1
# Warmup Iteration   1: <failure>

java.lang.AssertionError: Nothing changed?
	at JMH.JMHSample_06_FixtureLevel.check(JMHSample_06_FixtureLevel.java:28)
	at JMH.generated.JMHSample_06_FixtureLevel_measureWrong_jmhTest.measureWrong_Throughput(JMHSample_06_FixtureLevel_measureWrong_jmhTest.java:80)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.openjdk.jmh.runner.BenchmarkHandler$BenchmarkTask.call(BenchmarkHandler.java:430)
	at org.openjdk.jmh.runner.BenchmarkHandler$BenchmarkTask.call(BenchmarkHandler.java:412)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)


# Run complete. Total time: 00:00:08

Benchmark                                    Mode  Cnt          Score          Error  Units
JMH.JMHSample_06_FixtureLevel.measureRight  thrpt    3  399335988.728 ± 14286060.402  ops/s
```
从结果中我们可以看到在测试measureWrong时，刚执行完一个iteration就产生了异常
