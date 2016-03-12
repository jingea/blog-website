category: Groovy
date: 2014-04-08
title: groovy数字
---
> 本文是对Groovy部分官方文档进行了翻译

Groovy支持多种不同的整数字面量和小数字面量 (通过依靠Java数字类型实现)

### Integral literals

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

#### Alternative non-base 10 representations

##### Binary literal

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
##### Octal literal

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
##### Hexadecimal literal

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

### Decimal literals

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


### Underscore in literals

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

### Number type suffixes

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
### Math operations

尽管接下来我们还要详细讨论操作符, 但是鉴于数学操作符的重要性, 现在我们还是要先讨论其行为和返回类型

* byte, char, short 和 int 之间的二进制计算返回的是int类型
* byte, char, short 和 int 之间的二进制计算中涉及到long的话, 那么返回的就是long类型
* BigInteger 与任何整数类型的二进制计算 返回的结果都是BigInteger类型
* float, double 和 BigDecimal 之间的二进制计算返回的结果都是double类型
* 俩个BigDecimal之间的二进制运算返回的都是BigDecimal类型.

由于Groovy提供了操作符重载功能, BigInteger和BigDecimal之间的算术运算也得以实现, 但是在Java中需要调用一些方法才能计算这些不同类型的数字.

#### The case of the power operator

Groovy 里有一种强大的操作符`**`, 这个操作符带有base和exponent俩个参数. 这个操作符的结果依赖于它的操作数和操作结果.Groovy使用下面的规则来决定该操作符的返回类型

##### 如果exponent为小数类型
```java
1. 如果结果能表示为Integer类型,那就返回Integer类型
2. 否则如果结果能表示为Long类型,那就返回Long类型
3. 否则的话就返回Double
```

##### 如果exponent为整数类型
```groovy
1. 如果exponent负数负数, 那就返回Integer, Long 或者Double,
2. 如果exponent是正数或者0, 那就要根据base来判断了
	A. 如果base是 BigDecimal, 那就返回BigDecimal类型
	B. 如果base是 BigInteger, 那就返回BigInteger类型
	C. 如果base是 Integer, 那就返回Integer类型, 如果返回的值超过Integer范围的话,就返回BigInteger
	D. 如果base是 Long, 那就返回Long类型, 如果返回的值超过Long范围的话,就返回BigInteger
```

##### 示例
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


## Booleans
Boolean是一种特殊的数据类型, 他们的值只有俩种情况：true 和 false.
```groovy
def myBooleanVariable = true
boolean untypedBooleanVar = false
booleanField = true
```
true and false are the only two primitive boolean values. But more complex boolean expressions can be represented using logical operators.

In addition, Groovy has special rules (often referred to as Groovy Truth) for coercing non-boolean objects to a boolean value.
