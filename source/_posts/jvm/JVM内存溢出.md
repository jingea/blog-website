category: JVM
date: 2014-09-06
title: JVM内存溢出 
---

## ConstantPool OOM

溢出代码
```java
/**
 * 运行时常量溢出
 * VM Args: -XX:PermSize=10M -XX:MaxPermSize=10M
 * @author mingwang
 *
 */
public class RuntimeConstantPoolOOM {

	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
		int i = 0;
		while(true) {
			list.add(String.valueOf(i++).intern());
		}
	}
}
```
如果想运行时常量池添加内容最简单的方式就是String.intern()这个native方法.该方法的作用是:如果池中已经包含一个等于此String对象的字符串,则返回池中这个字符串的String对象.否则将次String对象包含的字符串添加到常量池中,并返回次String对象音乐.

## Stack OOM
溢出代码
```java
public class TestStackSOF {

	private static int stackLength = 1;
	public static void stackLeak() {
		stackLength ++;
		stackLeak();
	}

	public static void main(String[] args) {
		try {
			stackLeak();
		} catch(Throwable e) {
			System.out.println("stack length:" + stackLength + ". " + e.getMessage());
		}
	}
}
```
运行上面的程序
```bash
D:\testOOM>java -XX:+HeapDumpOnOutOfMemoryError -Xss1M TestStackSOF
stack length:22427. null
```
1M的栈空间大概能执行以上那个简单方法的22427次. 这个次数并不是在编译期就决定的,而是在运行时根据具体的内存使用情况而变化的. 

下面的代码
```java
public class Test {

        public static void stackLeak() {
                stackLeak();
        }
        public static void main(String[] args) {
                stackLeak();
        }
}
```
使用`-XX:+HeapDumpOnOutOfMemoryError`并不能产生堆内存溢出错误, 也没有产生类似于java_pid19212.hprof文件的文件.
使用`java -XX:ErrorFile=./error.log -Xss1M Test` 也没有产生错误文件
上面的并没有产生
```java
public class JavaVMStackOOM {
	private void dontStop() {
		while(true) {

		}
	}

	public void stackLeakByThread() {
		while(true) {
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					dontStop();
				}
			});
		}
	}

	public static void main(String[] args) {
		JavaVMStackOM om = new JavaVMStackOM();
		om.stackLeakByThread();
	}
}
```
以上俩个实现都都无法让虚拟机产生OutOfMemoryError异常,只能产生StackOverflowError.实验结果表明: 单个线程下,无论由于栈帧太大还是虚拟机容量太小,当内存无法分配时,虚拟机抛出的都是StackOverflowError.如果测试时不是限于单线程,通过不断建立新线程的方式倒是可以产生内存溢出异常. 但是这样产生的内存溢出异常与栈空间是否足够大并不存在任何联系,或者准确说,在这种情况下,给每个线程的栈分配的内存越大,反而越容易产生内存溢出异常.

当开发多线程应用时应该特别注意的是,出现StackOverflowError异常时有错误堆栈可以阅读,相对来说比较容易找到问题.如果使用虚拟机默认参数,栈深度在大多数情况下达到1000-2000完全没有问题,对于正常的方法调用(包括递归),这个深度应该够用了,但是如果建立过多的线程导致的内存溢出,在不能减少线程数或者更换64位虚拟机的情况下,就只能通过减少最大堆和减少栈容量来换取更多的线程.

## Metadata OOM
```java
public class JavaMethodAreaOOM {

	public static void main(String[] args) {
		while(true) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(OOMObject.class);
			enhancer.setUseCache(false);
			enhancer.setCallBack(new MethodInterceptor(){
				public Object intercept(Object obj, Method method, Object[] objs,
				MethodProxy proxy) throws Throwable {
					return proxy.invokeSuper(obj, args);
				}
			});
		}
	}

	static class OOMObject {

	}
}
```
执行代码
```java
javac JavaMethodAreaOOMRun.java
java -XX:PermSize10M -XX:MaxPermSize10M JavaMethodAreaOOMRun
```
方法区用于存放Class信息,为了测试这个区域,基本思路是产生大量的类去填充方法区,直到溢出.本例中使用的是CGLib, 还可以使用ASM等框架进行测试.方法区溢出也是一种常见的内存溢出异常.一个类如果被垃圾收集器回收,其条件是非常苛刻的. 在经常动态生成大量Class的应用中,需要特别注意类的回收状况. (基于OSGI的应用即使是同一个类文件被不同的加载器加载也会视为不同的类)


## Heap OOM
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



## DirectMemory OOM
溢出代码
```java
/**
 * VM Args: -Xmx20M -XX:MaxDirectMemorySize=10M
 */
public class DirectMemoryOOM {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws Exception {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe)unsafeField.get(null);
        while(true)
            unsafe.allocateMemory(_1MB);
    }
}
```
直接通过反射获取Unsafe实例并进行内存分配,Unsafe类的getUnsafe()方法限制了只有引导类加载器才会返回实例,也就是设计者希望只有rt.jar中的类才能使用unsafe的功能. 因为虽然使用DirectbyeBuffer分配内存也会抛出内存异常,但抛出异常时并没有真正向操作系统申请分配内存,而是通过计算得知内存无法分配,于是手动抛出异常,真正申请分配内存的方法是:unsafe.allocateMemory(_1MB);
