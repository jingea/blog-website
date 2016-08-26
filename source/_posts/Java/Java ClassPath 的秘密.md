category: Java
date: 2016-08-25
title: Java ClassPath 的秘密
---
写了一段简单的测试代码
```java
import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.TimeUnit;
public class Test {
	public static void main(String[] args) throws InterruptedException {
		JSONObject json = new JSONObject();
		TimeUnit.HOURS.sleep(1);
	}
}
```
然后分别执行
```bash
[root@root wangming]# javac -cp ./* Test.java
javac: invalid flag: ./Test.class
Usage: javac <options> <source files>
use -help for a list of possible options
[root@root wangming]# javac -cp . Test.java
Test.java:2: error: package com.alibaba.fastjson does not exist
import com.alibaba.fastjson.JSONObject;
                           ^
Test.java:9: error: cannot find symbol
                JSONObject json = new JSONObject();
                ^
  symbol:   class JSONObject
  location: class Test
Test.java:9: error: cannot find symbol
                JSONObject json = new JSONObject();
                                      ^
  symbol:   class JSONObject
  location: class Test
3 errors
[root@root wangming]# javac -cp .:./ Test.java
Test.java:2: error: package com.alibaba.fastjson does not exist
import com.alibaba.fastjson.JSONObject;
                           ^
Test.java:9: error: cannot find symbol
                JSONObject json = new JSONObject();
                ^
  symbol:   class JSONObject
  location: class Test
Test.java:9: error: cannot find symbol
                JSONObject json = new JSONObject();
                                      ^
  symbol:   class JSONObject
  location: class Test
3 errors
[root@root wangming]# javac -cp .:./* Test.java 
```
那么`./*`, `.`, `.:./`和`.:./*`这四个环境变量究竟有什么不一样呢?
