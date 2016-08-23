category: JVM
date: 2014-10-01
title: Jmap
---
## 用法
java内存映射工具,用于生成堆转储快照.

如果不使用jmap命令,想要获取java堆转储快照还有一些比较暴力的手段:
* `-XX:+HeapDumpOnOutOfMemoryError`: 可以让虚拟机在OOM异常自动生成dump文件,通过
* `-XX:+HeapDumpOnCtrlBreak`参数则可以使用`[CTRL] + [Break]`: 键让虚拟机生成dump文件,又或者在Linux系统
下通过`kill -3`命令发送进程退出信号,也能拿到dump文件.

jmap的作用并不仅仅是为了获取dump文件,它还可以查询`finalize`执行队列,java堆和永久代的详细信息,如空间使用率,当前使用的是哪种收集器.

和jinfo命令一样,jmap有不少功能是在windows平台下受限的,除了生成dump文件`-dump`选项和用于查看每个类的实例,空间占用统计的`-histo`选项所有系统操作系统都提供之外,其余选项只能在Linux/Solaris下使用.

```bash
jmap [ option ] vmid
```

jmap工具主要选项
* `-dump`: 生成java堆转储快照.格式为:`-dump:[live,]format=b,file=<filename>`.live表示只dump存活对象
* `-finalizerinfo`: 显示在`F-Queue`中等待`Finalizer`线程执行`finalize`方法的对象.
* `-heap`: 显示java堆的详细信息,使用哪种回收器,参数配置,分代状况.
* `-histo`: 显示堆中对象统计信息,包括类,实例数量和合计容量
* `-permstat`: 以`ClassLoader`为统计口径显示永久代内存状态.
* `-F`: 当虚拟机进程对`-dump`选项没有响应时,可使用这个选项强制生成dump快照

获取当前进程的堆快照
```bash
➜ test jmap -dump:live,format=b,file=2028dump 2028
Dumping heap to /Users/wangming/Desktop/test/2028dump ...
Heap dump file created
```

获取当前进程的对象统计信息, 下面统计出了数量大于10000个对象的类
```bash
➜ test jmap -histo 2028 | awk '{if($2> 10000) print $1 "  " $2 "  "  $3 "  " $4 }'
1:  36397  6363208  [C
3:  35324  847776  java.lang.String
7:  10522  336704  java.util.concurrent.ConcurrentHashMap$Node
Total  207899  14900384
```

## 源码分析
我们先看一下jmap的help帮助手册
```bash
D:\>jmap -help
Usage:
    jmap [option] <pid>
        (to connect to running process)
    jmap [option] <executable <core>
        (to connect to a core file)
    jmap [option] [server_id@]<remote server IP or hostname>
        (to connect to remote debug server)

where <option> is one of:
    <none>               to print same info as Solaris pmap
    -heap                打印Java堆概览
    -histo[:live]        打印Java堆得对象柱状分布图; 如果指定"live" 的话, 那么就只统计存活的对象
    -clstats             打印class 加载的统计信息
    -finalizerinfo       to print information on objects awaiting finalization
    -dump:<dump-options> 以二进制格式dump JVM 堆内存信息 (示例: jmap -dump:live,format=b,file=heap.bin <pid>)
                         dump-options:
                           live         只dump 存活的对象; 如果不指定的话, 堆内存内的所有对象都会被dump出来.
                           format=b     二进制格式
                           file=<file>  dump 堆内存到哪个文件
                         
    -F                   force. Use with -dump:<dump-options> <pid> or -histo to force a heap dump or histogram when <pid> does not respond. The "live" suboption is not supported in this mode.
    -h | -help           to print this help message
    -J<flag>             to pass <flag> directly to the runtime system

```
jmap 命令支持三种模式, 分别是本地进程, core文件和远程进程模式.

JMap类是jmap命令的封装实现类. 这个类同样是由参数决定来调用[VM attach mechanism]()还是[SA tool]().

> 现在只有`-heap`,`-finalizerinfo`和`-F`选项下会使用[VM attach mechanism](),其他的情况都是使用[SA tools]().
```java
import java.lang.reflect.Method;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.AttachNotSupportedException;
import sun.tools.attach.HotSpotVirtualMachine;

public class JMap {

	// 使用 attach mechanism 的选项
	private static String HISTO_OPTION = "-histo";
	private static String LIVE_HISTO_OPTION = "-histo:live";
	private static String DUMP_OPTION_PREFIX = "-dump:";

	// 使用 SA tool 的选项
	private static String SA_TOOL_OPTIONS = "-heap|-heap:format=b|-permstat|-finalizerinfo";

	// The -F (force) option is currently not passed through to SA
	private static String FORCE_SA_OPTION = "-F";

	// Default option (if nothing provided)
	private static String DEFAULT_OPTION = "-pmap";

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			usage(); // no arguments
		}
		boolean useSA = false;
		// 例如选中的选项 (-heap, -dump:*, ... )
		String option = null;
		// 开始遍历所有的选项(选项以-开始). 如果选项中不包含-F, 则应该只有一个选项
		int optionCount = 0;
		while (optionCount < args.length) {
			String arg = args[optionCount];
			if (!arg.startsWith("-")) {
				break;
			}
			// 判断是否是-F选项, 如果是则使用SA
			if (arg.equals(FORCE_SA_OPTION)) {
				useSA = true;
			} else {
				if (option != null) {
					usage();  // option already specified
				}
				option = arg;
			}
			optionCount++;
		}

		// if no option provided then use default.
		if (option == null) {
			option = DEFAULT_OPTION;
		}
		if (option.matches(SA_TOOL_OPTIONS)) {
			useSA = true;
		}

		// 下面开始检查参数个数. SA Tool只会使用1或者2个参数. 在使用-dump参数时只会使用一个参数
		int paramCount = args.length - optionCount;
		if (paramCount == 0 || paramCount > 2) {
			usage();
		}

		if (optionCount == 0 || paramCount != 1) {
			useSA = true;
		} else {
			// 如果只有一个参数且没办法解析成pid的话, 那么肯定是要连接一个debug server, 也要使用SA Tool
			if (!args[optionCount].matches("[0-9]+")) {
				useSA = true;
			}
		}

		if (useSA) {
			// parameters (<pid> or <exe> <core>)
			String params[] = new String[paramCount];
			for (int i=optionCount; i<args.length; i++ ){
				params[i-optionCount] = args[i];
			}
			runTool(option, params);
		} else {
			// 开始调用VirtualMachine相关的接口方法
			String pid = args[1];
			if (option.equals(HISTO_OPTION)) {
				histo(pid, false);
			} else if (option.equals(LIVE_HISTO_OPTION)) {
				histo(pid, true);
			} else if (option.startsWith(DUMP_OPTION_PREFIX)) {
				dump(pid, option);
			} else {
				usage();
			}
		}
	}

	// 调用 SA tool
	private static void runTool(String option, String args[]) throws Exception {
		String[][] tools = {
				{ "-pmap",           "sun.jvm.hotspot.tools.PMap"     },
				{ "-heap",           "sun.jvm.hotspot.tools.HeapSummary"     },
				{ "-heap:format=b",  "sun.jvm.hotspot.tools.HeapDumper"      },
				{ "-histo",          "sun.jvm.hotspot.tools.ObjectHistogram" },
				{ "-permstat",       "sun.jvm.hotspot.tools.PermStat"        },
				{ "-finalizerinfo",  "sun.jvm.hotspot.tools.FinalizerInfo"   },
		};

		String tool = null;

		// -dump option needs to be handled in a special way
		if (option.startsWith(DUMP_OPTION_PREFIX)) {
			// first check that the option can be parsed
			String fn = parseDumpOptions(option);
			if (fn == null) usage();
			// tool for heap dumping
			tool = "sun.jvm.hotspot.tools.HeapDumper";

			// HeapDumper -f <file>
			args = prepend(fn, args);
			args = prepend("-f", args);
		} else {
			int i=0;
			while (i < tools.length) {
				if (option.equals(tools[i][0])) {
					tool = tools[i][1];
					break;
				}
				i++;
			}
		}
		if (tool == null) {
			usage();   // no mapping to tool
		}

		// 加载相关的工具类, PMap, HeapSummary, HeapDumper, ObjectHistogram, PermStat, FinalizerInfo
		Class<?> c = loadClass(tool);
		if (c == null) {
			usage();
		}
		Class[] argTypes = { String[].class } ;
		Method m = c.getDeclaredMethod("main", argTypes);
		Object[] invokeArgs = { args };
		m.invoke(null, invokeArgs);
	}

	private static Class loadClass(String name) {
		try {
			return Class.forName(name, true, ClassLoader.getSystemClassLoader());
		} catch (Exception x)  { }
		return null;
	}

	private static final String LIVE_OBJECTS_OPTION = "-live";
	private static final String ALL_OBJECTS_OPTION = "-all";

	private static void histo(String pid, boolean live) throws IOException {
		VirtualMachine vm = attach(pid);
		InputStream in = ((HotSpotVirtualMachine)vm).
				heapHisto(live ? LIVE_OBJECTS_OPTION : ALL_OBJECTS_OPTION);
		drain(vm, in);
	}

	private static void dump(String pid, String options) throws IOException {
		// 从options中将dump filename解析出来
		String filename = parseDumpOptions(options);
		if (filename == null) {
			usage();  // invalid options or no filename
		}

		// get the canonical path - important to avoid just passing
		// a "heap.bin" and having the dump created in the target VM
		// working directory rather than the directory where jmap
		// is executed.
		filename = new File(filename).getCanonicalPath();

		// 是否只dump 存活的对象
		boolean live = isDumpLiveObjects(options);

		VirtualMachine vm = attach(pid);
		System.out.println("Dumping heap to " + filename + " ...");
		InputStream in = ((HotSpotVirtualMachine)vm).
				dumpHeap((Object)filename,
						(live ? LIVE_OBJECTS_OPTION : ALL_OBJECTS_OPTION));
		drain(vm, in);
	}

	// 解析-dump选项. 合法的选项为format=b和file=<file>.
	// 如果文件存在的话, 就返回文件名。 如果文件不存在或者是非法选项的话则返回null
	private static String parseDumpOptions(String arg) {
		assert arg.startsWith(DUMP_OPTION_PREFIX);

		String filename = null;
		// 将-dump:后面的参数以, 分割
		String options[] = arg.substring(DUMP_OPTION_PREFIX.length()).split(",");

		for (int i=0; i<options.length; i++) {
			String option = options[i];

			if (option.equals("format=b")) {
				// ignore format (not needed at this time)
			} else if (option.equals("live")) {
				// a valid suboption
			} else {
				// file=<file> - check that <file> is specified
				if (option.startsWith("file=")) {
					filename = option.substring(5);
					if (filename.length() == 0) {
						return null;
					}
				} else {
					return null;  // option not recognized
				}
			}
		}
		return filename;
	}

	private static boolean isDumpLiveObjects(String arg) {
		String options[] = arg.substring(DUMP_OPTION_PREFIX.length()).split(",");
		for (String suboption : options) {
			if (suboption.equals("live")) {
				return true;
			}
		}
		return false;
	}

	// Attach to <pid>, existing if we fail to attach
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
			if ((x instanceof AttachNotSupportedException) && haveSA()) {
				System.err.println("The -F option can be used when the " +
						"target process is not responding");
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

	// 将arg加入到args中
	private static String[] prepend(String arg, String args[]) {
		String[] newargs = new String[args.length+1];
		newargs[0] = arg;
		System.arraycopy(args, 0, newargs, 1, args.length);
		return newargs;
	}

	private static boolean haveSA() {
		Class c = loadClass("sun.jvm.hotspot.tools.HeapSummary");
		return (c != null);
	}

	// print usage message
	private static void usage() {
		System.out.println("Usage:");
		if (haveSA()) {
			System.out.println("    jmap [option] <pid>");
			System.out.println("        (to connect to running process)");
			System.out.println("    jmap [option] <executable <core>");
			System.out.println("        (to connect to a core file)");
			System.out.println("    jmap [option] [server_id@]<remote server IP or hostname>");
			System.out.println("        (to connect to remote debug server)");
			System.out.println("");
			System.out.println("where <option> is one of:");
			System.out.println("    <none>               to print same info as Solaris pmap");
			System.out.println("    -heap                to print java heap summary");
			System.out.println("    -histo[:live]        to print histogram of java object heap; if the \"live\"");
			System.out.println("                         suboption is specified, only count live objects");
			System.out.println("    -permstat            to print permanent generation statistics");
			System.out.println("    -finalizerinfo       to print information on objects awaiting finalization");
			System.out.println("    -dump:<dump-options> to dump java heap in hprof binary format");
			System.out.println("                         dump-options:");
			System.out.println("                           live         dump only live objects; if not specified,");
			System.out.println("                                        all objects in the heap are dumped.");
			System.out.println("                           format=b     binary format");
			System.out.println("                           file=<file>  dump heap to <file>");
			System.out.println("                         Example: jmap -dump:live,format=b,file=heap.bin <pid>");
			System.out.println("    -F                   force. Use with -dump:<dump-options> <pid> or -histo");
			System.out.println("                         to force a heap dump or histogram when <pid> does not");
			System.out.println("                         respond. The \"live\" suboption is not supported");
			System.out.println("                         in this mode.");
			System.out.println("    -h | -help           to print this help message");
			System.out.println("    -J<flag>             to pass <flag> directly to the runtime system");
		} else {
			System.out.println("    jmap -histo <pid>");
			System.out.println("      (to connect to running process and print histogram of java object heap");
			System.out.println("    jmap -dump:<dump-options> <pid>");
			System.out.println("      (to connect to running process and dump java heap)");
			System.out.println("");
			System.out.println("    dump-options:");
			System.out.println("      format=b     binary default");
			System.out.println("      file=<file>  dump heap to <file>");
			System.out.println("");
			System.out.println("    Example:       jmap -dump:format=b,file=heap.bin <pid>");
		}

		System.exit(1);
	}
}
```
