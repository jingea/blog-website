category: 编程语言
date: 2015-04-08
title: haskell
---

#类型系统

## 数据类型
在Haskell中数据只是函数的一种方言,他们并没有本质上的区别.
在Haskell中所有的数据类型都必须首字母都必须大写.

在GHCI中我们可以通过`::t`命令来查看一个数据类型或者函数类型.

我们可以通过下面的语法声明一个数据
```
var :: 数据类型
var = 数据初始值
```
或者我们可以将这俩行并为一行
```
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


## Eq
```haskell

```

## Ord
```haskell

```

## Enum
```haskell

```

## Bounded
```haskell

```

## Num
```haskell

```

## Show
```haskell

```


# 表达式
## 条件表达式

```haskell
isOne :: Int -> Bool
isOne arg =
    if arg == 1 then True
    else False
```

## 情况分析表达式
与`switch case`类似,只不过情况分析表达式没有`break`, 使用`_`作为通配符.
```haskell
month :: Int -> Int
month n = case n of
    1 -> 31
    2 -> 28
    12 -> 31
    _ -> error "error"
```

## 守卫表达式

```haskell
abs :: Num a => a -> a
abs n | n > 0 = n
      | otherwise = -n
```

## 匹配模式表达式

```haskell
month :: Int -> Int
month 1 = 31
month 2 = 28
month 3 = 21
month 12 = 31
month _ = error "error"
```

# 运算符
```
优先级9 : !!, .
优先级8 : ^, ^^, **
优先级7 : *, /, div,   mod, rem, quot
优先级6 : +, -
优先级5 : :, ++
优先级4 : ==, /=, <, <=, >, >=,     elem, notElem
优先级3 : &&
优先级2 : ||
优先级1 : >>, >>=
优先级0 : $, $!, $!!seq
```
> 凡是英文运算符,其前后都必须带有`标点

# 函数
我们采用如下格式定义一个函数
```
函数名 :: 参数1的类型 -> 参数2的类型 -> ... -> 结果类型 (1)
函数名 参数1 参数2 ... = 函数体                         (2)
```
1. 定义函数签名
2. 定义函数

下面我们举例出多种函数定义变体形式:

### 带有类型类的函数定义

```haskell
add :: Num t => t -> t -> t
add x y = x + y
```

### 带有多个类型的函数定义

```haskell
add :: (Show t, Int t) => t -> t -> t
add x y = x + y
```

#### 不带有类型类的函数定义
```haskell
add :: Int -> Int -> Int
add x y = x + y
```

#### 函数定义
```haskell
add x y = x + y :: Int
```

#### 类型自动推断的函数定义
```haskell
add x y = x + y
```

#### 函数后跟'
在函数名后加一个`'`,与原函数这代表着俩个函数.
```haskell
add' :: Num t => t -> t -> t
add' x y = x + y

add :: Num t => t -> t -> t
add x y = x + y

```

## 函数类型
### 柯里化函数
当调用一个N参数的函数时, 传递M个参数(N < M),那么该参数返回的结果也是一个函数.这个过程称为柯里化.

但是并不是每种函数都可以这么调用,只有下面形式的函数才可以这么调用.
```haskell
add :: Num t => t -> t -> t
add x y = x + y
```

当我们只向`add`函数传递一个参数`5`的时候,我们会得到下面一个这样子的函数:
```haskell
add 5 y = 5 + y

函数类型为:
add :: Num t => t -> t
```

### 偏函数
如果调用函数时,参数列表不完整,这时就称为函数的不完全应用,也称为偏函数.


### 非柯里化函数
非柯里化的函数,必须在调用的时候,将所有参数都放到元组中,然后传递给函数.
```haskell
add :: Num t => (t ,t) -> t
add (x, y) = x + y
```

### 多态函数
```haskell

```

```haskell

```

### 重载类型函数
```haskell

```

```haskell

```

## lambada

## 参数绑定
### let...in...
`let`里定义的部分会在函数体中进行替换
#### 替换表达式
```haskell
s :: Double -> Double -> Double -> Double
s a b c =
    let p = (a + b + c) / 2
    in sqrt (p * (p - a) * (p - b) * (p - c))
```
#### 替换多个表达式
```haskell

```
#### 替换函数
```haskell

```

### where
```haskell
s :: Double -> Double -> Double -> Double
s a b c = sqrt (p * (p - a) * (p - b) * (p - c))
    where p = (a + b + c) / 2
```

#### 常用函数
* 恒值函数id

```haskell

```
* 常数函数const

```haskell

```
* 参数反置函数flip

```haskell

```
* 错误函数error

```haskell

```
* undifine函数

```haskell

```
* min/max函数

```haskell

```

## 内置函数
#### 列表函数
* null

```haskell

```
* length

```haskell

```
* !!

```haskell

```
* reverse

```haskell

```
* head

```haskell

```
* last

```haskell

```
* tail

```haskell

```
* init

```haskell

```
* map


```haskell

```
* filter

```haskell

```
* take

```haskell

```
* drop

```haskell

```
* span

```haskell

```
* break

```haskell

```
* takeWhile

```haskell

```
* dropWhile

```haskell

```
* spiltAt

```haskell

```
* repeat

```haskell

```
* replicate

```haskell

```
* any

```haskell

```
* all

```haskell

```
* elem

```haskell

```
* notelem

```haskell

```
* iterate

```haskell

```
* until

```haskell

```
* zip

```haskell

```
* concat

```haskell

```
* concatMap

```haskell

```

#### 字符串
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


#### 字符库
* Data.char

```haskell

```

#### 位函数库
* Data.Bits

```haskell

```
*

```haskell

```
*

```haskell

```

*

```haskell

```
*

```haskell

```
*

```haskell

```
