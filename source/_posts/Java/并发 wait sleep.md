category: Java
date: 2016-09-23
title: wait sleep 不同
---
wait和sleep都可以停止线程的运行,但是这俩点有什么不一样呢？当在加锁的代码中
* wait 会释放锁
* sleep 不会释放锁

> 当进入wait或者sleep之后,线程退出CPU占用, 操作系统都不会再给这个线程分配时间片

先看wait的例子
```java
public class TestLock {

	private static final Object lock = new Object();
	public static void main(String[] args) {
		ThreadUtil.printThreadState("thread1", "thread2");

		Runnable runnable = () -> {
			synchronized (lock) {
				System.out.println(Thread.currentThread().getName() + " Sleep");
				try {
					lock.wait(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + " Sleep Finshed");
			}
		};
		Thread thread1 = new Thread(runnable, "thread1");
		Thread thread2 = new Thread(runnable, "thread2");
		thread1.start();
		thread2.start();
	}
}
```
结果为
```bash
thread1 Sleep
thread2 Sleep
2016-09-23T15:15:31.629  thread2 -> TIMED_WAITING
2016-09-23T15:15:31.630  thread1 -> TIMED_WAITING
2016-09-23T15:15:32.529  thread2 -> TIMED_WAITING
2016-09-23T15:15:32.529  thread1 -> TIMED_WAITING
thread1 Sleep Finshed
thread2 Sleep Finshed
```
我们看到当线程1 wait之后,线程2也进入了锁中

然后我们将`lock.wait(3000);`改为`TimeUnit.SECONDS.sleep(3);`
```java
import java.util.concurrent.TimeUnit;

public class TestLock {

	private static final Object lock = new Object();

	public static void main(String[] args) {
		ThreadUtil.printThreadState("thread1", "thread2");

		Runnable runnable = () -> {
			synchronized (lock) {
				System.out.println(Thread.currentThread().getName() + " Sleep");
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + " Sleep Finshed");
			}
		};
		Thread thread1 = new Thread(runnable, "thread1");
		Thread thread2 = new Thread(runnable, "thread2");
		thread1.start();
		thread2.start();
	}
}
```
结果为
```bash
thread1 Sleep
2016-09-23T15:17:52.488  thread2 -> BLOCKED
2016-09-23T15:17:52.488  thread1 -> TIMED_WAITING
2016-09-23T15:17:53.462  thread2 -> BLOCKED
2016-09-23T15:17:53.462  thread1 -> TIMED_WAITING
2016-09-23T15:17:54.463  thread2 -> BLOCKED
2016-09-23T15:17:54.463  thread1 -> TIMED_WAITING
thread1 Sleep Finshed
thread2 Sleep
2016-09-23T15:17:55.464  thread2 -> TIMED_WAITING
2016-09-23T15:17:56.465  thread2 -> TIMED_WAITING
2016-09-23T15:17:57.466  thread2 -> TIMED_WAITING
thread2 Sleep Finshed
```
我们看到只有当线程1执行完之后线程才继续执行


在上面的例子中用到的输出线程状态信息的工具类
```java
import java.time.LocalDateTime;

public class ThreadUtil {

	public static void printThreadState(String... filter) {
		Thread print = new Thread(() -> {
			long now = System.currentTimeMillis();
			while (true) {
				if (System.currentTimeMillis() - now > 1000) {
					now = System.currentTimeMillis();
					Thread.getAllStackTraces().forEach((key, thread) -> {
						for (int i = 0; i < filter.length; i++) {
							if (key.getName().equals(filter[i])) {
								System.out.println(LocalDateTime.now() + "  " +key.getName() + " -> " + key.getState());
							}
						}
					});
				}
			}
		}, "Print");
		print.start();
	}
}
```

## 线程中断
Java 提供了中断机制,可以使用中断来结束一个线程.(使用中断来结束一个线程,要求线程检查它是否被中断了,然后决定是否响应这个中断请求. 线程允许忽略中断并继续执行(将if语句注掉就可忽略中断请求))
```java
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) {

		Thread thread = new Thread(() -> {
			while(true) {
				if (Thread.interrupted()) {
					System.out.println("interrupt  " + System.currentTimeMillis());
					break;
				}
			}
		});
		thread.start();

		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Begin interrupt");
		thread.interrupt();
	}
}
```
