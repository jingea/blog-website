category: JVM
date: 2014-10-01
title: Jmap
---
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
