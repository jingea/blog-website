category: Java
tag: JVM
date: 2016-01-29
title: 使用Classloader加载类
---

类加载器不单单是用于实现类的加载动作, 对于任意一个类,都需要由加载它的类加载器和类本身一同确立其在java虚拟机中的唯一性.换句话说:比较俩个类是否相等,只有在这俩个类是由同一个类加载器加载的前提下才有意义. 否则即使来自同一个源文件,只要加载它们的类加载器不同,这俩个类就必定不相等.

> 判断俩个类相等可以通过下面方法: `Class`对象的`equals()`方法, `isAssignbleFrom()`方法, `isInstance()`方法的返回结果, 也包括使用`instanceof`关键字做对象所属关系判断等. 不同的类加载器对`instanceof`关键字运算结果的影响


## 双亲委派模型

![ClassLoader的体系架构](https://raw.githubusercontent.com/ming15/blog-website/images/jvm/ClassLoader%E7%9A%84%E4%BD%93%E7%B3%BB%E6%9E%B6%E6%9E%84.png)
从JVM来角度讲, 只存在俩种不同的类加载器:
* 启动类加载器: 使用C++语言实践,是虚拟机自身的一部分. 
* 其他类加载器: 这些类加载器都由java语言实现,独立于虚拟机外部,并且全部都继承自抽象类:`java.lang.ClassLoader`

系统提供的类加载器
* 启动类加载器 : 这个类加载器负责将`<JAVA_HOME>\lib`目录中的,或者`-Xbootclasspath`参数所指定的路径中的,并且是虚拟机识别的(仅按照文件名识别,如rt,jar,名字不符合的类库即使放在lib目录里也不会被加载)类库加载到虚拟机内存中,启动类加载器无法被java程序直接使用.
* 扩展类加载器 : 这个类加载器由`sun.misc.Launcher$ExtClassLoader`实现,负责加载`<JAVA_HOME>\lib\ext`目录中的,或者被`java.ext.dirs`系统变量所指定的路径中的所有类库, 开发者可以直接使用扩展类加载器.
* 应用程序加载器 : 这个类加载器由`sun.misc.Launcher$AppClassLoader`来实现. 由于类加载器是`ClassLoader`中`getSystemClassLoader()`方法的返回值,所以一般也称它为系统类加载器. 它负责加载用户类路径(ClassPath)上所指定的类库,开发者可以直接使用这个类加载器,如果应用程序中没有自定义过自己的类加载器,一般情况下就是程序中默认的类加载器.


类加载器收到类加载请求,它不会自己去尝试加载这个类,而把这个请求委派给父类加载器去完成,每一个层次的类加载都是如此,因此所有的类加请求最终都应该传送到顶层的启动类加载器中,只有当父加载器反馈自己无法完成这个加载请求(它的搜索范围中没有找到所需的类)时,子类加载器才会尝试自己去加载.

```java
 protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // 第一步检查被加载的类是否已经被加载进入了虚拟机
            Class<?> c = findLoadedClass(name);
            if (c == null) {
				// 如果没有被加载进虚拟机中则进行加载
                long t0 = System.nanoTime();
                try {
					// 优先从父类加载器中进行加载, 所有的类加请求最终都应该传送到顶层的启动类加载器中
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
					// 
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }
				
                if (c == null) {
                    // 父类加载器找不到则调用自己的findClass(name)找到类然后进行加载
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
```

> 使用双亲委派模型来组织类加载之间的关系, 可以确保我们自己JVM的安全. 因为当我们自己写一个 `java.lang.Object`, 这个类虽然能够被正常编译, 但是它永远不会被加载器虚拟机中, 因为这个类会在启动类加载器中完成了加载.

在刚才的`loadClass()`方法中我们看到最终我们自己实现类加载的逻辑是在`findClass()`中进行的, 这是为了向前兼容,JDK1.2之后添加的方法.JDK1.2之后已不提倡用户再去覆盖`loadClass()`方法,而在`loadClass()`方法的逻辑里如果父类加载失败,则会调用自己的`findClass()`方法来完成加载,这样就可以保证新写出来的类加载器是符合双亲委派规则的.

> 为了解决各个类加载器的基础类调用用户代码, java设计团队引入了这样一个设计:线程上下文类加载器,这个类加载器可以通过`java.lang.Thread`类的`setContextClassLoaser()`方法进行设置,如果创建线程时还未设置,它将会从父线程中继承一个:如果在应用程序的全局范围内都没有设置过,那么这个类加载器默认就是应用程序类加载器.有了线程上下文类加载器,JNDI服务使用这个线程上下文类加载器去加载所需要的SPI代码,也就是父类加载器请求子类加载器去完成类加载的动作,这种行为实际就是打通了双亲委派模型的层次结构来逆向使用类加载器,已经违背了双亲委派模型的一般性原则.

上面所说只完成了类加载的动作, 但是如果我们想要实现热更代码的这种功能的话,就不能单纯依赖重写`findClass(name)`了，而是要重写`loadClass(String name)`了，这是因为在`ClassLoader`中的`loadClass(String name)`方法当发现已经加载过的类就不会再重新加载了
```java
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Random;

import static org.objectweb.asm.Opcodes.*;

public class TestClassLoader {

	public static void main(String[] arg) throws Exception {

		URL url = new File(".").toURL();
		for (int i = 0; i< 5; i++) {
			MyClassLoader myLoader = new MyClassLoader(new URL[]{url});
			Class<?> obj = myLoader.loadClass("Mesurable");
			for (Field field : obj.getFields()) {
				System.out.println(field.getName());
			}
		}
	}
}

class MyClassLoader extends URLClassLoader {

	public MyClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> loadClass = null;
		if (name.contains("java.lang.Object")) {
			// 因为我们的父类是java.lang.Object, 因此我们要调用父类加载器进行加载
			loadClass = super.loadClass(name);
		} else {
			loadClass = findLoadedClass(name);
			if (loadClass != null) {
				return loadClass;
			}
			byte[] bytes = generateClass();
			loadClass = defineClass(name, bytes, 0, bytes.length);
		}
		return loadClass;
	}

	private byte[] generateClass() {
		ClassWriter cw = new ClassWriter(0);
		cw.visit(V1_8,											// 指定class文件版本号, 我们将其设置为java8
				ACC_PUBLIC,	// 设置接口的修饰符
				"Mesurable",								// 我们设置classname, 需要在这里指定全限定名
				null,											// 设置泛型信息, 因为我们的接口是非泛化的, 因此我们将其设置为null
				"java/lang/Object",							// 设置父类, 同时需要设定全限定名
				null);			// 设置接口, 同样需要设置全限定名

		cw.visitField(
				ACC_PUBLIC,	// 设置字段的修饰符
				"LESS__" + random.nextInt(100),										// 设置字段名
				"I",										// 设置字段类型
				null,										// 设置泛型信息
				new Long(-1))							// 设置字面量值.
				.visitEnd();

		cw.visitEnd();
		return cw.toByteArray();
	}

	private Random random = new Random();
}

```

> `URLClassLoader`根据`URL`指定的路径从`JAR`文件或者目录里加载`class`文件或者其他资源文件. 如果`URL`以`/`结束,就表示到某个目录里进行加载. 否则就表示到某个`JAR`文件里进行加载. 线程里用于创建`URLClassLoader`实例的`AccessControlContext`会在加载类文件以及资源文件时使用到. `URLClassLoader`实例创建好之后会根据默认的授权权限依据指定的`URL`来进行加载类.











