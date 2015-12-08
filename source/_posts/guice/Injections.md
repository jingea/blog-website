category: guice
date: 2015-12-08
title: Guice 
---
## Constructor Injection
对构造器进行注入
```java
public class TestLinkedBindings {
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
	Print(A a) {
		this.a = a;
	}

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

## Method Injection
```java
public class TestLinkedBindings {
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
Guice会自动调用被`Inject`注解过的方法

## Field Injection
```java
public class TestLinkedBindings {
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
public class TestLinkedBindings {
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
    for field at guice.Print.a(TestLinkedBindings.java:37)
  while locating guice.Print
```
说明如果可选值如果是false的话就必须对其进行绑定

## Static Injections
```java
public class TestLinkedBindings {
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
	private static A a;

	public void print() {
		a.print();
	}
}

class ABCModule extends AbstractModule {
	@Override
	protected void configure() {
		requestStaticInjection(Print.class);
		bind(A.class).to(B.class);
	}
}
}
```
如果我们要对某个静态成员进行注解的话，必须在`config()`方法里调用`requestStaticInjection()`方法,将静态成员所在类传进去.