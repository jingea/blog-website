category: Java
date: 2016-06-13
title: Integer Parse ValueOf
---
今天使用FindBugs检查项目时，发现有这样一个提示
```bash
Boxing/unboxing to parse a primitive
A boxed primitive is created from a String, just to extract the unboxed primitive value. It is more efficient to just call the static parseXXX method.
```
这句话的意思是说，我们正在讲一个String解析成一个boxed的原生类型，也就是Integer，Long这些等等，但是我们只需要将String解析成unboxed原生类型即可，也就是int，long这种。最后它推荐使用`parseXXX()`这样的静态方法.

于是很好奇`parseXXX()`和`valueOf()`有啥不同呢？打开源码看一看
```java
public static Integer valueOf(String s) throws NumberFormatException {
      return Integer.valueOf(parseInt(s, 10));
  }
public static Integer valueOf(int i) {
    if (i >= IntegerCache.low && i <= IntegerCache.high)
        return IntegerCache.cache[i + (-IntegerCache.low)];
    return new Integer(i);
}
```
我们看到`valueOf()`内部也是调用了`parseInt()`方法. 从下面的`valueOf()`方法可以看出, `parseInt()`返回的是一个unboxed的原生类型数据. 因此在上面的场景中会有那样的提示.

但是看到这里还不算完, 看到这让我想起了一个以前碰到的面试题
```java
public class TestIntegerValueOf {
	public static void main(String[] args) {
		Integer i1 = 1;
		Integer i2 = 1;
		System.out.println(i1 == i2);

		Integer i3 = 200;
		Integer i4 = 200;
		System.out.println(i3 == i4);

		Integer i5 = Integer.valueOf(100);
		Integer i6 = Integer.valueOf(100);
		System.out.println(i5 == i6);

		Integer i7 = Integer.valueOf(200);
		Integer i8 = Integer.valueOf(200);
		System.out.println(i7 == i8);
	}
}
```
结果为
```bash
true
false
true
false
```
我们看到在讲int强转为Interger的时候, 也是`valueOf()`的逻辑

除了Integer之外, 还有哪些基本原生类型是这样子的呢？先看看long
```java
public static Long valueOf(long l) {
    final int offset = 128;
    if (l >= -128 && l <= 127) { // will cache
        return LongCache.cache[(int)l + offset];
    }
    return new Long(l);
}
```
接下来是Short
```java
public static Short valueOf(short s) {
    final int offset = 128;
    int sAsInt = s;
    if (sAsInt >= -128 && sAsInt <= 127) { // must cache
        return ShortCache.cache[sAsInt + offset];
    }
    return new Short(s);
}
```
接下来是Byte
```java
public static Byte valueOf(byte b) {
    final int offset = 128;
    return ByteCache.cache[(int)b + offset];
}
```
接下来是Char
```java
public static Character valueOf(char c) {
    if (c <= 127) { // must cache
        return CharacterCache.cache[(int)c];
    }
    return new Character(c);
}
```
接下来是Float
```java
public static Float valueOf(float f) {
    return new Float(f);
}
```
接下来是Double
```java
public static Double valueOf(double d) {
    return new Double(d);
}
```

我们看到除了浮点型之外，都采用了缓存的原理。虽然我们从源码中看到了结果，可是求个心安，我们还是要写代码测试一下
```java
public class Test {
	public static void main(String[] args) {
		Short s_1 = 100;
		Short s_2 = 100;
		System.out.println(s_1 == s_2);

		Long l_1 = 100l;
		Long l_2 = 100l;
		System.out.println(l_1 == l_2);

		Character c_1 = 100;
		Character c_2 = 100;
		System.out.println(c_1 == c_2);

		Short s1 = 200;
		Short s2 = 200;
		System.out.println(s1 == s2);

		Long l1 = 200l;
		Long l2 = 200l;
		System.out.println(l1 == l2);

		Character c1 = 200;
		Character c2 = 200;
		System.out.println(c1 == c2);
	}
}
```
结果为
```bash
true
true
true
false
false
false
```
好了，这下世界安静了

-------------------------------------------------------------------------
后来在项目中发现有使用Gson转换中也会发生这种情况, 写个代码测试一下
```java
public class TestIntegerValueOf {
	public static void main(String[] args) {
		Obj obj = new Obj();
		obj.integer = 200;
		Gson gson = new Gson();
		String str = gson.toJson(obj);
		System.out.println(str);
		Obj newObj1 = gson.fromJson(str, Obj.class);
		Obj newObj2 = gson.fromJson(str, Obj.class);
		System.out.println(newObj1.integer == newObj2.integer);
	}

	private static class Obj {
		public Integer integer;
	}
}
```
输出结果为
```bash
{"integer":200}
false
```
看来在Gson中不是使用强转就是使用的`valueOf()`