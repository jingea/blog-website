category: linux
date: 2015-10-15
title: Linux /proc/net/dev
---
```shell
[root@cvs /]# cat /proc/net/dev
Inter-|   Receive                                                |  Transmit
 face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets errs drop fifo colls carrier compressed
    lo:365528559149 865504543    0    0    0     0          0         0 365528559149 865504543    0    0    0     0       0          0
   em1:542483270223 575346473    0    0    0    62          0   8267561 580200919340 586706511    0    0    0     0       0          0
   em2:       0       0    0    0    0     0          0         0        0       0    0    0    0     0       0          0
   em3:       0       0    0    0    0     0          0         0        0       0    0    0    0     0       0          0
   em4:       0       0    0    0    0     0          0         0        0       0    0    0    0     0       0          0
```

Inter                                                     
* `face`:接口的名字

Receive
* `bytes`: 收发的字节数   
* `packets`: 收发正确的包量
* `errs`: 收发错误的包量
* `drop`: 收发丢弃的包量
* `fifo`:
* `frame`:
* `compressed`:
* `multicast`:

Transmit
* `bytes`: 收发的字节数   
* `packets`: 收发正确的包量
* `errs`: 收发错误的包量
* `drop`: 收发丢弃的包量
* `fifo`:
* `colls`:
* `carrier`:
* `compressed`:
