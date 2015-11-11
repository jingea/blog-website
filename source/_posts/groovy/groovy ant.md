category: groovy
date: 2014-04-08
title: groovy ANT
---
> 本文是对Groovy部分官方文档进行了翻译

虽然`Ant`只是一个构建工具, 但其提供了例如能够操作文件(包括zip文件), 拷贝, 资源管理等诸多实用功能. 然而如果你不喜欢使用`build.xml`文件或者`Jelly`脚本, 而是想要一种清晰简洁的构建方式, 那么你就可以试试使用Groovy编写构建过程.

Groovy提供了一个辅助类`AntBuilder`帮忙编写Ant构建任务. 它看起来很像一个不带尖括号的Ant’s XML的简洁版本. 因此你可以在脚本中混合和匹配标记. Ant本身是一组Jar文件的集合. 将这组jar文件添加到你的classpath上, 你就可以在Groovy中轻轻松松的使用它们.

`AntBuilder`通过便捷的构造器语法直接暴露了Ant task. 下面是一个简单的示例, 它的功能是在标准输出上输出一条消息.
```groovy
def ant = new AntBuilder()          
ant.echo('hello from Ant!')        
```

1. 创建一个`AntBuilder`实例
2. 执行`AntBuilder`实例的echo task

假设,现在你需要创建一个ZIP文件：
```groovy
def ant = new AntBuilder()
ant.zip(destfile: 'sources.zip', basedir: 'src')
```

在下面的例子中, 我们将展示在Groovy中使用传统的Ant 模式通过`AntBuilder`拷贝一组文件.
```groovy
// lets just call one task
ant.echo("hello")

// here is an example of a block of Ant inside GroovyMarkup
ant.sequential {
    echo("inside sequential")
    def myDir = "target/AntTest/"
    mkdir(dir: myDir)
    copy(todir: myDir) {
        fileset(dir: "src/test") {
            include(name: "**/*.groovy")
        }
    }
    echo("done")
}

// now lets do some normal Groovy again
def file = new File(ant.project.baseDir,"target/AntTest/groovy/util/AntTest.groovy")
assert file.exists()
```

下面的例子是遍历一组文件, 然后将每个文件根据特殊模式进行匹配.
```groovy
// lets create a scanner of filesets
def scanner = ant.fileScanner {
    fileset(dir:"src/test") {
        include(name:"**/Ant*.groovy")
    }
}

// now lets iterate over
def found = false
for (f in scanner) {
    println("Found file $f")
    found = true
    assert f instanceof File
    assert f.name.endsWith(".groovy")
}
assert found
```

Or execute a JUnit test:

下面我们执行JUnit
```groovy
// lets create a scanner of filesets
ant.junit {
    test(name:'groovy.util.SomethingThatDoesNotExist')
}
```

现在, 让我们的步子迈地更大一点：在Groovy中编译然后执行一个Java文件.
```groovy
ant.echo(file:'Temp.java', '''
    class Temp {
        public static void main(String[] args) {
            System.out.println("Hello");
        }
    }
''')
ant.javac(srcdir:'.', includes:'Temp.java', fork:'true')
ant.java(classpath:'.', classname:'Temp', fork:'true')
ant.echo('Done')
```

需要提及的是, `AntBuilder`是内嵌于`Gradle`中的. 你可以像在Groovy中那样, 在`Gradle`使用`AntBuilder`