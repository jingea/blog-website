category: guice
date: 2015-12-08
title: Guice 多模块分开绑定
---
我们可以在不同的模块里绑定实现不同的绑定
```java
public class TestMultipleModules {
	public static void main(String[] args) {
		AbstractModule module1 = new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class).to(B.class);
			}
		};

		AbstractModule module2 = new AbstractModule() {
			@Override
			protected void configure() {
				bind(B.class).to(C.class);
			}
		};
		Injector injector = Guice.createInjector(module1, module2);
		A a = injector.getInstance(A.class);
		B b = injector.getInstance(B.class);
		a.print();
		b.print();
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
class C extends B {
	@Override
	public void print() {
		System.out.println("C");
	}
}
class Print {
	@Inject
	private A a;

	public void print() {
		a.print();
	}
}
```
结果为
```xml
C
C
```
> module1和module2里对A只能进行相同的绑定,也就是说即使在不同的module里也不能即A绑定到B又绑定到C