category: jvm7
date: 2014-10-08
title: java虚拟机参数
---
# java虚拟机参数


## 内存管理参数
* `-XDisableExplicitGC`: 忽略来自System.gc()方法触发的垃圾收集
* `-XExplicitGCInvokesConcurrent`:  当收到System.gc()方法提交的垃圾收集申请时,使用CMS收集器收集
* `-XUseSerialGC`: 打开此开关后使用Serial + Serial Old的收集器组合进行内存回收.
* `-XUseParNewGC`: 虚拟机运行在Client模式下的默认值,打开此开关后,使用ParNew+Seial Old的收集器组合进行垃圾收集
* `-XUseConcMarkSweepGc`:  打开次开关后使用`: ParNew+CMS+Serial Old`: 收集器组合进行垃圾收集.如果CMS收集器出现`: Concurrent Mode Failure`: ,则`: Seial Old`: 收集器将作为后备收集器.
* `-XUseParallelGC`: 虚拟机运行在Server模式下的默认值,打开此开关后,使用Parallel Scavenge + Serial Old的收集器组合进行内存回收
* `-XUseParaelOldGC`: 打开此开关后,使用Parallel Scavenge + Parallel Old的收集器组合进行内存回收
* `-XSurvivorRatio`:  新生代中Eden区和Survivor区的容量比值(默认为8)
* `-XPretenureSizeThreshold`: 直接晋升到老年代的对象大小,设置这个参数后,大于这个参数的对象将直接在老年代分配
* `-XMaxTenuringThreshold`: 晋升到老年代的对象年龄,每个对象在坚持过一次Minor GC之后,年龄就+1,当超过这个参数值时就进入老年代
* `-XUseAdaptiveSizePolicy`: 动态调整java堆中各个区域的大小及进入老年代的年龄
* `-XHandlePromotionFailure`: 是否允许分配担保失败,即老年代的剩余空间不足以应付新生代的整个Eden和Survivor区的所有对象都存活的极端情况
* `-XParallelGCThreads`: 设置并行GC时进行内存回收的线程数(少于或等于8个CPU时默认值为CPU数量值,多于8个CPU时比CPU数量值小)
* `-XGCTimeRatio`: GC时间占总时间的比率.仅在使用Parallel Scavenge收集器时生效
* `-XMaxGCPauseMillis`: 设置GC最大停顿时间.仅在使用Parallel Scavenge收集器时生效
* `-XCMSInitiatingOccupancyFraction`: 设置CMS收集器在老年代空间被使用多少后触发垃圾收集
* `-XUseCMSCompactAtFullCollection`: 设置CMS收集器在完成垃圾收集后是否要进行一次内存碎片整理
* `-XCMSFullGCBeforeCompaction`: 设置CMS收集器在进行若干次垃圾收集后再启动一次内存碎片整理
* `-XScavengeBeforeFullGC`: 在Full GC发生之前触发一次Minor GC
* `-XUseGCOverheadLimit`: 禁止GC过程无限制的执行,如果过于频繁,就直接发生OutOfMemory
* `-XUseTLAB`: 优先在本地线程缓冲区中分配对象,避免分配内存时的锁定过程
* `-XMaxHeapFreeRatio`: 当Xmx值比Xms值大时,堆可以动态收缩和扩展,这个参数控制当堆空闲大于指定比率时自动收缩
* `-XMinHeapFreeRatio`: 当Xmx值比Xms值大时,堆可以动态收缩和扩展,这个参数控制当堆空闲小于指定比率时自动收缩
* `-XMaxPermSize`: 永久代的最大值
* `-Xms` : 初始堆大小
* `-Xmx` : 最大堆大小
* `-Xmn` : 设置年轻代大小. 整个JVM内存大小=年轻代大小 + 年老代大小 + 持久代大小.(Xms 必须大于  Xmn)

## 即时编译参数
* `CompileThreshold`: 触发即时编译的阈值
* `OnStackReplacePercentage`: OSR比率,它是OSR即时编译阈值计算公司的一个参数,用于代替BackEdgeThreshold参数控制回边计数器的实际溢出阈值
* `ReservedCodeCacheSize`: 即时编译器编译的代码缓存使得最大值

## 类型加载参数
* `UseSplitVerifier`: 使用依赖StackMapTable信息的类型检查代替数据流分析,以加快字节码校验速度
* `FailOverToOldVerier`: 当类型校验失败时,是否允许回到老的类型推到校验方式进行校验,如果开启则允许
* `RelaxAccessControlCheck`: 在校验阶段放松对类型访问性的限制

## 多线程相关参数
* `UseSpinning`: 开启自旋锁以免线程频繁的挂起和唤醒
* `PreBlockSpin`: 使用自旋锁时默认的自旋次数
* `UseThreadPriorities`: 使用本地线程优先级
* `UseBiaseLocking`: 是否使用偏向锁,如果开启则使用
* `UseFastAccessorMethods`: 当频繁反射执行某个方法时,生成字节码来加快反射的执行速度

## 性能参数
* `AggressiveOpts`: 使用激进的优化特征,这些特征一般是具备正面和负面双重影响的,需要根据具体应用特点分析才能判定是否对性能有好处
* `UseLargePages`: 如果可能,使用大内存分页,这项特性需要操作系统的支持
* `LargePageSizeInBytes`: 使用指定大小的内存分页,这项特性需要操作系统的支持
* `StringCache`: 是否使用字符串缓存,开启则使用

## 调试参数
* `HeapDumpOnOutOfMemoryError`: 在发生内存溢出异常时是否生成堆转储快照,关闭则不生成
* `OnOutOfMemoryError`: 当虚拟机抛出内存溢出异常时,执行指令的命令
* `OnError`: 当虚拟机抛出ERROR异常时,执行指令的命令
* `PrintClassHistogram`: 使用[ctrl]-[break]快捷键输出类统计状态,相当于jmap-histo的功能
* `PrintConcurrentLocks`: 打印J.U.C中的状态
* `PrintCommandLineFlags`: 打印启动虚拟机时输入的非稳定参数
* `PrintGC`: 打印GC信息
* `PrintCompilation`: 显示所有可设置的参数及它们的值(***从JDK 6 update 21开始才可以用)
* `PrintGCDetails`: 打印GC的详细信息
* `PrintGCTimesStamps`: 打印GC停顿耗时
* `PrintTenuingDistribution`: 打印GC后新生代各个年龄对象的大小
* `TraceClassLoading`: 打印类加载信息
* `TraceClassUnloading`: 打印类卸载信息
* `PrintInlining`: 打印方法内联信息
* `PrintCFGToFile`: 将CFG图信息输出到文件,只有DEBUG版虚拟机才支持此参数
* `PrintIdealGraphFile`: 将Ideal图信息输出到文件,只有DEBUG版虚拟机才支持此参数
* `UnlockDiagnosticVMOptions`: 让虚拟机进入诊断模式,一些参数(如PrintAssembly)需要在诊断模式中才能使用
* `PrintAssembly`: 打印即时编译后的二进制信息


## 参数组合
* 给远程服务器加debug
```
-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=10020
```

## 其他参数

#### Behavioral Options
* `OptionAndDefaultValue` :  Description
* `-XX:+UseVMInterruptibleIO` :  Thread interrupt before or with EINTR for I/O operations results in OS_INTRPT.
* `-XX:+MaxFDLimit` :  Bump the number of file descriptors to max.
* `-XX:-UseConcMarkSweepGC` :  Use concurrent mark-sweep collection for the old generation.
* `-XX:-UseParallelOldGC` :  Use parallel garbage collection for the full collections. Enabling this option automatically sets -XX:+UseParallelGC.
* `-XX:+UseAltSigs` :  Use alternate signals instead of SIGUSR1 and SIGUSR2 for VM internal signals. (Introduced in 1.3.1 update 9, 1.4.1. Relevant to Solaris only.)
* `-XX:+FailOverToOldVerifier` :  Fail over to old verifier when the new type checker fails.
* `-XX:-AllowUserSignalHandlers` :  Do not complain if the application installs signal handlers. (Relevant to Solaris and Linux only.)
* `OptionAndDefaultValue` :  Description
* `-XX:NewRatio=n` :  Ratio of old/new generation sizes. The default value is 2.
* `-XX:ConcGCThreads=n` :  Number of threads concurrent garbage collectors will use. The default value varies with the platform on which the JVM is running.
* `-XX:+UseG1GC` :  Use the Garbage First (G1) Collector
* `-XX:InitiatingHeapOccupancyPercent=n` :  Percentage of the (entire) heap occupancy to start a concurrent GC cycle. It is used by GCs that trigger a concurrent GC cycle based on the occupancy of the entire heap, not just one of the generations (e.g., G1). A value of 0 denotes 'do constant GC cycles'. The default value is 45.
* `-XX:G1HeapRegionSize=n` :  With G1 the Java heap is subdivided into uniformly sized regions. This sets the size of the individual sub-divisions. The default value of this parameter is determined ergonomically based upon heap size. The minimum value is 1Mb and the maximum value is 32Mb.
* `-XX:G1ReservePercent=n` :  Sets the amount of heap that is reserved as a false ceiling to reduce the possibility of promotion failure. The default value is 10.
* `OptionAndDefaultValue` :  Description
* `-XX:AllocatePrefetchStyle=1` :  Generated code style for prefetch instructions.
* `-XX:+UseMPSS` :  Use Multiple Page Size Support w/4mb pages for the heap. Do not use with ISM as this replaces the need for ISM. (Introduced in 1.4.0 update 1, Relevant to Solaris 9 and newer.) [1.4.1 and earlier: false]
* `-XX:NewSize=2m` :  Default size of new generation (in bytes) [5.0 and newer: 64 bit VMs are scaled 30% larger; x86: 1m; x86, 5.0 and older: 640k]
* `-XX:AllocatePrefetchLines=1` :  Number of cache lines to load after the last object allocation using prefetch instructions generated in JIT compiled code. Default values are 1 if the last allocated object was an instance and 3 if it was an array.
* `-XX:+OptimizeStringConcat` :  Optimize String concatenation operations where possible. (Introduced in Java 6 Update 20)
* `-XX:-UseISM` :  Use Intimate Shared Memory. [Not accepted for non-Solaris platforms.] For details, see Intimate Shared Memory.
* `-XX:NewRatio=2` :  Ratio of old/new generation sizes. [Sparc -client: 8; x86 -server: 8; x86 -client: 12.]-client: 4 (1.3) 8 (1.3.1+), x86: 12]
* `-XX:MaxNewSize=size` :  Maximum size of new generation (in bytes). Since 1.4, MaxNewSize is computed as a function of NewRatio. [1.3.1 Sparc: 32m; 1.3.1 x86: 2.5m.]
* `-XX:ThreadStackSize=512` :  Thread Stack Size (in Kbytes). (0 means use default stack size) [Sparc: 512; Solaris x86: 320 (was 256 prior in 5.0 and earlier); Sparc 64 bit: 1024; Linux amd64: 1024 (was 0 in 5.0 and earlier); all others 0.]
* `-XX:+UseCompressedStrings` :  Use a byte[] for Strings which can be represented as pure ASCII. (Introduced in Java 6 Update 21 Performance Release)
* `-XX:+UseBiasedLocking` :  Enable biased locking. For more details, see this tuning example. (Introduced in 5.0 update 6.) [5.0: false]
* `OptionAndDefaultValue` :  Description
* `-XX:LoopUnrollLimit=n` :  Unroll loop bodies with server compiler intermediate representation node count less than this value. The limit used by the server compiler is a function of this value, not the actual value. The default value varies with the platform on which the JVM is running.
* `-XX:GCLogFileSize=8K` :  The size of the log file at which point the log will be rotated, must be >= 8K.
* `-XX:HeapDumpPath=./java_pid<pid>.hprof` :  Path to directory or filename for heap dump. Manageable. (Introduced in 1.4.2 update 12, 5.0 update 7.)
* `-XX:+PerfDataSaveToFile` :  Saves jvmstat binary data on exit.
* `-Xloggc:<filename>` :  Log GC verbose output to specified file. The verbose output is controlled by the normal verbose GC flags.
* `-XX:+AlwaysPreTouch` :  Pre-touch the Java heap during JVM initialization. Every page of the heap is thus demand-zeroed during initialization rather than incrementally during application execution.
* `-XX:InlineSmallCode=n` :  Inline a previously compiled method only if its generated native code size is less than this. The default value varies with the platform on which the JVM is running.
* `-XX:InitialTenuringThreshold=7` :  Sets the initial tenuring threshold for use in adaptive GC sizing in the parallel young collector. The tenuring threshold is the number of times an object survives a young collection before being promoted to the old, or tenured, generation.
* `-XX:+UseCompressedOops` :  Enables the use of compressed pointers (object references represented as 32 bit offsets instead of 64-bit pointers) for optimized 64-bit performance with Java heap sizes less than 32gb.
* `-XX:-PrintAdaptiveSizePolicy` :  Enables printing of information about adaptive generation sizing.
* `-XX:AllocatePrefetchDistance=n` :  Sets the prefetch distance for object allocation. Memory about to be written with the value of new objects is prefetched into cache at this distance (in bytes) beyond the address of the last allocated object. Each Java thread has its own allocation point. The default value varies with the platform on which the JVM is running.
* `-XX:MaxInlineSize=35` :  Maximum bytecode size of a method to be inlined.
* `-XX:-UseGCLogFileRotation` :  Enabled GC log rotation, requires -Xloggc.
* `-XX:-CITime` :  Prints time spent in JIT Compiler. (Introduced in 1.4.0.)
* `-XX:-TraceClassResolution` :  Trace constant pool resolutions. (Introduced in 1.4.2.)
* `-XX:FreqInlineSize=n` :  Maximum bytecode size of a frequently executed method to be inlined. The default value varies with the platform on which the JVM is running.
* `-XX:-TraceLoaderConstraints` :  Trace recording of loader constraints. (Introduced in 6.)
* `-XX:ErrorFile=./hs_err_pid<pid>.log` :  If an error occurs, save the error data to this file. (Introduced in 6.)
* `-XX:NumberOfGClogFiles=1` :  Set the number of files to use when rotating logs, must be >= 1. The rotated log files will use the following naming scheme, <filename>.0, <filename>.1, ..., <filename>.n-1.
* `-XX:-ExtendedDTraceProbes` :  Enable performance-impacting dtrace probes. (Introduced in 6. Relevant to Solaris only.)
* `-XX:-PrintTenuringDistribution` :  Print tenuring age information.
