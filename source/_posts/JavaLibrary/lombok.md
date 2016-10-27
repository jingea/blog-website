category: Java Library
date: 2015-12-09
title: Lombok初探
---
参考文档[lombok](https://projectlombok.org/features/index.html)
在使用lombok的时候, 我们需要在IDE上安装上lombok插件以及引用相关的jar包依赖
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.16.6</version>
</dependency>
```

lombok其实帮我们做的事情是, 在编译java源码的时候,会根据相关注解编译出相关的代码. 例如如果我们使用`@Setter`注解的话,那么在编译的会在class文件中添加相应的setter代码

## val
下来我们看一个小例子
```java
val example = new ArrayList<String>();
example.add("Hello, World!");
val foo = example.get(0);
System.out.println(foo.equals("Hello, World!"));
```
上面这个例子我们使用val代替了`ArrayList`类型. 插件会只能地帮我们识别出这个真正的类型是什么.

> 注意, `new ArrayList<String>()`菱形符里应该指定类型,否则我们既可以在example上添加String又可以添加int,会带来类型上的不安全

## NonNull
```java
public class TestNonNull {
	public static void main(String[] args) {
		print(null);
	}

	public static void print(@NonNull String content) {
		System.out.println(content);
	}
}
```
这个注解很简单就是检查参数非null,如果传进的参数为null的就抛出
```java
Exception in thread "main" java.lang.NullPointerException: content
	at Lombok.TestNonNull.print(TestNonNull.java:13)
	at Lombok.TestNonNull.main(TestNonNull.java:10)
```

## Cleanup
这个注解会在资源在其作用域离开之后,自动将资源关闭掉
```java
@Cleanup BufferedReader fileReader1 = new BufferedReader(new FileReader("D://hazelcast-documentation-3.5.3.pdf"));
```

## Getter Setter
```java
public class TestGetterSetter {
	public static void main(String[] args) {
		A a = new A();
		a.getA2();
		a.getA3();
		a.setA1("a1");
		a.setA3("a3");
	}
}

class A {
	@Setter private String a1;
	@Getter private String a2;
	@Setter @Getter private String a3;

}
```
`@Getter`和`@Setter`注解有一点需要说明的是,我们可以指定他们的访问级别
```java
@Setter(value = AccessLevel.MODULE) private String a1;
```
可设置的级别有:
* PUBLIC,
* MODULE,
* PROTECTED,
* PACKAGE,
* PRIVATE,
* NONE;

## ToString
`@ToString`注解既可以用在类上可以用在方法上, 这个注解会修改类的`toString()`方法

```java
public class TestToString {
	public static void main(String[] args) {
		C c = new C();
		c.c1 = "c_1";
		c.c2 = "c_2";
		c.b1 = "b_1";
		c.b2 = "b_2";
		System.out.println(c);

	}
}

// toString()中不包含b1和b2这俩个属性
@ToString(exclude={"b1", "b2"})
class B {
	public String b1;
	public String b2;
	public String b3;
}

// 调用父类的toString和在输出时包含字段名称
@ToString(callSuper=true, includeFieldNames = true)
class C extends B {
	public String c1;
	public String c2;
}
```
上面代码输出为
```java
C(super=B(b2=null), c1=null, c2=null)
B(b2=null)
C(super=B(b2=b_2), c1=c_1, c2=c_2)
```

## EqualsAndHashCode
这个注解人如其名, 会为我们生成俩个规范的`equals()`和`hashCode()`方法, 至于什么是规范的,参考Effective java这本书
```java
@EqualsAndHashCode
class D {

}
```

## Data
`@Data`注解是`@ToString, @EqualsAndHashCode, @Getter / @Setter and @RequiredArgsConstructor `这些注解的一个集合. 它会默认地为我们使用那些注解.
```java
@Data class Simple {
	private int id;

	public void init() {
		setId(123);
	}
}
```

## Value
`@Value`是`@Data`注解的一个变种, 它是在`@Data`注解的基础将,将类成为不可变的.
```java
@Value class E {

}
```

## Builder
`@Builder`将类修改成Builder模式(同样参考Effective Java).

```java
public class TestBuilder {
	public static void main(String[] args) {
		F f = F.builder().f1("fff").fAB(123).build();
		System.out.println(f.getF1());
		System.out.println(f.getFABs().size());

		G g = G.GBuilder().buildG();

	}
}

@Builder class F {

	@Getter private String f1;

	@Singular @Getter private Set<Integer> fABs;
	@Singular @Getter private Set<Integer> fAsBs;
}

@Builder(builderClassName = "GBuilder", buildMethodName = "buildG", builderMethodName = "GBuilder")
class G {
	public static void printG() {
		System.out.println("GGG");
	}
}
```
使用`@Singular`注解的集合属性名必须使用`s`结尾, lombok会将属性名结尾的`s`去掉,剩余的名字会作为方法名, 向这个集合中添加元素
