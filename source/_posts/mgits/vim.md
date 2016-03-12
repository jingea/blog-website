category: mgits
date: 2015-10-08
title: GVIM
---

# _vimrc配置文件
## GVIM打开即全屏
`au GUIEnter * simalt ~x `

## 设置VeraMono字体
[VeraMono](http://www.vimer.cn/wp-content/uploads/2009/11/VeraMono.ttf)需要下载安装(百度字体安装就好)
`set guifont=Bitstream_Vera_Sans_Mono:h10:cANSI`

## python编译
```java
autocmd BufRead *.py set makeprg=python\ -c\ \"import\ py_compile,sys;\ sys.stderr=sys.stdout;\ py_compile.compile(r'%')\"  
autocmd BufRead *.py set efm=%C\ %.%#,%A\ \ File\ \"%f\"\\,\ line\ %l%.%#,%Z%[%^\ ]%\\@=%m  
autocmd BufRead *.py nmap <F5> :!python %<CR>  
autocmd BufRead *.py nmap <F6> :make<CR>  
autocmd BufRead *.py copen "如果是py文件，则同时打开编译信息窗口  
```

# 快捷键
* 搜索：`/`向下搜索. `?`向上搜索
* 替换： `:%s/abc/123/g`, 将abc都替换为123
* 删除行：`dd`删除整行. `5dd`从当前行开始向后删除5整行. `D`从光标删除到行尾
* 删除字符：`x`向后删除一个字符. `X`向前删除一个字符
* 撤销: `u`
* 复制:`yy`复制一行. `5yy`从当前行开始向下复制5行
* 粘贴: `p`光标向下移动. `P`光标不动
* 块选择: `V`多行整行选择. `v`多行字符选择. `ctrl v`矩阵方式选择.
* 光标移动：`$`移动到行尾. `0`移动到行首. `G`移动到最后一行. `gg`移动到最一行.
* 窗口编辑: `：split`水平新建窗口. `：vsplit `垂直分割.
* 在窗口间游走: `Ctrl W` 加 `h, j, k, l`一起使用
* 分页编辑： `：tabnew`新建分页。 `：tabclose`关闭当前分页. `：tabonly `关闭其他所有的分页
* 顶部底部跳转： 顶部``, 底部`shift + g`

# 插件
## [Pydiction ](http://www.vim.org/scripts/script.php?script_id=850)
python自动补全插件

配置,`_vimrc`文件追加
```java
filetype plugin on
let g:pydiction_location = 'D:/Program Files/Vim/pydiction/complete-dict'
```
然后将`pydiction/after/ftplugin/python_pydiction.vim`复制到`Vim\vimfiles\ftplugin\python_pydiction.vim`

## [The NERD tree](http://www.vim.org/scripts/script.php?script_id=1658)
文件树. 将下载下来的压缩包解压到`Vim\vimfiles`
* `:NERDTree` 打开当前文件所在目录树. 该命令后可跟需要打开的目录路径
* `:NERDTreeClose` 关闭目录树

修改_vimrc配置文件,添加映射
```java
nmap <F2> :NERDTreeToggle<CR>  
```

## [bsh.vim](http://www.vim.org/scripts/script.php?script_id=1202)
Shell语法高亮

只需将`bsh.vim `文件拷贝到`Vim\vimfiles\syntax`就可以了
