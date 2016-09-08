category: Java
date: 2016-09-08
title: LinkedBlockingQueue
---

`LinkedBlockingQueue` 是基于双锁队列算法(锁实现使用`ReentrantLock`)实现的阻塞式先进先出(FIFO)的链表式队列.

双锁指的是`LinkedBlockingQueue`内部使用了俩个`ReentrantLock`
* `ReentrantLock takeLock` : 用于操作队列头
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
        if (c == capacity)
            signalNotFull();
        return x;
    }
```

* `ReentrantLock putLock` : 用于操作队列尾
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
```
这样一来就实现了了一个高效地读写分离的并发安全链表队列