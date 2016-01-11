category: jvm
tag: asm, jvm工具
date: 2014-11-28
title: ASM 初探
---
ASM通过`ClassVisitor`来生成和转换class字节码. `ClassVisitor`中的每个方法都对应着class数据结构, 你可以通过每个方法名轻松的判断出这个方法对应的是哪个数据结构. 

`ClassVisitor`内的方法调用顺序如下:
