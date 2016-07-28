category: python2
date: 2016-07-28
title: Fabric 执行本地和远程命令
---
## 安装
```bash
pip install fabric
```
如果已经安装过了则进行更新, 我现在基于的是1.12.0版本
```bash
pip install --upgrade fabric
```

## 执行本地命令
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

## 错误处理
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