category: JVM
date: 2014-10-06
title: jhat
---
jhat是一款jdk自带的堆快照分析工具
```bash
➜  test jhat 2028dump
Reading from 2028dump...
Dump file created Sun May 29 18:10:00 CST 2016
Snapshot read, resolving...
Resolving 205627 objects...
Chasing references, expect 41 dots.........................................
Eliminating duplicate references.........................................
Snapshot resolved.
Started HTTP server on port 7000
Server is ready.
```
我们在浏览器里直接浏览
```bash
http://localhost:7000
```
打开之后我们会看到满满的JDK对我们爱的死去活来了(lol), 在最下面JDK还为我们提供了OQL查询的支持
