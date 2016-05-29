category: JVM
date: 2014-10-04
title: jcmd
---
jcmd是在JDK7 中新添加的一款工具(内嵌了对jps, jmap等功能支持, 而且Oracle官网也推荐使用jcmd替代jmap)
```bash
➜  test jcmd 2028 help
2028:
The following commands are available:
JFR.stop
JFR.start
JFR.dump
JFR.check
VM.native_memory
VM.check_commercial_features
VM.unlock_commercial_features
ManagementAgent.stop
ManagementAgent.start_local
ManagementAgent.start
Thread.print
GC.class_stats
GC.class_histogram
GC.heap_dump
GC.run_finalization
GC.run
VM.uptime
VM.flags
VM.system_properties
VM.command_line
VM.version
help
```
上面罗列了对2028号进程所支持的所有的jcmd操作.

下面我们看一下2028进程的线程信息
```bash
➜  test jcmd 2028 Thread.print
2028:
2016-05-29 18:35:12
Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.5-b02 mixed mode):

"DestroyJavaVM" #22 prio=5 os_prio=31 tid=0x00007febd3cfe800 nid=0xf03 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"http-nio-8080-Acceptor-0" #20 daemon prio=5 os_prio=31 tid=0x00007febd3354000 nid=0x5d03 runnable [0x000000012a739000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.ServerSocketChannelImpl.accept0(Native Method)
	at sun.nio.ch.ServerSocketChannelImpl.accept(ServerSocketChannelImpl.java:241)
	- locked <0x0000000760b89690> (a java.lang.Object)
	at org.apache.tomcat.util.net.NioEndpoint$Acceptor.run(NioEndpoint.java:682)
	at java.lang.Thread.run(Thread.java:745)

"http-nio-8080-ClientPoller-1" #19 daemon prio=5 os_prio=31 tid=0x00007febd3353800 nid=0x5b03 runnable [0x000000012a1b4000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:202)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:103)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x0000000760b89bb0> (a sun.nio.ch.Util$2)
	- locked <0x0000000760b89bc0> (a java.util.Collections$UnmodifiableSet)
	- locked <0x0000000760b89b60> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at org.apache.tomcat.util.net.NioEndpoint$Poller.run(NioEndpoint.java:1034)
	at java.lang.Thread.run(Thread.java:745)

"http-nio-8080-ClientPoller-0" #18 daemon prio=5 os_prio=31 tid=0x00007febd33f2800 nid=0x5903 runnable [0x0000000129e7e000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:202)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:103)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x0000000760b8a298> (a sun.nio.ch.Util$2)
	- locked <0x0000000760b8a2a8> (a java.util.Collections$UnmodifiableSet)
	- locked <0x0000000760b8a248> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at org.apache.tomcat.util.net.NioEndpoint$Poller.run(NioEndpoint.java:1034)
	at java.lang.Thread.run(Thread.java:745)

"NioBlockingSelector.BlockPoller-1" #17 daemon prio=5 os_prio=31 tid=0x00007febd585d000 nid=0x5703 runnable [0x0000000128733000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:202)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:103)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x0000000760b8a948> (a sun.nio.ch.Util$2)
	- locked <0x0000000760b8a958> (a java.util.Collections$UnmodifiableSet)
	- locked <0x0000000760b8a8f8> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at org.apache.tomcat.util.net.NioBlockingSelector$BlockPoller.run(NioBlockingSelector.java:342)

"container-0" #16 prio=5 os_prio=31 tid=0x00007febd34ac000 nid=0x5503 waiting on condition [0x0000000129d7b000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at org.apache.catalina.core.StandardServer.await(StandardServer.java:425)
	at org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer$1.run(TomcatEmbeddedServletContainer.java:140)

"ContainerBackgroundProcessor[StandardEngine[Tomcat].StandardHost[localhost].StandardContext[]]" #15 daemon prio=5 os_prio=31 tid=0x00007febd34b3000 nid=0x5303 waiting on condition [0x000000012815f000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at org.apache.catalina.core.ContainerBase$ContainerBackgroundProcessor.run(ContainerBase.java:1344)
	at java.lang.Thread.run(Thread.java:745)

"Attach Listener" #12 daemon prio=9 os_prio=31 tid=0x00007febd4a6e800 nid=0x4f03 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Monitor Ctrl-Break" #9 daemon prio=5 os_prio=31 tid=0x00007febd48a2000 nid=0x4b03 runnable [0x0000000127e69000]
   java.lang.Thread.State: RUNNABLE
	at java.net.PlainSocketImpl.socketAccept(Native Method)
	at java.net.AbstractPlainSocketImpl.accept(AbstractPlainSocketImpl.java:404)
	at java.net.ServerSocket.implAccept(ServerSocket.java:545)
	at java.net.ServerSocket.accept(ServerSocket.java:513)
	at com.intellij.rt.execution.application.AppMain$1.run(AppMain.java:90)
	at java.lang.Thread.run(Thread.java:745)

"Service Thread" #8 daemon prio=9 os_prio=31 tid=0x00007febd303a800 nid=0x4703 runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C1 CompilerThread2" #7 daemon prio=9 os_prio=31 tid=0x00007febd303a000 nid=0x4503 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread1" #6 daemon prio=9 os_prio=31 tid=0x00007febd4810000 nid=0x4303 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread0" #5 daemon prio=9 os_prio=31 tid=0x00007febd3021000 nid=0x4103 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Signal Dispatcher" #4 daemon prio=9 os_prio=31 tid=0x00007febd3020800 nid=0x3c0f runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Finalizer" #3 daemon prio=8 os_prio=31 tid=0x00007febd3009800 nid=0x2f03 in Object.wait() [0x0000000125c53000]
   java.lang.Thread.State: WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:142)
	- locked <0x000000076005b248> (a java.lang.ref.ReferenceQueue$Lock)
	at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:158)
	at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)

"Reference Handler" #2 daemon prio=10 os_prio=31 tid=0x00007febd4033800 nid=0x2d03 in Object.wait() [0x0000000125b50000]
   java.lang.Thread.State: WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	at java.lang.Object.wait(Object.java:502)
	at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:157)
	- locked <0x0000000760057ca0> (a java.lang.ref.Reference$Lock)

"VM Thread" os_prio=31 tid=0x00007febd382e800 nid=0x2b03 runnable

"GC task thread#0 (ParallelGC)" os_prio=31 tid=0x00007febd4012800 nid=0x2203 runnable

"GC task thread#1 (ParallelGC)" os_prio=31 tid=0x00007febd3806800 nid=0x2403 runnable

"GC task thread#2 (ParallelGC)" os_prio=31 tid=0x00007febd3807000 nid=0x2603 runnable

"GC task thread#3 (ParallelGC)" os_prio=31 tid=0x00007febd3808000 nid=0x2803 runnable

"VM Periodic Task Thread" os_prio=31 tid=0x00007febd4813000 nid=0x4903 waiting on condition

JNI global references: 238

➜  test
```
