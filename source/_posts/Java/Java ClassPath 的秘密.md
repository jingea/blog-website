category: Java
date: 2016-08-26
title: Java ClassPath 的秘密
---
写了一段简单的测试代码
```java
import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.TimeUnit;
public class Test {
	public static void main(String[] args) throws InterruptedException {
		JSONObject json = new JSONObject();
		TimeUnit.HOURS.sleep(1);
	}
}
```
然后分别执行
```bash
[root@root wangming]# javac -cp ./* Test.java
javac: invalid flag: ./Test.class
Usage: javac <options> <source files>
use -help for a list of possible options
[root@root wangming]# javac -cp . Test.java
Test.java:2: error: package com.alibaba.fastjson does not exist
import com.alibaba.fastjson.JSONObject;
                           ^
Test.java:9: error: cannot find symbol
                JSONObject json = new JSONObject();
                ^
  symbol:   class JSONObject
  location: class Test
Test.java:9: error: cannot find symbol
                JSONObject json = new JSONObject();
                                      ^
  symbol:   class JSONObject
  location: class Test
3 errors
[root@root wangming]# javac -cp .:./ Test.java
Test.java:2: error: package com.alibaba.fastjson does not exist
import com.alibaba.fastjson.JSONObject;
                           ^
Test.java:9: error: cannot find symbol
                JSONObject json = new JSONObject();
                ^
  symbol:   class JSONObject
  location: class Test
Test.java:9: error: cannot find symbol
                JSONObject json = new JSONObject();
                                      ^
  symbol:   class JSONObject
  location: class Test
3 errors
[root@root wangming]# javac -cp .:./* Test.java 
```

在[Setting the class path](http://docs.oracle.com/javase/7/docs/technotes/tools/solaris/classpath.html)这篇文章中, 说classpath 可以是以下三种形式
* jar包(.jar结尾的文件) 里面包含了class文件
* zip包(.zip结尾的文件) 里面包含了class文件
* 目录(文件夹) 如果class文件里有package, 那么目录里一定要有和package相应的目录结构

如果一个classpath中包含了通配符(`*`), 那么Java就不会在这个目录下搜索class文件了. 

例如`lib/*`, 如果classpath是这个, 那么classpath就只会在`lib`目录下搜索jar文件, 然后从jar文件中去加载class, 如果想要在lib目录下既搜索jar文件也搜索class文件的话, 那么可以写成`lib:lib/*`或者`lib/*:lib`.

还有一点很重要的是, 如果`lib`目录下有子目录的话`lib/jetty`的话, Java是不会进行递归搜索子目录的。

说到这里, Java为什么不会在`lib/*`下搜索class文件呢？是这样的, 如果`lib`目录下有`a.jar`和`b.jar`, 其实现在的`lib/*`就被替换成了`lib/a.jar:lib/b.jar`. 我们可以在Java的系统变量里看到这个结果.

看到这,我们应该就推断出上面错误的原因了, `.`只会搜索`class`文件但是不会搜索jar, 而`*`通配符则自动帮我换成了jar文件的classpath的替换.

> 安装上JDK后, 系统会自动设置一个CLASSPATH的环境变量(.:/home/jdk1.8/lib/dt.jar:/home/jdk1.8/lib/tools.jar), 但是当运行Java命令, 指定`-cp`的时候,会覆盖掉这个classpath, 所以在新的classpath上一定要指定`.`



classpath适用于下列工具
(JDK Tools and Utilities)[http://docs.oracle.com/javase/7/docs/technotes/tools/index.html]

(How Classes are Found)[http://docs.oracle.com/javase/7/docs/technotes/tools/findingclasses.html]