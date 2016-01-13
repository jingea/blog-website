category: 编程语言
tag: python2
date: 2015-08-08
title: PYTHON2 json
---
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

