category: Java
date: 2015-11-19
title: Java 常用技能
---

## 控制台乱码
在windows系统里,我的cmd控制台的代码页是`65001(UTF)`,但是当我在指向java命令时却会发生乱码现象,只需要指向`chcp 936`这个命令,改变一下代码页就好了

## java命令
`java  -jar ./tools-1.0-SNAPSHOT.jar` 从某个jar运行, mainfest文件必须指定MainClass属性,如果不指定的话,在运行`java`命令的时候就会产生 xxx.jar中没有主清单属性

`java  -jar ./ App` 从指定的classpath下所有的jar中,寻找App主类运行

## 获取周数
通过`Calendar`我们可以知道某个日期处于一年中第几周
```java
Calendar calendar = Calendar.getInstance();
calendar.setTime(new Date());
System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
```

## 在Jar包读取文件
我们使用Maven构建一个工程, 然后在资源目录(resources)下放置一个文件input.file, 然后我们写一个测试类
```java
public class Test {

    public static void main(String[] args) throws InterruptedException, IOException {
        InputStream in = Test.class.getResourceAsStream("input.file");
        BufferedReader reader = new BufferedReader((new InputStreamReader(in)));
        System.out.println(reader.readLine());
    }
}
```
在Idea里运行该程序, 可以成功看到input.file文件里的hi, how are you?的输出. 这是因为idea将resources设置在了cleasspath里, 而类加载器的`getResourceAsStream()`就是从classpath中进行文件查找, 因此可以找到的.

而当我们将测试工程打包, 然后在target下执行命令
```java
java -cp .;./* Test
```
同样可以看到输出, 这是因为`getResourceAsStream()`会在jar包内部进行文件查找

> 注意: 如果想要在jar内加载文件的话, 只能使用类加载器的`getResourceAsStream()`方法

## 便利jar包中文件
我们知道我们可以使用`ClassLoader`的`getResourceAsStream()`读取jar包中的文件, 那么如何知道jar包中有哪些文件, 也就是如何便利jar包中的文件呢？
```java
public class Test {

	public static void main(String[] arg) throws Exception {
		JarFile jar = new JarFile("D:\\premain\\target\\agent-1.0-SNAPSHOT.jar");
		Enumeration<JarEntry> jarEntrys = jar.entries();
		while (jarEntrys.hasMoreElements()) {
			JarEntry jarEntry = jarEntrys.nextElement();
			System.out.println(jarEntry.getName());
		}
	}
}
```

## goto
```java
public class TestBreak {

	public static void main(String[] args) {
		step1:
		for (int i = 0; i < 3; i++) {
			System.out.println("I is " + i);
			for (int j = 0; j < 3; j++) {
				if (j == i) {
					System.out.println("	J is " + j);
					continue step1;
				}
			}
		}
	}
}
```
