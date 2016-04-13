category: JVM
date: 2014-10-08
title: JVM 优化参数
---

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
*`-XX:+FailOverToOldVerifier`:当新的类型检测器失败时切换到旧的认证器
*`-XX:-AllowUserSignalHandlers`:允许为java进程安装信号处理器（限于Linux和Solaris,默认关闭）
*`-XX:AllocatePrefetchStyle=1`:预取指令的产生代码风格：0-没有预取指令,1-每一次分配内存就执行预取指令,2-当执行预取代码指令时,用TLAB分配水印指针指向门
*`-XX:AllocatePrefetchLines=1`:在使用JIT生成的预读取指令分配对象后读取的缓存行数.如果上次分配的对象是一个实例则默认值是1,如果是一个数组则是3
*`-XX:+OptimizeStringConcat`:对字符串拼接进行优化
*`-XX:+UseCompressedStrings`:如果可以表示为纯ASCII的话,则用byte[]代替字符串.
*`-XX:+UseBiasedLocking`:使用偏锁.
*`-XX:LoopUnrollLimit=n`:代表节点数目小于给定值时打开循环体.
*`-XX:+PerfDataSaveToFile`:Jvm退出时保存jvmstat的二进制数据.
*`-XX:+AlwaysPreTouch`:当JVM初始化时预先对Java堆进行预先摸底(堆中每个页归零处理).
*`-XX:InlineSmallCode=n`:当编译的代码小于指定的值时,内联编译的代码.
*`-XX:InitialTenuringThreshold=7`:设置初始的对象在新生代中最大存活次数.
*`-XX:+UseCompressedOops`:使用compressedpointers.这个参数默认在64bit的环境下默认启动,但是如果JVM的内存达到32G后,这个参数就会默认为不启动,因为32G内存后,压缩就没有多大必要了,要管理那么大的内存指针也需要很大的宽度了
*`-XX:AllocatePrefetchDistance=n`:为对象分配设置预取距离.
*`-XX:MaxInlineSize=35`:内联函数最大的字节码大小.
*`-XX:-TraceClassResolution`:追踪常量池resolutions.
*`-XX:FreqInlineSize=n`:经常执行方法内联的最大字节大小
*`-XX:-TraceLoaderConstraints`:跟踪加载器的限制记录.