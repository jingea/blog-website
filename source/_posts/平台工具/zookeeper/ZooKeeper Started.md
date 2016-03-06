category: 平台工具
tag: ZooKeeper
date: 2013-09-13
title: ZooKeeper Started
---
演示windows系统下快速使用`Zookeeper3.4.6`版本

我们从Zookeeper官网下载下其最新的压缩包之后,然后解压得到下面的目录：
```
├───bin
├───conf
├───contrib
├───datadir
├───dist-maven
├───docs
├───lib
├───recipes
└───src
```
> `datadir`是我自己创建的,用于存放内存数据的快照文件。

1. 进入到`conf`目录，将`zoo_sample.cfg`修改为`zoo.cfg`文件
2. 修改`zoo.cfg`文件内容,`dataDir=E:/zookeeper-3.4.6/datadir`这个是我修改过的路径
3. 进入到`bin`目录, 执行`.\zkServer.cmd start` .最后见到` Established session 0x150eb438ceb0000 with negotiated timeout 30000 for client /127.0.0.1:54408`就启动成功了

> 如果遇到`java.lang.NumberFormatException: For input string: "E:\zookeeper-3.4.6\bin\..\conf\zoo.cfg"`这个提示,那就要去修改`bin/zkServer.cmd`文件, 将`%*`这个去掉就好了

然后我们执行
```
bin/zkCli.cmd -server 127.0.0.1:2181
```
就连接上了刚才启动的服务器,进入shell
```
E:\zookeeper-3.4.6\bin>.\zkCli.cmd -server 127.0.0.1:2181
Connecting to 127.0.0.1:2181
2015-11-09 16:04:51,367 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.6-1569965, built on 02/20/2014 09:09 GMT
...
2015-11-09 16:04:51,374 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=C:\Users\Administrator
2015-11-09 16:04:51,374 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=E:\zookeeper-3.4.6\bin
2015-11-09 16:04:51,375 [myid:] - INFO  [main:ZooKeeper@438] - Initiating client connection, connectString=127.0.0.1:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@277050dc
Welcome to ZooKeeper!
2015-11-09 16:04:51,514 [myid:] - INFO  [main-SendThread(127.0.0.1:2181):ClientCnxn$SendThread@975] - Opening socket connection to server 127.0.0.1/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
2015-11-09 16:04:51,516 [myid:] - INFO  [main-SendThread(127.0.0.1:2181):ClientCnxn$SendThread@852] - Socket connection established to 127.0.0.1/127.0.0.1:2181, initiating session
2015-11-09 16:04:51,684 [myid:] - INFO  [main-SendThread(127.0.0.1:2181):ClientCnxn$SendThread@1235] - Session establishment complete on server 127.0.0.1/127.0.0.1:2181, sessionid = 0x150eb438ceb0000, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
JLine support is enabled
[zk: 127.0.0.1:2181(CONNECTED) 0]
```

进入到shell之后,我们可以敲人`help`命令，查看我们都能使用哪些命令：
```
[zk: 127.0.0.1:2181(CONNECTED) 1] help
ZooKeeper -server host:port cmd args
	stat path [watch]
	set path data [version]
	ls path [watch]
	delquota [-n|-b] path
	ls2 path [watch]
	setAcl path acl
	setquota -n|-b val path
	history
	redo cmdno
	printwatches on|off
	delete path [version]
	sync path
	listquota path
	rmr path
	get path [watch]
	create [-s] [-e] path data acl
	addauth scheme auth
	quit
	getAcl path
	close
	connect host:port
[zk: 127.0.0.1:2181(CONNECTED) 2]
```
看到了,我们能使用这么多命令，下来我们简单的接受几个命令.

我们使用`create`命令创建一个`znode`(这个node里和字符串"my_data"关联起来了).
```
[zk: 127.0.0.1:2181(CONNECTED) 1] create /zk_test my_data
Created /zk_test
[zk: 127.0.0.1:2181(CONNECTED) 3] ls /
[zookeeper, zk_test]
[zk: 127.0.0.1:2181(CONNECTED) 4]
```
但是实际上，这个node还没有被创建出来.

下来我们使用`get`命令验证一下`zk_test`是不是真的和"my_data"关联起来了
```
[zk: 127.0.0.1:2181(CONNECTED) 4] get /zk_test
my_data
cZxid = 0x3
ctime = Mon Nov 09 16:29:12 CST 2015
mZxid = 0x3
mtime = Mon Nov 09 16:29:12 CST 2015
pZxid = 0x3
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 7
numChildren = 0
[zk: 127.0.0.1:2181(CONNECTED) 5]
```

我们还可以使用`set`命令将刚才那个node重新关联
```
[zk: 127.0.0.1:2181(CONNECTED) 5] set /zk_test new_data
cZxid = 0x3
ctime = Mon Nov 09 16:29:12 CST 2015
mZxid = 0x4
mtime = Mon Nov 09 16:35:11 CST 2015
pZxid = 0x3
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 8
numChildren = 0
[zk: 127.0.0.1:2181(CONNECTED) 7] get /zk_test
new_data
cZxid = 0x3
ctime = Mon Nov 09 16:29:12 CST 2015
mZxid = 0x4
mtime = Mon Nov 09 16:35:11 CST 2015
pZxid = 0x3
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 8
numChildren = 0
[zk: 127.0.0.1:2181(CONNECTED) 8]
```

最后，我们将这个node删掉
```
[zk: 127.0.0.1:2181(CONNECTED) 9] delete /zk_test
[zk: 127.0.0.1:2181(CONNECTED) 10] ls /
[zookeeper]
[zk: 127.0.0.1:2181(CONNECTED) 11]
```
