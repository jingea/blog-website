category: JMH
date: 2015-12-28
title: 测试模式-吞吐量,平均执行时间,采样
---
当benchmark编译过程中, JMH会为此生成大量的额外代码. JMH可以在多种不同的模式下进行基准测试.

## 吞吐量
```java
public class JMHSample_02_BenchmarkModes {
	/*
     * Mode.Throughput, 统计一段时间内可持续调用基准测试方法的次数.
     */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.SECONDS)
	public void measureThroughput() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(100);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JMHSample_02_BenchmarkModes.class.getSimpleName())
				.warmupIterations(3)	// 热身3次
				.measurementIterations(4)	// 执行4次
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}
```
结果为
```java
# Run progress: 0.00% complete, ETA 00:00:07
# Fork: 1 of 1
# Warmup Iteration   1: 10.007 ops/s
# Warmup Iteration   2: 10.006 ops/s
# Warmup Iteration   3: 10.004 ops/s
Iteration   1: 9.987 ops/s
Iteration   2: 10.005 ops/s
Iteration   3: 10.002 ops/s
Iteration   4: 9.999 ops/s


Result "measureThroughput":
  9.999 ±(99.9%) 0.051 ops/s [Average]
  (min, avg, max) = (9.987, 9.999, 10.005), stdev = 0.008
  CI (99.9%): [9.947, 10.050] (assumes normal distribution)


# Run complete. Total time: 00:00:07

Benchmark                                           Mode  Cnt  Score   Error  Units
JMH.JMHSample_02_BenchmarkModes.measureThroughput  thrpt    4  9.999 ± 0.051  ops/s
```

从上面的结果中我们可以看到
* Benchmark：执行的基准测试为`JMH.JMHSample_02_BenchmarkModes.measureThroughput`
* Mode：执行模式为thrpt, 即Throughput
* Cnt：执行次数为4
* Score： 平均得分为9.999
* Error：差值为0.051
* Units：执行单元为ops/s

## 平均执行时间
```java
/*
 * Mode.AverageTime 测试每个操作的平均执行时间.
 */
@Benchmark
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public void measureAvgTime() throws InterruptedException {
	TimeUnit.MILLISECONDS.sleep(100);
}

public static void main(String[] args) throws RunnerException {
	Options opt = new OptionsBuilder()
			.include(JMHSample_02_BenchmarkModes.class.getSimpleName())
			.warmupIterations(3)	// 热身3次
			.measurementIterations(4)	// 执行4次
			.forks(1)
			.build();

	new Runner(opt).run();
}
```
执行结果为
```java
# Run progress: 0.00% complete, ETA 00:00:07
# Fork: 1 of 1
# Warmup Iteration   1: 99925.584 us/op
# Warmup Iteration   2: 99961.273 us/op
# Warmup Iteration   3: 100001.479 us/op
Iteration   1: 99958.320 us/op
Iteration   2: 99991.971 us/op
Iteration   3: 99951.680 us/op
Iteration   4: 100001.128 us/op


Result "measureAvgTime":
  99975.775 ±(99.9%) 157.859 us/op [Average]
  (min, avg, max) = (99951.680, 99975.775, 100001.128), stdev = 24.429
  CI (99.9%): [99817.916, 100133.634] (assumes normal distribution)


# Run complete. Total time: 00:00:07

Benchmark                                       Mode  Cnt      Score     Error  Units
JMH.JMHSample_02_BenchmarkModes.measureAvgTime  avgt    4  99975.775 ± 157.859  us/op
```
在这个测试中我们要注意的是`us`和`MICROSECONDS`都是同一个含义, 都是微秒. 每个操作的平均值为100000微妙左右,也就是100毫秒左右,而我们的操作是sleep 100毫秒,和我们的预期结果相近

## 采样
```java
/*
 * Mode.SampleTime 会对执行时间进行采样.
 *
 * JMH还会对采样自动调节频率, 如果方法足够长的话, 我们会得到所有的采样
 */
@Benchmark
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public void measureSamples() throws InterruptedException {
	TimeUnit.MILLISECONDS.sleep(100);
}

public static void main(String[] args) throws RunnerException {
	Options opt = new OptionsBuilder()
			.include(JMHSample_02_BenchmarkModes.class.getSimpleName())
			.warmupIterations(3)	// 热身3次
			.measurementIterations(4)	// 执行4次
			.forks(1)
			.build();

	new Runner(opt).run();
}
```
执行结果
```java
# Warmup: 3 iterations, 1 s each
# Measurement: 4 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Sampling time
# Benchmark: JMH.JMHSample_02_BenchmarkModes.measureSamples

# Run progress: 0.00% complete, ETA 00:00:07
# Fork: 1 of 1
# Warmup Iteration   1: n = 10, mean = 100 ms/op, p{0.00, 0.50, 0.90, 0.95, 0.99, 0.999, 0.9999, 1.00} = 99, 100, 100, 100, 100, 100, 100, 100 ms/op
# Warmup Iteration   2: n = 10, mean = 100 ms/op, p{0.00, 0.50, 0.90, 0.95, 0.99, 0.999, 0.9999, 1.00} = 100, 100, 100, 100, 100, 100, 100, 100 ms/op
# Warmup Iteration   3: n = 10, mean = 100 ms/op, p{0.00, 0.50, 0.90, 0.95, 0.99, 0.999, 0.9999, 1.00} = 99, 100, 100, 100, 100, 100, 100, 100 ms/op
Iteration   1: n = 11, mean = 100 ms/op, p{0.00, 0.50, 0.90, 0.95, 0.99, 0.999, 0.9999, 1.00} = 100, 100, 100, 100, 100, 100, 100, 100 ms/op
Iteration   2: n = 10, mean = 100 ms/op, p{0.00, 0.50, 0.90, 0.95, 0.99, 0.999, 0.9999, 1.00} = 100, 100, 100, 100, 100, 100, 100, 100 ms/op
Iteration   3: n = 10, mean = 100 ms/op, p{0.00, 0.50, 0.90, 0.95, 0.99, 0.999, 0.9999, 1.00} = 99, 100, 100, 100, 100, 100, 100, 100 ms/op
Iteration   4: n = 10, mean = 100 ms/op, p{0.00, 0.50, 0.90, 0.95, 0.99, 0.999, 0.9999, 1.00} = 99, 100, 100, 100, 100, 100, 100, 100 ms/op


Result "measureSamples":
  99.886 ±(99.9%) 0.088 ms/op [Average]
  (min, avg, max) = (99.222, 99.886, 100.008), stdev = 0.159
  CI (99.9%): [99.798, 99.975] (assumes normal distribution)
  Samples, N = 41
        mean =     99.886 ±(99.9%) 0.088 ms/op
         min =     99.222 ms/op
  p( 0.0000) =     99.222 ms/op
  p(50.0000) =     99.877 ms/op
  p(90.0000) =    100.008 ms/op
  p(95.0000) =    100.008 ms/op
  p(99.0000) =    100.008 ms/op
  p(99.9000) =    100.008 ms/op
  p(99.9900) =    100.008 ms/op
  p(99.9990) =    100.008 ms/op
  p(99.9999) =    100.008 ms/op
         max =    100.008 ms/op


# Run complete. Total time: 00:00:07

Benchmark                                         Mode  Cnt   Score   Error  Units
JMH.JMHSample_02_BenchmarkModes.measureSamples  sample   41  99.886 ± 0.088  ms/op
```

## 单次执行
```java
/*
 * Mode.SingleShotTime 测试单个方法的调用时间. 我们只会调用一次基准方法. 这个模式非常适用于你不想要持续执行基准测试
 * 的冷启动测试
 */
@Benchmark
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public void measureSingleShot() throws InterruptedException {
	TimeUnit.MILLISECONDS.sleep(100);
}

public static void main(String[] args) throws RunnerException {
	Options opt = new OptionsBuilder()
			.include(JMHSample_02_BenchmarkModes.class.getSimpleName())
			.warmupIterations(3)	// 热身3次
			.measurementIterations(4)	// 执行4次
			.forks(1)
			.build();

	new Runner(opt).run();
}
```
执行结果
```java
# Warmup: 3 iterations, single-shot each
# Measurement: 4 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: JMH.JMHSample_02_BenchmarkModes.measureSingleShot

# Run progress: 0.00% complete, ETA 00:00:00
# Fork: 1 of 1
# Warmup Iteration   1: 99620.910 us/op
# Warmup Iteration   2: 99072.153 us/op
# Warmup Iteration   3: 99502.285 us/op
Iteration   1: 99269.561 us/op
Iteration   2: 99170.555 us/op
Iteration   3: 99598.574 us/op
Iteration   4: 99304.877 us/op


Result "measureSingleShot":
  99335.892 ±(99.9%) 1189.778 us/op [Average]
  (min, avg, max) = (99170.555, 99335.892, 99598.574), stdev = 184.119
  CI (99.9%): [98146.114, 100525.670] (assumes normal distribution)
  Samples, N = 4
        mean =  99335.892 ±(99.9%) 1189.778 us/op
         min =  99170.555 us/op
  p( 0.0000) =  99170.555 us/op
  p(50.0000) =  99287.219 us/op
  p(90.0000) =  99598.574 us/op
  p(95.0000) =  99598.574 us/op
  p(99.0000) =  99598.574 us/op
  p(99.9000) =  99598.574 us/op
  p(99.9900) =  99598.574 us/op
  p(99.9990) =  99598.574 us/op
  p(99.9999) =  99598.574 us/op
         max =  99598.574 us/op


# Run complete. Total time: 00:00:01

Benchmark                                          Mode  Cnt      Score      Error  Units
JMH.JMHSample_02_BenchmarkModes.measureSingleShot    ss    4  99335.892 ± 1189.778  us/op
```

## 组合模式
我们还可以将以上模式组合起来
```java
@Benchmark
@BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public void measureMultiple() throws InterruptedException {
	TimeUnit.MILLISECONDS.sleep(100);
}
```

## 全部依次执行

```java
@Benchmark
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public void measureAll() throws InterruptedException {
	TimeUnit.MILLISECONDS.sleep(100);
}
```
