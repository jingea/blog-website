category: JavaSE 并发
date: 2016-03-28
title: JavaSE 并发安全集合
---

## Queues
* ConcurrentLinkedQueue: 可高效拓展且线程安全的非阻塞FIFO队列
* ConcurrentLinkedDeque : 和`ConcurrentLinkedQueue`很像, 不过是实现了`Deque`接口

支持`BlockingQueue`的接口
* LinkedBlockingQueue
* ArrayBlockingQueue
* SynchronousQueue
* PriorityBlockingQueue
* DelayQueue

TransferQueue
* LinkedTransferQueue : TransferQueue的默认实现

BlockingDeque
* LinkedBlockingDeque ; BlockingDeque的默认实现

## Concurrent Collections
除了`Queue`之外, `java.util.concurrent`包内还定义了一些并发集合
* ConcurrentHashMap : `HashMap`的线程安全版本
* ConcurrentSkipListMap : `TreeMap`的线程安全版本
* ConcurrentSkipListSet
* CopyOnWriteArrayList : `ArrayList`的线程安全版本
* CopyOnWriteArraySet :
