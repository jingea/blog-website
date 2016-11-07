category: JVM
date: 2016-06-21
title: ClassForName加载类
---
我在[使用Classloader加载类](http://www.yu66.wang/2016/01/29/jvm/使用Classloader加载类/)这篇文章中介绍了, 使用ClassLoader加载类. 但是我们还可以使用一个更简单的方式
`Class.forName()`让系统来加载一个类. 其实我们看它的源码实现的话, 会发现, 它自己也是通过ClassLoader实现的加载
```java
@CallerSensitive
public static Class<?> forName(String className)
            throws ClassNotFoundException {
    return forName0(className, true, ClassLoader.getClassLoader(Reflection.getCallerClass()));
}

private static native Class<?> forName0(String name, boolean initialize, ClassLoader loader)
        throws ClassNotFoundException;
```
我们看到了, 它内部也是找到了一个系统的ClassLoader开始对Class进行加载的.

我们写个测试代码测试一下
```java
import java.lang.reflect.Method;

public class TestClassForName {
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException {
        Class<?> simpleClass = Class.forName("SimpleClass");
        for (Method method: simpleClass.getMethods()) {
            System.out.println(method.getName());
        }
        Class<?> simpleClass1 = Class.forName("SimpleClass");
        System.out.println(simpleClass.equals(simpleClass1));
        System.out.println(simpleClass == simpleClass1);
    }
}
```
然后我在同一级包下定义一个类
```java
public class SimpleClass {
    public void empotyMethod() {}
}
```
我们运行一下看一下结果
```bash
empotyMethod
wait
wait
wait
equals
toString
hashCode
getClass
notify
notifyAll
true
true
```
ok, 类已经被成功加载并且找到了.
