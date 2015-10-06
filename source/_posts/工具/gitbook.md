category: 工具
title: gitbook使用
---
# 安装gitbook命令行

1. 下载安装`npm`和`io.js`
2. 安装`git`, `gitbook`需要依赖`git`.
3. 将`git`的`bin`目录放到环境变量`Path`里
4. 在windows下npm module一般都是安装到`C:\Users\Administrator\AppData\Roaming\npm\node_modules`这
    里,所以为了我们能够使用安装好的module,我们将这个路径添加到环境变量`Path`里
5. 然后使用`npm install gitbook-cli -g` 安装gitbook
6. 最后验证一下gitbook是否安装成功： `gitbook -V` 我安装的是`0.3.3`, 所以在命令行里直接输出了`0.3.3`


# gitbook + github简历博客
我们假设下列所有操作都在`D:\git`这个目录下操作
1. 我们将github上创建的项目`demo`检出到`D:\git`目录里,最好你也是用svn检出的，因为我是在svn检出的前提下写了个小工具
2. 然后我们进入到`D:\git\demo`目录里,我们会看到`branches`和`trunk`俩个文件夹,`branches`用于存储博客的web文件,`trunk`用于存放博客的`markdown`源文件
3. 接着我们进入到`D:\git\demo\trunk`新建`blog`文件夹
4. 进入到`D:\git\demo\trunk\blog`在这个目录里新建一个`build.bat`批处理脚本文件,同时创建一个`repository`
5. `build.bat`批处理脚本文件内容为`gitbook build ./repository ../../branches/gh-pages`
6. 我们使用gitbook客户端在`repository`文件夹内创建一个gitbook项目
7. 双击运行`build.bat`
8. 查看`D:\git\demo\branches\gh-pages`是否生成了一个web站点呢？这个就是我们的博客了
9. 最后在`D:\git\demo`这个目录里上传所有的文件就好了

