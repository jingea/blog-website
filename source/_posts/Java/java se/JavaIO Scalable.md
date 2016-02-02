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
* Continuous improvement with increasing resources (CPU, memory, disk, bandwidth)
* Also meet availability and performance goals(Short latencies, Meeting peak demand, Tunable quality of service)
* 分治模式一般是实现任何高可用目标的做好方式

如何实现分治模式呢？
* 将任务切分成多个小的子任务, 每个子任务执行一个非阻塞操作.
* Execute each task when it is enabled
* 


```java

```


```java

```


```java

```


```java

```