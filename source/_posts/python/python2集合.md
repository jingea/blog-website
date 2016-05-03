category: python2
date: 2015-08-08
title: PYTHON2集合
---

### 列表
* 声明一个列表 `list = [123, "ad"]`
* 索引第一个元素 `list[0]`
* 在尾部添加一个元素 `list.append(2.56)`
* 对第一个元素重新赋值 `list[0] = "0"`
* 获取列表长度 `len(list)`
* 删除第一个元素 `del list[0]`



### 元组
元组和列表十分类似，只不过元组和字符串一样是 不可变的 即你不能修改元组
```python
tumple = (123, "adf")
print(tumple[0])
print(len(tumple))
```

### 字典
```python
map = {
       "key1":"value1",
       "key2":"value2",
       "key3":"value3",
       }
print(map)
print(len(map))
print(map["key1"])
del map["key1"]
print(map)

for key in map:
    print(key + "   " + map[key])
if "key2" in map:
    print("map contains key2")

help(dict)
```

### 序列
序列的两个主要特点是索引操作符和切片操作符
```python
shoplist = ['apple', 'mango', 'carrot', 'banana']

print('Item 0 is', shoplist[0])
print('Item -1 is', shoplist[-1])

### Slicing on a list
print('Item 1 to 3 is', shoplist[1:3])
print('Item 2 to end is', shoplist[2:])
print('Item 1 to -1 is', shoplist[1:-1])
print('Item start to end is', shoplist[:])

### Slicing on a string
name = 'swaroop'
print('characters 1 to 3 is', name[1:3])
print('characters 2 to end is', name[2:])
print('characters 1 to -1 is', name[1:-1])
print('characters start to end is', name[:])
```


