category: JVM
date: 2016-28-29
title: HotSpot Dynamic Attach Mechanism 简析
---

在 `在VirtualMachine` javadoc中给了我们一个示范用例
```java
// attach to target VM
VirtualMachine vm = VirtualMachine.attach("2177");

// get system properties in target VM
Properties props = vm.getSystemProperties();

// construct path to management agent
String home = props.getProperty("java.home");
String agent = home + File.separator + "lib" + File.separator
    + "management-agent.jar";

// load agent into target VM
vm.loadAgent(agent, "com.sun.management.jmxremote.port=5000");

// detach
vm.detach();
```
如果要引用 `在VirtualMachine`的话, 我们需要在classpath上添加tools.jar文件。注意在Windows和Linux上的tools.jar文件实现是不同的, 需要在不同的平台上依赖不同的文件.


Attach API 相关实现可以在openjdk中找到
* `VirtualMachine` : `openjdk\jdk\src\share\classes\com\sun\tools\attach`
* `AttachProvider` : `openjdk\jdk\src\share\classes\com\sun\tools\attach\spi`
* `HotSpotAttachProvider` : `openjdk\jdk\src\share\classes\sun\tools\attach`
* `HotSpotVirtualMachine` : `openjdk\jdk\src\share\classes\sun\tools\attach`
* `LinuxVirtualMachine` : `openjdk\jdk\src\solaris\classes\sun\tools\attach`
* `LinuxAttachProvider` : `openjdk\jdk\src\solaris\classes\sun\tools\attach`
* `jvmstat` : `openjdk\jdk\src\share\classes\sun\jvmstat`

Attach API 主要就是依赖上面的几个文件实现的

从[HotSpot Dynamic Attach Mechanism](http://openjdk.java.net/groups/hotspot/docs/Serviceability.html#battach)这篇介绍中我们可以看到Attach API的实现概要.

HotSpot Dynamic Attach Mechanism这个工具是用于Attach到另一个运行Java代码的进程, 目标Java进程会开启一个` JVM TI `代理或者一个`java.lang.instrument`代理.

而Sun公司(Hotspot VM)对其还有一些额外的功能实现(`HotSpotVirtualMachine`中实现)
* dump堆内存
* 显示加载进目标虚拟机的class的实例数量. 可以选择是全部的实例数量还是仅仅显示存活下来的实例数量.

当JVM第一次收到attach请求的时候, 它会创建一个`attach listener`线程. 

在不同的系统上, 请求的发送方式也不一样(参考`LinuxVirtualMachine`). 例如在Linux或者Solaris系统上, attach 客户端会创建一个`.attach_pid(pid)`文件, 然后向目标虚拟机发送一个`SIGQUIT`信号. 目标虚拟机会根据`.attach_pid(pid)`文件与否运行`attach listener`线程. 

在Linux系统上客户端是通过socket与`attach listener`线程进行交互的.

当attach成功之后, 会在`/tmp`目录下生成一个`.java_pid<pid>`的文件生成. 这个文件的生成目录是不可修改的, 而且一旦文件生成之后, 不可以删掉再尝试重新生成.

我们看一下`LinuxVirtualMachine` 的attach 过程
```java
  LinuxVirtualMachine(AttachProvider provider, String vmid)
        throws AttachNotSupportedException, IOException
    {
        super(provider, vmid);

        // This provider only understands pids
        int pid;
        try {
            pid = Integer.parseInt(vmid);
        } catch (NumberFormatException x) {
            throw new AttachNotSupportedException("Invalid process identifier");
        }

        // 尝试搜索 socket file. 如果找不到的话就向目标虚拟机发送QUIT信号, 让目标虚拟机开启 attach mechanism.
        // 然后继续尝试搜索  socket file.
        // target VM会创建这个文件, 这个是因为Unix domain socket本身的实现机制需要去创建一个文件, 通过这个文件来进行IPC
        // Find the  socket file. If not found then we attempt to start the
        // attach mechanism in the target VM by sending it a QUIT signal.
        // Then we attempt to find the socket file again.
        path = findSocketFile(pid);
        if (path == null) {
            // 创建 .attach_pid<pid> 文件, 触发Attach Listener线程的创建,因为SIGQUIT信号不是只有这里发送，
            // 通过这个文件来告诉target VM，有attach请求过来了。
            File f = createAttachFile(pid);
            try {
                // On LinuxThreads each thread is a process and we don't have the
                // pid of the VMThread which has SIGQUIT unblocked. To workaround
                // this we get the pid of the "manager thread" that is created
                // by the first call to pthread_create. This is parent of all
                // threads (except the initial thread).
                if (isLinuxThreads) {
                    int mpid;
                    try {
                        mpid = getLinuxThreadsManager(pid);
                    } catch (IOException x) {
                        throw new AttachNotSupportedException(x.getMessage());
                    }
                    assert(mpid >= 1);
                    sendQuitToChildrenOf(mpid);
                } else {
                    sendQuitTo(pid);
                }

                // 默认等待5秒钟, 等待目标虚拟机 attach mechanism 启动完成
                // 启动完成之后会创建一个 .java_pid<pid> 文件, 该文件会赋值给 path 变量
                // give the target VM time to start the attach mechanism
                int i = 0;
                long delay = 200;
                int retries = (int)(attachTimeout() / delay);
                do {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException x) { }
                    path = findSocketFile(pid);
                    i++;
                } while (i <= retries && path == null);
                if (path == null) {
                    throw new AttachNotSupportedException(
                        "Unable to open socket file: target process not responding " +
                        "or HotSpot VM not loaded");
                }
            } finally {
                f.delete();
            }
        }

        // Check that the file owner/permission to avoid attaching to
        // bogus process
        checkPermissions(path);

        // Check that we can connect to the process
        // - this ensures we throw the permission denied error now rather than
        // later when we attempt to enqueue a command.
        int s = socket();
        try {
            connect(s, path);
        } finally {
            close(s);
        }
    }
	
	  // Return the socket file for the given process.
    private String findSocketFile(int pid) {
        File f = new File(tmpdir, ".java_pid" + pid);
        if (!f.exists()) {
            return null;
        }
        return f.getPath();
    }

    // 在 Solaris/Linux 系统中开启 attach mechanism 的一个非常简单的方式是使用握手的方式. 通过在目标虚拟机的工作目录
    // (或者tmp目录下) 创建一个.attach_pid<pid> 文件 , 然后目标虚拟机的 SIGQUIT 信号处理器通过不断地查询这个文件是否存在
    // 来决定是否开启 attach mechanism .
    //
    // On Solaris/Linux a simple handshake is used to start the attach mechanism
    // if not already started. The client creates a .attach_pid<pid> file in the
    // target VM's working directory (or temp directory), and the SIGQUIT handler
    // checks for the file.
    private File createAttachFile(int pid) throws IOException {
        String fn = ".attach_pid" + pid;
        String path = "/proc/" + pid + "/cwd/" + fn;
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException x) {
            f = new File(tmpdir, fn);
            f.createNewFile();
        }
        return f;
    }
```

从以上分析我们可以看出, Attach API 是客户端通过本地socket与目标jvm进行通信的, 具体的功能实现都是在目标JVM里实现的.