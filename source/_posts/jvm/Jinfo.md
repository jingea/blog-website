category: JVM
date: 2014-10-04
title: Jinfo
---
jinfo的作用是实时查看和调整虚拟机的各项参数.
```java
jinfo [ option ] pid
```
option可以为如下值
* `-flag <name>`         to print the value of the named VM flag
* `-flag [+|-]<name>`    to enable or disable the named VM flag
* `-flag <name>=<value>` to set the named VM flag to the given value
* `-flags`               to print VM flags
* `-sysprops`            to print Java system properties
* `<no option>`          to print both of the above
```bash
test jinfo 2028
Attaching to process ID 2028, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.5-b02
Java System Properties:

java.runtime.name = Java(TM) SE Runtime Environment
java.vm.version = 25.5-b02
sun.boot.library.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/lib
gopherProxySet = false
java.vendor.url = http://java.oracle.com/
java.vm.vendor = Oracle Corporation
path.separator = :
file.encoding.pkg = sun.io
java.vm.name = Java HotSpot(TM) 64-Bit Server VM
idea.launcher.port = 7532
sun.os.patch.level = unknown
sun.java.launcher = SUN_STANDARD
user.country = CN
java.vm.specification.name = Java Virtual Machine Specification
PID = 2028
java.runtime.version = 1.8.0_05-b13
java.awt.graphicsenv = sun.awt.CGraphicsEnvironment
os.arch = x86_64
java.endorsed.dirs = /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre/lib/endorsed
org.jboss.logging.provider = slf4j
line.separator =

java.io.tmpdir = /var/folders/54/dy3_8pkj1ld2_q6r8rvvr1n80000gn/T/
java.vm.specification.vendor = Oracle Corporation
os.name = Mac OS X
sun.jnu.encoding = UTF-8
spring.beaninfo.ignore = true
java.specification.name = Java Platform API Specification
java.class.version = 52.0
sun.management.compiler = HotSpot 64-Bit Tiered Compilers
os.version = 10.10.5
user.timezone = Asia/Harbin
catalina.useNaming = false
java.awt.printerjob = sun.lwawt.macosx.CPrinterJob
file.encoding = UTF-8
java.specification.version = 1.8
catalina.home = /private/var/folders/54/dy3_8pkj1ld2_q6r8rvvr1n80000gn/T/tomcat.3734431332202362357.8080
java.vm.specification.version = 1.8
sun.arch.data.model = 64
sun.java.command = com.intellij.rt.execution.application.AppMain HTTPServer
java.home = /Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home/jre
user.language = zh
java.specification.vendor = Oracle Corporation
awt.toolkit = sun.lwawt.macosx.LWCToolkit
java.vm.info = mixed mode
java.version = 1.8.0_05
java.awt.headless = true
java.vendor = Oracle Corporation
catalina.base = /private/var/folders/54/dy3_8pkj1ld2_q6r8rvvr1n80000gn/T/tomcat.3734431332202362357.8080
file.separator = /
java.vendor.url.bug = http://bugreport.sun.com/bugreport/
sun.io.unicode.encoding = UnicodeBig
sun.cpu.endian = little
sun.cpu.isalist =

VM Flags:
Non-default VM flags: -XX:InitialHeapSize=100663296 -XX:MaxHeapSize=1610612736 -XX:MaxNewSize=536870912 -XX:MinHeapDeltaBytes=524288 -XX:NewSize=1572864 -XX:OldSize=99090432 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
```
