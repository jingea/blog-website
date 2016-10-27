category: Java
date: 2016-09-26
title: Java 字符串优化
---
今天review同事写的一段代码, 在枚举中动态生成字符串key的时候, 使用字符串常量进行拼接, 虽然感觉这么写会有一定的GC性能消耗, 但是具体原因有点忘记了, 今天就重新写段测试代码,进行测试一下.

当使用字符串(`String`)的时候, Java编译器会给我们自动的做一些优化编译的工作. 下面我们分别用三段代码, 从GC和编译俩个角度看看Java是如何给我们优化的.

首先呢,我们使用`-XX:+PrintHeapAtGC  -Xmx10M -Xms10M`这个参数分别运行以下三段代码

首先看一下`TestCombine1` 
```java
public class TestCombine1 {

	private static final String str1 = new String("123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789");
	public static void main(String[] args) {
		for (int i = 0; i < 1000000; i++) {
			String string1 = "string" + str1;
		}
	}
}
```
运行结果为
 
```bash
{Heap before GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 2048K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 100% used [0x00000000ffd00000,0x00000000fff00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 7168K, used 0K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 0% used [0x00000000ff600000,0x00000000ff600000,0x00000000ffd00000)
 Metaspace       used 2754K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 296K, capacity 384K, committed 384K, reserved 1048576K
Heap after GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 488K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 0% used [0x00000000ffd00000,0x00000000ffd00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff00000,0x00000000fff7a020,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 7168K, used 530K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 7% used [0x00000000ff600000,0x00000000ff684ac0,0x00000000ffd00000)
 Metaspace       used 2754K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 296K, capacity 384K, committed 384K, reserved 1048576K
}
{Heap before GC invocations=2 (full 0):
 PSYoungGen      total 2560K, used 2536K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 100% used [0x00000000ffd00000,0x00000000fff00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff00000,0x00000000fff7a020,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 7168K, used 530K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 7% used [0x00000000ff600000,0x00000000ff684ac0,0x00000000ffd00000)
 Metaspace       used 2969K, capacity 4490K, committed 4864K, reserved 1056768K
  class space    used 319K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=2 (full 0):
 PSYoungGen      total 2560K, used 504K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 0% used [0x00000000ffd00000,0x00000000ffd00000,0x00000000fff00000)
  from space 512K, 98% used [0x00000000fff80000,0x00000000ffffe010,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 7168K, used 581K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 8% used [0x00000000ff600000,0x00000000ff691670,0x00000000ffd00000)
 Metaspace       used 2969K, capacity 4490K, committed 4864K, reserved 1056768K
  class space    used 319K, capacity 386K, committed 512K, reserved 1048576K
}
...
{Heap before GC invocations=38 (full 0):
 PSYoungGen      total 2560K, used 2080K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 100% used [0x00000000ffd00000,0x00000000fff00000,0x00000000fff00000)
  from space 512K, 6% used [0x00000000fff00000,0x00000000fff08000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 7168K, used 1212K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 16% used [0x00000000ff600000,0x00000000ff72f050,0x00000000ffd00000)
 Metaspace       used 3236K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 351K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=38 (full 0):
 PSYoungGen      total 2560K, used 32K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 0% used [0x00000000ffd00000,0x00000000ffd00000,0x00000000fff00000)
  from space 512K, 6% used [0x00000000fff80000,0x00000000fff88000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 7168K, used 1212K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 16% used [0x00000000ff600000,0x00000000ff72f050,0x00000000ffd00000)
 Metaspace       used 3236K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 351K, capacity 386K, committed 512K, reserved 1048576K
}
```
我们看到一共GC了38次.


接下来看一下`TestCombine2`
```java
public class TestCombine2 {

	public static void main(String[] args) {
		for (int i = 0; i < 100000; i++) {
			String string3 = "string" + getFieldConstant();
		}
	}

	public static String getFieldConstant() {
		return "123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789";
	}
}
```
运行结果
```bash
{Heap before GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 2048K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 100% used [0x00000000ffd00000,0x00000000fff00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 7168K, used 0K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 0% used [0x00000000ff600000,0x00000000ff600000,0x00000000ffd00000)
 Metaspace       used 2752K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 296K, capacity 384K, committed 384K, reserved 1048576K
Heap after GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 504K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 0% used [0x00000000ffd00000,0x00000000ffd00000,0x00000000fff00000)
  from space 512K, 98% used [0x00000000fff00000,0x00000000fff7e010,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 7168K, used 489K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 6% used [0x00000000ff600000,0x00000000ff67a598,0x00000000ffd00000)
 Metaspace       used 2752K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 296K, capacity 384K, committed 384K, reserved 1048576K
}
...
{Heap before GC invocations=38 (full 0):
 PSYoungGen      total 2560K, used 2080K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 100% used [0x00000000ffd00000,0x00000000fff00000,0x00000000fff00000)
  from space 512K, 6% used [0x00000000fff00000,0x00000000fff08000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 7168K, used 1317K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 18% used [0x00000000ff600000,0x00000000ff749738,0x00000000ffd00000)
 Metaspace       used 3234K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 351K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=38 (full 0):
 PSYoungGen      total 2560K, used 32K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 0% used [0x00000000ffd00000,0x00000000ffd00000,0x00000000fff00000)
  from space 512K, 6% used [0x00000000fff80000,0x00000000fff88000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 7168K, used 1317K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 18% used [0x00000000ff600000,0x00000000ff749738,0x00000000ffd00000)
 Metaspace       used 3234K, capacity 4494K, committed 4864K, reserved 1056768K
  class space    used 351K, capacity 386K, committed 512K, reserved 1048576K
}
```
也GC了38次


接下来看一下`TestCombine3`
```java
public class TestCombine3 {

	private static final String str2 = "123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789";
	public static void main(String[] args) {
		for (int i = 0; i < 1000000; i++) {
			String string2 = "string" + str2;
		}
	}
}
```
运行结果
```bash
{Heap before GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 2048K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 100% used [0x00000000ffd00000,0x00000000fff00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 7168K, used 0K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 0% used [0x00000000ff600000,0x00000000ff600000,0x00000000ffd00000)
 Metaspace       used 2753K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 296K, capacity 384K, committed 384K, reserved 1048576K
Heap after GC invocations=1 (full 0):
 PSYoungGen      total 2560K, used 504K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 0% used [0x00000000ffd00000,0x00000000ffd00000,0x00000000fff00000)
  from space 512K, 98% used [0x00000000fff00000,0x00000000fff7e030,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 7168K, used 435K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 6% used [0x00000000ff600000,0x00000000ff66ccc0,0x00000000ffd00000)
 Metaspace       used 2753K, capacity 4480K, committed 4480K, reserved 1056768K
  class space    used 296K, capacity 384K, committed 384K, reserved 1048576K
}
{Heap before GC invocations=2 (full 0):
 PSYoungGen      total 2560K, used 2552K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 100% used [0x00000000ffd00000,0x00000000fff00000,0x00000000fff00000)
  from space 512K, 98% used [0x00000000fff00000,0x00000000fff7e030,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 7168K, used 435K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 6% used [0x00000000ff600000,0x00000000ff66ccc0,0x00000000ffd00000)
 Metaspace       used 2970K, capacity 4490K, committed 4864K, reserved 1056768K
  class space    used 319K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=2 (full 0):
 PSYoungGen      total 2560K, used 488K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
  eden space 2048K, 0% used [0x00000000ffd00000,0x00000000ffd00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff80000,0x00000000ffffa020,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 7168K, used 570K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 7% used [0x00000000ff600000,0x00000000ff68e880,0x00000000ffd00000)
 Metaspace       used 2970K, capacity 4490K, committed 4864K, reserved 1056768K
  class space    used 319K, capacity 386K, committed 512K, reserved 1048576K
}
```
一共就GC了俩次, 为什么结果是这样的呢？我们使用javap分析一下(限于篇幅, 我们只看最关键的部分).

首先看TestCombine1反编译之后的源码
```java
javap -v TestCombine1
...
Constant pool:
   #1 = Methodref          #13.#33        // java/lang/Object."<init>":()V
   #2 = Integer            100000
   #3 = Class              #34            // java/lang/StringBuilder
   #4 = Methodref          #3.#33         // java/lang/StringBuilder."<init>":()V
   #5 = String             #35            // string
   #6 = Methodref          #3.#36         // java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
   #7 = Fieldref           #12.#37        // testString/TestCombine1.str1:Ljava/lang/String;
   #8 = Methodref          #3.#38         // java/lang/StringBuilder.toString:()Ljava/lang/String;
   #9 = Class              #39            // java/lang/String
  #10 = String             #40            // 123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789
  #11 = Methodref          #9.#41         // java/lang/String."<init>":(Ljava/lang/String;)V
  #12 = Class              #42            // testString/TestCombine1
  #13 = Class              #43            // java/lang/Object
  #14 = Utf8               str1
  #15 = Utf8               Ljava/lang/String;
...
  #33 = NameAndType        #16:#17        // "<init>":()V
  #34 = Utf8               java/lang/StringBuilder
  #35 = Utf8               string
  #36 = NameAndType        #44:#45        // append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
  #37 = NameAndType        #14:#15        // str1:Ljava/lang/String;
  #38 = NameAndType        #46:#47        // toString:()Ljava/lang/String;
  #39 = Utf8               java/lang/String
  #40 = Utf8               123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789
  #41 = NameAndType        #16:#48        // "<init>":(Ljava/lang/String;)V
  #42 = Utf8               testString/TestCombine1
  #43 = Utf8               java/lang/Object
  #44 = Utf8               append
  #45 = Utf8               (Ljava/lang/String;)Ljava/lang/StringBuilder;
  #46 = Utf8               toString
  #47 = Utf8               ()Ljava/lang/String;
  #48 = Utf8               (Ljava/lang/String;)V
{
...
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=3, args_size=1
         0: iconst_0
         1: istore_1
         2: iload_1
         3: ldc           #2                  // int 100000
         5: if_icmpge     36
         8: new           #3                  // class java/lang/StringBuilder
        11: dup
        12: invokespecial #4                  // Method java/lang/StringBuilder."<init>":()V
        15: ldc           #5                  // String string
        17: invokevirtual #6                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        20: getstatic     #7                  // Field str1:Ljava/lang/String;
        23: invokevirtual #6                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        26: invokevirtual #8                  // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
        29: astore_2
        30: iinc          1, 1
        33: goto          2
        36: return
...
SourceFile: "TestCombine1.java"
```
通过看main方法, 我们可以看出, 是将`"string"`字符串常量和 `str1`这个静态常量通过`StringBuilder`进行拼接得到的`string1`. 所以不断产生`StringBuilder`对象,就不断地进行GC了

然后看TestCombine2反编译之后的源码
```java
ζ javap -v TestCombine2
...
Constant pool:
   #1 = Methodref          #11.#30        // java/lang/Object."<init>":()V
   #2 = Integer            100000
   #3 = Class              #31            // java/lang/StringBuilder
   #4 = Methodref          #3.#30         // java/lang/StringBuilder."<init>":()V
   #5 = String             #32            // string
   #6 = Methodref          #3.#33         // java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
   #7 = Methodref          #10.#34        // testString/TestCombine2.getFieldConstant:()Ljava/lang/String;
   #8 = Methodref          #3.#35         // java/lang/StringBuilder.toString:()Ljava/lang/String;
   #9 = String             #36            // 123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789
  #10 = Class              #37            // testString/TestCombine2
  #11 = Class              #38            // java/lang/Object
  #12 = Utf8               <init>
  #13 = Utf8               ()V
...
  #26 = Utf8               getFieldConstant
  #27 = Utf8               ()Ljava/lang/String;
  #28 = Utf8               SourceFile
  #29 = Utf8               TestCombine2.java
  #30 = NameAndType        #12:#13        // "<init>":()V
  #31 = Utf8               java/lang/StringBuilder
  #32 = Utf8               string
  #33 = NameAndType        #39:#40        // append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
  #34 = NameAndType        #26:#27        // getFieldConstant:()Ljava/lang/String;
  #35 = NameAndType        #41:#27        // toString:()Ljava/lang/String;
  #36 = Utf8               123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789
  #37 = Utf8               testString/TestCombine2
  #38 = Utf8               java/lang/Object
  #39 = Utf8               append
  #40 = Utf8               (Ljava/lang/String;)Ljava/lang/StringBuilder;
  #41 = Utf8               toString
{
...
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=3, args_size=1
         0: iconst_0
         1: istore_1
         2: iload_1
         3: ldc           #2                  // int 100000
         5: if_icmpge     36
         8: new           #3                  // class java/lang/StringBuilder
        11: dup
        12: invokespecial #4                  // Method java/lang/StringBuilder."<init>":()V
        15: ldc           #5                  // String string
        17: invokevirtual #6                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        20: invokestatic  #7                  // Method getFieldConstant:()Ljava/lang/String;
        23: invokevirtual #6                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        26: invokevirtual #8                  // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
        29: astore_2
        30: iinc          1, 1
        33: goto          2
        36: return
...
SourceFile: "TestCombine2.java"
```
和TestCombine1类似, 在mian方法中， 也是使用`StringBuilder`进行拼接处理的

最后看下TestCombine3反编译之后的源码
```java
ζ javap -v TestCombine3
...
Constant pool:
   #1 = Methodref          #5.#26         // java/lang/Object."<init>":()V
   #2 = Integer            1000000
   #3 = Class              #27            // testString/TestCombine3
   #4 = String             #28            // string123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789
   #5 = Class              #29            // java/lang/Object
   #6 = Utf8               str2
   #7 = Utf8               Ljava/lang/String;
   #8 = Utf8               ConstantValue
   #9 = String             #30            // 123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789
...
  #26 = NameAndType        #10:#11        // "<init>":()V
  #27 = Utf8               testString/TestCombine3
  #28 = Utf8               string123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789
  #29 = Utf8               java/lang/Object
  #30 = Utf8               123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789
{
...
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=3, args_size=1
         0: iconst_0
         1: istore_1
         2: iload_1
         3: ldc           #2                  // int 1000000
         5: if_icmpge     17
         8: ldc           #4                  // String string123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789
        10: astore_2
        11: iinc          1, 1
        14: goto          2
        17: return
...
}
SourceFile: "TestCombine3.java"
```
我们看` #4 = String             #28            // string123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789`这一行, Java编译器已经将`String string2 = "string" + str2;`这俩个字符串字面量常量
优化成了一个字符串字面量常量.

从上面的分析中我们可以得到, 在Java编译器进行优化的话, 只有当字符串字面量进行拼接的时候, 才会对其进行优化.