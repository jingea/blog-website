category: JVM
date: 2014-09-02
title: JVM 堆内存
---
## 概述
我们首先看一下JVM堆内存的特点
* 是供各个线程共享的运行时内存
* 所有类实例和数组对象分配内存的地方
* 存储了内存管理系统(GC)
* 堆内存可以处于物理上不连续的内存空间中,逻辑上是连续的即可.
* 如果在堆内中没有内存完成实例分配,而且堆无法再拓展时,会抛出OutOfMemoryError
* 随着JIT编译器的发展和逃逸分析技术的逐渐成熟,栈上分配,标量替换优化技术将会导致一些变化,所有的对象在堆上分配也不是那么绝对了

然后我们看一下堆内存内部分配: 由于现在GC收集器基本都是采用的分代收集算法,所以java堆还可以细分为:新生代和老年代.分的再细一点还有Eden空间,From Survivor空间,To Sruvivor空间.

## 内存分配
1. 新生代GC(`Minor GC`)：新生代GC, Java对象大多都朝生夕灭,所以`Minor GC`非常频繁,回收速度也比较快.
2. 老年代GC(`Major GC/Full GC`)：老年代GC,出现了Major GC,经常会伴随至少一次的Minor GC. MajorGC的速度一般会比Minor GC慢10倍以上.


## 引用计数算法
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

## 根搜索算法
这个算法的基本思想是:通过一系列的名为"GC Roots"的对象作为起始点, 从这些起始点开始向下搜索,搜索所走过的路径称为引用链,当一个对象到GC Roots没有任何引用链时,则证明这个对象是不可到达的.

在java中可作为GC Roots的对象包括以下几种:
1. 虚拟机栈(栈帧中的本地变量表)中的引用对象.
2. 方法区中的类静态属性引用的对象.
3. 方法区中的常量引用对象
4. 本地方法栈中JNI的引用的对象

### 新生代
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

### 老年代
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

#### 动态年龄判断

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

### 空间分配担保

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
