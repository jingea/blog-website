category: JVM
date: 2016-11-03
title: Spring-loaded
---
在前面的文章里分别都说过了instrument和Hotswap, 显然这俩个技术在代码热更方面都有比较大的局限性. 今天测试一下Spring出品的Spring-loaded. 它可以动态地添加删除方法, 属性字段等.

Spring-loaded 的用法非常简单, 下载springloaded.jar这个jar包, 然后使用代理的方式将它挂载到JVM上就可以了.
```bash
java -javaagent:<pathto>/springloaded.jar -noverify SomeJavaClass
```

尽管有了很高的灵活性,但是还是多少稍微有一些限制, 如果想要reload的文件不能以下列字符串开头
```bash
antlr/
org/springsource/loaded/
com/springsource/tcserver/
com/springsource/insight/
groovy/
groovyjarjarantlr/
groovyjarjarasm/
grails/
java/
javassist/
org/codehaus/groovy/
org/apache/
org/springframework/
org/hibernate/
org/hsqldb/
org/aspectj/
org/xml/
org/h2/
```

还可以设置一些系统参数
```bash
java -Dspringloaded=explain -javaagent:<pathto>/springloaded.jar -noverify SomeJavaClass
```
`explain`mode会输出无法reload的诊断信息,例如
```bash
Feb 05, 2014 11:00:51 AM org.springsource.loaded.TypeRegistry couldBeReloadable
INFO: WhyNotReloadable? The type org/apache/maven/model/building/ModelBuilder is using a package name 'org/apache/' which is 
     considered infrastructure and types within it are not made reloadable
```
除了`explain`还有`verbose`模式, 这个模式会使用`java.util.Logging`输出reload过程详细信息.
```bash
Watcher Observed last modification time change for /Users/aclement/play/grails10411/jira-reload/target/classes/br/
        com/app/domains/CategoryController.class (lastScanTime=1391628759166)
Watcher Firing file changed event /Users/aclement/play/grails10411/jira-reload/target/classes/br/
        com/app/domains/CategoryController.class
ReloadableType Loading new version of br/com/app/domains/CategoryController, identifying suffix OV1YS1A,
               new data length is 27209bytes
```
当然, 上面俩个模式并不是互斥的, 我们可以对spring-loaded指定多个参数
```bash
-Dspringloaded=verbose;explain;profile=grails
```

看到了这么多, 其实还有一些特性没有测试到， 但是不想再自欺欺人了, 只有一个原因`-noverify`, 它关闭了JVM的字节码检查功能, 它让[JVM坐上火箭去了月球](https://blogs.oracle.com/buck/entry/never_disable_bytecode_verification_in)