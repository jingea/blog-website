category: Haskell
date: 2015-04-08
title: haskell函数
---
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

### 重载类型函数
```haskell

```

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
* where
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

