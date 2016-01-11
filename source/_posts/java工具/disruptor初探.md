category: java工具
tag: disruptor
date: 2015-12-19
title: Disruptor 初探
---
## 基础概念
![](https://raw.githubusercontent.com/ming15/blog-website/images/disruptor/Models.png)

* Ring Buffer: 从3.0开始 Ring Buffer 只负责在Disruptor中存储和更新Events.
* Sequence: The Disruptor uses Sequences as a means to identify where a particular component is up to. 每一个消费者(EventProcessor) 都像Disruptor那样维持一个Sequence . 大部分并发代码都要依赖于Sequence值的变化(Sequence同样拥有AtomicLong的大部分特性). 实际上他们二者之间的主要不同之处也是Sequence contains additional functionality to prevent false sharing between Sequences and other values.
* Sequencer: Sequencer 是Disruptor真正的核心部分. 它有俩个主要实现(single producer, multi producer) ,它实现了全部分并发算法,并且能够正确地在生产者和消费者之间传递数据.
* Sequence Barrier: Sequence Barrier 由Sequencer 产生, 它同样还保持着由Sequencer发布的Sequence值, 以及其他消费者依赖的Sequence值. 它同样还决定着当前是否有可用的EventProcessor让消费者执行.
* Wait Strategy: Wait Strategy 用来决定消费者如何等待生产者生成出的事件.
* EventProcessor: The main event loop for handling events from the Disruptor and has ownership of consumer's Sequence. There is a single representation called BatchEventProcessor that contains an efficient implementation of the event loop and will call back onto a used supplied implementation of the EventHandler interface.
* EventHandler: 用户继承这个接口,实现消费者逻辑


## Multicast Events
Disruptor与普通队列最大的不同之处就是, 当有多个消费者监听同一个Disruptor时, 当Disruptor内有多个事件, 这些事件会全部地发布到所有的消费者中, 而普通的队列只能一次一个事件发布到一个消费者中. Disruptor的这种方式让你可以在多个独立并发操作中处理相同的数据.