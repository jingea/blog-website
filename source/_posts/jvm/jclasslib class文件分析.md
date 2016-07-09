category: JVM
date: 2016-07-06
title: jclasslib bytecode viewer 对 class文件分析
---
jclasslib bytecode viewer 是一个java编译后字节码查看的一个工具.

我写了一段源码
```java
public class JClassLibObject {

    private static String name;
    private String singleName;

    public static int init() {
        int i = 0;
        i ++;
        return i;
    }

    public void print() {
        int i = init();
        System.out.println(i);
    }
}
```
看一下在jclasslib bytecode viewer中的样子
![](https://raw.githubusercontent.com/ming15/blog-website/images/jvm/jclasslib_constant_pool.jpg)
![](https://raw.githubusercontent.com/ming15/blog-website/images/jvm/jclasslib_fields_methods.jpg)

[github地址](https://github.com/ingokegel/jclasslib)
[下载地址](https://sourceforge.net/projects/jclasslib/)
