category: jvm
tag: instrument, jvm工具
date: 2015-11-24
title: instrument 初探
---
使用 Instrumentation，开发者可以构建一个独立于应用程序的代理程序（Agent），用来监测和协助运行在 JVM 上的程序，甚至能够替换和修改某些类的定义。

Instrumentation提供了这样的功能：
* 获取某个对象的大小
* 热加载class文件
* 获取JVM信息

> 要知道一个对象所使用的内存量,需要将所有实例变量使用的内存和对象本身的开销(一般是16字节)相加.这些开销包括一个指向对象的类的引用,垃圾收集信息和同步信息.另外一般内存的使用会被填充为8字节的倍数.


## Premain
premain函数是JavaSE5中实现instrument的方式.

使用premain我们要自定义MANIFEST.MF文件, 定义Premain-Class
```
Manifest-Version: 1.0
Premain-Class: wang.ming15.instrument.core.Premain
```
然后我们在maven文件中输出该文件
```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <configuration>
                <archive>
                    <manifestFile>
                        src/main/resources/META-INF/MANIFEST.MF
                    </manifestFile>
                    <manifest>
                        <addClasspath>true</addClasspath>
                        <classpathPrefix>lib/</classpathPrefix>
                        <mainClass>
                        </mainClass>
                    </manifest>
                </archive>
            </configuration>
        </plugin>
    </plugins>
</build>
```


### 获取对象大小
首先我们要写一个代理文件出来(该文件放在`core-1.0-SNAPSHOT.jar`中)
```java
public class Premain {

	private static Instrumentation instrumentation;

	public static void premain(String agentArgs, Instrumentation inst) {
		instrumentation = inst;
	};

	public static Instrumentation getInstrumentation() {
		return instrumentation;
	}
}
```
然后在自己的应用程序中引用该文件(在`examples-1.0-SNAPSHOT.jar`中)
```java
public class PrintObjectSize {

	public static void main(String[] args) {
		System.out.println("Hello world, App");

		objectSize();
	}

	public static void objectSize() {
		Instrumentation inst = Premain.getInstrumentation();
		String str = "123456789";
		long size = inst.getObjectSize(str);
		System.out.println(str + " 对象大小: " + size);
	}
}
```
然后执行命令
```
java -javaagent:../instrument/target/core-1.0-SNAPSHOT.jar -cp ./target/examples-1.0-SNAPSHOT.jar wang.ming15.instrument.examples.PrintObjectSize
```
然后就会获得对象的大小
```
Hello world, App
123456789 对象大小: 24
```

### 加载jar包
我们在Premain类中增加一个加载jar的功能
```java
public static void appendJarToSystemClassLoader(String path) {
	JarFile jarFile = null;
	try {
		jarFile = new JarFile(path);
	} catch (IOException e) {
		e.printStackTrace();
	}
	instrumentation.appendToSystemClassLoaderSearch(jarFile);

}

public static void appendJarToBootstrapClassLoader(Instrumentation inst, String path) {
	JarFile jarFile = null;
	try {
		jarFile = new JarFile(path);
	} catch (IOException e) {
		e.printStackTrace();
	}
	inst.appendToBootstrapClassLoaderSearch(jarFile);

}
```
然后我们写一个测试类
```java
public class TestJarLoader {

	public static void main(String[] args) {
		for (int i = 0; i < 120; i++) {
			Premain.appendJarToSystemClassLoader(args[0]);
			Print.print();

			Stream.of(Premain.getInstrumentation().getAllLoadedClasses())
					.filter(clazz -> clazz.getName().contains("Print"))
					.forEach(aClass -> System.out.println(aClass.getName() + "  " + aClass.getMethods().length));

			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
```
然后执行命令
```
java -javaagent:../instrument/target/core-1.0-SNAPSHOT.jar -cp ./target/examples-1.0-SNAPSHOT.jar wang.ming15.instrument.examples.TestJarLoader D:/workspace/idea/instrument/trunk/print/target/print-1.0-SNAPSHOT.jar
```
结果输出为
```
Now Time is Thu Dec 31 10:50:39 CST 2015

wang.ming15.instrument.print.Print  11
java.io.PrintStream  44
Now Time is Thu Dec 31 10:50:44 CST 2015

wang.ming15.instrument.print.Print  11
java.io.PrintStream  44
```

### 重新加载类
我们使用`redefineClasses()`可以使用提供的字节码重新定义Class. 这个方法会使用新的字节码全部替换原先存在的Class字节码. 而如果想要修改原先存在的Class字节码应该使用`retransformClasses()`方法.

对于已经在栈帧中的字节码, 他们会继续执行下去, 但是当方法再次调用的时候,则会使用刚刚加载完成的新的字节码.

同时需要注意的是`redefineClasses()`并不会触发初始化操作, 也不会抛出初始化时的异常. 因此一些静态属性并不会被重新赋值

在重新加载类的时候, 该类已经实例化出的对象同时也不会受到影响.

该方法的操作过程是一个基于操作集合的, 也就是说在redefine的时候, 可能有A B俩个类都进行, 而且A依赖于B, 那么在redefine的时候这俩个操作是同时完成的, 类似于原子操作.

还需要指明的是, redefine 操作虽然可以改变方法体, 常量池以及属性, 但是redefine过程肯定不能对属性或者方法进行增加,删除,重命名的操作, 也不能修改方法签名以及修改继承关系.

在redefine过程中,一旦抛出异常, 那么此过程执已经redefine成功的class也会被会滚成原来的.

想使用这个功能我们需要在MANIFEST.MF文件中增加这样一行`Can-Redefine-Classes: true`, 然后我们在Premain中增加一个redefine方法
```java
public static void redefineClasses(Class clazz, String path) {
	if (!instrumentation.isRedefineClassesSupported()) {
		throw new RuntimeException();
	}
	try {
//		InputStream input = Premain.class.getClassLoader().getResourceAsStream(path);
		InputStream input = new FileInputStream(path);
		byte[] bytes = new byte[input.available()];
		input.read(bytes);
		instrumentation.redefineClasses(new ClassDefinition(clazz, bytes));
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (UnmodifiableClassException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
```
然后我们写一个测试类
```
public class TestClassLoader {

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			Test.print();
			TimeUnit.SECONDS.sleep(5);
			Premain.redefineClasses(Test.class, "D://Test.class");
		}
	}
}
```
最后成功输出
```
I am a big T
I am a big T
I am a big T
I am a big T
I am a big T
I am a big T
I am a big T
I am a big T ok
I am a big T ok
```

## Agentmain

在 Java SE 5 中premain 所作的 Instrumentation 也仅限与 main 函数执行前，这样的方式存在一定的局限性。Java SE 6 针对这种状况做出了改进，开发者可以在 main 函数开始执行以后，再启动自己的 Instrumentation 程序。在 Java SE 6 的 Instrumentation 当中，有一个跟 premain“并驾齐驱”的“agentmain”方法，可以在 main 函数开始运行之后再运行。

首先我们还是需要修改MANIFEST.MF文件, 在其中添加
```
Agent-Class: wang.ming15.instrument.core.Agentmain
```

## 获取对象大小
同样我们还是先输出一下对象的大小
```

```
