category: Java
tag: JavaSE
date: 2016-02-02
title: volatile 使用
---
当一个变量被`volatile`修饰后,它将具备俩种特性
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