category: : 编程语言
tag: python2
date: 2015-08-08
title: PYTHON2 模块
---

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

