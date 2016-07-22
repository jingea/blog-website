category: Nginx
date: 2016-03-06
title: Nginx 静态资源服务
---
## 配置
修改nginx.conf文件
```json
server {
       listen       8000;

       autoindex on;             #开启索引功能
       autoindex_exact_size off; # 关闭计算文件确切大小（单位bytes），只显示大概大小（单位kb、mb、gb）
       autoindex_localtime on;   # 显示本机时间而非 GMT 时间

       root /home/files/;
   }
```
然后在地址栏里访问
```bash
http://192.168.15.20:8000/
```
我们就能看见文件列表了.

## 403异常
产生了403异常的话, 说明nginx没有访问该目录, 也就是`/home/files/`的权限, 需要将nginx用户添加到这个目录的用户组里.
```bash
usermod -G groupname username
```
然后查看用户组
```bash
cat /etc/group | grep groupname
```
然后nginx用户要有要访问目录的上层所有父目录的可执行权限, 要有访问目录的读权限

最后nginx用户是在nginx.conf配置里
```bash
#user  nobody;
user  www www;
worker_processes  4;
```
user就是了

## 禁止访问目录
跟Apache的Deny from all类似，nginx有deny all指令来实现。

禁止对叫dirdeny目录的访问并返回403 Forbidden，可以使用下面的配置:
```json
location /dirdeny {
      deny all;
      return 403;
}
```