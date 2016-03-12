category: Reids
date: 2015-11-19
title: Redis事务
---

## 普通事务
首先介绍普通事务`MULTI`，`EXEC`，`DISCARD`:

* `MULTI`告诉 redis 服务器开启一个事务
* `EXEC`告诉 redis 开始执行事务
* `DISCARD`告诉 redis 取消事务

`MULTI`命令执行后, redis进入事务状态,redis会持续缓存某个客户端的命令(其他客户端处于饥饿状态).
![](https://raw.githubusercontent.com/ming15/blog-website/images/redis/redis-multi.png)
当redis接受到客户端的`EXEC`命令后会开始执行刚才缓存在事务队列里的任务. `DISCARD` 会将事务队列清空.
![](https://raw.githubusercontent.com/ming15/blog-website/images/redis/redis-tranactions.png)
```shell
redis 127.0.0.1:7006> MULTI
OK
redis 127.0.0.1:7006> SET a ""redis 127.0.0.1:7006> SET a ""
QUEUED
redis 127.0.0.1:7006> SET a "a"
QUEUED
redis 127.0.0.1:7006>  EXEC
1) OK
2) OK
redis 127.0.0.1:7006> SET b "b"
OK
redis 127.0.0.1:7006> EXEC
(error) ERR EXEC without MULTI
redis 127.0.0.1:7006> MULTI
OK
redis 127.0.0.1:7006> SET n "n"
QUEUED
redis 127.0.0.1:7006> EXEC
1) OK
redis 127.0.0.1:7006> MULTI
OK
redis 127.0.0.1:7006> SET c "c"
QUEUED
redis 127.0.0.1:7006> DISCARD
OK
redis 127.0.0.1:7006> GET a
"a"
redis 127.0.0.1:7006> GET b
"b"
redis 127.0.0.1:7006> Get c
(error) ERR Operation against a key holding the wrong kind of value
redis 127.0.0.1:7006> GET n
"n"
redis 127.0.0.1:7006>
```
1. 使用`MULTI`命令开启事务
2. 输入一个错误的命令,点击回车,redis并没有报错,说明这个命令确实是被缓存起来了没有执行
3. 使用`SET`命令将a设置为"a"
4. 然后执行事务,我们看到俩条事务都执行完了,但是第一条命令并没有报错
5. 然后再次使用`SET`命令将b设置为"b"
6. 再次执行事务, 并不成功,提示我们要开启事务,说明事务一旦执行完就自动退出了
7. 再次开启事务,然后使用`SET`命令将n设置为"n"
8. 退出事务
9. 接下来我们依次使用`GET`命令获取值,但是n取不到,说明退出事务确实没有执行事务队列里的命令
![](https://raw.githubusercontent.com/ming15/blog-website/images/redis/redis_transaction.png)

## watch机制
下来我们来看一下redis的watch机制
![](https://raw.githubusercontent.com/ming15/blog-website/images/redis/watch1.png)
![](https://raw.githubusercontent.com/ming15/blog-website/images/redis/watch2.png)
![](https://raw.githubusercontent.com/ming15/blog-website/images/redis/watch3.png)
![](https://raw.githubusercontent.com/ming15/blog-website/images/redis/watch4.png)
![](https://raw.githubusercontent.com/ming15/blog-website/images/redis/redis_watched_keys.png)

## pipline机制


参考文章
* [](http://redisbook.readthedocs.org/en/latest/feature/transaction.html)
* [](http://ju.outofmemory.cn/entry/81786)
* [](http://redisdoc.com/topic/transaction.html#id2)
