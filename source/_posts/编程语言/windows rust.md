category: 编程语言
title: 在windows上搭建rust开发环境
---
在windows上搭建rust开发环境

1. 安装[msys2](http://sourceforge.net/projects/msys2/) (我的电脑是64位的,所以以下操作都是以64位为主)
2. 在`msys2`中安装`openssl` -> `pacman -S mingw-w64-x86_64-openssl` (32位`pacman -S mingw-w64-i686-openssl`)
3. 将`C:\msys64\mingw64\bin`添加到环境变量`Path`中
4. 将`C:\msys64\mingw64\lib`下的`libcrypto.dll.a`复制一份,将新文件命名为`libeay32.a`
5. 将`C:\msys64\mingw64\lib`下的`libssl.dll.a`复制一份,将新文件命名为`libssl32.a`
6. 下载安装[rust](http://www.rust-lang.org/)
7. 将`Rust stable 1.0\bin\rustlib\x86_64-pc-windows-gnu\bin`这个`bin`改成其他的名字(随便什么名字,不让Path找到就好了)
8. 现在rust程序就可以正常运行了