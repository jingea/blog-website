category: redis
date: 2015-04-02
title: Redis 部署
---

[Redis安装 使用](http://www.redis.cn/download.html)

下载，解压和安装：
```
$ wget http://download.redis.io/releases/redis-2.8.19.tar.gz
$ tar xzf redis-2.8.19.tar.gz
$ cd redis-2.8.19
$ make
```

编译后的可执行文件在src目录中，可以使用下面的命令运行Redis:
```
$ src/redis-server
```

你可以使用内置的客户端连接Redis:
```
$ src/redis-cli
redis> set foo bar
OK
redis> get foo
"bar"
```

### 启动redis服务器
```shell
#!/bin/bash
cd redis-2.8.19
src/redis-server
```
