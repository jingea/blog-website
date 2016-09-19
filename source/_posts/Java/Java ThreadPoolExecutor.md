category: Java
date: 2016-09-19
title: Java ThreadPoolExecutor
---

## 资源管理
一直受困于`ThreadPoolExecutor`的内部实现, 今天就拿出点时间解决几点自己的疑问
1. `ThreadPoolExecutor`是如何重复利用线程资源的
2. `ThreadPoolExecutor` reject 策略

首先拿出一段运行代码
```java
public class TestPool {
	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(5);
		pool.execute(() -> System.out.println("task is running"));
	}
}
```
我们从构造函数入手
```java
public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
}
    
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}
```
我们看到了在构造函数中, 只是进行了初始化的操作, 并没有运行任何逻辑代码, 那么下来我们从`execute(Runnable )`这个方法入手

```java
 public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /*
         * 分三步执行
         * 1. 现在的线程池的线程数量小于corePoolSize值, 则创建一个新的线程, 用新的线程执行command.
         *    在调用addWorker()时会自动检测runState和workerCount, 如果发现添加失败的话, 会返回false.
         * 2. 如果能将command成功地加入到任务队列里, 在接下来的执行中无论是否新建工作线程都要进行对线程池状态
         * 	  进行Double check, 因为 existing ones died since last checking 或者线程池恰巧在这时关闭了.
         * 	  So we recheck state and if necessary roll back the enqueuing if stopped, or start a new thread if there are none.
         * 3. 如果不能将command入列到任务队列的话,就尝试启动一个新的线程来运行它. 如果仍然失败就需要reject任务了.
         */
        int c = ctl.get();
		// 第一步 ：
		// 计算线程池中运行的线程数量, 如果当前运行的线程数量小于corePoolSize, 就增加一个worker.
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
			// 因为在addWorker时会改变ctl的值, 因此重新获取一下
            c = ctl.get();
        }
		// 第二步:
		// 工作线程已经达到corePoolSize数量或者添加worker失败, 将command加入到任务队列里面去
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
			// 如果线程池不再处于运行状态且能成功从任务队列里将删除删除掉, 就reject任务
            if (! isRunning(recheck) && remove(command))
                reject(command);
			// 如果worker数量为0的话，就新建一个worker, 执行刚刚添加到任务队列里的任务
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
		// 第三步:
		// 如果任务队列已经满了, 则尝试新建一个worker用来执行command
        else if (!addWorker(command, false))
            reject(command);
    }
```
这个方法的重点一个是`addWorker()`它会启动一个新的线程, 如果指定了first task(`addWorker(command, true)`), 那么新的worker线程就从first task开始执行.
如果没有指定的话(`addWorker(null, false)`), 就从任务队列里取出任务依次执行.

另一个重点是`workQueue.offer(command)`通过这个方法向任务队列里添加任务, 然后在`Worker`的`runWorker()`里依次执行任务.

下来我们看一下`addWorker()`方法
```java
private boolean addWorker(Runnable firstTask, boolean core) {
		// 下面整个循环都是为了 改变ctl中工作线程worker的数量.
        retry:
        for (;;) {
            int c = ctl.get();
			// 计算线程池状态.
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN &&
					! (rs == SHUTDOWN && firstTask == null && ! workQueue.isEmpty())
					)
				// 当线程池处于非运行状态,
                return false;

			// 开始增加ctl中的线程数量
            for (;;) {
                int wc = workerCountOf(c);
                if (wc >= CAPACITY ||
                    wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();  // Re-read ctl
				// 如果线程池的状态发生了改变则继续执行retry循环, 进行Check if queue empty only if necessary 检测.
				// 没有线程池状态没有发生变化的话, 则继续执行ctl数量增加操作
                if (runStateOf(c) != rs)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

		// ok,到现在ctl中的worker数量已经改变完成, 开始真正的创建worker
        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    int rs = runStateOf(ctl.get());

                    if (rs < SHUTDOWN ||
                        (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }
```
根据线程池的当前状态和指定的bound 检查新的worker能否添加.如果新的worker能被添加的话, 就会创建出一个新的worker, 同时增加worker count的数量。然后在这个新的worker运行firstTask

如果线程池是停止状态或者准备停止的话, 这个方法会返回一个false.如果ThreadFactory创建线程失败的话,也会返回一个false.当创建线程失败时,不管是ThreadFactory返回null还是产生错误(一般是在Thread.start()时抛出OutOfMemoryError), 我们将
执行回滚操作

当新创建一个线程时, firstTask会成为它第一个执行的任务。当线程池线程数量小于corePoolSize或者队列满的时候, 创建出的worker内部会自动创建一个first task ，忽略掉从任务队列中出列的任务.

我们需要着重看一下`t.start();`方法, 这个方法是开始运行`Worker`对象里的`thread`线程对象(其本身也是`Worker`类型). 最终也是执行到`Worker`的`runWorker()`方法.
```java
final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
			// 如果first task不为空的话就先执行first task, 否则就从任务队列中取出task执行
            while (task != null || (task = getTask()) != null) {
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                if ((runStateAtLeast(ctl.get(), STOP) ||
                     (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP))) && !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }
```
看到这里我们可以看出, `ThreadPoolExecutor`其内部也是通过`while`来不断轮训任务队列, 执行任务的`task.run();`方法, 不开启新线程的方式, 来达到线程资源管理的目的.

## 线程池状态
从上面的分析, 我们看到了很多这种代码
```java
workerCountOf(c)
isRunning(c)
```
看到这里可能会有很多疑问, 贴一下`ThreadPoolExecutor`部分内部成员
```java
   private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }
```
`ctl` 内部封装了俩个关键性的字段
*	workerCount, 工作的线程数
*  runState, 线程池状态
为了将这俩个值都存储进`ctl`里，`workerCount`的最大值是500万左右(`(1 << (Integer.SIZE - 3)) - 1`),而不是2亿(queue的最大值是Integer的最大值)。  如果将来需要更高的任务量的话,可以采用`AtomLong`作为`ctl`的类型，但是现在采用int可以带来更快的运行速度和更简单

下面的几个值表示了整个线程池的运行状态

* `RUNNING`:  Accept new tasks and process queued tasks
* `SHUTDOWN`: Don't accept new tasks, but process queued tasks
* `STOP`:     Don't accept new tasks, don't process queued tasks, and interrupt in-progress tasks
* `TIDYING`:  All tasks have terminated, workerCount is zero, the thread transitioning to state TIDYING will run the terminated() hook method
* `TERMINATED`: terminated() has completed


随着线程池运行, 上面几个状态是依次递增的, 但是在整个线程池生命周期中不一定会达到每个状态.
线程池的状态转换过程如下:
* `RUNNING -> SHUTDOWN`  On invocation of shutdown(), perhaps implicitly in finalize()
* `(RUNNING or SHUTDOWN) -> STOP`    On invocation of shutdownNow()
* `SHUTDOWN -> TIDYING` When both queue and pool are empty
* `STOP -> TIDYING`	When pool is empty
* `TIDYING -> TERMINATED`	When the terminated() hook method has completed
