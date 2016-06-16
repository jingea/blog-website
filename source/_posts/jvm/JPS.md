category: JVM
date: 2014-10-02
title: jps
---
## 用法
虚拟机进程状况工具

列出正在运行的虚拟机进程,并显示虚拟机执行主类的名称,以及这些进程的本地虚拟机的唯一ID(LVMID).

对于本地虚拟机进程来说,LVMID与操作系统的进程ID是一致的,使用windwos的任务管理器或者Unix的ps命令也可以查询到虚拟机进程的LVMID,但如果同时启动了多个虚拟机进程,无法根据进程名称定位时,那就只能依赖jps命令显示主类的功能才能区分.

命令格式:
```java
jps [ options ] [hostid]
```
jps可以通过RMI协议查询开启了RMI服务的远程虚拟机进程状态,hostid为RMI注册表中注册的主机名

jps工具主要选项
* `-q`: 只输出LVMID,省略主类的名称
* `-m`: 输出虚拟机进程启动时传递给主类main()函数的参数
* `-l`: 输出主类的全名,如果进程执行的jar包,输出jar路径
* `-v`: 输出虚拟机进程启动时JVM参数.

## 源码解析
```java
import java.util.Iterator;
import java.util.Set;
import sun.jvmstat.monitor.*;

public class Jps {

	private static Arguments arguments;

	public static void main(String[] args) {
		try {
			arguments = new Arguments(args);
		} catch (IllegalArgumentException e) {
			return;
		}

		try {
			HostIdentifier hostId = arguments.hostId();
			MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(hostId);

			// get the set active JVMs on the specified host.
			Set jvms = monitoredHost.activeVms();
			for (Iterator j = jvms.iterator(); j.hasNext(); /* empty */ ) {
				int lvmid = ((Integer) j.next()).intValue();
				if (arguments.isQuiet()) {
					continue;
				}

				try {
					String vmidString = "//" + lvmid + "?mode=r";
					// process information unavailable";
					VmIdentifier id = new VmIdentifier(vmidString);
					MonitoredVm vm = monitoredHost.getMonitoredVm(id, 0);

					// main class information unavailable";
					MonitoredVmUtil.mainClass(vm, arguments.showLongPaths());

					if (arguments.showMainArgs()) {
						String mainArgs = MonitoredVmUtil.mainArgs(vm);
					}
					if (arguments.showVmArgs()) {
						String jvmArgs = MonitoredVmUtil.jvmArgs(vm);
					}
					if (arguments.showVmFlags()) {
						String jvmFlags = MonitoredVmUtil.jvmFlags(vm);
					}
					monitoredHost.detach(vm);
				} catch (Exception e) {
				} finally {
				}
			}
		} catch (MonitorException e) {
			e.printStackTrace();
		}
	}
}

import java.net.URISyntaxException;

import sun.jvmstat.monitor.HostIdentifier;

/**
 * Class for processing command line arguments and providing method level access
 * to the command line arguments.
 */
public class Arguments {

	private boolean help;
	private boolean quiet;
	private boolean longPaths;
	private boolean vmArgs;
	private boolean vmFlags;
	private boolean mainArgs;
	private String hostname;
	private HostIdentifier hostId;

	public Arguments(String[] args) throws IllegalArgumentException {
		int argc = 0;

		if (args.length == 1) {
			if ((args[0].compareTo("-?") == 0) || (args[0].compareTo("-help") == 0)) {
				help = true;
				return;
			}
		}

		for (argc = 0; (argc < args.length) && (args[argc].startsWith("-")); argc++) {
			String arg = args[argc];

			if (arg.compareTo("-q") == 0) {
				quiet = true;
			} else if (arg.startsWith("-")) {
				for (int j = 1; j < arg.length(); j++) {
					switch (arg.charAt(j)) {
					case 'm':
						mainArgs = true;
						break;
					case 'l':
						longPaths = true;
						break;
					case 'v':
						vmArgs = true;
						break;
					case 'V':
						vmFlags = true;
						break;
					default:
						throw new IllegalArgumentException("illegal argument: " + args[argc]);
					}
				}
			} else {
				throw new IllegalArgumentException("illegal argument: " + args[argc]);
			}
		}

		switch (args.length - argc) {
		case 0:
			hostname = null;
			break;
		case 1:
			hostname = args[args.length - 1];
			break;
		default:
			throw new IllegalArgumentException("invalid argument count");
		}

		try {
			hostId = new HostIdentifier(hostname);
		} catch (URISyntaxException e) {
			IllegalArgumentException iae = new IllegalArgumentException("Malformed Host Identifier: " + hostname);
			iae.initCause(e);
			throw iae;
		}
	}

	public boolean isQuiet() {
		return quiet;
	}

	public boolean showLongPaths() {
		return longPaths;
	}

	public boolean showVmArgs() {
		return vmArgs;
	}

	public boolean showVmFlags() {
		return vmFlags;
	}

	public boolean showMainArgs() {
		return mainArgs;
	}

	public HostIdentifier hostId() {
		return hostId;
	}
}
```
从上面的源码中可以看到, jps的主要使用的是`sun.jvmstat.monitor`里的工具