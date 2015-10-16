category: java基础
date: 2015-06-08
title: java泛型
---

# 泛型
泛型（Generic type 或者 generics）是对 Java 语言的类型系统的一种扩展，以支持创建可以按类型进行参数化的类.

## 泛型类
我们定义一个简单的泛型类, `T`称为泛型参数, `G`被称为泛型化了
```java
class G<T> {

}
```
接着我们在内部定义一个泛型变量
```
class G<T> {
	T t;
}
```
然后我们再添加一个泛型方法泛型方法
```java
class G<T> {
	T t;

	public void setValue(T t) {
		this.t = t;
	}
}
```
下来我们来使用一下这个泛型类
```java
G<String> g = new G<>();
g.setValue("value");
```

### 泛型参数

#### extends
```java
class G<T extends SuperParam> {
	T t;
	public void setValue(T t) {
		this.t = t;
	}
}

class SuperParam {}

class Param extends SuperParam {}
```
我们使用`extends`关键字定义了一个泛型类, 接下来我们实例化这个泛型类
```java
G<SuperParam> g = new G<>();	// compile ok
g.setValue(new SuperParam());

G<Param> g1 = new G<>();		// compile ok
g.setValue(new Param());

G<String> g2 = new G<>();		// compile error
g.setValue(new String());
```
当我们将`String`作为泛型参数的时候会提示`Type parameter 'java.lang.String' is not within its bound; should extends 'SuperParam'`. 很显然现在的泛型参数要继承自`SuperParam`.

#### super
```java

```

#### 通配符
```java

```

#### 自定义泛型参数
```java

```

#### 在泛型方法中的使用
```java

```

### 泛型类的继承关系
```java

```

#### 在泛型方法中的使用
```java

```

## 泛型和数组
```java

```


## 泛型在JVM中