category: Java
tag: JavaSE
date: 2015-03-08
title: JAVA钩子程序
---

### 触发的时机有：
1. 程序正常退出或者调用System.exit方法，如果是多线程环境，要求是最后一个非守护线程终止，
2. JVM收到需要关闭自己的信号（比如SIGINT、SIGTERM等，但像SIGKILL，JVM就没有机会去处理了），也或者发生如系统关闭这种不可阻挡的事件。

### 对于addShutdownHook中的钩子代码，也是有一些要注意的地方，下面列举几点：
1. 关闭钩子可以注册多个，在关闭JVM时就会起多个线程来运行钩子。通常来说，一个钩子就足够了，但如果需要启用多个钩子，就需要注意并发带来的问题。
2. 钩子里也要注意对异常的处理，如果不幸抛出了异常，那么钩子的执行序列就会被终止。
3. 在钩子运行期间，工作线程也在运行，需要考虑到工作线程是否会对钩子的执行带来影响，我最近发现的一个bug就是这种情况，场景是钩子要关闭文件句柄，但因为同时server还接收提交请求，结果文件又被打开，造成不想要的结果。
4. 钩子里的代码尽可能简洁，否则当像系统关闭等情景可能钩子来不及运行完JVM就被退出了。

#### 使用信号触发JVM的钩子程序
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

#### 运行钩子程序
```
nohup java HookTest &
```
#### 关闭程序
```
kill HookTest_PID
```
我们可以在nohup程序中看到Hook execute!!!输出


#### 测试JVM堆栈溢出后也会调用钩子程序
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

#### 测试程序正常结束后也会调用钩子程序
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

#### 测试调用exit后直接关闭JVM
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

#### 测试
```java

```