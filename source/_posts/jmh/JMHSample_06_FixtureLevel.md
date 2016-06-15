category: JMH
date: 2016-01-04
title: 运行级别
---
## FixtureLevel
在演示这个示例之前我们先说一下`@State`里Fixture methods的运行级别含义
* `Level.Trial`: 整个benchmark, 其含义是iteration的序列集合. 在整个benchmark之前或者之后运行
* `Level.Iteration`: benchmark里单个iteration,包含统计内调用方法的集合.在每个benchmark iteration 之前或者之后运行
* `Level.Invocation`:　单个benchmark方法的调用.在每个 benchmark method 调用之前或者之后运行

fixture methods 所消耗的事件并不会统计在最终结果中, 因此我们可以在这些方法中做一些耗时操作
```java
@State(Scope.Thread)
public class JMHSample_06_FixtureLevel {

	double x;

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

## FixtureLevelInvocation
Fixtures含有不同的运行等级. Level.Invocation通常可以帮我们在基准测试方法之前执行一些特殊的操作, 这些操作并不会被统计在测量结果中. 但是需要注意的是生成的时间戳和同步代码块会对测量的结果产生偏移
```java
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.*;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class JMHSample_07_FixtureLevelInvocation {
    /*
     * 我们在整个benchmark过程中保持同一个NormalState对象, 然后分别在benchmark刚开始启动和
     * benchmark结束时创建和销毁ExecutorService对象
     */
	@State(Scope.Benchmark)
	public static class NormalState {
		ExecutorService service;

		// 在整个benchmark开始进行初始化
		@Setup(Level.Trial)
		public void up() {
			service = Executors.newCachedThreadPool();
		}

		// 在整个benchmark结束后, 将ExecutorService关闭掉
		@TearDown(Level.Trial)
		public void down() {
			service.shutdown();
		}

	}

	public static class LaggingState extends NormalState {
		public static final int SLEEP_TIME = Integer.getInteger("sleepTime", 10);

		// 在benchmark过程中, 没执行一次benchmark方法调用, 都会首先执行一下这个方法
		@Setup(Level.Invocation)
		public void lag() throws InterruptedException {
			TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public double measureHot(NormalState e, final Scratch s) throws ExecutionException, InterruptedException {
		return e.service.submit(new Task(s)).get();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public double measureCold(LaggingState e, final Scratch s) throws ExecutionException, InterruptedException {
		return e.service.submit(new Task(s)).get();
	}

	@State(Scope.Thread)
	public static class Scratch {
		private double p;
		public double doWork() {
			p = Math.log(p);
			return p;
		}
	}

	public static class Task implements Callable<Double> {
		private Scratch s;

		public Task(Scratch s) {
			this.s = s;
		}

		@Override
		public Double call() {
			return s.doWork();
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JMHSample_07_FixtureLevelInvocation.class.getSimpleName())
				.warmupIterations(3)
				.measurementIterations(3)
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}
```
执行结果
```java
# Warmup: 3 iterations, 1 s each
# Measurement: 3 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_07_FixtureLevelInvocation.measureCold

# Run progress: 0.00% complete, ETA 00:00:12
# Fork: 1 of 1
# Warmup Iteration   1: 110.941 us/op
# Warmup Iteration   2: 91.016 us/op
# Warmup Iteration   3: 94.717 us/op
Iteration   1: 87.367 us/op
Iteration   2: 70.264 us/op
Iteration   3: 74.239 us/op


Result "measureCold":
  77.290 ±(99.9%) 163.284 us/op [Average]
  (min, avg, max) = (70.264, 77.290, 87.367), stdev = 8.950
  CI (99.9%): [≈ 0, 240.574] (assumes normal distribution)


# Warmup: 3 iterations, 1 s each
# Measurement: 3 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_07_FixtureLevelInvocation.measureHot

# Run progress: 50.00% complete, ETA 00:00:06
# Fork: 1 of 1
# Warmup Iteration   1: 5.642 us/op
# Warmup Iteration   2: 5.384 us/op
# Warmup Iteration   3: 5.234 us/op
Iteration   1: 5.187 us/op
Iteration   2: 5.178 us/op
Iteration   3: 5.536 us/op


Result "measureHot":
  5.300 ±(99.9%) 3.723 us/op [Average]
  (min, avg, max) = (5.178, 5.300, 5.536), stdev = 0.204
  CI (99.9%): [1.578, 9.023] (assumes normal distribution)


# Run complete. Total time: 00:00:13

Benchmark                                            Mode  Cnt   Score     Error  Units
JMH.JMHSample_07_FixtureLevelInvocation.measureCold  avgt    3  77.290 ± 163.284  us/op
JMH.JMHSample_07_FixtureLevelInvocation.measureHot   avgt    3   5.300 ±   3.723  us/op
```
