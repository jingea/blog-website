category: JVM
date: 2014-11-28
title: JVM 日志输出
---
## PrintGC
在jvm选项上添加上这个参数,只要遇上GC就会输出GC日志

## PrintGCDetails
这个参数相比于PrintGC,会输出更加详细的信息.

## PrintHeapAtGC
这个参数会在GC前后打印出堆内信息

## PrintGCTimeStamps
这个参数会在每次GC的时候,输出GC发生的时间

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
