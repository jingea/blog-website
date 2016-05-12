category: Quasar
date: 2015-09-08
title: Quasar Core
---

## Fibers

Quasar最主要的贡献就是提供了一个非常轻量级的线程, 这种线程在Quasar里称为fiber. Fiber不论是功能还是API使用上都与传统线程都非常像, 但不同的是, 它们并不会被OS管理. 每个Fiber占用的内存都非常少(空闲的Fiber大概会占用400byte), 而且当任务切换时会带来更低的CPU负担. 在Quasar中, Fiber可以被一个或者多个ForkJoinPools调度.

Fiber的设计目的并不是取代线程. A fiber should be used when its body (the code it executes) blocks very often waiting on other fibers (e.g. waiting for messages sent by other fibers on a channel, or waiting for the value of a dataflow-variable). 对于那种处于长时间运算的任务, 还是采用传统的线程会更加合适. 但幸运的是, 我们接下来会演示Fiber如何与传统线程进行良好的交互.

Fiber还特别适用于那种引起callback hell的异步调用代码. Fiber还会在保持代码简洁性以及维护代码逻辑线程化模式的同时保持应用的扩展性以及性能的不损失.

### Using Fibers
我们使用`Fiber`类来创建出一个fiber, 下面的fiber类似于一个线程:
```java
new Fiber<V>() {
  @Override
  protected V run() throws SuspendExecution, InterruptedException {
        // your code
    }
}.start();
```

上面的例子与开启一个线程不同的是:
1. fiber可以有一个泛型化的返回值(如果不需要返回值的话, 则返回类型使用`Void`, 直接返回null即可)
2. `run`方法可以抛出一个`InterruptedException`异常.

You can also start a fiber by passing an instance of  to Fiber’s constructor:
另外一种开启fiber的方式是, 向`Fiber`构造器传递一个`SuspendableRunnable`或者`SuspendableCallable`类型的实例
```java
new Fiber<Void>(new SuspendableRunnable() {
  public void run() throws SuspendExecution, InterruptedException {
    // your code
  }
}).start();
```

You can join a fiber much as you’d do a thread with the join method. To obtain the value returned by the fiber (if any), you call the get method, which joins the fiber and returns its result.

Other than Fiber’s constructor and start method, and possibly the join and get methods, you will not access the Fiber class directly much. To perform operations you would normally want to do on a thread, it is better to use the Strand class (discussed later), which is a generalizations of both threads and fibers.


### The Fiber Scheduler and Runtime Fiber Monitoring

`Fiber`是通过`FiberScheduler`进行调度的. 当创建`Fiber`实例时, 你可以指定它的调度器, 如果你不指定的话, 就会使用默认的`FiberForkJoinScheduler`实例进行调度(基于`ForkJoinPool`). `FiberForkJoinScheduler`是一个高质量的`work-stealing`模式调度器, 但是某些情况下, 你也许想要使用线程池或者自己设计的调度器, 在这种情况下, 你可以使用`FiberExecutorScheduler`, 具体用法参考它的API.

每一个调度器都会创建出一个`MXBean`, 用于监控调度器调度fiber. MXBean的名称为`co.paralleluniverse:type=Fibers,name=SCHEDULER_NAME`, 更多细节也是参考其API.

### Runaway Fibers
A fiber that is stuck in a loop without blocking, or is blocking the thread its running on (by directly or indirectly performing a thread-blocking operation) is called a runaway fiber. It is perfectly OK for fibers to do that sporadically (as the work stealing scheduler will deal with that), but doing so frequently may severely impact system performance (as most of the scheduler’s threads might be tied up by runaway fibers). Quasar detects runaway fibers, and notifies you about which fibers are problematic, whether they’re blocking the thread or hogging the CPU, and gives you their stack trace, by printing this information to the console as well as reporting it to the runtime fiber monitor (exposed through a JMX MBean; see the previous section).

fiber一般是非阻塞运行的, 但是也可以阻塞运行他的线程.

Note that this condition might happen when classes are encountered for the first time and need to be loaded from disk. This is alright because this happens only sporadically, but you may notice reports about problematic fibers during startup, as this when most class loading usually occurs.

If you wish to turn off runaway fiber detection, set the co.paralleluniverse.fibers.detectRunawayFibers system property to "false".

### Thread Locals
Using ThreadLocals in a fiber works as you’d expect – the values are local to the fiber. An InheritableThreadLocal inherits its value from the fiber’s parent, i.e. the thread or the fiber that spawned it.

### Suspendable This, Suspendable That
The run method in Fiber, SuspendableRunnable and SuspendableCallable declares that it may throw a SuspendExecution exception. This is not a real exception, but part of the inner working of fibers. Any method that may run in a fiber and may block, declares to throw this exception or is annotated with the @Suspendable annotation. Such a method is called a suspendable method. When a method you write calls a suspendable method, it, too, is a suspendable method, and must therefore declare to throw SuspendExecution (if you cannot add this exception to your method’s throws clause, say, because you’re implementing an interface that does not throw it, you can annotate your method with the @Suspendable annotation, but this requires extra consideration; please see the Advanced Fiber Usage section). Adding SuspendExecution to the throws clause is convenient because it makes the compiler force you to add the exception to any method that calls your method, which you should.

> Note: Other than a few methods in the Fiber class that are usually only used internally, whenever you encounter a method that declares to throw SuspendExecution, it is safe to call to use by fibers as well as regular threads. If used in a thread, it will never actually throw a SuspendExecution exception, so it is best to declare a catch(SuspendExecution e) block when called on a regular thread, and just throw an AssertionError, as it should never happen.

### Synchronized Methods/Blocks in Fibers
Because synchronized blocks or methods block the kernel threads, by default they are not allowed in fibers. Suspendable methods that are marked synchronized or contain synchronized blocks will cause Quasar instrumentation to fail. However, Quasar can gracefully handle the occasional blocked thread, so synchronized methods/blocks can be allowed by passing the m argument to the Quasar Java agent, or by setting the allowMonitors property on the instrumentation Ant task.

## Strands
A strand (represented by the Strand class) is an abstraction for both fibers and threads; in short – a strand is either a fiber or a thread. The Strand class provides many useful methods. Strand.currentStrand() returns the current running strand (be it a fiber or a thread); Strand.sleep() suspends the current strand for the given number of milliseconds; getStackTrace returns the current stack trace of the strand. To learn more about what operations you can perform on strands, please consult the Javadoc.

park and unpark
Most importantly (though relevant only for power-users who would like to implement their own concurrency primitives, such as locks), the Strand class contains the methods park and unpark, that delegate to Fiber.park and Fiber.unpark methods if the strand is a fiber, or to LockSupport’s park and unpark methods if the strand is a thread (LockSupport lies at the core of all java.util.concurrent classes). This allows to create synchronization mechanisms that work well for both fibers and threads.

Just as you almost never use LockSupport directly, so, too, you will never need to call Strand.park or Strand.unpark, unless you’re writing your own concurrency constructs (like a new kind of lock).

### Transforming any Asynchronous Callback to A Fiber-Blocking Operation
As we said above, fibers are great as a replacement for callbacks. The FiberAsync class helps us easily turn any callback-based asynchronous operation to as simple fiber-blocking call.

Assume that operation Foo.asyncOp(FooCompletion callback) is an asynchronous operation, where FooCompletion is defined as:

interface FooCompletion {
  void success(String result);
  void failure(FooException exception);
}
We then define the following subclass of FiberAsync:

class FooAsync extends FiberAsync<String, FooException> implements FooCompletion {
  @Override
  public void success(String result) {
    asyncCompleted(result);
  }

  @Override
  public void failure(FooException exception) {
    asyncFailed(exception);
  }
}
Then, to transform the operation to a fiber-blocking one, we can define:

String op() {
  new FooAsync() {
    protected void requestAsync() {
       Foo.asyncOp(this);
    }
  }.run();
}
The call to run will block the fiber until the operation completes.

Note: each FiberAsync instance will be linked to the invoking fiber upon construction and it will maintain internal state for a single operation. This means that it needs to be both created and run by the invoking fiber and it can be used for a single operation call only (that is, it cannot be re-used for further calls).

Transforming asynchronous code to fiber-blocking calls has a negligible overhead both in terms of memory and performance, while making the code shorter and far simpler to understand.

## Advanced Fiber Usage
### Fiber Internals
We will now cover in some depth the inner workings of Quasar fibers. You should read this section if you’d like to annotate suspendable methods with the @Suspendable annotation rather than by declaring throws SuspendExecution, or if you’re just curious.

Internally, a fiber is a continuation which is then scheduled in a scheduler. A continuation captures the instantaneous state of a computation, and allows it to be suspended and then resumed at a later time from the point where it was suspended. Quasar creates continuations by instrumenting (at the bytecode level) suspendable methods. For scheduling, Quasar uses ForkJoinPool, which is a very efficient, work-stealing, multi-threaded scheduler.

Whenever a class is loaded, Quasar’s instrumentation module (usually run as a Java agent) scans it for suspendable methods. Every suspendable method f is then instrumented in the following way: It is scanned for calls to other suspendable methods. For every call to a suspendable method g, some code is inserted before (and after) the call to g that saves (and restores) the state of a local variables to the fiber’s stack (a fiber manages its own stack), and records the fact that this (i.e. the call to g) is a possible suspension point. At the end of this “suspendable function chain”, we’ll find a call to Fiber.park. park suspends the fiber by throwing a SuspendExecution exception (which the instrumentation prevents you from catching, even if your method contains a catch(Throwable t) block).

If g indeed blocks, the SuspendExecution exception will be caught by the Fiber class. When the fiber is awakened (with unpark), method f will be called, and then the execution record will show that we’re blocked at the call to g, so we’ll immediately jump to the line in f where g is called, and call it. Finally, we’ll reach the actual suspension point (the call to park), where we’ll resume execution immediately following the call. When g returns, the code inserted in f will restore f’s local variables from the fiber stack.

This process sounds complicated, but its incurs a performance overhead of no more than 3%-5%.

### @Suspendable
So far, our way to specify a suspendable method is by declaring it throws SuspendExecution. This is convenient because SuspendExecution is a checked exception, so if f calls g and g is suspendable, the Java compiler will force us to declare that f is suspendable (and it must be because it calls g and g might be suspended).

Sometimes, however, we cannot declare f to throw SuspendExecution. One example is that f is an implementation of an interface method, and we cannot (or don’t want to) change the interface so that it throws SuspendExecution. It is also possible that we want f to be run in regular threads as well as fibers.

An example for that are the synchronization primitives in the co.paralleluniverse.strands.concurrent package, which implement interfaces declared in java.util.concurrent, and we want to maintain compatibility. Also, no harm will come if we use these classes in regular threads. They will work just as well for threads as for fibers, because internally they call Strand.park which is fiber-blocking (suspends) if run in a fiber, but simply blocks the thread if not.

So, suppose method f is declared in interface I, and we’d like to make its implementation in class C suspendable. The compiler will not let us declare that we throw SuspendExecution because that will conflict with f’s declaration in I.

What we do, then, is annotate C.f with the @Suspendable annotation (in the co.paralleluniverse.fibers package). Assuming C.f calls park or some other suspendable method g – which does declare throws SuspendExecution, we need to surround f’s body with try {} catch(SuspendExecution) just so the method will compile, like so:

class C implements I {
  @Suspendable
  public int f() {
    try {
      // do some stuff
      return g() * 2;
    } catch(SuspendExecution s) {
      throw new AssertionError(s);
    }
  }
}
The catch block will never be executed; the instrumentation will take care of that.

But now let’s consider method h:

@Suspendable
public void h(I x) {
  x.f();
}
First, if we want to run h in a fiber, then it must be suspendable because it calls f which is suspendable. We could designate h as suspendable either by annotating it with @Suspendable or by declaring throws SuspendExecution (even though f is not declared to throw SuspendExecution).

When h is encountered by the instrumentation module, it will be instrumented because it’s marked suspendable, but in order for the instrumentation to work, it needs to know of h’s calls to other instrumented methods. h calls f, which is suspendable, but through its interface I, while we’ve only annotated f’s implementation in class C. The instrumenter doeKotls not know that I.f has an implementation that might suspend.

Therefore, if you’d like to use the @Suspendable annotation, there’s a step you need to add to your build step, after compilation and before creating the jar file: running the co.paralleluniverse.fibers.instrument.SuspendablesScanner Ant task. In Gradle it looks like this:

ant.taskdef(name:'scanSuspendables', classname:'co.paralleluniverse.fibers.instrument.SuspendablesScanner',
    classpath: "build/classes/main:build/resources/main:${configurations.runtime.asPath}")
ant.scanSuspendables(
    auto:false,
    suspendablesFile: "$sourceSets.main.output.resourcesDir/META-INF/suspendables",
    supersFile: "$sourceSets.main.output.resourcesDir/META-INF/suspendable-supers") {
    fileset(dir: sourceSets.main.output.classesDir)
}
SuspendablesScanner scans your code after it’s been compiled for methods annotated with @Suspendable. In our example it will find C.f. It will then see that C.f is an implementation of I.f, and so it will list I.f in a text file (META-INF/suspendable-supers), that contains all methods that have overriding suspendable implementations.

When the instrumentation module instruments h, it will find I.f in the file, and, knowing it might suspend, inject the appropriate code.

Note that this has no effect on other calls to I.f. The instrumentation module only cares that I.f has suspendable implementations when it finds it called in suspendable methods (in our case: h).

When using AOT instrumentation, InstrumentationTask must be able to find META-INF/suspendable-supers in its classpath.

### Automatic Instrumentation
Quasar supports automatic detection of suspendable methods, without manually marking them at all. The SuspendableScanner ant task can be configured to automatically find suspendable methods by analyzing the call graph:

ant.taskdef(name:'scanSuspendables', classname:'co.paralleluniverse.fibers.instrument.SuspendablesScanner',
    classpath: "build/classes/main:build/resources/main:${configurations.runtime.asPath}")
ant.scanSuspendables(
    auto:true,
    suspendablesFile: "$sourceSets.main.output.resourcesDir/META-INF/suspendables",
    supersFile: "$sourceSets.main.output.resourcesDir/META-INF/suspendable-supers") {
    fileset(dir: sourceSets.main.output.classesDir)
}
This will create a META-INF/suspendables file containing the names of the suspendable methods.

When using AOT instrumentation, InstrumentationTask must be able to find META-INF/suspendables and META-INF/suspendable-supers in its classpath.

Automatic detection of suspendable methods is an experimental feature.

### Fiber Serialization
Fibers can be serialized while parked, and then deserialized an unparked to continue where they left off. The parkAndSerialize method parks the currently running fiber, and then calls the passed callback, which can serialize the fiber (or any object graph containing the fiber) into a byte array using the supplied serializer.

The unparkSerialized method deserializes the serialized representation of the fiber, and unparks it. You can deserialize the byte array using the serializer returned from the getFiberSerializer method, and pass the (uninitialized, unparked) deserialized fiber to the unparkDeserialized method. The latter approach is necessary if the serialized fiber is part of a bigger object graph serialized in the byte array.

### Troubleshooting Fibers
If you forget to mark a method as suspendable (with throws SuspendExecution or @Suspendable), you will encounter some strange errors. These will usually take the form of non-sensical ClassCastExceptions, NullPointerExceptions, or SuspendExecution being thrown. To troubleshoot those, set the value of the co.paralleluniverse.fibers.verifyInstrumentation system property to true and run your program. This will verify that all of your methods are instrumented properly, or print a warning to the console letting you know which methods that should have been marked suspendable weren’t.

Do not turn on verifyInstrumentation in production, as it will slow down your code considerably.

## Channels
Channels are queues used to pass messages between strands (remember, strands are a general name for threads and fibers). If you are familiar with Go, Quasar channels are like Go channels.

A channel is an interface that extends two other interfaces: SendPort, which defines the methods used to send messages to a channel, and ReceivePort, which defines the methods used to receive messages from a channel.

Channels are normally created by calling any of the newChannel static methods of the Channels class. The newChannel methods create a channel with a specified set of properties. Those properties are:

* bufferSize – if positive, the number of messages that the channel can hold in an internal buffer; 0 for a transfer channel, i.e. a channel with no internal buffer. or -1 for a channel with an unbounded (infinite) buffer.
* policy – the OverflowPolicy specifying how the channel (if bounded) will behave if its internal buffer overflows.
* singleProducer – whether the channel will be used by a single producer strand.
* singleConsumer – whether the channel will be used by a single consumer strand.
Note that not all property combinations are supported. Consult the Javadoc for details.

### Sending and Receiving Messages
Messages are sent to a channel using the SendPort.send method. The send method blocks if the channel’s buffer is full and the channel has been configured with the BLOCK overflow policy. There are versions of send that block indefinitely or up to a given timeout, and the trySend method sends a message if the channel’s buffer has room, or returns immediately, without blocking, if not. Consult the Javadoc for details.

Messages are received from a channel using the ReceivePort.receive method. There are versions of receive that block indefinitely or up to a given timeout, and the tryReceive method receives a message if one is available, or returns immediately, without blocking, if not. Consult the Javadoc for details.

A channel can be closed with the close method, found in both ReceivePort and SendPort. All messages sent to the channel after the close method has been called will be silently ignored, but all those sent before will still be available (when calling receive). After all messages sent before the channel closed are consumed, the receive function will return null, and ReceivePort.isClosed() will return true.

> Note: As usual, while the blocking channel methods declare to throw SuspendExecution, this exception will never actually be thrown. If using channels in a plain thread, you should catch(SuspendExecution e) { throw AssertionError(); }. Alternatively, you can use the convenience wrappers ThreadReceivePort and ThreadSendPort.

### Primitive Channels
Quasar provides 4 types of channels for primitive data types: int, long, float and double. Consult the Javadoc of, for example, IntSendPort IntReceivePort and for details.

All primitive channels do not support multiple consumers.

### Ticker Channels
A channel created with the DISPLACE overflow policy is called a ticker channel because it provides guarantees similar to that of a digital stock-ticker: you can start watching at any time, the messages you read are always read in order, but because of the limited screen size, if you look away or read to slowly you may miss some messages.

The ticker channel is useful when a program component continually broadcasts some information. The size channel’s circular buffer, its “screen” if you like, gives the subscribers some leeway if they occasionally fall behind reading.

A ticker channel is single-consumer, i.e. only one strand is allowed to consume messages from the channel. On the other hand, it is possible, and useful, to create several views of the channel, each used by a different consumer strand. A view (which is of type TickerChannelConsumer is created with the Channels.newTickerConsumerFor method.

The method returns a ReceivePort that can be used to receive messages from channel. Each ticker-consumer will yield monotonic messages, namely no message will be received more than once, and the messages will be received in the order they’re sent, but if the consumer is too slow, messages could be lost.

Each consumer strand will use its own ticker-consumer, and each can consume messages at its own pace, and each TickerChannelConsumer port will return the same messages (messages consumed from one will not be removed from the other views), subject possibly to different messages being missed by different consumers depending on their pace.

### Transforming Channels (AKA Reactive Extensions)
The Channels class has several static methods that can be used to manipulate and compose values sent to or received off channels:

* map - returns a channel that transforms messages by applying a given mapping function. There are two versions of map: one that operates on ReceivePort and one that operates on SendPort.
* filter - returns a channel that only lets messages that satisfy a predicate through. There are two versions of filter: one that operates on ReceivePort and one that operates on SendPort.
* flatMap - returns a channel that transforms any message into a new channel whose messages are then concatenated into the returned channel. There are two versions of flatMap: one that operates on ReceivePort and one that operates on SendPort.
* reduce - returns a channel that transforms messages by applying a given reducing function. There are two versions of reduce: one that operates on ReceivePort and one that operates on SendPort.
* zip - returns a channel that combines each vector of messages from a vector of channels into a single combined message.
* take - returns a channel that allows receiving at most N messages from another channel before being automatically closed.
* group - returns a channel that funnels messages from a set of given channels and supports its atomic dynamic reconfiguration as well as setting mute, pause and solo states for a subset of it (similarly to core.async’s mix).
The fiberTransform method can perform any imperative channel transformation by running transformation code in a new dedicated fiber. The transformation reads messages from an input channels and writes messages to the output channel. When the transformation terminates, the output channel is automatically closed.

Here’s an example of fiberTransform using Java 8 syntax:

Channels.fiberTransform(Channels.newTickerConsumerFor(t), avg,
        (DoubleReceivePort in, SendPort<Double> out) -> {
            try {
                double[] window = new double[WINDOW_SIZE];
                long i = 0;
                for (;;) {
                    window[(int) (i++ % WINDOW_SIZE)] = in.receiveDouble();
                    out.send(Arrays.stream(window).average().getAsDouble());
                }
            } catch (ReceivePort.EOFException e) {
            }
        });
transform and transformSend wrap a ReceivePort or a SendPort respectively, with a fluent interface for all the transformations covered in this section.

### Channel Selection
A powerful tool when working with channels is the ability to wait on several channel operations at once. If you are familiar with the Go programming language, this capability is provided by the select statement.

The Selector class exposes several static methods that allow channel selection. The basic idea is this: you declare several channel operations (sends and receives), each possibly operating on a different channel, and then use Selector to perform at most one.

Here is an example of using Selector. For details, please consult the Javadoc:

SelectAction sa = Selector.select(Selector.receive(ch1), Selector.send(ch2, msg));
The example will do exactly one of the following operations: send msg to ch1 or receive a message from ch2.

A very concise select syntax for Kotlin is available in the co.paralleluniverse.kotlin package:

val ch1 = Channels.newChannel<Int>(1)
val ch2 = Channels.newChannel<Int>(1)

assertTrue (
    fiber @Suspendable {
        select(Receive(ch1), Send(ch2, 2)) {
            it
        }
    }.get() is Send
)

ch1.send(1)

assertTrue (
    fiber @Suspendable {
        select(Receive(ch1), Send(ch2, 2)) {
            when (it) {
                is Receive -> it.msg
                is Send -> 0
                else -> -1
            }
        }
    }.get() == 1
)}
