category: JVM
date: 2016-08-22
title: hsperfdata
---
Hotspot出于性能测试和排查问题的目的而提供了jvmstat 工具. 我们可以使用JVM参数`-XX:+UsePerfData`来决定是否开启这个功能. 因此jvm运行时会生成一个目录hsperfdata_$USER($USER是启动java进程的用户,在linux中默认是/tmp),目录下会有些java 进程的 pid文件. 文件内部存储的是jvmstat统计的jvm进程的相关信息.

> jvmstat源码参考[openjdk\jdk\src\share\classes\sun\jvmstat](http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/com/sun/jvmstat)


当我们将/tmp/hsperfdata目录下的某个进程文件删除之后, 相应的进程也就消失了, 经过测试即使Java进程里已经注册了钩子程序, 但是钩子程序也不会执行, 进程就直接消失了. 而且进程消失之后, 这个文件也就没有了. 

PerfData 相关的JVM选项
* `-XX:+UsePerfData`	用于设置是否开启统计.(默认值为true, 开启)
* `-XX:+PerfDataSaveToFile`	在系统退出时是否将PerfData 内存数据保存到`hsperfdata_` 文件(默认为false, 不开启). 注意这个数据是保存到Java进程当前的工作目录, 而不是`/tmp`下
* `PerfDataSamplingInterval`	统计采样间隔(单位毫秒, 默认是50毫秒)
* `PerfDisableSharedMem`	是否将标准内存的性能数据进行存储(默认为false, 不存储)
* `PerfDataMemorySize`	性能统计数据可存储大小. 总是对操作系统内存页的大小进行取整(默认是32k)。 因为在内存中保存的是32k的数据, 因此存储到文件之后, 文件的大小也固定在32k.



我们可以使用jdk提供的一些工具来读取这个目录下的JVM进程信息统计文件, 例如

jstat读取`/tmp/hsperfdata_username/`目录下的文件查看虚拟机老年代的情况
```bash
[root@root wangming]# jstat -gcold file:///tmp/hsperfdata_username/2332 
   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT   
 26112.0  25379.2   3840.0   3719.1    164864.0     18007.6    495     1    0.102    2.377
```