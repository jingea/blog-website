category: JVM
date: 2016-07-04
title: hs_err_pidXXX.log 文件分析
---
当虚拟机发生致命错误时,会生成一个 hs_err_pidXXX.log的文件. 这个文件包括
* 触发致命错误的操作异常或者信号；
* 版本和配置信息；
* 触发致命异常的线程详细信息和线程栈；
* 当前运行的线程列表和它们的状态；
* 堆的总括信息；
* 加载的本地库；
* 命令行参数；
* 环境变量；
* 操作系统CPU的详细信息。

下面我们一段一段看一下这个日志文件
```java
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x00007fdf1e19eed3, pid=9850, tid=140593500931840
#
# JRE version: Java(TM) SE Runtime Environment (8.0_60-b27) (build 1.8.0_60-b27)
# Java VM: Java HotSpot(TM) 64-Bit Server VM (25.60-b23 mixed mode linux-amd64 compressed oops)
# Problematic frame:
# C  [libcollector.so+0x45ed3]
#
# Failed to write core dump. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# If you would like to submit a bug report, please visit:
#   http://bugreport.java.com/bugreport/crash.jsp
#
```
上面给出了JVM发生问题的概括信息, JVM捕获到一个

```java
---------------  T H R E A D  ---------------

Current thread (0x00007fdee8032000):  JavaThread "server-log-thread-3" [_thread_in_Java, id=10583, stack(0x00007fde799e4000,0x00007fde79ae5000)]

// si_signo : 
// 11 (SIGSEGV) : 当一个进程执行了一个无效的内存引用，或发生段错误时发送给它的信号 -> 在Internal exceptions中, 有个类找不到
siginfo: si_signo: 11 (SIGSEGV), si_code: 128 (SI_KERNEL), si_addr: 0x0000000000000000

Registers:
RAX=0x0000000000000004, RBX=0x00007fde79ae0b50, RCX=0x0015442800007f6f, RDX=0x0015442800007fef
RSP=0x00007fde79ae0720, RBP=0x00007fde79ae1040, RSI=0x00007fde79ae0b58, RDI=0x00000000e8307681
R8 =0x0000000000000004, R9 =0x0000000000000000, R10=0x0000000000000004, R11=0x00000000e8306bd3
R12=0x00007fde79ae28b0, R13=0x0000000000000000, R14=0x0000000000000004, R15=0x0000000000000000
RIP=0x00007fdf1e19eed3, EFLAGS=0x0000000000010202, CSGSFS=0x0000000000000033, ERR=0x0000000000000000
  TRAPNO=0x000000000000000d

Top of Stack: (sp=0x00007fde79ae0720)
0x00007fde79ae0720:   0000000000000000 0000000000000004
0x00007fde79ae0730:   0000000579ae0970 0000000000000000
0x00007fde79ae0740:   00007fde79ae0798 0000000000000004
0x00007fde79ae0750:   0000000000000000 0000000000000005
0x00007fde79ae0760:   00007fde79ae0b50 0000000000000160
0x00007fde79ae0770:   00007fdf06b7dbdf 0000000f00000000
0x00007fde79ae0780:   0000000000000000 00007fdf00000000
0x00007fde79ae0790:   0000000000000000 0000147306b7dd79
0x00007fde79ae07a0:   0000000000000000 0000000000000000
0x00007fde79ae07b0:   00007fde79ae0868 0000000006b7e4c1
0x00007fde79ae07c0:   00007fdf06b7ed89 00007fdf06b7f240
0x00007fde79ae07d0:   00007fdf06b7f246 00007fdf06b7f27f
0x00007fde79ae07e0:   00007fdf06b7f298 00007fdf06b7f29e
0x00007fde79ae07f0:   00000000e8306bd3 00000000e8306cda
0x00007fde79ae0800:   00000000e8306d22 00000000e83070af
0x00007fde79ae0810:   00000000e8307212 00000000e8307214
0x00007fde79ae0820:   00000000e8307245 00000000e8307278
0x00007fde79ae0830:   00000000e83072a8 00000000e83072e0
0x00007fde79ae0840:   00000000e8307397 00000000e83073c7
0x00007fde79ae0850:   00000000e8307602 00000000e83076ce
0x00007fde79ae0860:   ffffffffffffffff 00007fdf0690ed42
0x00007fde79ae0870:   00007fdf0690ed79 00007fdf0690ed92
0x00007fde79ae0880:   00007fdf0690ed9f 00007fdf0690edc2
0x00007fde79ae0890:   00007fdf0690edd3 00007fdf0690ee1d
0x00007fde79ae08a0:   00007fdf0690ee5f 00007fdf0690ee92
0x00007fde79ae08b0:   00007fdf0690ee97 00007fdf0690ee9e
0x00007fde79ae08c0:   00007fdf0690eea6 00007fdf0690eede
0x00007fde79ae08d0:   00007fdf0690ef1d 00007fdf0690ef45
0x00007fde79ae08e0:   00007fdf0690ef4d 00007fdf0690ef84
0x00007fde79ae08f0:   00007fdf0690ef9d 00007fdf0690efaa
0x00007fde79ae0900:   00007fdf0690efcd 00007fdf0690efde
0x00007fde79ae0910:   00007fdf0690f01c 00007fdf0690f05e 

Instructions: (pc=0x00007fdf1e19eed3)
0x00007fdf1e19eeb3:   74 3c 48 8d 73 08 48 8b 4b 10 48 8b 53 08 83 f8
0x00007fdf1e19eec3:   04 0f 85 81 00 00 00 48 3b d1 0f 82 c0 16 00 00
0x00007fdf1e19eed3:   48 8b 02 48 3b c2 0f 82 b4 16 00 00 48 3b 43 18
0x00007fdf1e19eee3:   0f 87 aa 16 00 00 48 89 06 e9 99 11 00 00 48 8b 

Register to memory mapping:

RAX=0x0000000000000004 is an unknown value
RBX=0x00007fde79ae0b50 is pointing into the stack for thread: 0x00007fdee8032000
RCX=0x0015442800007f6f is an unknown value
RDX=0x0015442800007fef is an unknown value
RSP=0x00007fde79ae0720 is pointing into the stack for thread: 0x00007fdee8032000
RBP=0x00007fde79ae1040 is pointing into the stack for thread: 0x00007fdee8032000
RSI=0x00007fde79ae0b58 is pointing into the stack for thread: 0x00007fdee8032000
RDI=0x00000000e8307681 is an unknown value
R8 =0x0000000000000004 is an unknown value
R9 =0x0000000000000000 is an unknown value
R10=0x0000000000000004 is an unknown value
R11=0x00000000e8306bd3 is an unknown value
R12=0x00007fde79ae28b0 is pointing into the stack for thread: 0x00007fdee8032000
R13=0x0000000000000000 is an unknown value
R14=0x0000000000000004 is an unknown value
R15=0x0000000000000000 is an unknown value


Stack: [0x00007fde799e4000,0x00007fde79ae5000],  sp=0x00007fde79ae0720,  free space=1009k
Native frames: (J=compiled Java code, j=interpreted, Vv=VM code, C=native code)
C  [libcollector.so+0x45ed3]
C  [libcollector.so+0x421e7]  __collector_get_frame_info+0x407
C  [libcollector.so+0x4f023]
C  [libcollector.so+0x2fb4a]  __collector_ext_profile_handler+0x13a
C  [libcollector.so+0x26712]
C  [libpthread.so.0+0xf7e0]
```


```java
---------------  P R O C E S S  ---------------

Java Threads: ( => current thread )
  0x00007fdea8296000 JavaThread "ForkJoinPool.commonPool-worker-3" daemon [_thread_blocked, id=18060, stack(0x00007fde771c4000,0x00007fde772c5000)]
  0x00007fdea8298800 JavaThread "ForkJoinPool.commonPool-worker-0" daemon [_thread_blocked, id=17840, stack(0x00007fde79be6000,0x00007fde79ce7000)]
  0x00007fdeb006b800 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=17794, stack(0x00007fde76fc2000,0x00007fde770c3000)]
  0x00007fdeac167800 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=17793, stack(0x00007fde76ec1000,0x00007fde76fc2000)]
  0x0000000000c1c000 JavaThread "ForkJoinPool.commonPool-worker-2" daemon [_thread_blocked, id=17347, stack(0x00007fde76bbe000,0x00007fde76cbf000)]
  0x00007fdeac166800 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=17175, stack(0x00007fde78edb000,0x00007fde78fdc000)]
  0x00007fdeac166000 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=17174, stack(0x00007fde78cd9000,0x00007fde78dda000)]
  0x00007fdeac14a000 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=17173, stack(0x00007fde795e0000,0x00007fde796e1000)]
  0x00007fdeb0109000 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=17172, stack(0x00007fde772c5000,0x00007fde773c6000)]
  0x00007fdeac14e800 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=16163, stack(0x00007fde76cbf000,0x00007fde76dc0000)]
  0x00007fdee003d800 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=16162, stack(0x00007fde777c9000,0x00007fde778ca000)]
  0x00007fde8c020000 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=16161, stack(0x00007fde767bd000,0x00007fde768be000)]
  0x00007fdeac150000 JavaThread "MySQL Statement Cancellation Timer" daemon [_thread_blocked, id=10490, stack(0x00007fde76dc0000,0x00007fde76ec1000)]
  0x00007fdee80ef800 JavaThread "pool-2-thread-10" [_thread_blocked, id=10777, stack(0x00007fde77dca000,0x00007fde77ecb000)]
  0x00007fdee80ed800 JavaThread "pool-2-thread-9" [_thread_blocked, id=10776, stack(0x00007fde77ecb000,0x00007fde77fcc000)]
  0x00007fdee80ec000 JavaThread "pool-2-thread-8" [_thread_blocked, id=10775, stack(0x00007fde77fcc000,0x00007fde780cd000)]
  0x00007fdee80e8800 JavaThread "pool-2-thread-7" [_thread_blocked, id=10774, stack(0x00007fde780cd000,0x00007fde781ce000)]
  0x00007fdee80e9800 JavaThread "pool-2-thread-6" [_thread_blocked, id=10773, stack(0x00007fde781ce000,0x00007fde782cf000)]
  0x00007fdee813b000 JavaThread "pool-2-thread-5" [_thread_blocked, id=10772, stack(0x00007fde782cf000,0x00007fde783d0000)]
  0x00007fdee8139000 JavaThread "pool-2-thread-4" [_thread_blocked, id=10771, stack(0x00007fde783d0000,0x00007fde784d1000)]
  0x00007fdee8137800 JavaThread "pool-2-thread-3" [_thread_blocked, id=10770, stack(0x00007fde784d1000,0x00007fde785d2000)]
  0x00007fdee8135800 JavaThread "pool-2-thread-2" [_thread_blocked, id=10769, stack(0x00007fde785d2000,0x00007fde786d3000)]
  0x00007fdee8134000 JavaThread "pool-2-thread-1" [_thread_blocked, id=10768, stack(0x00007fde786d3000,0x00007fde787d4000)]
  0x00007fdea400e000 JavaThread "chatpool-1-thread-5" [_thread_blocked, id=10725, stack(0x00007fde787d4000,0x00007fde788d5000)]
  0x00007fdea400c800 JavaThread "chatpool-1-thread-4" [_thread_blocked, id=10724, stack(0x00007fde788d5000,0x00007fde789d6000)]
  0x00007fdea400a800 JavaThread "chatpool-1-thread-3" [_thread_blocked, id=10723, stack(0x00007fde789d6000,0x00007fde78ad7000)]
  0x00007fdea4009000 JavaThread "chatpool-1-thread-2" [_thread_blocked, id=10722, stack(0x00007fde78ad7000,0x00007fde78bd8000)]
  0x00007fdea4007000 JavaThread "chatpool-1-thread-1" [_thread_blocked, id=10721, stack(0x00007fde78bd8000,0x00007fde78cd9000)]
  0x00007fdee8032800 JavaThread "server-log-thread-4" [_thread_blocked, id=10586, stack(0x00007fde798e3000,0x00007fde799e4000)]
=>0x00007fdee8032000 JavaThread "server-log-thread-3" [_thread_in_Java, id=10583, stack(0x00007fde799e4000,0x00007fde79ae5000)]
  0x00007fdee802e800 JavaThread "server-log-thread-2" [_thread_blocked, id=10581, stack(0x00007fde7a2eb000,0x00007fde7a3ec000)]
  0x00007fdef0016800 JavaThread "nioEventLoopGroup-3-3" [_thread_in_native, id=10217, stack(0x00007fde7b7fa000,0x00007fde7b8fb000)]
  0x00007fdef0015000 JavaThread "nioEventLoopGroup-3-2" [_thread_in_native, id=10129, stack(0x00007fde79ae5000,0x00007fde79be6000)]
  0x00007fdee001f800 JavaThread "com.mchange.v2.async.ThreadPoolAsynchronousRunner$PoolThread-#2" daemon [_thread_blocked, id=10105, stack(0x00007fde79ce7000,0x00007fde79de8000)]
  0x00007fdee001d800 JavaThread "com.mchange.v2.async.ThreadPoolAsynchronousRunner$PoolThread-#1" daemon [_thread_blocked, id=10104, stack(0x00007fde79de8000,0x00007fde79ee9000)]
  0x00007fdee001c000 JavaThread "com.mchange.v2.async.ThreadPoolAsynchronousRunner$PoolThread-#0" daemon [_thread_blocked, id=10103, stack(0x00007fde79ee9000,0x00007fde79fea000)]
  0x00007fdee001a000 JavaThread "Timer-1" daemon [_thread_blocked, id=10102, stack(0x00007fde79fea000,0x00007fde7a0eb000)]
  0x00007fde80005800 JavaThread "server-log-thread-1" [_thread_blocked, id=10101, stack(0x00007fde7b5f8000,0x00007fde7b6f9000)]
  0x00007fdef0013800 JavaThread "nioEventLoopGroup-3-1" [_thread_in_native, id=10019, stack(0x00007fde7b6f9000,0x00007fde7b7fa000)]
  0x00007fdefc0e9800 JavaThread "org.eclipse.jetty.server.session.HashSessionManager@185b549Timer" daemon [_thread_blocked, id=9928, stack(0x00007fde7a5ec000,0x00007fde7a6ed000)]
  0x00007fdefc0ca000 JavaThread "qtp2111018100-86" [_thread_blocked, id=9927, stack(0x00007fde7a6ed000,0x00007fde7a7ee000)]
  0x00007fdefc0c8800 JavaThread "qtp2111018100-85" [_thread_blocked, id=9926, stack(0x00007fde7a7ee000,0x00007fde7a8ef000)]
  0x00007fdefc0c6800 JavaThread "qtp2111018100-84" [_thread_blocked, id=9925, stack(0x00007fde7a8ef000,0x00007fde7a9f0000)]
  0x00007fdefc0c5000 JavaThread "qtp2111018100-83" [_thread_blocked, id=9924, stack(0x00007fde7a9f0000,0x00007fde7aaf1000)]
  0x00007fdefc0c3000 JavaThread "qtp2111018100-82" [_thread_blocked, id=9923, stack(0x00007fde7aaf1000,0x00007fde7abf2000)]
  0x00007fdefc0c1000 JavaThread "qtp2111018100-81-acceptor-0@455b9a0-ServerConnector@5f3dd7e0{HTTP/1.1,[http/1.1]}{0.0.0.0:18080}" [_thread_in_native, id=9922, stack(0x00007fde7abf2000,0x00007fde7acf3000)]
  0x00007fdefc0bf800 JavaThread "qtp2111018100-80" [_thread_in_native, id=9921, stack(0x00007fde7acf3000,0x00007fde7adf4000)]
  0x00007fdefc0be800 JavaThread "qtp2111018100-79" [_thread_in_native, id=9920, stack(0x00007fde7adf4000,0x00007fde7aef5000)]
  0x00007fdf15f6d800 JavaThread "webthread" [_thread_blocked, id=9918, stack(0x00007fde7aef5000,0x00007fde7aff6000)]
  0x00007fdf15f64800 JavaThread "nioEventLoopGroup-2-1" [_thread_in_native, id=9917, stack(0x00007fde7b1f6000,0x00007fde7b2f7000)]
  0x00007fdf15f11000 JavaThread "GameLogic::getInstance" [_thread_blocked, id=9915, stack(0x00007fde7b2f7000,0x00007fde7b3f8000)]
  0x00007fdf15b06000 JavaThread "GroupServerScheduler_QuartzSchedulerThread" [_thread_blocked, id=9909, stack(0x00007fde7bafb000,0x00007fde7bbfc000)]
  0x00007fdf15aeb000 JavaThread "GroupServerScheduler_Worker-32" [_thread_blocked, id=9908, stack(0x00007fde7bbfc000,0x00007fde7bcfd000)]
  0x00007fdf15ae9000 JavaThread "GroupServerScheduler_Worker-31" [_thread_in_native, id=9907, stack(0x00007fde7bcfd000,0x00007fde7bdfe000)]
  0x00007fdf15ae7000 JavaThread "GroupServerScheduler_Worker-30" [_thread_in_native, id=9906, stack(0x00007fde7bdfe000,0x00007fde7beff000)]
  0x00007fdf15ae5000 JavaThread "GroupServerScheduler_Worker-29" [_thread_in_Java, id=9905, stack(0x00007fde7beff000,0x00007fde7c000000)]
  0x00007fdf15ae3000 JavaThread "GroupServerScheduler_Worker-28" [_thread_in_native, id=9904, stack(0x00007fded8054000,0x00007fded8155000)]
  0x00007fdf15ae0800 JavaThread "GroupServerScheduler_Worker-27" [_thread_blocked, id=9903, stack(0x00007fded8155000,0x00007fded8256000)]
  0x00007fdf15ade800 JavaThread "GroupServerScheduler_Worker-26" [_thread_blocked, id=9902, stack(0x00007fded8256000,0x00007fded8357000)]
  0x00007fdf15adc800 JavaThread "GroupServerScheduler_Worker-25" [_thread_blocked, id=9901, stack(0x00007fded8357000,0x00007fded8458000)]
  0x00007fdf15ada800 JavaThread "GroupServerScheduler_Worker-24" [_thread_blocked, id=9900, stack(0x00007fded8458000,0x00007fded8559000)]
  0x00007fdf15ad8800 JavaThread "GroupServerScheduler_Worker-23" [_thread_in_native, id=9899, stack(0x00007fded8559000,0x00007fded865a000)]
  0x00007fdf15ad6800 JavaThread "GroupServerScheduler_Worker-22" [_thread_in_native, id=9898, stack(0x00007fded865a000,0x00007fded875b000)]
  0x00007fdf15ad4800 JavaThread "GroupServerScheduler_Worker-21" [_thread_in_native, id=9897, stack(0x00007fded875b000,0x00007fded885c000)]
  0x00007fdf15ad2800 JavaThread "GroupServerScheduler_Worker-20" [_thread_blocked, id=9896, stack(0x00007fded885c000,0x00007fded895d000)]
  0x00007fdf15ad0800 JavaThread "GroupServerScheduler_Worker-19" [_thread_in_native, id=9895, stack(0x00007fded895d000,0x00007fded8a5e000)]
  0x00007fdf15ace800 JavaThread "GroupServerScheduler_Worker-18" [_thread_blocked, id=9894, stack(0x00007fded8a5e000,0x00007fded8b5f000)]
  0x00007fdf15acc800 JavaThread "GroupServerScheduler_Worker-17" [_thread_blocked, id=9893, stack(0x00007fded8b5f000,0x00007fded8c60000)]
  0x00007fdf15aca800 JavaThread "GroupServerScheduler_Worker-16" [_thread_in_native, id=9892, stack(0x00007fded8c60000,0x00007fded8d61000)]
  0x00007fdf15ac8800 JavaThread "GroupServerScheduler_Worker-15" [_thread_in_native, id=9891, stack(0x00007fded8d61000,0x00007fded8e62000)]
  0x00007fdf15ac6800 JavaThread "GroupServerScheduler_Worker-14" [_thread_in_native, id=9890, stack(0x00007fded8e62000,0x00007fded8f63000)]
  0x00007fdf15ac4000 JavaThread "GroupServerScheduler_Worker-13" [_thread_in_native, id=9889, stack(0x00007fded8f63000,0x00007fded9064000)]
  0x00007fdf15ac2000 JavaThread "GroupServerScheduler_Worker-12" [_thread_blocked, id=9888, stack(0x00007fded9064000,0x00007fded9165000)]
  0x00007fdf15ac0000 JavaThread "GroupServerScheduler_Worker-11" [_thread_blocked, id=9887, stack(0x00007fded9165000,0x00007fded9266000)]
  0x00007fdf15abe000 JavaThread "GroupServerScheduler_Worker-10" [_thread_blocked, id=9886, stack(0x00007fded9266000,0x00007fded9367000)]
  0x00007fdf15abc800 JavaThread "GroupServerScheduler_Worker-9" [_thread_in_native, id=9885, stack(0x00007fded9367000,0x00007fded9468000)]
  0x00007fdf15aba800 JavaThread "GroupServerScheduler_Worker-8" [_thread_in_native, id=9884, stack(0x00007fded9468000,0x00007fded9569000)]
  0x00007fdf15ab8800 JavaThread "GroupServerScheduler_Worker-7" [_thread_blocked, id=9883, stack(0x00007fded9569000,0x00007fded966a000)]
  0x00007fdf15ab6000 JavaThread "GroupServerScheduler_Worker-6" [_thread_blocked, id=9882, stack(0x00007fded966a000,0x00007fded976b000)]
  0x00007fdf15ab5000 JavaThread "GroupServerScheduler_Worker-5" [_thread_in_native, id=9881, stack(0x00007fded976b000,0x00007fded986c000)]
  0x00007fdf15aa5000 JavaThread "GroupServerScheduler_Worker-4" [_thread_blocked, id=9880, stack(0x00007fded986c000,0x00007fded996d000)]
  0x00007fdf15aa3000 JavaThread "GroupServerScheduler_Worker-3" [_thread_blocked, id=9879, stack(0x00007fded996d000,0x00007fded9a6e000)]
  0x00007fdf15aa1800 JavaThread "GroupServerScheduler_Worker-2" [_thread_in_native, id=9878, stack(0x00007fded9a6e000,0x00007fded9b6f000)]
  0x00007fdf15aa1000 JavaThread "GroupServerScheduler_Worker-1" [_thread_blocked, id=9877, stack(0x00007fdeecbf2000,0x00007fdeeccf3000)]
  0x00007fdf14bf3800 JavaThread "commons-pool-EvictionTimer" daemon [_thread_blocked, id=9874, stack(0x00007fdeecaf1000,0x00007fdeecbf2000)]
  0x00007fdf14b70000 JavaThread "Thread-5" [_thread_blocked, id=9872, stack(0x00007fdeeccf3000,0x00007fdeecdf4000)]
  0x00007fdf14956000 JavaThread "com.mchange.v2.async.ThreadPoolAsynchronousRunner$PoolThread-#2" daemon [_thread_blocked, id=9871, stack(0x00007fdeed207000,0x00007fdeed308000)]
  0x00007fdf14954000 JavaThread "com.mchange.v2.async.ThreadPoolAsynchronousRunner$PoolThread-#1" daemon [_thread_blocked, id=9870, stack(0x00007fdeed308000,0x00007fdeed409000)]
  0x00007fdf14979000 JavaThread "com.mchange.v2.async.ThreadPoolAsynchronousRunner$PoolThread-#0" daemon [_thread_blocked, id=9869, stack(0x00007fdeed409000,0x00007fdeed50a000)]
  0x00007fdf1495b000 JavaThread "Timer-0" daemon [_thread_blocked, id=9868, stack(0x00007fdeed50a000,0x00007fdeed60b000)]
  0x00007fdf1487d800 JavaThread "Abandoned connection cleanup thread" daemon [_thread_blocked, id=9867, stack(0x00007fdeee056000,0x00007fdeee157000)]
  0x00007fdf1419d000 JavaThread "Service Thread" daemon [_thread_blocked, id=9865, stack(0x00007fdeee55c000,0x00007fdeee65d000)]
  0x00007fdf1417e000 JavaThread "C1 CompilerThread2" daemon [_thread_blocked, id=9864, stack(0x00007fdeee65d000,0x00007fdeee75e000)]
  0x00007fdf1417c000 JavaThread "C2 CompilerThread1" daemon [_thread_blocked, id=9863, stack(0x00007fdeee75e000,0x00007fdeee85f000)]
  0x00007fdf14179800 JavaThread "C2 CompilerThread0" daemon [_thread_blocked, id=9862, stack(0x00007fdeee85f000,0x00007fdeee960000)]
  0x00007fdf1411c800 JavaThread "Signal Dispatcher" daemon [_thread_blocked, id=9861, stack(0x00007fdeee960000,0x00007fdeeea61000)]
  0x00007fdf14093000 JavaThread "Finalizer" daemon [_thread_blocked, id=9860, stack(0x00007fdeeea61000,0x00007fdeeeb62000)]
  0x00007fdf14090800 JavaThread "Reference Handler" daemon [_thread_blocked, id=9859, stack(0x00007fdeeeb62000,0x00007fdeeec63000)]
  0x00007fdf14009800 JavaThread "main" [_thread_in_native, id=9853, stack(0x00007fdf19653000,0x00007fdf19754000)]

Other Threads:
  0x00007fdf14077800 VMThread [stack: 0x00007fdeeec73000,0x00007fdeeed74000] [id=9858]
  0x00007fdf141a9800 WatcherThread [stack: 0x00007fdeee45b000,0x00007fdeee55c000] [id=9866]

VM state:not at safepoint (normal execution)

VM Mutex/Monitor currently owned by a thread: None
```

```java
Heap:
 PSYoungGen      total 493568K, used 166652K [0x00000000e0000000, 0x0000000100000000, 0x0000000100000000)
  eden space 470016K, 30% used [0x00000000e0000000,0x00000000e8bc7328,0x00000000fcb00000)
  from space 23552K, 99% used [0x00000000fcb00000,0x00000000fe1f8020,0x00000000fe200000)
  to   space 27648K, 0% used [0x00000000fe500000,0x00000000fe500000,0x0000000100000000)
 ParOldGen       total 1572864K, used 1284243K [0x0000000080000000, 0x00000000e0000000, 0x00000000e0000000)
  object space 1572864K, 81% used [0x0000000080000000,0x00000000ce624e60,0x00000000e0000000)
 Metaspace       used 51328K, capacity 52048K, committed 52568K, reserved 1095680K
  class space    used 5475K, capacity 5650K, committed 5760K, reserved 1048576K

Card table byte_map: [0x00007fdf1861e000,0x00007fdf18a1f000] byte_map_base: 0x00007fdf1821e000

Marking Bits: (ParMarkBitMap*) 0x00007fdf1a702de0
 Begin Bits: [0x00007fdef4000000, 0x00007fdef6000000)
 End Bits:   [0x00007fdef6000000, 0x00007fdef8000000)

Polling page: 0x00007fdf1e150000

CodeCache: size=245760Kb used=37974Kb max_used=38659Kb free=207786Kb
 bounds [0x00007fdf05000000, 0x00007fdf076b0000, 0x00007fdf14000000]
 total_blobs=9397 nmethods=8876 adapters=435
 compilation: enabled

Compilation events (10 events):
Event: 193774.120 Thread 0x00007fdf1417c000 13436       4       xxx.yy.model.proto.DataModel$LotteryPetCardModel$Builder::setLowFirst (21 bytes)
Event: 193774.121 Thread 0x00007fdf1417c000 nmethod 13436 0x00007fdf059d52d0 code [0x00007fdf059d5420, 0x00007fdf059d54b8]
Event: 193786.767 Thread 0x00007fdf1417e000 13437       3       xxx.yy.database.services.PlayerDbService::readCharacterCacheDataFromDB (168 bytes)
Event: 193786.771 Thread 0x00007fdf1417e000 nmethod 13437 0x00007fdf05a58790 code [0x00007fdf05a58be0, 0x00007fdf05a5ae38]
Event: 193822.892 Thread 0x00007fdf14179800 13438       4       xxx.yy.model.proto.DataModel$LotteryPetCardModel$Builder::setPlayerGuid (21 bytes)
Event: 193822.893 Thread 0x00007fdf14179800 nmethod 13438 0x00007fdf0735d710 code [0x00007fdf0735d860, 0x00007fdf0735d8f8]
Event: 193825.830 Thread 0x00007fdf1417e000 13439       3       xxx.yy.database.dao.LotteryPetCardModelDao$1::handleRead (125 bytes)
Event: 193825.832 Thread 0x00007fdf1417e000 nmethod 13439 0x00007fdf075d68d0 code [0x00007fdf075d6be0, 0x00007fdf075d89c8]
Event: 193828.997 Thread 0x00007fdf1417e000 13440       3       xxx.yy.database.services.LotteryPetCardDbService::readLotteryPetCardModelByPlayerGuid (68 bytes)
Event: 193829.000 Thread 0x00007fdf1417e000 nmethod 13440 0x00007fdf06ddcc90 code [0x00007fdf06ddd060, 0x00007fdf06ddf818]

GC Heap History (10 events):
// 十次fullGC日志, 只保留最后俩次
Event: 194330.858 GC heap before
{Heap before GC invocations=4790 (full 325):
 PSYoungGen      total 497152K, used 492928K [0x00000000e0000000, 0x0000000100000000, 0x0000000100000000)
  eden space 471040K, 100% used [0x00000000e0000000,0x00000000fcc00000,0x00000000fcc00000)
  from space 26112K, 83% used [0x00000000fe680000,0x00000000ffbe0080,0x0000000100000000)
  to   space 26624K, 0% used [0x00000000fcc00000,0x00000000fcc00000,0x00000000fe600000)
 ParOldGen       total 1572864K, used 1282358K [0x0000000080000000, 0x00000000e0000000, 0x00000000e0000000)
  object space 1572864K, 81% used [0x0000000080000000,0x00000000ce44da78,0x00000000e0000000)
 Metaspace       used 51328K, capacity 52048K, committed 52568K, reserved 1095680K
  class space    used 5475K, capacity 5650K, committed 5760K, reserved 1048576K
Event: 194330.932 GC heap after
Heap after GC invocations=4790 (full 325):
 PSYoungGen      total 497664K, used 22048K [0x00000000e0000000, 0x0000000100000000, 0x0000000100000000)
  eden space 471040K, 0% used [0x00000000e0000000,0x00000000e0000000,0x00000000fcc00000)
  from space 26624K, 82% used [0x00000000fcc00000,0x00000000fe188020,0x00000000fe600000)
  to   space 26624K, 0% used [0x00000000fe600000,0x00000000fe600000,0x0000000100000000)
 ParOldGen       total 1572864K, used 1283163K [0x0000000080000000, 0x00000000e0000000, 0x00000000e0000000)
  object space 1572864K, 81% used [0x0000000080000000,0x00000000ce516e40,0x00000000e0000000)
 Metaspace       used 51328K, capacity 52048K, committed 52568K, reserved 1095680K
  class space    used 5475K, capacity 5650K, committed 5760K, reserved 1048576K
}
Event: 194364.307 GC heap before
{Heap before GC invocations=4791 (full 325):
 PSYoungGen      total 497664K, used 493088K [0x00000000e0000000, 0x0000000100000000, 0x0000000100000000)
  eden space 471040K, 100% used [0x00000000e0000000,0x00000000fcc00000,0x00000000fcc00000)
  from space 26624K, 82% used [0x00000000fcc00000,0x00000000fe188020,0x00000000fe600000)
  to   space 26624K, 0% used [0x00000000fe600000,0x00000000fe600000,0x0000000100000000)
 ParOldGen       total 1572864K, used 1283163K [0x0000000080000000, 0x00000000e0000000, 0x00000000e0000000)
  object space 1572864K, 81% used [0x0000000080000000,0x00000000ce516e40,0x00000000e0000000)
 Metaspace       used 51328K, capacity 52048K, committed 52568K, reserved 1095680K
  class space    used 5475K, capacity 5650K, committed 5760K, reserved 1048576K
Event: 194364.362 GC heap after
Heap after GC invocations=4791 (full 325):
 PSYoungGen      total 496640K, used 23456K [0x00000000e0000000, 0x0000000100000000, 0x0000000100000000)
  eden space 470016K, 0% used [0x00000000e0000000,0x00000000e0000000,0x00000000fcb00000)
  from space 26624K, 88% used [0x00000000fe600000,0x00000000ffce8010,0x0000000100000000)
  to   space 27136K, 0% used [0x00000000fcb00000,0x00000000fcb00000,0x00000000fe580000)
 ParOldGen       total 1572864K, used 1283747K [0x0000000080000000, 0x00000000e0000000, 0x00000000e0000000)
  object space 1572864K, 81% used [0x0000000080000000,0x00000000ce5a8e40,0x00000000e0000000)
 Metaspace       used 51328K, capacity 52048K, committed 52568K, reserved 1095680K
  class space    used 5475K, capacity 5650K, committed 5760K, reserved 1048576K
}
// 在这次GC的时候发生内存溢出. 
// from区还剩余3194KB的内存, eden区有470016KB的内存需要转存到from上, 很明显不够, 还差466822内存
// old还剩余298844KB. 还差167977KB(164M)的内存.  
Event: 194407.296 GC heap before
{Heap before GC invocations=4792 (full 325):
 PSYoungGen      total 496640K, used 493472K [0x00000000e0000000, 0x0000000100000000, 0x0000000100000000)
  eden space 470016K, 100% used [0x00000000e0000000,0x00000000fcb00000,0x00000000fcb00000)
  from space 26624K, 88% used [0x00000000fe600000,0x00000000ffce8010,0x0000000100000000)
  to   space 27136K, 0% used [0x00000000fcb00000,0x00000000fcb00000,0x00000000fe580000)
 ParOldGen       total 1572864K, used 1283747K [0x0000000080000000, 0x00000000e0000000, 0x00000000e0000000)
  object space 1572864K, 81% used [0x0000000080000000,0x00000000ce5a8e40,0x00000000e0000000)
 Metaspace       used 51328K, capacity 52048K, committed 52568K, reserved 1095680K
  class space    used 5475K, capacity 5650K, committed 5760K, reserved 1048576K
Event: 194407.352 GC heap after
Heap after GC invocations=4792 (full 325):
 PSYoungGen      total 493568K, used 23520K [0x00000000e0000000, 0x0000000100000000, 0x0000000100000000)
  eden space 470016K, 0% used [0x00000000e0000000,0x00000000e0000000,0x00000000fcb00000)
  from space 23552K, 99% used [0x00000000fcb00000,0x00000000fe1f8020,0x00000000fe200000)
  to   space 27648K, 0% used [0x00000000fe500000,0x00000000fe500000,0x0000000100000000)
 ParOldGen       total 1572864K, used 1284243K [0x0000000080000000, 0x00000000e0000000, 0x00000000e0000000)
  object space 1572864K, 81% used [0x0000000080000000,0x00000000ce624e60,0x00000000e0000000)
 Metaspace       used 51328K, capacity 52048K, committed 52568K, reserved 1095680K
  class space    used 5475K, capacity 5650K, committed 5760K, reserved 1048576K
}
```

```java
// 去优化事件
Deoptimization events (10 events):
Event: 189304.095 Thread 0x00007fdee80ec000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007fdf0717d3cc method=java.util.TimSort.countRunAndMakeAscending([Ljava/lang/Object;IILjava/util/Comparator;)I @ 95
Event: 189307.916 Thread 0x00007fdee8139000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007fdf06b59c40 method=java.util.TimSort.countRunAndMakeAscending([Ljava/lang/Object;IILjava/util/Comparator;)I @ 95
Event: 190298.926 Thread 0x00007fdf15aa1800 Uncommon trap: reason=bimorphic action=maybe_recompile pc=0x00007fdf05ab16f0 method=java.util.HashMap.removeNode(ILjava/lang/Object;Ljava/lang/Object;ZZ)Ljava/util/HashMap$Node; @ 143
Event: 190739.254 Thread 0x00007fdf15f11000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007fdf06ebb004 method=xxx.yy.model.proto.DataModel$PlayerModel$Builder.buildPartial()Lrydl/commons/model/proto/DataModel$PlayerModel; @ 30
Event: 190739.254 Thread 0x00007fdf15f11000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007fdf071c488c method=xxx.yy.model.proto.DataModel$PlayerModel.hasDirty()Z @ 7
Event: 193755.595 Thread 0x00007fdf15ad2800 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007fdf06d7de9c method=xxx.yy.database.dao.LotteryPetCardModelDao$1.handleRead(Ljava/sql/ResultSet;)V @ 6
Event: 193755.596 Thread 0x00007fdf15adc800 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007fdf06b0d45c method=xxx.yy.database.dao.LotteryPetCardModelDao$1.handleRead(Ljava/sql/ResultSet;)V @ 6
Event: 193755.597 Thread 0x00007fdf15ad2800 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007fdf070a21a0 method=xxx.yy.database.services.PlayerDbService.readCharacterCacheDataFromDB(J)Lxxx/yydatabase/cache/CharacterCacheData; @ 82
Event: 193755.598 Thread 0x00007fdf15adc800 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007fdf070a21a0 method=xxx.yy.database.services.PlayerDbService.readCharacterCacheDataFromDB(J)Lxxx/yydatabase/cache/CharacterCacheData; @ 82
Event: 193755.599 Thread 0x00007fdf15ac8800 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007fdf070a21a0 method=xxx.yy.database.services.PlayerDbService.readCharacterCacheDataFromDB(J)Lxxx/yydatabase/cache/CharacterCacheData; @ 82

// 内部异常
Internal exceptions (10 events):
Event: 109891.065 Thread 0x00007fdf15f11000 Implicit null exception at 0x00007fdf075d7020 to 0x00007fdf075da461
Event: 118473.063 Thread 0x00007fdf15ae9000 Implicit null exception at 0x00007fdf05bcaba3 to 0x00007fdf05bcb135
Event: 118473.063 Thread 0x00007fdf15ae9000 Implicit null exception at 0x00007fdf069e31d4 to 0x00007fdf069e4afd
Event: 119309.203 Thread 0x00007fdf15ae9000 Implicit null exception at 0x00007fdf06cfe82c to 0x00007fdf06cff395
Event: 153568.486 Thread 0x00007fdea4009000 Implicit null exception at 0x00007fdf05936f7e to 0x00007fdf059377f9
Event: 188316.775 Thread 0x00007fdf15abc800 Exception <a 'java/lang/NullPointerException'> (0x00000000f78d5e90) thrown at [/HUDSON/workspace/8-2-build-linux-amd64/jdk8u60/4407/hotspot/src/share/vm/interpreter/linkResolver.cpp, line 1280]
Event: 188377.250 Thread 0x00007fdf15ad0800 Exception <a 'java/lang/NullPointerException'> (0x00000000f1a4fc60) thrown at [/HUDSON/workspace/8-2-build-linux-amd64/jdk8u60/4407/hotspot/src/share/vm/interpreter/linkResolver.cpp, line 1280]
Event: 188437.498 Thread 0x00007fdf15b06000 Exception <a 'java/lang/ClassNotFoundException': xxx/yymall/MallWeeklyCleanJobBeanInfo> (0x00000000f16a0390) thrown at [/HUDSON/workspace/8-2-build-linux-amd64/jdk8u60/4407/hotspot/src/share/vm/classfile/systemDictionary.cpp, line 210]
Event: 188437.499 Thread 0x00007fdf15b06000 Exception <a 'java/lang/ClassNotFoundException': xxx/yymall/MallWeeklyCleanJobCustomizer> (0x00000000f16b3810) thrown at [/HUDSON/workspace/8-2-build-linux-amd64/jdk8u60/4407/hotspot/src/share/vm/classfile/systemDictionary.cpp, line 210]
Event: 188437.499 Thread 0x00007fdf15b06000 Implicit null exception at 0x00007fdf05f7091d to 0x00007fdf05f70fc5

Events (10 events):
Event: 194330.854 Executing VM operation: ParallelGCFailedAllocation
Event: 194330.932 Executing VM operation: ParallelGCFailedAllocation done
Event: 194362.894 Executing VM operation: RevokeBias
Event: 194362.895 Executing VM operation: RevokeBias done
Event: 194362.896 Thread 0x00007fdea8296000 Thread exited: 0x00007fdea8296000
Event: 194364.298 Executing VM operation: ParallelGCFailedAllocation
Event: 194364.362 Executing VM operation: ParallelGCFailedAllocation done
Event: 194364.488 Thread 0x00007fdea8296000 Thread added: 0x00007fdea8296000
Event: 194407.294 Executing VM operation: ParallelGCFailedAllocation
Event: 194407.352 Executing VM operation: ParallelGCFailedAllocation done


Dynamic libraries:
// ...  省略的动态链接库信息

VM Arguments:
jvm_args: -agentlib:collector -Xms1024m -Xmx2048m -Xmn512m 

Environment Variables:
JAVA_HOME=/home/jdk1.8/
JRE_HOME=/home/jdk1.8//jre
JAVA_TOOL_OPTIONS=-agentlib:collector
CLASSPATH=.:/home/jdk1.8//lib/dt.jar:/home/jdk1.8//lib/tools.jar
PATH=/home/jdk1.8/bin:/usr/lib64/qt-3.3/bin:/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/opt/oracle/developerstudio12.5/bin:/root/bin
LD_LIBRARY_PATH=/opt/oracle/developerstudio12.5/bin/../lib/analyzer/runtime:/opt/oracle/developerstudio12.5/bin/../lib/analyzer/amd64/runtime
LD_PRELOAD=libcollector.so
SHELL=/bin/bash

Signal Handlers:
SIGSEGV: [libjvm.so+0xaba070], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO
SIGBUS: [libjvm.so+0xaba070], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO
SIGFPE: [libjvm.so+0x917630], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO
SIGPIPE: [libjvm.so+0x917630], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO
SIGXFSZ: [libjvm.so+0x917630], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO
SIGILL: [libjvm.so+0x917630], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO
SIGUSR1: SIG_DFL, sa_mask[0]=00000000000000000000000000000000, sa_flags=none
SIGUSR2: [libjvm.so+0x918c60], sa_mask[0]=00000000000000000000000000000000, sa_flags=SA_RESTART|SA_SIGINFO
SIGHUP: [libjvm.so+0x91a060], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO
SIGINT: [libjvm.so+0x91a060], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO
SIGTERM: [libjvm.so+0x91a060], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO
SIGQUIT: [libjvm.so+0x91a060], sa_mask[0]=11111111011111111101111111111110, sa_flags=SA_RESTART|SA_SIGINFO

```


```java
---------------  S Y S T E M  ---------------

OS:redhat-6

uname:Linux 2.6.32-431.el6.x86_64 #1 SMP Fri Nov 22 03:15:09 UTC 2013 x86_64
libc:glibc 2.12 NPTL 2.12 
rlimit: STACK 10240k, CORE 0k, NPROC 192063, NOFILE 4096, AS infinity
load average:0.99 0.89 0.98

/proc/meminfo:
MemTotal:       24604788 kB
MemFree:          358204 kB
Buffers:           66576 kB
Cached:          4915848 kB
SwapCached:        29272 kB
Active:         19372236 kB
Inactive:        4208388 kB
Active(anon):   16906140 kB
Inactive(anon):  1702500 kB
Active(file):    2466096 kB
Inactive(file):  2505888 kB
Unevictable:           0 kB
Mlocked:               0 kB
SwapTotal:      12369912 kB
SwapFree:       11736852 kB
Dirty:             41524 kB
Writeback:             0 kB
AnonPages:      18569936 kB
Mapped:            36736 kB
Shmem:             10144 kB
Slab:             389624 kB
SReclaimable:     308988 kB
SUnreclaim:        80636 kB
KernelStack:        5328 kB
PageTables:        74164 kB
NFS_Unstable:          0 kB
Bounce:                0 kB
WritebackTmp:          0 kB
CommitLimit:    24672304 kB
Committed_AS:   25201028 kB
VmallocTotal:   34359738367 kB
VmallocUsed:      335408 kB
VmallocChunk:   34359318864 kB
HardwareCorrupted:     0 kB
AnonHugePages:  16519168 kB
HugePages_Total:       0
HugePages_Free:        0
HugePages_Rsvd:        0
HugePages_Surp:        0
Hugepagesize:       2048 kB
DirectMap4k:        6784 kB
DirectMap2M:    25153536 kB


CPU:total 4 (4 cores per cpu, 1 threads per core) family 6 model 23 stepping 10, cmov, cx8, fxsr, mmx, sse, sse2, sse3, ssse3, sse4.1, tsc

/proc/cpuinfo:
processor	: 0
vendor_id	: GenuineIntel
cpu family	: 6
model		: 23
model name	: Intel(R) Xeon(R) CPU           X3323  @ 2.50GHz
stepping	: 10
cpu MHz		: 2499.848
cache size	: 3072 KB
physical id	: 0
siblings	: 4
core id		: 0
cpu cores	: 4
apicid		: 0
initial apicid	: 0
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush dts acpi mmx fxsr sse sse2 ss ht tm pbe syscall nx lm constant_tsc arch_perfmon pebs bts rep_good aperfmperf pni dtes64 monitor ds_cpl vmx est tm2 ssse3 cx16 xtpr pdcm dca sse4_1 xsave lahf_lm dts tpr_shadow vnmi flexpriority
bogomips	: 4999.69
clflush size	: 64
cache_alignment	: 64
address sizes	: 36 bits physical, 48 bits virtual
power management:

processor	: 1
vendor_id	: GenuineIntel
cpu family	: 6
model		: 23
model name	: Intel(R) Xeon(R) CPU           X3323  @ 2.50GHz
stepping	: 10
cpu MHz		: 2499.848
cache size	: 3072 KB
physical id	: 0
siblings	: 4
core id		: 1
cpu cores	: 4
apicid		: 1
initial apicid	: 1
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush dts acpi mmx fxsr sse sse2 ss ht tm pbe syscall nx lm constant_tsc arch_perfmon pebs bts rep_good aperfmperf pni dtes64 monitor ds_cpl vmx est tm2 ssse3 cx16 xtpr pdcm dca sse4_1 xsave lahf_lm dts tpr_shadow vnmi flexpriority
bogomips	: 4999.69
clflush size	: 64
cache_alignment	: 64
address sizes	: 36 bits physical, 48 bits virtual
power management:

processor	: 2
vendor_id	: GenuineIntel
cpu family	: 6
model		: 23
model name	: Intel(R) Xeon(R) CPU           X3323  @ 2.50GHz
stepping	: 10
cpu MHz		: 2499.848
cache size	: 3072 KB
physical id	: 0
siblings	: 4
core id		: 2
cpu cores	: 4
apicid		: 2
initial apicid	: 2
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush dts acpi mmx fxsr sse sse2 ss ht tm pbe syscall nx lm constant_tsc arch_perfmon pebs bts rep_good aperfmperf pni dtes64 monitor ds_cpl vmx est tm2 ssse3 cx16 xtpr pdcm dca sse4_1 xsave lahf_lm dts tpr_shadow vnmi flexpriority
bogomips	: 4999.69
clflush size	: 64
cache_alignment	: 64
address sizes	: 36 bits physical, 48 bits virtual
power management:

processor	: 3
vendor_id	: GenuineIntel
cpu family	: 6
model		: 23
model name	: Intel(R) Xeon(R) CPU           X3323  @ 2.50GHz
stepping	: 10
cpu MHz		: 2499.848
cache size	: 3072 KB
physical id	: 0
siblings	: 4
core id		: 3
cpu cores	: 4
apicid		: 3
initial apicid	: 3
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush dts acpi mmx fxsr sse sse2 ss ht tm pbe syscall nx lm constant_tsc arch_perfmon pebs bts rep_good aperfmperf pni dtes64 monitor ds_cpl vmx est tm2 ssse3 cx16 xtpr pdcm dca sse4_1 xsave lahf_lm dts tpr_shadow vnmi flexpriority
bogomips	: 4999.69
clflush size	: 64
cache_alignment	: 64
address sizes	: 36 bits physical, 48 bits virtual
power management:



Memory: 4k page, physical 24604788k(358204k free), swap 12369912k(11736852k free)

vm_info: Java HotSpot(TM) 64-Bit Server VM (25.60-b23) for linux-amd64 JRE (1.8.0_60-b27), built on Aug  4 2015 12:19:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)

time: Mon Jul  4 01:39:45 2016
elapsed time: 194417 seconds (2d 6h 0m 17s)
```