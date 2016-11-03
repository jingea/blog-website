category: JVM
date: 2016-11-01
title: safepoint
---
文章参考
* [Safepoints in HotSpot JVM](http://blog.ragozin.info/2012/10/safepoints-in-hotspot-jvm.html)
* [聊聊JVM（六）理解JVM的safepoint](http://blog.csdn.net/iter_zc/article/details/41847887)
* [JVM的Stop The World，安全点，黑暗的地底世界](http://calvin1978.blogcn.com/articles/safepoint.html)
* [Logging stop-the-world pauses in JVM](https://plumbr.eu/blog/performance-blog/logging-stop-the-world-pauses-in-jvm)
* [GC safe-point (or safepoint) and safe-region](http://xiao-feng.blogspot.tw/2008/01/gc-safe-point-and-safe-region.html)


在Hostspot JVM中 `STW暂停机制` 称为`safepoint`. 在safepoint过程当中,所有的java线程都会暂停运行，但是如果native线程准备通过jni访问java对象或者调用java方法等，这些线程也会被暂停掉．

那safepoint是如何工作的呢? 在Java Application当中, 每个线程都会检查Safepoint 状态, 如果safepoint是需要的话, 线程就会被打断执行.

JVM有俩种运行方式, 解释形和编译形。
* 解释形:  jvm有俩个`byte code dispatch tables`, 如果需要safepoint的话, JVM会切换BCDT 开启safepoint.
* 编译形: 通过JIT编译代码, JIT一般会在方法返回或者循环结束的位置插入safepoint代码.
safepoint 的状态检查实现机制非常灵活. 像一般的内存变量检查, 需要消耗非常昂贵的内存屏障(memory barriers).


























































