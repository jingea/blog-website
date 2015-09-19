title: java lambda
---

# 函数接口

## 函数接口定义
函数接口只是一个抽象方法的接口,用作lambda表达式类型.

注意, 上面这个定义有三个需要注意的地方
1. 函数接口是一个接口
2. 函数接口有且只有一个抽象方法
3. 函数接口用作lambda表达式类型

## 函数接口示例:
```java
// 定义一个非泛型没有返回值没有参数的函数接口
interface Run1 {
	public void runFast();
}
// 定义一个非泛型没有返回值有参数的函数接口
interface Run2 {
	public void runFast(int seconds);
}
// 定义一个非泛型有返回值有参数的函数接口
interface Run3 {
	public int runFast(int seconds);
}
// 定义一个泛型有返回值有参数的函数接口
interface Run4<T> {
	public int runFast(T t, int seconds);
}
```

# lambda表达式

## lambda表达式定义
接下来我们根据上面定义的函数接口来定义一下lambda表达式
```java
// 不带参数的版本
Run1 run1 = () -> {
	System.out.println("I am running");
};

// 参数要指定
Run2 run2 = seconds -> {
	System.out.println("I am running " + seconds + " seconds");
};

// 下面这个版本就必须要有个返回值了
Run3 run3 = seconds -> {
	System.out.println("I am running");
	return 0;
};

// 我们在下面的版本中指定了它的泛型信息
Run4<String> run4 = (name, seconds) -> {
	System.out.println(name + " is running");
	return 0;
};
```

## lambda表达式使用
接下来我们使用上面定义的lambda表达式
```java
run1.runFast();
-> I am running

run2.runFast(10);
-> I am running 10 seconds

int result = run3.runFast(10);
-> I am running

run4.runFast("小狗", 10); 小狗 is running
-> 
```

### 注意
我们引用lambda表达式外部的一个变量
```java
String name = "sam";
Run1 run1 = () -> {
	System.out.println(name + " am running");
};
```
编译运行通过没有问题,但是如果我们将name在lambda表达式内部重新赋值的话
```java
String name = "sam";
Run1 run1 = () -> {
	name = "";
	System.out.println(name + " am running");
};
```		
会提示`variable used in lambda expression shouble be final`, 这说明lambda其实内部引用的是值而不是变量.好,接下来我们换种方式再次验证一下我们的结果：
```java
String name = "sam";
name = "Jams";
Run1 run1 = () -> {
	System.out.println(name + " am running");
};
```
同样的产生了编译错误.

### java中重要的函数接口
* `Predicate<T>`
* `Consumer<T>`
* `Supplier<T>`
* `UnaryOperator<T>`
* `BinaryOperator<T>`