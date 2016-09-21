category: 编程语言
date: 2015-08-06
title: PYTHON2 基础语法
---
## 

## 控制流程
### if
```python
tmp = 0
if tmp > 0 :
    print(">")
elif tmp < 0 :
    print("<")
else :
    print("=")
```

### while
```python
tmp = 0
while tmp < 3 :
    print(tmp)
    tmp +=1
else :
    print("over")
```

### for
```python
for i in [0,1,2,3,4]:
    print(i)

    if i > 2 :
        break
    else :
        continue

else:
    print('loop over')
```

或者
```python
#  从0开始步增到10
for i in range(0, 10, 1)
    print(i)
```

## 模块
模块是一个包含函数和变量的文件。为了在其他程序中重用模块，模块的文件名必须以.py为扩展名。

### 使用`sys`模块
```python
import sys

for argv in sys.argv :
    print(argv)
```

使用`from..import..`, `import`可以使用`*`
```python
from sys import argv

for argvtmp in argv :
    print(argvtmp)
```

模块的name,下面的语法输出当前模块的name
```python
print(__name__)
```

### 自定义模块
* 建立`mymodule.py`文件
```python
# Filename: mymodule.py

def printModuleName():
    print(__name__)
```
* 建立`test_mymodule.py`文件
```python
import mymodule

mymodule.printModuleName()
```
1. 需要注意的是`mymodule.py`文件的`Filename`必须和文件名相同
2. 如果`module`的name是`__main__`说明这个module是由用户启动的

## 集合

### 列表
* 声明一个列表 `list = [123, "ad"]`
* 索引第一个元素 `list[0]`
* 在尾部添加一个元素 `list.append(2.56)`
* 对第一个元素重新赋值 `list[0] = "0"`
* 获取列表长度 `len(list)`
* 删除第一个元素 `del list[0]`



### 元组
元组和列表十分类似，只不过元组和字符串一样是 不可变的 即你不能修改元组
```python
tumple = (123, "adf")
print(tumple[0])
print(len(tumple))
```

### 字典
```python
map = {
       "key1":"value1",
       "key2":"value2",
       "key3":"value3",
       }
print(map)
print(len(map))
print(map["key1"])
del map["key1"]
print(map)

for key in map:
    print(key + "   " + map[key])
if "key2" in map:
    print("map contains key2")

help(dict)
```

### 序列
序列的两个主要特点是索引操作符和切片操作符
```python
shoplist = ['apple', 'mango', 'carrot', 'banana']

print('Item 0 is', shoplist[0])
print('Item -1 is', shoplist[-1])

### Slicing on a list
print('Item 1 to 3 is', shoplist[1:3])
print('Item 2 to end is', shoplist[2:])
print('Item 1 to -1 is', shoplist[1:-1])
print('Item start to end is', shoplist[:])

### Slicing on a string
name = 'swaroop'
print('characters 1 to 3 is', name[1:3])
print('characters 2 to end is', name[2:])
print('characters 1 to -1 is', name[1:-1])
print('characters start to end is', name[:])
```

## 多线程
```python
import socket
import threading
import time

count = 0
def socketSendData():
    client=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    client.connect(('www.baidu.com',80))
    time.sleep(1)


for i in range(0, 20000, 1):
    try:
       t = threading.Thread(target=socketSendData)
       info = t.start()
    except:
       count += 1
       print "Error: unable to start thread  " + str(count)
```

## 异常
捕获异常
```python
try:
   fh = open("test", "w")
except IOError:
   print "Error: open error"
else:
   print "hava open file"
   fh.close()
```
抛出异常`raise [Exception [, args [, traceback]]]`例如
```python
if(num < 100):
  raise RuntimeError("num is greater than 100!")
```

## 面向对象

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

## 字符串函数
### 修改
* `capitalize(...)` : Python capitalize()将字符串的第一个字母变成大写,其他字母变小写。`print "abc".capitalize()`
* `center(...)` : 返回一个原字符串居中,并使用空格填充至长度 width 的新字符串。默认填充字符为空格。`print "abc".center(10, "1")`结果为`111abc1111`
* `expandtabs(...)` : `S.expandtabs(tabsize=8) -> str` 把字符串中的 tab 符号('\t')转为空格，默认的空格数 tabsize 是 8。
* `format(...)` : `"abcd{0}fg".format("sdf")` {0}被替换成sdf
* `join(...)` : `S.join(iterable) -> str` 用于将序列中的元素以指定的字符连接生成一个新的字符串。
* `ljust(...)` : `S.ljust(width[, fillchar]) -> str` 返回一个原字符串左对齐,并使用空格填充至指定长度的新字符串。如果指定的长度小于原字符串的长度则返回原字符串。
* `rjust(...)` : `S.rjust(width[, fillchar]) -> str` 返回一个原字符串右对齐,并使用空格填充至长度 width 的新字符串。如果指定的长度小于字符串的长度则返回原字符串。
* `lower(...)` : `S.lower() -> str` 转换字符串中所有大写字符为小写。
* `lstrip(...)` : `S.lstrip([chars]) -> str` 用于截掉字符串左边的空格或指定字符。
* `replace(...)` : `S.replace(old, new[, count]) -> str` 把字符串中的 old（旧字符串） 替换成 new(新字符串)，如果指定第三个参数max，则替换不超过 max 次。
* `split(...)` : `S.split(sep=None, maxsplit=-1) -> list of strings` 通过指定分隔符对字符串进行切片，如果参数num 有指定值，则仅分隔 num 个子字符串. 另外参考`rsplit(...)`
* `strip(...)` : `S.strip([chars]) -> str` 用于移除字符串头尾指定的字符（默认为空格）。
* `rstrip(...)` : `S.rstrip([chars]) -> str` 删除 string 字符串末尾的指定字符（默认为空格）.
* `swapcase(...)` : `S.swapcase() -> str` 用于对字符串的大小写字母进行转换。
* `translate(...)` : `S.translate(table) -> str` 根据参数table给出的表(包含 256 个字符)转换字符串的字符, 要过滤掉的字符放到 del 参数中。
* `upper(...)` : `S.upper() -> str` 将字符串中的小写字母转为大写字母。
* `zfill(...)` : `S.zfill(width) -> str` 返回指定长度的字符串，原字符串右对齐，前面填充0。
* `title(...)` : `S.title() -> str` 返回"标题化"的字符串,就是说所有单词都是以大写开始，其余字母均为小写(见 istitle())。


### 查找
* `count(...)` : 用于统计字符串里某个字符出现的次数。可选参数为在字符串搜索的开始与结束位置。`"abcdefg".count("c", 2, 4)`结果为`1`
* `find(...)` : `S.find(sub[, start[, end]]) -> int` 检测字符串中是否包含子字符串 str ，如果指定 beg（开始） 和 end（结束） 范围，则检查是否包含在指定范围内，如果包含子字符串返回开始的索引值，否则返回-1。
* `rfind(...)` : `S.rfind(sub[, start[, end]]) -> int` 返回字符串最后一次出现的位置，如果没有匹配项则返回-1。
* `endswith(...)` : 用于判断字符串是否以指定后缀结尾，如果以指定后缀结尾返回True，否则返回False。可选参数"start"与"end"为检索字符串的开始与结束位置。`"abcdefg".endswith("d", 2, 4)`结果为true
* `index(...)` : `S.index(sub[, start[, end]]) -> int`检测字符串中是否包含子字符串 str ，如果指定 beg（开始） 和 end（结束） 范围，则检查是否包含在指定范围内，该方法与 python find()方法一样，只不过如果str不在 string中会报一个异常。
* `rindex(...)` : `S.rindex(sub[, start[, end]]) -> int` 返回子字符串 str 在字符串中最后出现的位置，如果没有匹配的字符串会报异常，你可以指定可选参数[beg:end]设置查找的区间。
* `partition(...)` : `S.partition(sep) -> (head, sep, tail)` Search for the separator sep in S, and return the part before it,the separator itself, and the part after it.  If the separator is not found, return S and two empty strings.
* `rpartition(...)` : `S.rpartition(sep) -> (head, sep, tail)` 类似于 partition()函数,不过是从右边开始查找.
* `startswith(...)` : `S.startswith(prefix[, start[, end]]) -> bool` 用于检查字符串是否是以指定子字符串开头，如果是则返回 True，否则返回 False。如果参数 beg 和 end 指定值，则在指定范围内检查。


### 检测
* `isalnum(...)` : `S.isalnum() -> bool` 判断字符串是否包含字母数字。
* `isalpha(...)` : `S.isalpha() -> bool` 检测字符串是否只由字母组成。
* `isdecimal(...)` : `S.isdecimal() -> bool` 检查字符串是否只包含十进制字符。这种方法只存在于unicode对象。
* `isdigit(...)` : `S.isdigit() -> bool` 检测字符串是否只由数字组成。
* `islower(...)` : `S.islower() -> bool` 检测字符串是否由小写字母组成。
* `isnumeric(...)` : `S.isnumeric() -> bool` 检查是否只有数字字符组成的字符串。这种方法目前只对unicode对象。
* `isspace(...)` : `S.isspace() -> bool` 是否只由空格组成。
* `istitle(...)` : `S.istitle() -> bool` 检测字符串中所有的单词拼写首字母是否为大写，且其他字母为小写。
* `isupper(...)` : `S.isupper() -> bool` 检测字符串中所有的字母是否都为大写。


### 编码
* `encode(...)` : encoding 指定的编码格式编码字符串。errors参数可以指定不同的错误处理方案。`S.encode(encoding='utf-8', errors='strict') -> bytes` 


### 遍历
* `splitlines(...)` : `S.splitlines([keepends]) -> list of strings` 返回一个字符串的所有行，可选包括换行符列表(如果num提供，则为true)


