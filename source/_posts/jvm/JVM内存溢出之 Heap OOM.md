category: JVM
date: 2014-09-06
title: JVM内存溢出之 Heap OOM
---
我们首先看一段内存溢出的代码
```java
public class TestHeapOOM {

	public static void main(String[] args) {
		for(int i = 0; i < 10; i ++) {
			System.out.println("Allocate : " + i);
			byte[] bytes = new byte[1324 * 1124 * i * 2];
		} 
	}
}
```

接下来我们运行一下上面的那个程序
```java
D:\testOOM>java -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintHeapAtGC -Xms10M -Xmx10M -Xmn4M TestHeapOOM
Allocate : 1
Allocate : 2
Allocate : 3
{Heap before GC invocations=1 (full 0):
 PSYoungGen      total 3584K, used 3002K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 97% used [0x00000000ffc00000,0x00000000ffeee8c0,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 4096K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 66% used [0x00000000ff600000,0x00000000ffa00010,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=1 (full 0):
 PSYoungGen      total 3584K, used 488K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff00000,0x00000000fff7a020,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 4312K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 70% used [0x00000000ff600000,0x00000000ffa36020,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=2 (full 0):
 PSYoungGen      total 3584K, used 488K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff00000,0x00000000fff7a020,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 4312K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 70% used [0x00000000ff600000,0x00000000ffa36020,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=2 (full 0):
 PSYoungGen      total 3584K, used 488K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff80000,0x00000000ffffa020,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 4312K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 70% used [0x00000000ff600000,0x00000000ffa36020,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=3 (full 1):
 PSYoungGen      total 3584K, used 488K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff80000,0x00000000ffffa020,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 4312K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 70% used [0x00000000ff600000,0x00000000ffa36020,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=3 (full 1):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 642K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff6a08a0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=4 (full 1):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 642K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff6a08a0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=4 (full 1):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 642K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff6a08a0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=5 (full 2):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 642K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff6a08a0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=5 (full 2):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 630K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff69d8d0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid17676.hprof ...
Heap dump file created [1336934 bytes in 0.006 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at TestHeapOOM.main(TestHeapOOM.java:6)

D:\testOOM>
```
我们来分析一下, 我们固定堆内存大小为10M, 新生代为4M. 我们看到PSYoungGen总共为3584K, 分别为eden区3072K, from survivor区为512K, 然后加上to survivor 区的512K, 总共为4096K. 老年代总共为6114K

我们来看一下第一次GC之前的内存分布情况: 此时程序已经进行了俩次内存分配, 在第三次的(分配6M 6114K的byte数组)时候触发了GC. 此时 新生代为3002K, 老年代为4096K, 我们可以推断出，第一次2M的byte数组应该是分配在了新生代, 而第二次的4M byte数组直接分配在了老年代.

而经过一次GC之后, 新生代使用了488K, 而老年代增长到4312K. 经过一次GC之后新生代消耗了`3002 - 488 -(4312 - 4096) = 2298`, 我们可以推断出第一次分配的那2M的byte数组被回收掉了.

接下来又进行了一次yong GC但是内存并没有发生什么变化,于是就发生了一次full GC.

第一次full GC(也就是invocations=3的时候)之后, 我们看到新生代被清空了, 老年代也只剩下了642K的内存被使用着, 我们推断应该是那个4M的byte数组被回收掉了. 但是此时要分配一个6M的byte数组,显然老年代是不够的. 于是在这次Full GC的时候又进行了一次GC操作, 但是内存仍然不够, 于是又产生了一次Full GC, 也就是full=2的那次. 但是很悲催, 内存仍然是不够用的, 于是就看到了java.lang.OutOfMemoryError: Java heap space, 同时生成了一个java_pid17676.hprof 的文件.

对于*.hprof文件。我们可以通过下列工具分析它
* Eclipse Memory Analyzer
* JProfiler
* jvisualvm
* jhat

由于我们从上面的GC日志中分析出了引发内存溢出的原因, 也就不再使用上列的工具分析*.hprof文件了,但是对于复杂的应用程序来说,如果发生了堆内存溢出的话, 使用上列工具分析的话,还是非常有必要的.

在分析这个文件的时候,我们重点确认内存中的对象是否是必要的,也就是弄清楚是引发了内存泄漏还是内存溢出.
1. 如果是内存泄漏可通过工具查看泄漏对象到GC Roots的引用链.于是就能找到泄漏对象是通过怎样的路径与GC Toots相关联,并导致垃圾收集器无法自动回收它们的. 掌握了泄漏对象的类型信息,以及GC Roots引用链信息,就可以比较准确地定位出泄漏代码的位置.
2. 如果不存在泄漏, 换句话说就是内存中的对象确实还都必须存货着, 那就应当检查虚拟机的堆参数,与物理机内存对比查看是否还可以调大,从代码上检查是否存在某些生命周期过长,持有状态时间过长的情况,尝试减少程序运行周期的内存消耗.

