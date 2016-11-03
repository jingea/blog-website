category: JVM
date: 2014-09-01
title: JVM 参数设置
---
## 内存分配

### Xmx
这个参数设置可用的最大堆内存, 如果我们不使用这个参数,在Mac平台上,默认分配的堆内存为1431M.
```java
public class Test {
	public static void main(String[] main) {
		long maxMemory = Runtime.getRuntime().maxMemory();

		System.out.println(maxMemory / 1000 / 1000 + "M");
	}
}
```
当我们使用上`-Xmx1m`的时候,我们手动地将堆内存设置为1m, 通过刚才的程序输出,确实是最大的内存为1m.  在这个例子中我们还要注意到, 在JVM分配内存时候, 1M = 1000KBM, 而不是1024KBM

### Xss
这个参数用来分配线程最大的栈空间, 这个参数决定了方法调用的最大的次数. 如果我们不使用这个参数,在mac平台上大概能进行16000个方法调用. 这个参数可设置的最小值为160k. 当我们设置上160k的时候, 程序能调用818次的方法调用
```java
public class Test {
	private static long count = 0;
	public static void main(String[] main) {
		try {
			invokeSelf();
		} catch (Throwable e) {
			System.out.println("invoke:" + count);
			e.printStackTrace();
		}
	}
	public static void invokeSelf() {
		count++;
		invokeSelf();
	}
}
```
我们知道在java的方法调用就是在栈帧不断地在java栈中出栈入栈, 因此栈帧的大小也影响着方法的调用次数.  栈帧是由局部变量表,操作数栈,和帧数据几个部分组成.


下面我们测试一下将上例的递归函数加入一个参数,看看调用次数
```java

public class Test {
	private static long count = 0;
	public static void main(String[] main) {
		try {
			invokeSelf(count);
		} catch (Throwable e) {
			System.out.println("invoke:" + count);
			e.printStackTrace();
		}
	}

	public static void invokeSelf(long times) {
		count++;
		invokeSelf(times);
	}
}
```

这次的调用次数成了682次,说明局部变量表确实是在影响方法调用的次数.
```java
public class Test {
	private static long count = 0;
	public static void main(String[] main) {
		try {
			invokeSelf(count);
		} catch (Throwable e) {
			System.out.println("invoke:" + count);
			e.printStackTrace();
		}
	}

	public static void invokeSelf(long times) {
		count++;
		int newCount = (int)count;
		invokeSelf(times);
	}
}
```
这次我添加了一个中间过程,调用次数变成了629


本想这个次数是针对一个线程的设置还是个共享值, 但是在多线程的情况下,明显的出现了难以预料的结果,他的调用次数有了明显的提升
```java
public class Test {

    private static long count = 0;
    public static void main(String[] main) {

        Runnable runnable = () -> {
            try {
                invokeSelf(count);
            } catch (Throwable e) {
                System.out.println("invoke : " + count);
    //            e.printStackTrace();
            }

        };

        Runnable runnable2 = () -> {
            try {
                invokeSelf2(count);
            } catch (Throwable e) {
                System.out.println("invoke : " + count);
                //            e.printStackTrace();
            }

        };

        Thread t = new Thread(runnable);
        Thread t2 = new Thread(runnable2);
        t.start();
        t2.start();
    }

    public static void invokeSelf(long times) {
        count++;
        int newCount = (int)count;
        invokeSelf(times);
    }

    public static void invokeSelf2(long times) {
        count++;
        int newCount = (int)count;
        invokeSelf2(times);
    }

}
```
这个很奇怪,
结果为
```java
invoke : 1083
invoke : 1083
```
或者
```java
invoke : 650
invoke : 1300
```
要不然次数是一样的,要不然次数是个倍数的关系

### MaxPermSize
`-XX:MaxPermSize`:永久代的最大值

### ms
`-XX:ms`:初始堆大小

### mn
`-XX:`:设置年轻代大小.整个JVM内存大小=年轻代大小+年老代大小+持久代大小.(Xms必须大于Xmn)

### NewSize
`-XX:NewSize=2m`:新生代默认大小(单位是字节)

### MaxNewSize
`-XX:MaxNewSize=size`:新生代最大值(单位字节)

### ThreadStackSize
`-XX:ThreadStackSize=512`:线程堆栈大小(单位Kbytes,0使用默认大小)

### SurvivorRatio
`-XX:SurvivorRatio=6`:新生代中Eden区和Survivor区的容量比值(默认为8)

### PretenureSizeThreshold
`-XX:PretenureSizeThreshold`:直接晋升到老年代的对象大小,设置这个参数后,大于这个参数的对象将直接在老年代分配

### MaxTenuringThreshold
`-XX:MaxTenuringThreshold`:晋升到老年代的对象年龄,每个对象在坚持过一次MinorGC之后,年龄就+1,当超过这个参数值时就进入老年代

### UseAdaptiveSizePolicy
`-XX:UseAdaptiveSizePolicy`:动态调整java堆中各个区域的大小及进入老年代的年龄
> 吞吐量垃圾回收器利用了一种叫做自适应大小的特性，自适应大小是基于对象的分配和存活率来自动改变eden空间和survivor空间大小，目的是优化对象的岁数分布。自适应大小的企图是提供易用性，容易优化JVM，以致于提供可靠的吞吐量

### HandlePromotionFailure
`-XX:HandlePromotionFailure`:是否允许分配担保失败,即老年代的剩余空间不足以应付新生代的整个Eden和Survivor区的所有对象都存活的极端情况

### UseTLAB
`-XX:UseTLAB`:优先在本地线程缓冲区中分配对象,避免分配内存时的锁定过程

### NewRatio
`-XX:NewRatio=n`:老年代与新生代比例(默认是2).

### MaxHeapFreeRatio
`-XX:MaxHeapFreeRatio`:当Xmx值比Xms值大时,堆可以动态收缩和扩展,这个参数控制当堆空闲大于指定比率时自动收缩

### MinHeapFreeRatio
`-XX:MinHeapFreeRatio`:当Xmx值比Xms值大时,堆可以动态收缩和扩展,这个参数控制当堆空闲小于指定比率时自动收缩

## 设置GC

### DisableExplicitGC
`-XDisableExplicitGC` 忽略来自System.gc()方法触发的垃圾收集

### GCTimeRatio
`-XX:`:GC时间占总时间的比率.仅在使用ParallelScavenge收集器时生效

### MaxGCPauseMillis
`-XX:`:设置GC最大停顿时间.仅在使用ParallelScavenge收集器时生效

### ScavengeBeforeFullGC
`-XX:`:在FullGC发生之前触发一次MinorGC

### UseGCOverheadLimit
`-XX:`:禁止GC过程无限制的执行,如果过于频繁,就直接发生OutOfMemory

### InitiatingHeapOccupancyPercent
`-XX:InitiatingHeapOccupancyPercent=n`:设置触发标记周期的Java堆占用率阈值.默认占用率是整个Java堆的45%.

### G1HeapRegionSize
`-XX:G1HeapRegionSize=n`:设置的G1区域的大小.值是2的幂,范围是1MB到32MB之间.目标是根据最小的Java堆大小划分出约2048个区域.

### G1ReservePercent
`-XX:G1ReservePercent=n`:设置作为空闲空间的预留内存百分比,以降低目标空间溢出的风险.默认值是10%.增加或减少百分比时,请确保对总的Java堆调整相同的量.JavaHotSpotVMbuild23中没有此设置.


## GC 输出

### PrintGC
在jvm选项上添加上这个参数,只要遇上GC就会输出GC日志. 我们写一个测试程序
```java
public class TestJVMLogArguments {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            byte[] bytes = new byte[1024 * 924 * 7];
            System.out.println("Allocate " + i);
        }
    }
}
```
我们采用虚拟机参数`-XX:+PrintGC -Xmx10M -Xms10M`看一下结果
```xml
Allocate 0
[GC (Allocation Failure)  8003K->7116K(9728K), 0.0013420 secs]
[Full GC (Ergonomics)  7116K->624K(9728K), 0.0063270 secs]
Allocate 1
[Full GC (Ergonomics)  7191K->592K(9728K), 0.0068230 secs]
Allocate 2
```
我们看到一共进行里3次GC(1次新生代GC, 俩次Full GC)
1. GC前, 堆使用内存为8003K, GC后堆内存使用为7116K, 当前堆可用的堆内存为9728K, 本次GC耗时0.0013420秒
2. GC前, 堆使用内存为7116K, GC后堆内存使用为624K, 当前堆可用的堆内存为9728K, 本次GC耗时0.0063270秒
3. GC前, 堆使用内存为7191K, GC后堆内存使用为592K, 当前堆可用的堆内存为9728K, 本次GC耗时0.0068230秒

### PrintGCDetails
这个参数相比于PrintGC,会输出更加详细的信息. 同样使用上面的测试程序, 然后使用虚拟机参数`-XX:+PrintGCDetails -Xmx10M -Xms10M`, 然后看一下输出
```xml
Allocate 0
[GC (Allocation Failure) [PSYoungGen: 1535K->512K(2560K)] 8003K->7136K(9728K), 0.0020620 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[Full GC (Ergonomics) [PSYoungGen: 512K->0K(2560K)] [ParOldGen: 6624K->623K(7168K)] 7136K->623K(9728K), [Metaspace: 3089K->3089K(1056768K)], 0.0067020 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
Allocate 1
[Full GC (Ergonomics) [PSYoungGen: 77K->0K(2560K)] [ParOldGen: 7091K->592K(7168K)] 7168K->592K(9728K), [Metaspace: 3092K->3092K(1056768K)], 0.0070690 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
Allocate 2
Heap
 PSYoungGen      total 2560K, used 78K [0x00000007bfd00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 2048K, 3% used [0x00000007bfd00000,0x00000007bfd138e0,0x00000007bff00000)
  from space 512K, 0% used [0x00000007bff00000,0x00000007bff00000,0x00000007bff80000)
  to   space 512K, 0% used [0x00000007bff80000,0x00000007bff80000,0x00000007c0000000)
 ParOldGen       total 7168K, used 7060K [0x00000007bf600000, 0x00000007bfd00000, 0x00000007bfd00000)
  object space 7168K, 98% used [0x00000007bf600000,0x00000007bfce5308,0x00000007bfd00000)
 Metaspace       used 3099K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 340K, capacity 386K, committed 512K, reserved 1048576K
```
GC过程为
1. 新生代GC, 新生代已用内存从1535K变为512K, 新生代可用内存为2560K. 整个堆内存从8003K变为7136K,整个堆可用内存为9728K, 耗时为0.0020620 秒.
2. Full GC, 新生代将已用内存512k清空, 可用内存为2560K. 老年代已用内存从7091K变成592K, 整个老年代可用内存为7168K. 整个堆内存已用内存从7136K变成623K, 整个堆可用内存为9728K. Metaspace区(Java8里的方法区)没有回收, 整个Full GC耗时0.0067020 秒.

最后还输出了虚拟机退出时, 整个虚拟机的内存分布情况
1. 新生代总共有2560K的内存, 使用了78K
2. 新生代eden区为2048K, 使用了3%
3. 新生代survivor区(from部分)为512k, 没有使用
4. 新生代survivor区(to部分)为512k, 没有使用
5. 老年代(ParOld垃圾回收器) 总共内存为7168K, 使用里7060K.

### PrintHeapAtGC
这个参数会在GC前后打印出堆内信息
```java
public class TestJVMLogArguments {
    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            byte[] bytes = new byte[1024 * 924 * 7];
            System.out.println("Allocate " + i);
        }
    }
}
```
我们使用虚拟机参数`-XX:+PrintHeapAtGC -Xmx10M -Xms10M`运行程序输出结果
```xml
Allocate 0
{Heap before GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 1535K [0x00000007bfd00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 2048K, 74% used [0x00000007bfd00000,0x00000007bfe7fe40,0x00000007bff00000)
  from space 512K, 0% used [0x00000007bff80000,0x00000007bff80000,0x00000007c0000000)
  to   space 512K, 0% used [0x00000007bff00000,0x00000007bff00000,0x00000007bff80000)
 ParOldGen       total 7168K, used 6468K [0x00000007bf600000, 0x00000007bfd00000, 0x00000007bfd00000)
  object space 7168K, 90% used [0x00000007bf600000,0x00000007bfc51010,0x00000007bfd00000)
 Metaspace       used 3097K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 512K [0x00000007bfd00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 2048K, 0% used [0x00000007bfd00000,0x00000007bfd00000,0x00000007bff00000)
  from space 512K, 100% used [0x00000007bff00000,0x00000007bff80000,0x00000007bff80000)
  to   space 512K, 0% used [0x00000007bff80000,0x00000007bff80000,0x00000007c0000000)
 ParOldGen       total 7168K, used 6616K [0x00000007bf600000, 0x00000007bfd00000, 0x00000007bfd00000)
  object space 7168K, 92% used [0x00000007bf600000,0x00000007bfc763a8,0x00000007bfd00000)
 Metaspace       used 3097K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=2 (full 1):
 ...
}
Allocate 1
```
限于篇幅, 我只输出了一次的GC日志. 大部分的内容解释我们都可以在`PrintGCDetails`例子解释中找到答案. 需要特别指出的是
* invocations=1 : 代表这是第一次GC
* (full 0) : 表示虚拟机进行的Full GC的次数

### GC开始时间
`PrintGCTimeStamps`参数会在每次GC的时候,输出GC发生的时间. 我们还是使用上面的测试程序, 指定虚拟机参数`-XX:+PrintGC -XX:+PrintGCTimeStamps -Xmx10M -Xms10M`看一下输出结果
```xml
Allocate 0
0.285: [GC (Allocation Failure)  8003K->7124K(9728K), 0.0015210 secs]
0.286: [Full GC (Ergonomics)  7124K->622K(9728K), 0.0072690 secs]
Allocate 1
```
1. 第一次GC发生在JVM启动0.285秒时进行的
2. 第二次GC发生在JVM启动0.286秒时进行的

`PrintGCDateStamps`输出比PrintGCTimeStamps可读性更好, 因为它输出的是系统时间. 仍然使用上面的测试程序, 然后使用JVM参数`-XX:+PrintGC -Xmx10M -Xms10M -XX:+PrintGCDateStamps`. 结果为
```bash
2016-05-18T17:28:34.689+0800: [GC (Allocation Failure)  2028K->1006K(9728K), 0.0076949 secs]
2016-05-18T17:28:34.741+0800: [GC (Allocation Failure)  3052K->1565K(9728K), 0.0191430 secs]
2016-05-18T17:28:34.772+0800: [GC (Allocation Failure)  1750K->1597K(9728K), 0.0088998 secs]
2016-05-18T17:28:34.781+0800: [GC (Allocation Failure)  1597K->1603K(9728K), 0.0371759 secs]
2016-05-18T17:28:34.818+0800: [Full GC (Allocation Failure)  1603K->1068K(9728K), 0.0078928 secs]
2016-05-18T17:28:34.826+0800: [GC (Allocation Failure)  1068K->1068K(9728K), 0.0185343 secs]
2016-05-18T17:28:34.844+0800: [Full GC (Allocation Failure)  1068K->1041K(9728K), 0.0176116 secs]
```
> 这个参数必须和`-XX:+PrintGC`, `-XX:+PrintGCDetails`一起使用,但是和`-XX:+PrintHeapAtGC`一起使用就无效了

### PrintGCApplicationConcurrentTime
输出应用程序从上次GC到这次GC的程序执行时间. 我们运行一下上面的程序, 看一下输出结果为
```xml
ζ java -XX:+PrintGCApplicationConcurrentTime -Xmx10M -Xms10M TestJVMLogArguments
Allocate 0
Application time: 0.0120062 seconds
Allocate 1
Application time: 0.0016650 seconds
```

### GC停顿时间
`PrintGCApplicationStoppedTime`打印程序因为GC停顿的时间. 运行一下上面的程序
```xml
ζ java -XX:+PrintGCApplicationStoppedTime -Xmx10M -Xms10M TestJVMLogArguments
Allocate 0
Total time for which application threads were stopped: 0.0076620 seconds, Stopping threads took: 0.0000163 seconds
Allocate 1
```
我们看到整个应用在Full GC时, 停顿了 0.0089770 秒.

### 各个年龄对象大小
`PrintTenuringDistribution`打印GC后新生代各个年龄对象的大小。 我们使用最开始的测试程序看一下结果
```bash
ζ java -XX:+PrintTenuringDistribution -Xmx10M -Xms10M TestJVMLogArguments
Allocate 0

Desired survivor size 524288 bytes, new threshold 7 (max 15)
Allocate 1
```

### GC日志文件
* UseGCLogFileRotation : 开启GC日志文件切分功能,前置选项-Xloggc, `-XX:+UseGCLogFileRotation`
* NumberOfGCLogFiles : 设置Gc日志文件的数量(必须大于1), `-XX:NumberOfGCLogFiles=10`
* GCLogFileSize : gc日志文件大小(必须>=8K), `-XX:GCLogFileSize=8K`
* Xloggc : `-Xloggc:<filename>`gc日志文件
我们使用测试程序
```java

```
然后指定虚拟机参数`-XX:+PrintGCDetails -XX:+PrintHeapAtGC -XX:NumberOfGCLogFiles=3 -XX:GCLogFileSize=10K -XX:+UseGCLogFileRotation -Xloggc:gc.log`来运行一下. 最后我们会看到三个文件
* gc.log.1 (11,016 字节)
* gc.log.2 (11,755 字节)
* gc.log.0.current (780 字节)
我们看到使用这四个参数已经成功将日志输出到三个文件中.
> 注意每次生成日志的时候都会将上次运行JVM的日志覆盖掉. 因此每次关服重启时, 最好将日志文件备份一下.

## 异常输出

### HeapDumpOnOutOfMemoryError
在发生内存溢出异常时是否生成堆转储快照,关闭则不生成`-XX:+HeapDumpOnOutOfMemoryError`

> `-XX:HeapDumpPath=./java_pid<pid>.hprof`:堆内存溢出存放日志目录.
更多参考[JVM内存溢出之 Heap OOM]()

### OnOutOfMemoryError
当虚拟机抛出内存溢出异常时,执行指令的命令`-XX:+OnOutOfMemoryError=...`例如
```bash
java -Xmx10M -Xms10M -Xmn6M -XX:OnOutOfMemoryError="jstack %p" TestJVMLogArguments 
```
当虚拟机内存溢出时执行jstack命令

### OnError
当虚拟机抛出ERROR异常时,执行指令的命令`-XX:+OnError=<cmd args>;<cmd args>`例如
```bash
java -Xmx10M -Xms10M -Xmn6M -XX:OnError="jstack %p > jstack;jmap %p > jmap" TestJVMLogArguments
```

> `-XX:ErrorFile=./hs_err_pid<pid>.log`:如果有Error发生,则将Error输入到该日志.

## 运行时状态

### -noverify
使用`-noverify`或者`-Xverify:none`可以关闭JVM的字节码检查, 但是强烈不推荐使用这个参数

参考[Never Disable Bytecode Verification in a Production System](https://blogs.oracle.com/buck/entry/never_disable_bytecode_verification_in)

### PrintReferenceGC
追踪系统内的软引用,弱引用,虚引用和Finallize队列的话可以使用`PrintReferenceGC`.

### verbose:class
* `-verbose:class` : 跟踪类的加载和卸载
* `-XX:+TraceClassLoading` : 追踪类的加载
* `-XX:+TraceClassUnloading` : 追踪类的卸载
我们运行最开始的那个测试程序, 看一下JVM启动时加载的类`-verbose:class`
```bash
[Opened /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.lang.Object from /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/lib/rt.jar]
```
这个特性用在使用ASM动态生成类的应用中特别有用, 因为这些类是由逻辑代码控制的, 加载这些类的行为具有一定的隐蔽性, 因此我们可以在虚拟机启动时, 加上这个参数做日志分析用.

### CITime
`-XX:+CITime`:打印`JITCompiler`的耗时

### PrintConcurrentLocks
打印J.U.C中的状态`-XX:+PrintConcurrentLocks`

### PrintInlining
打印方法内联信息`-XX:+PrintInlining`

### PrintAssembly
打印即时编译后的二进制信息

### PrintAdaptiveSizePolicy
`-XX:-PrintAdaptiveSizePolicy` :打印JVM自动划分新生代和老生代大小信息.
这个选项最好和-XX:+PrintGCDetails以及-XX:+PrintGCDateStamps或者-XX:+PrintGCTimeStamps一起使用.以GCAdaptiveSizePolicy开头的一些额外信息输出来了，survived标签表明“to” survivor空间的对象字节数。在这个例子中，survivor空间占用量是224408984字节，但是移动到old代的字节数却有10904856字节。overflow表明young代是否有对象溢出到old代，换句话说，就是表明了“to” survivor是否有足够的空间来容纳从eden空间和“from”survivor空间移动而来的对象。为了更好的吞吐量，期望在应用处于稳定运行状态下，survivor空间不要溢出。

## 系统相关

### PrintVMOptions
输出程序启动时, 虚拟机接收到命令行指定的参数. 我们指定虚拟机参数`-XX:+PrintVMOptions -Xmx10M -Xms10M`, 输出结果为
```bash
VM option '+PrintVMOptions'
```
> TODO 为什么输出只有PrintVMOptions, 而没有-Xmx10M -Xms10M呢?

### PrintCommandLineFlags
打印启动虚拟机时输入的非稳定参数`-XX:+PrintCommandLineFlags`
```xml
-XX:InitialHeapSize=10485760 -XX:MaxHeapSize=10485760 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
```

### PrintFlagsFinal
输出所有的系统的参数的值, 我们使用-XX:+PrintFlagsFinal`, 看一下结果
```bash
[Global flags]
    uintx AdaptiveSizeDecrementScaleFactor          = 4               {product}
    uintx AdaptiveSizeMajorGCDecayTimeScale         = 10              {product}
    uintx AdaptiveSizePausePolicy                   = 0               {product}
    uintx AdaptiveSizePolicyCollectionCostMargin    = 50              {product}
    uintx AdaptiveSizePolicyInitializingSteps       = 20              {product}
    uintx AdaptiveSizePolicyOutputInterval          = 0               {product}
    uintx AdaptiveSizePolicyWeight                  = 10              {product}
    uintx AdaptiveSizeThroughPutPolicy              = 0               {product}
    uintx AdaptiveTimeWeight                        = 25              {product}
     bool AdjustConcurrency                         = false           {product}
     bool AggressiveOpts                            = false           {product}
     intx AliasLevel                                = 3               {C2 product}
     bool AlignVector                               = false           {C2 product}
     intx AllocateInstancePrefetchLines             = 1               {product}
     intx AllocatePrefetchDistance                  = 192             {product}
     intx AllocatePrefetchInstr                     = 0               {product}
     intx AllocatePrefetchLines                     = 4               {product}
     intx AllocatePrefetchStepSize                  = 64              {product}
...
     bool UseVMInterruptibleIO                      = false           {product}
     bool UseXMMForArrayCopy                        = true            {product}
     bool UseXmmI2D                                 = false           {ARCH product}
     bool UseXmmI2F                                 = false           {ARCH product}
     bool UseXmmLoadAndClearUpper                   = true            {ARCH product}
     bool UseXmmRegToRegMoveAll                     = true            {ARCH product}
     bool VMThreadHintNoPreempt                     = false           {product}
     intx VMThreadPriority                          = -1              {product}
     intx VMThreadStackSize                         = 1024            {pd product}
     intx ValueMapInitialSize                       = 11              {C1 product}
     intx ValueMapMaxLoopSize                       = 8               {C1 product}
     intx ValueSearchLimit                          = 1000            {C2 product}
     bool VerifyMergedCPBytecodes                   = true            {product}
     intx WorkAroundNPTLTimedWaitHang               = 1               {product}
    uintx YoungGenerationSizeIncrement              = 20              {product}
    uintx YoungGenerationSizeSupplement             = 80              {product}
    uintx YoungGenerationSizeSupplementDecay        = 8               {product}
    uintx YoungPLABSize                             = 4096            {product}
     bool ZeroTLAB                                  = false           {product}
     intx hashCode                                  = 5               {product}
```
即时编译参数
* `CompileThreshold`:触发即时编译的阈值
* `OnStackReplacePercentage`:OSR比率,它是OSR即时编译阈值计算公司的一个参数,用于代替BackEdgeThreshold参数控制回边计数器的实际溢出阈值
* `ReservedCodeCacheSize`:即时编译器编译的代码缓存使得最大值

类型加载参数
* `UseSplitVerifier`:使用依赖StackMapTable信息的类型检查代替数据流分析,以加快字节码校验速度
* `FailOverToOldVerier`:当类型校验失败时,是否允许回到老的类型推到校验方式进行校验,如果开启则允许
* `RelaxAccessControlCheck`:在校验阶段放松对类型访问性的限制

多线程相关参数
* `UseSpinning`:开启自旋锁以免线程频繁的挂起和唤醒
* `PreBlockSpin`:使用自旋锁时默认的自旋次数
* `UseThreadPriorities`:使用本地线程优先级
* `UseBiaseLocking`:是否使用偏向锁,如果开启则使用
* `UseFastAccessorMethods`:当频繁反射执行某个方法时,生成字节码来加快反射的执行速度

性能参数
* `AggressiveOpts`:使用激进的优化特征,这些特征一般是具备正面和负面双重影响的,需要根据具体应用特点分析才能判定是否对性能有好处
* `UseLargePages`:如果可能,使用大内存分页,这项特性需要操作系统的支持
* `LargePageSizeInBytes`:使用指定大小的内存分页,这项特性需要操作系统的支持
* `StringCache`:是否使用字符串缓存,开启则使用

*给远程服务器加debug
```
-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$debug_port
```
* suspend:这个参数是用来当JVM启动之后等待debug客户端连接,如果没有debug客户端连接,那么虚拟机就会一直等待，造成假死的现象
* `-XX:+UseVMInterruptibleIO`:线程中断前或是EINTR在OS_INTRPT中对于I/O操作的结果
* `-XX:+FailOverToOldVerifier`:当新的类型检测器失败时切换到旧的认证器
* `-XX:-AllowUserSignalHandlers`:允许为java进程安装信号处理器（限于Linux和Solaris,默认关闭）
* `-XX:AllocatePrefetchStyle=1`:预取指令的产生代码风格：0-没有预取指令,1-每一次分配内存就执行预取指令,2-当执行预取代码指令时,用TLAB分配水印指针指向门
* `-XX:AllocatePrefetchLines=1`:在使用JIT生成的预读取指令分配对象后读取的缓存行数.如果上次分配的对象是一个实例则默认值是1,如果是一个数组则是3
* `-XX:+OptimizeStringConcat`:对字符串拼接进行优化
* `-XX:+UseCompressedStrings`:如果可以表示为纯ASCII的话,则用byte[]代替字符串.
* `-XX:+UseBiasedLocking`:使用偏锁.
* `-XX:LoopUnrollLimit=n`:代表节点数目小于给定值时打开循环体.
* `-XX:+PerfDataSaveToFile`:Jvm退出时保存jvmstat的二进制数据.
* `-XX:+AlwaysPreTouch`:当JVM初始化时预先对Java堆进行预先摸底(堆中每个页归零处理).
* `-XX:InlineSmallCode=n`:当编译的代码小于指定的值时,内联编译的代码.
* `-XX:InitialTenuringThreshold=7`:设置初始的对象在新生代中最大存活次数.
* `-XX:+UseCompressedOops`:使用compressedpointers.这个参数默认在64bit的环境下默认启动,但是如果JVM的内存达到32G后,这个参数就会默认为不启动,因为32G内存后,压缩就没有多大必要了,要管理那么大的内存指针也需要很大的宽度了
* `-XX:AllocatePrefetchDistance=n`:为对象分配设置预取距离.
* `-XX:MaxInlineSize=35`:内联函数最大的字节码大小.
* `-XX:-TraceClassResolution`:追踪常量池resolutions.
* `-XX:FreqInlineSize=n`:经常执行方法内联的最大字节大小
* `-XX:-TraceLoaderConstraints`:跟踪加载器的限制记录.
