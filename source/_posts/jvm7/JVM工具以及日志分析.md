category: jvm7
date: 2014-10-08
title: JVM工具以及日志分析
---
# JVM 相关工具

## JPS:虚拟机进程状况工具
列出正在运行的虚拟机进程,并显示虚拟机执行主类的名称,以及这些进程的本地虚拟机的唯一ID(LVMID). 

对于本地虚拟机进程来说,LVMID与操作系统的进程ID是一致的,使用windwos的任务管理器或者Unix的ps命令也可以查询到虚拟机进程的LVMID,但如果同时启动了多个虚拟机进程,无法根据进程名称定位时,那就只能依赖jps命令显示主类的功能才能区分.

### 命令格式:
```
jps [ options ] [hostid]
```
jps可以通过RMI协议查询开启了RMI服务的远程虚拟机进程状态,hostid为RMI注册表中注册的主机名

####jps工具主要选项
* `-q`: 只输出LVMID,省略主类的名称
* `-m`: 输出虚拟机进程启动时传递给主类main()函数的参数
* `-l`: 输出主类的全名,如果进程执行的jar包,输出jar路径
* `-v`: 输出虚拟机进程启动时JVM参数.

##jstat:虚拟机统计信息监视工具
用于监视虚拟机各种运行状态信息的命令行工具.它可以显示本地或远程虚拟机进程中的类装载,内存,垃圾收集,JIT编译等运行数据.

###jstat命令格式
```
jstat [ option vmid [interval [s|ms] [count]]]
```
对于命令格式中的VMID与LVMID需要特别说明一下:如果是本地虚拟机进程,VMID和LVMID是一致的,如果是远程
虚拟机进程,那么VMID的格式应该是:
```
[protocol:] [//]lvmid[@hostname [:port] /servername]
```
选项option代表着用户希望查询的虚拟机信息,主要分为三类:类装载,垃圾收集,运行期编译状况.

#### jstat工具主要选项
* `-class`: 监视类装载,卸载数量,总空间及类装载所耗费的时间
* `-gc`: 监视java堆状况,包括Eden区,2个survivor区,老年代,永久代等的容量,已用空间,GC时间合计等信息.
* `-gccapacity`: 监视内容与-gc基本相同,但输出主要关注java堆各个区域使用到最大和最小空间.
* `-gcutil`: 监视内容与-gc基本相同,但输出主要关注已使用空间占总空间的百分比.
* `-gccause`: 与-gcutil功能一样,但是会额外输出导致上一次GC产生的原因.
* `-gcnew`:监视新生代GC的状况.
* `-gcnewcapacity`: 监视内容与-gcnew基本相同输出主要关注使用到的最大和最小空间
* `-gcold`: 监视老年代GC的状况.
* `-gcoldcapacity`: 监视内容与-gcold基本相同,但输出主要关注使用到的最大和最小空间
* `-gcpermcapacity`: 输出永久代使用到呃最大和最小空间
* `-compiler`: 输出JIT编译器编译过的方法,耗时等信息
* `-printcompilation`: 输出已经被JIT编译的方法.

> E -> Eden. S0 -> Survivor0. S1 -> Survivor1. O -> Old. P -> Permanent. YGC -> YoungGC,Minor GC.
> FGC  -> Full GC. FGCT -> Full GC Time.

## Jinfo:Java配置
jinfo的作用是实时查看和调整虚拟机的各项参数.
### jinfo命令格式
```
jinfo [ option ] pid
```

## Jmap
java内存映射工具,用于生成堆转储快照.

如果不使用jmap命令,想要获取java堆转储快照还有一些比较暴力的手段:
`-XX:+HeapDumpOnOutOfMemoryError`: 可以让虚拟机在OOM异常自动生成dump文件,通过
`-XX:+HeapDumpOnCtrlBreak`参数则可以使用`[CTRL] + [Break]`: 键让虚拟机生成dump文件,又或者在Linux系统
下通过`kill -3`命令发送进程退出信号,也能拿到dump文件.

jmap的作用并不仅仅是为了获取dump文件,它还可以查询`finalize`执行队列,java堆和永久代的详细信息,如空间使用率,当前使用的是哪种收集器.

和jinfo命令一样,jmap有不少功能是在windows平台下受限的,除了生成dump文件`-dump`选项和用于查看每个类的实例,空间占用统计的`-histo`选项所有系统操作系统都提供之外,其余选项只能在Linux/Solaris下使用.

### jmap命令格式
```
jmap [ option ] vmid
```

#### jmap工具主要选项
* `-dump`: 生成java堆转储快照.格式为:`-dump:[live,]format=b,file=<filename>`.live表示只dump存活对象
* `-finalizerinfo`: 显示在`F-Queue`中等待`Finalizer`线程执行`finalize`方法的对象.
* `-heap`: 显示java堆的详细信息,使用哪种回收器,参数配置,分代状况.
* `-histo`: 显示堆中对象统计信息,包括类,实例数量和合计容量
* `-permstat`: 以`ClassLoader`为统计口径显示永久代内存状态.
* `-F`: 当虚拟机进程对`-dump`选项没有响应时,可使用这个选项强制生成dump快照

## jstack
java堆栈跟踪工具. `jstack`命令用于生成虚拟机当前时刻的线程快照.线程快照就是当前虚拟机内每一条线程正在执行的方法堆栈的集合,生成线程快照的主要目的是定位线程出现长时间停顿的原因,如[线程间死锁](),[死循环](),请求外部资源
导致长时间等待.

jstack命令格式
```
jstack [ option ] vmid
```
option值：
* `-F`: 当正常输出的请求不被响应时,强制说出线程堆栈
* `-l`: 除堆栈外,显示关于锁的附加信息
* `-m`: 如果调用本地方法的话,可以显示c/c++的堆栈

当对线程堆栈分析时，首先查找`BLOCKED`, 找到锁住的线程。



# 日志分析

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

第一行数据分析
```
nioEventLoopGroup-2461-1 表示的是进程名字
#4955
prio=10
os_prio=0
nid: 线程ID的16进制表示(可以通过`top -H`查看pid)
tid:
runnable
[0x00007fd7374bc000]`
```

线程堆栈信息
```
java.lang.Thread.State 线程状态
locked` 锁住的资源,分别锁住了  <0x00000000e673cf38>, <0x00000000e673cd30>, <0x00000000e673cc58>
```

### java.lang.Thread.State 线程状态
* `Runnable ` : 线程具备所有运行条件，在运行队列中准备操作系统的调度，或者正在运行
* `waiting for monitor entry` :  在等待进入一个临界区,所以它在`Entry Set`队列中等待.
> 此时线程状态一般都是 `Blocked`:如果大量线程在`waiting for monitor entry`, 可能是一个全局锁阻塞住了大量线程.如果短时间内打印的 `thread dump` 文件反映,随着时间流逝,`waiting for monitor entry`的线程越来越多,没有减少的趋势,可能意味着某些线程在临界区里呆的时间太长了,以至于越来越多新线程迟迟无法进入临界区.

* `waiting on condition` : 说明它在等待另一个条件的发生,来把自己唤醒,或者干脆它是调用了 `sleep(N)`.
> 如果大量线程在`waiting on condition`：可能是它们又跑去获取第三方资源,尤其是第三方网络资源,迟迟获取不到`Response`,导致大量线程进入等待状态.所以如果你发现有大量的线程都处在 `Wait on condition`,从线程堆栈看,正等待网络读写,这可能是一个网络瓶颈的征兆,因为网络阻塞导致线程无法执行.  此时线程状态大致为以下几种：
	1. `java.lang.Thread.State: WAITING (parking)`：一直等那个条件发生；
	2. `java.lang.Thread.State: TIMED_WAITING` (`parking`或`sleeping`)：定时的,那个条件不到来,也将定时唤醒自己.

	

* `in Object.wait()` : 说明它获得了监视器之后,又调用了 `java.lang.Object.wait()` 方法.	
> 每个 Monitor在某个时刻,只能被一个线程拥有,该线程就是 `Active Thread`,而其它线程都是 `Waiting Thread`,分别在两个队列 `Entry Set`和 `Wait Set`里面等候.在 `Entry Set`中等待的线程状态是 `Waiting for monitor entry`,而在 `Wait Set`中等待的线程状态是 `in Object.wait()`.当线程获得了 `Monitor`,如果发现线程继续运行的条件没有满足,它则调用对象(一般就是被 `synchronized` 的对象)的 `wait()` 方法,放弃了 `Monitor`,进入 `Wait Set`队列. 此时线程状态大致为以下几种：
	1. `java.lang.Thread.State: TIMED_WAITING (on object monitor)`; 
	2. `java.lang.Thread.State: WAITING (on object monitor)`;

	
## gc log
我使用`-Xmx2048m -Xms2048M  -Xmn1048m`的内存分配方式启动一个JVM,下面是其中一段GC 日志
```
{Heap before GC invocations=196 (full 0):
 par new generation   total 873856K, used 699148K [0x000000077ae00000, 0x00000007bae00000, 0x00000007bae00000)
  eden space 699136K, 100% used [0x000000077ae00000, 0x00000007a58c0000, 0x00000007a58c0000)
  from space 174720K,   0% used [0x00000007a58c0000, 0x00000007a58c30d8, 0x00000007b0360000)
  to   space 174720K,   0% used [0x00000007b0360000, 0x00000007b0360000, 0x00000007bae00000)
 concurrent mark-sweep generation total 1048576K, used 3377K [0x00000007bae00000, 0x00000007fae00000, 0x00000007fae00000)
 concurrent-mark-sweep perm gen total 21248K, used 9252K [0x00000007fae00000, 0x00000007fc2c0000, 0x0000000800000000)
670.529: [GC670.529: [ParNew: 699148K->10K(873856K), 0.0047350 secs] 702525K->3387K(1922432K), 0.0048480 secs] [Times: user=0.03 sys=0.00, real=0.00 secs]
Heap after GC invocations=197 (full 0):
 par new generation   total 873856K, used 10K [0x000000077ae00000, 0x00000007bae00000, 0x00000007bae00000)
  eden space 699136K,   0% used [0x000000077ae00000, 0x000000077ae00000, 0x00000007a58c0000)
  from space 174720K,   0% used [0x00000007b0360000, 0x00000007b03628d8, 0x00000007bae00000)
  to   space 174720K,   0% used [0x00000007a58c0000, 0x00000007a58c0000, 0x00000007b0360000)
 concurrent mark-sweep generation total 1048576K, used 3377K [0x00000007bae00000, 0x00000007fae00000, 0x00000007fae00000)
 concurrent-mark-sweep perm gen total 21248K, used 9252K [0x00000007fae00000, 0x00000007fc2c0000, 0x0000000800000000)
}
```


1. `Heap before GC invocations=196 (full 0)`:
    这一行表示在调用第196GC, 第0次full GC之前的jvm内存分配情况.
2. `par new generation   total 873856K, used 699148K [0x000000077ae00000, 0x00000007bae00000, 0x00000007bae00000)`:
    这一行的意思是新生代总共分配了873856K内存,使用了699148K的内存.
3. `eden space 699136K, 100% used [0x000000077ae00000, 0x00000007a58c0000, 0x00000007a58c0000)`:
    新生代的eden区分配了699136K内存,并且使用了100%
4. `from space 174720K,   0% used [0x00000007a58c0000, 0x00000007a58c30d8, 0x00000007b0360000)`:
    survivor1区分配了174720K内存,没有使用
6. `to   space 174720K,   0% used [0x00000007b0360000, 0x00000007b0360000, 0x00000007bae00000)`:
    survivor2区分配了174720K内存,没有使用
5. `concurrent mark-sweep generation total 1048576K, used 3377K [0x00000007bae00000, 0x00000007fae00000, 0x00000007fae00000)`
    采用并发标记清除算法对新生代共分配1048576K, 其中有3377K大小在使用着
7. `concurrent-mark-sweep perm gen total 21248K, used 9252K [0x00000007fae00000, 0x00000007fc2c0000, 0x0000000800000000)`:
    采用并发标记清除算法对永久代共分配21248K大小内存,使用了9252K.
8. `670.529: [GC670.529: [ParNew: 699148K->10K(873856K), 0.0047350 secs] 702525K->3387K(1922432K), 0.0048480 secs] [Times:`: user=0.03 sys=0.00, real=0.00 secs]`:
    开始gc,ParNew垃圾收集器的新生代经过0.0047350秒后,将699148K内存进行垃圾收集, gc后有10K内存在使用.
9. `Heap after GC invocations=197 (full 0)`:
    在对堆进行197次gc后的内存分配情况：
10. `par new generation   total 873856K, used 10K [0x000000077ae00000, 0x00000007bae00000, 0x00000007bae00000)`:
    新生代分配了873856K大小，使用了10K
11. `eden space 699136K,   0% used [0x000000077ae00000, 0x000000077ae00000, 0x00000007a58c0000)`:
    新生代eden区分配了699136K大小,使用了0k
12. `from space 174720K,   0% used [0x00000007b0360000, 0x00000007b03628d8, 0x00000007bae00000)`:
    新生代的survivor1区分配了174720K,使用了0k
13. `to space 174720K,   0% used [0x00000007a58c0000, 0x00000007a58c0000, 0x00000007b0360000)`:
    新生代的survivor2区分配了174720K,使用了0k
14. `concurrent mark-sweep generation total 1048576K, used 3377K [0x00000007bae00000, 0x00000007fae00000, 0x00000007fae00000)`:
15. `concurrent-mark-sweep perm gen total 21248K, used 9252K [0x00000007fae00000, 0x00000007fc2c0000, 0x0000000800000000)`:



