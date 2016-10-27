category: Java Library
tag: Guice
date: 2015-12-08
title: Guice Provides绑定方式
---
我们可以使用`Provides`注解替代`configure()`实现的绑定.
```java
public class TestProvidesMethods {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
			}

			@Provides
			public A provideA() {
				B b = new B();
				return b;
			}
		});
		A print = injector.getInstance(A.class);
		print.print();
	}
}
class A {
	public void print() {
		System.out.println("A");
	}
}
class B extends A {
	@Override
	public void print() {
		System.out.println("B");
	}
}
```
我们在module实现里添加了`@Provides`注释, 当我们在测试代码里要获取某种类型的对象的时候, Guice会根据返回某种类型的方法调用.

> `@Provides`注释下的名字可以是任意的. 但是我们还是建议采用provideXXX的形式

需要注意的是, 返回的类型必须是唯一的, 如果我们添加下面的代码
```java
class C implementsA {
	@Override
	public void print() {
		System.out.println("c");
	}
}

@Provides
public A provideC() {
	C b = new C();
	return b;
}
```
guice会产生异常
```java
Exception in thread "main" com.google.inject.CreationException: Unable to create injector, see the following errors:

1) A binding to guice.A was already configured at guice.ABCModule.provideA().
```

还有一点需要注意的是,如果`@Provides`已经使用过某种类型,那么在`config()`方法里就不能再次使用
```java
@Override
protected void configure() {
	bind(A.class).to(C.class);
}
```
同样会产生异常
```java
Exception in thread "main" com.google.inject.CreationException: Unable to create injector, see the following errors:

1) A binding to guice.A was already configured at guice.ABCModule.provideA().
```

如果我们的`provide`方法很复杂,我们可以将其抽取到一个类里
```java
public class TestLinkedBindings {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new ABCModule());
		A print = injector.getInstance(A.class);
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

class BProvider implements Provider<A> {

	@Override
	public A get() {
		return new B();
	}
}

class ABCModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(A.class).toProvider(BProvider.class);
	}
}
```
