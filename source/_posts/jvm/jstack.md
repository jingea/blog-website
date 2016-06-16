category: JVM
date: 2014-10-05
title: jstack
---
## 用法
java堆栈跟踪工具. `jstack`命令用于生成虚拟机当前时刻的线程快照.线程快照就是当前虚拟机内每一条线程正在执行的方法堆栈的集合,生成线程快照的主要目的是定位线程出现长时间停顿的原因,如[线程间死锁](),[死循环](),请求外部资源
导致长时间等待.

jstack命令格式
```java
jstack [ option ] vmid
```
示例
* `jstack [-l] <pid>` 连接正在运行的进程
* `jstack -F [-m] [-l] <pid>` 连接已经宕掉的进程
* `jstack [-m] [-l] <executable> <core>` 连接一个core文件
* `jstack [-m] [-l] [server_id@]<remote server IP or hostname>` 连接一个远程的debug Server
	
option值：
* `-F`: 当正常输出的请求不被响应时,强制说出线程堆栈
* `-l`: 除堆栈外,显示关于锁的附加信息
* `-m`: 如果调用本地方法的话,可以显示c/c++的堆栈

> 当对线程堆栈分析时，首先查找`BLOCKED`, 找到锁住的线程。


## jstack日志
下面摘抄的是NETTY中空epoll的一段记录
```java
"nioEventLoopGroup-2461-1" #4955 prio=10 os_prio=0 tid=0x00007fd857e9a000 nid=0x5e19 runnable [0x00007fd7374bc000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.EPollArrayWrapper.epollWait(Native Method)
	at sun.nio.ch.EPollArrayWrapper.poll(EPollArrayWrapper.java:269)
	at sun.nio.ch.EPollSelectorImpl.doSelect(EPollSelectorImpl.java:79)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x00000000e673cf38> (a io.netty.channel.nio.SelectedSelectionKeySet)
	- locked <0x00000000e673cd30> (a java.util.Collections$UnmodifiableSet)
	- locked <0x00000000e673cc58> (a sun.nio.ch.EPollSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at io.netty.channel.nio.NioEventLoop.select(NioEventLoop.java:622)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:310)
	at io.netty.util.concurrent.SingleThreadEventExecutor$2.run(SingleThreadEventExecutor.java:116)
	at io.netty.util.concurrent.DefaultThreadFactory$DefaultRunnableDecorator.run(DefaultThreadFactory.java:137)
	at java.lang.Thread.run(Thread.java:745)

   Locked ownable synchronizers:
	- None
```

第一行数据分析
```java
nioEventLoopGroup-2461-1 表示的是进程名字
#4955
prio=10
os_prio=0
nid: 线程ID的16进制表示(可以通过`top -H`查看pid)
tid:
runnable
[0x00007fd7374bc000]`
```

线程堆栈信息
```java
java.lang.Thread.State 线程状态
locked` 锁住的资源,分别锁住了  <0x00000000e673cf38>, <0x00000000e673cd30>, <0x00000000e673cc58>
```

java.lang.Thread.State 线程状态
* `Runnable ` : 线程具备所有运行条件，在运行队列中准备操作系统的调度，或者正在运行
* `waiting for monitor entry` :  在等待进入一个临界区,所以它在`Entry Set`队列中等待.
> 此时线程状态一般都是 `Blocked`:如果大量线程在`waiting for monitor entry`, 可能是一个全局锁阻塞住了大量线程.如果短时间内打印的 `thread dump` 文件反映,随着时间流逝,`waiting for monitor entry`的线程越来越多,没有减少的趋势,可能意味着某些线程在临界区里呆的时间太长了,以至于越来越多新线程迟迟无法进入临界区.

* `waiting on condition` : 说明它在等待另一个条件的发生,来把自己唤醒,或者干脆它是调用了 `sleep(N)`.
> 如果大量线程在`waiting on condition`：可能是它们又跑去获取第三方资源,尤其是第三方网络资源,迟迟获取不到`Response`,导致大量线程进入等待状态.所以如果你发现有大量的线程都处在 `Wait on condition`,从线程堆栈看,正等待网络读写,这可能是一个网络瓶颈的征兆,因为网络阻塞导致线程无法执行.  此时线程状态大致为以下几种：
	1. `java.lang.Thread.State: WAITING (parking)`：一直等那个条件发生；
	2. `java.lang.Thread.State: TIMED_WAITING` (`parking`或`sleeping`)：定时的,那个条件不到来,也将定时唤醒自己.

* `in Object.wait()` : 说明它获得了监视器之后,又调用了 `java.lang.Object.wait()` 方法.
> 每个 Monitor在某个时刻,只能被一个线程拥有,该线程就是 `Active Thread`,而其它线程都是 `Waiting Thread`,分别在两个队列 `Entry Set`和 `Wait Set`里面等候.在 `Entry Set`中等待的线程状态是 `Waiting for monitor entry`,而在 `Wait Set`中等待的线程状态是 `in Object.wait()`.当线程获得了 `Monitor`,如果发现线程继续运行的条件没有满足,它则调用对象(一般就是被 `synchronized` 的对象)的 `wait()` 方法,放弃了 `Monitor`,进入 `Wait Set`队列. 此时线程状态大致为以下几种：
	1. `java.lang.Thread.State: TIMED_WAITING (on object monitor)`;
	2. `java.lang.Thread.State: WAITING (on object monitor)`;

## 源码
直接上[JStack]()源码
```java
import java.lang.reflect.Method;
import java.io.InputStream;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.AttachNotSupportedException;
import sun.tools.attach.HotSpotVirtualMachine;

/**
 * JStack是我们平时使用的jstack命令的实现类.
 *
 * 该类会根据命令来选择是使用SA JStack tool 还是 VM attach mechanism 输出 进程的堆栈信息
 */
public class JStack {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			return;
		}

		// 是否使用 SA JStack tool
		boolean useSA = false;
		// 如果调用本地方法的话,显示c/c++的堆栈
		boolean mixed = false;
		// 除堆栈外,显示关于锁的附加信息
		boolean locks = false;

		// 解析命令行选项(option), 必须以"-"开头
		int optionCount = 0;
		while (optionCount < args.length) {
			String arg = args[optionCount];
			if (!arg.startsWith("-")) {
				break;
			}
			if (arg.equals("-F")) {
				useSA = true;
			} else {
				if (arg.equals("-m")) {
					mixed = true;
				} else {
					if (arg.equals("-l")) {
						locks = true;
					} else {
						// 只能有-F, -m, -l这三个参数, 出现其他参数则是输出错误
						return;
					}
				}
			}
			optionCount++;
		}

		// 如果要显示本地方法的堆栈信息，则必须要使用ＳＡ
		if (mixed) {
			useSA = true;
		}

		// 除了上面的选项外，后面的参数最多也就是俩个(可能是 <pid>, <executable> <core> 或者[server_id@]<remote server IP or hostname>)
		// 如果是俩个参数即<executable> <core> 或者是 [server_id@]<remote server IP or hostname>的话, 那就要使用SA
		int paramCount = args.length - optionCount;
		if (paramCount == 0 || paramCount > 2) {
			return;
		}
		if (paramCount == 2) {
			useSA = true;
		} else {
			if (!args[optionCount].matches("[0-9]+")) {
				useSA = true;
			}
		}

		if (useSA) {
			// 将(<pid> 或者 <exe> <core>参数再重新构造一遍
			String params[] = new String[paramCount];
			for (int i=optionCount; i<args.length; i++ ){
				params[i-optionCount] = args[i];
			}
			// 使用sun.jvm.hotspot.tools.JStack 执行命令
			runJStackTool(mixed, locks, params);
		} else {
			// pass -l to thread dump operation to get extra lock info
			String pid = args[optionCount];
			String params[];
			if (locks) {
				params = new String[] { "-l" };
			} else {
				params = new String[0];
			}
			// 通关VirtualMachine或者线程的堆栈信息
			runThreadDump(pid, params);
		}
	}


	// SA JStack tool
	private static void runJStackTool(boolean mixed, boolean locks, String args[]) throws Exception {
		Class<?> cl = loadSAClass();
		if (cl == null) {
			return;
		}

		// JStack tool also takes -m and -l arguments
		if (mixed) {
			args = prepend("-m", args);
		}
		if (locks) {
			args = prepend("-l", args);
		}

		Class[] argTypes = { String[].class };
		Method m = cl.getDeclaredMethod("main", argTypes);

		Object[] invokeArgs = { args };
		m.invoke(null, invokeArgs);
	}

	private static Class loadSAClass() {
		// 我们指定system class loader是为了在开发环境中 这个类可能在boot class path中，但是sa-jdi.jar却在system class path。
		// 一旦JDK被部署之后tools.jar 和 sa-jdi.jar 都会在system class path中。
		try {
			return Class.forName("sun.jvm.hotspot.tools.JStack", true,
					ClassLoader.getSystemClassLoader());
		} catch (Exception x)  { }
		return null;
	}

	private static void runThreadDump(String pid, String args[]) throws Exception {
		VirtualMachine vm = null;
		try {
			vm = VirtualMachine.attach(pid);
		} catch (Exception x) {
			String msg = x.getMessage();
			if (msg != null) {
				System.err.println(pid + ": " + msg);
			} else {
				x.printStackTrace();
			}
			if ((x instanceof AttachNotSupportedException) &&
					(loadSAClass() != null)) {
				System.err.println("The -F option can be used when the target " +
						"process is not responding");
			}
			System.exit(1);
		}

		InputStream in = ((HotSpotVirtualMachine)vm).remoteDataDump((Object[])args);

		// read to EOF and just print output
		byte b[] = new byte[256];
		int n;
		do {
			n = in.read(b);
			if (n > 0) {
				String s = new String(b, 0, n, "UTF-8");
				System.out.print(s);
			}
		} while (n > 0);
		in.close();
		vm.detach();
	}

	// return a new string array with arg as the first element
	private static String[] prepend(String arg, String args[]) {
		String[] newargs = new String[args.length+1];
		newargs[0] = arg;
		System.arraycopy(args, 0, newargs, 1, args.length);
		return newargs;
	}
}
```
从上面的源码我们可以看到, JStack会根据选项参数来判断是使用SA JStack tool 还是 VM attach mechanism 输出线程的堆栈信息. 在下面的情况下会使用SA JStack tool
* 有-F选项
* 有-m选项
* 有<executable> <core> 参数
* 有[server_id@]<remote server IP or hostname>参数
VM attach mechanism 很简单, 我们可以很愉快地写一段测试代码, 来使用一下这个功能, 但是[SA JStack tool](http://hg.openjdk.java.net/jdk7u/jdk7u/hotspot/file/2cd3690f644c/agent/src/share/classes/sun/jvm/hotspot/tools/JStack.java)是什么玩意嘞？



参考文章
* [如何dump出一个Java进程里的类对应的Class文件？](http://rednaxelafx.iteye.com/blog/727938)
* [如何jstack -F影响正在运行的Java进程吗？ ](http://qa.helplib.com/918740)
* [The HotSpot™ Serviceability Agent: An out-of-process high level debugger for a Java™ virtual machine](http://static.usenix.org/event/jvm01/full_papers/russell/russell_html/)
* [Java命令学习系列（2）：Jstack](http://www.importnew.com/18176.html)
* [HotSpot SA #1：JStack](http://blog.csdn.net/kisimple/article/details/43274035)