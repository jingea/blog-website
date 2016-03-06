category: 平台工具
tag: Nginx
date: 2016-03-06
title: Nginx文件服务器
---
我们使用Nginx搭建一个静态文件上传下载服务器.

需要完成的功能
* 文件上传
* 文件下载
* 用户验证
* 频率控制

Nginx配置
```
#user  nobody;
worker_processes  1;

events {
    worker_connections  1024;
}

http {
    sendfile        on;

    server {
        listen       8071;
        server_name  localhost;

        location /index {
            root   html;
            index  index.html index.htm;
        }

        location /upload {
            root   files;
        }

        location /download/ {
            root   files;
        }
    }
}
```
