category: Java Library
tag: Guice
date: 2015-12-08
title: Guice 笔记
---
## 示例
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

## Scopes
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


## 注入

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
但是我们应该避免静态属性注入

## 绑定


### 单绑定
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

> 参考@ImplementedBy or @ProvidedBy

### 链式绑定
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

### 命名绑定
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

### 多模块绑定
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

### Provides绑定
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