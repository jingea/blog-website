category: Haskell
date: 2015-04-08
title: haskell类型系统
---

## 数据类型
在Haskell中数据只是函数的一种方言,他们并没有本质上的区别.
在Haskell中所有的数据类型都必须首字母都必须大写.

在GHCI中我们可以通过`::t`命令来查看一个数据类型或者函数类型.

我们可以通过下面的语法声明一个数据
```haskell
var :: 数据类型
var = 数据初始值
```
或者我们可以将这俩行并为一行
```haskell
var = 数据初始值 :: 数据类型
```

### Bool类型

我们声明一个bool类型的数据,并将其初始化为`True`
```haskell
true = True :: Bool
```

### Char类型

单字符类型
```haskell
char = 'a' :: Char

char = '\100' :: Char

char = '\n' :: Char
```

### Int类型
有符号整数,其范围和OS与GHC的位数有关.在32位系统中,其范围就是`-2^31~2^31-1`
```haskell
int = -1 :: Int
```

### Word类型
有符号整数类型,其范围和OS与GHC的位数有关.在32位系统中,其范围就是`0~2^32-1`
```haskell
import Data.Word

word = 1 :: Word
```

### Integer类型
任意精度类型. 可以表示任意整数的大小, 限制它的因素只和OS有关.

当数据不指明类型时,Integer是整数的默认类型
```haskell
integer = 199999 :: Integer
```

### Float类型
单精度浮点小数
```haskell
float = 1.1 :: Float
```

### Double类型
双精度浮点小数
```haskell
double = 1.11111 :: Double
```

### Rational类型
有理数类型
```haskell
rational = 1 / 500 :: Rational
```

### String类型
`String`的类型为`[Char]`
```haskell
string = "char array" :: String
```

### 元组类型
元祖用`(,)`表示,其中的内容称为元件. 元件的个数并不限制(如有俩个元件的称为2元元组).

一旦确定了元件的个数和元件的类型,那他们就是不可再变的.
```haskell
tuple = (123, "abc") :: (Int, [Char])
```

### 列表类型
列表本身就是一个容器,内存可以存放各种类型的数据(包括函数),但是一旦类型确定了,就不可再变.
```haskell
list = [123, 8, 9] :: [Int]
```

#### 拼接列表
采用`x:xs`的形式进行拼接列表, `x`代表一个元素, `xs`代表一个列表.
```haskell
list = [123, 8, 9]

newList = 1 : list
```

#### 多维列表

```haskell
mulList = [[]]  -- 列表中套有一个列表,类似于2维数组

mulList = [[[]]]
```

## 类型别名
我们可以使用`type`关键字将复杂类型起一个简单的名字

```haskell
type NewType = (Int, Int)
```

接下来我们就可以使用这个类型了
```haskell
point :: NewType
point = (1, 2)
```

`type`关键字并没有产生新的类型,只是在编译期将新的类型替换为原来的类型.


## 类型类
Haskell提供了以下的类型类
* Eq
* Ord
* Enum
* Bounded
* Num
* Show

## 字符串
* show

```haskell

```
* read

```haskell

```
* lines

```haskell

```
* unlines

```haskell

```
* word

```haskell

```
* unword

```haskell

```
