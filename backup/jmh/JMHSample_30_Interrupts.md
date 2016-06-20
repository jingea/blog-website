category: JMH
date: 2016-06-
title:
---

JMH can also detect when threads are stuck in the benchmarks, and try
to forcefully interrupt the benchmark thread. JMH tries to do that
when it is arguably sure it would not affect the measurement.



In this example, we want to measure the simple performance characteristics
of the ArrayBlockingQueue. Unfortunately, doing that without a harness
support will deadlock one of the threads, because the executions of
take/put are not paired perfectly. Fortunately for us, both methods react
to interrupts well, and therefore we can rely on JMH to terminate the
measurement for us. JMH will notify users about the interrupt actions
nevertheless, so users can see if those interrupts affected the measurement.
JMH will start issuing interrupts after the default or user-specified timeout
had been reached.

This is a variant of org.openjdk.jmh.samples.JMHSample_18_Control, but without
the explicit control objects. This example is suitable for the methods which
react to interrupts gracefully.

```java
package testJMH;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Group)
public class JMHSample_30_Interrupts {

    private BlockingQueue<Integer> q;

    @Setup
    public void setup() {
        q = new ArrayBlockingQueue<Integer>(1);
    }

    @Group("Q")
    @Benchmark
    public Integer take() throws InterruptedException {
        return q.take();
    }

    @Group("Q")
    @Benchmark
    public void put() throws InterruptedException {
        q.put(42);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_30_Interrupts.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .threads(2)
                .forks(5)
                .timeout(TimeValue.seconds(5))
                .build();

        new Runner(opt).run();
    }

}
```
执行结果
```java


```
