category: Nginx
date: 2016-04-23
title: nginx web服务器
---
官方文档[](https://www.nginx.com/resources/admin-guide/nginx-web-server/)学习

Each virtual server for HTTP traffic defines special configuration instances called locations that control processing of specific sets of URIs. Each location defines its own scenario of what happens to requests that are mapped to this location. NGINX Plus provides full control over this process. Each location can proxy the request or return a file. In addition, the URI can be modified, so that the request is redirected to another location or virtual server. Also, a specific error code can be returned and you can configure a specific page to correspond to each error code.

## Setting Up Virtual Servers

Nginx插件配置必须最少包含一个`server`指令(用于定义虚拟服务器-virtual server). 当Nginx插件处理一个请求时, 它首先会选择处理该请求的虚拟服务器. 一个虚拟服务器定义在`http`上下文里, 例如:
```jaon
http {
    server {
        # Server configuration
    }
}
```
在`http`上下文里可以定义多个`server`指令, 这样一来就可以实现多个虚拟服务器了.

在`server`指令里通常包含一个`listen`指令, 通过`listen`指令来指定监听请求的IP地址和端口号(或者Unix domain socket and path). IPv4 和 IPv6 地址都可进行配置

下面的例子展示了在IP地址`127.0.0.1`和端口`8080`上进行网络事件监听
```json
server {
    listen 127.0.0.1:8080;
    # The rest of server configuration
}
```
如果端口忽略不写的话, Nginx插件会使用标准端口. 还有如果IP地址忽略不填的话, Nginx插件会在所有的IP地址上进行网络事件监听. 如果没有配置`listen`指令的话, 在超级用户权限下, 标准端口是`80/tcp`, 默认端口`8000/tcp`.

如果有多个`server`指令里配置的IP地址和端口匹配到请求, 那么Nginx插件会根据请求的`Host header`字段与`server_name`指令进行匹配. 我们可以将`server_name`指令配置成一个全名称的地址, 或者使用通配符, 正则表达式. 如果想要使用通配符的话, 可以在地址的开头或者结尾使用`*`.

```json
server {
    listen      80;
    server_name example.org www.example.org;
    ...
}
```

如果有多个`server_name`指令匹配到`Host header`, Nginx插件会根据如下顺序进行匹配查找, 直到找到第一个.

1. 名字完全符合
2. 以`*`开始的, 最符合的名字, 例如`*.example.org`
3. 以`*`结束的, 最符合的名字, 例如`mail.*`
4. 第一个匹配正则表达式的

如果请求中的`Host header`没有匹配到一个合适的server name, Nginx插件会将该消息路由到默认服上面去. 默认服是在`nginx.conf`文件里进行配置的. 我们也可以使用`default_server`参数在`listen`指令中进行显示设定.
```json
server {
    listen      80 default_server;
    ...
}
```

## Configuring Locations

Nginx插件可以根据请求的URI将请求派发到不同的代理上, 或者处理不同的文件. 这种功能是通过`server`指令里的`location`指令完成的.

例如你可以定义三个`location`指令, 一个用来将请求派发到一个代理服务器, 一个用来将其他的请求派发到另外的一个代理服务器,最后的一个用来提供本地文件系统服务.

NGINX Plus tests request URIs against the parameters of all location directives and applies the directives defined in the matching location. Inside each location block, it is usually possible (with a few exceptions) to place even more location directives to further refine the processing for specific groups of requests.

> 注意, 在这篇教程中, location这个字指的是一个单独的`location`上下文.

有俩种`location`指令参数
* prefix strings : 请求的URI是以固定的字符串开头, 例如prefix strings是`/some/path/`, 那么`/some/path/document.html`就是这种类型的, 但是`/my-site/some/path`就不是.
* regular expressions :

```json
location /some/path/ {
    ...
}
```

当使用表达式的时候, 如果使用`~`开头则表示要区分大小写. 如果以`~*`开头,则表示忽略大小写. 下面的例子是只要请求包含`.html`或者`.htm`这些字符串就都匹配
```json
location ~ \.html? {
    ...
}
```

Nginx插件进行location匹配时, 优先进行`prefix string`匹配, 如果`prefix string`匹配不到再进行正则匹配.

Higher priority is given to regular expressions, unless the ^~ modifier is used. Among the prefix strings NGINX Plus selects the most specific one (that is, the longest and most complete string). The exact logic for selecting a location to process a request is given below:

1. Test the URI against all prefix strings.
2. The = (equals sign) modifier defines an exact match of the URI and a prefix string. If the exact match is found, the search stops.
3. If the ^~ (caret-tilde) modifier prepends the longest matching prefix string, the regular expressions are not checked.
4. Store the longest matching prefix string.
5. Test the URI against regular expressions.
6. Break on the first matching regular expression and use the corresponding location.
7. If no regular expression matches, use the location corresponding to the stored prefix string.

`=`的典型用例是请求`/`, 如果请求`/`是非常频繁的, 在`location`指令上设置`= /`可以大幅提升访问速度, 这是因为在路径搜索匹配时, 一旦匹配到就不再继续搜素匹配了, 节约了时间.
```json
location = / {
    ...
}
```

`location`上下文中可以包含多个指令, 用于说明如何处理一个请求, 例如是想提供静态文件服务还是想向一个代理服务器派发请求. 例如, 在下面的例子中, 第一个请求就是提供`/data`目录下的静态文件服务, 第二个请求就是向一个代理服务器`www.example.com`派发请求.
```json
server {
    location /images/ {
        root /data;
    }

    location / {
        proxy_pass http://www.example.com;
    }
}

```
* `root`指令用于指定提供静态文件服务的文件系统路径. 如果有一个请求`/images/example.png`, 那么nginx插件会在文件系统中进行如下搜索`/data/images/example.png`.
* `proxy_pass`指令会将请求派发到代理服务器上. 在上面的例子中, 只要是请求不是以`/images/`开头的, 那么请求都会派发到那个代理服务器上.

## Using Variables

你可以在配置文件里使用变量, 以便Nginx插件可以根据不同的情况进行不同的处理. 变量在运行时进行计算, 然后将值传递给指令使用. 我们可以通过`$`来引用一个变量(例如`$time`).

在Nginx里已经为我们提前定义好了一些变量, 例如[core HTTP](http://nginx.org/en/docs/http/ngx_http_core_module.html?&_ga=1.54594219.1025068217.1457188479#variables). 而且你也可以使用`set`, `map`, `geo`等指令自定义一些变量.

## Returning Specific Status Codes

在一些web站点中, 由于资源移除或者其他原因, 我们需要直接给前端返回一个错误码, 那么我们可以使用`return`指令来完成.
```json
location /wrong/url {
    return 404;
}
```

下面的例子中, 第一个参数是一个错误码. 第二个参数是一个可选参数(代表一个重连接的URL)
```json
location /permanently/moved/url {
    return 301 http://www.example.com/moved/here;
}
```
`return`指令既可以放在`location`指令里, 也可以放在`server`指令里

## Rewriting URIs in Requests

请求URI可以在被处理时通过`rewrite`指令被多次修改, `rewrite`指令包含一个可选参数和俩个必选参数.
* 第一个参数(必填)是用来匹配请求的正则表达式.
* 第二个参数(必填)是用来指向重连接所需要匹配的URI.(下面的例子中我们采用的是正则表达式进行匹配)
* 第三个参数(选填)是用来标记是继续执行下一个重连接还是执行其他操作(例如返回一个错误码)
```json
location /users/ {
    rewrite ^/users/(.*)$ /show?user=$1 break;
}
```

在`server`或者`location`指令中可以包含多个`rewrite`指令. Nginx插件会根据`rewrite`指令出现的顺序依次执行.

After NGINX processes a set of rewriting instructions, it selects a location context according to the new URI. If the selected location contains rewrite directives, they are executed in turn. If the URI matches any of those, a search for the new location starts after all defined rewrite directives are processed.



下面的例子展示里`rewrite`和`return`指令混合使用的方式
```json
server {
    ...
    rewrite ^(/download/.*)/media/(.*)\..*$ $1/mp3/$2.mp3 last;
    rewrite ^(/download/.*)/audio/(.*)\..*$ $1/mp3/$2.ra  last;
    return  403;
    ...
}
```

上面的例子区分了俩组URI. `/download/some/media/file`的URI会被替换成`/download/some/mp3/file.mp3`. 由于这个`rewrite`指令后面跟里一个`last`标记, 因此下面的`rewrite`和`return`指令就不会再执行到了. 但是如果URI例如`/download/some/audio/file`并不符合第一个,那么Nginx插件会继续匹配到第二个, 当然第二个符合, 于是URI被替换成`/download/some/mp3/file.ra`. 如果前俩个都不符合的话, 那么就会返回`403`错误码.

下面, 我们会介绍俩种终止URI`rewrite`执行的方式:
* `last` – 这种方式只是会在当前的`server`或者`location`上下文中停止, 但是Nginx插件会对重写的URI继续在`location`里进行查找.
* `break` – 它不会对新的URI在当前上下文中进行查找, 而是简单的终止掉整个过程.

## Rewriting HTTP Responses

有时候你可能需要重写或者替换HTTP响应中的内容, 或者将一个字符串替换为另一个. `sub_filter`指令可以实现类似于这种的需求. `sub_filter`支持变量以及链式替换, 以便实现更加复杂的重写功能.

```json
location / {
    sub_filter      /blog/ /blog-staging/;
    sub_filter_once off;
}
```

另一个例子是将`http://`改变为`https://`, 而且将localhost地址改变为请求头中的主机名. `sub_filter_once`指令是告诉NGINX在`location`指令里可以开启多个`sub_filter`.
```json
location / {
    sub_filter     'href="http://127.0.0.1:8080/'    'href="http://$host/';
    sub_filter     'img src="http://127.0.0.1:8080/' 'img src="http://$host/';
    sub_filter_once on;
}
```

> 注意, 如果响应已经在一个`sub_filter`修改过里, 那么即使另一个`sub_filter`也匹配到了,但是不会再次修改.

## Handling Errors

使用`error_page`指令, 我们可以配置Nginx插件返回一个自定义的界面或者响应一个不同的错误码,甚至可以重定向到一个新的地址上面去. 在下面的例子中, `error_page`指令会在发生错误时返回一个指定的界面`/404.html`, 而不是像默认的返回一个404错误码.
```json
error_page 404 /404.html;
```

> 注意, 这个指令并不是直接就返回了(`return`指令直接返回), 而是指定一种当错误发生时的一种处理方式.

在下面的例子中, 当Nginx找不到文件时, 它会返回`301`错误码, 同时告诉客户端重定向到`http:/example.com/new/path.html`这个界面上.
```json
location /old/path.html {
    error_page 404 =301 http:/example.com/new/path.html;
}

```

The following configuration is an example of passing a request to the back end when a file is not found. Because there is no status code specified after the equals sign in the error_page directive, the response to the client has the status code returned by the proxied server (not necessarily 404).

```json
server {
    ...
    location /images/ {
        # Set the root directory to search for the file
        root /data/www;

        # Disable logging of errors related to file existence
        open_file_cache_errors off;

        # Make an internal redirect if the file is not found
        error_page 404 = /fetch$uri;
    }

    location /fetch/ {
        proxy_pass http://backend/;
    }
}
```
The error_page directive instructs NGINX Plus to make an internal redirect when a file is not found. The $uri variable in the final parameter to the error_page directive holds the URI of the current request, which gets passed in the redirect.

For example, if /images/some/file is not found, it is replaced with /fetch/images/some/file and a new search for a location starts. As a result, the request ends up in the second location context and is proxied to http://backend/.

The open_file_cache_errors directive prevents writing an error message if a file is not found. This is not necessary here since missing files are correctly handled.
