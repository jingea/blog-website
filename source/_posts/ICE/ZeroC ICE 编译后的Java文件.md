category: ICE
date: 2016-03-30
title: ZeroC ICE 编译后的java文件
---
我们可以将hello.ice文件
```java
#pragma once

// module相当于package
module Demo
{

// 定义了一个Hello的服务
interface Hello
{
    idempotent void sayHello(int delay);
    void shutdown();
};

};
```
通过下面的命令将其编译成我们需要的java文件
```java
slice2java -I. Hello.ice
```
编译后的文件如下

下面的是服务端要用到的文件
* Hello.java
* _HelloOperations.java
* _HelloOperationsNC.java
* _HelloDel.java
* _HelloDelD.java
* _HelloDelM.java
* _HelloDisp.java

下面是客户端需要使用到的文件
* HelloHolder.java
* HelloPrx.java
* HelloPrxHelper.java
* HelloPrxHolder.java

* Callback_Hello_sayHello.java
* Callback_Hello_shutdown.java













