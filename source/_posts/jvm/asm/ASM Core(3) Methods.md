category: jvm
tags: 
	- asm
	- jvm工具
date: 2016-01-14
title: ASM Core(3) Methods
---
[asm4-guide](http://download.forge.objectweb.org/asm/asm4-guide.pdf)学习心得
 
本文主要是讲述如何通过ASM API来生成和转换编译好的方法.

## 执行模型
在讲解字节码结构之前我们要首先讲解一下JVM的执行模型. 

java代码都是在线程中执行. 每一个线程都有它自己执行栈, 每个执行栈都是由N个栈帧组成. 每个栈帧都代表着一个方法调用, 每当我们调用一个方法的时候, 就会向当前执行栈(活动线程)中push一个栈帧. 当方法结束(return或者抛出异常)时就会将当前栈帧出栈. 然后继续调用下一个方法.

每一个栈帧都有俩部分组成
*  local variables 本地变量
*  operand stack  操作数栈

我们通过索引来访问local variables. operand stack存储的是操作数, 正如其名, 它也是一个栈结构, 因此我们通过Last In First Out对其进行访问. 

local variables和 operand stack的大小取决于方法的大小. 这些大小是在编译期进行计算, 而且也是进行单独存储的. 