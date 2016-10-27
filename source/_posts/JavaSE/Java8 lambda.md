category: Java
date: 2015-09-08
title: java lambda
---

## 函数接口

### 函数接口定义
函数接口只是一个抽象方法的接口,用作lambda表达式类型.

注意, 上面这个定义有三个需要注意的地方
1. 函数接口是一个接口
2. 函数接口有且只有一个抽象方法(只有一个表示数量上是唯一的,重载也是不可以)
3. 函数接口用作lambda表达式类型

### 函数接口示例:
```java
// 定义一个非泛型没有返回值没有参数的函数接口
interface Run1 {
	public void runFast();
}
// 定义一个非泛型没有返回值有参数的函数接口
interface Run2 {
	public void runFast(int seconds);
}
// 定义一个非泛型有返回值有参数的函数接口
interface Run3 {
	public int runFast(int seconds);
}
// 定义一个泛型有返回值有参数的函数接口
interface Run4<T> {
	public int runFast(T t, int seconds);
}
```

### 默认方法
我们知道java8对核心集合类进行了大幅度修改,例如`Collection`接口添加了`stream()`方法. 那么所有的`Collection`实现类都必须来实现该方法. 为了保持二进制接口的兼容性,java8提供了默认方法,来保证这一兼容性(例如来源在java1到jav7平台写出的代码仍然可以在java8平台上编译运行)
```java
interface Run10 {
	public void runFast();

	public default void runAt9Clock() {
		System.out.println("run10 runAt9Clock");
	}
}

interface Run11  extends Run10 {

}

// 调用
Run11 run11 = () -> {
	System.out.println();
};
run11.runAt9Clock();

```
那么所有的子类都可以来调用这个默认方法, 而不必实现它。

> 如果接口中只有一个默认方法,那么这个接口就不是接口函数.

#### 继承默认方法
```java
interface Run11 extends Run10 {
	public default void runAt9Clock() {
		System.out.println("run11 runAt9Clock");
	}
}

class Run12 implements Run10 {
	@Override
	public void runFast() {}

	public void runAt9Clock() {
		System.out.println("run12 runAt9Clock");
	}
}
```
从上面的例子中我们可以看到如果接口`Run11`继承了接口`Run10`, 同时重载了默认方法, 那么`Run11`中的默认方法也必须含有`default`关键字. 但是在类中重载的话,就可以不必存在了.
```java
Run11 run11 = () -> {
	System.out.println();
};
run11.runAt9Clock();

Run12 run12 = new Run12();
run12.runAt9Clock();

//result
run11 runAt9Clock
run12 runAt9Clock
```
接着我们都调用默认方法,我们发现当调用默认方法时都会优先调用子类中的方法.

#### 多重继承
```java
interface Run10 {
	public default void runAt9Clock() {
		System.out.println("run10 runAt9Clock");
	}
}

interface Run13 {
	public default void runAt9Clock() {
		System.out.println("run13 runAt9Clock");
	}
}

class Run14 implements Run10, Run13 {
	@Override
	public void runAt9Clock() {

	}
}
```
在上面这个情况下,我们需要手动在`Run14`这个类中指定重载哪个方法, 否则会产生编译错误：
```java
class Run14 implements Run10, Run13 {
	@Override
	public void runAt9Clock() {
		Run10.super.runAt9Clock();
	}
}
```

### 接口静态方法
我们定义一个接口静态方法
```java
interface Run1 {
	public void runFast();

	public static void runSlowly() {
		System.out.println("run1 run slowly");
	}
}

//
Run1.runSlowly();
```
需要注意的是：
* 接口静态方法不会被继承到子接口或者子类中

### @FunctionalInterface
所有的函数接口都应该添加`@FunctionalInterface`注释. 该注释会强制检查javac检查一个接口是否符合函数接口的标准. 如果将这个注释添加给类，枚举，多个方法的接口都会产生编译错误.

## lambda表达式

### lambda表达式定义
接下来我们根据上面定义的函数接口来定义一下lambda表达式
```java
// 不带参数的版本
Run1 run1 = () -> {
	System.out.println("I am running");
};

// 参数要指定
Run2 run2 = seconds -> {
	System.out.println("I am running " + seconds + " seconds");
};

// 下面这个版本就必须要有个返回值了
Run3 run3 = seconds -> {
	System.out.println("I am running");
	return 0;
};

// 我们在下面的版本中指定了它的泛型信息
Run4<String> run4 = (name, seconds) -> {
	System.out.println(name + " is running");
	return 0;
};
```

### lambda表达式使用

接下来我们使用上面定义的lambda表达式
```java
run1.runFast();
-> I am running

run2.runFast(10);
-> I am running 10 seconds

int result = run3.runFast(10);
-> I am running

run4.runFast("小狗", 10); 小狗 is running
->
```

#### 注意

我们引用lambda表达式外部的一个变量
```java
String name = "sam";
Run1 run1 = () -> {
	System.out.println(name + " am running");
};
```

编译运行通过没有问题,但是如果我们将name在lambda表达式内部重新赋值的话

```java
String name = "sam";
Run1 run1 = () -> {
	name = "";
	System.out.println(name + " am running");
};
```
会提示`variable used in lambda expression shouble be final`, 这说明lambda其实内部引用的是值而不是变量.

好,接下来我们换种方式再次验证一下我们的结果：
```java
String name = "sam";
name = "Jams";
Run1 run1 = () -> {
	System.out.println(name + " am running");
};
```
同样的产生了编译错误.

#### java中重要的函数接口
* `Predicate<T>`: `boolean test(T t)` 判断输入的对象是否符合某个条件
* `Consumer<T>`: `void accept(T t);`  接收一个输入参数并且没有返回值
* `Supplier<T>`: `T get();`  可以看成一个对象的工厂，每次调用返回一个给定类型的对象
* `UnaryOperator<T>`: ``
* `BinaryOperator<T>`: ``

## 函数
在Java8中什么是函数呢？
```java
Run1 run1 = () -> {
	System.out.println("I am running");
};
```
上面`run1`这个就代表一个函数. 一般我们把属于某个类的函数称为方法, 而不依赖于类而存在的函数称之为方法.

### 高阶函数
如果某个函数A作为函数B的参数或者返回值, 那么我们称函数B为高阶函数,像下面的`run6`就是一个高级函数
```java
interface Run6 {
	public void run(Run1 run1);
}

Run6 run6 = run1Param -> {
			System.out.println("run6");
			run1Param.runFast();
		};

run6.run(run1);
```
我们将`run1`这个函数作为方法传递给了`run6`.

#### 返回函数
```java
interface Run8 {
	public void run(String name, int second, int mils);
}

interface Run9 {
	public Run8 run(Run8 run8);
}

Run8 run8 = (name, second, mils) -> {
	System.out.println();
};

Run9 run9 = run8Param -> {
	return run8Param.run("lily");
};
```
在上述的例子中产生了编译错误, 在`Haskell`这种纯FP语言中可以将一个调用函数但是参数不完整的函数从某个参数中返回或者定义一个参数不完整的函数值.

### 重载解析
我们使用函数接口作为方法参数,然后进行重载
```java
// 定义函数接口
interface Run1 {
	public void runFast();
}

interface Run2 {
	public void runFast();
}


// 定义重载代码
	public static void run(Run1 run1){
		System.out.println("run1");
	}

	public static void run(Run2 run2){
		System.out.println("run2");
	}

// 定义运行代码
public static void main(String[] args) {
	run(() -> System.out.println());
}
```
当我们进行如上定义时,javac提示了编译错误：不确定的方法调用,`run(Run1 run1)`和`run(Run2 run2)`都符合.

但是如果`Run2`继承了`Run1`这个接口之后
```java
interface Run1 {
	public void runFast();
}

interface Run2 extends Run1 {
	public void runFast();
}
```
当我们运行测试代码之后,我们发现输出的`run2`.

当Lambda表达式作为参数时,其类型由它的目标类型推导得出,推导过程遵循如下规则：
* 如果只有一个可能的目标类型,由相应的函数接口里的参数类型推导得出
* 如果有多个可能的目标类型，由最具体的类型推导得出
* 如果有多个可能的目标类型且最具体的类型不明确，则需要人为指定类型

## 方法引用
方法引用是简洁的Lambda表达式，能够用于已经拥有名称的方法。

* 静态方法 (ClassName::methName)
* 对象实例方法 (instanceRef::methName)
* 类型的实例方法 (ClassName::methName, 引用时和静态方法是一样的，但这里的 methName 是个实例方法)
* 构造方法 (ClassName::new)
* 数组的构造方法 (TypeName[]::new)

### 静态方法引用
```java
public class Print {
	public static void main(String[] args) throws Exception {
		F f = Print::p;
		f.m();
	}

	public static void p() {
		System.out.println("Print");
	}
}

@FunctionalInterface
interface F {
	void m();
}

```

### 类型实例方法引用
```java
public class Print {
	public static void main(String[] args) throws Exception {
		F f = String::length;
		int len = f.m("12");
		System.out.println(len);
	}
}

@FunctionalInterface
interface F {
	int m(String p);
}
```

### 构造方法引用
```java
public class Print {
	public static void main(String[] args) throws Exception {
		F f = Print::new;
		Print p = f.m();
		System.out.println(p == null);	// 结果为null
	}
}

@FunctionalInterface
interface F {
	Print m();
}
```

### 闭包
Java8还提供了闭包这个特性,虽然我不知道闭包这个特性有啥用,但是还是实验了一下
```java
public class Java8 {

	public static void main(String[] args) {
		I i = () -> {
			C c = new C();
			c.count = 10;

			J j = () -> {
				System.out.println("J print c:" + c.count);
				return c;
			};

			System.out.println("I print c:" + c.count);
			return j;
		};

		J j = i.r();
		C c = j.c();
		c.count = 20;
		System.out.println("main print c:" + c.count);
	}
}


interface I {
	public J r();
}

interface J {
	public C c();
}

class C {
	public int count;
}
```
我们定义了俩个接口, `I`和`J`, 我们在I的lambada中调用J的lambada, 然后让J返回一个定义在I的对象C, 最后我们在main函数中成功的返回了这个对象.
