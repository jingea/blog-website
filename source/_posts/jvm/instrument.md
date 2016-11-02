category: JVM
date: 2015-11-24
title: Instrumentation Premain
---
## Premain

使用 Instrumentation，开发者可以构建一个独立于应用程序的代理程序（Agent），用来监测和协助运行在 JVM 上的程序，甚至能够替换和修改某些类的定义。

Instrumentation提供了这样的功能：
* 获取某个对象的大小
* 热加载class文件
* 获取JVM信息

> 要知道一个对象所使用的内存量,需要将所有实例变量使用的内存和对象本身的开销(一般是16字节)相加.这些开销包括一个指向对象的类的引用,垃圾收集信息和同步信息.另外一般内存的使用会被填充为8字节的倍数.


### Premain
premain函数是JavaSE5中实现instrument的方式.

使用premain我们要自定义MANIFEST.MF文件, 定义Premain-Class
```java
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


#### 获取对象大小
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
```java
java -javaagent:../instrument/target/core-1.0-SNAPSHOT.jar -cp ./target/examples-1.0-SNAPSHOT.jar wang.ming15.instrument.examples.PrintObjectSize
```
然后就会获得对象的大小
```java
Hello world, App
123456789 对象大小: 24
```

#### 加载jar包
我们在Premain类中增加一个动态向系统cp加载jar的功能
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
```java
java -javaagent:../instrument/target/core-1.0-SNAPSHOT.jar -cp ./target/examples-1.0-SNAPSHOT.jar wang.ming15.instrument.examples.TestJarLoader D:/workspace/idea/instrument/trunk/print/target/print-1.0-SNAPSHOT.jar
```
结果输出为
```java
Now Time is Thu Dec 31 10:50:39 CST 2015

wang.ming15.instrument.print.Print  11
java.io.PrintStream  44
Now Time is Thu Dec 31 10:50:44 CST 2015

wang.ming15.instrument.print.Print  11
java.io.PrintStream  44
```

#### 热加载
* `redefineClasses()`使用新的字节码全部替换原先存在的Class字节码. (它并不会触发初始化操作, 也不会抛出初始化时的异常. 因此一些静态属性并不会被重新赋值)
* `retransformClasses()` 修改原先存在的Class字节码.

> 对于已经在栈帧中的字节码, 他们会继续执行下去, 但是当方法再次调用的时候,则会使用刚刚加载完成的新的字节码. 在重新加载类的时候, 该类已经实例化出的对象同时也不会受到影响.

该方法的操作过程是一个基于操作集合的, 也就是说在redefine的时候, 可能有A B俩个类都进行, 而且A依赖于B, 那么在redefine的时候这俩个操作是同时完成的, 类似于原子操作.

redefine 操作可以改变修改如下字节码
* 方法体
* 常量池
* 属性
但是redefine过程不能产生如下影响
* 对方法进行增加,删除,重命名的操作
* 对属性进行增加,删除,重命名的操作
* 不能修改方法签名以及修改继承关系.

在redefine过程中,一旦抛出异常, 那么此过程执已经redefine成功的class也会被会滚成原来的.

想使用这个功能我们需要在MANIFEST.MF文件中增加这样一行`Can-Redefine-Classes: true`, 然后我们在Premain中增加一个load方法, 用于重新加载某个文件夹下所有的文件
```java
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 实现服务器局部代码热加载功能
 *      目前只支持方法体代码热更以及对属性值的改变
 *      但是不能修改类的继承结构, 不能修改方法签名, 不能增加删除方法以及属性成员
 *
 *  使用方法
 *      java -javaagent:D:\premain\target\agent-1.0-SNAPSHOT.jar -cp .;./* MainServerStart
 *      只需要将该项目打包出来然后参照上面的例子进行代理处理就好了, 然后正常启动游戏服就好
 *
 */
public class Premain {
    private static final Logger logger = Logger.getLogger(Premain.class);

    private static Instrumentation instrumentation;
    public static void premain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
    }

	private static int classSize = 0;

    /**
     * 遍历某个目录加载所有的class文件
     * @param directionPath
     */
    public static void loadFromDirection(String directionPath) {
        loadFromDirection(new File(directionPath), "");
    }

    private static void loadFromDirection(File dir, String parantName) {
        try {
            for (File file : dir.listFiles()) {
                if (file.isFile() && !file.getName().endsWith(".class")) {
                    continue;
                }
                if (file.isDirectory()) {
                    String fileName = file.getName();
                    if (parantName != null && !parantName.equals("")) {
                        fileName = parantName + "." + fileName;
                    }
                    loadFromDirection(file, fileName);
                    continue;
                }
                try(InputStream input = new FileInputStream(file);) {
                    String fileName = file.getPath();
                    String className = findClassName(fileName);
                    if (parantName != null && !parantName.equals("")) {
                        className = parantName + "." + className;
                    }
                    redefineClassesFromBytes(input, className, null);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从jar包或者ZIP里加载所有的class文件
     * @param jarPath
     */
    public static void loadFromZipFile(String jarPath, String prfixName) {
		Class[] allLoadClasses = instrumentation.getAllLoadedClasses();
		Map<String, Class> allLoadClassesMap = new HashMap<>(classSize);
		for (Class loadedClass : allLoadClasses) {
			if (loadedClass.getName().startsWith(prfixName)) {
				allLoadClassesMap.put(loadedClass.getName(), loadedClass);
			}
		}
		// 加载的类我们不会主动去卸载它, 因此, 我们记录下来上次更新时的类的数量, 下次就根据这个数量直接分配, 避免动态扩容
		classSize = allLoadClassesMap.size();

		try(InputStream in = new BufferedInputStream(new FileInputStream(new File(jarPath)));
            ZipInputStream zin = new ZipInputStream(in);) {
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    // TODO 检查是否还有其他操作要做
                } else {
                    long size = ze.getSize();
                    if (size > 0) {
                        String fileName = ze.getName();
                        if (!fileName.endsWith(".class")) {
                            continue;
                        }
                        ZipFile zf = new ZipFile(jarPath);
                        InputStream input = zf.getInputStream(ze);
                        if (input == null) {
                            logger.error("Code Reload cant find file : " + fileName);
                            continue;
                        }
                        redefineClassesFromBytes(input, fileName, allLoadClassesMap);
                        input.close();
                        zf.close();
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static String findClassName(String fileName) {
        int idx = fileName.lastIndexOf("\\");
        fileName = fileName.substring(idx + 1);
        fileName = fileName.split("\\.class")[0];
        return fileName;
    }

    /* 使用instrumentation将读取的class byte数组加载进虚拟机
     */
    private static void redefineClassesFromBytes(InputStream input, String fileName, Map<String, Class> allLoadClassesMap) {
        try {
        	String className = getClassName(fileName);
            logger.info("Start Hot Reload Class : " + fileName + "  (" + className + ")");
	        byte[] bytes = new byte[input.available()];
    	    input.read(bytes);
			Class loadedClass = allLoadClassesMap.get(className);
			if (loadedClass != null) {
				instrumentation.redefineClasses(new ClassDefinition(loadedClass, bytes));
			}
        } catch (final Exception e) {
            logger.error("Code Reload Failed : " + fileName, e);
        } catch (Error error) {
			logger.error("Code Reload Failed : " + fileName, error);
		}
    }

    private static String getClassName(String fileName) {
        fileName = fileName.split("\\.class")[0];
        fileName = fileName.replace("\\\\", ".");
        fileName = fileName.replace("/", ".");
        return fileName;
    }
```
然后我们写一个测试类
```java
import java.util.concurrent.TimeUnit;

public class TestReload {

	public static void main(String[] args) throws InterruptedException {
        fromDirection();
    }

    public static void fromJar() throws InterruptedException{
        for (int i = 0; i < 300; i++) {
            Premain.loadFromJarFile("D:\\ming\\test\\target\\test-1.0-SNAPSHOT.jar");
            TestReload.printTime();
            new TestReload().printNewTime();
            TimeUnit.SECONDS.sleep(5);
        }
    }

    public static void fromDirection() throws InterruptedException {
        for (int i = 0; i < 300; i++) {
            Premain.loadFromDirection("D:\\ming\\test\\target\\classes");
            TestReload.printTime();
            new TestReload().printNewTime();
            TimeUnit.SECONDS.sleep(5);
        }
    }

    public static void printTime() {
        System.out.println(2);
    }

    public void printNewTime() {
        System.out.println(2);
        System.out.println(id);
    }

    public int id = 2;
}
```
我们不断地修改printTime()和printNewTime()以及Id的值, 最后成功输出
```java
1
1
1
1
1
1
1
1
1
2
2
2
```

> 在上面的实现中我分别实现了从目录和jar包对class文件进行热加载

下面我们测试一下,如果增加了属性和方法成员, 看看有什么变化(下面只列出了TestReload.java的新增以及修改部分)
```java
public class TestReload {

	...

        public void printNewTime() {
        System.out.println(id);
        printName();
    }

    public int id = 2;

    public String name = "abc";

    public void printName() {
        System.out.println(name);
    }
}
```
当我们再次重新加载的时候就会抛出异常
```xml
D:\ming\test\target>java -javaagent:D:\premain\target\agent-1.0-SNAPSHOT.jar -cp .;./* TestReload
1
1
2
2
2
2
java.lang.UnsupportedOperationException: class redefinition failed: attempted to change the schema (add/remove fields)
	at sun.instrument.InstrumentationImpl.redefineClasses0(Native Method)
	at sun.instrument.InstrumentationImpl.redefineClasses(Unknown Source)
	at Premain.redefineClassesFromBytes(Premain.java:44)
	at Premain.loadFromDirection(Premain.java:24)
	at TestReload.fromDirection(TestReload.java:19)
	at TestReload.main(TestReload.java:6)
2
java.lang.UnsupportedOperationException: class redefinition failed: attempted to change the schema (add/remove fields)
	at sun.instrument.InstrumentationImpl.redefineClasses0(Native Method)
	at sun.instrument.InstrumentationImpl.redefineClasses(Unknown Source)
	at Premain.redefineClassesFromBytes(Premain.java:44)
	at Premain.loadFromDirection(Premain.java:24)
	at TestReload.fromDirection(TestReload.java:19)
	at TestReload.main(TestReload.java:6)
2
```

完整项目[JVM-reload](https://github.com/ming15/JVM-reload)

## Agentmain
在 Java SE 5 中premain 所作的 Instrumentation 也仅限与 main 函数执行前，这样的方式存在一定的局限性。Java SE 6 针对这种状况做出了改进，开发者可以在 main 函数开始执行以后，再启动自己的 Instrumentation 程序。在 Java SE 6 的 Instrumentation 当中，有一个跟 premain“并驾齐驱”的“agentmain”方法，可以在 main 函数开始运行之后再运行。

首先我们还是需要修改MANIFEST.MF文件, 在其中添加
```java
Manifest-Version: 1.0
Agent-Class: AgentMain
Can-Redefine-Classes: true

```

然后我们写一个代理类
```java
import javax.xml.transform.Transformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AgentMain {

    public static void agentmain(String agentArgs, Instrumentation inst)
            throws ClassNotFoundException, UnmodifiableClassException,
            InterruptedException {
        for (Class clazz : inst.getAllLoadedClasses()) {
            System.out.println("Loaded Class : " + clazz.getName());
        }
        Printer.printTime();
    }
}

class Printer {

    public static void printTime() {
        System.out.println("now is " + new Date());
    }
}
```
然后写一个启动类
```java
import com.sun.tools.attach.VirtualMachine;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

public class AgentLoader {

    public static void main(String[] args) throws Exception {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        System.out.println(pid);
        VirtualMachine vm = VirtualMachine.attach(pid);
        for (int i = 0; i < 100; i++) {
//            vm.loadAgent("D:\\ming\\test\\target\\test-1.0-SNAPSHOT.jar");
            vm.loadAgentPath("D:\\ming\\test\\target\\test-1.0-SNAPSHOT.jar");
            System.out.println("Load Agent Over!!!");
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
```
打包后, 执行命令
```java
java -cp .;./* AgentLoader
```
