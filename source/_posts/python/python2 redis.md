category: python2
date: 2016-07-23
title: PYTHON2 访问redis
---
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

