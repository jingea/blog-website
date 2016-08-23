category: JVM
date: 2014-10-04
title: Jinfo
---
## 用法
jinfo的作用是实时查看和调整虚拟机的各项参数.
```java
jinfo [ option ] pid
```
option可以为如下值
* `-flag <name>`         打印指定name的vm flag的值
* `-flag [+|-]<name>`    将指定名称的 VM flag打开或者关闭
* `-flag <name>=<value>` 将指定名称的 VM flag重新设置值
* `-flags`               打印所有的 VM flags
* `-sysprops`            打印 Java system properties
* `<no option>`          如果没有选项的话就是执行上面的所有选项
```bash
test jinfo 2028
Attaching to process ID 2028, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.5-b02
Java System Properties:

java.runtime.name = Java(TM) SE Runtime Environment
java.vm.version = 25.5-b02
sun.boot.library.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/lib
gopherProxySet = false
java.vendor.url = http://java.oracle.com/
java.vm.vendor = Oracle Corporation
path.separator = :
file.encoding.pkg = sun.io
java.vm.name = Java HotSpot(TM) 64-Bit Server VM
idea.launcher.port = 7532
sun.os.patch.level = unknown
sun.java.launcher = SUN_STANDARD
user.country = CN
java.vm.specification.name = Java Virtual Machine Specification
PID = 2028
java.runtime.version = 1.8.0_05-b13
java.awt.graphicsenv = sun.awt.CGraphicsEnvironment
os.arch = x86_64
java.endorsed.dirs = /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/lib/endorsed
org.jboss.logging.provider = slf4j
line.separator =

java.io.tmpdir = /var/folders/54/dy3_8pkj1ld2_q6r8rvvr1n80000gn/T/
java.vm.specification.vendor = Oracle Corporation
os.name = Mac OS X
sun.jnu.encoding = UTF-8
spring.beaninfo.ignore = true
java.specification.name = Java Platform API Specification
java.class.version = 52.0
sun.management.compiler = HotSpot 64-Bit Tiered Compilers
os.version = 10.10.5
user.timezone = Asia/Harbin
catalina.useNaming = false
java.awt.printerjob = sun.lwawt.macosx.CPrinterJob
file.encoding = UTF-8
java.specification.version = 1.8
catalina.home = /private/var/folders/54/dy3_8pkj1ld2_q6r8rvvr1n80000gn/T/tomcat.3734431332202362357.8080
java.vm.specification.version = 1.8
sun.arch.data.model = 64
sun.java.command = com.intellij.rt.execution.application.AppMain HTTPServer
java.home = /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre
user.language = zh
java.specification.vendor = Oracle Corporation
awt.toolkit = sun.lwawt.macosx.LWCToolkit
java.vm.info = mixed mode
java.version = 1.8.0_05
java.awt.headless = true
java.vendor = Oracle Corporation
catalina.base = /private/var/folders/54/dy3_8pkj1ld2_q6r8rvvr1n80000gn/T/tomcat.3734431332202362357.8080
file.separator = /
java.vendor.url.bug = http://bugreport.sun.com/bugreport/
sun.io.unicode.encoding = UnicodeBig
sun.cpu.endian = little
sun.cpu.isalist =

VM Flags:
Non-default VM flags: -XX:InitialHeapSize=100663296 -XX:MaxHeapSize=1610612736 -XX:MaxNewSize=536870912 -XX:MinHeapDeltaBytes=524288 -XX:NewSize=1572864 -XX:OldSize=99090432 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
```

## 源码分析
我们首先看一下jinfo的help帮助文档
```bash
D:\>jinfo -help
Usage:
    jinfo [option] <pid>
        (to connect to running process)
    jinfo [option] <executable <core>
        (to connect to a core file)
    jinfo [option] [server_id@]<remote server IP or hostname>
        (to connect to remote debug server)

where <option> is one of:
    -flag <name>         打印指定name的vm flag的值
    -flag [+|-]<name>    将指定名称的 VM flag打开或者关闭
    -flag <name>=<value> 将指定名称的 VM flag重新设置值
    -flags               打印所有的 VM flags
    -sysprops            打印 Java system properties
    <no option>          如果没有选项的话就是执行上面的所有选项
    -h | -help           to print this help message
```
我们看到jinfo其实支持的是三种模式, 分别是本地进程, core文件和远程进程模式.

```java
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import sun.tools.attach.HotSpotVirtualMachine;
import com.sun.tools.attach.VirtualMachine;

public class JInfo {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			usage(); // no arguments
		}

		boolean useSA = true;
		String arg1 = args[0];
		if (arg1.startsWith("-")) {
			if (arg1.equals("-flags") || arg1.equals("-sysprops")) {
				// SA JInfo 需要 <pid> 或者 <server> 或者(<executable> and <code file>).
				// 因此 包含选项在内的所有参数应该是 2个 或者 3个.
				if (args.length != 2 && args.length != 3) {
					usage();
				}
			} else if (arg1.equals("-flag")) {
				useSA = false;
			} else {
				usage();
			}
		}

		if (useSA) {
			runTool(args);
		} else {
			if (args.length == 3) {
				String pid = args[2];
				String option = args[1];
				flag(pid, option);
			} else {
				usage();
			}
		}
	}

	// 调用sa tool内部实现的jinfo
	private static void runTool(String args[]) throws Exception {
		String tool = "sun.jvm.hotspot.tools.JInfo";
		Class<?> c = loadClass(tool);
		Class[] argTypes = { String[].class };
		Method m = c.getDeclaredMethod("main", argTypes);

		Object[] invokeArgs = { args };
		m.invoke(null, invokeArgs);
	}

	private static Class loadClass(String name) {
		// 我们指定system class loader是为了在开发环境中 这个类可能在boot class path中，但是sa-jdi.jar却在system class path。
		// 一旦JDK被部署之后tools.jar 和 sa-jdi.jar 都会在system class path中。
		try {
			return Class.forName(name, true, ClassLoader.getSystemClassLoader());
		} catch (Exception x) {
		}
		return null;
	}

	// 调用Attach API 实现的JInfo
	private static void flag(String pid, String option) throws IOException {
		VirtualMachine vm = attach(pid);
		String flag;
		InputStream in;
		int index = option.indexOf('=');
		if (index != -1) {
			flag = option.substring(0, index);
			String value = option.substring(index + 1);
			in = ((HotSpotVirtualMachine) vm).setFlag(flag, value);
		} else {
			char c = option.charAt(0);
			switch (c) {
			case '+':
				flag = option.substring(1);
				in = ((HotSpotVirtualMachine) vm).setFlag(flag, "1");
				break;
			case '-':
				flag = option.substring(1);
				in = ((HotSpotVirtualMachine) vm).setFlag(flag, "0");
				break;
			default:
				flag = option;
				in = ((HotSpotVirtualMachine) vm).printFlag(flag);
				break;
			}
		}

		drain(vm, in);
	}

	// Attach to <pid>, exiting if we fail to attach
	private static VirtualMachine attach(String pid) {
		try {
			return VirtualMachine.attach(pid);
		} catch (Exception x) {
			String msg = x.getMessage();
			if (msg != null) {
				System.err.println(pid + ": " + msg);
			} else {
				x.printStackTrace();
			}
			System.exit(1);
			return null; // keep compiler happy
		}
	}

	// Read the stream from the target VM until EOF, then detach
	private static void drain(VirtualMachine vm, InputStream in) throws IOException {
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

	// print usage message
	private static void usage() {

		Class c = loadClass("sun.jvm.hotspot.tools.JInfo");
		boolean usageSA = (c != null);

		System.out.println("Usage:");
		if (usageSA) {
			System.out.println("    jinfo [option] <pid>");
			System.out.println("        (to connect to running process)");
			System.out.println("    jinfo [option] <executable <core>");
			System.out.println("        (to connect to a core file)");
			System.out.println("    jinfo [option] [server_id@]<remote server IP or hostname>");
			System.out.println("        (to connect to remote debug server)");
			System.out.println("");
			System.out.println("where <option> is one of:");
			System.out.println("    -flag <name>         to print the value of the named VM flag");
			System.out.println("    -flag [+|-]<name>    to enable or disable the named VM flag");
			System.out.println("    -flag <name>=<value> to set the named VM flag to the given value");
			System.out.println("    -flags               to print VM flags");
			System.out.println("    -sysprops            to print Java system properties");
			System.out.println("    <no option>          to print both of the above");
			System.out.println("    -h | -help           to print this help message");
		} else {
			System.out.println("    jinfo <option> <pid>");
			System.out.println("       (to connect to a running process)");
			System.out.println("");
			System.out.println("where <option> is one of:");
			System.out.println("    -flag <name>         to print the value of the named VM flag");
			System.out.println("    -flag [+|-]<name>    to enable or disable the named VM flag");
			System.out.println("    -flag <name>=<value> to set the named VM flag to the given value");
			System.out.println("    -h | -help           to print this help message");
		}

		System.exit(1);
	}
}
```
JInfo 只有在使用`-flag`选项的时候才会使用[VM Attach API](),其他的选项都是使用[SA tools]()实现的
