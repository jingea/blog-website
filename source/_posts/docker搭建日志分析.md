title: docker搭建日志分析
---
# 在docker中搭建elasticsearch + kibana + logstash,日志分析系统

0. 下载安装docker,进入到docker里
1. 下载安装centos,然后进入到centos里
```
docker pull centos
docker run --net=host --name=log centos
docker attach log
```
2. 更改yum源
```
cd /etc/yum.repos.d/
wget http://mirrors.163.com/.help/CentOS6-Base-163.repo
yum makecache
yum -y update
yum install epel-release
```
3. 下载相关软件
```
yum -y install wget tar net-tools.x86_64 java-1.8.0-openjdk-devel.x86_64 redis nginx
```
4. 检查软件是否安装成功
```
yum list installed redis nginx
```
5. 下载elasticsearch + kibana + logstash
```
wget https://download.elastic.co/elasticsearch/elasticsearch/elasticsearch-1.5.2.tar.gz
wget https://download.elastic.co/kibana/kibana/kibana-4.0.2-linux-x64.tar.gz
wget https://download.elastic.co/logstash/logstash/logstash-1.4.2.tar.gz
```
6. 安装elasticsearch + kibana + logstash
```
tar -xvf elasticsearch-1.5.2.tar.gz
tar -xvf kibana-4.0.2-linux-x64.tar.gz
tar -xvf logstash-1.4.2.tar.gz 
```
7. 运行elasticsearch + kibana + logstash
```
nohup ./elasticsearch-1.5.2/bin/elasticsearch &
nohup ./kibana-4.0.2-linux-x64/bin/kibana &
```

###### Elasticsearch (LogStash的搜索引擎)
1. 进入到该文件夹下的bin目录执行
2. sudo ./plugin -i elasticsearch/marvel/latest
3. 输入密码之后,程序就会自动下载marvel
4. 随后我们启动命令./elasticsearch start
5. 随后我们在浏览器里输入 http://192.168.1.104:9200/_plugin/marvel
6. 我们就能看到Elasticsearch 的集群和数据管理界面 Marvel

###### Kibana(用户友好的搜索界面)
1. 将Kibana解压后拷贝进elasticsearch/plugins/目录下,最后去掉Kibana文件夹里的版本号
2. 然后将Kibana文件夹里新建文件夹 _site,然后将其全部内容放进 _site里
3. 执行./elasticsearch restart
4. 随后我们在浏览器里输 http://192.168.1.104:9200/_plugin/kibana/

###### Logstash
1. 安装rvm ,分别运行下面三个命令
```
curl -L get.rvm.io | bash -s stable, 
source ~/.bashrc,  
source ~/.bash_profile
```
2. 修改 RVM 的 Ruby 安装源到国内的 淘宝镜像服务器,这样能提高安装速度(执行下面命令)
``` 
 sed -i -e 's/ftp\.ruby-lang\.org\/pub\/ruby/ruby\.taobao\.org\/mirrors\/ruby/g' ~/.rvm/config/db
```
3. 装JRUBY(执行下面俩个命令)
```
   rvm install jruby-1.7.11
   rvm use jruby-1.7.11
```
4. 安装logstash ruby 依赖 : 
```
./logstash deps
```


## Elasticsearch插件:
### 插件安装:
1. 我以安装elasticsearch-head为例, sudo ./plugin -install mobz/elasticsearch-head
2. 然后我们重启一下elasticsearch, 在地址栏输入http://192.168.1.104:9200/_plugin/head/
3. 我们就会看到head已经安装成功
4. 需要说明的是,在启动elasticsearch时 我们会看到一条输出为 
```
[2015-01-25 11:39:11,936][INFO][plugins] [Lady Octopus] loaded [marvel], sites [marvel, head]
```
5. 这个是我们安装的全部的插件,当再安装完其他插件,如果有生成sites的话,我们只需要将地址拦中的head改成相应插件名即可

### 插件内容摘自 http://www.searchtech.pro/elasticsearch-plugins
##### 常用插件
* `head:  elasticsearch的集群管理工具.
* `bigdesk:   elasticsearch的一个集群监控工具,可以通过它来查看es集群的各种状态,如:cpu、内存使用情况,索引数据、搜索情况,http连接数等.

分词插件
* `Combo Analysis Plugin` :组合分词器,可以把多个分词器的结果组合在一起.
* `Smart Chinese Analysis Plugin` :lucene默认的中文分词器
* `ICU Analysis plugin` :lucene自带的ICU分词,ICU是一套稳定、成熟、功能强大、轻便易用和跨平台支持Unicode 的开发包.
* `Stempel `(Polish) Analysis plugin` :法文分词器
* `IK Analysis Plugin` :大名鼎鼎的ik分词,都懂的！
* `Mmseg Analysis Plugin` :mmseg中文分词
* `Hunspell Analysis Plugin` :lucene自带的Hunspell模块
* `Japanese `(Kuromoji) Analysis plugin` :日文分词器
* `Japanese Analysis plugin` :日文分词器
* `Russian and English Morphological Analysis Plugin` :俄文英文分词器
* `Pinyin Analysis Plugin `:拼音分词器
* `String2Integer Analysis Plugin `:字符串转整型工具.主要用在facet这个功能上,如果facet的field的值是字符串的话,计算起来比较耗资源.可以把字符串映射成整型,对整型进行facet操作要比对字符串的快很多.

#### 同步插件
* `CouchDB River Plugin `:CouchDB和elasticsearch的同步插件
* `Wikipedia River Plugin `:wikipedia文件读取插件.wikipedia是维基百科的一个离线库,不定期发布最新数据,是以xml形式发布的.这个river读取这个文件来建索引.
* `Twitter River Plugin `:twitter的同步插件,可以同步你twitter上的微博.
* `RabbitMQ River Plugin `:rabbitmq同步插件,读取rabbitmq上的队列信息并索引.
* `RSS River Plugin `:定期索引指定一个或多个RSS源的数据.
* `MongoDB River Plugin `:mongodb同步插件,mongodb必须搭成副本集的模式,因为这个插件的原理是通过定期读取mongodb中的oplog来同步数据.
* `Open Archives Initiative `:可以索引oai数据提供者提供的数据.
* `St9 River Plugin `:可以索引索引st9数据(st9是神马？囧！！！)
* `Sofa River Plugin `:这个插件可以把多个CouchDB的数据库同步到同一个es索引中.
* `JDBC River Plugin `:关系型数据库的同步插件
* `FileSystem River Plugin `:本地文件系统文件同步插件,使用方法是指定一个本地目录路径,es会定期扫描索引该目录下的文件.
* `LDAP River Plugin `:索引LDAP目录下的文件数据.
* `Dropbox River Plugin `:索引dropbox网盘上的文件.通过oauth协议来调用dropbox上的api建索引.
* `ActiveMQ River Plugin `:activemq队列的同步插件,和之前rabbitmq的类似
* `Solr River Plugin `:solr同步插件,可以把solr里面的索引同步到es
* `CSV River Plugin `:通过指定目录地址来索引csv文件.

#### 数据传输插件
* `Servlet transport `:Servlet rest插件,通过servlet来封装rest接口.
* `Memcached transport plugin `:本插件可以通过memcached协议进行rest接口的调用.注意:这里不是使用memcache作为es的缓存.
* `Thrift Transport `:使用thrift进行数据传输.
* `ZeroMQ transport layer plugin `:使用zeromq进rest接口的调用.
* `Jetty HTTP transport plugin `:使用jetty来提供http rest接口.默认是使用netty.这个插件的好处是可以对http接口进行一些权限的设置.

#### 脚本插件
* `Python language Plugin `:python脚本支持
* `JavaScript language Plugin `:javascript脚本支持
* `Groovy lang Plugin `:groovy脚本支持
* `Clojure Language Plugin `:clojure脚本支持

#### 站点插件(以网页形式展现)
* `BigDesk Plugin `:监控es状态的插件,推荐！
* `Elasticsearch Head Plugin `:很方便对es进行各种操作的客户端.
* `Paramedic Plugin `:es监控插件
* `SegmentSpy Plugin `:查看es索引segment状态的插件
* `Inquisitor Plugin `:这个插件主要用来调试你的查询.

#### 其它插件
* `Mapper Attachments Type plugin `:附件类型插件,通过tika库把各种类型的文件格式解析成字符串.
* `Hadoop Plugin `:hadoop和elasticsearch的集成插件,可以通过hadoop的mapreduce算法来并行建立索引,同时支持cascading,hive和pig等框架.
* `AWS Cloud Plugin `:elasticsearch与amazon web services的集成.
* `ElasticSearch Mock Solr Plugin `:elasticsearch的solr api接口.用了这个插件可以使用solr的api来调用es,直接用solrj就可以调用es.比较适用于从solr转es时暂时过度.
* `Suggester Plugin `:es 搜索提示功能插件,不过es0.9版本后自带了这个功能,
* `ElasticSearch PartialUpdate Plugin `:elasticsearch的部分更新插件.
* `ZooKeeper Discovery Plugin `:通过zookeeper管理集群的插件.通过这个插件,es的分布式架构和solrcloud相似.
* `ElasticSearch Changes Plugin `:elasticsearch索引操作记录插件.通过这个插件可以查看用户对索引的增删改操作.
* `ElasticSearch View Plugin `:这个插件可以把es的文档以html,xml或text的方式显示出来,它也可以通过查询生成web页面.
* `ElasticSearch New Relic Plugin `:elasticsearch和newrelic的集成插件.newrelica是一个性能监控工具.这个插件会把节点的状态数据传到newrelic的账号上.


#### Logstash全部插件列表如下.


* input插件列表:
amqp、drupal_dblog、eventlog、exec、file、ganglia、gelf、gemfire、 generator、heroku、irc、log4j、lumberjack、pipe、redis、relp、sqs、stdin、stomp、 syslog、tcp、twitter、udp、xmpp、zenoss、zeromq.

* filter插件列表:
alter、anonymize、checksum、csv、date、dns、environment、gelfify、 geoip、grep、grok、grokdiscovery、json、kv、metrics、multiline、mutate、noop、 split、syslog_pri、urldecode、xml、zeromq.

* output插件列表:
amqp、boundary、circonus、cloudwatch、datadog、elasticsearch、 elasticsearch_http、elasticsearch_river、email、exec、file、ganglia、gelf、gemfire、graphite、 graphtastic、http、internal、irc、juggernaut、librato、loggly、lumberjack、metriccatcher、 mongodb、nagios、nagios_nsca、null、opentsdb、pagerduty、pipe、redis、riak、riemann、 sns、sqs、statsd、stdout、stomp、syslog、tcp、websocket、xmpp、zabbix、zeromq


http://kibana.logstash.es/content/v3/10-minute-walk-through.html

http://www.logstashbook.com/TheLogstashBook_sample.pdf

