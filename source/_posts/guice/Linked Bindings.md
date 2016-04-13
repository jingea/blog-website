category: guice
date: 2015-12-08
title: Guice 绑定
---

## 单绑定
```java
public class TestBindings {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(BindingA.class);
			}
		});
		BindingA a = injector.getInstance(BindingA.class);
		a.print();
	}
}

class BindingA {
	public void print() {
		System.out.println("A");
	}
}
```
输出结果为
```xml
A
```
我们可以将`BindingA`绑定到Guice里, 当我们getInstance时会直接获得该实例

> 注意如果单邦定时, BindingA必须为class, 如果为接口的话会产生No implementation for testGuice.BindingA was bound.异常

## 链式绑定
```java
public class TestBindings {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(BindingA.class).to(BindingB.class);
				bind(BindingB.class).to(BindingC.class);
			}
		});

		BindingC c = injector.getInstance(BindingC.class);
		c.print();
		BindingB b = injector.getInstance(BindingB.class);
		b.print();
		BindingA a = injector.getInstance(BindingA.class);
		a.print();
	}
}

class BindingA {
	public void print() {
		System.out.println("A");
	}
}
class BindingB extends BindingA {
	@Override
	public void print() {
		System.out.println("B");
	}
}
class BindingC extends BindingB {
	@Override
	public void print() {
		System.out.println("c");
	}
}
```
这段代码的最后调用结果都是
```java
c
c
c
```
这就是Guice的Linked Bindings, 当binding形成一条链之后,会以最终的绑定为最终绑定

> 注意绑定关系必须是继承关系

## 命名绑定
这种特性是为了,当某个接口有多种实现时,我们可以通过`@Named`指定我们具体使用哪种实现
```java
public class TestNamedBindings {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class).annotatedWith(Names.named("BType")).to(B.class);
				bind(A.class).annotatedWith(Names.named("CType")).to(C.class);
			}
		});
		Print print = injector.getInstance(Print.class);
		print.printB();
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
class C implements A {
	@Override
	public void print() {
		System.out.println("C");
	}
}

class Print {
	public void printB() {
		b.print();
	}
	public void printC() {
		c.print();
	}
	@Inject
	@Named("BType")
	private A b;
	@Inject
	@Named("CType")
	private A c;
}
```
输出结果为
```xml
B
C
B
```
我们还可以使用`BindingAnnotation`来实现相同的功能
```java
public class TestNamedBindings {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class).annotatedWith(BType.class).to(B.class);
				bind(A.class).annotatedWith(CType.class).to(C.class);
			}
		});
		Print print = injector.getInstance(Print.class);
		print.printB();
		print.printC();
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
class C implements A {
	@Override
	public void print() {
		System.out.println("C");
	}
}

@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
@interface BType {}

@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
@interface CType {}

class Print {
	public void printB() {
		b.print();
	}
	public void printC() {
		c.print();
	}
	@Inject
	@BType
	private A b;
	@Inject
	@CType
	private A c;
}
```