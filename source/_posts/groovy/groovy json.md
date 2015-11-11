category: groovy
date: 2014-04-08
title: groovy JSON
---
> 本文是对Groovy部分官方文档进行了翻译

Groovy 原生支持Groovy对象和JSON之间的转换. `groovy.json`包内的类用于JSON的序列化和解析功能

## JsonSlurper

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

### Parser Variants

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

### JsonOutput

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