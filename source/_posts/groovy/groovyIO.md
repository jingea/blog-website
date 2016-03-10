category: Groovy
date: 2014-04-08
title: groovy IO
---
> 本文是对Groovy部分官方文档进行了翻译

### 读文件
作为第一个例子,让我们看一下,如何输出一个文本文件里的所有行
```groovy
new File(baseDir, 'haiku.txt').eachLine { line ->
    println line
}
```

`eachLine`方法是Groovy自动添加到File Class的,同时呢,Groovy还添加了很多变量,例如,你如果想要知道每一行的行号,你可以使用这个变量:
```groovy
new File(baseDir, 'haiku.txt').eachLine { line, nb ->
    println "Line $nb: $line"
}
```
无论由于什么原因, 当`eachLine`中抛出了异常,这个方法都会确保,资源已经被正确的关闭掉了. 这对所有Groovy自动添加的关于I/O资源的方法都有效.

例如, 某种情况你使用了`Reader`, 但是你还想让Groovy自己管理资源. 下面这个例子, 即使抛出了exception, reader仍然会被自动关闭.
```groovy
def count = 0, MAXSIZE = 3
new File(baseDir,"haiku.txt").withReader { reader ->
    while (reader.readLine()) {
        if (++count > MAXSIZE) {
            throw new RuntimeException('Haiku should only have 3 verses')
        }
    }
}
```

如果你想要把文本文件中每一行都放进一个list中, 你可以这么做:
```groovy
def list = new File(baseDir, 'haiku.txt').collect {it}
```

或者你想利用操作符将文件中每一行都添加到一个数组中:
```groovy
def array = new File(baseDir, 'haiku.txt') as String[]
```

下面这个示例,非常简单的实现了,将一个文件存进一个字节数组里:
```groovy
byte[] contents = file.bytes
```

如下例,我们轻松地获得了一个输入流.
```groovy
def is = new File(baseDir,'haiku.txt').newInputStream()
// do something ...
is.close()
```

上个例子中我们获得了一个输入流,但是最后我们不得不手动关闭它, Groovy提供另一个方法`withInputStream`, 这个方法可以帮我们自动的关闭输入流.
```groovy
new File(baseDir,'haiku.txt').withInputStream { stream ->
    // do something ...
}
```

### 写文件

有时候,你需要的也许只是写文件,下面展示了,如何在Groovy中写文件
```groovy
new File(baseDir,'haiku.txt').withWriter('utf-8') { writer ->
    writer.writeLine 'Into the ancient pond'
    writer.writeLine 'A frog jumps'
    writer.writeLine 'Water’s sound!'
}
```

但对于一个要求很简单的需求来说,我们可以使用`<<`向文件中写
```groovy
new File(baseDir,'haiku.txt') << '''Into the ancient pond
A frog jumps
Water’s sound!'''
```

当然不是每一次我们都是向文件中输出文本,下面的例子演示了,我们如何向一个文件中写入字节:
```groovy
file.bytes = [66,22,11]
```

当然,你也可以直接打开一个输出流,下面的例子演示了如何开启一个输出流.
```groovy
def os = new File(baseDir,'data.bin').newOutputStream()
// do something ...
os.close()
```

同`newInputStream`一样,`newOutputStream`同样需要手动关闭, ok,你大概想到了`withOutputStream`:
```groovy
new File(baseDir,'data.bin').withOutputStream { stream ->
    // do something ...
}
```

### 遍历文件

在脚本中, 有个很常用的需求就是,遍历一个目录,然后找到一个文件,进行某些操作. Groovy提供了很多方法,来达到这个效果. 下面的例子演示了将一个目录下的所有文件都执行某个操作:
```groovy
dir.eachFile { file ->                      (1)
    println file.name
}
dir.eachFileMatch(~/.*\.txt/) { file ->     (2)
    println file.name
}
```

1. 在目录下的每个文件上执行闭包操作.
2. 根据正则表达式在目录下找到符合条件的文件,然后执行闭包操作.

也许你想要遍历某个目录和目录里的所有子目录, 那么你可以使用`eachFileRecurse`
```groovy
dir.eachFileRecurse { file ->                      (1)
    println file.name
}

dir.eachFileRecurse(FileType.FILES) { file ->      (2)
    println file.name
}
```
1. 对目录里的所有子目录进行递归, 然后对找到的文件和目录进行闭包操作
2. 对目录里进行递归查找,但是只查找文件.

```groovy
dir.traverse { file ->
    if (file.directory && file.name=='bin') {
        FileVisitResult.TERMINATE                   (1)
    } else {
        println file.name
        FileVisitResult.CONTINUE                    (2)
    }

}
```
1. 如果找到的文件是目录,且它的名字是"dir", 则停止遍历
2.  打印出文件的名字,接着遍历

### 序列化

在java中会使用`java.io.DataOutputStream` 序列化数据也不罕见. Groovy对这个需求也做了非常简单的实现, 下面的例子演示了如何序列化和反序列化:
```groovy
boolean b = true
String message = 'Hello from Groovy'
// Serialize data into a file
file.withDataOutputStream { out ->
    out.writeBoolean(b)
    out.writeUTF(message)
}
// ...
// Then read it back
file.withDataInputStream { input ->
    assert input.readBoolean() == b
    assert input.readUTF() == message
}
```

同样,如果这个数据实例了序列化接口`Serializable`, 你可以使用 object output stream将整个数据序列化到文件:
```groovy
Person p = new Person(name:'Bob', age:76)
// Serialize data into a file
file.withObjectOutputStream { out ->
    out.writeObject(p)
}
// ...
// Then read it back
file.withObjectInputStream { input ->
    def p2 = input.readObject()
    assert p2.name == p.name
    assert p2.age == p.age
}
```

### 执行命令

前面的章节介绍了在Groovy中操作files, readers or streams非常简单. 然而, 像系统管理员或者开发者,可能更多的是执行一个系统命令.

Groovy同样提供了非常简单的方式执行命令行命令. 只需要定义一个命令的字符串,然后执行这个字符串的`execute()`. 在类Unix系统中(如果在windows中也安装了类Unix命令行工具也算),你可以这样执行命令.
```groovy
def process = "ls -l".execute()             (1)
println "Found text ${process.text}"        (2)
```
1. 在外部过程(external process)执行ls命令
2. 获得命令的输出,并输出

`execute()`方法返回一个`java.lang.Process`实例, 随后选择一种输出流`in/out/err`, 同时检查`exit`值,查看是否命令执行完毕.

下面的例子使用了和刚才那个例子一样的命令,但是现在我们每次都会对获得的结果进行行输出.
```groovy
            def process = "ls -l".execute()             (1)
            process.in.eachLine { line ->               (2)
                println line                            (3)
            }
            assert process instanceof Process
        }
    }

    void testProcessConsumeOutput() {
        if (unixlike) {
            doInTmpDir { b ->
                File file = null
                def tmpDir = b.tmp {
                    file = 'foo.tmp'('foo')
                }
                assert file.exists()
                def p = "rm -f foo.tmp".execute([], tmpDir)
                p.consumeProcessOutput()
                p.waitFor()
                assert !file.exists()
            }

        }
    }

    void testProcessPipe() {
        if (unixlike) {
            doInTmpDir { b ->
                def proc1, proc2, proc3, proc4
                proc1 = 'ls'.execute()
                proc2 = 'tr -d o'.execute()
                proc3 = 'tr -d e'.execute()
                proc4 = 'tr -d i'.execute()
                proc1 | proc2 | proc3 | proc4
                proc4.waitFor()
                if (proc4.exitValue()) {
                    println proc4.err.text
                } else {
                    println proc4.text
                }

                def sout = new StringBuilder()
                def serr = new StringBuilder()
                proc2 = 'tr -d o'.execute()
                proc3 = 'tr -d e'.execute()
                proc4 = 'tr -d i'.execute()
                proc4.consumeProcessOutput(sout, serr)
                proc2 | proc3 | proc4
                [proc2, proc3].each { it.consumeProcessErrorStream(serr) }
                proc2.withWriter { writer ->
                    writer << 'testfile.groovy'
                }
                proc4.waitForOrKill(1000)
                println "Standard output: $sout"
                println "Standard error: $serr"
            }
        }
    }

    public static class Person implements Serializable {
        String name
        int age
    }
}
```

1	executes the ls command in an external process
2	for each line of the input stream of the process
3	print the line
1. 在外部进程中执行ls命令
2.

It is worth noting that in corresponds to an input stream to the standard output of the command. out will refer to a stream where you can send data to the process (its standard input).


Remember that many commands are shell built-ins and need special handling. So if you want a listing of files in a directory on a Windows machine and write:

```groovy
def process = "dir".execute()
println "${process.text}"
```

接着你会收到一个异常`IOException`,异常信息为`Cannot run program "dir": CreateProcess error=2`,系统找不到指定的文件.

这是因为`dir`是内建于`windows shell(cmd.ext)`, 想要使用那个命令,你要像下面这个样操作:
```groovy
def process = "cmd /c dir".execute()
println "${process.text}"
```

还有,因为上述的功能是在内部使用的`java.lang.Process`, 这个类的一些不足的地方,我们也要充分考虑. 在javadoc中,是这样描述这个类的:

> Because some native platforms only provide limited buffer size for standard input and output streams, failure to promptly write the input stream or read the output stream of the subprocess may cause the subprocess to block, and even deadlock
Because of this, Groovy provides some additional helper methods which make stream handling for processes easier.

现在演示一下,如何输出进程里所有的输出(包括error stream).
```groovy
def p = "rm -f foo.tmp".execute([], tmpDir)
p.consumeProcessOutput()
p.waitFor()
```

`consumeProcessOutput`仍然有很多对`StringBuffer`, `InputStream`, `OutputStream`等封装的变量, 如果想要获取一个完整的封装列表的,那可以参考 [GDK API for java.lang.Process](http://docs.groovy-lang.org/latest/html/groovy-jdk/java/lang/Process.html)

另外, `pipeTo`命令 可以让一个进程的输出流连接到一个进程的输入流里. 如下例:

```groovy
proc1 = 'ls'.execute()
proc2 = 'tr -d o'.execute()
proc3 = 'tr -d e'.execute()
proc4 = 'tr -d i'.execute()
proc1 | proc2 | proc3 | proc4
proc4.waitFor()
if (proc4.exitValue()) {
    println proc4.err.text
} else {
    println proc4.text
}
```
Consuming errors
```groovy
def sout = new StringBuilder()
def serr = new StringBuilder()
proc2 = 'tr -d o'.execute()
proc3 = 'tr -d e'.execute()
proc4 = 'tr -d i'.execute()
proc4.consumeProcessOutput(sout, serr)
proc2 | proc3 | proc4
[proc2, proc3].each { it.consumeProcessErrorStream(serr) }
proc2.withWriter { writer ->
    writer << 'testfile.groovy'
}
proc4.waitForOrKill(1000)
println "Standard output: $sout"
println "Standard error: $serr"
```

