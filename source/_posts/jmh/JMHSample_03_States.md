category: JMH
date: 2016-01-01
title: 03_States 
---
在很多种情况下, 在benchmark运行的过程中, 你可能需要维持一些状态, 但是JMH被设计成经常并发的执行benchmark, 因此JMH提供了一些用于保存状态的对象.

我们可以使用`@State`赋予其一个生命周期. 如下例所示, 
```java
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JMHSample_03_States {


    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile double x = Math.PI;
    }

    @State(Scope.Thread)
    public static class ThreadState {
        volatile double x = Math.PI;
    }

    /*
     * Benchmark函数可以引用哪些状态对象, 这些状态对象的值由JMH负责注入. Benchmark函数可以没有状态对象,
     * 也可以有一个或者多个状态对象引用. 我们可以很轻松的构建一个多线程的Benchmark.
     */

    @Benchmark
    public void measureUnshared(ThreadState state) {
        // 所有的Benchmark线程都会调用这个方法,但是由于`ThreadState`作用在了Scope.Thread, 因此每个线程都会有一个自己的本地
        // 状态数据拷贝, 线程间的状态数据并不会进行共享
        state.x++;
    }

    @Benchmark
    public void measureShared(BenchmarkState state) {
        // 所有的benchmark都会调用这个方法, 由于BenchmarkState是作用于Scope.Benchmark, 因此所有的线程都会共享同一个
        // BenchmarkState实例
        state.x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_03_States.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .threads(4)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}

```
有一点非常重要的是state由对其进行访问的benchmark线程实例化.