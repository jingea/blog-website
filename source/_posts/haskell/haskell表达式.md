category: Haskell
date: 2015-04-08
title: haskell表达式
---

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
