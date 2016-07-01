category: JVM
date: 2015-11-24
title: Instrumentation Agentmain
---

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
