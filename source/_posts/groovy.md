title: groovy
---
# IO
## 1.1. Reading files
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

## 1.2. Writing files

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

## 1.3. Traversing file trees

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

## 1.4. Data and objects

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

## 1.5. Executing External Processes

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

Here is how to gobble all of the output (including the error stream output) from your process:

现在演示一下,如何输出进程里所有的输出(包括error stream).
```groovy
def p = "rm -f foo.tmp".execute([], tmpDir)
p.consumeProcessOutput()
p.waitFor()
```

`consumeProcessOutput`仍然有很多对`StringBuffer`, `InputStream`, `OutputStream`等封装的变量, 如果想要获取一个完整的封装列表的,那可以参考 [GDK API for java.lang.Process](http://docs.groovy-lang.org/latest/html/groovy-jdk/java/lang/Process.html)

另外, `pipeTo`命令 可以让一个进程的输出流连接到一个进程的输入流里. 如下例:

Pipes in action
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

# 2. Working with collections

Groovy 语言层面上就支持多种集合类型,包括list, map, range. 大多数类型集合都是基于java的集合框架,而且Groovy development kit对这些集合内置很多快捷方法.
## 2.1. Lists

### 2.1.1. List literals

你可以像下面这样创建集合, 注意`[]`是空集合表达式.
```groovy
def list = [5, 6, 7, 8]
assert list.get(2) == 7
assert list[2] == 7
assert list instanceof java.util.List

def emptyList = []
assert emptyList.size() == 0
emptyList.add(5)
assert emptyList.size() == 1
```groovy

每一个list表达式都是实现自`java.util.List`

当然list也可以指定其具体的实现类型
```groovy
def list1 = ['a', 'b', 'c']
//construct a new list, seeded with the same items as in list1
def list2 = new ArrayList<String>(list1)

assert list2 == list1 // == checks that each corresponding element is the same

// clone() can also be called
def list3 = list1.clone()
assert list3 == list1
```

list本质上是一个有序的对象集合.
```groovy
def list = [5, 6, 7, 8]
assert list.size() == 4
assert list.getClass() == ArrayList     // the specific kind of list being used

assert list[2] == 7                     // indexing starts at 0
assert list.getAt(2) == 7               // equivalent method to subscript operator []
assert list.get(2) == 7                 // alternative method

list[2] = 9
assert list == [5, 6, 9, 8,]           // trailing comma OK

list.putAt(2, 10)                       // equivalent method to [] when value being changed
assert list == [5, 6, 10, 8]
assert list.set(2, 11) == 10            // alternative method that returns old value
assert list == [5, 6, 11, 8]

assert ['a', 1, 'a', 'a', 2.5, 2.5f, 2.5d, 'hello', 7g, null, 9 as byte]
//objects can be of different types; duplicates allowed

assert [1, 2, 3, 4, 5][-1] == 5             // use negative indices to count from the end
assert [1, 2, 3, 4, 5][-2] == 4
assert [1, 2, 3, 4, 5].getAt(-2) == 4       // getAt() available with negative index...
try {
    [1, 2, 3, 4, 5].get(-2)                 // but negative index not allowed with get()
    assert false
} catch (e) {
    assert e instanceof ArrayIndexOutOfBoundsException
}
```

### 2.1.2. List as a boolean expression

list还可以计算出boolean表达式.
```groovy
assert ![]             // an empty list evaluates as false

//all other lists, irrespective of contents, evaluate as true
assert [1] && ['a'] && [0] && [0.0] && [false] && [null]
```

### 2.1.3. Iterating on a list

可以通过`each`, `eachWithIndex`遍历整个集合.
```groovy
[1, 2, 3].each {
    println "Item: $it" // `it` is an implicit parameter corresponding to the current element
}
['a', 'b', 'c'].eachWithIndex { it, i -> // `it` is the current element, while `i` is the index
    println "$i: $it"
}
```

在遍历的时候,我们经常需要将遍历出来的值经过某些运算,然后再重新放进一个新的list中. 这种操作经常称为映射(mapping), 这种操作通过`collect`方法实现.
```groovy
assert [1, 2, 3].collect { it * 2 } == [2, 4, 6]

// shortcut syntax instead of collect
assert [1, 2, 3]*.multiply(2) == [1, 2, 3].collect { it.multiply(2) }

def list = [0]
// it is possible to give `collect` the list which collects the elements
assert [1, 2, 3].collect(list) { it * 2 } == [0, 2, 4, 6]
assert list == [0, 2, 4, 6]
```

### 2.1.4. Manipulating lists

###### Filtering and searching

[Groovy development kit](http://www.groovy-lang.org/gdk.html)提供了许多强大有趣的方法用来强化标准集合:

```groovy
assert [1, 2, 3].find { it > 1 } == 2           // find 1st element matching criteria
assert [1, 2, 3].findAll { it > 1 } == [2, 3]   // find all elements matching critieria
assert ['a', 'b', 'c', 'd', 'e'].findIndexOf {      // find index of 1st element matching criteria
    it in ['c', 'e', 'g']
} == 2

assert ['a', 'b', 'c', 'd', 'c'].indexOf('c') == 2  // index returned
assert ['a', 'b', 'c', 'd', 'c'].indexOf('z') == -1 // index -1 means value not in list
assert ['a', 'b', 'c', 'd', 'c'].lastIndexOf('c') == 4

assert [1, 2, 3].every { it < 5 }               // returns true if all elements match the predicate
assert ![1, 2, 3].every { it < 3 }
assert [1, 2, 3].any { it > 2 }                 // returns true if any element matches the predicate
assert ![1, 2, 3].any { it > 3 }

assert [1, 2, 3, 4, 5, 6].sum() == 21                // sum anything with a plus() method
assert ['a', 'b', 'c', 'd', 'e'].sum {
    it == 'a' ? 1 : it == 'b' ? 2 : it == 'c' ? 3 : it == 'd' ? 4 : it == 'e' ? 5 : 0
    // custom value to use in sum
} == 15
assert ['a', 'b', 'c', 'd', 'e'].sum { ((char) it) - ((char) 'a') } == 10
assert ['a', 'b', 'c', 'd', 'e'].sum() == 'abcde'
assert [['a', 'b'], ['c', 'd']].sum() == ['a', 'b', 'c', 'd']

// an initial value can be provided
assert [].sum(1000) == 1000
assert [1, 2, 3].sum(1000) == 1006

assert [1, 2, 3].join('-') == '1-2-3'           // String joining
assert [1, 2, 3].inject('counting: ') {
    str, item -> str + item                     // reduce operation
} == 'counting: 123'
assert [1, 2, 3].inject(0) { count, item ->
    count + item
} == 6
```

下面这段代码是由Groovy语言支撑的在集合中找到最大和最小数的例子:
```groovy
def list = [9, 4, 2, 10, 5]
assert list.max() == 10
assert list.min() == 2

// we can also compare single characters, as anything comparable
assert ['x', 'y', 'a', 'z'].min() == 'a'

// we can use a closure to specify the sorting behaviour
def list2 = ['abc', 'z', 'xyzuvw', 'Hello', '321']
assert list2.max { it.size() } == 'xyzuvw'
assert list2.min { it.size() } == 'z'
```

在闭包里,你还可以自定义一个比较规则.
```groovy
Comparator mc = { a, b -> a == b ? 0 : (a < b ? -1 : 1) }

def list = [7, 4, 9, -6, -1, 11, 2, 3, -9, 5, -13]
assert list.max(mc) == 11
assert list.min(mc) == -13

Comparator mc2 = { a, b -> a == b ? 0 : (Math.abs(a) < Math.abs(b)) ? -1 : 1 }


assert list.max(mc2) == -13
assert list.min(mc2) == -1

assert list.max { a, b -> a.equals(b) ? 0 : Math.abs(a) < Math.abs(b) ? -1 : 1 } == -13
assert list.min { a, b -> a.equals(b) ? 0 : Math.abs(a) < Math.abs(b) ? -1 : 1 } == -1
```

###### Adding or removing elements

我们可以使用`[]`去声明一个新的空list, 然后使用`<<`向list追加元素
```groovy
def list = []
assert list.empty

list << 5
assert list.size() == 1

list << 7 << 'i' << 11
assert list == [5, 7, 'i', 11]

list << ['m', 'o']
assert list == [5, 7, 'i', 11, ['m', 'o']]

//first item in chain of << is target list
assert ([1, 2] << 3 << [4, 5] << 6) == [1, 2, 3, [4, 5], 6]

//using leftShift is equivalent to using <<
assert ([1, 2, 3] << 4) == ([1, 2, 3].leftShift(4))
```groovy
We can add to a list in many ways:
```groovy
assert [1, 2] + 3 + [4, 5] + 6 == [1, 2, 3, 4, 5, 6]
// equivalent to calling the `plus` method
assert [1, 2].plus(3).plus([4, 5]).plus(6) == [1, 2, 3, 4, 5, 6]

def a = [1, 2, 3]
a += 4      // creates a new list and assigns it to `a`
a += [5, 6]
assert a == [1, 2, 3, 4, 5, 6]

assert [1, *[222, 333], 456] == [1, 222, 333, 456]
assert [*[1, 2, 3]] == [1, 2, 3]
assert [1, [2, 3, [4, 5], 6], 7, [8, 9]].flatten() == [1, 2, 3, 4, 5, 6, 7, 8, 9]

def list = [1, 2]
list.add(3)
list.addAll([5, 4])
assert list == [1, 2, 3, 5, 4]

list = [1, 2]
list.add(1, 3) // add 3 just before index 1
assert list == [1, 3, 2]

list.addAll(2, [5, 4]) //add [5,4] just before index 2
assert list == [1, 3, 5, 4, 2]

list = ['a', 'b', 'z', 'e', 'u', 'v', 'g']
list[8] = 'x' // the [] operator is growing the list as needed
// nulls inserted if required
assert list == ['a', 'b', 'z', 'e', 'u', 'v', 'g', null, 'x']
```

在list中`+`的语义并没有发生变化,这是何等的重要啊~~~ 与`<<`相比, `+`会创建一个新的list,  但是这个创建的list很可能不是你所预期的, 而且这种方式也可能会导致一些性能问题.

`Groovy development kit`同样提供了很多便捷的方式从list里删除元素:
```groovy
assert ['a','b','c','b','b'] - 'c' == ['a','b','b','b']
assert ['a','b','c','b','b'] - 'b' == ['a','c']
assert ['a','b','c','b','b'] - ['b','c'] == ['a']

def list = [1,2,3,4,3,2,1]
list -= 3           // creates a new list by removing `3` from the original one
assert list == [1,2,4,2,1]
assert ( list -= [2,4] ) == [1,1]
```
同样,你也能通过索引的方式从list里删除元素.
```groovy
def list = [1,2,3,4,5,6,2,2,1]
assert list.remove(2) == 3          // remove the third element, and return it
assert list == [1,2,4,5,6,2,2,1]
```
假设,你如果从list中删除多个相同元素中的第一个, 那你可以调用`remove`方法.
```groovy
def list= ['a','b','c','b','b']
assert list.remove('c')             // remove 'c', and return true because element removed
assert list.remove('b')             // remove first 'b', and return true because element removed

assert ! list.remove('z')           // return false because no elements removed
assert list == ['a','b','b']
```
如果你想要将list清空的话,只需要调用`clear`方法即可
```groovy
def list= ['a',2,'c',4]
list.clear()
assert list == []
```

###### Set operations

`Groovy development kit`还包含很多逻辑运算的方法
```groovy
assert 'a' in ['a','b','c']             // returns true if an element belongs to the list
assert ['a','b','c'].contains('a')      // equivalent to the `contains` method in Java
assert [1,3,4].containsAll([1,4])       // `containsAll` will check that all elements are found

assert [1,2,3,3,3,3,4,5].count(3) == 4  // count the number of elements which have some value
assert [1,2,3,3,3,3,4,5].count {
    it%2==0                             // count the number of elements which match the predicate
} == 2

assert [1,2,4,6,8,10,12].intersect([1,3,6,9,12]) == [1,6,12]

assert [1,2,3].disjoint( [4,6,9] )
assert ![1,2,3].disjoint( [2,4,6] )
```

###### Sorting

Groovy还提供了很多使用闭包比较器的排序操作
```groovy
assert [6, 3, 9, 2, 7, 1, 5].sort() == [1, 2, 3, 5, 6, 7, 9]

def list = ['abc', 'z', 'xyzuvw', 'Hello', '321']
assert list.sort {
    it.size()
} == ['z', 'abc', '321', 'Hello', 'xyzuvw']

def list2 = [7, 4, -6, -1, 11, 2, 3, -9, 5, -13]
assert list2.sort { a, b -> a == b ? 0 : Math.abs(a) < Math.abs(b) ? -1 : 1 } ==
        [-1, 2, 3, 4, 5, -6, 7, -9, 11, -13]

Comparator mc = { a, b -> a == b ? 0 : Math.abs(a) < Math.abs(b) ? -1 : 1 }

// JDK 8+ only
// list2.sort(mc)
// assert list2 == [-1, 2, 3, 4, 5, -6, 7, -9, 11, -13]

def list3 = [6, -3, 9, 2, -7, 1, 5]

Collections.sort(list3)
assert list3 == [-7, -3, 1, 2, 5, 6, 9]

Collections.sort(list3, mc)
assert list3 == [1, 2, -3, 5, 6, -7, 9]
```

###### Duplicating elements

`roovy development kit`还通过重载操作符的方式, 内部提供了一些方法进行list元素复制.
```groovy
assert [1, 2, 3] * 3 == [1, 2, 3, 1, 2, 3, 1, 2, 3]
assert [1, 2, 3].multiply(2) == [1, 2, 3, 1, 2, 3]
assert Collections.nCopies(3, 'b') == ['b', 'b', 'b']

// nCopies from the JDK has different semantics than multiply for lists
assert Collections.nCopies(2, [1, 2]) == [[1, 2], [1, 2]] //not [1,2,1,2]
```

## 2.2. Maps

### 2.2.1. Map literals

在Groovy中可以使用`[:]` 创建一个map.
```groovy
def map = [name: 'Gromit', likes: 'cheese', id: 1234]
assert map.get('name') == 'Gromit'
assert map.get('id') == 1234
assert map['name'] == 'Gromit'
assert map['id'] == 1234
assert map instanceof java.util.Map

def emptyMap = [:]
assert emptyMap.size() == 0
emptyMap.put("foo", 5)
assert emptyMap.size() == 1
assert emptyMap.get("foo") == 5
```

Map的key默认是`string`, 例如`[a:1]`等同于`['a':1]`. 比较荣誉造成疑惑的就是,如果你创建了一个变量a(值为b), 但是你将变量a`put`进map后, map的key会是a,而不是b. 如果你遇到了这个情况的话,那么你必须对使用`()`key进行转义了.
```groovy
def a = 'Bob'
def ages = [a: 43]
assert ages['Bob'] == null // `Bob` is not found
assert ages['a'] == 43     // because `a` is a literal!

ages = [(a): 43]            // now we escape `a` by using parenthesis
assert ages['Bob'] == 43   // and the value is found!
```

通过下面的方式你可以轻松克隆一个map
```groovy
def map = [
        simple : 123,
        complex: [a: 1, b: 2]
]
def map2 = map.clone()
assert map2.get('simple') == map.get('simple')
assert map2.get('complex') == map.get('complex')
map2.get('complex').put('c', 3)
assert map.get('complex').get('c') == 3
```

### 2.2.2. Map property notation

Maps和beans也是非常相像的, 所以你可以对map使用`get/set`操作元素,当然这也有个前提,那就是map中的key必须是符合Groovy标识符的key.

```groovy
def map = [name: 'Gromit', likes: 'cheese', id: 1234]
assert map.name == 'Gromit'     // can be used instead of map.get('Gromit')
assert map.id == 1234

def emptyMap = [:]
assert emptyMap.size() == 0
emptyMap.foo = 5
assert emptyMap.size() == 1
assert emptyMap.foo == 5
```

注意:`map.foo`总是会在map中查找key`foo`. 这意味着,
```groovy
def map = [name: 'Gromit', likes: 'cheese', id: 1234]
assert map.class == null
assert map.get('class') == null
assert map.getClass() == LinkedHashMap // this is probably what you want

map = [1      : 'a',
       (true) : 'p',
       (false): 'q',
       (null) : 'x',
       'null' : 'z']
assert map.containsKey(1) // 1 is not an identifier so used as is
assert map.true == null
assert map.false == null
assert map.get(true) == 'p'
assert map.get(false) == 'q'
assert map.null == 'z'
assert map.get(null) == 'x'
```

### 2.2.3. Iterating on maps

`Groovy development kit`还提供了`eachWithIndex`方法遍历map.值得注意的是,map会保留put元素的顺序,也就是说,当你遍历一个map的时候,无论进行多少次,你获得的元素的顺序是一定的.
```groovy
def map = [
        Bob  : 42,
        Alice: 54,
        Max  : 33
]

// `entry` is a map entry
map.each { entry ->
    println "Name: $entry.key Age: $entry.value"
}

// `entry` is a map entry, `i` the index in the map
map.eachWithIndex { entry, i ->
    println "$i - Name: $entry.key Age: $entry.value"
}

// Alternatively you can use key and value directly
map.each { key, value ->
    println "Name: $key Age: $value"
}

// Key, value and i as the index in the map
map.eachWithIndex { key, value, i ->
    println "$i - Name: $key Age: $value"
}
```

### 2.2.4. Manipulating maps

###### Adding or removing elements

向map中添加元素你可以使用`put`方法, `下标`, `putAll`方法.
```groovy
def defaults = [1: 'a', 2: 'b', 3: 'c', 4: 'd']
def overrides = [2: 'z', 5: 'x', 13: 'x']

def result = new LinkedHashMap(defaults)
result.put(15, 't')
result[17] = 'u'
result.putAll(overrides)
assert result == [1: 'a', 2: 'z', 3: 'c', 4: 'd', 5: 'x', 13: 'x', 15: 't', 17: 'u']
```

如果想要删除map中全部的元素,可以使用`clear`方法.
```groovy
def m = [1:'a', 2:'b']
assert m.get(1) == 'a'
m.clear()
assert m == [:]
```

通过map字面量标记创建的map会使用`object`的`equals`方法和`hashcode`方法.

还要注意的是,不要使用GString作为map的key, 因为GString的hashcode方法和String的hashcode方法不一样.
```groovy
def key = 'some key'
def map = [:]
def gstringKey = "${key.toUpperCase()}"
map.put(gstringKey,'value')
assert map.get('SOME KEY') == null
```

###### Keys, values and entries

我们可以在视图中inspect`keys, values, and entries`
```groovy
def map = [1:'a', 2:'b', 3:'c']

def entries = map.entrySet()
entries.each { entry ->
  assert entry.key in [1,2,3]
  assert entry.value in ['a','b','c']
}

def keys = map.keySet()
assert keys == [1,2,3] as Set
```

Mutating values returned by the view (be it a map entry, a key or a value) is highly discouraged because success of the operation directly depends on the type of the map being manipulated. In particular, Groovy relies on collections from the JDK that in general make no guarantee that a collection can safely be manipulated through keySet, entrySet, or values.


###### Filtering and searching

The Groovy development kit contains filtering, searching and collecting methods similar to those found for lists:

```groovy
def people = [
    1: [name:'Bob', age: 32, gender: 'M'],
    2: [name:'Johnny', age: 36, gender: 'M'],
    3: [name:'Claire', age: 21, gender: 'F'],
    4: [name:'Amy', age: 54, gender:'F']
]

def bob = people.find { it.value.name == 'Bob' } // find a single entry
def females = people.findAll { it.value.gender == 'F' }

// both return entries, but you can use collect to retrieve the ages for example
def ageOfBob = bob.value.age
def agesOfFemales = females.collect {
    it.value.age
}

assert ageOfBob == 32
assert agesOfFemales == [21,54]

// but you could also use a key/pair value as the parameters of the closures
def agesOfMales = people.findAll { id, person ->
    person.gender == 'M'
}.collect { id, person ->
    person.age
}
assert agesOfMales == [32, 36]

// `every` returns true if all entries match the predicate
assert people.every { id, person ->
    person.age > 18
}

// `any` returns true if any entry matches the predicate

assert people.any { id, person ->
    person.age == 54
}
```

###### Grouping

We can group a list into a map using some criteria:

```groovy
assert ['a', 7, 'b', [2, 3]].groupBy {
    it.class
} == [(String)   : ['a', 'b'],
      (Integer)  : [7],
      (ArrayList): [[2, 3]]
]

assert [
        [name: 'Clark', city: 'London'], [name: 'Sharma', city: 'London'],
        [name: 'Maradona', city: 'LA'], [name: 'Zhang', city: 'HK'],
        [name: 'Ali', city: 'HK'], [name: 'Liu', city: 'HK'],
].groupBy { it.city } == [
        London: [[name: 'Clark', city: 'London'],
                 [name: 'Sharma', city: 'London']],
        LA    : [[name: 'Maradona', city: 'LA']],
        HK    : [[name: 'Zhang', city: 'HK'],
                 [name: 'Ali', city: 'HK'],
                 [name: 'Liu', city: 'HK']],
]
```

## 2.3. Ranges

Ranges allow you to create a list of sequential values. These can be used as List since Range extends java.util.List.

Ranges defined with the .. notation are inclusive (that is the list contains the from and to value).

Ranges defined with the ..< notation are half-open, they include the first value but not the last value.

```groovy
// an inclusive range
def range = 5..8
assert range.size() == 4
assert range.get(2) == 7
assert range[2] == 7
assert range instanceof java.util.List
assert range.contains(5)
assert range.contains(8)

// lets use a half-open range
range = 5..<8
assert range.size() == 3
assert range.get(2) == 7
assert range[2] == 7
assert range instanceof java.util.List
assert range.contains(5)
assert !range.contains(8)

//get the end points of the range without using indexes
range = 1..10
assert range.from == 1
assert range.to == 10
```

Note that int ranges are implemented efficiently, creating a lightweight Java object containing a from and to value.

Ranges can be used for any Java object which implements java.lang.Comparable for comparison and also have methods next() and previous() to return the next / previous item in the range. For example, you can create a range of String elements:

# Parsing and producing JSON

Groovy 原生支持Groovy对象和JSON之间的转换. `groovy.json`包内的类用于JSON的序列化和解析功能

## 1. JsonSlurper

`JsonSlurper`用于将JSON文本或者其他数据内容解析成Groovy里的数据结构,例如`maps</code>, `lists</code>, 或者其他原生基本类型 `Integer</code>, `Double</code>, `Boolean</code>, `String`。

这个类重载了很多方法, 而且还添加了一些特殊的方法, 例如`parseText</code>, `parseFile` 等.下面这个例子中我们使用了 `parseText` 方法, 它会解析一个JSON字符串, 然后递归地将它转换成`list</code>, `map`结构. 一些其他的`parse*</code> 方法和这个方法很类似, 都返回了JSON字符串, 只不过其他的方法接受的参数不一样.

```groovy
def jsonSlurper = new JsonSlurper()
def object = jsonSlurper.parseText('{ "name": "John Doe" } /* some comment */')

assert object instanceof Map
assert object.name == 'John Doe'
```

需要注意的是, 产生的结果是一个纯map, 可以像一个普通的Groovy对象实例持有它. `JsonSlurper`根据[ECMA-404 JSON Interchange Standard](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf)定义来解析JSON, 同时支持JavaScript的注释和时间类型.

除了支持maps之外, `JsonSlurper` 还支持将JSON数组解析成list的功能
```groovy
def jsonSlurper = new JsonSlurper()
def object = jsonSlurper.parseText('{ "myList": [4, 8, 15, 16, 23, 42] }')

assert object instanceof Map
assert object.myList instanceof List
assert object.myList == [4, 8, 15, 16, 23, 42]
```

JSON标准上只支持下面这些原生数据类型：`string</code>, `number</code>, `object</code>, `true</code>, `false</code>, `null</code>. `JsonSlurper` 将那些JSON类型转换成Groovy类型.
```groovy
def jsonSlurper = new JsonSlurper()
def object = jsonSlurper.parseText '''
    { "simple": 123,
      "fraction": 123.66,
      "exponential": 123e12
    }'''

assert object instanceof Map
assert object.simple.class == Integer
assert object.fraction.class == BigDecimal
assert object.exponential.class == BigDecimal
```

`JsonSlurper` 生成的结果就是纯Groovy对象实例, 她的内部不会包含任何的JSON相关的类对象, 它的用法是相当透明的. 事实上`JsonSlurper`的结果遵循`GPath`表达式. `GPath`是一个非常强大的表达式语言, 它支持多种不同的数据格式(例如`XmlSlurper`支持`XML` 就是其中一个例子)

如果想要了解更多的内容, 你可以直接去[GPath expressions](http://docs.groovy-lang.org/latest/html/documentation/core-semantics.html#gpath_expressions)看一看.
下面给出了JSON类型与Groovy数据类型之间的对应关系.
```groovy
JSON			Groovy
string			java.lang.String
number			java.lang.BigDecimal or java.lang.Integer
object			java.util.LinkedHashMap
array			java.util.ArrayList
true			true
false			false
null			null
date			java.util.Date based on the yyyy-MM-dd’T’HH:mm:ssZ date format
```

如果JSON中的一个值是`null</code>, `JsonSlurper`支持它转换成Groovy中的`null</code>.这就与其他JSON解析器形成了对比, 代表一个空值与库提供的单一对象。

## 1.1. Parser Variants

Groovy 有多个`JsonSlurper` 解析器实现. 每一个解析器都对应着不同的需求, 每一个特定的解析都能很好的处理特定需求, 所以默认的解析器并不是适应于所有的情况. 下面就对各个解析器做个简介:

`JsonParserCharArray` 解析器接受一个JSON字符串, 然后其内部使用一个字节数组进行解析. During value conversion it copies character sub-arrays (a mechanism known as "chopping") and operates on them.


* `JsonFastParser`解析器是`JsonParserCharArray`解析器的变种, 它是最快的解析器. 尽管它是最快的,但是基于某些原因,它并不是默认的解析器. `JsonFastParser`解析器也被称为索引覆盖(index-overlay)解析器. 当解析给定JSON字符串的时候,该解析器会极力避免创建新的字节数组或者字符串实例. 它一直指向原生的字节数组。 另外, 它会尽可能的推迟对象的创建. If parsed maps are put into long-term caches care must be taken as the map objects might not be created and still consist of pointer to the original char buffer only. `JsonFastParser`采取了一种特殊的切割模型, 它会尽早地分割char buffer, 以便能维持一份对原生buffer比较小的拷贝. 如果你想使用`JsonFastParser</code>, 那么给你的建议是保持`JsonFastParser`的JSON buffer在2MB左右, 而且时刻要保持长期缓存限制.

* `JsonParserLax` 是`JsonFastParser`的一个变种实现. 它与`JsonFastParser` 有一些相似的想能特点, 但是不同的是它不是仅仅依靠`ECMA-404 JSON grammar</code>. 例如,在下面例子中它支持不带引号的字符串注释.

`JsonParserUsingCharacterSource` 用于解析非常大的文件. 它使用一种称为<code>"character windowing"</code>的技术去解析非常大(超过2MB)的JSON文件,而且性能上也非常稳定

`JsonSlurper`的默认实现是 `JsonParserCharArray</code>.  `JsonParserType`包含了解析器种类的枚举类型:

```groovy
Implementation					Constant
JsonParserCharArray				JsonParserType#CHAR_BUFFER
JsonFastParser					JsonParserType#INDEX_OVERLAY
JsonParserLax					JsonParserType#LAX
JsonParserUsingCharacterSource	JsonParserType#CHARACTER_SOURCE
```

如果想要改变解析器的实现也非常简单, 只需要通过调用`JsonSlurper#setType()</code>方法给`JsonParserType`设置上不同的值就可以了

```groovy
def jsonSlurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)
def object = jsonSlurper.parseText('{ "myList": [4, 8, 15, 16, 23, 42] }')

assert object instanceof Map
assert object.myList instanceof List
assert object.myList == [4, 8, 15, 16, 23, 42]
```

# 2. JsonOutput

`JsonOutput`用于将Groovy对象序列化成JSON字符串. 

`JsonOutput` 重载了`toJson`静态方法. 每个不同的`toJson`方法都会接受一个不同的参数类型. 

`toJson`方法返回的是一个包含JSOn格式的字符串
```groovy
def json = JsonOutput.toJson([name: 'John Doe', age: 42])

assert json == '{"name":"John Doe","age":42}'
```

`JsonOutput`不仅支持原生类型, map, list等类型序列化到JSON, 甚至还支持序列化`POGOs</code>(一种比较老的Groovy对象)
```groovy
class Person { String name }

def json = JsonOutput.toJson([ new Person(name: 'John'), new Person(name: 'Max') ])

assert json == '[{"name":"John"},{"name":"Max"}]'
```

刚才那个例子中, JSON输出默认没有进行pretty输出. 因此`JsonSlurper`还提供了`prettyPrint`方法
```groovy
def json = JsonOutput.toJson([name: 'John Doe', age: 42])

assert json == '{"name":"John Doe","age":42}'

assert JsonOutput.prettyPrint(json) == '''\
{
    "name": "John Doe",
    "age": 42
}'''.stripIndent()
```

`prettyPrint`方法只接受一个String类型的字符串, 它不能和`JsonOutput`里其他的方式结合起来使用, it can be applied on arbitrary JSON String instances.

在Groovy中还可以使用`JsonBuilder</code>, `StreamingJsonBuilder`方式创建JSON. 这俩个构建起都提供了一个`DSL</code>, 当构建器生成一个JSON的时候,可以制定一个对象图.


```groovy
// an inclusive range
def range = 'a'..'d'
assert range.size() == 4
assert range.get(2) == 'c'
assert range[2] == 'c'
assert range instanceof java.util.List
assert range.contains('a')
assert range.contains('d')
assert !range.contains('e')
```

You can iterate on a range using a classic for loop:

```groovy
for (i in 1..10) {
    println "Hello ${i}"
}
```

but alternatively you can achieve the same effect in a more Groovy idiomatic style, by iterating a range with each method:

```groovy
(1..10).each { i ->
    println "Hello ${i}"
}
```

Ranges can be also used in the switch statement:

```
switch (years) {
    case 1..10: interestRate = 0.076; break;
    case 11..25: interestRate = 0.052; break;
    default: interestRate = 0.037;
}
```

## 2.4. Syntax enhancements for collections

### 2.4.1. GPath support

Thanks to the support of property notation for both lists and maps, Groovy provides syntactic sugar making it really easy to deal with nested collections, as illustrated in the following examples:

```groovy
def listOfMaps = [['a': 11, 'b': 12], ['a': 21, 'b': 22]]
assert listOfMaps.a == [11, 21] //GPath notation
assert listOfMaps*.a == [11, 21] //spread dot notation

listOfMaps = [['a': 11, 'b': 12], ['a': 21, 'b': 22], null]
assert listOfMaps*.a == [11, 21, null] // caters for null values
assert listOfMaps*.a == listOfMaps.collect { it?.a } //equivalent notation
// But this will only collect non-null values
assert listOfMaps.a == [11,21]
```

### 2.4.2. Spread operator

The spread operator can be used to "inline" a collection into another. It is syntactic sugar which often avoids calls to putAll and facilitates the realization of one-liners:

```groovy
assert [ 'z': 900,
         *: ['a': 100, 'b': 200], 'a': 300] == ['a': 300, 'b': 200, 'z': 900]
//spread map notation in map definition
assert [*: [3: 3, *: [5: 5]], 7: 7] == [3: 3, 5: 5, 7: 7]

def f = { [1: 'u', 2: 'v', 3: 'w'] }
assert [*: f(), 10: 'zz'] == [1: 'u', 10: 'zz', 2: 'v', 3: 'w']
//spread map notation in function arguments
f = { map -> map.c }
assert f(*: ['a': 10, 'b': 20, 'c': 30], 'e': 50) == 30

f = { m, i, j, k -> [m, i, j, k] }
//using spread map notation with mixed unnamed and named arguments
assert f('e': 100, *[4, 5], *: ['a': 10, 'b': 20, 'c': 30], 6) ==
        [["e": 100, "b": 20, "c": 30, "a": 10], 4, 5, 6]
```

### 2.4.3. The star-dot `*.' operator

The "star-dot" operator is a shortcut operator allowing you to call a method or a property on all elements of a collection:

```groovy
assert [1, 3, 5] == ['a', 'few', 'words']*.size()

class Person {
    String name
    int age
}
def persons = [new Person(name:'Hugo', age:17), new Person(name:'Sandra',age:19)]
assert [17, 19] == persons*.age
```

### 2.4.4. Slicing with the subscript operator

You can index into lists, arrays, maps using the subscript expression. It is interesting that strings are considered as special kinds of collections in that context:

```groovy
def text = 'nice cheese gromit!'
def x = text[2]

assert x == 'c'
assert x.class == String

def sub = text[5..10]
assert sub == 'cheese'

def list = [10, 11, 12, 13]
def answer = list[2,3]
assert answer == [12,13]
```

Notice that you can use ranges to extract part of a collection:

```groovy
list = 100..200
sub = list[1, 3, 20..25, 33]
assert sub == [101, 103, 120, 121, 122, 123, 124, 125, 133]
```

The subscript operator can be used to update an existing collection (for collection type which are not immutable):

```groovy
list = ['a','x','x','d']
list[1..2] = ['b','c']
assert list == ['a','b','c','d']
```

It is worth noting that negative indices are allowed, to extract more easily from the end of a collection:

You can use negative indices to count from the end of the List, array, String etc.

```groovy
text = "nice cheese gromit!"
x = text[-1]
assert x == "!"

def name = text[-7..-2]
assert name == "gromit"
```

Eventually, if you use a backwards range (the starting index is greater than the end index), then the answer is reversed.

```groovy
text = "nice cheese gromit!"
name = text[3..1]
assert name == "eci"
```

## 2.5. Enhanced Collection Methods

In addition to lists, maps or ranges, Groovy offers a lot of additional methods for filtering, collecting, grouping, counting, …? which are directly available on either collections or more easily iterables.

In particular, we invite you to read the Groovy development kit API docs and specifically:

* methods added to Iterable can be found here
* methods added to Iterator can be found here
* methods added to Collection can be found here
* methods added to List can be found here
* methods added to Map can be found here

# Scripting Ant tasks

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