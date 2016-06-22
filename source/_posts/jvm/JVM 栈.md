category: JVM
date: 2014-09-03
title: JVM栈
---
## 概述
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

## PC寄存器
每当我们创建一个线程的时候, 都会JVM都会附带着创建一个本线程私有的PC寄存器和虚拟机栈. PC寄存器用于存放当前线程执行的字节码指令(线程当前方法)地址. 字节码解释器通过修改寄存器里的值使线程完成下一个指令的执行. 分支,循环,跳转,异常处理,线程恢复等基础功能都需要依赖这个寄存器完成. 在一个单CPU的环境中, 一个多线程的程序通过轮流切换线程完成多线程运行. 那么在切换线程的时候, 被切换的线程对应的寄存器里的值被保存了下来, 当线程再切换回来的时候,线程得以继续运行.

> PC寄存器是唯一一个在java虚拟机规范中没有规定任何`OutOfMemoryError`情况的区域.

## 本地方法栈
* 用来支持native方法

## 操作数栈
每个栈帧内部都包含一个称为操作数栈的先进后出栈. 同局部变量表一样,操作数栈的最大深度也是在编译的时候被写入到Code属性的max_stacks数据项之中的.操作数栈的每一个元素都可以是任意的java数据类型,包括long和double(一个long或者double类型的数据会占用俩个单位的栈深度, 其他类型占用一个单位的栈深度). 32位的数据类型所占的栈容量为1,64位数据类型所占的栈容量为2.在方法执行的时候,操作数栈的深度都不会超过在max_stacks数据项中设定的最大值. 

栈帧在刚创建的时候, 操作数栈是空的, JVM提供了一系列指令从局部变量表或者对象实例的字段中复制常量或变量值到操作数栈中.也提供了一些列指令从操作数栈取走, 操作数据, 以及把结果重新入栈. 也就是入栈和出栈操作.例如:在做算术运算的时候是通过操作数栈来进行的,又或者在调用其他方法的时候是通过操作数栈来进行参数传递的.参考[字节码指令](http://www.ming15.wang/2014/09/08/jvm/%E5%AD%97%E8%8A%82%E7%A0%81%E6%8C%87%E4%BB%A4/)

例如,整数加法的字节码指令iadd在运行的时候要求操作数栈中最接近栈顶的俩个元素已经存入了俩个int型的数值,当执行这个指令时,会将这俩个int值出栈并相加,然后将相加的结果入栈.

另外,在概念模型中,俩个栈帧为虚拟机栈的元素,相互之间是完全独立的.但是大多数虚拟机的实现里都会做一些优化处理,令俩个栈帧出现一部分重叠.让下面栈帧的部分操作数栈与上面栈帧的部分局部变量表重叠在一起,这样在进行方法调用时就可以共有一部分数据,而无需进行额外的参数复制传递:

## 局部变量表
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