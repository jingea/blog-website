category: Java
date: 2016-02-02
title: volatile 使用
---
在讲`volatile`的之前,我们先看一下java的内存模型. 我们知道当我们`new`出来一个对象的时候,这个对象会被直接分配到堆上(暂不考虑栈上分配等技术). 而程序的逻辑是在方法中定义的,方法运行在线程里也就是栈上. 因此JVM会将线程里使用的数据从堆上拷贝到线程的本地存储上. 这个过程涉及了下列8个操作
1. lock: 将堆上的变量标志为某个线程独享的状态
2. unlock: 将堆上的变量释放出来, 以便被其他线程锁定
3. read: 将某个变量从堆上拷贝到线程的工作内存上
4. load: 将已经从堆上拷贝到线程的工作内存上的变量放入到变量副本中
5. use: 将线程变量副本中的变量传递给虚拟机执行引擎. (每当虚拟机遇到一个需要使用该变量的字节码指令时,都会执行该操作)
6. assign: 将虚拟机执行引擎返回的变量的值赋值到工作变量中
7. store: 将工作变量值传递到堆内存中.
8. write: 将从线程工作变量中接受到的值写入到主内存变量中

当一个变量被`volatile`修饰后, 每次`load`操作都是从堆中获取值, `assign`的时候也是直接写回到堆中内存变量中, 而不是在线程本地变量中操作.

volatile变量具备俩种特性
* 线程可见性: 某个线程修改了被`volatile`修饰的变量后,其他线程可以里面看见这个最新的值.
* 禁止指令重排序优化


> `volatile`最适用的场景是一个线程写,多个线程读的场景. 如果有多个线程同时写的话还是需要锁或者并发容器等等进行保护

下面我们看一个指令重排序的例子
```java
public class Test {

	private static boolean stop = false;

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread(() -> {
			int i = 0;
			while (!stop) {
				i++;
			}
		});
		thread.start();
		TimeUnit.SECONDS.sleep(3);
		stop = true;
	}
}
```
上面的这段代码会被优化成(这种优化也被称为提升优化)
```java
public class Test {

	private static boolean stop = false;

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread(() -> {
			int i = 0;
			if (!stop) {
				while (true) {
					i++;
				}
			}
		});
		thread.start();
		TimeUnit.SECONDS.sleep(3);
		stop = true;
	}
}
```
但是如果`stop`变量被`volatile`修饰后
```java
public class Test {

	private static volatile boolean stop = false;

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread(() -> {
			int i = 0;
			while (!stop) {
				i++;
			}
		});
		thread.start();
		TimeUnit.SECONDS.sleep(3);
		stop = true;
	}
}
```
程序就能正确的停止运行了

> Java中对于重排序是这样规定的, 只要在单线程环境中, 重排序前后代码的运行结果总是一致的, 那么这段代码的重排序就是合法的. 但是当在多线程的环境中, 重排序就会影响到程序的执行, 就像刚才我们的例子展示的那样. 例外还有一点值得说明的是, 当代码中运行时包含`native`方法时, 会打断编译器的重排序(例如`System.out.println()`或者`Threads.sleep()`)

`volatile`并不能解决并发写的情况, 正如我们开头所说的`volatile`最适用的场景是一个线程写,多个线程读的场景. 例如下面的程序, 无论我是否对`counter`进行`volatile`修饰都不能解决并发异常的问题
```java
public class Test {

	private static volatile int counter = 0;

	public static void main(String[] args) throws InterruptedException {
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < 100000; i++) {
			Thread thread = new Thread(() -> {
				// 并发写counter
				try {
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				counter++;
			});

			thread.start();
			threads.add(thread);
		}

		threads.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		System.out.println(counter);
	}
}
```
上面的程序最后的输出结果, 总是小于100000.

> 还有一点需要说明的是,`volatile`修饰的数组,只能保证数组本身的内存可见性,但是对于其中的元素的修改是不会保证的.
