category: jvm
tags: 
	- asm
	- jvm工具
date: 2016-01-11
title: ASM 初探
---
[asm4-guide](http://download.forge.objectweb.org/asm/asm4-guide.pdf)学习心得
 ASM是一种小巧轻便的 Java 字节码操控框架，它能方便地生成和改造 Java 代码

ASM通过`ClassVisitor`来生成和转换class字节码. `ClassVisitor`中的每个方法都对应着class数据结构, 你可以通过每个方法名轻松的判断出这个方法对应的是哪个数据结构. 

`ClassVisitor`内的方法调用顺序如下:
1. visit 
2. visitSource? 
3. visitOuterClass? 
4. ( visitAnnotation | visitAttribute )*
5. ( visitInnerClass | visitField | visitMethod )*
6. visitEnd
接下来我们分析一下以上的调用过程
* 首先调用`visit`方法(有且仅有调用一次)
* 接下来调用`visitSource`函数(最多调用一次)
* 然后调用`visitOuterClass`函数(最多调用一次)
* 接下来调用`visitAnnotation`和`visitAttribute`函数, 这俩个函数的调用可调用任意次且不分前后顺序
* 然后调用`visitInnerClass`,`visitField`和`visitMethod`函数, 同样对这三个函数的调用不限制次数以及不分前后顺序
* 最后调用`visitEnd`函数(有且仅有调用一次),调用这个函数用于结束整个过程.


ASM通过基于`ClassVisitor`的三个API来生成和转换class字节码
* `ClassReader`: 用于解析一个给定的class二进制字节数组, 然后按照上文介绍的顺序依次调用`accept()`的`ClassVisitor`参数的方法.
* `ClassWriter` : 一个`ClassVisitor`的子类, 用于直接生成二进制的字节码. 
* `ClassVisitor` : 代理了全部的字节码相关的方法调用. 它接收另一个`ClassVisitor`对象形成责任链模式调用.

我们通过`ClassReader`来解析一个二进制的class结构数据, 然后`ClassReader`按照一定的顺序调用`ClassVisitor` 来改变class结构数据, 最后通过`ClassVisitor`生成新的class二进制数据.