title: MemoryMXBean
---
## MemoryMXBean
我们通过`MemoryMXBean mbeans = ManagementFactory.getMemoryMXBean();`获得JVM 内存系统的管理接口.
下面是对`MemoryMXBean`的一个介绍：

`MemoryUsage`对象是JVM内存使用情况的一个快照. 总的来说,`MemoryUsage`实例通常是由那些获得JVM的内存池或者JVM堆/非堆内存使用情况的方法创建的.

`MemoryUsage`内部持有4个值：
* `init` 表示的是当JVM实例启动时从操作系统内分配得到的内存初始值. 随着JVM的运行它可能会从操作系统中分配到更多的内存，也可能会将多余的内存还给操作系统. 这个值也可能是未定义的.
* `used`  表示当前使用了多少JVM内存(单位`byte`).
* `committed` 这个值表示当前虚拟机运行需要的内存大小. 这个值也是随着虚拟机运行而变化的. `committed`可能会小于`init`的值，但是却总是大于等于`used`的值。
* `max`  represents the maximum amount of memory (in bytes) that can be used for memory management. Its value may be undefined. The maximum amount of memory may change over time if defined. The amount of used and committed memory will always be less than or equal to max if max is defined. A memory allocation may fail if it attempts to increase the used memory such that used > committed even if used <= max would still be true (for example, when the system is low on virtual memory).  
该值表示的内存管理能使用的最大的内存值.该值也可能是未定义的. 当`max`被定义过之后,`used`和`committed`的值总是会小于`max`值的.

下面的图展示了内存池的一个示例
```
+----------------------------------------------+
+////////////////           |                  +
+////////////////           |                  +
+----------------------------------------------+

|--------|
   init
|---------------|
       used
|---------------------------|
          committed
|----------------------------------------------|
                    max
```

MXBean Mapping
MemoryUsage is mapped to a CompositeData with attributes as specified in the from method.Since:1.5Author:Mandy Chung