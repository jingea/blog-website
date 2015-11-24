category: Java SE
date: 2015-11-24
title: Instrumentation
---

> 要知道一个对象所使用的内存量,需要将所有实例变量使用的内存和对象本身的开销(一般是16字节)相加.这些开销包括一个指向对象的类的引用,垃圾收集信息和同步信息.另外一般内存的使用会被填充为8字节的倍数.

使用 Instrumentation，开发者可以构建一个独立于应用程序的代理程序（Agent），用来监测和协助运行在 JVM 上的程序，甚至能够替换和修改某些类的定义。
Instrumentation提供了这样的功能：
* 启动后的 instrument
* 本地代码（native code）instrument
* 动态改变 classpath 