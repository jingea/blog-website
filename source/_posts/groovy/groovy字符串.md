category: Groovy
date: 2014-04-08
title: groovy字符串
---
> 本文是对Groovy部分官方文档进行了翻译

在Groovy文本字面量被称为String,这是以字符链的形式出现的. Groovy允许你实例化`java.lang.String`,像  GStrings (`groovy.lang.GString`)那样, (GString还被称为插值字符串)

### 单引号字符
单引号字符串是通过单引号括起来的一列字符
```groovy
'a single quoted string'
```

单引号字符和`java.lang.String`是同一个东西, 同时它也不允许插值的出现
### 字符串连接

Groovy里所有的字符串都可以通过 `+` 连接起来
```groovy
assert 'ab' == 'a' + 'b'
```

### 三重单引号字符串

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

#### 更换特殊字符

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
#### Unicode 转义序列

有一些字符并不能通过键盘输出, 那么此时就可以通过Unicode 转义序列来实现. 例如`backslash`, 在u后跟4个16进制数字即可.

```groovy
'The Euro currency symbol: \u20AC'
```
### 双引号包含的 string

通过双引号包括起来的字符串
```groovy
"a double quoted string"
```
当双引号字符串内没有插值(`${}`)的时候, 那它就等同于`java.lang.String`, 当有插值的时候那么双引号字符串就是`groovy.lang.GString`的实例

#### String 插值

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
#### 特殊插值形式-闭包表达式

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

1. 我们定义了数值为1的number类型变量, 它稍后会作为插值出现在俩个GString中,
2. 我们希望eagerGString 产生的字符串包含着相同的值 1
3. 同样我们也希望lazyGString 产生的字符串包含着相同的值 1
4. 然后我们将number改变一个值.
5. With a plain interpolated expression, the value was actually bound at the time of creation of the GString.
6. But with a closure expression, the closure is called upon each coercion of the GString into String, resulting in an updated string containing the new number value.

An embedded closure expression taking more than one parameter will generate an exception at runtime. Only closures with zero or one parameters are allowed.

#### Inteoperability with Java
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


#### GString and String hashCodes

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

### Triple double quoted string

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

### Slashy string
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

### Dollar slashy string

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

### Characters

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

