category: guice
date: 2015-12-08
title: Guice Multiple Modules
---

```java
public class TestLinkedBindings {
	public static void main(String[] args) {
		AbstractModule module1 = new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class).to(B.class).in(Singleton.class);
			}
		};
		AbstractModule module2 = new AbstractModule() {
			@Override
			protected void configure() {
				bind(A.class).to(B.class).in(Singleton.class);
			}
		};
		Injector injector = Guice.createInjector(module1, module2);
		A b1 = injector.getInstance(A.class);
		A b2 = injector.getInstance(A.class);
		System.out.println(b1 == b2);
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
	@Inject
	private A a;

	public void print() {
		a.print();
	}
}
```
module1和module2里对A只能进行相同的绑定,也就是说即使在不同的module里也不能即A绑定到B又绑定到C