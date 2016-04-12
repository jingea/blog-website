category: JVM
date: 2014-11-28
title: JVM 日志输出
---
本文主要列举了JVM中常用的日志输出参数

## PrintGC
在jvm选项上添加上这个参数,只要遇上GC就会输出GC日志
```xml
-XX:+PrintGC -Xmx30M
```
然后我们写一个测试程序
```java
public class TestPrintGC {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            byte[] bytes = new byte[1024 * 1024 * 15];
        }
    }
}
```
看一下结果
```xml
[GC (Allocation Failure)  512K->336K(21504K), 0.0033610 secs]
[GC (Allocation Failure)  841K->536K(22016K), 0.0025350 secs]
[GC (Allocation Failure)  16312K->16020K(22016K), 0.0014770 secs]
[GC (Allocation Failure)  16020K->16032K(23040K), 0.0018800 secs]
[Full GC (Allocation Failure)  16032K->604K(9216K), 0.0085870 secs]
[GC (Allocation Failure)  16006K->15996K(23040K), 0.0011620 secs]
[GC (Allocation Failure)  15996K->15996K(25600K), 0.0013020 secs]
[Full GC (Allocation Failure)  15996K->611K(11776K), 0.0084450 secs]
```

## PrintGCDetails
这个参数相比于PrintGC,会输出更加详细的信息.
```xml
-XX:+PrintGCDetails -Xmx30M
```
同样使用上面的测试程序, 然后看一下输出
```xml
[GC (Allocation Failure) [PSYoungGen: 512K->368K(1024K)] 512K->376K(21504K), 0.0067890 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 873K->512K(1536K)] 881K->544K(22016K), 0.0030230 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 938K->512K(1536K)] 16330K->16020K(22016K), 0.0011850 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 512K->480K(2560K)] 16020K->16052K(23040K), 0.0010710 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
[Full GC (Allocation Failure) [PSYoungGen: 480K->0K(2560K)] [ParOldGen: 15572K->613K(6656K)] 16052K->613K(9216K), [Metaspace: 3090K->3090K(1056768K)], 0.0068200 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 41K->96K(2560K)] 16014K->16069K(23040K), 0.0005350 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (Allocation Failure) [PSYoungGen: 96K->128K(5120K)] 16069K->16101K(25600K), 0.0003560 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
[Full GC (Allocation Failure) [PSYoungGen: 128K->0K(5120K)] [ParOldGen: 15973K->613K(6656K)] 16101K->613K(11776K), [Metaspace: 3091K->3091K(1056768K)], 0.0041320 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
Heap
 PSYoungGen      total 5120K, used 41K [0x00000007bf600000, 0x00000007bfc00000, 0x00000007c0000000)
  eden space 4096K, 1% used [0x00000007bf600000,0x00000007bf60a548,0x00000007bfa00000)
  from space 1024K, 0% used [0x00000007bfb00000,0x00000007bfb00000,0x00000007bfc00000)
  to   space 1024K, 0% used [0x00000007bfa00000,0x00000007bfa00000,0x00000007bfb00000)
 ParOldGen       total 20480K, used 15973K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 77% used [0x00000007be200000,0x00000007bf1996e0,0x00000007bf600000)
 Metaspace       used 3097K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 340K, capacity 386K, committed 512K, reserved 1048576K
```

## PrintHeapAtGC
这个参数会在GC前后打印出堆内信息
```xml
-XX:+PrintHeapAtGC -Xmx30M
```
测试程序
```java
public class TestPrintGCDetails {
    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            byte[] bytes = new byte[1024 * 1024 * 15];
        }
    }
}
```
输出结果
```xml
{Heap before GC invocations=1 (full 0):
 PSYoungGen      total 1024K, used 512K [0x00000007bf600000, 0x00000007bf780000, 0x00000007c0000000)
  eden space 512K, 100% used [0x00000007bf600000,0x00000007bf680000,0x00000007bf680000)
  from space 512K, 0% used [0x00000007bf700000,0x00000007bf700000,0x00000007bf780000)
  to   space 512K, 0% used [0x00000007bf680000,0x00000007bf680000,0x00000007bf700000)
 ParOldGen       total 20480K, used 0K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 0% used [0x00000007be200000,0x00000007be200000,0x00000007bf600000)
 Metaspace       used 2247K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 245K, capacity 384K, committed 384K, reserved 1048576K
Heap after GC invocations=1 (full 0):
 PSYoungGen      total 1024K, used 384K [0x00000007bf600000, 0x00000007bf800000, 0x00000007c0000000)
  eden space 512K, 0% used [0x00000007bf600000,0x00000007bf600000,0x00000007bf680000)
  from space 512K, 75% used [0x00000007bf680000,0x00000007bf6e0020,0x00000007bf700000)
  to   space 512K, 0% used [0x00000007bf780000,0x00000007bf780000,0x00000007bf800000)
 ParOldGen       total 20480K, used 0K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 0% used [0x00000007be200000,0x00000007be200000,0x00000007bf600000)
 Metaspace       used 2247K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 245K, capacity 384K, committed 384K, reserved 1048576K
}
{Heap before GC invocations=2 (full 0):
 PSYoungGen      total 1024K, used 889K [0x00000007bf600000, 0x00000007bf800000, 0x00000007c0000000)
  eden space 512K, 98% used [0x00000007bf600000,0x00000007bf67e728,0x00000007bf680000)
  from space 512K, 75% used [0x00000007bf680000,0x00000007bf6e0020,0x00000007bf700000)
  to   space 512K, 0% used [0x00000007bf780000,0x00000007bf780000,0x00000007bf800000)
 ParOldGen       total 20480K, used 0K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 0% used [0x00000007be200000,0x00000007be200000,0x00000007bf600000)
 Metaspace       used 2621K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 286K, capacity 384K, committed 384K, reserved 1048576K
Heap after GC invocations=2 (full 0):
 PSYoungGen      total 1536K, used 496K [0x00000007bf600000, 0x00000007bf800000, 0x00000007c0000000)
  eden space 1024K, 0% used [0x00000007bf600000,0x00000007bf600000,0x00000007bf700000)
  from space 512K, 96% used [0x00000007bf780000,0x00000007bf7fc010,0x00000007bf800000)
  to   space 512K, 0% used [0x00000007bf700000,0x00000007bf700000,0x00000007bf780000)
 ParOldGen       total 20480K, used 48K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 0% used [0x00000007be200000,0x00000007be20c000,0x00000007bf600000)
 Metaspace       used 2621K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 286K, capacity 384K, committed 384K, reserved 1048576K
}
{Heap before GC invocations=3 (full 0):
 PSYoungGen      total 1536K, used 938K [0x00000007bf600000, 0x00000007bf800000, 0x00000007c0000000)
  eden space 1024K, 43% used [0x00000007bf600000,0x00000007bf66ea50,0x00000007bf700000)
  from space 512K, 96% used [0x00000007bf780000,0x00000007bf7fc010,0x00000007bf800000)
  to   space 512K, 0% used [0x00000007bf700000,0x00000007bf700000,0x00000007bf780000)
 ParOldGen       total 20480K, used 15408K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 75% used [0x00000007be200000,0x00000007bf10c010,0x00000007bf600000)
 Metaspace       used 3067K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=3 (full 0):
 PSYoungGen      total 1536K, used 496K [0x00000007bf600000, 0x00000007bf900000, 0x00000007c0000000)
  eden space 1024K, 0% used [0x00000007bf600000,0x00000007bf600000,0x00000007bf700000)
  from space 512K, 96% used [0x00000007bf700000,0x00000007bf77c010,0x00000007bf780000)
  to   space 512K, 0% used [0x00000007bf880000,0x00000007bf880000,0x00000007bf900000)
 ParOldGen       total 20480K, used 15532K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 75% used [0x00000007be200000,0x00000007bf12b3a8,0x00000007bf600000)
 Metaspace       used 3067K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=4 (full 0):
 PSYoungGen      total 1536K, used 496K [0x00000007bf600000, 0x00000007bf900000, 0x00000007c0000000)
  eden space 1024K, 0% used [0x00000007bf600000,0x00000007bf600000,0x00000007bf700000)
  from space 512K, 96% used [0x00000007bf700000,0x00000007bf77c010,0x00000007bf780000)
  to   space 512K, 0% used [0x00000007bf880000,0x00000007bf880000,0x00000007bf900000)
 ParOldGen       total 20480K, used 15532K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 75% used [0x00000007be200000,0x00000007bf12b3a8,0x00000007bf600000)
 Metaspace       used 3067K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=4 (full 0):
 PSYoungGen      total 2560K, used 496K [0x00000007bf600000, 0x00000007bf900000, 0x00000007c0000000)
  eden space 2048K, 0% used [0x00000007bf600000,0x00000007bf600000,0x00000007bf800000)
  from space 512K, 96% used [0x00000007bf880000,0x00000007bf8fc010,0x00000007bf900000)
  to   space 512K, 0% used [0x00000007bf800000,0x00000007bf800000,0x00000007bf880000)
 ParOldGen       total 20480K, used 15548K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 75% used [0x00000007be200000,0x00000007bf12f3a8,0x00000007bf600000)
 Metaspace       used 3067K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=5 (full 1):
 PSYoungGen      total 2560K, used 496K [0x00000007bf600000, 0x00000007bf900000, 0x00000007c0000000)
  eden space 2048K, 0% used [0x00000007bf600000,0x00000007bf600000,0x00000007bf800000)
  from space 512K, 96% used [0x00000007bf880000,0x00000007bf8fc010,0x00000007bf900000)
  to   space 512K, 0% used [0x00000007bf800000,0x00000007bf800000,0x00000007bf880000)
 ParOldGen       total 20480K, used 15548K [0x00000007be200000, 0x00000007bf600000, 0x00000007bf600000)
  object space 20480K, 75% used [0x00000007be200000,0x00000007bf12f3a8,0x00000007bf600000)
 Metaspace       used 3067K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=5 (full 1):
 PSYoungGen      total 2560K, used 0K [0x00000007bf600000, 0x00000007bf900000, 0x00000007c0000000)
  eden space 2048K, 0% used [0x00000007bf600000,0x00000007bf600000,0x00000007bf800000)
  from space 512K, 0% used [0x00000007bf880000,0x00000007bf880000,0x00000007bf900000)
  to   space 512K, 0% used [0x00000007bf800000,0x00000007bf800000,0x00000007bf880000)
 ParOldGen       total 6656K, used 614K [0x00000007be200000, 0x00000007be880000, 0x00000007bf600000)
  object space 6656K, 9% used [0x00000007be200000,0x00000007be299b18,0x00000007be880000)
 Metaspace       used 3067K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
}
```

## PrintGCApplicationConcurrentTime
打印应用程序的执行时间
```xml
-XX:+PrintGCApplicationConcurrentTime -Xmx30M
```
使用上面的应用程序,输出结果为
```xml
Application time: 0.0070450 seconds
Application time: 0.0440040 seconds
Application time: 0.0463770 seconds
Application time: 0.0078020 seconds
```

## PrintGCApplicationStoppedTime
打印程序因为GC停顿的时间
```xml
Total time for which application threads were stopped: 0.0012960 seconds
Total time for which application threads were stopped: 0.0013340 seconds
Total time for which application threads were stopped: 0.0097940 seconds
```

## PrintGCTimeStamps
这个参数会在每次GC的时候,输出GC发生的时间
```xml
-XX:+PrintGCTimeStamps -Xmx30M
```
使用上面的测试程序,看一下输出结果
```xml

```

## PrintReferenceGC
追踪系统内的软引用,弱引用,虚引用和Finallize队列的话可以使用`PrintReferenceGC`

## verbose:class
* `-verbose:class` : 跟踪类的加载和卸载
* `-XX:+TraceClassLoading` : 追踪类的加载
* `-XX:+TraceClassUnloading` : 追踪类的卸载

## PrintClassHistogram
`-XX:PrintClassHistogram` 在运行时输出系统中的类的分布情况.

## PrintVMOptions
`-XX:PrintVMOptions` : 输出程序启动时, 虚拟机接收到的指定的参数

## PrintFlagsFinal
`-XX:+PrintFlagsFinal` : 输出所有的系统的参数的值.

## PrintGCDateStamps


## UseGCLogFileRotation

## NumberOfGCLogFiles
指定GC文件的数量

## GCLogFileSize
指定每个GC文件的大小

## HeapDumpOnOutOfMemoryError
在发生内存溢出异常时是否生成堆转储快照,关闭则不生成

## OnOutOfMemoryError
当虚拟机抛出内存溢出异常时,执行指令的命令

## OnError
当虚拟机抛出ERROR异常时,执行指令的命令

## PrintClassHistogram
使用[ctrl]-[break]快捷键输出类统计状态,相当于jmap-histo的功能

## PrintConcurrentLocks
打印J.U.C中的状态

## PrintCommandLineFlags
打印启动虚拟机时输入的非稳定参数

## PrintCompilation
显示所有可设置的参数及它们的值(***从JDK6update21开始才可以用)

## PrintTenuingDistribution
打印GC后新生代各个年龄对象的大小

## PrintInlining
打印方法内联信息

## PrintCFGToFile
将CFG图信息输出到文件,只有DEBUG版虚拟机才支持此参数

## PrintIdealGraphFile
将Ideal图信息输出到文件,只有DEBUG版虚拟机才支持此参数

## PrintAssembly
打印即时编译后的二进制信息

## gc log
我使用`-Xmx2048m -Xms2048M  -Xmn1048m`的内存分配方式启动一个JVM,下面是其中一段GC 日志
```java
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

1. `Heap before GC invocations=196 (full 0)`: 这一行表示在调用第196GC, 第0次full GC之前的jvm内存分配情况.
2. `par new generation   total 873856K, used 699148K [0x000000077ae00000, 0x00000007bae00000, 0x00000007bae00000)`: 这一行的意思是新生代总共分配了873856K内存,使用了699148K的内存.
3. `eden space 699136K, 100% used [0x000000077ae00000, 0x00000007a58c0000, 0x00000007a58c0000)`:新生代的eden区分配了699136K内存,并且使用了100%
4. `from space 174720K,   0% used [0x00000007a58c0000, 0x00000007a58c30d8, 0x00000007b0360000)`: survivor1区分配了174720K内存,没有使用
6. `to   space 174720K,   0% used [0x00000007b0360000, 0x00000007b0360000, 0x00000007bae00000)`: survivor2区分配了174720K内存,没有使用
5. `concurrent mark-sweep generation total 1048576K, used 3377K [0x00000007bae00000, 0x00000007fae00000, 0x00000007fae00000)` ：采用并发标记清除算法对新生代共分配1048576K, 其中有3377K大小在使用着
7. `concurrent-mark-sweep perm gen total 21248K, used 9252K [0x00000007fae00000, 0x00000007fc2c0000, 0x0000000800000000)`: 采用并发标记清除算法对永久代共分配21248K大小内存,使用了9252K.
8. `670.529: [GC670.529: [ParNew: 699148K->10K(873856K), 0.0047350 secs] 702525K->3387K(1922432K), 0.0048480 secs] [Times:`: user=0.03 sys=0.00, real=0.00 secs]`:开始gc,ParNew垃圾收集器的新生代经过0.0047350秒后,将699148K内存进行垃圾收集, gc后有10K内存在使用.
9. `Heap after GC invocations=197 (full 0)`:在对堆进行197次gc后的内存分配情况：
10. `par new generation   total 873856K, used 10K [0x000000077ae00000, 0x00000007bae00000, 0x00000007bae00000)`:新生代分配了873856K大小，使用了10K
11. `eden space 699136K,   0% used [0x000000077ae00000, 0x000000077ae00000, 0x00000007a58c0000)`:新生代eden区分配了699136K大小,使用了0k
12. `from space 174720K,   0% used [0x00000007b0360000, 0x00000007b03628d8, 0x00000007bae00000)`:新生代的survivor1区分配了174720K,使用了0k
13. `to space 174720K,   0% used [0x00000007a58c0000, 0x00000007a58c0000, 0x00000007b0360000)`:新生代的survivor2区分配了174720K,使用了0k
14. `concurrent mark-sweep generation total 1048576K, used 3377K [0x00000007bae00000, 0x00000007fae00000, 0x00000007fae00000)`:
15. `concurrent-mark-sweep perm gen total 21248K, used 9252K [0x00000007fae00000, 0x00000007fc2c0000, 0x0000000800000000)`:
