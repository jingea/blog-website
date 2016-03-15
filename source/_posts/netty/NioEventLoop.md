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
                if (ioRatio == 100) {
                    processSelectedKeys();
                    runAllTasks();
                } else {
                    final long ioStartTime = System.nanoTime();

                    processSelectedKeys();

                    final long ioTime = System.nanoTime() - ioStartTime;
                    runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
                }

                if (isShuttingDown()) {
                    closeAll();
                    if (confirmShutdown()) {
                        break;
                    }
                }
            } catch (Throwable t) {
                logger.warn("Unexpected exception in the selector loop.", t);

                // Prevent possible consecutive immediate failures that lead to
                // excessive CPU consumption.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Ignore.
                }
            }
        }
    }
```
