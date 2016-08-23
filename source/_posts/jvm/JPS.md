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
```
从上面的源码中可以看到, jps的主要使用的是`sun.jvmstat.monitor`里的工具实现的. 参考[]()和[]()