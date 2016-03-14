category: python2
date: 2016-03-12
title: PYTHON2 异常
---
捕获异常
```python
try:
   fh = open("test", "w")
except IOError:
   print "Error: open error"
else:
   print "hava open file"
   fh.close()
```
抛出异常`raise [Exception [, args [, traceback]]]`例如
```python
if(num < 100):
  raise RuntimeError("num is greater than 100!")
```
