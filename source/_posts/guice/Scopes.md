category: guice
date: 2015-12-08
title: Guice 
---
默认的,Guice每次在`getInstance()`的时候都会返回一个新的对象.

## Singleton
```java
public class TestLinkedBindings {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new ABCModule());
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
```
我们使用`Singleton`注解可以得到一个单例类

另外,我们还可以在绑定的时候进行设置
```java
class ABCModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(A.class).to(B.class).in(Singleton.class);
	}
}
```

## web Scope
Guice还对web应用提供了其他俩种socpe
* SessionScoped
* RequestScoped
