category: Java Library
tag: Guice
date: 2015-12-08
title: Guice 注入
---

## 构造器注入
对构造器进行注入
```java
public class TestInject {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				// A类型的变量都使用B的实例值进行注入. 也就是说当我们对A类型的变量注入值的时候, 其实注入的是B类型
				// B一定要继承A或者实现A接口
				bind(A.class).to(B.class);
			}
		});
		
		Print print = injector.getInstance(Print.class);
		print.print();
	}
}

class A {
	void print() {
		System.out.println("A");
	}
}

class B extends A{
	@Override
	public void print() {
		System.out.println("B");
	}
}

class Print {
	private A a;

	@Inject
	Print(A a) {
		this.a = a;
	}

	public void print() {
		a.print();
	}
}
```
输出结果为B, 注入成功

## 方法参数注入
```java
public class TestInject {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new ABCModule());
		Print print = injector.getInstance(Print.class);
		print.print();
	}
}

interface A {
	void print();
}

class B implements A {
	@Override
	public void print() {
		System.out.println("B");
	}
}

class Print {

	private A a;
	@Inject
	public void setA(A a) {
		this.a = a;
	}
	public void print() {
		a.print();
	}
}
```
输出结果同样是B

## 方法注入
当一个方法使用`Inject`注解时, 如果getInstance该类的实例就会调用该方法一次
```java
public class TestStaticInjection {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
			}
		});
		Print b1 = injector.getInstance(Print.class);
	}
}

class Print {
	@Inject
	public void print() {
		System.out.println("Hello world");
	}
}
```

## 成员属性注入
```java
public class TestInject {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new ABCModule());
		Print print = injector.getInstance(Print.class);
		print.print();
	}
}

interface A {
	void print();
}

class B implements A {
	@Override
	public void print() {
		System.out.println("B");
	}
}

class Print {
	@Inject
	private A a;

	public void print() {
		a.print();
	}
}

class ABCModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(A.class).to(B.class);
	}
}
```
Guice会对被`Inject`注解过的属性赋值

## Optional Injections
```java
public class TestInject {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new ABCModule());
		Print print = injector.getInstance(Print.class);
		print.print();
	}
}

interface A {
	void print();
}

class B implements A {
	@Override
	public void print() {
		System.out.println("B");
	}
}

class Print {
	@Inject(optional=true)
	private A a;

	public void print() {
		a.print();
	}
}

class ABCModule extends AbstractModule {
	@Override
	protected void configure() {
//		bind(A.class).to(B.class);
	}
}
```
如果我将要被`Inject`注解的属性设置为`optional=true`的话,当我注释掉绑定代码,在运行代码时会产生一个空指针异常,这是因为当找不到绑定的时候,就不进行注解
```
Exception in thread "main" java.lang.NullPointerException
```
但是如果我将`Inject`注解的属性设置为`optional=false`的话,在运行代码会产生
```java
Exception in thread "main" com.google.inject.ConfigurationException: Guice configuration errors:

1) No implementation for guice.A was bound.
  while locating guice.A
    for field at guice.Print.a(TestInject.java:37)
  while locating guice.Print
```
说明如果可选值如果是false的话就必须对其进行绑定

## 静态属性注入
对类中的静态字段进行注入
```java
public class TestStaticInjection {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				requestStaticInjection(Print.class);
			}
		});
		Print b1 = injector.getInstance(Print.class);
		b1.print();
	}
}

class A {
	public void print() {
		System.out.println("A");
	}
}

class Print {
	@Inject
	private static A a;

	public void print() {
		a.print();
	}
}
```
> 但是我们应该避免静态属性注入