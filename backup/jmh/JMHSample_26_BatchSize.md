category: JMH
date: 2016-06-
title:
---

Sometimes you need to evaluate operation which doesn't have  the steady state. The cost of a benchmarked operation may significantly vary from invocation to invocation.

In this case, using the timed measurements is not a good idea, and the only acceptable benchmark mode is a single shot. On the other hand, the operation may be too small for reliable single shot measurement.

We can use "batch size" parameter to describe the number of benchmark calls to do per one invocation without looping the method manually and protect from problems described in JMHSample_11_Loops.

```java
package testJMH;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.LinkedList;
import java.util.List;

@State(Scope.Thread)
public class JMHSample_26_BatchSize {
    /*
     * Suppose we want to measure insertion in the middle of the list.
     */

    List<String> list = new LinkedList<String>();

    @Benchmark
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 1)
    @BenchmarkMode(Mode.AverageTime)
    public List<String> measureWrong_1() {
        list.add(list.size() / 2, "something");
        return list;
    }

    @Benchmark
    @Warmup(iterations = 5, time = 5)
    @Measurement(iterations = 5, time = 5)
    @BenchmarkMode(Mode.AverageTime)
    public List<String> measureWrong_5() {
        list.add(list.size() / 2, "something");
        return list;
    }

    /*
     * This is what you do with JMH.
     */
    @Benchmark
    @Warmup(iterations = 5, batchSize = 5000)
    @Measurement(iterations = 5, batchSize = 5000)
    @BenchmarkMode(Mode.SingleShotTime)
    public List<String> measureRight() {
        list.add(list.size() / 2, "something");
        return list;
    }

    @Setup(Level.Iteration)
    public void setup(){
        list.clear();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_26_BatchSize.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
```
执行结果
```java


```
