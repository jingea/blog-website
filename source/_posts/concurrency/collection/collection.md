category: 
- collection
title: 简介
---
java 提供了俩类用于并发的集合

###### 基本上,Java提供两种在并发应用程序中使用的集合：
* 阻塞式集合:这类集合包括添加和移除数据的方法.当集合已满或为空时,被调用的添加或者移除的方法就不能立即被执行,那么调用这个方法的线程将被阻塞,一直到该方法可以被执行.
			  这种集合包括添加和删除数据的操作.如果操作不能立即进行,是因为集合已满或者为空,该程序将被阻塞,直到操作可以进行.
* 非阻塞式集合:这类集合也包括添加和移除数据的方法,如果方法不能被执行,则返回null或者抛出异常,但是调用这个方法的线程不会被阻塞.
				这种集合也包括添加和删除数据的操作.如果操作不能立即进行,这个操作将返回null值或抛出异常,但该线程将不会阻塞.
			  
如果我们要实现一个线程安全的队列有两种实现方式：一种是使用阻塞算法,另一种是使用非阻塞算法.使用阻塞算法的队列可以用一个锁(入队和出队用同一把锁)或两个锁(入队和出队用不同的锁)等方式来实现,
而非阻塞的实现方式则可以使用循环CAS的方式来实现,

## 阻塞队列:

###### 阻塞队列说明:
> 它实质上就是一种带有一点扭曲的 FIFO 数据结构.它提供了可阻塞的put和take方法.如果Queue已经满了,put方法会被阻塞直到有空间可用;如果Queue是空的,那么take方法会被阻塞,直到有元素可用.offer方法如果数据项不能被添加到队列中,会返回一个失败的状态.Queue的长度可用有限,也可以无限;无限的Queue永远不会充满,所以它的put方法永远不会被阻塞.阻塞队列支持生产者-消费者设计模式.BlockingQueue可以使用任意数量的生产者和消费者,从而简化了生产者-消费者设计的实现.

> Java6 新增了俩种新的容器类型 Deque和 BlockingDeque 分别对Queue 和BlockingQueue 进行了拓展Deque是一个双端队列,实现了在队列头和队列尾 > > > 高效的插入和移除.正如阻塞队列适用于生产者和消费者模式,双端队列适用于工作密取模式(工作密取模式非常适用于既是生产者又是消费者问题).

> ArrayDeque：Deque是基于有首尾指针的数组(环形缓冲区)实现的.和LinkedList不同,这个类没有实现List接口.因此,如果没有首尾元素的话就不能取出任何元素.这个类比LinkedList要好一些,因为它产生的垃圾数量较少(在扩展的时候旧的数组会被丢弃).Stack：一种后进先出的队列.不要在生产代码中使用,使用别的Deque来代替(ArrayDeque比较好).PriorityQueue：一个基于优先级的队列.使用自然顺序或者制定的比较器来排序.他的主要属性——poll/peek/remove/element会返回一个队列的最小值.不仅如此,PriorityQueue还实现了Iterable接口,队列迭代时不进行排序(或者其他顺序).在需要排序的集合中,使用这个队列会比TreeSet等其他队列要方便.

* `BlockingQueue`：简化了生产者-消费者实现过程,支持任意数量的生产者或者消费者.
* `ArrayBlockingQueue`：一个由数组结构(ArrayList)组成的有界阻塞队列.比同步List拥有更高的并发性
基于数组实现的一个有界阻塞队,大小不能重新定义.所以当你试图向一个满的队列添加元素的时候,
就会受到阻塞,直到另一个方法从队列中取出元素.

* `DelayQueue`：一个由优先级堆支持的、基于时间的调度队列.存储延迟元素的阻塞列表无界的保存Delayed元素的集合.元素只有在延时已经过期的时候才能被取出.队列的第一个元素延期最小
(包含负值——延时已经过期).当你要实现一个延期任务的队列的时候使用(不要自己手动实现——使用ScheduledThreadPoolExecutor).

* `LinkedBlockingDeque`: 一个由链表结构组成的双向阻塞队列.可选择有界或者无界基于链表的实现.在队列为空或者满的情况下使用ReentrantLock-s.LinkedBlockingQueue与此一样

* `LinkedBlockingQueue`：一个由链表结构(LinkedList)组成的有界阻塞队列.比同步List拥有更高的并发性

* `LinkedTransferQueue`: 一个由链表结构组成的无界阻塞队列.基于链表的无界队列.除了通常的队列操作,它还有一系列的transfer方法,可以让生产者直接给等待的消费者传递信息,这样就不用将元素存储到队列中了.这是一个基于CAS操作的无锁集合.

* `PriorityBlockingQueue`：一个支持优先级排序的无界阻塞队列.PriorityQueue的无界的版本.

* `SynchronousQueue`：一个不存储元素的阻塞队列.它维护一组线程,这些线程在等待着吧元素加入或移除队列.一个有界队列,其中没有任何内存容量.这就意味着任何插入操作必须等到响应的取出操作才能执行,反之亦反.如果不需要Queue接口的话,通过Exchanger类也能完成响应的功能.


###### 非阻塞集合说明:

这一部分将介绍java.util.concurrent包中线程安全的集合.这些集合的主要属性是一个不可分割的必须执行的方法.因为并发的操作,例如add或update或者check再update,都有一次以上的调用,必须同步.因为第一步从集合中组合操作查询到的信息在开始第二步操作时可能变为无效数据.多数的并发集合是在Java 1.5引入的.ConcurrentSkipListMap / ConcurrentSkipListSet 和 LinkedBlockingDeque
是在Java 1.6新加入的.Java 1.7加入了最后的 ConcurrentLinkedDeque 和 LinkedTransferQueue


## 非阻塞集合:

* `ConcurrentHashMap`:  代替基于散列的Map,使用分段锁机制,任意数量的读取线程可以访问Map,读取线程和写入线程可以并发的访问Map, 一定数量的写入线程可以并发的修改Map.ConcurrentHashMap没有实现对Map加锁以提供独占访问 (Hashtable和synchronizedMap 都可以获得Map的锁以防止其他线程的访问).ConcurrentHashMap本身实现了很多原子复合操作,具体参考测试程序.

get操作全并发访问,put操作可配置并发操作的哈希表.并发的级别可以通过构造函数中concurrencyLevel参数设置(默认级别16).该参数会在Map内部划分一些分区.在put操作的时候只有只有更新的分区是锁住的.
这种Map不是代替HashMap的线程安全版本——任何 get-then-put的操作都需要在外部进行同步.

* `ConcurrentLinkedDeque`: 非阻塞列表. 基于链表实现的无界队列,添加元素不会堵塞.但是这就要求这个集合的消费者工作速度至少要和生产这一样快,不然内存就会耗尽.严重依赖于CAS(compare-and-set)操作.

* `ConcurrentLinkedQueue`: 一个传统的先进先出队列

* `ConcurrentSkipListMap`: 作为同步的SortedMap.基于跳跃列表(Skip List)的ConcurrentNavigableMap实现.本质上这种集合可以当做一种TreeMap的线程安全版本来使用

* `ConcurrentSkipListSet`: 作为同步的SortedSet.使用 ConcurrentSkipListMap来存储的线程安全的Set.

* `CopyOnWriteArrayList`: list的实现每一次更新都会产生一个新的隐含数组副本,所以这个操作成本很高.通常用在遍历操作比更新操作多的集合,比如listeners/observers集合.最适合于读操作通常大大超过写操作的情况.代替遍历操作为主要操作的同步的List.如果写大于读的并发用什么?

* `CopyOnWriteArraySet`: 最适合于读操作通常大大超过写操作的情况.使用CopyOnWriteArrayList来存储的线程安全的Set










