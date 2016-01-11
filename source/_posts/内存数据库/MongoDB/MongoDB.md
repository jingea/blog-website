category: 内存数据库
tag: MongoDB
date: 2015-03-08
title: 运行MongoDB
---

## Run MongoDB On Windows
如果在没有进行auth设置且在Secure Mode运行, 那么就不要使 mongod.exe在公共网络上可见.

### 设置MOngoDB环境

#### 设置环境变量
在环境变量里添加环境变量 `D:\Program Files\MongoDB\Server\3.0\` 然后在Path里添加： `%MONGODB_HOME%\bin`

#### data directory
```
MongoDB 需要一个data directory来存储全部的数据. MongoDB默认的data directory路径是\data\db, 
所以我们需要创建一个data directory. 假设我们在D盘创建了一个这样的目录: D:\mongodb\data\db.

你可以通过--dbpath选项给mongod.exe设置另一个data directory.
mongod.exe --dbpath D:\mongodb\data\db

如果你的data directory包含空格的话,那么就需要使用""将他们包含起来：
mongod.exe --dbpath "d:\test\mongo db data"
```

## 启动MongoDB

### 使用mongod.exe命令启动mongoDB
```
	mongod.exe
```

### 启动日志
最后我们在启动日志里看到
```
waiting for connections on port 27017
```

### 命令行方式启动

MongoDB 默认存储数据目录为/data/db/ (或者 c:/data/db), 默认端口 27017,默认 HTTP 端口 28017.
```
mongod --dbpath=/data/db
```

### 配置文件方式启动
MongoDB 也支持同 mysql 一样的读取启动配置文件的方式来启动数据库,配置文件的内容如下:
```
cat /etc/mongodb.cnf
```
启动时加上”-f”参数,并指向配置文件即可:
```
mongod -f /etc/mongodb.cnf
```

#### Daemon 方式启动
MongoDB 提供了一种后台 Daemon 方式启动的选择,只需加上一个” --fork”参数即可,,但如果用到了 ” --fork”参数就必须也启用 ”--logpath”参数,这是强制的
```
mongod --dbpath=/data/db --logpath=/data/log/r3.log --fork
```

#### mongod 参数说明
mongod 的参数分为一般参数, windows 参数, replication 参数, replica set 参数,以及隐含参数.上面列举的都是一般参数

mongod 的参数中,没有设置内存大小相关的参数,是的, MongoDB 使用 os mmap 机制来缓存数据文件数据,自身目前不提供缓存机制.这样好处是代码简单,
mmap 在数据量不超过内存时效率很高.但是数据量超过系统可用内存后,则写入的性能可能不太稳定,容易出现大起大落,不过在最新的 1.8 版本中,这个情况相对以前的版本已经
有了一定程度的改善.

##### mongod 的主要参数有：
* dbpath —— 数据文件存放路径,每个数据库会在其中创建一个子目录,用于防止同一个实例多次运行的 mongod.lock 也保存在此目录中.
* logpath —— 错误日志文件
* logappend —— 错误日志采用追加模式（默认是覆写模式）
* bind_ip —— 对外服务的绑定 ip,一般设置为空,及绑定在本机所有可用 ip 上,如有需要可以单独指定
* port —— 对外服务端口 . Web 管理端口在这个 port 的基础上+1000
* fork —— 以后台 Daemon 形式运行服务
* journal —— 开启日志功能,通过保存操作日志来降低单机故障的恢复时间,在 1.8 版本后正式加入,取代在 1.7.5 版本中的 dur 参数.
* syncdelay —— 系统同步刷新磁盘的时间,单位为秒,默认是 60 秒.
* directoryperdb —— 每个 db 存放在单独的目录中,建议设置该参数.与 MySQL 的独立表空间类似
* maxConns —— 最大连接数
* repairpath —— 执行 repair 时的临时目录.在如果没有开启 journal,异常 down 机后重启 ,必须执行 repair操作.

## 停止数据库

* Control-C
* shutdownServer()指令
```
mongo --port 28013
use admin
db.shutdownServer()
```

## 常用工具集
MongoDB 在 bin 目录下提供了一系列有用的工具,这些工具提供了 MongoDB 在运维管理上的方便。
* bsondump: 将 bson 格式的文件转储为 json 格式的数据
* mongo: 客户端命令行工具,其实也是一个 js 解释器,支持 js 语法
* mongod: 数据库服务端,每个实例启动一个进程,可以 fork 为后台运行
* mongodump/ mongorestore: 数据库备份和恢复工具
* mongoexport/ mongoimport: 数据导出和导入工具
* mongofiles: GridFS 管理工具,可实现二制文件的存取
* mongos: 分片路由,如果使用了 sharding 功能,则应用程序连接的是 mongos 而不是mongod
* mongosniff: 这一工具的作用类似于 tcpdump,不同的是他只监控 MongoDB 相关的包请求,并且是以指定的可读性的形式输出
* mongostat: 实时性能监控工具

## 部署 Replica Sets
* 创建数据文件存储路径
```
mkdir E:/mongoData/data/r0
mkdir E:/mongoData/data/r1
mkdir E:/mongoData/data/r2
```
* 创建日志文件路径
```
mkdir E:/mongoData/log
```
* 创建主从 key 文件，用于标识集群的私钥的完整路径，如果各个实例的 key file 内容不一致，程序将不能正常用。
```
mkdir E:/mongoData/key
echo "this is rs1 super secret key" > E:/mongoData/key/r0
echo "this is rs1 super secret key" > E:/mongoData/key/r1
echo "this is rs1 super secret key" > E:/mongoData/key/r2
```
* 启动 3 个实例
```
mongod --replSet rs1 --keyFile E:/mongoData/key/r0 -fork --port 28010 --dbpath E:/mongoData/data/r0 --logpath=E:/mongoData/log/r0.log --logappend
mongod --replSet rs1 --keyFile E:/mongoData/key/r1 -fork --port 28011 --dbpath E:/mongoData/data/r1 --logpath=E:/mongoData/log/r1.log --logappend
mongod --replSet rs1 --keyFile E:/mongoData/key/r2 -fork --port 28012 --dbpath E:/mongoData/data/r2 --logpath=E:/mongoData/log/r2.log --logappend
```
* 配置及初始化 Replica Sets
```
mongo -port 28010
```

