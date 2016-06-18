category: JVM
date: 2014-10-06
title: hprof
---
这个是java agent工具, 用它可以监控应用程序在运行时的CPU信息和堆信息. 我们来看一下它的help文档
```bash
➜  test java -agentlib:hprof=help

     HPROF: Heap and CPU Profiling Agent (JVMTI Demonstration Code)

hprof usage: java -agentlib:hprof=[help]|[<option>=<value>, ...]

Option Name and Value  Description                    Default
---------------------  -----------                    -------
heap=dump|sites|all    heap profiling                 all
cpu=samples|times|old  CPU usage                      off
monitor=y|n            monitor contention             n
format=a|b             text(txt) or binary output     a
file=<file>            write data to file             java.hprof[{.txt}]
net=<host>:<port>      send data over a socket        off
depth=<size>           stack trace depth              4
interval=<ms>          sample interval in ms          10
cutoff=<value>         output cutoff point            0.0001
lineno=y|n             line number in traces?         y
thread=y|n             thread in traces?              n
doe=y|n                dump on exit?                  y
msa=y|n                Solaris micro state accounting n
force=y|n              force output to <file>         y
verbose=y|n            print messages about dumps     y

Obsolete Options
----------------
gc_okay=y|n
```
示例:

每20毫秒统计一次CPU信息(栈深度为3):
```bash
java -agentlib:hprof=cpu=samples,interval=20,depth=3 classname
```
Get heap usage information based on the allocation sites:
```bash
java -agentlib:hprof=heap=sites classname
```

注意:
* `format=b`不能和`monitor=y`一起使用
* `format=b`不能和`cpu=old|times`一起使用
* `-Xrunhprof`接口可以继续使用, 例如`java -Xrunhprof:[help]|[<option>=<value>, ...]`.这个等同于`java -agentlib:hprof=[help]|[<option>=<value>, ...]`

我们看一个很简单的统计函数运行时间的示例
```bash
java -agentlib:hprof=cpu=times,interval=10 Test
```
