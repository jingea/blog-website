category: JMH
date: 2015-12-28
title: 01_HelloWorld
---
```java
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JMHSample_01_HelloWorld {

    /*
     * 这是我们的第一个基准函数
     *
     * JMH通过如下流程工作: 用户通过@Benchmark注解基准函数, 然后JMH在执行基准测试时会自动生成一些代码.
     * @Benchmark注解的详细信息可以参考其Javadoc,但是我们可以简单地将其想象成有效负载测试.
     *
     * 我们可以在同一个类中定义多个benchmark函数, 函数名都无所谓, 但是我们应该尽可能的起一个有意义的名字.
     *
     * 需要注意的是, 如果函数没有执行完的话, JMH也不会停止运行. 如果函数中抛出异常, 那JMH同样的会停止运行, 执行下一个benchmark
     *
     * 尽管我们实例中并没有测试任何功能, 但是这个例子却能展示出当你进行基准测试时, 基础设施的基准线.
     */

	@Benchmark
	public void wellHelloThere() {
		// 我们故意将该函数留空
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JMHSample_01_HelloWorld.class.getSimpleName())
				.forks(1)
				.build();

		new Runner(opt).run();
	}

}
```

执行结果为
```java
# JMH 1.11.2 (released 60 days ago)
# Warmup: 20 iterations, 1 s each
# Measurement: 20 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: JMH.JMHSample_01_HelloWorld.wellHelloThere

# Run progress: 0.00% complete, ETA 00:00:40
# Fork: 1 of 1
# Warmup Iteration   1: 3385522466.026 ops/s
# Warmup Iteration   2: 3522171101.974 ops/s
# Warmup Iteration   3: 3565836983.445 ops/s
# Warmup Iteration   4: 3515408341.483 ops/s
# Warmup Iteration   5: 3588745518.898 ops/s
# Warmup Iteration   6: 3565087523.829 ops/s
# Warmup Iteration   7: 3562771873.632 ops/s
# Warmup Iteration   8: 3583632296.413 ops/s
# Warmup Iteration   9: 3565611310.891 ops/s
# Warmup Iteration  10: 3590948927.976 ops/s
# Warmup Iteration  11: 3576434147.340 ops/s
# Warmup Iteration  12: 3588304463.970 ops/s
# Warmup Iteration  13: 3581233142.118 ops/s
# Warmup Iteration  14: 3575921198.480 ops/s
# Warmup Iteration  15: 3581761922.835 ops/s
# Warmup Iteration  16: 3589445022.964 ops/s
# Warmup Iteration  17: 3566869657.337 ops/s
# Warmup Iteration  18: 3537779750.878 ops/s
# Warmup Iteration  19: 3568040140.557 ops/s
# Warmup Iteration  20: 3590220221.972 ops/s
Iteration   1: 3583340988.084 ops/s
Iteration   2: 3582273415.857 ops/s
Iteration   3: 3581430663.273 ops/s
Iteration   4: 3573066377.669 ops/s
Iteration   5: 3578876607.022 ops/s
Iteration   6: 3581448410.242 ops/s
Iteration   7: 3566004905.951 ops/s
Iteration   8: 3585318369.276 ops/s
Iteration   9: 3559148782.715 ops/s
Iteration  10: 3545919918.740 ops/s
Iteration  11: 3594271895.253 ops/s
Iteration  12: 3570179893.808 ops/s
Iteration  13: 3566673424.322 ops/s
Iteration  14: 3568946183.344 ops/s
Iteration  15: 3554498386.309 ops/s
Iteration  16: 3586741606.233 ops/s
Iteration  17: 3583433388.045 ops/s
Iteration  18: 3583027708.144 ops/s
Iteration  19: 3569799683.683 ops/s
Iteration  20: 3581887656.086 ops/s


Result "wellHelloThere":
  3574814413.203 ±(99.9%) 10497360.400 ops/s [Average]
  (min, avg, max) = (3545919918.740, 3574814413.203, 3594271895.253), stdev = 12088775.847
  CI (99.9%): [3564317052.803, 3585311773.603] (assumes normal distribution)


# Run complete. Total time: 00:00:40

Benchmark                                    Mode  Cnt           Score          Error  Units
JMH.JMHSample_01_HelloWorld.wellHelloThere  thrpt   20  3574814413.203 ± 10497360.400  ops/s
```
上面的输出依次为
1. JMH执行版本
2. 执行20次热身, 每次执行1秒
3. 测试20次, 每次执行1秒
4. 每次执行的超时时间, 默认是10分钟
5. 执行线程, 我们设置的是1个线程, 且测试是同步执行的
6. 基准模式是每秒执行的吞吐量
7. 下面的执行结果是, 热身执行了20次, 每次执行的吞吐量大概为35亿次
