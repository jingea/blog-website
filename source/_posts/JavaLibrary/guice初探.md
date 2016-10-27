category: Java Library
tag: Guice
date: 2015-12-08
title: Guice 初探
---
Google Guice 是一个轻量级的依赖注入框架

在Guice的依赖注入中我们使用如下API进行注入
* `Binder`
* `Injector`
* `Module`
* `Guice`
直接看例子
```java
public class CarModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(Car.class).to(Benci.class);
	}
}

public interface Car {
	public void run();
}

public class Benci implements Car {
	@Override
	public void run() {
		System.out.println("Benci Run");
	}
}
```
测试代码
```java
Injector injector = Guice.createInjector(new CarModule());
Car benci = injector.getInstance(Car.class);
benci.run();
```

## ImplementedBy
```java
public class LunYu implements Book {
	@Override
	public String content() {
		return "Lunyu";
	}
}

@ImplementedBy(LunYu.class)
public interface Book {

	public String content();
}

public class ReadBook {

	public void readLunyu() {
		System.out.println(book.content());
	}

	@Inject
	private Book book;
}
```
测试代码
```java
Injector intjector = Guice.createInjector();
Book lunyu = intjector.getInstance(Book.class);
System.out.println(lunyu.content());
```

## Inject
```java
public class Man implements People {
	@Override
	public String name() {
		return "Tom";
	}
}

public interface People {

	public String name();
}

public class PeopleModule implements Module {
	@Override
	public void configure(Binder binder) {
		binder.bind(People.class).to(Man.class);
	}
}

public class PrintName {

	public void print() {
		System.out.println(man.name());
	}

	@Inject
	private People man;
}
```
测试代码
```java
Injector intjector = Guice.createInjector(new PeopleModule());
PrintName pn = intjector.getInstance(PrintName.class);
pn.print();
```
