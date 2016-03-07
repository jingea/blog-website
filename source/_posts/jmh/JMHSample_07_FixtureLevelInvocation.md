category: JMH
date: 2016-01-04
title: 07_FixtureLevelInvocation 
---
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
```
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