title: groovy
---
> 本文是对Groovy部分官方文档进行了翻译

# 注释
## 单行注释
想要使用单行注释, 使用`//`就可以了.  本行中`//`后续的内容都会被认为是注释的一部分
```groovy
// a standalone single line comment
println "hello" // a comment till the end of the line
```

## 多行注释
多行注释从`/*`开始, 直到`*/`结束(跨行也包含在内)
```groovy
/* a standalone multiline comment
spanning two lines */
println "hello" /* a multiline comment starting
at the end of a statement */
println 1 /* one */ + 2 /* two */
```
### GroovyDoc 注释
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

## Shebang line
除了上面提到的单行注释外, 还有一种特殊的单行注释.这种注释在UNIX系统下通常称为shebang线, 这种注释允许脚本直接在命令行里执行( 但是前提是你已经在系统是安装了`groovy`,并且在`PATH`里进行了配置)

```groovy
#!/usr/bin/env groovy
println "Hello from the shebang line"
```
`#`字符必须是这个文件里的第一个字符,否则编译器将会抛出一个编译错误.

# 标识符

## 普通标识符

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

## 带引号的标识符

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

# 字符串
Text literals are represented in the form of chain of characters called strings. Groovy lets you instantiate `java.lang.String` objects, as well as GStrings (`groovy.lang.GString`) which are also called interpolated strings in other programming languages.

在Groovy文本字面量被称为String,这是以字符链的形式出现的. Groovy允许你实例化`java.lang.String`,像  GStrings (`groovy.lang.GString`)那样, (GString还被称为插值字符串)

## 单引号字符
Single quoted strings are a series of characters surrounded by single quotes:

单引号字符串是通过单引号括起来的一列字符
```groovy
'a single quoted string'
```
Single quoted strings are plain `java.lang.String` and don’t support interpolation.

单引号字符和`java.lang.String`是同一个东西, 同时它也不允许插值的出现
## 字符串连接

Groovy里所有的字符串都可以通过 `+` 连接起来
```groovy
assert 'ab' == 'a' + 'b'
```

## 三重单引号字符串

三重单引号字符串 是通过三个单引号 包围起来的字符序列.
```groovy
'''a triple single quoted string'''
```
三重单引号字符串就是纯`java.lang.String` 而且不允许插值.
三重单引号字符串可以多行赋值.
```groovy
def aMultilineString = '''line one
line two
line three'''
```

如果你的代码进行了缩进, 例如类中的方法体, 那跨行的三重单引号字符串也会包含缩进. 不过可以调用`String#stripIndent()` 去除掉缩进. `String#stripMargin()`方法会通过分割符从字符串的开头
```groovy
def startingAndEndingWithANewline = '''
line one
line two
line three
'''
```

你也许会注意到最终得到的字符串会包含一个换行符.It is possible to strip that character by escaping the newline with a backslash:
```groovy
def strippedFirstNewline = '''\
line one
line two
line three
'''

assert !strippedFirstNewline.startsWith('\n')
```

### 更换特殊字符

可以通过`\`字符在`''`继续引用`'`
```groovy
'an escaped single quote: \' needs a backslash'
```

当然也可以通过`\`来引用它自身
```groovy
'an escaped escape character: \\ needs a double backslash'
```

还有一些其他的特殊字符需要`\`来引用
```groovy
Escape sequence	Character
'\t'	tabulation
'\b'	backspace
'\n'	newline
'\r'	carriage return
'\f'	formfeed
'\\'	backslash
'\''	single quote (for single quoted and triple single quoted strings)
'\"'	double quote (for double quoted and triple double quoted strings)
```
### Unicode 转义序列

有一些字符并不能通过键盘输出, 那么此时就可以通过Unicode 转义序列来实现. 例如`backslash`, 在u后跟4个16进制数字即可.

```groovy
'The Euro currency symbol: \u20AC'
```
## 双引号包含的 string

通过双引号包括起来的字符串
```groovy
"a double quoted string"
```
To escape a double quote, you can use the backslash character: "A double quote: \"".

当双引号字符串内没有插值(${})的时候, 那它就等同于`java.lang.String`, 当有插值的时候那么双引号字符串就是`groovy.lang.GString`的实例

### String 插值

任何表达式都可以嵌入到除了单引号和三引号的所有字符串常量中. 当对字符串求值的时候, 插值会使用他的值来替换掉字符串里的占位符. 占位符表达式通过`${}` 或者 `$`来实现. 占位符里的表达式值会被转换成其字符串表示形式, 转换是通过调用表达式`toString()`方法,通过传递一个String参数.

下面的例子展示的是字符串里的占位符定位本地变量
```groovy
def name = 'Guillaume' // a plain string
def greeting = "Hello ${name}"

assert greeting.toString() == 'Hello Guillaume'
```

但是并非所有的表达式都是合法的, 像下面我们列举的这个算术表达式

```groovy
def sum = "The sum of 2 and 3 equals ${2 + 3}"
assert sum.toString() == 'The sum of 2 and 3 equals 5'
```

其实并不是只有表达式允许出现在`${}`表达式里. Statements 同样可以在`${}` 占位符里出现, 但是statement的值会是null. 如果有N个statements出现在`${}`里,那么最后一个statement应该返回一个有效值,以便被插入到字符串里. 例如`"The sum of 1 and 2 is equal to ${def a = 1; def b = 2; a + b}"` 是允许的,而且也会像语法预期的那样执行, 但是习惯上,GString 占位符里应该更多的是使用简单表达式.
除了` ${}`占位符之外, 我们也可以使用`$`标记前缀点缀表达式：

```groovy
def person = [name: 'Guillaume', age: 36]
assert "$person.name is $person.age years old" == 'Guillaume is 36 years old'
```
但是仅仅一下形式的点缀表达式是合法的：a.b, a.b.c,etc.但是那些包含括号的表达式(例如方法调用,花括号为闭包,算术运算符)是无效的.
下面给出了一个定义成数字形式的变量.
```groovy
def number = 3.14
```

下面的 statement 将会抛出一个`groovy.lang.MissingPropertyException` 异常,因为Groovy认为你正在尝试访问那个数字的不存在的toString属性.
```groovy
shouldFail(MissingPropertyException) {
    println "$number.toString()"
}
```
你可以理解成解析器会将`"$number.toString()"` 解释成 `"${number.toString}()"`.如果你想要在GString中避免`$`或者`${}` 称为插值的话,只需要在它们前面加上`\`即可.

```groovy
assert '${name}' == "\${name}"
```
### 特殊插值形式-闭包表达式

到目前为止,我们看到可以在${}占位符里插入任何的表达式, 但还有一种特殊的表达式-闭包表达式. 当占位符内好汉一个箭头时`${→}`,这个表达式实际上就是一个闭包表达式.

```groovy
def sParameterLessClosure = "1 + 2 == ${-> 3}" (1)
assert sParameterLessClosure == '1 + 2 == 3'

def sOneParamClosure = "1 + 2 == ${ w -> w << 3}" (2)
assert sOneParamClosure == '1 + 2 == 3'
```
1. 由于闭包不用声明参数, 所以在使用闭包时,我们不必对其传参
2. 上例中,闭包中使用了一个`java.io.StringWriter argument`参数, 我们可以使用`<<`操作符添加内容.不论任何情况, 占位符都被嵌入了闭包.

上面的表达式看起来更像是使用了一个啰嗦的方式去定义插值表达式, 但是闭包有个有趣又高级的特性：惰性计算:

```groovy
def number = 1 (1)
def eagerGString = "value == ${number}"
def lazyGString = "value == ${ -> number }"

assert eagerGString == "value == 1" (2)
assert lazyGString ==  "value == 1" (3)

number = 2 (4)
assert eagerGString == "value == 1" (5)
assert lazyGString ==  "value == 2" (6)
```
1	We define a number variable containing 1 that we then interpolate within two GStrings, as an expression in eagerGString and as a closure in lazyGString.
2	We expect the resulting string to contain the same string value of 1 for eagerGString.
3	Similarily for lazyGString
4	Then we change the value of the variable to a new number
5	With a plain interpolated expression, the value was actually bound at the time of creation of the GString.
6	But with a closure expression, the closure is called upon each coercion of the GString into String, resulting in an updated string containing the new number value.
An embedded closure expression taking more than one parameter will generate an exception at runtime. Only closures with zero or one parameters are allowed.

1. 我们定义了数值为1的number类型变量, 它稍后会作为插值出现在俩个GString中,
2. 我们希望eagerGString 产生的字符串包含着相同的值 1
3. 同样我们也希望lazyGString 产生的字符串包含着相同的值 1
4. 然后我们将number改变一个值.
5.
6.

### Inteoperability with Java
当一个方法(不管是在Java还是在Groovy中定义的)带有一个`java.lang.String`参数, 但我们传递一个`groovy.lang.GString instance`实例, GString会自动调用toString()方法.

```groovy
String takeString(String message) {         (4)
    assert message instanceof String        (5)
    return message
}

def message = "The message is ${'hello'}"   (1)
assert message instanceof GString           (2)

def result = takeString(message)            (3)
assert result instanceof String
assert result == 'The message is hello'
```
1. 首先我们创建一个GString变量
2. 然后我们检查一下声明的变量是否是GString的实例
3. 接着我们向一个方法(参数为String类型)传递GString类型变量
4. takeString()显式地指出了它唯一的参数为String
5. 我们再次验证所需的参数是String 而不是GString


### GString and String hashCodes

尽管插值字符串能被用来代替`Java strings`, 但是他们在某些地方并不是完全一样的—— 他们的hashCodes是不同的. Java Strig是`immutable`, 然而, GString通过它的内插值 生成的字符串是可以改变的. 即使生成完全一样的字符串, GStrings 和 Strings的 hashCode 仍然是不一样的.

```groovy
assert "one: ${1}".hashCode() != "one: 1".hashCode()
```

GString 和 Strings 拥有不同的hashCode值, 在Map中应该避免使用GString作为key, 特别的,当我们想要检索值的之后应该使用String,而不是GString.
```groovy
def key = "a"
def m = ["${key}": "letter ${key}"]     (1)

assert m["a"] == null                   (2)
```
1. map使用一对键值被创建了出来,其key是GString类型
2. 当我们通过一个String类型的key进行检索值的时候,我们会得到一个null的结果, 产生这样的现象正是由于String和GString拥有不同的hashCode

## Triple double quoted string

三重双引号字符串其使用和双引号字符串及其相像, 但与双引号字符串不同的一点是：它们是可以换行的(像三重单引号字符串那样)
```groovy
def name = 'Groovy'
def template = """
    Dear Mr ${name},

    You're the winner of the lottery!

    Yours sincerly,

    Dave
"""

assert template.toString().contains('Groovy')
```

在三重双引号字符串中,不管是双引号还是单引号都不需要escaped

## Slashy string
除了引号字符串, Groovy还提供了slashy字符串(使用/作为分隔符). Slashy字符串对定义正则表达式和正则模式是非常有用的.

```groovy
def fooPattern = /.*foo.*/
assert fooPattern == '.*foo.*'
```

只有在`/ slashes`中需要使用\ 来escaped
```groovy
def escapeSlash = /The character \/ is a forward slash/
assert escapeSlash == 'The character / is a forward slash'
```

Slashy字符串也可以是多行的
```groovy
def multilineSlashy = /one
    two
    three/

assert multilineSlashy.contains('\n')
```

Slashy字符串也可以插值形式出现(像GString一样)
```groovy
def color = 'blue'
def interpolatedSlashy = /a ${color} car/

assert interpolatedSlashy == 'a blue car'
```

下面有一些常识方面的东西需要你知道：
`//`不会被解释为空Slashy字符串,这代表着行注释.

```groovy
assert '' == //
```

## Dollar slashy string

Dollar slashy字符串 通过`$/``/$` 来实现多行GString. 美元符作为转义字符, 而且它还能转义另一个美元符号, 或者一个 forward slash. 除了要实现像GString占位符和闭包美元符slashy的开头美元符之外, 美元符和forward slashes都不需要转义
```groovy
def name = "Guillaume"
def date = "April, 1st"

def dollarSlashy = $/
    Hello $name,
    today we're ${date}.

    $ dollar sign
    $$ escaped dollar sign
    \ backslash
    / forward slash
    $/ escaped forward slash
    $/$ escaped dollar slashy string delimiter
/$

assert [
    'Guillaume',
    'April, 1st',
    '$ dollar sign',
    '$ escaped dollar sign',
    '\\ backslash',
    '/ forward slash',
        '$/ escaped forward slash',
        '/$ escaped dollar slashy string delimiter'

        ].each { dollarSlashy.contains(it) }
```

## Characters

不像java, Groovy里没有显式的字符字面量. 可以通过下面三种方式,显式地生成Groovy 字符变量
```groovy
char c1 = 'A' (1)
assert c1 instanceof Character

def c2 = 'B' as char (2)
assert c2 instanceof Character

def c3 = (char)'C' (3)
assert c3 instanceof Character
```
1. 通过指定char类型来显式地声明一个character变量
2. 通过操作符强制转换类型
3. 通过强制转换成指定类型

# Numbers

Groovy支持多种不同的整数字面量和小数字面量 (通过依靠Java数字类型实现)

## Integral literals

The integral literal types are the same as in Java:

证书类型变量和Java里的一样

* byte
* char
* short
* int
* long
* java.lang.BigInteger

You can create integral numbers of those types with the following declarations:

可以通过以下声明方式创建整数类型变量
```groovy
// primitive types
byte  b = 1
char  c = 2
short s = 3
int   i = 4
long  l = 5

// infinite precision
BigInteger bi =  6
```
如果使用`def`关键字, 整型类型会发生改变：它会自动适配成能够存储number类型的类型
```groovy
def a = 1
assert a instanceof Integer

// Integer.MAX_VALUE
def b = 2147483647
assert b instanceof Integer

// Integer.MAX_VALUE + 1
def c = 2147483648
assert c instanceof Long

// Long.MAX_VALUE
def d = 9223372036854775807
assert d instanceof Long

// Long.MAX_VALUE + 1
def e = 9223372036854775808
assert e instanceof BigInteger
```
As well as for negative numbers:
```groovy
def na = -1
assert na instanceof Integer

// Integer.MIN_VALUE
def nb = -2147483648
assert nb instanceof Integer

// Integer.MIN_VALUE - 1
def nc = -2147483649
assert nc instanceof Long

// Long.MIN_VALUE
def nd = -9223372036854775808
assert nd instanceof Long

// Long.MIN_VALUE - 1
def ne = -9223372036854775809
assert ne instanceof BigInteger
```

### Alternative non-base 10 representations

#### Binary literal

在Java6以前和Groovy中,number类型可以是小数, 8进制和16进制. 但是在Java7和Groovy2中,可以使用0b前缀表示二进制数据.
```groovy
int xInt = 0b10101111
assert xInt == 175

short xShort = 0b11001001
assert xShort == 201 as short

byte xByte = 0b11
assert xByte == 3 as byte

long xLong = 0b101101101101
assert xLong == 2925l

BigInteger xBigInteger = 0b111100100001
assert xBigInteger == 3873g

int xNegativeInt = -0b10101111
assert xNegativeInt == -175
```
#### Octal literal

8进制的电话,只需要开头是0后跟要表示的8进制数即可.
```groovy
int xInt = 077
assert xInt == 63

short xShort = 011
assert xShort == 9 as short

byte xByte = 032
assert xByte == 26 as byte

long xLong = 0246
assert xLong == 166l

BigInteger xBigInteger = 01111
assert xBigInteger == 585g

int xNegativeInt = -077
assert xNegativeInt == -63
```
#### Hexadecimal literal

Hexadecimal numbers are specified in the typical format of 0x followed by hex digits.

16进制的电话,只需要开头是0x后跟要表示的16进制数即可.
```groovy

int xInt = 0x77
assert xInt == 119

short xShort = 0xaa
assert xShort == 170 as short

byte xByte = 0x3a
assert xByte == 58 as byte

long xLong = 0xffff
assert xLong == 65535l

BigInteger xBigInteger = 0xaaaa
assert xBigInteger == 43690g

Double xDouble = new Double('0x1.0p0')
assert xDouble == 1.0d

int xNegativeInt = -0x77
assert xNegativeInt == -119
```

## Decimal literals

小数字面量和在java 里一样
* float
* double
* java.lang.BigDecimal

可以通过下面的方式创建小数类型的number
```groovy
// primitive types
float  f = 1.234
double d = 2.345

// infinite precision
BigDecimal bd =  3.456
```
Decimals can use exponents, with the e or E exponent letter, followed by an optional sign, and a integral number representing the exponent:


```groovy
assert 1e3  ==  1_000.0
assert 2E4  == 20_000.0
assert 3e+1 ==     30.0
assert 4E-2 ==      0.04
assert 5e-1 ==      0.5
```
Conveniently for exact decimal number calculations, Groovy choses java.lang.BigDecimal as its decimal number type. In addition, both float and double are supported, but require an explicit type declaration, type coercion or suffix. Even if BigDecimal is the default for decimal numbers, such literals are accepted in methods or closures taking float or double as parameter types.

Decimal numbers can’t be represented using a binary, octal or hexadecimal representation.


## Underscore in literals

When writing long literal numbers, it’s harder on the eye to figure out how some numbers are grouped together, for example with groups of thousands, of words, etc. By allowing you to place underscore in number literals, it’s easier to spot those groups:


```groovy
long creditCardNumber = 1234_5678_9012_3456L
long socialSecurityNumbers = 999_99_9999L
double monetaryAmount = 12_345_132.12
long hexBytes = 0xFF_EC_DE_5E
long hexWords = 0xFFEC_DE5E
long maxLong = 0x7fff_ffff_ffff_ffffL
long alsoMaxLong = 9_223_372_036_854_775_807L
long bytes = 0b11010010_01101001_10010100_10010010
```

## Number type suffixes

我们可以通过添加后缀的方式强制指定一个数字的类型(包含二进制,八进制和十六进制)
```java
Type			Suffix
BigInteger		G or g
Long			L or l
Integer			I or i
BigDecimal		G or g
Double			D or d
Float			F or f
```
```groovy
assert 42I == new Integer('42')
assert 42i == new Integer('42') // lowercase i more readable
assert 123L == new Long("123") // uppercase L more readable
assert 2147483648 == new Long('2147483648') // Long type used, value too large for an Integer
assert 456G == new BigInteger('456')
assert 456g == new BigInteger('456')
assert 123.45 == new BigDecimal('123.45') // default BigDecimal type used
assert 1.200065D == new Double('1.200065')
assert 1.234F == new Float('1.234')
assert 1.23E23D == new Double('1.23E23')
assert 0b1111L.class == Long // binary
assert 0xFFi.class == Integer // hexadecimal
assert 034G.class == BigInteger // octal
```
## Math operations

尽管接下来我们还要详细讨论操作符, 但是鉴于数学操作符的重要性, 现在我们还是要先讨论其行为和返回类型

* byte, char, short 和 int 之间的二进制计算返回的是int类型
* byte, char, short 和 int 之间的二进制计算中涉及到long的话, 那么返回的就是long类型
* BigInteger 与任何整数类型的二进制计算 返回的结果都是BigInteger类型
* float, double 和 BigDecimal 之间的二进制计算返回的结果都是double类型
* 俩个BigDecimal之间的二进制运算返回的都是BigDecimal类型.

The following table summarizes those rules:
```groovy

```

由于Groovy提供了操作符重载功能, BigInteger和BigDecimal之间的算术运算也得以实现, 但是在Java中需要调用一些方法才能计算这些不同类型的数字.

### The case of the division operator

The division operators / (and /= for division and assignment) produce a double result if either operand is a float or double, and a BigDecimal result otherwise (when both operands are any combination of an integral type short, char, byte, int, long, BigInteger or BigDecimal).

BigDecimal division is performed with the divide() method if the division is exact (ie. yielding a result that can be represented within the bounds of the same precision and scale), or using a MathContext with a precision of the maximum of the two operands' precision plus an extra precision of 10, and a scale of the maximum of 10 and the maximum of the operands' scale.

For integer division like in Java, you should use the intdiv() method, as Groovy doesn’t provide a dedicated integer division operator symbol.

除法操作符`/`(和`/=`)会得到一个double类型的结果,

### The case of the power operator

Groovy 里有一种强大的操作符`**`, 这个操作符带有base和exponent俩个参数. 这个操作符的结果依赖于它的操作数和操作结果.Groovy使用下面的规则来决定该操作符的返回类型

#### 如果exponent为小数类型
```java
1. 如果结果能表示为Integer类型,那就返回Integer类型
2. 否则如果结果能表示为Long类型,那就返回Long类型
3. 否则的话就返回Double
```

#### 如果exponent为整数类型
```
1. 如果exponent负数负数, 那就返回Integer, Long 或者Double,
2. 如果exponent是正数或者0, 那就要根据base来判断了
	A. 如果base是 BigDecimal, 那就返回BigDecimal类型
	B. 如果base是 BigInteger, 那就返回BigInteger类型
	C. 如果base是 Integer, 那就返回Integer类型, 如果返回的值超过Integer范围的话,就返回BigInteger
	D. 如果base是 Long, 那就返回Long类型, 如果返回的值超过Long范围的话,就返回BigInteger
```

#### 示例
```groovy
// base and exponent are ints and the result can be represented by an Integer
assert    2    **   3    instanceof Integer    //  8
assert   10    **   9    instanceof Integer    //  1_000_000_000

// the base is a long, so fit the result in a Long
// (although it could have fit in an Integer)
assert    5L   **   2    instanceof Long       //  25

// the result can't be represented as an Integer or Long, so return a BigInteger
assert  100    **  10    instanceof BigInteger //  10e20
assert 1234    ** 123    instanceof BigInteger //  170515806212727042875...

// the base is a BigDecimal and the exponent a negative int
// but the result can be represented as an Integer
assert    0.5  **  -2    instanceof Integer    //  4

// the base is an int, and the exponent a negative float
// but again, the result can be represented as an Integer
assert    1    **  -0.3f instanceof Integer    //  1

// the base is an int, and the exponent a negative int
// but the result will be calculated as a Double
// (both base and exponent are actually converted to doubles)
assert   10    **  -1    instanceof Double     //  0.1

// the base is a BigDecimal, and the exponent is an int, so return a BigDecimal
assert    1.2  **  10    instanceof BigDecimal //  6.1917364224

// the base is a float or double, and the exponent is an int
// but the result can only be represented as a Double value
assert    3.4f **   5    instanceof Double     //  454.35430372146965
assert    5.6d **   2    instanceof Double     //  31.359999999999996

// the exponent is a decimal value
// and the result can only be represented as a Double value
assert    7.8  **   1.9  instanceof Double     //  49.542708423868476
assert    2    **   0.1f instanceof Double     //  1.0717734636432956
```


# Booleans
Boolean是一种特殊的数据类型, 他们的值只有俩种情况：true 和 false.
```groovy
def myBooleanVariable = true
boolean untypedBooleanVar = false
booleanField = true
```
true and false are the only two primitive boolean values. But more complex boolean expressions can be represented using logical operators.

In addition, Groovy has special rules (often referred to as Groovy Truth) for coercing non-boolean objects to a boolean value.


# IO
## 读文件
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

## 写文件

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

## 遍历文件

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

## 序列化

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

## 执行命令

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

# 集合

Groovy 语言层面上就支持多种集合类型,包括list, map, range. 大多数类型集合都是基于java的集合框架,而且Groovy development kit对这些集合内置很多快捷方法.
## Lists

Groovy使用了一种被`[]`括起来,值通过`,`分割的语法 定义list. Groovy list 采用的是 JDK里`java.util.List`的实现, 因为它自身并没有定义自己的集合类.
Groovy list 的默认实现是`java.util.ArrayList`, 在后面我们可以看到其他形式的list

```groovy
def numbers = [1, 2, 3]         (1)

assert numbers instanceof List  (2)
assert numbers.size() == 3      (3)
```

1. 我们定义了一个Number类型的List,然后将这个list分配给一个变量
2. 判断list是 Java’s `java.util.List` interface 的实例
3. list的大小可以通过size()来进行查询, 例子中也给我们展示了这个list确实包含3个元素

在上面的list中,我们使用的是同类元素的list, 但其实Groovy list中的数据类型还可以不一样：
```groovy
def heterogeneous = [1, "a", true]  (1)
```
1. 我们定义了一个包含有number,string,boolean 三个类型的list

在上面我们提到过, list实际上是`java.util.ArrayList`实例, 但其实list还可以是其他不同类型的实例, 下面我们通过操作符或者显式类型声明来强制指定 list使用不同的List实现
```groovy
def arrayList = [1, 2, 3]
assert arrayList instanceof java.util.ArrayList

def linkedList = [2, 3, 4] as LinkedList    (1)
assert linkedList instanceof java.util.LinkedList

LinkedList otherLinked = [3, 4, 5]          (2)
assert otherLinked instanceof java.util.LinkedList
```
1. 我们使用操作符强制将类型显式地声明为`java.util.LinkedList`
2. 我们使用显式声明方式, 将list声明为`java.util.LinkedList`

我们可以通过`[]`下标操作符来访问list中的元素(读写都可以). 下标既如果是正数的话,那就从左到右访问元素, 如果下标是负数那就从右到左访问元素. 我们好可以使用`<<`操作符向list里追加元素
```groovy
def letters = ['a', 'b', 'c', 'd']

assert letters[0] == 'a'     (1)
assert letters[1] == 'b'

assert letters[-1] == 'd'    (2)
assert letters[-2] == 'c'

letters[2] = 'C'             (3)
assert letters[2] == 'C'

letters << 'e'               (4)
assert letters[ 4] == 'e'
assert letters[-1] == 'e'

assert letters[1, 3] == ['b', 'd']         (5)
assert letters[2..4] == ['C', 'd', 'e']    (6)
```

1. 访问第一个元素(从这可以看出,list的下标是从0开始的)
2. 通过-1 下标访问list中的最后一个元素.
3. 使用下标对list中第三个元素重新赋值
4. 使用`<<`向list尾部添加一个元素
5. 一次性访问list中俩个元素,这个操作的结果是返回一个包含俩个元素的新的list
6. 使用值域符来访问list中一定范围内的值.

由于list支持多种不同类型的元素, 那么list中也可以包含list,这样就可以制造出多维list
```groovy
def multi = [[0, 1], [2, 3]]     (1)
assert multi[1][0] == 2          (2)
```

1. 定义了一个包含Number类型list的list
2. 访问外层的第二个元素(第二个list), 然后访问内部list的第一个元素(第二个list的第一个元素)

### List literals

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

### List as a boolean expression

list还可以计算出boolean表达式.
```groovy
assert ![]             // an empty list evaluates as false

//all other lists, irrespective of contents, evaluate as true
assert [1] && ['a'] && [0] && [0.0] && [false] && [null]
```

### Iterating on a list

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

### Manipulating lists

#### Filtering and searching

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

#### Adding or removing elements

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

#### Set operations

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

#### Sorting

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

#### Duplicating elements

`roovy development kit`还通过重载操作符的方式, 内部提供了一些方法进行list元素复制.
```groovy
assert [1, 2, 3] * 3 == [1, 2, 3, 1, 2, 3, 1, 2, 3]
assert [1, 2, 3].multiply(2) == [1, 2, 3, 1, 2, 3]
assert Collections.nCopies(3, 'b') == ['b', 'b', 'b']

// nCopies from the JDK has different semantics than multiply for lists
assert Collections.nCopies(2, [1, 2]) == [[1, 2], [1, 2]] //not [1,2,1,2]
```

## Arrays

Groovy 数组重用了list符号, 但是如果想要创建数组, 那么就必须强制地显式定义数组类型
```groovy
String[] arrStr = ['Ananas', 'Banana', 'Kiwi']  (1)

assert arrStr instanceof String[]    (2)
assert !(arrStr instanceof List)

def numArr = [1, 2, 3] as int[]      (3)

assert numArr instanceof int[]       (4)
assert numArr.size() == 3
```

1. 使用显式变量类型定义了一个字符串数组
2. 断言刚才创建的数组是否是string类型
3. 使用操作符定义一个int数组
4. 断言刚才创建的数组是否是int类型

我们也可以创建出一个多维数组
```groovy
def matrix3 = new Integer[3][3]         (1)
assert matrix3.size() == 3

Integer[][] matrix2                     (2)
matrix2 = [[1, 2], [3, 4]]
assert matrix2 instanceof Integer[][]
```
1. 我们指定了新数组的边界
2. 当然我们也可以不指定它的边界

访问数组元素和访问list元素的方式相同
```groovy
String[] names = ['Cédric', 'Guillaume', 'Jochen', 'Paul']
assert names[0] == 'Cédric'     (1)

names[2] = 'Blackdrag'          (2)
assert names[2] == 'Blackdrag'
```
1	Retrieve the first element of the array
2	Set the value of the third element of the array to a new value
1. 检索数组中第一个元素
2. 对数组中第三个元素重新赋值

Groovy不支持Java数组初始化语法, 因为Java数组中的花括号可能被会Groovy无解成闭包

## Maps
有时候我们在其他语言中称map为 字典或者关联数组. Map将key和value关联起来, 在Groovy中map被`[]`括起来, 通过`,`分割键值对, 键值通过`:`分割
```groovy
def colors = [red: '#FF0000', green: '#00FF00', blue: '#0000FF']   (1)

assert colors['red'] == '#FF0000'    (2)
assert colors.green  == '#00FF00'    (3)

colors['pink'] = '#FF00FF'           (4)
colors.yellow  = '#FFFF00'           (5)

assert colors.pink == '#FF00FF'
assert colors['yellow'] == '#FFFF00'

assert colors instanceof java.util.LinkedHashMap
```

1. 我们定义了一个string类型的代表颜色名字的数组,
2. 然后使用下标来检索map中是否包含red这个key
3. 我们还可以直接使用`.`来索引到某个key
4. 我们可以使用下标向map中添加一个新的键值对
5. 我们也可以使用`.`添加一个新的键值对

Groovy创建的map类型默认的是`java.util.LinkedHashMap`

当你想要访问一个不存在的key时：
```groovy
assert colors.unknown == null
```
你将检索出一个null的结果

在上面的例子中我们使用的是以string作为key, 但是你还可以使用其他类型作为map的key：

```groovy
def numbers = [1: 'one', 2: 'two']

assert numbers[1] == 'one'
```

我们使用了number作为了map新的key类型, number类型就会直接被解释为number类型, 因此Groovy不会像先前那样创建一个string类型的key. 但是假设你想要传递一个变量作为key,是变量的值作为key：

```groovy
def key = 'name'
def person = [key: 'Guillaume']      (1)

assert !person.containsKey('name')   (2)
assert person.containsKey('key')     (3)
```
1. 与`\'Guillaume'` 关联的key实际上是`"key"`这个字符串, 而不是这个key的引用值`'name'`
2. map中不包含`'name'`key
3. 取而代之的是map中包含一个`"key"`的字符串

你可以向map中传递一个引号字符串作为key,例如`["name": "Guillaume"]`.

```groovy
person = [(key): 'Guillaume']        (1)

assert person.containsKey('name')    (2)
assert !person.containsKey('key')    (3)
```
1	This time, we surround the key variable with parentheses, to instruct the parser we are passing a variable rather than defining a string key
2	The map does contain the name key
3	But the map doesn’t contain the key key as before
1.
2.
3.

### Map literals

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

### Map property notation

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

### Iterating on maps

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

### Manipulating maps

#### Adding or removing elements

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

#### Keys, values and entries

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


#### Filtering and searching

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

#### Grouping

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

## Ranges

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

# JsonSlurper

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

## Parser Variants

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

## JsonOutput

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

# Syntax enhancements for collections

## GPath support

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

## Spread operator

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

## Slicing with the subscript operator

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