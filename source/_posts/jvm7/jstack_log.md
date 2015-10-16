category: jvm7
date: 2014-10-08
title: jstack_log
---
# jstack_log

## jstack日志
下面摘抄的是NETTY中空epoll的一段记录
```
"nioEventLoopGroup-2461-1" #4955 prio=10 os_prio=0 tid=0x00007fd857e9a000 nid=0x5e19 runnable [0x00007fd7374bc000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.EPollArrayWrapper.epollWait(Native Method)
	at sun.nio.ch.EPollArrayWrapper.poll(EPollArrayWrapper.java:269)
	at sun.nio.ch.EPollSelectorImpl.doSelect(EPollSelectorImpl.java:79)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000000e673cf38> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000000e673cd30> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000000e673cc58> (a sun.nio.ch.EPollSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:622)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:310)
	at io.netty.util.concurrent.SingleThreadEventExecutor$2.run(SingleThreadEventExecutor.java:116)
	at io.netty.util.concurrent.DefaultThreadFactory$DefaultRunnableDecorator.run(DefaultThreadFactory.java:137)
	at java.lang.Thread.run(Thread.java:745)

   Locked ownable synchronizers:
	- None
```

##### 第一行数据分析
1. `nioEventLoopGroup-2461-1` 表示的是进程名字
2. `#4955`
3. `prio=10`
4. `os_prio=0`
5. `nid`:线程ID的16进制表示(可以通过`top -H`查看pid)
6. `tid`:
7. `runnable`
8. `[0x00007fd7374bc000]`

##### 线程堆栈信息
1. `java.lang.Thread.State` 线程状态
2. `locked` 锁住的资源,分别锁住了`<0x00000000e673cf38>`, `<0x00000000e673cd30>`, `<0x00000000e673cc58>`



## java.lang.Thread.State 线程状态

* ` Runnable ` : 线程具备所有运行条件，在运行队列中准备操作系统的调度，或者正在运行
* `waiting for monitor entry` :  在等待进入一个临界区,所以它在`Entry Set`队列中等待.此时线程状态一般都是 `Blocked`:
	如果大量线程在`waiting for monitor entry`, 可能是一个全局锁阻塞住了大量线程.如果短时间内打印的 `thread dump` 文件反映,随着时间流逝,`waiting for monitor entry`的线程越来越多,没有减少的趋势,可能意味着某些线程在临界区里呆的时间太长了,以至于越来越多新线程迟迟无法进入临界区.

* `waiting on condition` : 说明它在等待另一个条件的发生,来把自己唤醒,或者干脆它是调用了 `sleep(N)`.
	###### 此时线程状态大致为以下几种：<br>
	1. `java.lang.Thread.State: WAITING (parking)`：一直等那个条件发生；
	2. `java.lang.Thread.State: TIMED_WAITING` (`parking`或`sleeping`)：定时的,那个条件不到来,也将定时唤醒自己.

	如果大量线程在`waiting on condition`：可能是它们又跑去获取第三方资源,尤其是第三方网络资源,迟迟获取不到`Response`,导致大量线程进入等待状态.所以如果你发现有大量的线程都处在 `Wait on condition`,从线程堆栈看,正等待网络读写,这可能是一个网络瓶颈的征兆,因为网络阻塞导致线程无法执行.

* `in Object.wait()` : 说明它获得了监视器之后,又调用了 `java.lang.Object.wait()` 方法.	
	每个 Monitor在某个时刻,只能被一个线程拥有,该线程就是 `Active Thread`,而其它线程都是 `Waiting Thread`,分别在两个队列 `Entry Set`和 `Wait Set`里面等候.在 `Entry Set`中等待的线程状态是 `Waiting for monitor entry`,而在 `Wait Set`中等待的线程状态是 `in Object.wait()`.
	当线程获得了 `Monitor`,如果发现线程继续运行的条件没有满足,它则调用对象(一般就是被 `synchronized` 的对象)的 `wait()` 方法,放弃了 `Monitor`,进入 `Wait Set`队列.

	######此时线程状态大致为以下几种：<br>
	1. `java.lang.Thread.State: TIMED_WAITING (on object monitor)`; 
	2. `java.lang.Thread.State: WAITING (on object monitor)`;
