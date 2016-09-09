category: Java
date: 2016-09-08
title: LinkedBlockingQueue
---

`LinkedBlockingQueue` 是基于双锁队列算法(锁实现使用`ReentrantLock`)实现的阻塞式先进先出(FIFO)的链表式队列. 其默认的链表大小是Interger的最大值,也就是说只要内存hold住,可以在`LinkedBlockingQueue`入列2亿多个数据.

双锁指的是`LinkedBlockingQueue`内部使用了俩个`ReentrantLock`
* `ReentrantLock takeLock` : 用于操作队列头, 取出队头元素
```java
  public E take() throws InterruptedException {
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                notEmpty.await();
            }
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1)
                notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
		// 由于count是先get后Decrement的, 因此现在的count < capacity, 通知阻塞的线程可以添加数据了
        if (c == capacity)
            signalNotFull();
        return x;
    }
	
	private E dequeue() {
        // assert takeLock.isHeldByCurrentThread();
        // assert head.item == null;
        Node<E> h = head;
        Node<E> first = h.next;
        h.next = h; // help GC
        head = first;
        E x = first.item;
        first.item = null;
        return x;
    }
```

* `ReentrantLock putLock` : 用于操作队列尾, 向队尾添加元素
```java
    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        // Note: convention in all put/take/etc is to preset local var
        // holding count negative to indicate failure unless set.
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get() == capacity) {
                notFull.await();
            }
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity)
                notFull.signal();
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
    }
	
	 private void enqueue(Node<E> node) {
        // assert putLock.isHeldByCurrentThread();
        // assert last.next == null;
        last = last.next = node;
    }
```
通过双锁分别操作队列头和队列尾就实现了了一个高效地读写分离的并发安全链表队列

刚才我们只是从宏观上看了一下`LinkedBlockingQueue`. 下面我们从细节上分析一下. `LinkedBlockingQueue`有俩个入列方式, 分别是`put`和`offer`, 在上面中我们看到了`put`的实现. 它内部使用了一个`notEmpty`的`Condition`, 使用这个是为了当队列已经满了的时候, 就将添加元素的线程阻塞住。接着我们看一下`take()`方法, 它的最后有一个`signalNotFull()`, 就是由它来通知阻塞线程来添加数据的. 而`offer()`实现就没有阻塞行为
```java
    public boolean offer(E e) {
        if (e == null) throw new NullPointerException();
        final AtomicInteger count = this.count;
        if (count.get() == capacity)
            return false;
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            if (count.get() < capacity) {
                enqueue(node);
                c = count.getAndIncrement();
                if (c + 1 < capacity)
                    notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
        return c >= 0;
    }
```
在`offer()`实现中,如果添加成功就返回true, 添加失败(没有进行添加操作)就返回false, 它并不会阻塞住调用者.

看完入列, 我们再看一下出列操作. 刚开始给出的一段代码就是一段出列代码`take()`方法. `take()`方法内部也使用了一个名为`notEmpty`的`Condition`, 如果当前队列为空的话, 就一直阻塞住进行出列操作的线程, 由`put()`的`signalNotEmpty()`来通知当前阻塞的线程可以出列操作了(但是为什么要判断为0呢？？想不明白).

大概琢磨一下就能理清`LinkedBlockingQueue`的工作流程. 但是仍然遗留了俩个比较大的疑问.
1. `LinkedBlockingQueue`的内部链表是由双锁保证线程安全的,那么当俩个线程分别操作队列头和队列尾的时候,如果恰巧是同一个元素,会不会发生问题
2. 是如何保证`LinkedBlockingQueue`的内部计数器`count`(`AtomicInteger`)的线程安全的?

对于第一个问题, 我画了一张入列和出列的流程图:
![](https://raw.githubusercontent.com/ming15/blog-website/images/concurrency/LinkedBlockingQueue.jpg)
从图中我们可以看到, 在初始化`LinkedBlockingQueue`实例的时候,其内部就有了一个Node, first和last都指向这个Node. 当入列一个元素的时候,last指针后移, 而first指针位置不变(参考`enqueue(Node<E> node)`和`dequeue()`方法)。其实这个时候实际的第一个Node为A, 而在逻辑上来讲第一个Node为B(因为第一个数据是存储在了B里), 出列的时候, 是删除队列的第一个实际位置(A)的Node,取出的是第二个实际位置(B)的数据,此时第三个实际位置就变成了第一个逻辑位置.

对于第二个问题,在`put()`方法中是这么描述的
```bash
/*
 * Note that count is used in wait guard even though it is
 * not protected by lock. This works because count can
 * only decrease at this point (all other puts are shut
 * out by lock), and we (or some other waiting put) are
 * signalled if it ever changes from capacity. Similarly
 * for all other uses of count in other wait guards.
 */
```
在入列的操作中count只会增加, 在出列的过程中count只会减少. 而入列只是向上做边界检查, 出列只是向下做边界检查, 因此只要保证了count是原子的, 哪怕出现了数据不一致的情况也不会出现队列的大小超过最大值或者小于0的情况.