category: Java
tag: guice
date: 2015-12-08
title: Guice Linked Bindings
---
```java
public class TestLinkedBindings {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new ABCModule());
		A c = injector.getInstance(C.class);
		c.print();
		A b = injector.getInstance(B.class);
		b.print();
		A a = injector.getInstance(A.class);
		a.print();
	}
}

interface A {
	public void print();
}

class B implements A {

	@Override
	public void print() {
		System.out.println("B");
	}
}

class C extends B {
	@Override
	public void print() {
		System.out.println("c");
	}
}

class ABCModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(A.class).to(B.class);
		bind(B.class).to(C.class);
	}
}
```
这段代码的最后调用结果都是
```
c
c
c
```
这就是Guice的Linked Bindings, 当binding形成一条链之后,会以最终的绑定为最终绑定

> 注意绑定关系必须是继承关系