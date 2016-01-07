category: JavaSE
tag: JAVA7
date: 2015-11-19
title: JAVA 小常识
---

## 控制台乱码
在windows系统里,我的cmd控制台的代码页是`65001(UTF)`,但是当我在指向java命令时却会发生乱码现象,只需要指向`chcp 936`这个命令,改变一下代码页就好了

## java命令
`java  -jar ./tools-1.0-SNAPSHOT.jar` 从某个jar运行, mainfest文件必须指定MainClass属性,如果不指定的话,在运行`java`命令的时候就会产生 xxx.jar中没有主清单属性

`java  -jar ./ App` 从指定的classpath下所有的jar中,寻找App主类运行