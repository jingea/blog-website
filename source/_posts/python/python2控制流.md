category: python2
date: 2015-08-08
title: PYTHON2 控制流
---
### if
```python
tmp = 0
if tmp > 0 :
    print(">")
elif tmp < 0 :
    print("<")
else :
    print("=")
```

### while
```python
tmp = 0
while tmp < 3 :
    print(tmp)
    tmp +=1
else :
    print("over")
```

### for
```python
for i in [0,1,2,3,4]:
    print(i)

    if i > 2 :
        break
    else :
        continue

else:
    print('loop over')
```

或者
```python
#  从0开始步增到10
for i in range(0, 10, 1)
    print(i)
```