category: JVM
date: 2014-09-03
title: JVM 内存分配
---
## 栈
JVM栈和常用的数据结构很相似,都是一种先进后出的数据结构. JVM栈是每个线程私有的内存空间.线程的基本行为就是方法调用, 而方法调用就是通过JVM栈传递的.每当我们创建一个线程对象的的时候, 都会创建一个JVM栈. 它的生命周期与线程相同.

JVM栈是由JVM栈帧组成的, 每次方法调用都会有一个JVM栈帧进入JVM栈,也称入栈, 当方法执行完(不管是return还是异常),栈帧都会被弹出JVM栈,也称出栈. 栈帧包含如下结构:
* PC寄存器
* 本地方法栈
* 局部变量表
* 操作数栈
* 动态连接
* 方法出口

在java虚拟机中.对这个区域规定了俩种异常情况:
1. 如果请求的栈深度大于虚拟机所允许的深度,抛出`StackOverflowError`
2. 如果虚拟机可以动态扩展,当拓展时无法申请到足够的内存时会抛出`OutOfMemoryError`异常

### PC寄存器
每当我们创建一个线程的时候, 都会JVM都会附带着创建一个本线程私有的PC寄存器和虚拟机栈. PC寄存器用于存放当前线程执行的字节码指令(线程当前方法)地址. 字节码解释器通过修改寄存器里的值使线程完成下一个指令的执行. 分支,循环,跳转,异常处理,线程恢复等基础功能都需要依赖这个寄存器完成. 在一个单CPU的环境中, 一个多线程的程序通过轮流切换线程完成多线程运行. 那么在切换线程的时候, 被切换的线程对应的寄存器里的值被保存了下来, 当线程再切换回来的时候,线程得以继续运行.

> PC寄存器是唯一一个在java虚拟机规范中没有规定任何`OutOfMemoryError`情况的区域.

### 本地方法栈
* 用来支持native方法

### 操作数栈
每个栈帧内部都包含一个称为操作数栈的先进后出栈. 同局部变量表一样,操作数栈的最大深度也是在编译的时候被写入到Code属性的max_stacks数据项之中的.操作数栈的每一个元素都可以是任意的java数据类型,包括long和double(一个long或者double类型的数据会占用俩个单位的栈深度, 其他类型占用一个单位的栈深度). 32位的数据类型所占的栈容量为1,64位数据类型所占的栈容量为2.在方法执行的时候,操作数栈的深度都不会超过在max_stacks数据项中设定的最大值. 

栈帧在刚创建的时候, 操作数栈是空的, JVM提供了一系列指令从局部变量表或者对象实例的字段中复制常量或变量值到操作数栈中.也提供了一些列指令从操作数栈取走, 操作数据, 以及把结果重新入栈. 也就是入栈和出栈操作.例如:在做算术运算的时候是通过操作数栈来进行的,又或者在调用其他方法的时候是通过操作数栈来进行参数传递的.参考[字节码指令](http://www.ming15.wang/2014/09/08/jvm/%E5%AD%97%E8%8A%82%E7%A0%81%E6%8C%87%E4%BB%A4/)

例如,整数加法的字节码指令iadd在运行的时候要求操作数栈中最接近栈顶的俩个元素已经存入了俩个int型的数值,当执行这个指令时,会将这俩个int值出栈并相加,然后将相加的结果入栈.

另外,在概念模型中,俩个栈帧为虚拟机栈的元素,相互之间是完全独立的.但是大多数虚拟机的实现里都会做一些优化处理,令俩个栈帧出现一部分重叠.让下面栈帧的部分操作数栈与上面栈帧的部分局部变量表重叠在一起,这样在进行方法调用时就可以共有一部分数据,而无需进行额外的参数复制传递:

### 局部变量表
局部变量表存放基本类型的数据和对象的引用,但对象本身不存放在栈中,而是存放在堆中.
* 其长度在编译器决定
* 一个局部变量称为一个Slot.每个Slot只可以保存一个`boolean, byte, char, short, int, float, reference,returnAddress`类型的数据.`long`或者`double`需要俩个Slot保存.
* 局部变量表来完成方法调用时的参数传递. (如果是实例方法, 第0个局部变量是用来存储调用实例方法的对象的引用)

局部变量表中的Slot是可重用的, 我们看下面的例子:
```java
public class CollectSlot {
	public static void main(String[] args) {
		byte[] byes3m = new byte[3 * 1024 * 1024];
		System.gc();
	}
}
```
运行一下上面的程序, 我们得到下面的结果
```bash
ζ java -XX:+PrintGCDetails -XX:MaxNewSize=1m -Xmx10M -Xms10M CollectSlot
[GC (Allocation Failure) [PSYoungGen: 509K->488K(1024K)] 509K->504K(9728K), 0.0004559 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (System.gc()) [PSYoungGen: 745K->488K(1024K)] 3833K->3656K(9728K), 0.0005722 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC (System.gc()) [PSYoungGen: 488K->0K(1024K)] [ParOldGen: 3168K->3614K(8704K)] 3656K->3614K(9728K), [Metaspace: 2572K->2572K(1056768K)], 0.0045062 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
Heap
 PSYoungGen      total 1024K, used 10K [0x00000000ffe80000, 0x0000000100000000, 0x0000000100000000)
  eden space 512K, 2% used [0x00000000ffe80000,0x00000000ffe82a68,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 8704K, used 3614K [0x00000000ff600000, 0x00000000ffe80000, 0x00000000ffe80000)
  object space 8704K, 41% used [0x00000000ff600000,0x00000000ff987840,0x00000000ffe80000)
 Metaspace       used 2578K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Java HotSpot(TM) 64-Bit Server VM warning: NewSize (1536k) is greater than the MaxNewSize (1024k). A new max generation size of 1536k will be used.
```
在启动程序的时候, 我们将JVM堆内存设置为10M, 新生代为1M, 当我们在应用程序中分配3M内存的时候, byes3m这个对象就直接分配在了老年代中. 从GC日志的第一条中我们也可以看出, `[PSYoungGen: 509K->488K(1024K)]` 新生代已经使用了509k, 回收后488K, 总共1024k. 当调用`System.gc()`我们发现永久代的内存并没有回收掉，这也正是我们的预期

然后我们修改一下那个程序
```java
public class CollectSlot {
	public static void main(String[] args) {
		byte[] byes3m = new byte[3 * 1024 * 1024];
		byes3m = null;
		byte[] byes3m_ = new byte[3 * 1024 * 1024];
		System.gc();
	}
}
```
我们将byes3m置为null, 看看其占用的内存会不会回收掉
```bash
ζ java -XX:+PrintGCDetails -XX:MaxNewSize=1m -Xmx10M -Xms10M CollectSlot
[GC (Allocation Failure) [PSYoungGen: 509K->496K(1024K)] 509K->528K(9728K), 0.0005626 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (System.gc()) [PSYoungGen: 753K->488K(1024K)] 6929K->6744K(9728K), 0.0006016 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC (System.gc()) [PSYoungGen: 488K->0K(1024K)] [ParOldGen: 6256K->3614K(8704K)] 6744K->3614K(9728K), [Metaspace: 2572K->2572K(1056768K)], 0.0045914 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
Heap
 PSYoungGen      total 1024K, used 10K [0x00000000ffe80000, 0x0000000100000000, 0x0000000100000000)
  eden space 512K, 2% used [0x00000000ffe80000,0x00000000ffe82a68,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 8704K, used 3614K [0x00000000ff600000, 0x00000000ffe80000, 0x00000000ffe80000)
  object space 8704K, 41% used [0x00000000ff600000,0x00000000ff987840,0x00000000ffe80000)
 Metaspace       used 2578K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Java HotSpot(TM) 64-Bit Server VM warning: NewSize (1536k) is greater than the MaxNewSize (1024k). A new max generation size of 1536k will be used.
```
好，我们看到了`ParOldGen: 6256K->3614K(8704K)` 这句话, 说明已经有3M的内存被回收掉了。 
> 赋null值的操作在经过虚拟机JIT编译器优化之后会被消除掉,这时候将变量设置为null实际上是没有意义的.因为我们的方法调用还没有达到JIT编译的次数, 因此在上面的例子中, 赋null值还是管用的, 但是在平时编码时,我们还是尽量不要依赖这种null赋值的操作

下面我们再修改一下程序, 将其放在代码块中，这样placeholder1的slot就会被placeholder2复用, 
```java
public class CollectSlot {
	public static void main(String[] args) {
		{
			byte[] byes3m = new byte[3 * 1024 * 1024];
		}
		byte[] byes3m1 = new byte[3 * 1024 * 1024];
		System.gc();
	}
}
```
运行结果为
```bash
ζ java -XX:+PrintGCDetails -XX:MaxNewSize=1m -Xmx10M -Xms10M CollectSlot
[GC (Allocation Failure) [PSYoungGen: 509K->472K(1024K)] 509K->472K(9728K), 0.0006416 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[GC (System.gc()) [PSYoungGen: 729K->488K(1024K)] 6873K->6752K(9728K), 0.0038950 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC (System.gc()) [PSYoungGen: 488K->0K(1024K)] [ParOldGen: 6264K->3614K(8704K)] 6752K->3614K(9728K), [Metaspace: 2572K->2572K(1056768K)], 0.0045731 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
Heap
 PSYoungGen      total 1024K, used 10K [0x00000000ffe80000, 0x0000000100000000, 0x0000000100000000)
  eden space 512K, 2% used [0x00000000ffe80000,0x00000000ffe82a68,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 8704K, used 3614K [0x00000000ff600000, 0x00000000ffe80000, 0x00000000ffe80000)
  object space 8704K, 41% used [0x00000000ff600000,0x00000000ff987840,0x00000000ffe80000)
 Metaspace       used 2578K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Java HotSpot(TM) 64-Bit Server VM warning: NewSize (1536k) is greater than the MaxNewSize (1024k). A new max generation size of 1536k will be used.
```
在上面的GC日志中,我们同样看到`ParOldGen: 6264K->3614K(8704K)`说明在代码块里面的那3M内存也已经被回收掉了. 这段内存能被回收的关键就是byes3m1复用了byes3m局部变量表中的Slot.因此byes3m原来指向的堆内存就不存在引用了,在GC时,这段内存就被回收掉了.但是如果没有byes3m1这个对象创建的话,byes3m的虽然离开了其作用域,但是由于GCRoots还关联着对其的引用,因此也是不会被回收的.

这种代码在绝大部分情况下影响都非常小, 但是如果一个方法中有一些很耗时的操作, 同时又分配了很大的内存, 将这些不再使用的占大内存的变量放到代码块中就是一个比较好的操作了，所以我们应该以恰当的作用域来控制变量回收时间。

关于局部变量表,还有一点可能会对实际开发产生影响,就是局部变量表不像前面介绍的类变量那样存在"准备阶段".类变量有俩次赋初始值的过程,一次在准备阶段,赋予系统初始值.另外一次在初始化阶段,赋予程序员定义的初始化. 因此即使在初始化阶段程序员没有为类变量赋值也没关系,类变量仍然具有一个确定的初始值. 但是局部变量就不一样了,如果一个局部变量定义了但没有赋初始值是不能使用的.


## 堆
我们首先看一下JVM堆内存的特点
* 是供各个线程共享的运行时内存
* 所有类实例和数组对象分配内存的地方
* 存储了内存管理系统(GC)
* 堆内存可以处于物理上不连续的内存空间中,逻辑上是连续的即可.
* 如果在堆内中没有内存完成实例分配,而且堆无法再拓展时,会抛出OutOfMemoryError
* 随着JIT编译器的发展和逃逸分析技术的逐渐成熟,栈上分配,标量替换优化技术将会导致一些变化,所有的对象在堆上分配也不是那么绝对了

然后我们看一下堆内存内部分配: 由于现在GC收集器基本都是采用的分代收集算法,所以java堆还可以细分为:新生代和老年代.分的再细一点还有Eden空间,From Survivor空间,To Sruvivor空间.

### 内存分配
1. 新生代GC(`Minor GC`)：新生代GC, Java对象大多都朝生夕灭,所以`Minor GC`非常频繁,回收速度也比较快.
2. 老年代GC(`Major GC/Full GC`)：老年代GC,出现了Major GC,经常会伴随至少一次的Minor GC. MajorGC的速度一般会比Minor GC慢10倍以上.


### 引用计数算法
引用计数算法很难解决对象之间相互循环引用的问题
```java
public class ReferenceCountingGC {

	public Object instance = null;
	private static final int _1MB = 3 * 1024 * 1024;
	private byte[] bigSize = new byte[_1MB];

	public static void main(String[] args) {
		{
			ReferenceCountingGC obj1 = new ReferenceCountingGC();
			ReferenceCountingGC obj2 = new ReferenceCountingGC();

			obj1.instance = obj2;
			obj2.instance = obj1;
		}
		System.gc();
	}
}
```
我们运行`-XX:+PrintGCDetails -Xmx10M -Xms10M`得到结果为
```bash
[GC (System.gc()) [PSYoungGen: 1650K->504K(2560K)] 7794K->7001K(9728K), 0.0029060 secs] [Times: user=0.05 sys=0.00, real=0.00 secs]
[Full GC (System.gc()) [PSYoungGen: 504K->0K(2560K)] [ParOldGen: 6497K->6952K(7168K)] 7001K->6952K(9728K), [Metaspace: 3051K->3051K(1056768K)], 0.0104574 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
Heap
 PSYoungGen      total 2560K, used 41K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 2% used [0x00000000ffd00000,0x00000000ffd0a560,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 7168K, used 6952K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 96% used [0x00000000ff600000,0x00000000ffcca158,0x00000000ffd00000)
 Metaspace       used 3058K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 331K, capacity 386K, committed 512K, reserved 1048576K
```
我们看到GC之后这块内存并没有回收掉

### 根搜索算法
这个算法的基本思想是:通过一系列的名为"GC Roots"的对象作为起始点, 从这些起始点开始向下搜索,搜索所走过的路径称为引用链,当一个对象到GC Roots没有任何引用链时,则证明这个对象是不可到达的.

在java中可作为GC Roots的对象包括以下几种:
1. 虚拟机栈(栈帧中的本地变量表)中的引用对象.
2. 方法区中的类静态属性引用的对象.
3. 方法区中的常量引用对象
4. 本地方法栈中JNI的引用的对象

#### 新生代
新生代分为Eden区和Survivor区(Eden有一个, Survivor有俩个). 大多数情况下,对象在新生代`Eden`区中分配.当`Eden`区没有足够的空间进行分配时,虚拟机将发起一次`Minor GC`, 将存活下来的对象移动到一个Survivor区中

```java
private static final int _1MB = 1024 * 1024;

/**
  * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8
  */
public static void testAllocation() {
	    byte[] allocation1, allocation2, allocation3, allocation4;
	    allocation1 = new byte[2 * _1MB];
	    allocation2 = new byte[2 * _1MB];
	    allocation3 = new byte[2 * _1MB];
	    allocation4 = new byte[4 * _1MB];  // 出现一次Minor GC
}
```
分析如下：

1. 首先在堆中分配3个2MB大小和1个4MB大小的byte数组, 在运行时通过`-Xms20M、 -Xmx20M`和`-Xmn10M`这3个参数限制Java堆大小为20MB,且不可扩展,其中10MB分配给新生代,剩下的10MB分配给老年代.
2. `-XX:SurvivorRatio=8`决定了新生代中Eden区与一个`Survivor`区的空间比例是8比1,从输出的结果也能清晰地看到`“eden space 8192K、from space 1024K、to space 1024K”`的信息,新生代总可用空间为`9216KB`(`Eden`区+1个`Survivor`区的总容量).
3. 执行`testAllocation()`中分配`allocation4`对象的语句时会发生一次Minor GC,这次GC的结果是新生代6651KB变为148KB,而总内存占用量则几乎没有减少(因为allocation1、2、3三个对象都是存活的,虚拟机几乎没有找到可回收的对象).
4. 这次GC发生的原因是给allocation4分配内存的时候,发现Eden已经被占用了6MB,剩余空间已不足以分配allocation4所需的4MB内存,因此发生Minor GC.GC期间虚拟机又发现已有的3个2MB大小的对象全部无法放入Survivor空间(Survivor空间只有1MB大小),所以只好通过分配担保机制提前转移到老年代去.
5. 这次GC结束后,4MB的allocation4对象被顺利分配在Eden中.因此程序执行完的结果是Eden占用4MB(被allocation4占用),Survivor空闲,老年代被占用6MB(被allocation1、2、3占用)

#### 老年代
大对象和长期存活的对象会进入老年代。所谓大对象就是指,需要大量连续内存空间的Java对象,最典型的大对象就是那种很长的字符串及数组. 如果连续出现多个大对象, 会导致老年代频繁发生`Full GC`, 因此在写程序时应该避免频繁出现大对象.

我们可以使用`-XX:PretenureSizeThreshold`参数令大于这个值的对象直接在老年代中分配. 这样做的目的是避免在Eden区及两个Survivor区之间发生大量的内存拷贝(新生代采用复制算法收集内存).

```java
private static final int _1MB = 1024 * 1024;

/**
  * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8
  * -XX:PretenureSizeThreshold=3145728
  */
public static void testPretenureSizeThreshold() {
	　byte[] allocation;
	　allocation = new byte[4 * _1MB];  //直接分配在老年代中
}
```
我们看到Eden空间几乎没有被使用,而老年代10MB的空间被使用了40%,也就是4MB的allocation对象直接就分配在老年代中,这是因为`PretenureSizeThreshold`被设置为3MB(就是3145728B,这个参数不能与`-Xmx`之类的参数一样直接写3MB),因此超过3MB的对象都会直接在老年代中进行分配.

> 注意`PretenureSizeThreshold`参数只对Serial和ParNew两款收集器有效,`Parallel Scavenge`收集器不认识这个参数,`Parallel Scavenge`收集器一般并不需要设置.如果遇到必须使用此参数的场合,可以考虑ParNew加CMS的收集器组合.

虚拟机给每个对象定义了一个对象年龄(Age)计数器.如果对象在Eden出生并经过第一次Minor GC后仍然存活,	并且能被Survivor容纳的话,将被移动到Survivor空间中,并将对象年龄设为1.对象在Survivor区中每熬过一次Minor GC,年龄就增加1岁,当它的年龄增加到一定程度(默认为15岁)时,就会被晋升到老年代中.对象晋升老年代的年龄阈值,可以通过参数`-XX:MaxTenuringThreshold`来设置.

大家可以分别以`-XX:MaxTenuringThreshold=1`和`-XX:MaxTenuringThreshold=15`两种设置来执行刚才示例. 例子中allocation1对象需要256KB的内存空间,Survivor空间可以容纳.当MaxTenuringThreshold=1时,allocation1对象在第二次GC发生时进入老年代,新生代已使用的内存GC后会非常干净地变成0KB.而MaxTenuringThreshold=15时,第二次GC发生后,allocation1对象则还留在新生代Survivor空间,这时候新生代仍然有404KB的空间被占用.

实例代码
```java
private static final int _1MB = 1024 * 1024;

/**
  * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M
  * -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=1
  * -XX:+PrintTenuringDistribution
  */
@SuppressWarnings("unused")
public static void testTenuringThreshold() {
	 byte[] allocation1, allocation2, allocation3;
	 allocation1 = new byte[_1MB / 4];
	  // 什么时候进入老年代取决于XX:MaxTenuringThreshold设置
	 allocation2 = new byte[4 * _1MB];
	 allocation3 = new byte[4 * _1MB];
	 allocation3 = null;
	 allocation3 = new byte[4 * _1MB];
}
```

##### 动态年龄判断

为了能更好地适应不同程序的内存状况,虚拟机并不总是要求对象的年龄必须达到`MaxTenuringThreshold`才能晋升老年代,如果在`Survivor`空间中相同年龄所有对象大小的总和大于`Survivor`空间的一半,年龄大于或等于该年龄的对象就可以直接进入老年代,无须等到`MaxTenuringThreshold`中要求的年龄.

例如下例中设置参数`-XX: MaxTenuringThreshold=15`,会发现运行结果中`Survivor`的空间占用仍然为0%,而老年代比预期增加了`6%`,也就是说`allocation1、allocation2`对象都直接进入了老年代,而没有等到15岁的临界年龄.因为这两个对象加起来已经达到了512KB,并且它们是同年的,满足同年对象达到Survivor空间的一半规则.我们只要注释掉其中一个对象的new操作,就会发现另外一个不会晋升到老年代中去了.

示例代码
```java
	private static final int _1MB = 1024 #### 1024;

	/**
	  * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M
	  * -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=15
	  * -XX:+PrintTenuringDistribution
	  */
	@SuppressWarnings("unused")
	public static void testTenuringThreshold2() {
		 byte[] allocation1, allocation2, allocation3, allocation4;
		 allocation1 = new byte[_1MB / 4];
		  // allocation1+allocation2大于survivor空间的一半
		 allocation2 = new byte[_1MB / 4];
		 allocation3 = new byte[4 #### _1MB];
		 allocation4 = new byte[4 #### _1MB];
		 allocation4 = null;
		 allocation4 = new byte[4 #### _1MB];
	}
```

#### 空间分配担保

在发生Minor GC时,虚拟机会检测之前每次晋升到老年代的平均大小是否大于老年代的剩余空间大小,如果大于,则改为直接进行一次Full GC.如果小于,则查看HandlePromotionFailure设置是否允许担保失败;如果允许,那只会进行Minor GC;如果不允许,则也要改为进行一次Full GC.

前面提到过,新生代使用复制收集算法,但为了内存利用率,只使用其中一个Survivor空间来作为轮换备份,因此当出现大量对象在Minor GC后仍然存活的情况时(最极端就是内存回收后新生代中所有对象都存活),就需要老年代进行分配担保,让Survivor	无法容纳的对象直接进入老年代.与生活中的贷款担保类似,老年代要进行这样的担保,前提是老年代本身还有容纳这些对象的	剩余空间,一共有多少对象会活下来,在实际完成内存回收之前是无法明确知道的,所以只好取之前每一次回收晋升到老年代对象容量的平均大小值作为经验值,与老年代的剩余空间进行比较,决定是否进行Full GC来让老年代腾出更多空间.

取平均值进行比较其实仍然是一种动态概率的手段,也就是说如果某次Minor GC存活后的对象突增,远远高于平均值的话,依然会导致担保失败(Handle Promotion Failure).如果出现了HandlePromotionFailure失败,	那就只好在失败后重新发起一次Full GC.虽然担保失败时绕的圈子是最大的,但大部分情况下都还是会将	HandlePromotionFailure开关打开,避免Full GC过于频繁,

示例代码
```java
	private static final int _1MB = 1024 #### 1024;

	/**
	  * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M
	  * -XX:SurvivorRatio=8 -XX:-HandlePromotionFailure
	  */
	@SuppressWarnings("unused")
	public static void testHandlePromotion() {
		 byte[] allocation1, allocation2, allocation3,
		 allocation4, allocation5, allocation6, allocation7;
		 allocation1 = new byte[2 #### _1MB];
		 allocation2 = new byte[2 #### _1MB];
		 allocation3 = new byte[2 #### _1MB];
		 allocation1 = null;
		 allocation4 = new byte[2 #### _1MB];
		 allocation5 = new byte[2 #### _1MB];
		 allocation6 = new byte[2 #### _1MB];
		 allocation4 = null;
		 allocation5 = null;
		 allocation6 = null;
		 allocation7 = new byte[2 #### _1MB];
	}
```

## 直接内存

直接内存并不是虚拟机运行时数据区的一部分,也不是java虚拟机规范中定义的内存区域,但是这部分内存也被频繁使用,而且也会导致OutOfMemoryError异常出现

在JDK1.4引入的NIO类,一种基于通道与缓冲区的I/O方式,它可以利用Native函数库直接分配堆外内存,然后通过一个存储在java堆里面的DirectByteBuffer对象作为这块内存的引用进行操作.这样能在一些场景中显著提高性能,因为避免了java堆和Native堆中来回复制数据.

显然本机直接内存的分配不会收到java堆大小的限制,但是既然是内存,则肯定会收到本机总内存(包括RAM及SWAP区或者分页文件)及处理器寻址空间的限制.一般在配置虚拟机参数时,会genuine实际内存设置-Xmx等参数信息,但经常会忽略掉直接内存,使得各个区域的总和大于物理内存限制,从而导致动态拓展时,出现OutOfMemoryError.

##  方法区
* 虚拟机启动时创建
* 供各个线程共享的运行时内存
* 存储了每个类的结构信息, 运行时常量池, 静态变量,即时编译器编译后的代码, 方法数据, 构造函数, 普通方法的字节码内容
* java虚拟机规范对这个区域的限制非常宽松,除了和java堆一样不需要连续的内存外,和可以实现固定大小或者可拓展的之外,还可以选择不实现垃圾收集.(在HotSop虚拟机中一般喜欢称这个区域为永久代)并非数据进入永久代就像其名字一样"永久存在". 这个区域的回收目标是针对常量池的回收和对类型的卸载.
* 当方法区无法满足内存分配需求时,将抛出OutOfMemoryError.

运行时常量池是方法区的一部分.

Class文件中除了有类的版本,字段,方法,接口等信息外,还有一项信息是常量池,用于存储编译器产生的各种字面量和符号引用.这部分内容将在类加载后存放到方法区的运行时常量池中.

运行时常量池相对于Class文件常量池的另外一个重要特征是具备动态性,java语言并不要求常量一定只能在编译器产生,也就是并非预置入Class文件常量池的内容才能进入方法区运行时常量池,运行期间也可能将新的常量放入常量池,这种特性被用到比较多的便是`String#intern()`在加载类和接口到虚拟机后就创建对应的常量池,其是Class文件中每个类或者接口常量池表的运行时表示.

它包含了从编译期克制的数值字面量到必须到运行期解析后才能获得的方法或字段引用

java 中的常量池,是为了方便快捷地创建某些对象而出现的,当需要一个对象时,就可以从池中取一个出来(如果池中没有则创建一个)， 则在需要重复创建相等变量时节省了很多时间 . 常量池其实也就是一个内存空间,不同于使用 `new` 关键字创建的对象所在的堆空间 . 常量池用来存放在编译期间就可以确定的数据,比如字符串等类型

在新生代,常规应用进行一次垃圾收集,一般可以收回70%-95%的空间,而永久代(方法区)远低于此.

永久代的垃圾回收主要是回收俩部分内容:
* 废弃常量: 回收废弃常量与回收java堆中的对象非常类似.以常量池字面量回收为例,如果一个字符串"ABC"已经进入了常量池,但是当前系统中没有任何一个String对象是叫做"ABC"的,换句话说也就是没有任何String对象引用这个字面量,也没有其他地方引用这个字面量,如果这个时候发生内存回收,而且必要的话,这个"ABC"常量会被清除出常量池.常量池中的其他类(皆苦),方法,字段的符号引用也与此类似.
* 无用的类

判断一个类是否是无用的类条件要苛刻的多. 要同时满足下面三个条件:
1. 该类的所有实例都已经被回收,也就是java堆中不存在该类的实例.<br.>
2. 加载该类的ClassLoader已经被回收.<br.>
3. 该类对应的java.lang.Class对象没有在任何地方被引用,无法在任何地方通过反射访问该类.

虚拟机可以对满足上面三个条件的类进行回收,这里说的仅仅是可以,而不是和对象一样,不使用了就必然回收.是否对类进行回收HotSpot虚拟机提供了-Xnoclassgc参数进行控制,还可以使用`-verbose:Class`及
`-XX:+TraceClassLoading`,`-XX:+TraceClassUnLoading`查看类的加载和卸载信息.`-verbose:Class`和`-XX:+TraceClassLoading`可以在Product版的虚拟机中使用,但是`-XX:+TraceClassLoading`参数需要fastdebug版的虚拟机支持

静态存储里存放程序运行时一直存在的数据 . 可用关键字 static 来标识一个对象的特定元素是静态的,被static 修饰的成员变量和成员方法独立于该类的任何对象,它不依赖类特定的实例,被类的所有实例共享 . 但 JAVA 对象本身不会存放在静态存储空间里,而只是把对象中的一些特殊元素放置这里 .
