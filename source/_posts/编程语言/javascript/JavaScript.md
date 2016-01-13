category: 编程语言
tag: JavaScript
date: 2015-09-08
title: JavaScript变量和流程控制
---
JavaScript是一门基于原型继承的函数式的面向对象的编程语言,对象可以直接从其他对象继承属性,且JavaScript是无类型的.

## 数据类型

### 数字
JavaScript只有一个数字类型,它在内部被表示为64位的浮点数. 它没有分离出整数类型,因此1和1.0的值是相同的

NaN是一个数值，它表示一个不能产生正常结果的运算结果。`NaN`不等于任何值，包括它自己。可以使用函数`isNaN`来检测`NaN`。

### 字符串
字符串可以由一对单引号或者一对双引号构成，可以包含0到多个字符。`\`是转义字符。JavaScript采用Unicode16作为字符集，因此所有的字符都是16位的。

字符串有一个length属性，可以获得字符串长度。

字符串同样也是不可变的。一旦字符串被创建出来就无法改变它。我们同+链接其他字符串创建一个新的字符串。俩个包含着相同字符且字符顺序也相同的字符被认为是同一个字符串。`===`进行字符串判断。

## 变量
我们通过 `var`关键字来声明一个变量

### 函数私有变量
JavaScript通过函数管理作用域。在函数内部声明的变量只在这个函数内部可用，而在函数外面不可用。

### 全局变量
每个JavaScript环境有一个全局对象，当你在任意的函数外面使用this的时候可以访问到。你创建的每一个全局变量都成了这个全局对象的属性。

## 控制流程

我们可以通过条件语句(if和switch),循环语句（while，for和do）强制跳转语句（break,return，throw）和函数调用来改变执行序列。

### if
进行if判断时，下列值被作为假：
* false
* null
* 空字符串 `''`
* 数字0
* 数字NaN

### switch
其表达式的值和case条件进行匹配。表达式可以是字符串或者数字。case表达式不一定必须是常量
```javascript
switch (num)
{
	case 1:
	  x="输入正确";
	  break;
	default:
	  x="输入错误";
}
```

### while
```javascript
while (i<5)
{
	sum += i;
}
```

### for
```javascript
for (var i = 0; i<5; i++)
{
	sum += i;
}
```
for...in
```javascript
var array = [1, 2]
for (i in array)
{
	sum += i;
}
```
