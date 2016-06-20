category: JMH
date: 2016-06-
title:
---


In some weird cases you need to get the separate throughput/time metrics for the benchmarked code depending on the outcome of the current code. Trying to accommodate the cases like this, JMH optionally provides the special annotation which treats @State objects
as the object bearing user counters. See @AuxCounters javadoc for the limitations.

```java
package testJMH;

import org.openjdk.jmh.annotations.AuxCounters;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class JMHSample_23_AuxCounters {

    @AuxCounters
    @State(Scope.Thread)
    public static class AdditionalCounters {
        public int case1, case2;

        @Setup(Level.Iteration)
        public void clean() {
            case1 = case2 = 0;
        }

        public int total() {
            return case1 + case2;
        }
    }

    /*
     * This code measures the "throughput" in two parts of the branch.
     * The @AuxCounters state above holds the counters which we increment
     * ourselves, and then let JMH to use their values in the performance
     * calculations. Note how we reset the counters on each iteration.
     */

    @Benchmark
    public void measure(AdditionalCounters counters) {
        if (Math.random() < 0.1) {
            counters.case1++;
        } else {
            counters.case2++;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_23_AuxCounters.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}


```
执行结果
```java


```
