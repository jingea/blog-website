category: Nginx
date: 2016-03-06
title: Nginx负载均衡实战
---
我们测试一次Nginx的负载均衡配置.

首先我们使用python启动俩个HTTP服务器
```python
import BaseHTTPServer
import urlparse
import sys

port = sys.argv[1]
class WebRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def do_GET(self):
        """
        """
        print port

server = BaseHTTPServer.HTTPServer(('0.0.0.0',int(sys.argv[1])), WebRequestHandler)
server.serve_forever()
```
然后在命令行分别启动
```shell
python ./PyHttpServer.py 8091
python ./PyHttpServer.py 8092
```

然后我们配置nginx.conf文件
```shell
#user  nobody;
worker_processes  1;

pid        logs/nginx.pid;

events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    upstream big_server_com {
      server 127.0.0.1:8091 weight=5;
      server 127.0.0.1:8092 weight=5;
    }

    server {
        listen       8090;
        server_name  localhost;

        location / {
            root   html;
            index  index.html index.htm;
            proxy_pass      http://big_server_com;

        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```
我们让nginx在8090端口上监听, 然后将其反向代理到`8091`和`8092`端口上.

> 还有一点很重要的是, 要代理的服务必须在Nginx启动之前都启动完毕, 否则Nginx没办法完成代理工作.

最后我们在浏览器上发送HTTP请求
```shell
http://localhost:8090
```
我们发现那俩个python的HTTP服务器果真都有输出了.

但是我们发现, 就是那俩个服务器同时都有输出, 而不是应该只有其中一个有输出. 所以下来还需要研究一下如何配置Upstream, 看看如何让其真正实现负载均衡和单点失败
