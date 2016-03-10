category: Groovy
date: 2014-04-08
title: groovy注释和标识符
---
> 本文是对Groovy部分官方文档进行了翻译

## 注释
### 单行注释
想要使用单行注释, 使用`//`就可以了.  本行中`//`后续的内容都会被认为是注释的一部分
```groovy
// a standalone single line comment
println "hello" // a comment till the end of the line
```

### 多行注释
多行注释从`/*`开始, 直到`*/`结束(跨行也包含在内)
```groovy
/* a standalone multiline comment
spanning two lines */
println "hello" /* a multiline comment starting
at the end of a statement */
println 1 /* one */ + 2 /* two */
```
#### GroovyDoc 注释
`GroovyDoc` 注释也是多行的, 但是它是以`/**`开始, `*/`结束定义的.
这种注释一般用于以下情况：
* 类型定义(包含 classes, interfaces, enums, annotations)
* 字段和属性定义
* 方法定义

```groovy
/**
  * A Class description
  */
 class Person {
     /** the name of the person */
     String name

     /**
      * Creates a greeting method for a certain person.
      *
      * @param otherPerson the person to greet
      * @return ag reeting message
      */
     String greet(String otherPerson) {
        "Hello ${otherPerson}"
     }
 }
```

### Shebang line
除了上面提到的单行注释外, 还有一种特殊的单行注释.这种注释在UNIX系统下通常称为shebang线, 这种注释允许脚本直接在命令行里执行( 但是前提是你已经在系统是安装了`groovy`,并且在`PATH`里进行了配置)

```groovy
#!/usr/bin/env groovy
println "Hello from the shebang line"
```
`#`字符必须是这个文件里的第一个字符,否则编译器将会抛出一个编译错误.

## 标识符

### 普通标识符

标识符以一个`字母`或者`$`或者`_`开始, 不能以数字打头.
如果以字母打头,他们在下列范围内

* 'a' to 'z' (lowercase ascii letter)
* 'A' to 'Z' (uppercase ascii letter)
* '\u00C0' to '\u00D6'
* '\u00D8' to '\u00F6'
* '\u00F8' to '\u00FF'
* '\u0100' to '\uFFFE'

剩下的字符就可以包含字母或者数字了.  下面列举了一些合法的标识符：
```groovy
def name
def item3
def with_underscore
def $dollarStart
```
下面是一些非法的标识符
```groovy
def 3tier
def a+b
def a#b
```
`.`后面的关键字也是合法的标识符
```groovy
foo.as
foo.assert
foo.break
foo.case
foo.catch
```

### 带引号的标识符

带引号的标识符出现在`.\`. 例如`person.name`表达式中的`name`部分能通过这俩种方式引起来`person."name"`或者`person.\'name'`. 当特定标识符中包含非法字符(java语言禁止的字符),但是通过引号的方式可以达到在Groovy的合法. 例如,一个破折号,一个空格,一个感叹号,
```groovy
def map = [:]

map."an identifier with a space and double quotes" = "ALLOWED"
map.'with-dash-signs-and-single-quotes' = "ALLOWED"

assert map."an identifier with a space and double quotes" == "ALLOWED"
assert map.'with-dash-signs-and-single-quotes' == "ALLOWED"
```

正像一会我们在strings模块看到的一样, Groovy提供了不同的string字面量. 以下所列举的都是合法的
```groovy
map.'single quote'
map."double quote"
map.'''triple single quote'''
map."""triple double quote"""
map./slashy string/
map.$/dollar slashy string/$
```

strings 和 Groovy’s GStrings 在纯字符上面是有一点不同的,as in that the latter case, the interpolated values are inserted in the final string for evaluating the whole identifier:
```groovy
def firstname = "Homer"
map."Simson-${firstname}" = "Homer Simson"

assert map.'Simson-Homer' == "Homer Simson"
```

