category: Netty
date: 2016-03-14
title: Netty NioEventLoop
---
在真实的业务环境中, 我们都是使用主从Reactor线程模型. 在Netty中主从线程池都是使用的`NioEventLoopGroup`, 它实现了
`java.util.concurrent.Executor`. 虽然在编程中我们使用的是`NioEventLoopGroup`, 但是主要的逻辑确是在`MultithreadEventExecutorGroup`里实现的.

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
