category: JVM
date: 2014-09-06
title: JVM内存溢出之 Heap OOM
---
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
我们还注意到使用`-XX:+HeapDumpOnOutOfMemoryError`并不能产生堆内存溢出错误, 也没有产生类似于java_pid19212.hprof文件的文件.

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
