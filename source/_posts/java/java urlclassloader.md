category: java
title: URLClassLoader
---

# URLClassLoader

## 类说明
该类加载器根据`URL`指定的路径从`JAR`文件或者目录里记载`class`文件或者其他资源文件. 如果`URL`以`/`结束,就表示到某个目录里进行加载. 否则就表示到某个`JAR`文件里进行加载. 线程里用于创建`URLClassLoader`实例的`AccessControlContext`会在加载类文件以及资源文件时使用到. `URLClassLoader`实例创建好之后会根据默认的授权权限依据指定的`URL`来进行加载类.

## loadClass()方法
`URLClassLoader`实际调用的是父类的`loadClass()`方法.

加载类时会根据指定的二进制名称进行加载,加载顺序如下:
1. 调用`findLoadedClass(String)`方法查看要加载的类是否已经被加载进来了.
2. 调用父类加载器的`loadClass(String)`方法. 如果不存在父类加载器, 则使用内建于JVM里的类加载器.
3. 调用`findClass(String)`方法找到类.

如果通过上述步骤,我们找到了要加载的类,并且类文件合法,那么就会接着调用`resolveClass(Class)`方法, 解析加载进来的类.

如果`ClassLoader`子类重写了`findClass(String)`那么会调用子类的方法.
 
接下来我们来看一下这个方法实现:
```java
 protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // 首先查看目标类是否已经加载进了虚拟机
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
				// 发现没有加载进虚拟机,然后根据上面所描述的顺序先由父类加载器和虚拟机加载器尝试加载
                try {
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {}

				// 到目前为止父类加载器和虚拟机加载器都没有加载到我们需要的类,那么就有我们自己的加载器进行加载
                if (c == null) {
                    long t1 = System.nanoTime();
					// 调用子类的类加载实现,这里也就是UrlClassLoader的实现
                    c = findClass(name);

                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
				// 在UrlClassLoader实现中这个调用为false,也就是由JVM来实现解析
                resolveClass(c);
            }
            return c;
        }
    }
```
但是在实际测试中,会有`parent.loadClass(name, false);`找到类,而不是根据自己的`findClass()`实现找到类.

`parent`实际上是`Launcher#AppClassLoader`的实例. 在该类的`loadClass`实现中又会执行`super.loadClass(var1, var2);`, 从而间接的调用了`ClassLoader`的`loadClass(String name, boolean resolve)`.
但是这次再调用的时候, 再次执行` Class<?> c = findLoadedClass(name);`时,却找到了类,我怀疑这是从JVM中找到了,所以就不再接着调用我们自己的类加载器了.

因此,如果我们想要每次都能加载进新的类文件,我们需要自己实现`ClassLoader`:
```java
public class MyClassLoader extends URLClassLoader{

	public MyClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(name);
		if (clazz == null) {
			try {
				clazz = findClass(name);
				if (clazz != null) {
					System.out.println("find in findClass:" + name);
				}
			} catch (ClassNotFoundException e) {
				clazz = super.loadClass(name);
				if (clazz != null) {
					System.out.println("find in super.loadClass:" + name);
				}
			}
		}
		return clazz;
	}
}
```

