category: Nginx
date: 2016-03-05
title: Nginx配置
---
下面我们看一下Nginx官方给出的nginx.config可有的全部配置内容
```
user       www www;  ## Default: nobody
# 在Nginx启动的时候会启动一个Master进程,和N个worker进程,Master讲接收到的任务分配给worker进程执行. worker进程数一般与主机的CPU的核心数相等
worker_processes  5;  ## Default: 1
# 错误日志的输出位置. (TODO 错误日志指的是哪些?)
error_log  logs/error.log;
# Nginx启动时的主进程ID
pid        logs/nginx.pid;
# worker进程能打开的最多的文件数(TODO 在正式环境中Nginx都会打开什么文件? 指的是Socket连接吗?)
worker_rlimit_nofile 8192;

# events模块, 包含nginx中所有处理连接的设置
events {
  # worker进程能打开的最大的连接数 (小于worker_rlimit_nofile数)
  worker_connections  4096;  ## Default: 1024
}

# http模块, 用于处理http请求
http {
  # 引用mime.types配置, 主要是配置HTTP请求中的mineType类型
  include    conf/mime.types;
  # 引用nginx的代理配置. (TODO)
  include    /etc/nginx/proxy.conf;
  # 为nginx引用cgi配置
  include    /etc/nginx/fastcgi.conf;
  # 指定当HTTP请求没有任何请求路径时展示的界面.
  index    index.html index.htm index.php;

  #
  default_type application/octet-stream;
  # log输出格式
  log_format   main '$remote_addr - $remote_user [$time_local]  $status '
    '"$request" $body_bytes_sent "$http_referer" '
    '"$http_user_agent" "$http_x_forwarded_for"';

  # 访问nginx时, 记录的http请求的日志存储位置 (TODO  如何以时间分割文件?)
  access_log   logs/access.log  main;
  # sendfile并不是发送文件, 而是设置磁盘和TCP Socket传输数据时直接通过系统缓存实现, 不再经过用户区的Read, write操作
  sendfile     on;
  # 设置在发送HTTP头文件时一次性全部发送, 而不是一个接一个的发送. (TODO)
  tcp_nopush   on;
  # TODO
  server_names_hash_bucket_size 128; # this seems to be required for some vhosts

  # 开启一个网络监听服务
  server { # php/fastcgi
    # 该server监听的端口
    listen       80;
    # 绑定到的域名地址, 一般我们在主机都是使用localhost
    server_name  domain1.com www.domain1.com;
    # 该server生成的日志的保存地址
    access_log   logs/domain1.access.log  main;
    # 指定http请求中主机资源起始位置, 也就是`http://localhost:8090/`后面的位置
    root         html;

    # 所有请求以.php结尾的文件都到下面的代理地址中进行代理请求
    location ~ \.php$ {
      fastcgi_pass   127.0.0.1:1025;
    }
  }

  # 再设置一个网络服务
  server { # simple reverse-proxy
    listen       80;
    server_name  domain2.com www.domain2.com;
    access_log   logs/domain2.access.log  main;

    # serve static files
    location ~ ^/(images|javascript|js|css|flash|media|static)/  {
      root    /var/www/virtual/big.server.com/htdocs;
      expires 30d;
    }

    # 反向代理设置, 将/下所有的请求进行转发
    location / {
      #设置反向代理的地址, 將80端口接受到的请求转发到localhost的8080端口上
      proxy_pass      http://127.0.0.1:8080;
    }
  }

  # 设置反向代理
  upstream big_server_com {
    # 按照权重将代理过来的请求代理到俩个代理服务器上
    server 127.0.0.3:8000 weight=5;
    server 127.0.0.3:8001 weight=5;
    server 192.168.0.1:8000;
    server 192.168.0.1:8001;
  }

  server { # simple load balancing
    listen          80;
    server_name     big.server.com;
    access_log      logs/big.server.access.log main;

    location / {
      # 设置反向代理, 代理到big_server_com上(upstream刚刚定义的)
      proxy_pass      http://big_server_com;
    }
  }
}
```
上面谈到的location涉及到的内容较多, 我们单独介绍一下:

nginx location语法
```
location [=|~|~*|^~] /uri/ { … }
```
* `=` 严格匹配。如果这个查询匹配，那么将停止搜索并立即处理此请求。
* `~` 为区分大小写匹配(可用正则表达式)
* `~*` 为不区分大小写匹配(可用正则表达式)
* `!~` 区分大小写不匹配
* `!~*` 不区分大小写不匹配
* `^~` 如果路径匹配那么不测试正则表达式。



上面的配置引用里其他的配置文件,而且很多配置没有配置选项,下面是Nginx官网给出的另一种配置
```
user  www www;
worker_processes  2;
pid /var/run/nginx.pid;

# [ debug | info | notice | warn | error | crit ]
error_log  /var/log/nginx.error_log  info;

events {
  worker_connections   2000;
  # use [ kqueue | rtsig | epoll | /dev/poll | select | poll ] ;
  use kqueue;
}

http {
  include       conf/mime.types;
  default_type  application/octet-stream;

  log_format main      '$remote_addr - $remote_user [$time_local]  '
    '"$request" $status $bytes_sent '
    '"$http_referer" "$http_user_agent" '
    '"$gzip_ratio"';

  log_format download  '$remote_addr - $remote_user [$time_local]  '
    '"$request" $status $bytes_sent '
    '"$http_referer" "$http_user_agent" '
    '"$http_range" "$sent_http_content_range"';

  # HTTP请求,客户端相关设置
  client_header_timeout  3m;
  client_body_timeout    3m;
  send_timeout           3m;

  client_header_buffer_size    1k;
  large_client_header_buffers  4 4k;

  # 消息发送时开启gzip设置
  gzip on;
  gzip_min_length  1100;
  gzip_buffers     4 8k;
  gzip_types       text/plain;

  output_buffers   1 32k;
  postpone_output  1460;

  sendfile         on;
  tcp_nopush       on;

  tcp_nodelay      on;
  send_lowat       12000;

  keepalive_timeout  75 20;

  # lingering_time     30;
  # lingering_timeout  10;
  # reset_timedout_connection  on;


  server {
    listen        one.example.com;
    server_name   one.example.com  www.one.example.com;

    access_log   /var/log/nginx.access_log  main;

    location / {
      proxy_pass         http://127.0.0.1/;
      proxy_redirect     off;

      proxy_set_header   Host             $host;
      proxy_set_header   X-Real-IP        $remote_addr;
      # proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for;

      client_max_body_size       10m;
      client_body_buffer_size    128k;

      client_body_temp_path      /var/nginx/client_body_temp;

      proxy_connect_timeout      90;
      proxy_send_timeout         90;
      proxy_read_timeout         90;
      proxy_send_lowat           12000;

      proxy_buffer_size          4k;
      proxy_buffers              4 32k;
      proxy_busy_buffers_size    64k;
      proxy_temp_file_write_size 64k;

      proxy_temp_path            /var/nginx/proxy_temp;

      charset  koi8-r;
    }

    error_page  404  /404.html;

    location /404.html {
      root  /spool/www;

      charset         on;
      source_charset  koi8-r;
    }

    location /old_stuff/ {
      rewrite   ^/old_stuff/(.*)$  /new_stuff/$1  permanent;
    }

    location /download/ {
      valid_referers  none  blocked  server_names  *.example.com;

      if ($invalid_referer) {
        #rewrite   ^/   http://www.example.com/;
        return   403;
      }

      # rewrite_log  on;
      # rewrite /download/*/mp3/*.any_ext to /download/*/mp3/*.mp3
      rewrite ^/(download/.*)/mp3/(.*)\..*$ /$1/mp3/$2.mp3 break;

      root         /spool/www;
      # autoindex    on;
      access_log   /var/log/nginx-download.access_log  download;
    }

    location ~* ^.+\.(jpg|jpeg|gif)$ {
      root         /spool/www;
      access_log   off;
      expires      30d;
    }
  }
}
```
