category: linux
date: 2015-10-08
title: iTerm2 plus Oh My Zsh
---
iTerm2是Mac下term的一个替代工具.

快捷键
* ⌘ + 数字在各 tab 标签直接来回切换
* 选择即复制 + 鼠标中键粘贴，这个很实用
* ⌘ + f 所查找的内容会被自动复制
* ⌘ + d 横着分屏 / ⌘ + shift + d 竖着分屏
* ⌘ + r = clear，而且只是换到新一屏，不会想 clear 一样创建一个空屏
* ctrl + u 清空当前行，无论光标在什么位置
* 输入开头命令后 按 ⌘ + ; 会自动列出输入过的命令
* ⌘ + shift + h 会列出剪切板历史

Oh my zsh 是bash的一个替代产品. 我们可以在
```bash
.oh-my-zsh/plugins
```
目录下见到N多插件.

有些插件是没有开启的, 那么我们可以修改下面这个文件
```bash
.zshrc
```
然后在里面找到
```bash
plugins=(git)
```
这一行, 想要添加什么插件, 在里面添加就好了, 例如我们还想开启ruby插件
```bash
plugins=(git ruby)
```
