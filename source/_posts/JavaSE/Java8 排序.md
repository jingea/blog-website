category: Java
date: 2016-07-12
title: java8 排序
---
## 示例
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
```java
public class TestClassForName {
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException {
        List<CompareObject> list = CompareObject.getList();
        list.stream().sorted(Comparator.comparing(CompareObject::getTall)
                .thenComparing(CompareObject::getAge, Comparator.reverseOrder())
                .thenComparing(CompareObject::getName))
                .forEach(co -> System.out.println(co.getTall() + " " + co.getAge() + " " + co.getName()));

    }
}
```
输出结果为
```bash
160 19 徐来
160 16 张德建
160 15 张华
170 18 李子栋
170 18 玛丽凯乐
180 19 刘丽
180 19 赵同利
180 18 王义海
```
我们看到年龄已经降序排序了

## TimSort异常
今天项目中跑出了一段排序的异常代码
```java
java.lang.IllegalArgumentException: Comparison method violates its general contract!
	at java.util.TimSort.mergeLo(TimSort.java:777)
	at java.util.TimSort.mergeAt(TimSort.java:514)
	at java.util.TimSort.mergeCollapse(TimSort.java:441)
	at java.util.TimSort.sort(TimSort.java:245)
	at java.util.Arrays.sort(Arrays.java:1512)
	at java.util.ArrayList.sort(ArrayList.java:1454)
	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:387)
	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
```
通过百度得知, 这是因为JDK升级到7的时候内置的排序算法由归并排序替换成了TimeSort算法. 然后打开JDK API看一下它现在的描述
```bash
int compare(T o1,
          T o2)
Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
In the foregoing description, the notation sgn(expression) designates the mathematical signum function, which is defined to return one of -1, 0, or 1 according to whether the value of expression is negative, zero or positive.

The implementor must ensure that sgn(compare(x, y)) == -sgn(compare(y, x)) for all x and y. (This implies that compare(x, y) must throw an exception if and only if compare(y, x) throws an exception.)

The implementor must also ensure that the relation is transitive: ((compare(x, y)>0) && (compare(y, z)>0)) implies compare(x, z)>0.

Finally, the implementor must ensure that compare(x, y)==0 implies that sgn(compare(x, z))==sgn(compare(y, z)) for all z.

It is generally the case, but not strictly required that (compare(x, y)==0) == (x.equals(y)). Generally speaking, any comparator that violates this condition should clearly indicate this fact. The recommended language is "Note: this comparator imposes orderings that are inconsistent with equals."

Parameters:
o1 - the first object to be compared.
o2 - the second object to be compared.
Returns:
a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
Throws:
NullPointerException - if an argument is null and this comparator does not permit null arguments
ClassCastException - if the arguments' types prevent them from being compared by this comparator.
```
通过这个描述我们可以看出compare函数是对o1和o2进行对比，如果`o1<o2`则返回负数, `o1==o2`返回0, `o1>o2`返回正数.

在第二段中又描述到，`sgn(expression)`函数（数学上的符号函数, 取出一个数字的符号）定义了在上文中的正数要返回1，负数要返回-1，0则返回0.

接着说到，如果要实现这个函数则必须保证三点
1. 在所有的x和y下，`sgn(compare(x, y)) == -sgn(compare(y, x))`. 这意味着当且仅当`compare(y, x)`抛出异常的话,则`compare(x, y)`也必须抛出一个异常. 
2. 还必须要保证传递性, `((compare(x, y)>0) && (compare(y, z)>0))`则必须`compare(x, z)>0`. 
3.最后还需要保证当`compare(x, y)==0`时则对于所有的z要`sgn(compare(x, z))==sgn(compare(y, z))`.

最后阐述的是对`equals()`函数, compare函数并不严格要求要确保`(compare(x, y)==0) == (x.equals(y))`. 

看了这么多, 那么那个bug是怎么发生的呢？

