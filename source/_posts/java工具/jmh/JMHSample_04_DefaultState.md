category: jmh
date: 2016-01-01
title: 04_DefaultState 
---
```java
/*
 * 幸运的是, 在大多数情况下我们只需要一个状态对象, 因此我们可以将@State注解到Benchmark类的自身, 然后
 * 让所有的benchmark函数都可以对其进行访问
 */

@State(Scope.Thread)
public class JMHSample_04_DefaultState {

    double x = Math.PI;

    @Benchmark
    public void measure() {
        x++;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_04_DefaultState.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
```