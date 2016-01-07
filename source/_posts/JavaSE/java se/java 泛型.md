category: JavaSE
tag: JAVA7
date: 2015-06-08
title: java泛型
---

泛型（Generic type 或者 generics）是对 Java 语言的类型系统的一种扩展，以支持创建可以按类型进行参数化的类.

### 泛型类
我们定义一个简单的泛型类, `T`称为泛型参数, `G`被称为泛型化了
```java
class G<T> {

}
```
接着我们在内部定义一个泛型变量
```
class G<T> {
	T t;
}
```
然后我们再添加一个泛型方法泛型方法
```java
class G<T> {
	T t;

	public void setValue(T t) {
		this.t = t;
	}
}
```
下来我们来使用一下这个泛型类
```java
G<String> g = new G<>();
g.setValue("value");
```


### 泛型方法
在一个非泛化的类中我们也可以直接定义泛化的方法


### 类型擦除
说到java中的泛型就不得不提泛型参数的类型擦除. 当java源码文件被编译成class文件的时候,编译器会将泛型中的类型参数擦除掉(其实class文件中还是会保留部分的泛型信息, 具体参考java虚拟机规范).

类定义的泛型参数`T`会被替换成具体类型, 一般为Object. 而`<T>`信息则会被擦除掉, 例如G就会替换成
```java
class G {
	Object t;

	public void setValue(Object t) {
		this.t = t;
	}
}
```
而在引用该类型的时候则会擦除成
```java
G g = new G();
g.setValue("value");
```
因此，java里的泛型类型安全是由编译器保证的, 在运行期是无法保证类型的安全的.

### 泛型类的继承关系
如果类被泛型化之后, 会对类本身的继承关系造成影响
```java
public class TestGeneric {

	public static void main(String[] args) {
		SuperParam superParam = new SuperParam();

		G<SuperParam> g = new G<>();
		g.setValue(superParam);

		SubG1 subG1 = new SubG1();
		subG1.setValue(1);
		subG1.printValue("SubG1 printValue");
		subG1.print();

		SubG2 subG2 = new SubG2();
		subG2.setValue("SubG2 setValue");
		subG2.printValue("SubG2 printValue");
		subG2.print();

		SubG3<Integer> subG3 = new SubG3<>();
		subG3.setValue("SubG3 setValue");
		subG3.printValue("SubG3 printValue");
		subG3.print(3);

		SubG4<Integer> subG4 = new SubG4();
		subG4.setValue("SubG4 setValue");
		subG4.printValue("SubG4 printValue");
		subG4.print(4);
	}
}

class G<T> {
	T t;

	public void setValue(T t) {
		this.t = t;
	}

	public void printValue(T t) {
		System.out.println(t);
	}

	public T getT() {
		return t;
	}
}

// SubG1继承了G的泛型参数. 但是SubG1本身是没有泛化的
class SubG1 extends G {
	public void print() {
		System.out.println(t);
	}
}

// 强制指定继承过来的T的类型为String
class SubG2 extends G<String> {
	public void print() {
		System.out.println("Super:" + t);
	}
}

// G的类型由继承过来的方法指定, SubG3则再次由本类自己指定类型
class SubG3<T> extends G {
	public void print(T t1) {
		System.out.println("Super:" + t + ". this:" + t1);
	}
}

// 指定G的类型为String, SubG4则仍然由本类进行泛型化
class SubG4<T> extends G<String> {
	public void print(T t1) {
		System.out.println("Super:" + t + ". this:" + t1);
	}
}

class SuperParam {
	public String toString() {
		return "SuperParam";
	}
}
```
结果为
```
SubG1 printValue
1
SubG2 printValue
Super:SubG2 setValue
SubG3 printValue
Super:SubG3 setValue. this:3
SubG4 printValue
Super:SubG4 setValue. this:4
```
需要特别指出的是, 在`SubG1`对象分别调用`setValue()`和`printValue()`方法时分别使用了`Integer`和`String`俩个类型, 但是却没有产生任何异常信息.


### 泛型参数的继承关系
```java
public class TestGeneric {

	public static void main(String[] args) {
		SuperParam superParam = new SuperParam();
		Param param = new Param();

		G<SuperParam> gSuperParam = new G<>();
		gSuperParam.setValue(superParam);
		gSuperParam.setValue(param);
		SuperParam t = gSuperParam.getT();

		G<Param> gParam = new G<>();
		gParam.setValue(param);
		gParam.setValue(superParam);	// compile error

		SubParam subParam = new SubParam();
		gParam.setValue(subParam);

	}
}

class G<T> {
	T t;

	public void setValue(T t) {
		this.t = t;
	}

	public T getT() {
		return t;
	}
}

class SuperParam {}

class Param extends SuperParam {}

class SubParam extends Param {}
```
从这一行`gParam.setValue(subParam);`我们可以看到类型参数的继承结构和普通类型的继承结构的规则是一样的.

### 泛化在方法中的应用
```java
public class TestGeneric {

	public static void main(String[] args) {

		print(new G<>());
		print(new G<SuperParam>());		// compile Error
		print(new SubG1());
		print(new SubG2());
		print(new SubG3());
		print(new SubG3<SuperParam>());
		print(new SubG4());
		print(new SubG4<SuperParam>());

		printSuperParam(new G<>());
		printSuperParam(new G<SuperParam>());
		printSuperParam(new G<Param>());		// compile Error
	}

	public static void print(G<String> gs) {}

	public static void printSuperParam(G<SuperParam> gs) {}
}
```
从上面的例子中我们可以看出, 泛化的类的泛型参数并没有对其类型判断造成影响, 子类化的参数仍然是编译通过的. 但是类型参数的继承再传递到方法时, 却被认为不是相同的类型.

### 通配符
`?`在泛型参数中作为通配符存在, 它一般和`extends`和`super`关键字一起使用. 它表示不确定的一组类型, 例如和`extends`关键字一起使用就是表示继承自某个类的所有类型

### extends
`extends`关键字是用来定义泛型参数的继承关系. 它表示我们的泛型参数继承自某个类型, 也被我们称为上界符.

我们修改一下G的类型定义,我们引入`extends`关键字
```java
public class TestGeneric {

	public static void main(String[] args) {

		SuperParam superParam = new SuperParam();

		G<SuperParam> g = new G<>();
		g.setValue(superParam);

		SubG1 subG1 = new SubG1();
		subG1.setValue(1);
		subG1.printValue("SubG1 printValue");
		subG1.print();

	}
}

class G<T extends SuperParam> {
	T t;

	public void setValue(T t) {
		this.t = t;
	}

	public void printValue(T t) {
		System.out.println(t);
	}

	public T getT() {
		return t;
	}
}
```
当使用`extends`关键字之后, 我们就将这个类的泛化信息固定了下来, 在实例化的时候, 其类型参数必须是继承自某类的子类型


如果我们在实例化的时候指定`extends`会发生什么呢?
```java
public class TestGeneric {

	public static void main(String[] args) {
		SuperParam superParam = new SuperParam();

		G<? extends SuperParam> g = new G<>();
		g.setValue(superParam);		// compile error
		g.setValue(new Param());		// compile error
	}
}

class G<T> {
	T t;

	public void setValue(T t) {
		this.t = t;
	}
}

class SuperParam {}

class Param extends SuperParam {}
class Param1 extends SuperParam {}
```
不推荐这种用法, 因为这种情况下如果我们可以对其使用`Param`或者`Param1`的类型, 那么这就和不使用泛型是一样的, 会引起类型转化异常.

但是我们却可以在另外一种情况下使用这个关键字
```java
public class TestGeneric {

	public static void main(String[] args) {
		SuperParam superParam = new SuperParam();

		print(new G<>());	// 默认的是SuperParam类型
		print(new G<Param>());
	}

	public static void print(G<? extends SuperParam> g) {
		SuperParam t = g.getT();
	}
}

class G<T> {
	T t;

	public void setValue(T t) {
		this.t = t;
	}

	public T getT() {
		return t;
	}
}

class SuperParam {}

class Param extends SuperParam {}
```

### super
`super`作为一种下界符存在. 也就在具体使用时的参数都必须是泛型参数的父类才行.
```java
G<? super SuperParam> g = new G<>();
g.setValue(new SuperParam());
g.setValue(new Param());
```
同样我们可以在方法中如此使用
```java
public class TestGeneric {

	public static void main(String[] args) {
		print(new G<SuperParam>());
		print(new G<>());	// 默认是Param
	}

	public static void print(G<? super Param> g) {
		Object t = g.getT();
	}
}

class G<T> {
	T t;

	public void setValue(T t) {
		this.t = t;
	}

	public T getT() {
		return t;
	}
}

class SuperParam {}

class Param extends SuperParam {}
```

