category: 杂记
date: 2012-08-20
title: SVN 总结
---
## 服务器搭建
下载svn服务器[subversion](http://sourceforge.net/projects/win32svn/)和svn客户端TortoiseSVN(下载页面有安装程序和汉化程序)，还有[apache]()(apache主要是为了解析成网络，要不然安装好的svn只能在局域网里使用)

在e盘下创建svn仓库`svnresp` ![](https://raw.githubusercontent.com/yu66/blog-website/images/svn/0.jpg)

创建好版本库后需要将版本库里的配置文件`E:\svnresp\conf\svnserve.conf`文件进行修改
```java
# password-db = passwd
将其修改成
password-db = passwd
```
如果不修改这个配置在提交文件是会产生：svn认证失败的错误。 修改如图,前面一定不能有空格：
[](https://raw.githubusercontent.com/yu66/blog-website/images/svn/1.jpg)[](https://raw.githubusercontent.com/yu66/blog-website/images/svn/2.jpg)
然后修改同一目录下的`E:\svnresp\conf\passwd.conf`文件
[](https://raw.githubusercontent.com/yu66/blog-website/images/svn/3.jpg)
> 注意xiaoli是合法用户，而sally则是非法用户。=前面的为用户名，后面的为密码，同样的前面不能有空格

都配置好成功之后，接下来我到一个测试项目下进行导入。

首先是开启我们的svn服务器：
[](https://raw.githubusercontent.com/yu66/blog-website/images/svn/4.jpg)

回车后只要不显示其他内容那么就说明该svn服务器已经启动了。`svnserve -d -r`是固定写法，而其后面的内容是版本库地址。

然后我们找到测试项目，在空白处右击
[](https://raw.githubusercontent.com/yu66/blog-website/images/svn/5.jpg)

选择版本浏览器或者导入都可以，我选择的是导入。
[](https://raw.githubusercontent.com/yu66/blog-website/images/svn/6.jpg)

`localhost`是你要把文件提交到的svn版本库的ip，我在本机做的所以就填写的localhost。而newdemo是我为提交的文件所存放的文件夹。导入信息就是这个版本的信息，可以在版本浏览器中看到。
[](https://raw.githubusercontent.com/yu66/blog-website/images/svn/7.jpg)
填写好在passwd文件里配置好的用户名和密码就可以提交文件了。
[](https://raw.githubusercontent.com/yu66/blog-website/images/svn/8.jpg)

## 修改服务器地址
当svn服务器地址变化之后我们使用如下命令进行修改
```java
svn switch --relocate http://10.234.10.11/svn/server/ http://10.230.8.116/svn/server/
```
> 在修改前后我们可以使用`svn info`命令进行查看, 当前svn信息.

## 无法clean up
在TortoiseSVN执行clean up时报错“Previous operation has not finished; run 'cleanup' if it was interrupted”, 遇到这种情况只要在命令行里执行
```bash
svn cleanup
```
就好了
> 需要在安装TortoiseSVN时, 勾选上命令行工具