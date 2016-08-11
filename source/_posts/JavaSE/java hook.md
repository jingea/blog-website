category: JavaSE
date: 2015-03-08
title: JAVA钩子程序
---
## 简介
触发的时机有：
1. 当所有的非deamon线程(守护线程)结束, 或者调用了`Systrem.exit()`方法 而导致的程序正常的退出
2. JVM收到需要关闭自己的信号（比如SIGINT、SIGTERM等，但像SIGKILL，JVM就没有机会去处理了），也或者发生如系统关闭这种不可阻挡的事件。

对于addShutdownHook中的钩子代码，也是有一些要注意的地方，下面列举几点：
1. 关闭钩子可以注册多个，在关闭JVM时就会起多个线程来运行钩子。通常来说，一个钩子就足够了，但如果需要启用多个钩子，就需要注意并发带来的问题。
2. 钩子里也要注意对异常的处理，如果不幸抛出了异常，那么钩子的执行序列就会被终止。
3. 在钩子运行期间，工作线程也在运行，需要考虑到工作线程是否会对钩子的执行带来影响，我最近发现的一个bug就是这种情况
4. 钩子里的代码尽可能简洁，否则当像系统关闭等情景可能钩子来不及运行完JVM就被退出了。


## 信号触发
使信号触发JVM的钩子程序
```java
public class HookTest {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Hook());
		while(true){}
	}

	static class Hook extends Thread{

		@Override
		public void run() {
			System.out.println("Hook execute!!!");
		}
	}
}
```

运行钩子程序
```java
nohup java HookTest &
```
关闭程序
```java
kill HookTest_PID
```
我们可以在nohup程序中看到Hook execute!!!输出

我从[JVMs and kill signals](http://journal.thobe.org/2013/02/jvms-and-kill-signals.html)看到一篇博客, 这个上面总结了哪些信号会导致JVM运行Hook
```java
signal			shutdown	runs hook	exit code	comment
default (15)	yes			yes			143			SIGTERM is the default unix kill signal
0				no			-			-	
1 (SIGHUP)		yes			yes			129	
2 (SIGINT)		yes			yes			130			SIGINT is the signal sent on ^C
3 (SIGQUIT)		no			-			-			触发 JVM dump threads / stack-traces
4 (SIGILL)		yes			no			134			触发 JVM 输出一个 core dump 文件, 同时abort on trap 6
5				yes			no			133			Makes the JVM exit with "Trace/BPT trap: 5"
6 (SIGABRT)		yes			no			134			Makes the JVM exit with "Abort trap: 6"
7				yes			no			135			Makes the JVM exit with "EMT trap: 7"
8 (SIGFPE)		yes			no			134			Makes the JVM write a core dump and abort on trap 6
9 (SIGKILL)		yes			no			137			The JVM is forcibly killed (exits with "Killed: 9")
10 (SIGBUS)		yes			no			134			Emulates a "Bus Error"
11 (SIGSEGV)	yes			no			134			Emulates a "Segmentation fault"
12				yes			no			140			Makes the JVM exit with "Bad system call: 12"
13				no			-			-			
14				yes			no			142			Makes the JVM exit with "Alarm clock: 14"
15 (SIGTERM)	yes			yes			143			This is the default unix kill signal
16				no			-			-			
17				no			-			145			Stops the application (sends it to the background), same as ^Z
18				no			-			146			Stops the application (sends it to the background), same as ^Z
19				no			-			-			
20				no			-			-			
21				no			-			149			Stops the application (sends it to the background), same as ^Z
22				no			-			150			Stops the application (sends it to the background), same as ^Z
23				no			-			-			
24				yes			no			152			Makes the JVM exit with "Cputime limit exceeded: 24"
25				no			-			-			
26				yes			no			154			Makes the JVM exit with "Virtual timer expired: 26"
27				yes			no			155			Makes the JVM exit with "Profiling timer expired: 27"
28				no			-			-			
29				no			-			-			
30				yes			no			158			Makes the JVM exit with "User defined signal 1: 30"
31				yes			no			134			Makes the JVM exit on Segmentation fault
```

## 内存溢出触发
测试JVM栈溢出后调用钩子程序
```java
public class HookTest {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Hook());
		exec();
	}

	public static void exec() {
		exec();
	}

	static class Hook extends Thread{

		@Override
		public void run() {
			System.out.println("Hook execute!!!");
		}
	}
}
```
运行后输出为
```bash
D:\testOOM>java HookTest
Exception in thread "main" java.lang.StackOverflowError
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	...
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
	at HookTest.exec(HookTest.java:9)
Hook execute!!!

D:\testOOM>
```

为了测试在更加复杂的环境下, Hook的使用情况, 看下面的测试代码
```java
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HookTest {

	private static Map<String, String> cache = new HashMap<>();

	public static void main(String[] args) {
		cache.put("abc", "abc");

		Runtime.getRuntime().addShutdownHook(new Hook());

		byte[] bytes = new byte[1024 * 1024 *1024 * 1024];
	}

	static class Hook extends Thread{

		@Override
		public void run() {
			for (int i = 0; i < 100; i++) {
				System.out.println(LocalDateTime.now());
				System.out.println("    freeMemory ： " + Runtime.getRuntime().freeMemory());
				System.out.println("    maxMemory ： " + Runtime.getRuntime().maxMemory());
				System.out.println("    totalMemory ： " + Runtime.getRuntime().totalMemory());
				System.out.println("    currentThread name : " + Thread.currentThread().getName());
				System.out.println("    cache size : " + cache.size());
				cache.put(LocalDateTime.now().toString(), LocalDateTime.now().toString());
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
```
运行后的输出结果为
```bash
ζ java HookTest
2016-07-09T16:12:12.479
    freeMemory : 155922512
    maxMemory : 2375024640
    totalMemory : 160956416
    currentThread name : Thread-0
    cache size : 1
2016-07-09T16:12:13.480
    freeMemory : 155922512
    maxMemory : 2375024640
    totalMemory : 160956416
    currentThread name : Thread-0
    cache size : 2
2016-07-09T16:12:14.480
    freeMemory : 155922512
    maxMemory : 2375024640
    totalMemory : 160956416
    currentThread name : Thread-0
    cache size : 3
2016-07-09T16:12:15.480
    freeMemory : 155922512
    maxMemory : 2375024640
    totalMemory : 160956416
    currentThread name : Thread-0
    cache size : 4
...
```

## 正常结束触发
测试程序正常结束后也会调用钩子程序
```java
public class HookTest {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Hook());
	}

	static class Hook extends Thread{

		@Override
		public void run() {
			System.out.println("Hook execute!!!");
		}
	}
}
```
运行结果为
```bash
D:\testOOM>java HookTest
Hook execute!!!

D:\testOOM>
```

## 调用exit()触发
```java
public class HookTest {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Hook());
		System.exit(0);

		System.out.println("Main over");
	}

	static class Hook extends Thread{

		@Override
		public void run() {
			System.out.println("Hook execute!!!");
		}
	}
}
```
运行结果为
```bash
D:\testOOM>java HookTest
Hook execute!!!

D:\testOOM>
```

## 不被触发
尽管上面列举出了N多触发钩子程序的示例, 但是并不保证这个钩子程序总是能被触发执行的, 例如
* JVM内部发生错误, 可能还没有来得及触发钩子程序, JVM就挂掉了(JVM 发生内部错误, 有没有日志呢?)
* 还有上面我们给出的那个信号表, 如果操作系统发送出上面的信号的话, 同样的, JVM没有执行钩子程序就退出了
* 还有调用`Runime.halt()`函数也不会执行钩子程序