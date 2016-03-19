category: python2
date: 2015-08-08
title: PYTHON2 多线程
---
## 启动多线程
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
