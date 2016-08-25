category: Java
date: 2016-06-14
title: ReflectASM 性能测试
---
我们采用JMH测试ReflectASM 官网给出的几个示例的吞吐量.

我们的测试代码为
```java
package testASM;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class TestRelectASM {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestRelectASM.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void measureMethodAccess() {
        SomeClass someObject = new SomeClass();
        MethodAccess access = MethodAccess.get(SomeClass.class);
        access.invoke(someObject, "setName", "Awesome McLovin");
        String name = (String)access.invoke(someObject, "getName");
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void measureFieldAccess() {
        SomeClass someObject = new SomeClass();
        FieldAccess access = FieldAccess.get(SomeClass.class);
        access.set(someObject, "age", 18);
        Integer age = (Integer)access.get(someObject, "age");
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void measureConstructorAccess() {
        ConstructorAccess<SomeClass> access = ConstructorAccess.get(SomeClass.class);
        SomeClass someObject = access.newInstance();
    }
}

class SomeClass {
    public int age;
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
```
测试结果为
```bash
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: testASM.TestRelectASM.measureConstructorAccess

# Run progress: 0.00% complete, ETA 00:00:30
# Fork: 1 of 1
# Warmup Iteration   1: 146112.555 ops/s
# Warmup Iteration   2: 399173.011 ops/s
# Warmup Iteration   3: 452136.048 ops/s
# Warmup Iteration   4: 450102.353 ops/s
# Warmup Iteration   5: 446112.781 ops/s
Iteration   1: 454817.511 ops/s
Iteration   2: 444272.121 ops/s
Iteration   3: 454354.750 ops/s
Iteration   4: 449983.056 ops/s
Iteration   5: 446149.202 ops/s


Result "measureConstructorAccess":
  449915.328 ±(99.9%) 18242.255 ops/s [Average]
  (min, avg, max) = (444272.121, 449915.328, 454817.511), stdev = 4737.456
  CI (99.9%): [431673.074, 468157.583] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: testASM.TestRelectASM.measureFieldAccess

# Run progress: 33.33% complete, ETA 00:00:21
# Fork: 1 of 1
# Warmup Iteration   1: 483272.367 ops/s
# Warmup Iteration   2: 638769.340 ops/s
# Warmup Iteration   3: 643087.589 ops/s
# Warmup Iteration   4: 654921.986 ops/s
# Warmup Iteration   5: 646076.594 ops/s
Iteration   1: 657034.718 ops/s
Iteration   2: 651774.971 ops/s
Iteration   3: 648118.383 ops/s
Iteration   4: 647813.662 ops/s
Iteration   5: 654691.279 ops/s


Result "measureFieldAccess":
  651886.603 ±(99.9%) 15542.738 ops/s [Average]
  (min, avg, max) = (647813.662, 651886.603, 657034.718), stdev = 4036.400
  CI (99.9%): [636343.865, 667429.341] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: testASM.TestRelectASM.measureMethodAccess

# Run progress: 66.67% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 432894.585 ops/s
# Warmup Iteration   2: 621680.095 ops/s
# Warmup Iteration   3: 618783.228 ops/s
# Warmup Iteration   4: 624738.576 ops/s
# Warmup Iteration   5: 611762.568 ops/s
Iteration   1: 625825.844 ops/s
Iteration   2: 621933.920 ops/s
Iteration   3: 644088.779 ops/s
Iteration   4: 644729.889 ops/s
Iteration   5: 615383.465 ops/s


Result "measureMethodAccess":
  630392.379 ±(99.9%) 51331.476 ops/s [Average]
  (min, avg, max) = (615383.465, 630392.379, 644729.889), stdev = 13330.621
  CI (99.9%): [579060.904, 681723.855] (assumes normal distribution)


# Run complete. Total time: 00:00:31

Benchmark                                        Mode  Cnt       Score       Error  Units
testASM.TestRelectASM.measureConstructorAccess  thrpt    5  449915.328 ± 18242.255  ops/s
testASM.TestRelectASM.measureFieldAccess        thrpt    5  651886.603 ± 15542.738  ops/s
testASM.TestRelectASM.measureMethodAccess       thrpt    5  630392.379 ± 51331.476  ops/s
```

接下来我们看一下JDK原生反射的性能
```java
package testASM;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class TestJDKReflect {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestRelectASM.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void measureMethodAccess() {
        SomeClass someObject = new SomeClass();
        try {
            Method setNameMethod = SomeClass.class.getMethod("setName");
            setNameMethod.invoke(someObject, "Awesome McLovin");
            Method getNameMethod = SomeClass.class.getMethod("getName");
            String name = (String)getNameMethod.invoke(someObject);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void measureFieldAccess() {
        SomeClass someObject = new SomeClass();
        try {
            Field ageField = SomeClass.class.getField("age");
            ageField.set(someObject, 18);
            Integer age = (Integer)ageField.get(someObject);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void measureConstructorAccess() {
        try {
            Constructor<SomeClass> con = SomeClass.class.getConstructor();
            SomeClass newInstance = con.newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
```
测试结果为
```bash
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: testASM.TestRelectASM.measureConstructorAccess

# Run progress: 0.00% complete, ETA 00:00:30
# Fork: 1 of 1
# Warmup Iteration   1: 272071.065 ops/s
# Warmup Iteration   2: 589448.060 ops/s
# Warmup Iteration   3: 583306.327 ops/s
# Warmup Iteration   4: 559099.151 ops/s
# Warmup Iteration   5: 546514.262 ops/s
Iteration   1: 603711.245 ops/s
Iteration   2: 582218.187 ops/s
Iteration   3: 561487.624 ops/s
Iteration   4: 582646.831 ops/s
Iteration   5: 612259.454 ops/s


Result "measureConstructorAccess":
  588464.668 ±(99.9%) 76995.468 ops/s [Average]
  (min, avg, max) = (561487.624, 588464.668, 612259.454), stdev = 19995.478
  CI (99.9%): [511469.200, 665460.136] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: testASM.TestRelectASM.measureFieldAccess

# Run progress: 33.33% complete, ETA 00:00:21
# Fork: 1 of 1
# Warmup Iteration   1: 407799.239 ops/s
# Warmup Iteration   2: 599419.421 ops/s
# Warmup Iteration   3: 624838.592 ops/s
# Warmup Iteration   4: 636293.822 ops/s
# Warmup Iteration   5: 623880.225 ops/s
Iteration   1: 600361.584 ops/s
Iteration   2: 642029.016 ops/s
Iteration   3: 613483.428 ops/s
Iteration   4: 645823.284 ops/s
Iteration   5: 632346.098 ops/s


Result "measureFieldAccess":
  626808.682 ±(99.9%) 74589.468 ops/s [Average]
  (min, avg, max) = (600361.584, 626808.682, 645823.284), stdev = 19370.648
  CI (99.9%): [552219.214, 701398.150] (assumes normal distribution)


# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: testASM.TestRelectASM.measureMethodAccess

# Run progress: 66.67% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 397028.204 ops/s
# Warmup Iteration   2: 602683.986 ops/s
# Warmup Iteration   3: 576226.708 ops/s
# Warmup Iteration   4: 602674.109 ops/s
# Warmup Iteration   5: 591631.635 ops/s
Iteration   1: 607990.169 ops/s
Iteration   2: 586306.494 ops/s
Iteration   3: 606049.775 ops/s
Iteration   4: 583441.620 ops/s
Iteration   5: 577363.527 ops/s


Result "measureMethodAccess":
  592230.317 ±(99.9%) 53519.265 ops/s [Average]
  (min, avg, max) = (577363.527, 592230.317, 607990.169), stdev = 13898.783
  CI (99.9%): [538711.052, 645749.582] (assumes normal distribution)


# Run complete. Total time: 00:00:31

Benchmark                                        Mode  Cnt       Score       Error  Units
testASM.TestRelectASM.measureConstructorAccess  thrpt    5  588464.668 ± 76995.468  ops/s
testASM.TestRelectASM.measureFieldAccess        thrpt    5  626808.682 ± 74589.468  ops/s
testASM.TestRelectASM.measureMethodAccess       thrpt    5  592230.317 ± 53519.265  ops/s

```
