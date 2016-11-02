category: JVM
date: 2016-11-01
title: safepoint
---
文章参考
* [Safepoints in HotSpot JVM](http://blog.ragozin.info/2012/10/safepoints-in-hotspot-jvm.html)
* [聊聊JVM（六）理解JVM的safepoint](http://blog.csdn.net/iter_zc/article/details/41847887)

在Hostspot JVM中 `STW暂停机制` 称为`safepoint`. 在safepoint过程当中,所有的Ｊａｖａ线程都会暂停运行，但是如果ｎａｔｉｖｅ线程准备通过ＪＮＩ访问Ｊａｖａ对象或者调用Ｊａｖａ方法等，这些线程也会被暂停掉．

那safepoint是如何工作的呢? 在Java Application当中, 每个线程都会检查Safepoint 状态, 如果safepoint是需要的话, 线程就会被打断执行.

JVM有俩种运行方式, 解释形和编译形。
* 解释形:  jvm有俩个`byte code dispatch tables`, 如果需要safepoint的话, JVM会切换BCDT 开启safepoint.
* 编译形: 通过JIT编译代码, JIT一般会在方法返回或者循环结束的位置插入safepoint代码.
safepoint 的状态检查实现机制非常灵活. 像一般的内存变量检查, 需要消耗非常昂贵的内存屏障(memory barriers).


























































