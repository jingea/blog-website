category: 编程语言
date: 2015-08-08
title: PYTHON2 文件和网络操作
---
## 文件
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

### 对象常用方法
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
```python
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

### 遍历目录文件
```python
#!/usr/bin/python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

import os.path
rootdir = "C:\\Users\\wangming\\Documents"

for parent, dirnames, filenames in os.walk(rootdir):

    for filename in filenames:
        fullname = os.path.join(parent, filename)
        # print "the full name of the file is:" + fullname #输出文件路径信息

        file = open(fullname)
        for line in file:
            if line.find(")[") > 0:
                print(str(fullname) + " -> " + str(line))

```


### 中文乱码
写入文件时,如果输出中文,我们经常会遇到乱码的问题.只需要在python文件顶部加上以下内容就可以了
```python
#-*- coding=utf-8 -*-
```


## 网络
### Socket服务器
```python
import socket

sock=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
sock.bind(('localhost',8089))
sock.listen(5)

print('tcpServer listen at: %s:%s\n\r' %('localhost',8089))

while True:
    client_sock,client_addr=sock.accept()
    print('%s:%s connect' %client_addr)
    while True:
        recv=client_sock.recv(4096)
        if not recv:
            client_sock.close()
            break
        print('[Client %s:%s said]:%s' % (client_addr[0],client_addr[1],recv))
        client_sock.send('tcpServer has received your message')
        sock.close()
```

## HttpServer
```python
import BaseHTTPServer
import urlparse
class WebRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def do_GET(self):
        """
        """
        print "8090"

server = BaseHTTPServer.HTTPServer(('0.0.0.0',8090), WebRequestHandler)
server.serve_forever()
```

self 还有如下参数
* self.path
* self.client_address
* self.address_string()
* self.command
* self.path
* self.request_version
* self.server_version
* self.sys_version
* self.protocol_version
* self.headers
* self.send_response(200)
* self.end_headers()

### Socket客户端
```python
client=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
client.connect(('localhost',8880))

client.send('2')
recvData=client.recv(1024)
print recvData
```
