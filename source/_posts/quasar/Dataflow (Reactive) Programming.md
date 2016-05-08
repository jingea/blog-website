category: JavaSE
date: 2015-09-08
title: Dataflow (Reactive) Programming
---


Dataflow, or reactive programming, is a computation described by composing variables whose value may be set (and possibly changed) at any given time, without concern for when these values are set. Quasar provides two dataflow primitives: Val and Var in the co.paralleluniverse.strands.dataflow package.

A Val is a dataflow constant. It can have its value set once, and read multiple times. Attempting to read the value of a Val before it’s been set, will block until a value is set.

Vals can also be used as a simple and effective strand coordination mechanism. Val implements j.u.c.Future.

A Var is a dataflow variable. It can have it’s value set multiple times, and every new value can trigger the re-computation of other Vars. You can set a Var to retain historical values (consult the Javadoc for more information).

Here is a simple example of using Vals and Vars.

Val<Integer> a = new Val<>();
Var<Integer> x = new Var<>();
Var<Integer> y = new Var<>(() -> a.get() * x.get());
Var<Integer> z = new Var<>(() -> a.get() + x.get());
Var<Integer> r = new Var<>(() -> {
    int res = y.get() + z.get();
    System.out.println("res: " + res);
    return res;
});

Fiber<?> f = new Fiber<Void>(() -> {
    for (int i = 0; i < 200; i++) {
        x.set(i);
        Strand.sleep(100);
    }
}).start();

Strand.sleep(2000);
a.set(3); // this will trigger everything
f.join();
In this examples, vars y and z, are dependent on val a and var x, and will have their values recomputed – after a is set – whenever x changes
