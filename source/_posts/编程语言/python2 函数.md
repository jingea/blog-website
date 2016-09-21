category: 编程语言
date: 2015-08-07
title: PYTHON2 函数
---

### 定义一个不带参数的函数
```python
# 定义一个不带参数的函数
def printHelloworld():
    print("hello world")

## 调用函数
printHelloworld()
```

### 定义一个带参数的函数
```python
# 定义一个带参数的函数
def printHelloworld(saywhat):
    print(saywhat)

## 调用函数
printHelloworld("hello world")
```

### 函数中的局部变量
```python
# 定义一个带参数的函数
def printHelloworld(saywhat):
    value = saywhat
    print(value)

## 调用函数
printHelloworld("hello world")
```

当在函数内部修改了局部变量之后,并不会影响脚本中的变量
```python
# 定义一个带参数的函数
def printHelloworld(saywhat):
    print(saywhat)
    saywhat = "new value"
    print(saywhat)

# 调用函数
str = "hello world"
printHelloworld(str)
print(str)
```

### 使用global语句
```python
# 定义一个带参数的函数
def printHelloworld():
    global saywhat ## 此处不可进行初始化
    saywhat = "new value"
    print(saywhat)

# 调用函数
printHelloworld()
print(saywhat)
```

### 默认参数值
我们也可以给函数参数指定默认值
```python
def printHelloworld(str, str1="str1 value", str2="str2 value"):
    print(str + " " + str1 + " " + str2)

# 调用函数
printHelloworld("123", str2="789")
```

### 可变参数
python函数也可以接受不定参数
```python
def f(*args):
	print(args)

f(1)
f(1, 2)
```
输出为
```python
(1,)
(1, 2)
```

### return返回值
```python
def printHelloworld(str, str1="str1 value", str2="str2 value"):
    print(str)
    if str1=="str1 value" :
        return "nil value"
    print(str1)
    print(str2)

# 调用函数
result = printHelloworld("123", str2="789")
print(result)

result = printHelloworld("123", str1="789")
print(result)
```

### 高阶函数
如果函数A里的参数或者返回值是另一个函数,那么函数A就是高阶函数.
```python
def add5(v1):
	return v1 + 5;

def add(v1, v2, add5):
	return add5(v1) + add5(v2)

print(add(2, 4, add5))
```

#### 内置高阶函数
`map()`函数：它接受一个函数和一个列表,然后遍历列表中的每个元素作用在函数参数中
```python
print(map(add5, [1, 2, 3]))

// 结果为
[6, 7, 8]
```

`reduce()`函数
```python

```

`filter()`函数
```python

```

### 闭包
在2.7版本中必须要如下声明一个闭包
```python
def outerF():
	count = [10]
	def innerF():
		print(count[0])
		count[0] = 20
	return innerF

f = outerF()
f()
f()
```

### 匿名函数
python通过lambda表达式完成匿名函数
```python
def nonameF(f, v1):
	return f(v1)

value = nonameF(lambda x: x + 1, 5)
print(value)
```
不过python只是有限的支持匿名函数, 匿名函数只能是一个表达式,而且不能拥有return,表达式的结果就是返回值

### 偏函数
偏函数就是通过`functools.partial`函数将函数A中的参数指定一个值然后返回一个新的函数
```python
import functools
def fa(var1, var2, var3):
	print(var1 + var2 + var3)

fb = functools.partial(fa, var2=2, var3=3)

fa(1, 2, 3)
fb(1)
```
最后我们看到了相同的结果

### 重定义
我们可以对一个已经存在的函数重新定义行为
```python
def f(*args):
	print(args)


f = lambda : 15
print(f())

n = f

def f():
	return 20

print(n())
print(f())
```
从这一点可以验证在python中函数也是对象.

### 装饰器
装饰器本质上就是一个高阶函数，它接收一个函数作为参数，然后，返回一个新函数。

python通过`@`语法内置实现装饰器
```python
def fb(f):
	print("fb")
	return f

@fb
def fa(var1, var2, var3):
	print(var1 + var2 + var3)

fa(1, 2, 3)
```
上面这个例子每次在调用`fa`方法时都会输出一个`fb`字符串
