category: 编程语言
date: 2015-08-09
title: PYTHON2 第三方库
---
## json
使用`dump()`方法将对象序列化成json,然后使用`load()`将字符串反序列化成对象
```python
#-*- coding=utf-8 -*-
import json

list = [123, "ad"]
listJson = json.dumps(list)
listR = json.loads(listJson)
print "列表序列化 : " + listJson
print "列表反序列化 : " + str(listR[0])

tumple = (123, "adf")
tumpleJson = json.dumps(tumple)
tumpleR = json.loads(tumpleJson)
print "元组序列化 : " + tumpleJson
print "元组反序列化 : " + str(tumpleR[1])

map = {
       "key1":"value1",
       "key2":"value2",
       "key3":"value3",
       }
mapJson = json.dumps(map)
mapR = json.loads(mapJson)
print "字典序列化 : " + mapJson
print "字典反序列化 : " + str(mapR["key1"])

seq = ['apple', 'mango', 'carrot', 'banana']
seqJson = json.dumps(seq)
seqR = json.loads(seqJson)
print "序列序列化 : " + seqJson
print "序列反序列化 : " + str(seqR[1])


type tumpleR[1]
type mapR["key1"]
type seqR[1]

```

## redis
```python
#!/usr/bin/python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

import redis
import json

_REDIS_HOST = 'localhost'
_REDIS_PORT = 6379
_REDIS_DB = 1
_PASSWORD = "2016"

def getRedisCli() :
    redisCli = redis.Redis(host=_REDIS_HOST, port=_REDIS_PORT, db=_REDIS_DB, password=_PASSWORD)
    return redisCli

def info() :
    redisCli = getRedisCli()
    return redisCli.info()

def slowlog_get() :
    redisCli = getRedisCli()
    return redisCli.slowlog_get()

def client_list() :
    redisCli = getRedisCli()
    return redisCli.client_list()

def dbsize() :
    redisCli = getRedisCli()
    return redisCli.dbsize()

info = info()
slowlog_get = slowlog_get()
dbsize = dbsize()
print info
```

## mysql
```python
#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
import mysql.connector

mysql_host = "localhost"
mysql_database = "test"
mysql_user = "root"
mysql_passwrold = "root"

reload(sys)
sys.setdefaultencoding('utf-8')


def exec_sqls(exec_sql):
    cnx = mysql.connector.connect(user=mysql_user, password=mysql_passwrold, host=mysql_host, database=mysql_database)
    cursor = cnx.cursor()
    exec_sql(cursor)
    cnx.commit()
    cursor.close()
    cnx.close()

def query_max_connections(cursor):
    print "连接数信息"

    max_connections = exec_sql_query(cursor, "show variables like 'max_connections'")
    max_connections_num = float(max_connections[0][1])
    print("max_connections : " + str(max_connections_num))
	
exec_sqls(query_max_connections)
```

## Fabric
安装
```bash
pip install fabric
```
如果已经安装过了则进行更新, 我现在基于的是1.12.0版本
```bash
pip install --upgrade fabric
```

执行本地命令
```python
#!/usr/bin/python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


from fabric.api import local

def echo_helloworld():
    local("ECHO helloworld")

echo_helloworld()
```

如果命令报错的话,我们进行错误处理
```python
#!/usr/bin/python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

from fabric.api import local, settings

def cd_test_dir():
    # 首先我们设置只是警告, 而不是发生错误时直接Abort
    with settings(warn_only=True):
        result = local("cd ./test", capture=True)
    if result.failed:
        local("mkdir ./test")
        local("cd ./test", capture=True)

cd_test_dir()
```