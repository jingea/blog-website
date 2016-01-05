category: jmh
date: 2015-01-05
title: 09_Blackholes 
---

```java
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JMHSample_09_Blackholes {

    /*
     * 如果你的benchmark想要返回多个结果, 你可以参考以下俩种方式:
     */

	double x1 = Math.PI;
	double x2 = Math.PI * 2;

	// 基准线, 执行Math.log所产生的消耗
	@Benchmark
	public double baseline() {
		return Math.log(x1);
	}

	// 下面这个基准测试, Math.log(x1)会被优化掉, 只有 Math.log(x2)会进行计算
	@Benchmark
	public double measureWrong() {
		Math.log(x1);
		return Math.log(x2);
	}

	// 返回多个选项A方案: 将多个计算结果进行合并. 这种方案是可接受, 它们并不会对结果产生太大的偏移
	@Benchmark
	public double measureRight_1() {
		return Math.log(x1) + Math.log(x2);
	}

	// 返回多个选项A方案: 显式的使用Blackhole对每个计算进行操作()
	@Benchmark
	public void measureRight_2(Blackhole bh) {
		bh.consume(Math.log(x1));
		bh.consume(Math.log(x2));
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JMHSample_09_Blackholes.class.getSimpleName())
				.warmupIterations(5)
				.measurementIterations(5)
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}
```
计算结果
```
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_09_Blackholes.baseline

# Run progress: 0.00% complete, ETA 00:00:40
# Fork: 1 of 1
# Warmup Iteration   1: 21.217 ns/op
# Warmup Iteration   2: 20.386 ns/op
# Warmup Iteration   3: 20.780 ns/op
# Warmup Iteration   4: 20.764 ns/op
# Warmup Iteration   5: 20.795 ns/op
Iteration   1: 20.748 ns/op
Iteration   2: 21.183 ns/op
Iteration   3: 20.868 ns/op
Iteration   4: 20.828 ns/op
Iteration   5: 20.835 ns/op


Result "baseline":
  20.892 ±(99.9%) 0.648 ns/op [Average]
  (min, avg, max) = (20.748, 20.892, 21.183), stdev = 0.168
  CI (99.9%): [20.245, 21.540] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_09_Blackholes.measureRight_1

# Run progress: 25.00% complete, ETA 00:00:31
# Fork: 1 of 1
# Warmup Iteration   1: 39.457 ns/op
# Warmup Iteration   2: 38.186 ns/op
# Warmup Iteration   3: 38.109 ns/op
# Warmup Iteration   4: 38.053 ns/op
# Warmup Iteration   5: 38.107 ns/op
Iteration   1: 38.232 ns/op
Iteration   2: 38.069 ns/op
Iteration   3: 38.040 ns/op
Iteration   4: 38.070 ns/op
Iteration   5: 38.101 ns/op


Result "measureRight_1":
  38.102 ±(99.9%) 0.292 ns/op [Average]
  (min, avg, max) = (38.040, 38.102, 38.232), stdev = 0.076
  CI (99.9%): [37.811, 38.394] (assumes normal distribution)

  
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_09_Blackholes.measureRight_2

# Run progress: 50.00% complete, ETA 00:00:21
# Fork: 1 of 1
# Warmup Iteration   1: 41.007 ns/op
# Warmup Iteration   2: 40.831 ns/op
# Warmup Iteration   3: 40.163 ns/op
# Warmup Iteration   4: 40.266 ns/op
# Warmup Iteration   5: 40.146 ns/op
Iteration   1: 40.281 ns/op
Iteration   2: 40.250 ns/op
Iteration   3: 40.336 ns/op
Iteration   4: 40.207 ns/op
Iteration   5: 40.336 ns/op


Result "measureRight_2":
  40.282 ±(99.9%) 0.215 ns/op [Average]
  (min, avg, max) = (40.207, 40.282, 40.336), stdev = 0.056
  CI (99.9%): [40.067, 40.497] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: JMH.JMHSample_09_Blackholes.measureWrong

# Run progress: 75.00% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 20.723 ns/op
# Warmup Iteration   2: 20.512 ns/op
# Warmup Iteration   3: 20.907 ns/op
# Warmup Iteration   4: 20.870 ns/op
# Warmup Iteration   5: 20.921 ns/op
Iteration   1: 20.886 ns/op
Iteration   2: 20.900 ns/op
Iteration   3: 20.908 ns/op
Iteration   4: 20.880 ns/op
Iteration   5: 20.820 ns/op


Result "measureWrong":
  20.879 ±(99.9%) 0.133 ns/op [Average]
  (min, avg, max) = (20.820, 20.879, 20.908), stdev = 0.035
  CI (99.9%): [20.746, 21.012] (assumes normal distribution)


# Run complete. Total time: 00:00:42

Benchmark                                   Mode  Cnt   Score   Error  Units
JMH.JMHSample_09_Blackholes.baseline        avgt    5  20.892 ± 0.648  ns/op
JMH.JMHSample_09_Blackholes.measureRight_1  avgt    5  38.102 ± 0.292  ns/op
JMH.JMHSample_09_Blackholes.measureRight_2  avgt    5  40.282 ± 0.215  ns/op
JMH.JMHSample_09_Blackholes.measureWrong    avgt    5  20.879 ± 0.133  ns/op
```
根据我们观察的结果来看measureWrong和baseline确实是经过了优化, measureRight_1和measureRight_2的结果也是相近的