category: linux
date: 2016-05-30
title: 终端生成GIF文件
---
[ttystudio](https://github.com/chjj/ttystudio)可以将命令行操作制作成gif文件.

安装
```bash
npm install ttystudio
```
全局安装
```bash
npm install ttystudio -g
```
使用
```bash
ttystudio output.gif --log
```
上面这个命令最终生成一个output.gif文件, 同时将生成的过程输出

除了上面简单的介绍外, 它还可以为我们添加边框, 录制部分帧

> 还有其他的录制软件 [asciinema](https://asciinema.org)
