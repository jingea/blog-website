category: Java SE
date: 2015-11-
title: JAVA 小常识
---

## 控制台乱码
在windows系统里,我的cmd控制台的代码页是`65001(UTF)`,但是当我在指向java命令时却会发生乱码现象,只需要指向`chcp 936`这个命令,改变一下代码页就好了

## java命令
`java  -jar ./tools-1.0-SNAPSHOT.jar` 从某个jar运行, 如果内部的mainfest文件已经指定了MainClass,则后边就不用再指定MainClass了

`java  -jar . App` 从当前的classpath中寻找jar,然后找到jar包中的App开始运行