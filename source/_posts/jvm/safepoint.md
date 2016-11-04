category: JVM
date: 2016-11-04
title: safepoint
---
在Hostspot JVM中 `STW暂停机制` 称为`safepoint`. 在safepoint过程当中,所有的java线程都会暂停运行，但是如果native线程准备通过jni访问java对象或者调用java方法等，这些线程也会被暂停掉．

那safepoint是如何工作的呢? 在Java Application当中, 每个线程都会检查Safepoint 状态, 如果safepoint是需要的话, 线程就会被打断执行.

JVM有俩种运行方式, 解释形和编译形。
* 解释形:  jvm有俩个`byte code dispatch tables`, 如果需要safepoint的话, JVM会切换BCDT 开启safepoint.
* 编译形: 通过JIT编译代码, JIT一般会在方法返回或者循环结束的位置插入safepoint代码.
safepoint 的状态检查实现机制非常灵活. 像一般的内存变量检查, 需要消耗非常昂贵的内存屏障(memory barriers).


在讲Safepoint之前, 先看一下
在Java中判断一个对象是否存活采用的是reachability analysis(即root reference)。如果一个对象的引用可以在mutator的栈slot中被直接找到, 那么就说这个对象是reachable的. 当然如果这个对象内部还还引用有其他对象, 那么其他对象也是reachable的.


在Java GC时需要获得整个JVM环境中的一致性的RootReference信息. 但是如果想要获得这个信息的话, 最简单的一种方式是, 在Root Reference过程中, 暂停整个JVM中Java Thread(以及涉及到Java Thread相关的VMThread) ,也就是发生了Stop The World.  但是STW之后, JVM也不一定能够枚举到整个JVM中的所有的root reference, 除非JVM保存了所有的rootRerence信息. 也就是说JVM能够知道都有JVM栈中的哪些slot和register含有对象的引用信息. 如果JVM能够知道这些信息的话，那么 JVM就能进行一次完整的root enumeration，否则JVM root reference出来的结果就是不完整的。

为了能够支持完整的root reference enumeration, JIT 就需要做一些额外的工作了, 因为只有JIT清楚的知道JVM的栈振信息和register内容. 当JIT在编译方法的时候, 它就可以保存每个指令的root reference信息，以便当执行该方法的时候，产生了暂停。

但是记住每个指令的信息所带来的代价实在是太大了。这会额外需要一大块内存进行信息存储, 而且在实际过程中这也是没有必要的，因为只有一小部分的指令才有机会宰执行过程中被暂停。JIT只需要知道这一小部分指令位置的信息就可以了， 这些位置被称为safe-point。safe-point意味着在root set enumeration过程中，这是一个安全的停止的位置。

然而有个问题是，我们如何能够确保mutator能够在safe-point上暂停呢》？
有俩种暂停mutator的方式：抢先式(preemptively )和主动式(voluntarily)

对于抢先式, 无论GC是否要开始收集了,它都会暂停mutator, 如果它发现mutator处于一个unsafe point的位置, 它会恢复mutator运行. 这种机制实现在了 ORP ，Harmony 的前身，但是现在绝大多数虚拟机都不会采用这种架构。

在 Harmony中采用的架构是主动式的，当GC想要触发一个收集垃圾动作时，它会设置一个标记。mutator会在safe-point上检查这个标记，一旦发现这个标记被设置了，就会中断当前执行. 绝大多数都是由JIT来负责插入safe-point位置的。


	1. 一个线程要么在Safepoint中，要么就是出于非Safepoint中。 当线程在Safepoint中时，它的Java machine状态可以被很好的描述出来，而且也可以很安全的被其他线程








文章参考
* [Safepoints in HotSpot JVM](http://blog.ragozin.info/2012/10/safepoints-in-hotspot-jvm.html)
* [聊聊JVM（六）理解JVM的safepoint](http://blog.csdn.net/iter_zc/article/details/41847887)
* [JVM的Stop The World，安全点，黑暗的地底世界](http://calvin1978.blogcn.com/articles/safepoint.html)
* [Logging stop-the-world pauses in JVM](https://plumbr.eu/blog/performance-blog/logging-stop-the-world-pauses-in-jvm)
* [GC safe-point (or safepoint) and safe-region](http://xiao-feng.blogspot.tw/2008/01/gc-safe-point-and-safe-region.html)
* [](http://chriskirk.blogspot.jp/2013/09/what-is-java-safepoint.html )
* [](http://blog.ragozin.info/2012/10/safepoints-in-hotspot-jvm.html )
* [](http://blog.csdn.net/iter_zc/article/details/41847887)
* []( http://www.zhihu.com/question/29268019)



https://groups.google.com/forum/#!msg/mechanical-sympathy/vO7oq9aiG4Y/NrDeAQ1xzcYJ

-XX:+PrintGCApplicationStoppedTime
2016-08-09T20:24:00.003+0800: 6632.406: Total time for which application threads were stopped: 0.0002730 seconds, Stopping threads took: 0.0000517 seconds
![]{http://hg.openjdk.java.net/jdk8u/jdk8u/hotspot/file/fc3cd1db10e2/src/share/vm/runtime/vm_operations.hpp#l39}
这个时间并不指的的是GC的时间, 而是花在GC safe-point里消耗的时间.



-XX:+PrintGCApplicationConcurrentTime
2016-08-09T20:24:00.003+0800: 6632.406: Application time: 0.0001614 seconds
程序未间断执行的时间
