category: Java Library
date: 2016-05-11
title: Oracle® Solaris Studio 
---
在Centos上安装Oracle® Solaris Studio.  [中文教程](http://docs.oracle.com/cd/E27071_01/html/E26451/gemyt.html#scrolltoc)

首先执行下列命令
```bash
yum install glibc
yum install elfutils-libelf-devel
yum install zlib
yum install libstdc++
yum install libgcc
```
执行完之后, 会将下列依赖包安装完成
```bash
glibc
glibc.i686
glibc-devel
glibc-devel.i686
elfutils-libelf-devel 
elfutils-libelf-devel.i686
zlib
zlib.i686
libstdc++
libstdc++.i686
libgcc
libgcc.i686
```

1. 在[下载界面](http://www.oracle.com/technetwork/server-storage/solarisstudio/downloads/index.html)下载Oracle Linux/ Red Hat Linux - RPM installer on x86 
2. 运行命令解压`bzcat download_directory/SolarisStudio12.4-linux-x86-rpm.tar.bz2 | /bin/tar -xf -`
3. 进行安装`./solarisstudio.sh --non-interactive `(包含GUI, 也就是我们可以在Linux的桌面上打开Solaris Studio IDE)
4. 验证是否安装成功`/opt/oracle/solarisstudio12.4/bin/analyzer -v`

安装完成后会显示
```bash
Configuring the installer...
Searching for JVM on the system...
Extracting installation data (can take a while, please wait)...
Running the installer wizard...
/tmp/ossi-c2x_test-20160509142618.silent.log:
[2016-05-09 14:26:18.764]: WARNING - Your OS distribution is not supported. The list of supported systems can be found in the Oracle Solaris Studio documentation. While it might be possible to install Oracle Solaris Studio on your system, it might not function properly.
```
运行`analyzer -v`显示
```bash
analyzer: Oracle Solaris Studio 12.4 Performance Analyzer 12.4 Linux_x64 2014/10/21
Java at /usr/java/jdk1.8.0_25/bin/java selected by PATH
java version "1.8.0_25"
Java(TM) SE Runtime Environment (build 1.8.0_25-b17)
Java HotSpot(TM) 64-Bit Server VM (build 25.25-b02, mixed mode)
WARNING: Linux CentOS_6.7 system "c2x_test" is not supported by the Performance tools.
Running /usr/java/jdk1.8.0_25/bin/java -version
/opt/oracle/solarisstudio12.4/bin/analyzer: ERROR: environment variable DISPLAY is not set
```

额外事项
1. 卸载程序 `/opt/oracle/solarisstudio12.4/uninstall.sh --non-interactive`
2. 如果安装时不想要GUI, 只需要在后面加上`--libraries-only`就好了
3. 设置环境变量 `vi /ect/profile` 修改`
PATH=$PATH:/opt/oracle/solarisstudio12.4/bin/`