category: 编程语言
tag: python2
date: 2015-08-08
title: PYTHON2 面向对象
---

### self
类的方法与普通的函数只有一个特别的区别——它们必须有一个额外的第一个参数名称`self`
```python

```

### 创建一个类
```python
class Person:
    pass

p = Person()
print p
```

### 对象的方法
```python
class Person:
    def run(self):
        print("run")

p = Person()
p.run()
```

#### __init__方法
`__init__`方法在类的一个对象被建立时，马上运行
```python
class Person:
    def run(self):
        print("run")
    def __init__(self):
        print("init")

p = Person()
p.run()
```

#### __del__方法
```python
class Person:
    def __init__(self):
        print("init")
    def __del__(self):
        print("__destory__")

p = Person()
```

### 变量
* 类的变量: 由一个类的所有对象（实例）共享使用。只有一个类变量的拷贝，所以当某个对象对类的变量做了改动的时候，这个改动会反映到所有其他的实例上。

* 对象的变量: 由类的每个对象/实例拥有。因此每个对象有自己对这个域的一份拷贝，即它们不是共享的，在同一个类的不同实例中，虽然对象的变量有相同的名称，但是是互不相关的。通过一个例子会使这个易于理解。

```python
class Father:
    age = 0

father = Father()
father.age = 10
Father.age = 20
print(father.age)
print(Father.age)
```

#### 权限控制
对象的属性(变量和方法)如果名字以`__`开头则不能被外部访问,但是如果名称构成形式为`__xxx__`则被称为特殊属性,是可以被外界访问的.

### 继承
```python
class Father:
    name = "Tom"
    def run(self):
        print("run")

class Son(Father):
    pass

son = Son()
print(son.name)
son.run()

```

#### `__init__`, `__del__`在继承中的使用
Python不会自动调用父类的constructor
```python
class Mother:
    pass

class Father:
    name = "Tom"
    def run(self):
        print("run")
    def __init__(self):
        print("Father init")
    def __del__(self):
        print("Father del")

class Son(Father, Mother):
    def __init__(self):
        print("Son init")
    def __del__(self):
        print("Son del")

son = Son()
print(son.name)
son.run()
```


 
