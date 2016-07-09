category: JVM
date: 2014-11-28
title: JVM 参数 -- 日志输出
---
本文主要列举了JVM中常用的日志输出参数

## GC 输出

### PrintGC
在jvm选项上添加上这个参数,只要遇上GC就会输出GC日志. 我们写一个测试程序
```java
public class TestJVMLogArguments {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            byte[] bytes = new byte[1024 * 924 * 7];
            System.out.println("Allocate " + i);
        }
    }
}
```
我们采用虚拟机参数`-XX:+PrintGC -Xmx10M -Xms10M`看一下结果
```xml
Allocate 0
[GC (Allocation Failure)  8003K->7116K(9728K), 0.0013420 secs]
[Full GC (Ergonomics)  7116K->624K(9728K), 0.0063270 secs]
Allocate 1
[Full GC (Ergonomics)  7191K->592K(9728K), 0.0068230 secs]
Allocate 2
```
我们看到一共进行里3次GC(1次新生代GC, 俩次Full GC)
1. GC前, 堆使用内存为8003K, GC后堆内存使用为7116K, 当前堆可用的堆内存为9728K, 本次GC耗时0.0013420秒
2. GC前, 堆使用内存为7116K, GC后堆内存使用为624K, 当前堆可用的堆内存为9728K, 本次GC耗时0.0063270秒
3. GC前, 堆使用内存为7191K, GC后堆内存使用为592K, 当前堆可用的堆内存为9728K, 本次GC耗时0.0068230秒

### PrintGCDetails
这个参数相比于PrintGC,会输出更加详细的信息. 同样使用上面的测试程序, 然后使用虚拟机参数`-XX:+PrintGCDetails -Xmx10M -Xms10M`, 然后看一下输出
```xml
Allocate 0
[GC (Allocation Failure) [PSYoungGen: 1535K->512K(2560K)] 8003K->7136K(9728K), 0.0020620 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[Full GC (Ergonomics) [PSYoungGen: 512K->0K(2560K)] [ParOldGen: 6624K->623K(7168K)] 7136K->623K(9728K), [Metaspace: 3089K->3089K(1056768K)], 0.0067020 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
Allocate 1
[Full GC (Ergonomics) [PSYoungGen: 77K->0K(2560K)] [ParOldGen: 7091K->592K(7168K)] 7168K->592K(9728K), [Metaspace: 3092K->3092K(1056768K)], 0.0070690 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
Allocate 2
Heap
 PSYoungGen      total 2560K, used 78K [0x00000007bfd00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 2048K, 3% used [0x00000007bfd00000,0x00000007bfd138e0,0x00000007bff00000)
  from space 512K, 0% used [0x00000007bff00000,0x00000007bff00000,0x00000007bff80000)
  to   space 512K, 0% used [0x00000007bff80000,0x00000007bff80000,0x00000007c0000000)
 ParOldGen       total 7168K, used 7060K [0x00000007bf600000, 0x00000007bfd00000, 0x00000007bfd00000)
  object space 7168K, 98% used [0x00000007bf600000,0x00000007bfce5308,0x00000007bfd00000)
 Metaspace       used 3099K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 340K, capacity 386K, committed 512K, reserved 1048576K
```
GC过程为
1. 新生代GC, 新生代已用内存从1535K变为512K, 新生代可用内存为2560K. 整个堆内存从8003K变为7136K,整个堆可用内存为9728K, 耗时为0.0020620 秒.
2. Full GC, 新生代将已用内存512k清空, 可用内存为2560K. 老年代已用内存从7091K变成592K, 整个老年代可用内存为7168K. 整个堆内存已用内存从7136K变成623K, 整个堆可用内存为9728K. Metaspace区(Java8里的方法区)没有回收, 整个Full GC耗时0.0067020 秒.

最后还输出了虚拟机退出时, 整个虚拟机的内存分布情况
1. 新生代总共有2560K的内存, 使用了78K
2. 新生代eden区为2048K, 使用了3%
3. 新生代survivor区(from部分)为512k, 没有使用
4. 新生代survivor区(to部分)为512k, 没有使用
5. 老年代(ParOld垃圾回收器) 总共内存为7168K, 使用里7060K.

### PrintHeapAtGC
这个参数会在GC前后打印出堆内信息
```java
public class TestJVMLogArguments {
    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            byte[] bytes = new byte[1024 * 924 * 7];
            System.out.println("Allocate " + i);
        }
    }
}
```
我们使用虚拟机参数`-XX:+PrintHeapAtGC -Xmx10M -Xms10M`运行程序输出结果
```xml
Allocate 0
{Heap before GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 1535K [0x00000007bfd00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 2048K, 74% used [0x00000007bfd00000,0x00000007bfe7fe40,0x00000007bff00000)
  from space 512K, 0% used [0x00000007bff80000,0x00000007bff80000,0x00000007c0000000)
  to   space 512K, 0% used [0x00000007bff00000,0x00000007bff00000,0x00000007bff80000)
 ParOldGen       total 7168K, used 6468K [0x00000007bf600000, 0x00000007bfd00000, 0x00000007bfd00000)
  object space 7168K, 90% used [0x00000007bf600000,0x00000007bfc51010,0x00000007bfd00000)
 Metaspace       used 3097K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 512K [0x00000007bfd00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 2048K, 0% used [0x00000007bfd00000,0x00000007bfd00000,0x00000007bff00000)
  from space 512K, 100% used [0x00000007bff00000,0x00000007bff80000,0x00000007bff80000)
  to   space 512K, 0% used [0x00000007bff80000,0x00000007bff80000,0x00000007c0000000)
 ParOldGen       total 7168K, used 6616K [0x00000007bf600000, 0x00000007bfd00000, 0x00000007bfd00000)
  object space 7168K, 92% used [0x00000007bf600000,0x00000007bfc763a8,0x00000007bfd00000)
 Metaspace       used 3097K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 339K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=2 (full 1):
 ...
}
Allocate 1
```
限于篇幅, 我只输出了一次的GC日志. 大部分的内容解释我们都可以在`PrintGCDetails`例子解释中找到答案. 需要特别指出的是
* invocations=1 : 代表这是第一次GC
* (full 0) : 表示虚拟机进行的Full GC的次数

### GC开始时间
`PrintGCTimeStamps`参数会在每次GC的时候,输出GC发生的时间. 我们还是使用上面的测试程序, 指定虚拟机参数`-XX:+PrintGC -XX:+PrintGCTimeStamps -Xmx10M -Xms10M`看一下输出结果
```xml
Allocate 0
0.285: [GC (Allocation Failure)  8003K->7124K(9728K), 0.0015210 secs]
0.286: [Full GC (Ergonomics)  7124K->622K(9728K), 0.0072690 secs]
Allocate 1
```
1. 第一次GC发生在JVM启动0.285秒时进行的
2. 第二次GC发生在JVM启动0.286秒时进行的

`PrintGCDateStamps`输出比PrintGCTimeStamps可读性更好, 因为它输出的是系统时间. 仍然使用上面的测试程序, 然后使用JVM参数`-XX:+PrintGC -Xmx10M -Xms10M -XX:+PrintGCDateStamps`. 结果为
```bash
2016-05-18T17:28:34.689+0800: [GC (Allocation Failure)  2028K->1006K(9728K), 0.0076949 secs]
2016-05-18T17:28:34.741+0800: [GC (Allocation Failure)  3052K->1565K(9728K), 0.0191430 secs]
2016-05-18T17:28:34.772+0800: [GC (Allocation Failure)  1750K->1597K(9728K), 0.0088998 secs]
2016-05-18T17:28:34.781+0800: [GC (Allocation Failure)  1597K->1603K(9728K), 0.0371759 secs]
2016-05-18T17:28:34.818+0800: [Full GC (Allocation Failure)  1603K->1068K(9728K), 0.0078928 secs]
2016-05-18T17:28:34.826+0800: [GC (Allocation Failure)  1068K->1068K(9728K), 0.0185343 secs]
2016-05-18T17:28:34.844+0800: [Full GC (Allocation Failure)  1068K->1041K(9728K), 0.0176116 secs]
```
> 这个参数必须和`-XX:+PrintGC`, `-XX:+PrintGCDetails`一起使用,但是和`-XX:+PrintHeapAtGC`一起使用就无效了

### PrintGCApplicationConcurrentTime
输出应用程序从上次GC到这次GC的程序执行时间. 我们运行一下上面的程序, 看一下输出结果为
```xml
ζ java -XX:+PrintGCApplicationConcurrentTime -Xmx10M -Xms10M TestJVMLogArguments
Allocate 0
Application time: 0.0120062 seconds
Allocate 1
Application time: 0.0016650 seconds
```

### GC停顿时间
`PrintGCApplicationStoppedTime`打印程序因为GC停顿的时间. 运行一下上面的程序
```xml
ζ java -XX:+PrintGCApplicationStoppedTime -Xmx10M -Xms10M TestJVMLogArguments
Allocate 0
Total time for which application threads were stopped: 0.0076620 seconds, Stopping threads took: 0.0000163 seconds
Allocate 1
```
我们看到整个应用在Full GC时, 停顿了 0.0089770 秒.

### 各个年龄对象大小
`PrintTenuringDistribution`打印GC后新生代各个年龄对象的大小。 我们使用最开始的测试程序看一下结果
```bash
ζ java -XX:+PrintTenuringDistribution -Xmx10M -Xms10M TestJVMLogArguments
Allocate 0

Desired survivor size 524288 bytes, new threshold 7 (max 15)
Allocate 1
```

### GC日志文件
* UseGCLogFileRotation : 开启GC日志文件切分功能,前置选项-Xloggc, `-XX:+UseGCLogFileRotation`
* NumberOfGCLogFiles : 设置Gc日志文件的数量(必须大于1), `-XX:NumberOfGCLogFiles=10`
* GCLogFileSize : gc日志文件大小(必须>=8K), `-XX:GCLogFileSize=8K`
* Xloggc : `-Xloggc:<filename>`gc日志文件
我们使用测试程序
```java

```
然后指定虚拟机参数`-XX:+PrintGCDetails -XX:+PrintHeapAtGC -XX:NumberOfGCLogFiles=3 -XX:GCLogFileSize=10K -XX:+UseGCLogFileRotation -Xloggc:gc.log`来运行一下. 最后我们会看到三个文件
* gc.log.1 (11,016 字节)
* gc.log.2 (11,755 字节)
* gc.log.0.current (780 字节)
我们看到使用这四个参数已经成功将日志输出到三个文件中.
> 注意每次生成日志的时候都会将上次运行JVM的日志覆盖掉. 因此每次关服重启时, 最好将日志文件备份一下.

## 异常输出

### HeapDumpOnOutOfMemoryError
在发生内存溢出异常时是否生成堆转储快照,关闭则不生成`-XX:+HeapDumpOnOutOfMemoryError`

> `-XX:HeapDumpPath=./java_pid<pid>.hprof`:堆内存溢出存放日志目录.
更多参考[JVM内存溢出之 Heap OOM]()

### OnOutOfMemoryError
当虚拟机抛出内存溢出异常时,执行指令的命令`-XX:+OnOutOfMemoryError=...`例如
```bash
java -Xmx10M -Xms10M -Xmn6M -XX:OnOutOfMemoryError="jstack %p" TestJVMLogArguments 
```
当虚拟机内存溢出时执行jstack命令

### OnError
当虚拟机抛出ERROR异常时,执行指令的命令`-XX:+OnError=<cmd args>;<cmd args>`例如
```bash
java -Xmx10M -Xms10M -Xmn6M -XX:OnError="jstack %p > jstack;jmap %p > jmap" TestJVMLogArguments
```

> `-XX:ErrorFile=./hs_err_pid<pid>.log`:如果有Error发生,则将Error输入到该日志.

## 运行时状态

### PrintReferenceGC
追踪系统内的软引用,弱引用,虚引用和Finallize队列的话可以使用`PrintReferenceGC`.

### verbose:class
* `-verbose:class` : 跟踪类的加载和卸载
* `-XX:+TraceClassLoading` : 追踪类的加载
* `-XX:+TraceClassUnloading` : 追踪类的卸载
我们运行最开始的那个测试程序, 看一下JVM启动时加载的类`-verbose:class`
```bash
[Opened /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.lang.Object from /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/lib/rt.jar]
```
这个特性用在使用ASM动态生成类的应用中特别有用, 因为这些类是由逻辑代码控制的, 加载这些类的行为具有一定的隐蔽性, 因此我们可以在虚拟机启动时, 加上这个参数做日志分析用.

### CITime
`-XX:+CITime`:打印`JITCompiler`的耗时

### PrintConcurrentLocks
打印J.U.C中的状态`-XX:+PrintConcurrentLocks`

### PrintInlining
打印方法内联信息`-XX:+PrintInlining`

### PrintAssembly
打印即时编译后的二进制信息

### PrintAdaptiveSizePolicy
`-XX:-PrintAdaptiveSizePolicy` :打印JVM自动划分新生代和老生代大小信息.
这个选项最好和-XX:+PrintGCDetails以及-XX:+PrintGCDateStamps或者-XX:+PrintGCTimeStamps一起使用.以GCAdaptiveSizePolicy开头的一些额外信息输出来了，survived标签表明“to” survivor空间的对象字节数。在这个例子中，survivor空间占用量是224408984字节，但是移动到old代的字节数却有10904856字节。overflow表明young代是否有对象溢出到old代，换句话说，就是表明了“to” survivor是否有足够的空间来容纳从eden空间和“from”survivor空间移动而来的对象。为了更好的吞吐量，期望在应用处于稳定运行状态下，survivor空间不要溢出。

## 系统相关

### PrintVMOptions
输出程序启动时, 虚拟机接收到命令行指定的参数. 我们指定虚拟机参数`-XX:+PrintVMOptions -Xmx10M -Xms10M`, 输出结果为
```bash
VM option '+PrintVMOptions'
```
> TODO 为什么输出只有PrintVMOptions, 而没有-Xmx10M -Xms10M呢?

### PrintCommandLineFlags
打印启动虚拟机时输入的非稳定参数`-XX:+PrintCommandLineFlags`
```xml
-XX:InitialHeapSize=10485760 -XX:MaxHeapSize=10485760 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
```

### PrintFlagsFinal
输出所有的系统的参数的值, 我们使用-XX:+PrintFlagsFinal`, 看一下结果
```bash
[Global flags]
    uintx AdaptiveSizeDecrementScaleFactor          = 4               {product}
    uintx AdaptiveSizeMajorGCDecayTimeScale         = 10              {product}
    uintx AdaptiveSizePausePolicy                   = 0               {product}
    uintx AdaptiveSizePolicyCollectionCostMargin    = 50              {product}
    uintx AdaptiveSizePolicyInitializingSteps       = 20              {product}
    uintx AdaptiveSizePolicyOutputInterval          = 0               {product}
    uintx AdaptiveSizePolicyWeight                  = 10              {product}
    uintx AdaptiveSizeThroughPutPolicy              = 0               {product}
    uintx AdaptiveTimeWeight                        = 25              {product}
     bool AdjustConcurrency                         = false           {product}
     bool AggressiveOpts                            = false           {product}
     intx AliasLevel                                = 3               {C2 product}
     bool AlignVector                               = false           {C2 product}
     intx AllocateInstancePrefetchLines             = 1               {product}
     intx AllocatePrefetchDistance                  = 192             {product}
     intx AllocatePrefetchInstr                     = 0               {product}
     intx AllocatePrefetchLines                     = 4               {product}
     intx AllocatePrefetchStepSize                  = 64              {product}
     intx AllocatePrefetchStyle                     = 1               {product}
     bool AllowJNIEnvProxy                          = false           {product}
     bool AllowNonVirtualCalls                      = false           {product}
     bool AllowParallelDefineClass                  = false           {product}
     bool AllowUserSignalHandlers                   = false           {product}
     bool AlwaysActAsServerClassMachine             = false           {product}
     bool AlwaysCompileLoopMethods                  = false           {product}
     bool AlwaysLockClassLoader                     = false           {product}
     bool AlwaysPreTouch                            = false           {product}
     bool AlwaysRestoreFPU                          = false           {product}
     bool AlwaysTenure                              = false           {product}
     bool AssertOnSuspendWaitFailure                = false           {product}
     bool AssumeMP                                  = false           {product}
     intx AutoBoxCacheMax                           = 128             {C2 product}
    uintx AutoGCSelectPauseMillis                   = 5000            {product}
     intx BCEATraceLevel                            = 0               {product}
     intx BackEdgeThreshold                         = 100000          {pd product}
     bool BackgroundCompilation                     = true            {pd product}
    uintx BaseFootPrintEstimate                     = 268435456       {product}
     intx BiasedLockingBulkRebiasThreshold          = 20              {product}
     intx BiasedLockingBulkRevokeThreshold          = 40              {product}
     intx BiasedLockingDecayTime                    = 25000           {product}
     intx BiasedLockingStartupDelay                 = 4000            {product}
     bool BindGCTaskThreadsToCPUs                   = false           {product}
     bool BlockLayoutByFrequency                    = true            {C2 product}
     intx BlockLayoutMinDiamondPercentage           = 20              {C2 product}
     bool BlockLayoutRotateLoops                    = true            {C2 product}
     bool BranchOnRegister                          = false           {C2 product}
     bool BytecodeVerificationLocal                 = false           {product}
     bool BytecodeVerificationRemote                = true            {product}
     bool C1OptimizeVirtualCallProfiling            = true            {C1 product}
     bool C1ProfileBranches                         = true            {C1 product}
     bool C1ProfileCalls                            = true            {C1 product}
     bool C1ProfileCheckcasts                       = true            {C1 product}
     bool C1ProfileInlinedCalls                     = true            {C1 product}
     bool C1ProfileVirtualCalls                     = true            {C1 product}
     bool C1UpdateMethodData                        = true            {C1 product}
     intx CICompilerCount                           = 2               {product}
     bool CICompilerCountPerCPU                     = true            {product}
     bool CITime                                    = false           {product}
     bool CMSAbortSemantics                         = false           {product}
    uintx CMSAbortablePrecleanMinWorkPerIteration   = 100             {product}
     intx CMSAbortablePrecleanWaitMillis            = 100             {manageable}
    uintx CMSBitMapYieldQuantum                     = 10485760        {product}
    uintx CMSBootstrapOccupancy                     = 50              {product}
     bool CMSClassUnloadingEnabled                  = true            {product}
    uintx CMSClassUnloadingMaxInterval              = 0               {product}
     bool CMSCleanOnEnter                           = true            {product}
     bool CMSCompactWhenClearAllSoftRefs            = true            {product}
    uintx CMSConcMarkMultiple                       = 32              {product}
     bool CMSConcurrentMTEnabled                    = true            {product}
    uintx CMSCoordinatorYieldSleepCount             = 10              {product}
     bool CMSDumpAtPromotionFailure                 = false           {product}
     bool CMSEdenChunksRecordAlways                 = true            {product}
    uintx CMSExpAvgFactor                           = 50              {product}
     bool CMSExtrapolateSweep                       = false           {product}
    uintx CMSFullGCsBeforeCompaction                = 0               {product}
    uintx CMSIncrementalDutyCycle                   = 10              {product}
    uintx CMSIncrementalDutyCycleMin                = 0               {product}
     bool CMSIncrementalMode                        = false           {product}
    uintx CMSIncrementalOffset                      = 0               {product}
     bool CMSIncrementalPacing                      = true            {product}
    uintx CMSIncrementalSafetyFactor                = 10              {product}
    uintx CMSIndexedFreeListReplenish               = 4               {product}
     intx CMSInitiatingOccupancyFraction            = -1              {product}
    uintx CMSIsTooFullPercentage                    = 98              {product}
   double CMSLargeCoalSurplusPercent                = 0.950000        {product}
   double CMSLargeSplitSurplusPercent               = 1.000000        {product}
     bool CMSLoopWarn                               = false           {product}
    uintx CMSMaxAbortablePrecleanLoops              = 0               {product}
     intx CMSMaxAbortablePrecleanTime               = 5000            {product}
    uintx CMSOldPLABMax                             = 1024            {product}
    uintx CMSOldPLABMin                             = 16              {product}
    uintx CMSOldPLABNumRefills                      = 4               {product}
    uintx CMSOldPLABReactivityFactor                = 2               {product}
     bool CMSOldPLABResizeQuicker                   = false           {product}
    uintx CMSOldPLABToleranceFactor                 = 4               {product}
     bool CMSPLABRecordAlways                       = true            {product}
    uintx CMSParPromoteBlocksToClaim                = 16              {product}
     bool CMSParallelInitialMarkEnabled             = true            {product}
     bool CMSParallelRemarkEnabled                  = true            {product}
     bool CMSParallelSurvivorRemarkEnabled          = true            {product}
    uintx CMSPrecleanDenominator                    = 3               {product}
    uintx CMSPrecleanIter                           = 3               {product}
    uintx CMSPrecleanNumerator                      = 2               {product}
     bool CMSPrecleanRefLists1                      = true            {product}
     bool CMSPrecleanRefLists2                      = false           {product}
     bool CMSPrecleanSurvivors1                     = false           {product}
     bool CMSPrecleanSurvivors2                     = true            {product}
    uintx CMSPrecleanThreshold                      = 1000            {product}
     bool CMSPrecleaningEnabled                     = true            {product}
     bool CMSPrintChunksInDump                      = false           {product}
     bool CMSPrintEdenSurvivorChunks                = false           {product}
     bool CMSPrintObjectsInDump                     = false           {product}
    uintx CMSRemarkVerifyVariant                    = 1               {product}
     bool CMSReplenishIntermediate                  = true            {product}
    uintx CMSRescanMultiple                         = 32              {product}
    uintx CMSSamplingGrain                          = 16384           {product}
     bool CMSScavengeBeforeRemark                   = false           {product}
    uintx CMSScheduleRemarkEdenPenetration          = 50              {product}
    uintx CMSScheduleRemarkEdenSizeThreshold        = 2097152         {product}
    uintx CMSScheduleRemarkSamplingRatio            = 5               {product}
   double CMSSmallCoalSurplusPercent                = 1.050000        {product}
   double CMSSmallSplitSurplusPercent               = 1.100000        {product}
     bool CMSSplitIndexedFreeListBlocks             = true            {product}
    uintx CMSTriggerRatio                           = 80              {product}
     intx CMSWaitDuration                           = 2000            {manageable}
    uintx CMSWorkQueueDrainThreshold                = 10              {product}
     bool CMSYield                                  = true            {product}
    uintx CMSYieldSleepCount                        = 0               {product}
    uintx CMSYoungGenPerWorker                      = 67108864        {pd product}
    uintx CMS_FLSPadding                            = 1               {product}
    uintx CMS_FLSWeight                             = 75              {product}
    uintx CMS_SweepPadding                          = 1               {product}
    uintx CMS_SweepTimerThresholdMillis             = 10              {product}
    uintx CMS_SweepWeight                           = 75              {product}
     bool CheckJNICalls                             = false           {product}
     bool ClassUnloading                            = true            {product}
     intx ClearFPUAtPark                            = 0               {product}
     bool ClipInlining                              = true            {product}
    uintx CodeCacheExpansionSize                    = 65536           {pd product}
    uintx CodeCacheMinimumFreeSpace                 = 512000          {product}
     bool CollectGen0First                          = false           {product}
     bool CompactFields                             = true            {product}
     intx CompilationPolicyChoice                   = 3               {product}
     intx CompilationRepeat                         = 0               {C1 product}
ccstrlist CompileCommand                            =                 {product}
    ccstr CompileCommandFile                        =                 {product}
ccstrlist CompileOnly                               =                 {product}
     intx CompileThreshold                          = 10000           {pd product}
     bool CompilerThreadHintNoPreempt               = true            {product}
     intx CompilerThreadPriority                    = -1              {product}
     intx CompilerThreadStackSize                   = 0               {pd product}
    uintx CompressedClassSpaceSize                  = 1073741824      {product}
    uintx ConcGCThreads                             = 0               {product}
     intx ConditionalMoveLimit                      = 3               {C2 pd product}
     intx ContendedPaddingWidth                     = 128             {product}
     bool ConvertSleepToYield                       = true            {pd product}
     bool ConvertYieldToSleep                       = false           {product}
     bool CreateMinidumpOnCrash                     = false           {product}
     bool CriticalJNINatives                        = true            {product}
     bool DTraceAllocProbes                         = false           {product}
     bool DTraceMethodProbes                        = false           {product}
     bool DTraceMonitorProbes                       = false           {product}
     bool Debugging                                 = false           {product}
    uintx DefaultMaxRAMFraction                     = 4               {product}
     intx DefaultThreadPriority                     = -1              {product}
     intx DeferPollingPageLoopCount                 = -1              {product}
     intx DeferThrSuspendLoopCount                  = 4000            {product}
     bool DeoptimizeRandom                          = false           {product}
     bool DisableAttachMechanism                    = false           {product}
     bool DisableExplicitGC                         = false           {product}
     bool DisplayVMOutputToStderr                   = false           {product}
     bool DisplayVMOutputToStdout                   = false           {product}
     bool DoEscapeAnalysis                          = true            {C2 product}
     bool DontCompileHugeMethods                    = true            {product}
     bool DontYieldALot                             = false           {pd product}
     bool DumpReplayDataOnError                     = true            {product}
     bool DumpSharedSpaces                          = false           {product}
     bool EagerXrunInit                             = false           {product}
     intx EliminateAllocationArraySizeLimit         = 64              {C2 product}
     bool EliminateAllocations                      = true            {C2 product}
     bool EliminateAutoBox                          = false           {C2 product}
     bool EliminateLocks                            = true            {C2 product}
     bool EliminateNestedLocks                      = true            {C2 product}
     intx EmitSync                                  = 0               {product}
     bool EnableContended                           = true            {product}
     bool EnableTracing                             = false           {product}
    uintx ErgoHeapSizeLimit                         = 0               {product}
    ccstr ErrorFile                                 =                 {product}
    ccstr ErrorReportServer                         =                 {product}
     bool EstimateArgEscape                         = true            {product}
     bool ExplicitGCInvokesConcurrent               = false           {product}
     bool ExplicitGCInvokesConcurrentAndUnloadsClasses  = false           {product}
     bool ExtendedDTraceProbes                      = false           {product}
     bool FLSAlwaysCoalesceLarge                    = false           {product}
    uintx FLSCoalescePolicy                         = 2               {product}
   double FLSLargestBlockCoalesceProximity          = 0.990000        {product}
     bool FailOverToOldVerifier                     = true            {product}
     bool FastTLABRefill                            = true            {product}
     intx FenceInstruction                          = 0               {ARCH product}
     intx FieldsAllocationStyle                     = 1               {product}
     bool FilterSpuriousWakeups                     = true            {product}
     bool ForceNUMA                                 = false           {product}
     bool ForceTimeHighResolution                   = false           {product}
     intx FreqInlineSize                            = 325             {pd product}
   double G1ConcMarkStepDurationMillis              = 10.000000       {product}
    uintx G1ConcRSHotCardLimit                      = 4               {product}
    uintx G1ConcRSLogCacheSize                      = 10              {product}
     intx G1ConcRefinementGreenZone                 = 0               {product}
     intx G1ConcRefinementRedZone                   = 0               {product}
     intx G1ConcRefinementServiceIntervalMillis     = 300             {product}
    uintx G1ConcRefinementThreads                   = 0               {product}
     intx G1ConcRefinementThresholdStep             = 0               {product}
     intx G1ConcRefinementYellowZone                = 0               {product}
    uintx G1ConfidencePercent                       = 50              {product}
    uintx G1HeapRegionSize                          = 0               {product}
    uintx G1HeapWastePercent                        = 10              {product}
    uintx G1MixedGCCountTarget                      = 8               {product}
     intx G1RSetRegionEntries                       = 0               {product}
    uintx G1RSetScanBlockSize                       = 64              {product}
     intx G1RSetSparseRegionEntries                 = 0               {product}
     intx G1RSetUpdatingPauseTimePercent            = 10              {product}
     intx G1RefProcDrainInterval                    = 10              {product}
    uintx G1ReservePercent                          = 10              {product}
    uintx G1SATBBufferEnqueueingThresholdPercent    = 60              {product}
     intx G1SATBBufferSize                          = 1024            {product}
     intx G1UpdateBufferSize                        = 256             {product}
     bool G1UseAdaptiveConcRefinement               = true            {product}
    uintx GCDrainStackTargetSize                    = 64              {product}
    uintx GCHeapFreeLimit                           = 2               {product}
    uintx GCLockerEdenExpansionPercent              = 5               {product}
     bool GCLockerInvokesConcurrent                 = false           {product}
    uintx GCLogFileSize                             = 0               {product}
    uintx GCPauseIntervalMillis                     = 0               {product}
    uintx GCTaskTimeStampEntries                    = 200             {product}
    uintx GCTimeLimit                               = 98              {product}
    uintx GCTimeRatio                               = 99              {product}
    uintx HeapBaseMinAddress                        = 2147483648      {pd product}
     bool HeapDumpAfterFullGC                       = false           {manageable}
     bool HeapDumpBeforeFullGC                      = false           {manageable}
     bool HeapDumpOnOutOfMemoryError                = false           {manageable}
    ccstr HeapDumpPath                              =                 {manageable}
    uintx HeapFirstMaximumCompactionCount           = 3               {product}
    uintx HeapMaximumCompactionInterval             = 20              {product}
    uintx HeapSizePerGCThread                       = 87241520        {product}
     bool IgnoreUnrecognizedVMOptions               = false           {product}
    uintx IncreaseFirstTierCompileThresholdAt       = 50              {product}
     bool IncrementalInline                         = true            {C2 product}
    uintx InitialBootClassLoaderMetaspaceSize       = 4194304         {product}
    uintx InitialCodeCacheSize                      = 2555904         {pd product}
    uintx InitialHeapSize                          := 10485760        {product}
    uintx InitialRAMFraction                        = 64              {product}
    uintx InitialSurvivorRatio                      = 8               {product}
    uintx InitialTenuringThreshold                  = 7               {product}
    uintx InitiatingHeapOccupancyPercent            = 45              {product}
     bool Inline                                    = true            {product}
     intx InlineSmallCode                           = 2000            {pd product}
     bool InlineSynchronizedMethods                 = true            {C1 product}
     bool InsertMemBarAfterArraycopy                = true            {C2 product}
     intx InteriorEntryAlignment                    = 16              {C2 pd product}
     intx InterpreterProfilePercentage              = 33              {product}
     bool JNIDetachReleasesMonitors                 = true            {product}
     bool JavaMonitorsInStackTrace                  = true            {product}
     intx JavaPriority10_To_OSPriority              = -1              {product}
     intx JavaPriority1_To_OSPriority               = -1              {product}
     intx JavaPriority2_To_OSPriority               = -1              {product}
     intx JavaPriority3_To_OSPriority               = -1              {product}
     intx JavaPriority4_To_OSPriority               = -1              {product}
     intx JavaPriority5_To_OSPriority               = -1              {product}
     intx JavaPriority6_To_OSPriority               = -1              {product}
     intx JavaPriority7_To_OSPriority               = -1              {product}
     intx JavaPriority8_To_OSPriority               = -1              {product}
     intx JavaPriority9_To_OSPriority               = -1              {product}
     bool LIRFillDelaySlots                         = false           {C1 pd product}
    uintx LargePageHeapSizeThreshold                = 134217728       {product}
    uintx LargePageSizeInBytes                      = 0               {product}
     bool LazyBootClassLoader                       = true            {product}
     intx LiveNodeCountInliningCutoff               = 20000           {C2 product}
     bool LogCommercialFeatures                     = false           {product}
     intx LoopMaxUnroll                             = 16              {C2 product}
     intx LoopOptsCount                             = 43              {C2 product}
     intx LoopUnrollLimit                           = 60              {C2 pd product}
     intx LoopUnrollMin                             = 4               {C2 product}
     bool LoopUnswitching                           = true            {C2 product}
     bool ManagementServer                          = false           {product}
    uintx MarkStackSize                             = 4194304         {product}
    uintx MarkStackSizeMax                          = 536870912       {product}
    uintx MarkSweepAlwaysCompactCount               = 4               {product}
    uintx MarkSweepDeadRatio                        = 1               {product}
     intx MaxBCEAEstimateLevel                      = 5               {product}
     intx MaxBCEAEstimateSize                       = 150             {product}
    uintx MaxDirectMemorySize                       = 0               {product}
     bool MaxFDLimit                                = true            {product}
    uintx MaxGCMinorPauseMillis                     = 18446744073709551615{product}
    uintx MaxGCPauseMillis                          = 18446744073709551615{product}
    uintx MaxHeapFreeRatio                          = 70              {product}
    uintx MaxHeapSize                              := 10485760        {product}
     intx MaxInlineLevel                            = 9               {product}
     intx MaxInlineSize                             = 35              {product}
     intx MaxJavaStackTraceDepth                    = 1024            {product}
     intx MaxJumpTableSize                          = 65000           {C2 product}
     intx MaxJumpTableSparseness                    = 5               {C2 product}
     intx MaxLabelRootDepth                         = 1100            {C2 product}
     intx MaxLoopPad                                = 11              {C2 product}
    uintx MaxMetaspaceExpansion                     = 5451776         {product}
    uintx MaxMetaspaceFreeRatio                     = 70              {product}
    uintx MaxMetaspaceSize                          = 18446744073709547520{product}
    uintx MaxNewSize                               := 3145728         {product}
     intx MaxNodeLimit                              = 75000           {C2 product}
 uint64_t MaxRAM                                    = 137438953472    {pd product}
    uintx MaxRAMFraction                            = 4               {product}
     intx MaxRecursiveInlineLevel                   = 1               {product}
    uintx MaxTenuringThreshold                      = 15              {product}
     intx MaxTrivialSize                            = 6               {product}
     intx MaxVectorSize                             = 16              {C2 product}
    uintx MetaspaceSize                             = 21807104        {pd product}
     bool MethodFlushing                            = true            {product}
    uintx MinHeapDeltaBytes                        := 524288          {product}
    uintx MinHeapFreeRatio                          = 40              {product}
     intx MinInliningThreshold                      = 250             {product}
     intx MinJumpTableSize                          = 10              {C2 pd product}
    uintx MinMetaspaceExpansion                     = 339968          {product}
    uintx MinMetaspaceFreeRatio                     = 40              {product}
    uintx MinRAMFraction                            = 2               {product}
    uintx MinSurvivorRatio                          = 3               {product}
    uintx MinTLABSize                               = 2048            {product}
     intx MonitorBound                              = 0               {product}
     bool MonitorInUseLists                         = false           {product}
     intx MultiArrayExpandLimit                     = 6               {C2 product}
     bool MustCallLoadClassInternal                 = false           {product}
    uintx NUMAChunkResizeWeight                     = 20              {product}
    uintx NUMAInterleaveGranularity                 = 2097152         {product}
    uintx NUMAPageScanRate                          = 256             {product}
    uintx NUMASpaceResizeRate                       = 1073741824      {product}
     bool NUMAStats                                 = false           {product}
    ccstr NativeMemoryTracking                      = off             {product}
     intx NativeMonitorFlags                        = 0               {product}
     intx NativeMonitorSpinLimit                    = 20              {product}
     intx NativeMonitorTimeout                      = -1              {product}
     bool NeedsDeoptSuspend                         = false           {pd product}
     bool NeverActAsServerClassMachine              = false           {pd product}
     bool NeverTenure                               = false           {product}
    uintx NewRatio                                  = 2               {product}
    uintx NewSize                                  := 3145728         {product}
    uintx NewSizeThreadIncrease                     = 5320            {pd product}
     intx NmethodSweepActivity                      = 10              {product}
     intx NmethodSweepCheckInterval                 = 5               {product}
     intx NmethodSweepFraction                      = 16              {product}
     intx NodeLimitFudgeFactor                      = 2000            {C2 product}
    uintx NumberOfGCLogFiles                        = 0               {product}
     intx NumberOfLoopInstrToAlign                  = 4               {C2 product}
     intx ObjectAlignmentInBytes                    = 8               {lp64_product}
    uintx OldPLABSize                               = 1024            {product}
    uintx OldPLABWeight                             = 50              {product}
    uintx OldSize                                  := 7340032         {product}
     bool OmitStackTraceInFastThrow                 = true            {product}
ccstrlist OnError                                   =                 {product}
ccstrlist OnOutOfMemoryError                        =                 {product}
     intx OnStackReplacePercentage                  = 140             {pd product}
     bool OptimizeFill                              = true            {C2 product}
     bool OptimizePtrCompare                        = true            {C2 product}
     bool OptimizeStringConcat                      = true            {C2 product}
     bool OptoBundling                              = false           {C2 pd product}
     intx OptoLoopAlignment                         = 16              {pd product}
     bool OptoScheduling                            = false           {C2 pd product}
    uintx PLABWeight                                = 75              {product}
     bool PSChunkLargeArrays                        = true            {product}
     intx ParGCArrayScanChunk                       = 50              {product}
    uintx ParGCDesiredObjsFromOverflowList          = 20              {product}
     bool ParGCTrimOverflow                         = true            {product}
     bool ParGCUseLocalOverflow                     = false           {product}
    uintx ParallelGCBufferWastePct                  = 10              {product}
    uintx ParallelGCThreads                         = 4               {product}
     bool ParallelGCVerbose                         = false           {product}
    uintx ParallelOldDeadWoodLimiterMean            = 50              {product}
    uintx ParallelOldDeadWoodLimiterStdDev          = 80              {product}
     bool ParallelRefProcBalancingEnabled           = true            {product}
     bool ParallelRefProcEnabled                    = false           {product}
     bool PartialPeelAtUnsignedTests                = true            {C2 product}
     bool PartialPeelLoop                           = true            {C2 product}
     intx PartialPeelNewPhiDelta                    = 0               {C2 product}
    uintx PausePadding                              = 1               {product}
     intx PerBytecodeRecompilationCutoff            = 200             {product}
     intx PerBytecodeTrapLimit                      = 4               {product}
     intx PerMethodRecompilationCutoff              = 400             {product}
     intx PerMethodTrapLimit                        = 100             {product}
     bool PerfAllowAtExitRegistration               = false           {product}
     bool PerfBypassFileSystemCheck                 = false           {product}
     intx PerfDataMemorySize                        = 32768           {product}
     intx PerfDataSamplingInterval                  = 50              {product}
    ccstr PerfDataSaveFile                          =                 {product}
     bool PerfDataSaveToFile                        = false           {product}
     bool PerfDisableSharedMem                      = false           {product}
     intx PerfMaxStringConstLength                  = 1024            {product}
     intx PreInflateSpin                            = 10              {pd product}
     bool PreferInterpreterNativeStubs              = false           {pd product}
     intx PrefetchCopyIntervalInBytes               = 576             {product}
     intx PrefetchFieldsAhead                       = 1               {product}
     intx PrefetchScanIntervalInBytes               = 576             {product}
     bool PreserveAllAnnotations                    = false           {product}
    uintx PretenureSizeThreshold                    = 0               {product}
     bool PrintAdaptiveSizePolicy                   = false           {product}
     bool PrintCMSInitiationStatistics              = false           {product}
     intx PrintCMSStatistics                        = 0               {product}
     bool PrintClassHistogram                       = false           {manageable}
     bool PrintClassHistogramAfterFullGC            = false           {manageable}
     bool PrintClassHistogramBeforeFullGC           = false           {manageable}
     bool PrintCodeCache                            = false           {product}
     bool PrintCodeCacheOnCompilation               = false           {product}
     bool PrintCommandLineFlags                     = false           {product}
     bool PrintCompilation                          = false           {product}
     bool PrintConcurrentLocks                      = false           {manageable}
     intx PrintFLSCensus                            = 0               {product}
     intx PrintFLSStatistics                        = 0               {product}
     bool PrintFlagsFinal                          := true            {product}
     bool PrintFlagsInitial                         = false           {product}
     bool PrintGC                                   = false           {manageable}
     bool PrintGCApplicationConcurrentTime          = false           {product}
     bool PrintGCApplicationStoppedTime             = false           {product}
     bool PrintGCCause                              = true            {product}
     bool PrintGCDateStamps                         = false           {manageable}
     bool PrintGCDetails                            = false           {manageable}
     bool PrintGCTaskTimeStamps                     = false           {product}
     bool PrintGCTimeStamps                         = false           {manageable}
     bool PrintHeapAtGC                             = false           {product rw}
     bool PrintHeapAtGCExtended                     = false           {product rw}
     bool PrintHeapAtSIGBREAK                       = true            {product}
     bool PrintJNIGCStalls                          = false           {product}
     bool PrintJNIResolving                         = false           {product}
     bool PrintOldPLAB                              = false           {product}
     bool PrintOopAddress                           = false           {product}
     bool PrintPLAB                                 = false           {product}
     bool PrintParallelOldGCPhaseTimes              = false           {product}
     bool PrintPromotionFailure                     = false           {product}
     bool PrintReferenceGC                          = false           {product}
     bool PrintSafepointStatistics                  = false           {product}
     intx PrintSafepointStatisticsCount             = 300             {product}
     intx PrintSafepointStatisticsTimeout           = -1              {product}
     bool PrintSharedSpaces                         = false           {product}
     bool PrintStringTableStatistics                = false           {product}
     bool PrintTLAB                                 = false           {product}
     bool PrintTenuringDistribution                 = false           {product}
     bool PrintTieredEvents                         = false           {product}
     bool PrintVMOptions                            = false           {product}
     bool PrintVMQWaitTime                          = false           {product}
     bool PrintWarnings                             = true            {product}
    uintx ProcessDistributionStride                 = 4               {product}
     bool ProfileInterpreter                        = true            {pd product}
     bool ProfileIntervals                          = false           {product}
     intx ProfileIntervalsTicks                     = 100             {product}
     intx ProfileMaturityPercentage                 = 20              {product}
     bool ProfileVM                                 = false           {product}
     bool ProfilerPrintByteCodeStatistics           = false           {product}
     bool ProfilerRecordPC                          = false           {product}
    uintx PromotedPadding                           = 3               {product}
    uintx QueuedAllocationWarningCount              = 0               {product}
     bool RangeCheckElimination                     = true            {product}
     intx ReadPrefetchInstr                         = 0               {ARCH product}
     bool ReassociateInvariants                     = true            {C2 product}
     bool ReduceBulkZeroing                         = true            {C2 product}
     bool ReduceFieldZeroing                        = true            {C2 product}
     bool ReduceInitialCardMarks                    = true            {C2 product}
     bool ReduceSignalUsage                         = false           {product}
     intx RefDiscoveryPolicy                        = 0               {product}
     bool ReflectionWrapResolutionErrors            = true            {product}
     bool RegisterFinalizersAtInit                  = true            {product}
     bool RelaxAccessControlCheck                   = false           {product}
    ccstr ReplayDataFile                            =                 {product}
     bool RequireSharedSpaces                       = false           {product}
    uintx ReservedCodeCacheSize                     = 251658240       {pd product}
     bool ResizeOldPLAB                             = true            {product}
     bool ResizePLAB                                = true            {product}
     bool ResizeTLAB                                = true            {pd product}
     bool RestoreMXCSROnJNICalls                    = false           {product}
     bool RestrictContended                         = true            {product}
     bool RewriteBytecodes                          = true            {pd product}
     bool RewriteFrequentPairs                      = true            {pd product}
     intx SafepointPollOffset                       = 256             {C1 pd product}
     intx SafepointSpinBeforeYield                  = 2000            {product}
     bool SafepointTimeout                          = false           {product}
     intx SafepointTimeoutDelay                     = 10000           {product}
     bool ScavengeBeforeFullGC                      = true            {product}
     intx SelfDestructTimer                         = 0               {product}
    uintx SharedBaseAddress                         = 34359738368     {product}
    uintx SharedMiscCodeSize                        = 122880          {product}
    uintx SharedMiscDataSize                        = 4194304         {product}
    uintx SharedReadOnlySize                        = 16777216        {product}
    uintx SharedReadWriteSize                       = 16777216        {product}
     bool ShowMessageBoxOnError                     = false           {product}
     intx SoftRefLRUPolicyMSPerMB                   = 1000            {product}
     bool SpecialEncodeISOArray                     = true            {C2 product}
     bool SplitIfBlocks                             = true            {C2 product}
     intx StackRedPages                             = 1               {pd product}
     intx StackShadowPages                          = 20              {pd product}
     bool StackTraceInThrowable                     = true            {product}
     intx StackYellowPages                          = 2               {pd product}
     bool StartAttachListener                       = false           {product}
     intx StarvationMonitorInterval                 = 200             {product}
     bool StressLdcRewrite                          = false           {product}
    uintx StringTableSize                           = 60013           {product}
     bool SuppressFatalErrorMessage                 = false           {product}
    uintx SurvivorPadding                           = 3               {product}
    uintx SurvivorRatio                             = 8               {product}
     intx SuspendRetryCount                         = 50              {product}
     intx SuspendRetryDelay                         = 5               {product}
     intx SyncFlags                                 = 0               {product}
    ccstr SyncKnobs                                 =                 {product}
     intx SyncVerbose                               = 0               {product}
    uintx TLABAllocationWeight                      = 35              {product}
    uintx TLABRefillWasteFraction                   = 64              {product}
    uintx TLABSize                                  = 0               {product}
     bool TLABStats                                 = true            {product}
    uintx TLABWasteIncrement                        = 4               {product}
    uintx TLABWasteTargetPercent                    = 1               {product}
    uintx TargetPLABWastePct                        = 10              {product}
    uintx TargetSurvivorRatio                       = 50              {product}
    uintx TenuredGenerationSizeIncrement            = 20              {product}
    uintx TenuredGenerationSizeSupplement           = 80              {product}
    uintx TenuredGenerationSizeSupplementDecay      = 2               {product}
     intx ThreadPriorityPolicy                      = 0               {product}
     bool ThreadPriorityVerbose                     = false           {product}
    uintx ThreadSafetyMargin                        = 52428800        {product}
     intx ThreadStackSize                           = 1024            {pd product}
    uintx ThresholdTolerance                        = 10              {product}
     intx Tier0BackedgeNotifyFreqLog                = 10              {product}
     intx Tier0InvokeNotifyFreqLog                  = 7               {product}
     intx Tier0ProfilingStartPercentage             = 200             {product}
     intx Tier23InlineeNotifyFreqLog                = 20              {product}
     intx Tier2BackEdgeThreshold                    = 0               {product}
     intx Tier2BackedgeNotifyFreqLog                = 14              {product}
     intx Tier2CompileThreshold                     = 0               {product}
     intx Tier2InvokeNotifyFreqLog                  = 11              {product}
     intx Tier3BackEdgeThreshold                    = 60000           {product}
     intx Tier3BackedgeNotifyFreqLog                = 13              {product}
     intx Tier3CompileThreshold                     = 2000            {product}
     intx Tier3DelayOff                             = 2               {product}
     intx Tier3DelayOn                              = 5               {product}
     intx Tier3InvocationThreshold                  = 200             {product}
     intx Tier3InvokeNotifyFreqLog                  = 10              {product}
     intx Tier3LoadFeedback                         = 5               {product}
     intx Tier3MinInvocationThreshold               = 100             {product}
     intx Tier4BackEdgeThreshold                    = 40000           {product}
     intx Tier4CompileThreshold                     = 15000           {product}
     intx Tier4InvocationThreshold                  = 5000            {product}
     intx Tier4LoadFeedback                         = 3               {product}
     intx Tier4MinInvocationThreshold               = 600             {product}
     bool TieredCompilation                         = true            {pd product}
     intx TieredCompileTaskTimeout                  = 50              {product}
     intx TieredRateUpdateMaxTime                   = 25              {product}
     intx TieredRateUpdateMinTime                   = 1               {product}
     intx TieredStopAtLevel                         = 4               {product}
     bool TimeLinearScan                            = false           {C1 product}
     bool TraceBiasedLocking                        = false           {product}
     bool TraceClassLoading                         = false           {product rw}
     bool TraceClassLoadingPreorder                 = false           {product}
     bool TraceClassResolution                      = false           {product}
     bool TraceClassUnloading                       = false           {product rw}
     bool TraceDynamicGCThreads                     = false           {product}
     bool TraceGen0Time                             = false           {product}
     bool TraceGen1Time                             = false           {product}
    ccstr TraceJVMTI                                =                 {product}
     bool TraceLoaderConstraints                    = false           {product rw}
     bool TraceMetadataHumongousAllocation          = false           {product}
     bool TraceMonitorInflation                     = false           {product}
     bool TraceParallelOldGCTasks                   = false           {product}
     intx TraceRedefineClasses                      = 0               {product}
     bool TraceSafepointCleanupTime                 = false           {product}
     bool TraceSuspendWaitFailures                  = false           {product}
     intx TrackedInitializationLimit                = 50              {C2 product}
     bool TransmitErrorReport                       = false           {product}
     intx TypeProfileArgsLimit                      = 2               {product}
    uintx TypeProfileLevel                          = 0               {pd product}
     intx TypeProfileMajorReceiverPercent           = 90              {C2 product}
     intx TypeProfileParmsLimit                     = 2               {product}
     intx TypeProfileWidth                          = 2               {product}
     intx UnguardOnExecutionViolation               = 0               {product}
     bool UnlinkSymbolsALot                         = false           {product}
     bool Use486InstrsOnly                          = false           {ARCH product}
     bool UseAES                                    = true            {product}
     bool UseAESIntrinsics                          = true            {product}
     intx UseAVX                                    = 1               {ARCH product}
     bool UseAdaptiveGCBoundary                     = false           {product}
     bool UseAdaptiveGenerationSizePolicyAtMajorCollection  = true            {product}
     bool UseAdaptiveGenerationSizePolicyAtMinorCollection  = true            {product}
     bool UseAdaptiveNUMAChunkSizing                = true            {product}
     bool UseAdaptiveSizeDecayMajorGCCost           = true            {product}
     bool UseAdaptiveSizePolicy                     = true            {product}
     bool UseAdaptiveSizePolicyFootprintGoal        = true            {product}
     bool UseAdaptiveSizePolicyWithSystemGC         = false           {product}
     bool UseAddressNop                             = true            {ARCH product}
     bool UseAltSigs                                = false           {product}
     bool UseAutoGCSelectPolicy                     = false           {product}
     bool UseBiasedLocking                          = true            {product}
     bool UseBimorphicInlining                      = true            {C2 product}
     bool UseBoundThreads                           = true            {product}
     bool UseBsdPosixThreadCPUClocks                = true            {product}
     bool UseCLMUL                                  = true            {ARCH product}
     bool UseCMSBestFit                             = true            {product}
     bool UseCMSCollectionPassing                   = true            {product}
     bool UseCMSCompactAtFullCollection             = true            {product}
     bool UseCMSInitiatingOccupancyOnly             = false           {product}
     bool UseCRC32Intrinsics                        = true            {product}
     bool UseCodeCacheFlushing                      = true            {product}
     bool UseCompiler                               = true            {product}
     bool UseCompilerSafepoints                     = true            {product}
     bool UseCompressedClassPointers               := true            {lp64_product}
     bool UseCompressedOops                        := true            {lp64_product}
     bool UseConcMarkSweepGC                        = false           {product}
     bool UseCondCardMark                           = false           {C2 product}
     bool UseCountLeadingZerosInstruction           = false           {ARCH product}
     bool UseCounterDecay                           = true            {product}
     bool UseDivMod                                 = true            {C2 product}
     bool UseDynamicNumberOfGCThreads               = false           {product}
     bool UseFPUForSpilling                         = false           {C2 product}
     bool UseFastAccessorMethods                    = false           {product}
     bool UseFastEmptyMethods                       = false           {product}
     bool UseFastJNIAccessors                       = true            {product}
     bool UseFastStosb                              = true            {ARCH product}
     bool UseG1GC                                   = false           {product}
     bool UseGCLogFileRotation                      = false           {product}
     bool UseGCOverheadLimit                        = true            {product}
     bool UseGCTaskAffinity                         = false           {product}
     bool UseHeavyMonitors                          = false           {product}
     bool UseHugeTLBFS                              = false           {product}
     bool UseInlineCaches                           = true            {product}
     bool UseInterpreter                            = true            {product}
     bool UseJumpTables                             = true            {C2 product}
     bool UseLWPSynchronization                     = true            {product}
     bool UseLargePages                             = false           {pd product}
     bool UseLargePagesInMetaspace                  = false           {product}
     bool UseLargePagesIndividualAllocation         = false           {pd product}
     bool UseLockedTracing                          = false           {product}
     bool UseLoopCounter                            = true            {product}
     bool UseLoopInvariantCodeMotion                = true            {C1 product}
     bool UseLoopPredicate                          = true            {C2 product}
     bool UseMaximumCompactionOnSystemGC            = true            {product}
     bool UseMembar                                 = true            {pd product}
     bool UseNUMA                                   = false           {product}
     bool UseNUMAInterleaving                       = false           {product}
     bool UseNewLongLShift                          = false           {ARCH product}
     bool UseOSErrorReporting                       = false           {pd product}
     bool UseOldInlining                            = true            {C2 product}
     bool UseOnStackReplacement                     = true            {pd product}
     bool UseOnlyInlinedBimorphic                   = true            {C2 product}
     bool UseOprofile                               = false           {product}
     bool UseOptoBiasInlining                       = true            {C2 product}
     bool UsePPCLWSYNC                              = true            {product}
     bool UsePSAdaptiveSurvivorSizePolicy           = true            {product}
     bool UseParNewGC                               = false           {product}
     bool UseParallelGC                            := true            {product}
     bool UseParallelOldGC                          = true            {product}
     bool UsePerfData                               = true            {product}
     bool UsePopCountInstruction                    = true            {product}
     bool UseRDPCForConstantTableBase               = false           {C2 product}
     bool UseSHM                                    = false           {product}
     intx UseSSE                                    = 4               {product}
     bool UseSSE42Intrinsics                        = true            {product}
     bool UseSerialGC                               = false           {product}
     bool UseSharedSpaces                           = false           {product}
     bool UseSignalChaining                         = true            {product}
     bool UseStoreImmI16                            = false           {ARCH product}
     bool UseSuperWord                              = true            {C2 product}
     bool UseTLAB                                   = true            {pd product}
     bool UseThreadPriorities                       = true            {pd product}
     bool UseTypeProfile                            = true            {product}
     bool UseUnalignedLoadStores                    = true            {ARCH product}
     bool UseVMInterruptibleIO                      = false           {product}
     bool UseXMMForArrayCopy                        = true            {product}
     bool UseXmmI2D                                 = false           {ARCH product}
     bool UseXmmI2F                                 = false           {ARCH product}
     bool UseXmmLoadAndClearUpper                   = true            {ARCH product}
     bool UseXmmRegToRegMoveAll                     = true            {ARCH product}
     bool VMThreadHintNoPreempt                     = false           {product}
     intx VMThreadPriority                          = -1              {product}
     intx VMThreadStackSize                         = 1024            {pd product}
     intx ValueMapInitialSize                       = 11              {C1 product}
     intx ValueMapMaxLoopSize                       = 8               {C1 product}
     intx ValueSearchLimit                          = 1000            {C2 product}
     bool VerifyMergedCPBytecodes                   = true            {product}
     intx WorkAroundNPTLTimedWaitHang               = 1               {product}
    uintx YoungGenerationSizeIncrement              = 20              {product}
    uintx YoungGenerationSizeSupplement             = 80              {product}
    uintx YoungGenerationSizeSupplementDecay        = 8               {product}
    uintx YoungPLABSize                             = 4096            {product}
     bool ZeroTLAB                                  = false           {product}
     intx hashCode                                  = 5               {product}
```
即时编译参数
* `CompileThreshold`:触发即时编译的阈值
* `OnStackReplacePercentage`:OSR比率,它是OSR即时编译阈值计算公司的一个参数,用于代替BackEdgeThreshold参数控制回边计数器的实际溢出阈值
* `ReservedCodeCacheSize`:即时编译器编译的代码缓存使得最大值

类型加载参数
* `UseSplitVerifier`:使用依赖StackMapTable信息的类型检查代替数据流分析,以加快字节码校验速度
* `FailOverToOldVerier`:当类型校验失败时,是否允许回到老的类型推到校验方式进行校验,如果开启则允许
* `RelaxAccessControlCheck`:在校验阶段放松对类型访问性的限制

多线程相关参数
* `UseSpinning`:开启自旋锁以免线程频繁的挂起和唤醒
* `PreBlockSpin`:使用自旋锁时默认的自旋次数
* `UseThreadPriorities`:使用本地线程优先级
* `UseBiaseLocking`:是否使用偏向锁,如果开启则使用
* `UseFastAccessorMethods`:当频繁反射执行某个方法时,生成字节码来加快反射的执行速度

性能参数
* `AggressiveOpts`:使用激进的优化特征,这些特征一般是具备正面和负面双重影响的,需要根据具体应用特点分析才能判定是否对性能有好处
* `UseLargePages`:如果可能,使用大内存分页,这项特性需要操作系统的支持
* `LargePageSizeInBytes`:使用指定大小的内存分页,这项特性需要操作系统的支持
* `StringCache`:是否使用字符串缓存,开启则使用

*给远程服务器加debug
```
-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$debug_port
```
* suspend:这个参数是用来当JVM启动之后等待debug客户端连接,如果没有debug客户端连接,那么虚拟机就会一直等待，造成假死的现象
* `-XX:+UseVMInterruptibleIO`:线程中断前或是EINTR在OS_INTRPT中对于I/O操作的结果
* `-XX:+FailOverToOldVerifier`:当新的类型检测器失败时切换到旧的认证器
* `-XX:-AllowUserSignalHandlers`:允许为java进程安装信号处理器（限于Linux和Solaris,默认关闭）
* `-XX:AllocatePrefetchStyle=1`:预取指令的产生代码风格：0-没有预取指令,1-每一次分配内存就执行预取指令,2-当执行预取代码指令时,用TLAB分配水印指针指向门
* `-XX:AllocatePrefetchLines=1`:在使用JIT生成的预读取指令分配对象后读取的缓存行数.如果上次分配的对象是一个实例则默认值是1,如果是一个数组则是3
* `-XX:+OptimizeStringConcat`:对字符串拼接进行优化
* `-XX:+UseCompressedStrings`:如果可以表示为纯ASCII的话,则用byte[]代替字符串.
* `-XX:+UseBiasedLocking`:使用偏锁.
* `-XX:LoopUnrollLimit=n`:代表节点数目小于给定值时打开循环体.
* `-XX:+PerfDataSaveToFile`:Jvm退出时保存jvmstat的二进制数据.
* `-XX:+AlwaysPreTouch`:当JVM初始化时预先对Java堆进行预先摸底(堆中每个页归零处理).
* `-XX:InlineSmallCode=n`:当编译的代码小于指定的值时,内联编译的代码.
* `-XX:InitialTenuringThreshold=7`:设置初始的对象在新生代中最大存活次数.
* `-XX:+UseCompressedOops`:使用compressedpointers.这个参数默认在64bit的环境下默认启动,但是如果JVM的内存达到32G后,这个参数就会默认为不启动,因为32G内存后,压缩就没有多大必要了,要管理那么大的内存指针也需要很大的宽度了
* `-XX:AllocatePrefetchDistance=n`:为对象分配设置预取距离.
* `-XX:MaxInlineSize=35`:内联函数最大的字节码大小.
* `-XX:-TraceClassResolution`:追踪常量池resolutions.
* `-XX:FreqInlineSize=n`:经常执行方法内联的最大字节大小
* `-XX:-TraceLoaderConstraints`:跟踪加载器的限制记录.
