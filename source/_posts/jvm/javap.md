category: JVM
date: 2014-10-10
title: javap
---
## 用法
javap是JDK自带的反汇编器，可以查看java编译器为我们生成的字节码
```bash
D:\>javap
用法: javap <options> <classes>
其中, 可能的选项包括:
  -version                 版本信息
  -v  -verbose             输出附加信息
  -l                       输出行号和本地变量表
  -public                  仅显示公共类和成员
  -protected               显示受保护的/公共类和成员
  -package                 显示程序包/受保护的/公共类和成员 (默认)
  -p  -private             显示所有类和成员
  -c                       对代码进行反汇编
  -s                       输出内部类型签名
  -sysinfo                 显示正在处理的类的系统信息 (路径, 大小, 日期, MD5 散列)
  -constants               显示最终常量
  -classpath <path>        指定查找用户类文件的位置
  -cp <path>               指定查找用户类文件的位置
  -bootclasspath <path>    覆盖引导类文件的位置
```
我们看一段源码
```java
public class Test {
    public static void main(String[] args) {
        System.out.println("Hello Javap");
    }
}
```
我们不使用任何的参数, javap一下这个class文件
```bash
$ javap Test.class
Compiled from "Test.java"
public class Test {
  public Test();
  public static void main(java.lang.String[]);
}
```

## -v
输出附加信息
```bash
$ javap -v Test.class
Classfile /Users/wangming/idea/untitled/target/classes/Test.class
  Last modified 2016-6-18; size 515 bytes
  MD5 checksum 475614e4cb4c152a5cf2be8929786a01
  Compiled from "Test.java"
public class Test
  SourceFile: "Test.java"
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #6.#20         //  java/lang/Object."<init>":()V
   #2 = Fieldref           #21.#22        //  java/lang/System.out:Ljava/io/PrintStream;
   #3 = String             #23            //  Hello Javap
   #4 = Methodref          #24.#25        //  java/io/PrintStream.println:(Ljava/lang/String;)V
   #5 = Class              #26            //  Test
   #6 = Class              #27            //  java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               LocalVariableTable
  #12 = Utf8               this
  #13 = Utf8               LTest;
  #14 = Utf8               main
  #15 = Utf8               ([Ljava/lang/String;)V
  #16 = Utf8               args
  #17 = Utf8               [Ljava/lang/String;
  #18 = Utf8               SourceFile
  #19 = Utf8               Test.java
  #20 = NameAndType        #7:#8          //  "<init>":()V
  #21 = Class              #28            //  java/lang/System
  #22 = NameAndType        #29:#30        //  out:Ljava/io/PrintStream;
  #23 = Utf8               Hello Javap
  #24 = Class              #31            //  java/io/PrintStream
  #25 = NameAndType        #32:#33        //  println:(Ljava/lang/String;)V
  #26 = Utf8               Test
  #27 = Utf8               java/lang/Object
  #28 = Utf8               java/lang/System
  #29 = Utf8               out
  #30 = Utf8               Ljava/io/PrintStream;
  #31 = Utf8               java/io/PrintStream
  #32 = Utf8               println
  #33 = Utf8               (Ljava/lang/String;)V
{
  public Test();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 4: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   LTest;

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #3                  // String Hello Javap
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 6: 0
        line 7: 8
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  args   [Ljava/lang/String;
}
```

## -l
输出行号和本地变量表
```bash
$ javap -l Test.class
Compiled from "Test.java"
public class Test {
  public Test();
    LineNumberTable:
      line 4: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0       5     0  this   LTest;

  public static void main(java.lang.String[]);
    LineNumberTable:
      line 6: 0
      line 7: 8
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0       9     0  args   [Ljava/lang/String;
}
```

## -public                  
仅显示公共类和成员
```bash
$ javap -public Test.class
Compiled from "Test.java"
public class Test {
  public Test();
  public static void main(java.lang.String[]);
}
```

## -p             
显示所有类和成员
```bash
$ javap -p Test.class
Compiled from "Test.java"
public class Test {
  public Test();
  public static void main(java.lang.String[]);
}
```

## -c                       
对代码进行反汇编
```bash
$ javap -c Test.class
Compiled from "Test.java"
public class Test {
  public Test();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
       3: ldc           #3                  // String Hello Javap
       5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       8: return
}
```

## -s                       
输出内部类型签名
```bash
$ javap -s Test.class
Compiled from "Test.java"
public class Test {
  public Test();
    descriptor: ()V

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
}
```

## -sysinfo                 
显示正在处理的类的系统信息 (路径, 大小, 日期, MD5 散列)
```bash
$ javap -sysinfo Test.class
Classfile /Users/wangming/idea/untitled/target/classes/Test.class
  Last modified 2016-6-18; size 515 bytes
  MD5 checksum 475614e4cb4c152a5cf2be8929786a01
  Compiled from "Test.java"
public class Test {
  public Test();
  public static void main(java.lang.String[]);
}
```

## -constants               
显示最终常量
```bash
$ javap -constants Test.class
Compiled from "Test.java"
public class Test {
  public Test();
  public static void main(java.lang.String[]);
}
```
