category: 
- concurrency
tag:
- 并发集合
title: ConcurrentSkipListSet
---
ConcurrentSkipListSet（提供的功能类似于TreeSet，能够并发的访问有序的set。因为ConcurrentSkipListSet是基于“跳跃列表（skip
list）”实现的，只要多个线程没有同时修改集合的同一个部分，那么在正常读、写集合的操作中不会出现竞争现象。

ConcurrentSkipListSet是线程安全的有序的集合，适用于高并发的场景。ConcurrentSkipListSet和TreeSet，它们虽然都是有序的集合。但是，
1. 它们的线程安全机制不同，TreeSet是非线程安全的，而ConcurrentSkipListSet是线程安全的。
2. ConcurrentSkipListSet是通过ConcurrentSkipListMap实现的，而TreeSet是通过TreeMap实现的。

ConcurrentSkipListSet是通过ConcurrentSkipListMap实现的，它的接口基本上都是通过调用ConcurrentSkipListMap接口来实现的
