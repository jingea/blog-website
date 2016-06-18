category: JMH
date: 2016-06-17
title:
---
JVM众所周知的一个特性是, 它为我们提供了各种优化配置. 但是这个特性却为基准测试带来了一些影响. 因为不同的测试混合到一起的话就分辨不出, 在不同的情况下
/*
 * JVMs are notoriously good at profile-guided optimizations. This is bad
 * for benchmarks, because different tests can mix their profiles together,
 * and then render the "uniformly bad" code for every test. Forking (running
 * in a separate process) each test can help to evade this issue.
 *
 * JMH will fork the tests by default.
 */

/*
 * Suppose we have this simple counter interface, and two implementations.
 * Even though those are semantically the same, from the JVM standpoint,
 * those are distinct classes.
 */

```java

```
执行结果
```java
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_4_forked_c1

# Run progress: 0.00% complete, ETA 00:00:50
# Fork: 1 of 1
# Warmup Iteration   1: 9.475 ns/op
# Warmup Iteration   2: 7.028 ns/op
# Warmup Iteration   3: 5.814 ns/op
# Warmup Iteration   4: 5.874 ns/op
# Warmup Iteration   5: 5.746 ns/op
Iteration   1: 5.722 ns/op
Iteration   2: 5.845 ns/op
Iteration   3: 5.770 ns/op
Iteration   4: 5.822 ns/op
Iteration   5: 6.258 ns/op


Result "measure_4_forked_c1":
  5.883 ±(99.9%) 0.827 ns/op [Average]
  (min, avg, max) = (5.722, 5.883, 6.258), stdev = 0.215
  CI (99.9%): [5.056, 6.711] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_5_forked_c2

# Run progress: 20.00% complete, ETA 00:00:42
# Fork: 1 of 1
# Warmup Iteration   1: 6.348 ns/op
# Warmup Iteration   2: 6.459 ns/op
# Warmup Iteration   3: 5.880 ns/op
# Warmup Iteration   4: 5.739 ns/op
# Warmup Iteration   5: 5.866 ns/op
Iteration   1: 5.735 ns/op
Iteration   2: 5.838 ns/op
Iteration   3: 5.759 ns/op
Iteration   4: 5.750 ns/op
Iteration   5: 5.763 ns/op


Result "measure_5_forked_c2":
  5.769 ±(99.9%) 0.154 ns/op [Average]
  (min, avg, max) = (5.735, 5.769, 5.838), stdev = 0.040
  CI (99.9%): [5.616, 5.923] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_1_c1

# Run progress: 40.00% complete, ETA 00:00:32
# Fork: N/A, test runs in the existing VM
# Warmup Iteration   1: 3.489 ns/op
# Warmup Iteration   2: 3.473 ns/op
# Warmup Iteration   3: 3.445 ns/op
# Warmup Iteration   4: 3.561 ns/op
# Warmup Iteration   5: 3.439 ns/op
Iteration   1: 3.516 ns/op
Iteration   2: 3.428 ns/op
Iteration   3: 3.536 ns/op
Iteration   4: 3.374 ns/op
Iteration   5: 3.459 ns/op

Result "measure_1_c1":
  3.463 ±(99.9%) 0.253 ns/op [Average]
  (min, avg, max) = (3.374, 3.463, 3.536), stdev = 0.066
  CI (99.9%): [3.210, 3.715] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_2_c2

# Run progress: 60.00% complete, ETA 00:00:21
# Fork: N/A, test runs in the existing VM
# Warmup Iteration   1: 24.308 ns/op
# Warmup Iteration   2: 23.863 ns/op
# Warmup Iteration   3: 24.380 ns/op
# Warmup Iteration   4: 24.461 ns/op
# Warmup Iteration   5: 24.427 ns/op
Iteration   1: 24.982 ns/op
Iteration   2: 24.271 ns/op
Iteration   3: 24.236 ns/op
Iteration   4: 24.675 ns/op
Iteration   5: 24.530 ns/op

Result "measure_2_c2":
  24.539 ±(99.9%) 1.184 ns/op [Average]
  (min, avg, max) = (24.236, 24.539, 24.982), stdev = 0.308
  CI (99.9%): [23.355, 25.723] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: testJMH.JMHSample_12_Forking.measure_3_c1_again

# Run progress: 80.00% complete, ETA 00:00:10
# Fork: N/A, test runs in the existing VM
# Warmup Iteration   1: 23.828 ns/op
# Warmup Iteration   2: 24.215 ns/op
# Warmup Iteration   3: 23.852 ns/op
# Warmup Iteration   4: 24.104 ns/op
# Warmup Iteration   5: 24.176 ns/op
Iteration   1: 24.041 ns/op
Iteration   2: 23.980 ns/op
Iteration   3: 24.379 ns/op
Iteration   4: 24.386 ns/op
Iteration   5: 24.370 ns/op

Result "measure_3_c1_again":
  24.231 ±(99.9%) 0.780 ns/op [Average]
  (min, avg, max) = (23.980, 24.231, 24.386), stdev = 0.203
  CI (99.9%): [23.451, 25.011] (assumes normal distribution)


# Run complete. Total time: 00:00:51

Benchmark                                         Mode  Cnt   Score   Error  Units
testJMH.JMHSample_12_Forking.measure_1_c1         avgt    5   3.463 ± 0.253  ns/op
testJMH.JMHSample_12_Forking.measure_2_c2         avgt    5  24.539 ± 1.184  ns/op
testJMH.JMHSample_12_Forking.measure_3_c1_again   avgt    5  24.231 ± 0.780  ns/op
testJMH.JMHSample_12_Forking.measure_4_forked_c1  avgt    5   5.883 ± 0.827  ns/op
testJMH.JMHSample_12_Forking.measure_5_forked_c2  avgt    5   5.769 ± 0.154  ns/op
```
