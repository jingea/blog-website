category: Java Library
tag: Guice
date: 2015-12-08
title: Guice Scopes
---
默认的,Guice每次在`getInstance()`的时候都会返回一个新的对象.
```java
public class TestScopes {
	public static void main(String[] args) {
		AbstractModule module1 = new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class);
			}
		};
		Injector injector = Guice.createInjector(module1);
		A a = injector.getInstance(A.class);
		A b = injector.getInstance(A.class);
		System.out.println(a.equals(b));
	}
}
class A {
	public void print() {
		System.out.println("A");
	}
}
```
输出结果为false, 但是我们可以使用Singleton注解采用单例方式创建全局唯一的对象
```java
public class TestSingleton {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class).to(B.class);
			}
		});
		A b1 = injector.getInstance(A.class);
		A b2 = injector.getInstance(A.class);
		System.out.println(b1 == b2);
	}
}

interface A {
	void print();
}

@Singleton
class B implements A {
	@Override
	public void print() {
		System.out.println("B");
	}
}
```
输出结果为
```xml
true
```
我们使用`Singleton`注解可以得到一个全局唯一的B实例, 每次注解B实例时, 都是同一个实例.

另外,我们还可以在绑定的时候进行设置
```java
class ABCModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(A.class).to(B.class).in(Singleton.class);
	}
}
```


