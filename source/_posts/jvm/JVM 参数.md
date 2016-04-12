category: JVM
date: 2014-10-08
title: java虚拟机参数
---

## 内存管理参数
*`-XDisableExplicitGC`:忽略来自System.gc()方法触发的垃圾收集
*`-XExplicitGCInvokesConcurrent`:当收到System.gc()方法提交的垃圾收集申请时,使用CMS收集器收集
*`-XUseSerialGC`:打开此开关后使用Serial+SerialOld的收集器组合进行内存回收.
*`-XUseParNewGC`:虚拟机运行在Client模式下的默认值,打开此开关后,使用ParNew+SeialOld的收集器组合进行垃圾收集
*`-XUseConcMarkSweepGc`:打开次开关后使用`ParNew+CMS+SerialOld`收集器组合进行垃圾收集.如果CMS收集器出现`ConcurrentModeFailure`,则`SeialOld`收集器将作为后备收集器.
*`-XUseParallelGC`:虚拟机运行在Server模式下的默认值,打开此开关后,使用ParallelScavenge+SerialOld的收集器组合进行内存回收
*`-XUseParaelOldGC`:打开此开关后,使用ParallelScavenge+ParallelOld的收集器组合进行内存回收
*`-XSurvivorRatio`:新生代中Eden区和Survivor区的容量比值(默认为8)
*`-XPretenureSizeThreshold`:直接晋升到老年代的对象大小,设置这个参数后,大于这个参数的对象将直接在老年代分配
*`-XMaxTenuringThreshold`:晋升到老年代的对象年龄,每个对象在坚持过一次MinorGC之后,年龄就+1,当超过这个参数值时就进入老年代
*`-XUseAdaptiveSizePolicy`:动态调整java堆中各个区域的大小及进入老年代的年龄
*`-XHandlePromotionFailure`:是否允许分配担保失败,即老年代的剩余空间不足以应付新生代的整个Eden和Survivor区的所有对象都存活的极端情况
*`-XParallelGCThreads`:设置并行GC时进行内存回收的线程数(少于或等于8个CPU时默认值为CPU数量值,多于8个CPU时比CPU数量值小)
*`-XGCTimeRatio`:GC时间占总时间的比率.仅在使用ParallelScavenge收集器时生效
*`-XMaxGCPauseMillis`:设置GC最大停顿时间.仅在使用ParallelScavenge收集器时生效
*`-XCMSInitiatingOccupancyFraction`:设置CMS收集器在老年代空间被使用多少后触发垃圾收集
*`-XUseCMSCompactAtFullCollection`:设置CMS收集器在完成垃圾收集后是否要进行一次内存碎片整理
*`-XCMSFullGCBeforeCompaction`:设置CMS收集器在进行若干次垃圾收集后再启动一次内存碎片整理
*`-XScavengeBeforeFullGC`:在FullGC发生之前触发一次MinorGC
*`-XUseGCOverheadLimit`:禁止GC过程无限制的执行,如果过于频繁,就直接发生OutOfMemory
*`-XUseTLAB`:优先在本地线程缓冲区中分配对象,避免分配内存时的锁定过程
*`-XMaxHeapFreeRatio`:当Xmx值比Xms值大时,堆可以动态收缩和扩展,这个参数控制当堆空闲大于指定比率时自动收缩
*`-XMinHeapFreeRatio`:当Xmx值比Xms值大时,堆可以动态收缩和扩展,这个参数控制当堆空闲小于指定比率时自动收缩
*`-XMaxPermSize`:永久代的最大值
*`-Xms`:初始堆大小
*`-Xmx`:最大堆大小
*`-Xmn`:设置年轻代大小.整个JVM内存大小=年轻代大小+年老代大小+持久代大小.(Xms必须大于Xmn)

## 即时编译参数
*`CompileThreshold`:触发即时编译的阈值
*`OnStackReplacePercentage`:OSR比率,它是OSR即时编译阈值计算公司的一个参数,用于代替BackEdgeThreshold参数控制回边计数器的实际溢出阈值
*`ReservedCodeCacheSize`:即时编译器编译的代码缓存使得最大值

## 类型加载参数
*`UseSplitVerifier`:使用依赖StackMapTable信息的类型检查代替数据流分析,以加快字节码校验速度
*`FailOverToOldVerier`:当类型校验失败时,是否允许回到老的类型推到校验方式进行校验,如果开启则允许
*`RelaxAccessControlCheck`:在校验阶段放松对类型访问性的限制

## 多线程相关参数
*`UseSpinning`:开启自旋锁以免线程频繁的挂起和唤醒
*`PreBlockSpin`:使用自旋锁时默认的自旋次数
*`UseThreadPriorities`:使用本地线程优先级
*`UseBiaseLocking`:是否使用偏向锁,如果开启则使用
*`UseFastAccessorMethods`:当频繁反射执行某个方法时,生成字节码来加快反射的执行速度

## 性能参数
*`AggressiveOpts`:使用激进的优化特征,这些特征一般是具备正面和负面双重影响的,需要根据具体应用特点分析才能判定是否对性能有好处
*`UseLargePages`:如果可能,使用大内存分页,这项特性需要操作系统的支持
*`LargePageSizeInBytes`:使用指定大小的内存分页,这项特性需要操作系统的支持
*`StringCache`:是否使用字符串缓存,开启则使用

## 参数组合
*给远程服务器加debug
```
-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$debug_port
```
* suspend:这个参数是用来当JVM启动之后等待debug客户端连接,如果没有debug客户端连接,那么虚拟机就会一直等待，造成假死的现象

*`-XX:+UseVMInterruptibleIO`:线程中断前或是EINTR在OS_INTRPT中对于I/O操作的结果
*`-XX:-UseParallelOldGC`:所有的集合使用并行垃圾收集器.能够自动化地设置这个选项-XX:+UseParallelGC
*`-XX:+FailOverToOldVerifier`:当新的类型检测器失败时切换到旧的认证器
*`-XX:-AllowUserSignalHandlers`:允许为java进程安装信号处理器（限于Linux和Solaris,默认关闭）


*`-XX:NewRatio=n`:老年代与新生代比例(默认是2).
*`-XX:ConcGCThreads=n`:`concurrentgarbagecollectors`使用的线程数.(默认值与JVM所在平台有关).
*`-XX:+UseG1GC`:使用`GarbageFirst(G1)`收集器
*`-XX:InitiatingHeapOccupancyPercent=n`:设置触发标记周期的Java堆占用率阈值.默认占用率是整个Java堆的45%.
*`-XX:G1HeapRegionSize=n`:设置的G1区域的大小.值是2的幂,范围是1MB到32MB之间.目标是根据最小的Java堆大小划分出约2048个区域.
*`-XX:G1ReservePercent=n`:设置作为空闲空间的预留内存百分比,以降低目标空间溢出的风险.默认值是10%.增加或减少百分比时,请确保对总的Java堆调整相同的量.JavaHotSpotVMbuild23中没有此设置.


*`-XX:AllocatePrefetchStyle=1`:预取指令的产生代码风格：0-没有预取指令,1-每一次分配内存就执行预取指令,2-当执行预取代码指令时,用TLAB分配水印指针指向门
*`-XX:NewSize=2m`:新生代默认大小(单位是字节)
*`-XX:AllocatePrefetchLines=1`:在使用JIT生成的预读取指令分配对象后读取的缓存行数.如果上次分配的对象是一个实例则默认值是1,如果是一个数组则是3
*`-XX:+OptimizeStringConcat`:对字符串拼接进行优化
*`-XX:MaxNewSize=size`:新生代最大值(单位字节)
*`-XX:ThreadStackSize=512`:线程堆栈大小(单位Kbytes,0使用默认大小)
*`-XX:+UseCompressedStrings`:如果可以表示为纯ASCII的话,则用byte[]代替字符串.
*`-XX:+UseBiasedLocking`:使用偏锁.


*`-XX:LoopUnrollLimit=n`:代表节点数目小于给定值时打开循环体.
*`-XX:GCLogFileSize=8K`:gc日志文件大小(必须>=8K).
*`-XX:HeapDumpPath=./java_pid<pid>.hprof`:堆内存溢出存放日志目录.
*`-XX:+PerfDataSaveToFile`:Jvm退出时保存jvmstat的二进制数据.
*`-Xloggc:<filename>`:gc日志文件
*`-XX:+AlwaysPreTouch`:当JVM初始化时预先对Java堆进行预先摸底(堆中每个页归零处理).
*`-XX:InlineSmallCode=n`:当编译的代码小于指定的值时,内联编译的代码.
*`-XX:InitialTenuringThreshold=7`:设置初始的对象在新生代中最大存活次数.
*`-XX:+UseCompressedOops`:使用compressedpointers.这个参数默认在64bit的环境下默认启动,但是如果JVM的内存达到32G后,这个参数就会默认为不启动,因为32G内存后,压缩就没有多大必要了,要管理那么大的内存指针也需要很大的宽度了
*`-XX:-PrintAdaptiveSizePolicy`:打印JVM自动划分新生代和老生代大小信息.
*`-XX:AllocatePrefetchDistance=n`:为对象分配设置预取距离.
*`-XX:MaxInlineSize=35`:内联函数最大的字节码大小.
*`-XX:-UseGCLogFileRotation`:开启GC日志文件切分功能,前置选项-Xloggc
*`-XX:-CITime`:打印`JITCompiler`的耗时
*`-XX:-TraceClassResolution`:追踪常量池resolutions.
*`-XX:FreqInlineSize=n`:经常执行方法内联的最大字节大小
*`-XX:-TraceLoaderConstraints`:跟踪加载器的限制记录.
*`-XX:ErrorFile=./hs_err_pid<pid>.log`:如果有Error发生,则将Error输入到该日志.
*`-XX:NumberOfGClogFiles=1`:设置Gc日志文件的数量(必须大于1)
*`-XX:-PrintTenuringDistribution`:打印对象的存活期限信息.
