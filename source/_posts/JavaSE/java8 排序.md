category: JavaSE
date: 2016-07-12
title: java8 排序
---
在这里我们主要看一下Java8提供的`Comparator`实现的快捷排序.

首先写一个工具类
```java
class CompareObject {
	public int tall;
	public int age;
	public String name;

	public CompareObject(int tall, int age, String name) {
		this.tall = tall;
		this.age = age;
		this.name = name;
	}

	public int getTall() {return tall;}
	public int getAge() {return age;}
	public String getName() {return name;}

	public static List<CompareObject> getList() {
		CompareObject co1 = new CompareObject(160, 15, "张华");
		CompareObject co2 = new CompareObject(160, 19, "徐来");
		CompareObject co3 = new CompareObject(160, 16, "张德建");
		CompareObject co4 = new CompareObject(170, 18, "李子栋");
		CompareObject co5 = new CompareObject(170, 18, "玛丽凯乐");
		CompareObject co6 = new CompareObject(180, 18, "王义海");
		CompareObject co7 = new CompareObject(180, 19, "赵同利");
		CompareObject co8 = new CompareObject(180, 19, "刘丽");
		List<CompareObject> list = new ArrayList<>();
		list.add(co1);
		list.add(co2);
		list.add(co3);
		list.add(co4);
		list.add(co5);
		list.add(co6);
		list.add(co7);
		list.add(co8);
		return list;
	}
}
```
我们首先看一个最普通的排序
```java
public class TestSort {

	public static void main(String[] args) {
		List<CompareObject> list = CompareObject.getList();
		list.stream().sorted(Comparator.comparing(CompareObject::getTall)
						.thenComparing(CompareObject::getAge)
						.thenComparing(CompareObject::getName))
				.forEach(co -> System.out.println(co.getTall() + " " + co.getAge() + " " + co.getName()));
	}
}
```
结果为
```bash
160 15 张华
160 16 张德建
160 19 徐来
170 18 李子栋
170 18 玛丽凯乐
180 18 王义海
180 19 刘丽
180 19 赵同利
```

下面我们看一下它的倒序
```java
public class TestSort {

	public static void main(String[] args) {
		List<CompareObject> list = CompareObject.getList();
		list.stream().sorted(Comparator.comparing(CompareObject::getTall).reversed()
						.thenComparing(CompareObject::getAge)
						.thenComparing(CompareObject::getName))
				.forEach(co -> System.out.println(co.getTall() + " " + co.getAge() + " " + co.getName()));
	}
}
```
结果为
```bash
180 18 王义海
180 19 刘丽
180 19 赵同利
170 18 李子栋
170 18 玛丽凯乐
160 15 张华
160 16 张德建
160 19 徐来
```
然们将其作用在第二个comparing上
```java
public class TestSort {

	public static void main(String[] args) {
		List<CompareObject> list = CompareObject.getList();
		list.stream().sorted(Comparator.comparing(CompareObject::getTall)
						.thenComparing(CompareObject::getAge).reversed()
						.thenComparing(CompareObject::getName))
				.forEach(co -> System.out.println(co.getTall() + " " + co.getAge() + " " + co.getName()));
	}
}
```
结果为
```bash
180 19 刘丽
180 19 赵同利
180 18 王义海
170 18 李子栋
170 18 玛丽凯乐
160 19 徐来
160 16 张德建
160 15 张华
```
然们将其作用在第三个comparing上
```java
public class TestSort {

	public static void main(String[] args) {
		List<CompareObject> list = CompareObject.getList();
		list.stream().sorted(Comparator.comparing(CompareObject::getTall)
						.thenComparing(CompareObject::getAge)
						.thenComparing(CompareObject::getName).reversed())
				.forEach(co -> System.out.println(co.getTall() + " " + co.getAge() + " " + co.getName()));
	}
}
```
结果为
```bash
180 19 赵同利
180 19 刘丽
180 18 王义海
170 18 玛丽凯乐
170 18 李子栋
160 19 徐来
160 16 张德建
160 15 张华
```
通关上面的现象我们可以得出, 放在后面的`reversed()`会将前面的都执行倒序操作.

那么如果只想对某个键进行倒序, 其他都正序要如何操作呢？