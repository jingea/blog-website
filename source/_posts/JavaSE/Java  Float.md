category: Java
date: 2016-10-31
title: Java Float 
---

今天有个以前的同事问了我一段代码, 
```java
public class SimplePrint {

	public static void main(String[] args) {
		long i = System.currentTimeMillis();
		System.out.println(i);
		float h = 0.0f;
		i -= h;
		System.out.println(i);
	}
}
```
前后俩个i输出结果不一致, 想了一下, 应该是 i 或者 h在转型时出的问题. 
于是写了段程序测试了一下
```java
public class SimplePrint {

	public static void main(String[] args) {
		long l = System.currentTimeMillis();
		System.out.println(l);
		l -= 0.0f;
		System.out.println(l);

		l = System.currentTimeMillis();
		l = (long)(l - 0.0f);
		System.out.println(l);

		l = System.currentTimeMillis();
		float f = (float)l;
		System.out.println(l);
		System.out.println(f);
		l = (long)(f - 0.0f);
		System.out.println(l);
	}
}
```
果真是如我所想, l 转换成了 Float,但是有点知其然不知其所然，于是跑到[stackoverflow上问了一下](http://stackoverflow.com/questions/40339165/what-is-happening-when-llong-ffloat), 有个大大回答的很好:

```java
E1 op= E2
```
等于
```java
E1 = (T) ((E1) op (E2))
```
T的类型就是E1, 而在上面的例子中当float和long运算时, long会转换成float.

我们知道在计算机中存储浮点数采用科学计数法, 也就是浮点数, 数字向右移动移动到小数点后面.
`3.1415` 被转化为 `0.31415x10^1`

Float是在Java中是用32个bit存储的, 1个bit表示正负符号(符号位), 7个bit表示精度(指数位), 23个bit表示有效数字.上面的数字就是5个有效数字, 指数为1, 精度也就是为1.

也就说按照小数点后面是0, 小数点前面最大有效数字是24位(16777216), 而指数为最大为7位(128), 很明显这个128位要大于24位, 这是为什么呢? 因为小数点后面可以是104个0然后是24个有效数字

> 有效数字是一个数字中从左第一个不为0的开始向右数, 直到数字结束, 中间的这部分就是有效数字

那么回到正文,刚开始的那个例子是问题出在哪里呢? 答案就在下面的例子里
```java
public class TestFloat {

	public static void main(String[] args) {
		long now = 1477911537443l;
		float f = (float)now;   // 0.1477911537443l * 10^13  => 10101100000011010011001(24位有效数字) 000110011100100011
		long time = (long)f;    // 截取000110011100100011 => 10101100000011010011001(有效数字) 000000000000000000 => 1477911511040
		System.out.println(time);   // 1477911511040
	}
}
```

