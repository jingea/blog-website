category: JVM
date: 2016-28-29
title: HotSpot Dynamic Attach Mechanism 简析
---
## 原理
从[HotSpot Dynamic Attach Mechanism](http://openjdk.java.net/groups/hotspot/docs/Serviceability.html#battach)这篇介绍中我们可以看到Attach API的实现概要.

HotSpot Dynamic Attach Mechanism这个工具是用于Attach到另一个运行Java代码的进程, 目标Java进程会开启一个` JVM TI `代理或者一个`java.lang.instrument`代理.

而Sun公司(Hotspot VM)对其还有一些额外的功能实现
* dump堆内存
* 显示加载进目标虚拟机的class的实例数量. 可以选择是全部的实例数量还是仅仅显示存活下来的实例数量.

当JVM第一次收到attach请求的时候, 它会创建一个`attach listener`线程. 

在不同的系统上, 请求的发送方式也不一样. 例如在Linux或者Solaris系统上, attach 客户端会创建一个`.attach_pid(pid)`文件, 然后向目标虚拟机发送一个`SIGQUIT`信号. 目标虚拟机会根据`.attach_pid(pid)`文件与否运行`attach listener`线程. 

在Linux系统上客户端是通过socket与`attach listener`线程进行交互的.

当attach成功之后, 会在`/tmp`目录下生成一个`.java_pid<pid>`的文件生成. 这个文件的生成目录是不可修改的, 而且一旦文件生成之后, 不可以删掉再尝试重新生成.