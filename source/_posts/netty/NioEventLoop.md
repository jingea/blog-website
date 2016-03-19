category: Netty
date: 2016-03-14
title: Netty NioEventLoop
---
在真实的业务环境中, 我们都是使用主从Reactor线程模型. 在Netty中主从线程池都是使用的`NioEventLoopGroup`, 它实现了
`java.util.concurrent.Executor`. 虽然在编程中我们使用的是`NioEventLoopGroup`, 但是主要的逻辑确是在`MultithreadEventExecutorGroup`里实现的.
![](https://raw.githubusercontent.com/ming15/blog-website/images/netty/NioEventLoopGroup.jpg)
下来我们首先看一下`MultithreadEventExecutorGroup`的数据成员
```java
private final EventExecutor[] children;
private final EventExecutorChooser chooser;

protected MultithreadEventExecutorGroup(int nThreads, ThreadFactory threadFactory, Object... args) {
        children = new SingleThreadEventExecutor[nThreads];
        if (isPowerOfTwo(children.length)) {
            chooser = new PowerOfTwoEventExecutorChooser();
        } else {
            chooser = new GenericEventExecutorChooser();
        }

        for (int i = 0; i < nThreads; i ++) {
            boolean success = false;
            try {
                children[i] = newChild(threadFactory, args);
                success = true;
            } catch (Exception e) {
                // TODO: Think about if this is a good exception type
                throw new IllegalStateException("failed to create a child event loop", e);
            }
        }
```
我们看到了`NioEventLoopGroup`内部聚合了一个`EventExecutor`的数组. 这个数组就构成了主从线程池. 线程的选择由`EventExecutorChooser chooser`来实现
```java
@Override
public EventExecutor next() {
    return chooser.next();
}

private final class PowerOfTwoEventExecutorChooser implements EventExecutorChooser {
    @Override
    public EventExecutor next() {
        return children[childIndex.getAndIncrement() & children.length - 1];
    }
}

private final class GenericEventExecutorChooser implements EventExecutorChooser {
    @Override
    public EventExecutor next() {
        return children[Math.abs(childIndex.getAndIncrement() % children.length)];
    }
}
```
而`newChild()`的方法实现是由子类来确定的, 我们来直接看一下`NioEventLoopGroup`的内部实现
```java
@Override
protected EventExecutor newChild(ThreadFactory threadFactory, Object... args) throws Exception {
    return new NioEventLoop(this, threadFactory, (SelectorProvider) args[0]);
}
```
它是直接生成了一个`NioEventLoop`的实例出来. 下来我们看一下`NioEventLoop`的实现
![](https://raw.githubusercontent.com/ming15/blog-website/images/netty/NioEventLoop.jpg)
我们看一下`NioEventLoop`的属性成员
```java
// 多路选择复用器
Selector selector;
// Netty优化过的SelectedSelectionKeys
private SelectedSelectionKeySet selectedKeys;
// SelectorProvider.provider()提供, 在NioEventLoopGroup构造器中实现
private final SelectorProvider provider;
```
我们看到`NioEventLoop`主要是实现了IO多路复用, 它的任务执行是由父类`SingleThreadEventExecutor`实现的, 下面我们从它的构造器来追溯到`SingleThreadEventExecutor`上
```java
NioEventLoop(NioEventLoopGroup parent, ThreadFactory threadFactory, SelectorProvider selectorProvider) {
    super(parent, threadFactory, false);
    if (selectorProvider == null) {
        throw new NullPointerException("selectorProvider");
    }
    provider = selectorProvider;
    selector = openSelector();
}
```
`SingleThreadEventExecutor`这个类主要是实现了主从线程池中的线程功能, 所有的任务都在单线程中执行, 因此将这个线程池串行化, 可以将其看待成一个线程. 在`SingleThreadEventExecutor`中的构造器中,添加向任务队列中添加一个调用`NioEventLoop`的`run()`方法的任务
```java
protected SingleThreadEventExecutor(
            EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
        thread = threadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                updateLastExecutionTime();
                try {
                    SingleThreadEventExecutor.this.run();
                    success = true;
                } catch (Throwable t) {
                } finally {
                    for (;;) {
                        int oldState = STATE_UPDATER.get(SingleThreadEventExecutor.this);
                        if (oldState >= ST_SHUTTING_DOWN || STATE_UPDATER.compareAndSet(
                                SingleThreadEventExecutor.this, oldState, ST_SHUTTING_DOWN)) {
                            break;
                        }
                    }
                    // Check if confirmShutdown() was called at the end of the loop.
                    if (success && gracefulShutdownStartTime == 0) {
                        logger.error("Buggy " + EventExecutor.class.getSimpleName());
                    }

                    try {
                        // Run all remaining tasks and shutdown hooks.
                        for (;;) {
                            if (confirmShutdown()) {
                                break;
                            }
                        }
                    } finally {
                        try {
                            cleanup();
                        } finally {
                            STATE_UPDATER.set(SingleThreadEventExecutor.this, ST_TERMINATED);
                            threadLock.release();
                            terminationFuture.setSuccess(null);
                        }
                    }
                }
            }
        });
        taskQueue = newTaskQueue();
    }

```
我们看到了一行关键性代码`SingleThreadEventExecutor.this.run()`, 它调用了自己的`run()`方法
```java
protected abstract void run();
```
而这个方法是在`NioEventLoop`中实现的,也是我们要重点分析的代码
```java
@Override
    protected void run() {
        for (;;) {
            boolean oldWakenUp = wakenUp.getAndSet(false);
            try {
                // 查看taskQueue里是否有任务, 如果有任务的话, 则直接selector.selectNow();
                if (hasTasks()) {
                    selectNow();
                } else {
                    select(oldWakenUp);

                    if (wakenUp.get()) {
                        selector.wakeup();
                    }
                }

                cancelledKeys = 0;
                needsToSelectAgain = false;
                final int ioRatio = this.ioRatio;
                // 如果当前线程是百分百执行的话, 则直接处理所有的任务
                if (ioRatio == 100) {
                    processSelectedKeys();
                    runAllTasks();
                } else {
                    final long ioStartTime = System.nanoTime();

                    processSelectedKeys();

                    final long ioTime = System.nanoTime() - ioStartTime;
                    runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
                }

                //
                if (isShuttingDown()) {
                    closeAll();
                    if (confirmShutdown()) {
                        break;
                    }
                }
            } catch (Throwable t) {
            }
        }
    }
```
上面的`run()`就是不断的轮询当前`NioEventLoop`里是否有任务. 然后处理Selector上已经就绪的Channel和任务队列里的任务.
然后我们接着往下看`processSelectedKeys()`方法
```java
private void processSelectedKeys() {
       if (selectedKeys != null) {
           processSelectedKeysOptimized(selectedKeys.flip());
       } else {
           processSelectedKeysPlain(selector.selectedKeys());
       }
   }


   private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
           // check if the set is empty and if so just return to not create garbage by
           // creating a new Iterator every time even if there is nothing to process.
           // See https://github.com/netty/netty/issues/597
           if (selectedKeys.isEmpty()) {
               return;
           }

           Iterator<SelectionKey> i = selectedKeys.iterator();
           for (;;) {
               final SelectionKey k = i.next();
               // 取出SelectionKey的附件
               final Object a = k.attachment();
               i.remove();

               if (a instanceof AbstractNioChannel) {
                 // a有可能是NioServerSocketChannel或者NioSocketChannel
                   processSelectedKey(k, (AbstractNioChannel) a);
               } else {
                   // 如果a不是Channel的话, 那就是NioTask了
                   @SuppressWarnings("unchecked")
                   NioTask<SelectableChannel> task = (NioTask<SelectableChannel>) a;
                   processSelectedKey(k, task);
               }

               if (!i.hasNext()) {
                   break;
               }

               if (needsToSelectAgain) {
                   selectAgain();
                   selectedKeys = selector.selectedKeys();

                   // Create the iterator again to avoid ConcurrentModificationException
                   if (selectedKeys.isEmpty()) {
                       break;
                   } else {
                       i = selectedKeys.iterator();
                   }
               }
           }
       }
```
然后咱们接着往下看对Channel的处理
```java
private static void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
    final NioUnsafe unsafe = ch.unsafe();

    try {
        int readyOps = k.readyOps();
        // Also check for readOps of 0 to workaround possible JDK bug which may otherwise lead
        // to a spin loop
        if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
            // 如果是读事件或者连接的事件,则直接调用read方法
            unsafe.read();
            if (!ch.isOpen()) {
                // Connection already closed - no need to handle write.
                return;
            }
        }
        if ((readyOps & SelectionKey.OP_WRITE) != 0) {
            // 如果是写操作位, 则说明有半包消息没有写完, 需要继续
            ch.unsafe().forceFlush();
        }
        if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
            // remove OP_CONNECT as otherwise Selector.select(..) will always return without blocking
            // See https://github.com/netty/netty/issues/924
            int ops = k.interestOps();
            ops &= ~SelectionKey.OP_CONNECT;
            k.interestOps(ops);

            unsafe.finishConnect();
        }
    } catch (CancelledKeyException ignored) {
        unsafe.close(unsafe.voidPromise());
    }
}
```

> 在unsafe的多态这我们要多说一些, 我们知道NioEventLoop内部处理的Channel其实是有俩种类型的, 一个是`NioServerScoketChannel`一个是`NioSocketChannel`.
>
> `NioServerSocketChannel`继承自`AbstractNioMessageChannel`, 而这个父类实现了一个`NioMessageUnsafe`的一个内部类, 这个内部类的`read()`方法会调用Channel里的`doReadMessage()`方法. 父类的`doReadMessage()`方法是由子类来具体实现的. 在`NioServerScoketChannel`中是生成了一个`NioSocketChannel`的列表作为消息返回, 然后再让`ServerBootstrapAcceptor`将`NioSocketChannel`绑定到从Reactor上.
>
> `NioSocketChannel`继承自`AbstractNioByteChannel`, 这个父类实现了一个`NioByteUnsafe`, 这个Unsafe就负责创建ByteBuf, 接受真正的网络数据了.
