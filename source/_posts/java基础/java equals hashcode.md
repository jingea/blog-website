category: java基础
date: 2015-06-08
title: Java equals和hashcode
---
Effective Java学习总结

## euqals
如果类不具有自己的逻辑相等概念，那么就没有必要自己去覆盖`euqals()`方法. 在这种情况下,每个类的实例都和自身相等. 

但是如果程序里也关心逻辑上是否是相等,那么在实现`equals()`时就要考虑它的通用约定：
* 自反性: 对于任何非null的引用值x,`x.equals(x)`必须返回true. 这一点保证的是对象的自身必须等于其自身.
* 对称性: 对于任何非null的引用值x和y,当且仅当`y.equals(x)`返回true时,`x.equals(y)`必须返回true.
* 传递性: 对于任何非null的引用值x，y和z,如果`y.equals(x)`返回true且`y.equals(z)`返回true,则`z.equals(x)`也必须返回true
* 一致性: 对于任何非null的引用值x和y,只要equals的比较操作在对象中的信息没有被修改,多次调用`x.equals(y)`则一致地返回true或者返回false. 这也就是说如果俩个对象相等,那么他们就应该始终保持着相等.
* 非空性: 对于任何非null的引用值,`x.euqals(null)`都必须返回false

下面依次是违反上面几个特性的例子：

违反自反性:
```java
@Override
public boolean equals(Object obj) {
	return !super.equals(obj);
}
```

违反对称性 
```java
public static void main(String str1[]) {

	N n1 = new N();
	n1.id = 123;
	Integer id = 123;
	System.out.println(n1.equals(id));
	System.out.println(id.equals(n1));
}

class N{

	public Integer id;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Integer) {
			return obj.equals(id);
		}
		return super.equals(obj);
	}
}
```

违反传递性
```java

```


违反一致性
```java
public static void main(String str1[]) {
		N n1 = new N();
		n1.id = 123;
		N n2 = new N();
		n2.id = 123;
		System.out.println(n1.equals(n2));
		n2.id = 12;
		System.out.println(n1.equals(n2));
	}
}

class N{
	public int id;
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof N)) {
			return false;
		}

		return ((N) obj).id == id;
	}
}
```

违反非空性： 这个一般我们不会犯错，因为我们一般都有下面这样的语法,当obj为null时,就会自动返回false
```java
if (obj instanceof N) {
	return false;
}
```
一般在实现equals方法时,我们要做到以下几点
```java
class N{

	public int id;

	@Override
	public boolean equals(Object obj) {
		// 检查参数是否是这个对象的引用,当equals操作代价昂贵时,这么做会达到性能的提升
		if (obj == this) {
			return true;
		}

		// 检查是否是正确的类型
		if (!(obj instanceof N)) {
			return false;
		}

		// 把参数转换为正确的类型
		N target = (N)obj;

		// 对于该类中的每个关键域都对其进行匹配
		return target.id == id;
	}
}
```
对于对每个关键域进行判断的时候,除了`float`和`double`都可使用`==`进行判断
* float采用`Float.compare()`进行判断
* double采用`Double.compare()`进行判断
如果是数组可以使用`Arrays.equals()`进行判断.

当equals完成了上述之后,还要对其进行对称性,传递性,一致性进行单元测试. 


## hashCode
当覆盖`equals()`时总要覆盖`hashCode()`.

对于hashCode的通俗约定：
* 在应用程序的运行期间,只要对象的equals方法
