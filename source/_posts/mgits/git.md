category: mgits
date: 2016-03-16
title: git
---

## 添加忽略文件
如果想要忽略文件或者文件夹的话, 可以直接修改`.gitignore`文件.

例如我要忽略pulic目录下所有的文件和文件夹, 只需要在`.gitignore`文件中添加如下一行
```bash
public/*
```
> 需要注意的是, 如果你在master分之上将其忽略了, 那么当你跳转到其他分之的时候, 你还需要修改其他分支的`.gitignore`文件
