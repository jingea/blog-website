category: 
- concurrency
tag:
- 并发集合
title: ArrayBlockingQueue
---
一个线程安全的、基于数组、有界的、阻塞的、FIFO 队列

ArrayBlockingQueue是一个用数组实现的有界阻塞队列.此队列按照先进先出（FIFO）的原则对元素进行排序.默认情况下不保证访问者公平的访问队列.通常情况下为了保证公平性会降低吞吐量.

(所谓公平访问队列是指阻塞的所有生产者线程或消费者线程,当队列可用时,可以按照阻塞的先后顺序访问队列,即先阻塞的生产者线程)

队列头部元素是队列中存在时间最长的元素,队列尾部是存在时间最短的元素,新元素将会被插入到队列尾部.队列从头部开始获取元素.

ArrayBlockingQueue是“有界缓存区”模型的一种实现,一旦创建了这样的缓存区,就不能再改变缓冲区的大小.ArrayBlockingQueue的一个特点是,必须在创建的时候指定队列的大小.当缓冲区已满,则需要阻塞新增的插入操作,同理,当缓冲区已空需要阻塞新增的提取操作.

ArrayBlockingQueue是使用的是循环队列方法实现的,对ArrayBlockingQueue的相关操作的时间复杂度,可以参考循环队列进行分析

此类基于 java.util.concurrent.locks.ReentrantLock 来实现线程安全，所以提供了 ReentrantLock 所能支持的公平性选择。

```
final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);	// 默认情况下不保证访问者公平的访问队列
final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10, true);	// 保证访问者公平的访问队列
```