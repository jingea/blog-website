category: Nginx
date: 2016-03-06
title: Nginx安装与启动,关闭
---
## 安装
在MAC上安装Nginx

使用命令
```
brew install nginx
```
homebrew会自动为我们安装. 程序会安装在
```
/usr/local/Cellar/nginx/1.6.2
```
nginx的配置文件在
```
cd /usr/local/etc/nginx
```

> 一般通过Homebrew安装的程序都会放在`/usr/local/Cellar/`里, 而配置文件会存储在`/usr/local/etc/`里

## 启动
我们可以通过下面这个简单的命令直接启动
```
/usr/bin/nginx
```
或者使用一个经过配置化的参数启动
```
/usr/bin/nginx -t -c ~/mynginx.conf -g "pid /var/run/nginx.pid; worker_processes 2;"
```

## 关闭
一般我们可以通过
```
/usr/bin/nginx -s stop
```
但是我们还可以通过想Nginx 主进程发送信号的方式来关闭它.
```
kill -QUIT $( cat /usr/local/nginx/logs/nginx.pid )
```
一般我们推荐第二种方式, 让Nginx自己去停掉所有的主从进程

Nginx还接受如下参数
* TERM, INT	: Quick shutdown
* QUIT :	Graceful shutdown
* KILL :	Halts a stubborn process
* HUP : Configuration reload, Start the new worker processes with a new configuration, Gracefully shutdown the old worker processes
* USR1 :	Reopen the log files
* USR2 :	Upgrade Executable on the fly
* WINCH :	Gracefully shutdown the worker processes
