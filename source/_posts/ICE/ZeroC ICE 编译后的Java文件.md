category: ICE
date: 2016-03-30
title: ZeroC ICE 编译后的Java文件
---
然后我们在这个目录里执行下面这个命令, 对Hello.ice文件进行编译
```java
slice2java -I. Hello.ice
```
然后生成一个Demo的文件夹, 里面的文件有
* Callback_Hello_sayHello.java
* Callback_Hello_shutdown.java
* Hello.java
* HelloHolder.java
* HelloPrx.java
* HelloPrxHelper.java
* HelloPrxHolder.java
* _HelloDel.java
* _HelloDelD.java
* _HelloDelM.java
* _HelloDisp.java
* _HelloOperations.java
* _HelloOperationsNC.java

