category: mgits
tag: Docker
date: 2015-06-08
title: docker命令
---
## Docker命令
### `service docker start`  
安装之后启动 Docker 服务.

### `docker pull` 
命令来从仓库获取所需要的镜像
```
docker pull ubuntu12.04
```

### `docker push` 
把自己创建的镜像上传到仓库中来共享
```
docker push ouruser/sinatra
```

### `docker images` 
显示本地已有的镜像.

### `docker commit` 
使用 docker commit 命令来提交更新后的副本. 这个命令是用来将容器的改变提交到镜像身上.如果目标镜像不存在就创建一个.
```
sudo docker commit -m "Added json gem" -a "Docker Newbee" 0b2616b0e5a8 ouruser/sinatrav2
```
* `-m` : 来指定提交的说明信息，跟我们使用的版本控制工具一样；
* `-a` : 可以指定更新的用户信息；
* `0b2616b0e5a8` : 用来创建镜像的容器的 ID；
* `ouruser/sinatrav2` : 指定目标镜像的仓库名和 tag 信息。


### `docker build` 
使用 docker build 来创建一个新的镜像.为此,首先需要创建一个 Dockerfile,包含一些如何创建镜像的指令.
```
docker build -t="ouruser/sinatrav2"
```
* -t 标记来添加 tag,指定新的镜像的用户信息

### `docker tag` 
命令来修改镜像的标签.
```
docker tag 5db5f8471261 ouruser/sinatradevel
```

### `docker import` 
从本地文件系统导入一个镜像,可以使用 openvz(容器虚拟化的先锋技术)的模板来创建 openvz 的模板下载地址为 templates .比如,先下载了一个 ubuntu-14.04 的镜像,之后使用以下命令导入
```
cat ubuntu-14.04-x86_64-minimal.tar.gz  |docker import - ubuntu14.04
```

### `docker save` 
导出镜像到本地文件
```
docker save -o ubuntu_14.04.tar ubuntu14.04
```

### `docker load` 
从导出的本地文件中再导入到本地镜像库 
```
docker load --input ubuntu_14.04.tar
docker load < ubuntu_14.04.tar
```

### `docker rmi` 
移除本地的镜像. 注意在删除镜像之前要先用 docker rm 删掉依赖于这个镜像的所有容器.
```
sudo docker rmi training/sinatra
```

### `docker run`  
基于镜像新建一个容器并启动
```
docker run ubuntu14.04

docker run -t -i ubuntu14.04 /bin/bash
```
* -t 选项让Docker分配一个伪终端(pseudo-tty)并绑定到容器的标准输入上
* -i 则让容器的标准输入保持打开.
* -d 让 Docker 容器在后台以守护态(Daemonized)形式运行
* -P 端口映射.当使用 -P 标记时，Docker 会随机映射一个 49000~49900 的端口到内部容器开放的网络端口。

> -p（小写的）则可以指定要映射的端口，并且，在一个指定端口上只可以绑定一个容器。支持的格式有 `ip:hostPort:containerPort | ip::containerPort | hostPort:containerPort`   在`--net=host`模式下，可以时容器内的端口自动映射到宿主主机上


映射所有接口地址： 使用 `hostPort:containerPort` 格式本地的 `5000` 端口映射到容器的 `5000` 端口，可以执行
```
$ sudo docker run -d -p 5000:5000 training/webapp python app.py
```

此时默认会绑定本地所有接口上的所有地址。 映射到指定地址的指定端口

可以使用 `ip:hostPort:containerPort` 格式指定映射使用一个特定地址，比如 `localhost` 地址 `127.0.0.1`
```
$ sudo docker run -d -p 127.0.0.1:5000:5000 training/webapp python app.py
```

映射到指定地址的任意端口。 使用 `ip::containerPort` 绑定 `localhost` 的任意端口到容器的 `5000` 端口，本地主机会自动分配一个端口。
```
$ sudo docker run -d -p 127.0.0.1::5000 training/webapp python app.py
```

### `docker start` 
直接将一个已经终止的容器启动运行

### `docker stop` 
终止一个运行中的容器.

### `docker restart` 
将一个运行态的容器终止,然后再重新启动它.

### `docker attach` 
进入容器

### `docker export ` 
导出本地某个容器
```
docker export 7691a814370e > ubuntu.tar
```

### `docker import` 
从容器快照文件中再导入为镜像
```
cat ubuntu.tar | sudo docker import - test/buntuv1.0

docker import http//example.com/exampleimage.tgz example/imagerepo
```

### `docker rm` 
移除容器.删除一个处于终止状态的容器
```
docker rm  trusting_newton
```

### `docker search` 
查找官方仓库中的镜像

### `docker ps` 

### `docker logs` 
获取容器的输出信息
```
docker logs insane_babbage
```

### `docker port`
查看当前映射的端口配置，也可以查看到绑定的地址
```
docker port nostalgic_morse 5000
```


## Dockerfile 

Dockerfile中每一条指令都创建镜像的一层,例如
```
# This is a comment
FROM ubuntu14.04
MAINTAINER Docker Newbee <newbee@docker.com>
RUN apt-get -qq update
RUN apt-get -qqy install ruby ruby-dev
RUN gem install sinatra
Dockerfile 基本的语法是
```
1. 使用`#`来注释
2. `FROM` 指令告诉 `Docker` 使用哪个镜像作为基础
3. 接着是维护者的信息
4. `RUN`开头的指令会在创建中运行,比如安装一个软件包,在这里使用 `apt-get` 来安装了一些软件
5. `ADD` 命令复制本地文件到镜像;
6. `EXPOSE` 命令来向外部开放端口;
7. `CMD` 命令来描述容器启动后运行的程序等


当利用 `docker run` 来创建容器时,`Docker` 在后台运行的标准操作包括
1. 检查本地是否存在指定的镜像,不存在就从公有仓库下载
2. 利用镜像创建并启动一个容器
3. 分配一个文件系统,并在只读的镜像层外面挂载一层可读写层
4. 从宿主主机配置的网桥接口中桥接一个虚拟接口到容器中去
5. 从地址池配置一个 ip 地址给容器
6. 执行用户指定的应用程序
7. 执行完毕后容器被终止


`docker load` vs `docker import` 用户既可以使用 `docker load` 来导入镜像存储文件到本地镜像库,也可以使用 `docker import `来导入一个容器快照到本地镜像库.这两者的区别在于容器快照文件将丢弃所有的历史记录和元数据信息(即仅保存容器当时的快照状态),而镜像存储文件将保存完整记录,体积也要大.此外,从容器快照文件导入时可以重新指定标签等元数据信息.

