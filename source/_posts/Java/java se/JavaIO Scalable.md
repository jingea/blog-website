category: Java
tag: JavaSE
date: 2016-02-02
title: Scalable IO In Java 学习笔记
---

在大多数的网络服务中都会有如下的模型
* Read request
* Decode request
* Process service
* Encode reply
* Send reply

下面我们看一个经典的服务设计
[Classic Service Designs](Classic Service Designs.jpg)
每一个handler都在它自己的线程中执行.  我们来看一下这个模式的代码实现
```java
class Server implements Runnable {
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(PORT);
			while (!Thread.interrupted())
				new Thread(new Handler(ss.accept())).start();
			// or, single-threaded, or a thread pool
		} catch (IOException ex) { /* ... */ }
	}

	static class Handler implements Runnable {
		final Socket socket;

		Handler(Socket s) {
			socket = s;
		}

		public void run() {
			try {
				byte[] input = new byte[MAX_INPUT];
				socket.getInputStream().read(input);
				byte[] output = process(input);
				socket.getOutputStream().write(output);
			} catch (IOException ex) { /* ... */ }
		}

		private byte[] process(byte[] cmd) { /* ... */ }
	}
}
```
我们知道系统的资源(CPU, 线程, 内网)是非常宝贵的, 在这种模型中每个网络请求都会建立一个新的线程, 当有大量连接同时进来的时候, 我们的资源可能很快就会被榨干. 因此我们要提供服务的可拓展性. 对于服务的可拓展性我们提出如下目标

* Graceful degradation under increasing load (more clients)
* 当我们通过增加资源的时候,性能可以有一个持续的提升 (例如CPU, memory, disk, 带宽等)
* 高可用和高性能的目标 (较短的延迟, 较高的峰值需求, 可调的服务质量)
* 分治模式一般是实现任何高可用目标的做好方式

## 分治模式
* 将任务切分成多个小的子任务, 每个子任务执行一个非阻塞操作.
* 当任务可用时，立即执行它(例如IO事件可以作为可执行条件的触发器)
* `java.nio`支持的基本机制： 非阻塞的读和写. 事件达到后立即进行分发
* 应对各种变化需求: 基于事件驱动模型.

## 事件驱动模式
* 更少的资源. 这种模式不必每个客户端连接都分配一个线程
* 更少的负载. 这种模式会带来更少的线程上下文切换, 以及线程阻塞
* 事件分发可能较慢, 需要手动地将事件绑定到action上面去

## Reactor模式
* Reactor：将IO事件转发到handler上
* Handler：执行非阻塞的操作

基本的Reactor模型
[Single threaded版本](Basic Reactor Design.jpg)
上面是一个基本Reactor模型(单线程版本)

Java Nio对其的支持
* Channels ： 实现对文件或者Socket连接的功能, 同时提供非阻塞读数据功能
* Buffers ： 可以被Channels直接读写的类似数组的对象
* Selectors ： 过滤出含有IO事件的Channels集合
* SelectionKeys ： 包含IO事件状态和绑定的对象


```java

```


```java

```


```java

```


```java

```