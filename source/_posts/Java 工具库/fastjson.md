category: Java工具
date: 2015-12-23
title: fastjson
---
## 读取数组
```java
List<Reward> rewards = JSONArray.parseArray(jsonStr, Reward.class);
```

## json和protobuf相互转换
```xml
 <dependency>
    <groupId>com.googlecode.protobuf-java-format</groupId>
    <artifactId>protobuf-java-format</artifactId>
    <version>1.2</version>
</dependency>
```
然后使用
```java
String string = JsonFormat.printToString(mail);
```