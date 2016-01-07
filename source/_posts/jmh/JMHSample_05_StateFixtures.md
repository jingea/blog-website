category: jmh
date: 2015-01-04
title: 05_StateFixtures 
---
```java
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
public class JMHSample_05_StateFixtures {

	double x;

    /*
     * 由于@State对象会存在于整个benchmark的生命周期之中, 因此JMH提供了一些在生命周期中的某个阶段执行一些特殊的函数.
     * 这些函数称为Fixture methods, 注意这些函数只在@State对象中有用, 如果用于非@State对象则会编译失败.
     * 这些函数和JUnit中的@Before等等很像
     *
     * 还有一点需要说明的是这些对象只会由调用了含有@State对象的benchmark函数的线程执行调用. 这意味着这些函数是在
     * thread-local上下文中执行, 你不必使用synchronization进行并发保护
     */

	// 由于我们使用的是Scope.Thread, 因此每当线程第一次调用含有@State对象的基准函数时都会调用prepare()函数
	@Setup
	public void prepare() {
		x = Math.PI;
	}

	// 由于我们使用的是Scope.Thread, 因此每当线程调用完含有@State对象的基准函数时都会调用prepare()函数
	@TearDown
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
				.include(JMHSample_05_StateFixtures.class.getSimpleName())
				.warmupIterations(5)
				.measurementIterations(5)
				.forks(1)
				.jvmArgs("-ea")
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
# Benchmark mode: Throughput, ops/time
# Benchmark: JMH.JMHSample_05_StateFixtures.measureRight

# Run progress: 0.00% complete, ETA 00:00:20
# Fork: 1 of 1
# Warmup Iteration   1: 395892824.608 ops/s
# Warmup Iteration   2: 390597753.910 ops/s
# Warmup Iteration   3: 391909791.701 ops/s
# Warmup Iteration   4: 390345655.260 ops/s
# Warmup Iteration   5: 397800824.768 ops/s
Iteration   1: 399438984.759 ops/s
Iteration   2: 393471030.370 ops/s
Iteration   3: 397809527.925 ops/s
Iteration   4: 395613641.908 ops/s
Iteration   5: 393314885.110 ops/s


Result "measureRight":
  395929614.015 ±(99.9%) 10337928.122 ops/s [Average]
  (min, avg, max) = (393314885.110, 395929614.015, 399438984.759), stdev = 2684727.104
  CI (99.9%): [385591685.893, 406267542.136] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: JMH.JMHSample_05_StateFixtures.measureWrong

# Run progress: 50.00% complete, ETA 00:00:11
# Fork: 1 of 1
# Warmup Iteration   1: 3066015172.981 ops/s
# Warmup Iteration   2: 3143979330.555 ops/s
# Warmup Iteration   3: 3078037603.045 ops/s
# Warmup Iteration   4: 2944032954.636 ops/s
# Warmup Iteration   5: 3062399303.457 ops/s
Iteration   1: 3020901214.195 ops/s
Iteration   2: 3117915404.622 ops/s
Iteration   3: 3186149965.554 ops/s
Iteration   4: 3204341135.065 ops/s
Iteration   5: <failure>

java.lang.AssertionError: Nothing changed?
	at JMH.JMHSample_05_StateFixtures.check(JMHSample_05_StateFixtures.java:35)
	at JMH.generated.JMHSample_05_StateFixtures_measureWrong_jmhTest.measureWrong_Throughput(JMHSample_05_StateFixtures_measureWrong_jmhTest.java:82)
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




Result "measureWrong":
  3132326929.859 ±(99.9%) 536855433.895 ops/s [Average]
  (min, avg, max) = (3020901214.195, 3132326929.859, 3204341135.065), stdev = 83078972.658
  CI (99.9%): [2595471495.964, 3669182363.754] (assumes normal distribution)


# Run complete. Total time: 00:00:23

Benchmark                                     Mode  Cnt           Score           Error  Units
JMH.JMHSample_05_StateFixtures.measureRight  thrpt    5   395929614.015 ±  10337928.122  ops/s
JMH.JMHSample_05_StateFixtures.measureWrong  thrpt    4  3132326929.859 ± 536855433.895  ops/s
```
我们可以看出当Thread执行完之后, check的时候就发生了异常