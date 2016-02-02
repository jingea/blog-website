category: Java
tag: JavaSE
date: 2016-02-02
title: volatile 使用
---
当一个变量被`volatile`修饰后,它将具备俩种特性
* 线程可见性: 某个线程修改了被`volatile`修饰的变量后,其他线程可以里面看见这个最新的值.
* 禁止指令重排序优化

> volatile最适用的场景是一个线程写,多个线程读的场景. 如果有多个线程同时写的话还是需要锁或者并发容器等等进行保护

