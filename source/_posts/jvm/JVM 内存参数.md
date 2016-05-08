category: JVM
date: 2014-10-08
title: JVM内存参数
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


## 垃圾收集器
### ExplicitGCInvokesConcurrent
`-XX:`:当收到System.gc()方法提交的垃圾收集申请时,使用CMS收集器收集

### UseSerialGC
`-XX:`:打开此开关后使用Serial+SerialOld的收集器组合进行内存回收.

### UseParNewGC
`-XX:`:虚拟机运行在Client模式下的默认值,打开此开关后,使用ParNew+SeialOld的收集器组合进行垃圾收集

### UseConcMarkSweepGc
`-XX:`:打开次开关后使用`ParNew+CMS+SerialOld`收集器组合进行垃圾收集.如果CMS收集器出现`ConcurrentModeFailure`,则`SeialOld`收集器将作为后备收集器.

### UseParallelGC
`-XX:`:虚拟机运行在Server模式下的默认值,打开此开关后,使用ParallelScavenge+SerialOld的收集器组合进行内存回收

### UseParaelOldGC
`-XX:`:打开此开关后,使用ParallelScavenge+ParallelOld的收集器组合进行内存回收

### ParallelGCThreads
`-XX:`:设置并行GC时进行内存回收的线程数(少于或等于8个CPU时默认值为CPU数量值,多于8个CPU时比CPU数量值小)

### CMSInitiatingOccupancyFraction
`-XX:`:设置CMS收集器在老年代空间被使用多少后触发垃圾收集

### UseCMSCompactAtFullCollection
`-XX:`:设置CMS收集器在完成垃圾收集后是否要进行一次内存碎片整理

### CMSFullGCBeforeCompaction
`-XX:`:设置CMS收集器在进行若干次垃圾收集后再启动一次内存碎片整理

### UseParallelOldGC
`-XX:-UseParallelOldGC`:所有的集合使用并行垃圾收集器.能够自动化地设置这个选项-XX:+UseParallelGC

###　ConcGCThreads
`-XX:ConcGCThreads=n`:`concurrentgarbagecollectors`使用的线程数.(默认值与JVM所在平台有关).

### UseG1GC
`-XX:+UseG1GC`:使用`GarbageFirst(G1)`收集器

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
