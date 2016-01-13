category: : 编程语言
tag: python2
date: 2015-08-08
title: PYTHON2 文件操作
---
打开一个文件
```python
f = open(name, [mode], [size])
```
* name: 文件名
* mode: 打开方式
* size: 操作的字节数

### mode值:
* `r`: 只读方式打开(文件必须存在)
* `w`: 只写方式打开(文件不存在创建文件,文件存在清空文件)
* `a`: 追加方式打开(文件不存在创建文件)
* `r+/w+`: 读写方式打开
* `a+`: 读写方式打开
* `rb,wb,ab,rb+,wb+,ab+`: 二进制方式打开

> 注意:如果我们使用非二进制模式输出时`\n(0A)`会被自动替换为`\r\n(0D 0A)`,因此在文件输出时,我们要注意这个问题.

### f对象常用方法
* `read([size])` : 读取文件(size有值则读取size个字节),如果不填写size则读取全部
* `readline([size])` : 每次读取一行(size值为当前行的长度,但是如果每次读取不完的话,下次再调用readline时会继续在当前行读取)
* `readlines([size])` : 读取多行,返回每一行组成的列表. 如果不填写size则读取全部内容(不推荐使用这种方式读取所有行)
* `write(str)` : 将字符串直接写入文件中
* `writelines(lines)`: 将字符串或者字符串列表写入文件中.
* `close()`: 关闭文件操作

我们可以使用for循环遍历整个文件
```python
file = open("demo.txt")
for line in file:
	print(line)
```

### OS模块文件操作
```
fd = os.open(filename, flag, [mode])
```
filename和mode我们通过上面的描述都知道了,现在我们看一下flag属性值(文件打开方式)
* os.O_CREAT : 创建文件
* os.O_RDONLY : 只读方式打开
* os.O_WRONLY : 只写方式打开
* os.O_RDWR : 读写方式打开

示例
```python
fd = os.open("test.txt", os.O_CREAT | os.O_RDWR)
os.write(fd, "helloworld")
```

### 中文乱码
写入文件时,如果输出中文,我们经常会遇到乱码的问题.只需要在python文件顶部加上以下内容就可以了
```
#-*- coding=utf-8 -*-
```

