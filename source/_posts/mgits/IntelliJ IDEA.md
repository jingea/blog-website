category: mgits
date: 2015-08-08
title: IntelliJ IDEA
---
## 快捷键

* 块选择 : `ALT Shift Insert`

查询快捷键
* Ctrl+N   查找类 
* Ctrl+SHIFT+N  查找文件 
* CIRL+B / F4   找变量的来源 
* Ctrl+ALT+B  找所有的子类 
* Ctrl+G   定位行 
* Ctrl+F   在当前窗口查找文本 
* Ctrl+SHIFT+F  在指定窗口查找文本 
* Ctrl+R   在当前窗口替换文本 
* Ctrl+SHIFT+R  在指定窗口替换文本 
* Ctrl+E   最近打开的文件 
* F3   向下查找关键字出现位置 
* SHIFT+F3  向上一个关键字出现位置 
* F4   查找变量来源 
* Ctrl+ALT+F7  选中的字符查找工程出现的地方 

自动代码
* ALT+回车  导入包,自动修正 
* Ctrl+ALT+L  格式化代码 
* Ctrl+ALT+I  自动缩进 
* Ctrl+ALT+O  优化导入的类和包 
* ALT+INSERT  生成代码(如GET,SET方法,构造函数等)  
* Ctrl+SHIFT+SPACE 自动补全代码 
* Ctrl+空格  代码提示 
* Ctrl+ALT+SPACE  类名或接口名提示 
* Ctrl+P   方法参数提示 
* Ctrl+J   自动代码 
* Ctrl+ALT+T  把选中的代码放在 TRY{} IF{} ELSE{} 里

复制快捷方式
* Ctrl+D   复制行 
* Ctrl+X   剪切,删除行  

其他快捷方式
* Ctrl+Z   倒退 
* Ctrl+SHIFT+Z  向前 
* Ctrl+ALT+F12  资源管理器打开文件夹 
* Ctrl+/   注释//   
* Ctrl+SHIFT+/  注释/*...*/ 
* Ctrl+W   选中代码，连续按会有其他效果 
* Ctrl+B   快速打开光标处的类或方法 
* ALT+ ←/→  切换代码视图 
* Ctrl+ALT ←/→  返回上次编辑的位置 
* ALT+ ↑/↓  在方法间快速移动定位 
* SHIFT+F6  重构-重命名 
* Ctrl+Q   显示注释文档 
* ALT+1   快速打开或隐藏工程面板 
* Ctrl+SHIFT+UP/DOWN 代码向上/下移动。 
* Ctrl+UP/DOWN  光标跳转到第一行或最后一行下 
* F2 或Shift+F2 高亮错误或警告快速定位

版本控制相关
* ALT+SHIFT+C  查找修改的文件 
* Ctrl+SHIFT+Alt+UP/DOWN  跳转到当前文件的上一个修改或者下一个修改

## 缩进设置
![](https://raw.githubusercontent.com/ming15/blog-website/images/other/idea_indent.jpg)
* `Use tab character` : 如果勾选则使用tab缩进,否则使用空格缩进
* `Tab size` : 每个tab占用几个空格.
* `Indent` : 每个缩进占用几个空格.

## 2016-03-03
刚刚来到了新的项目, 新的项目里使用Gradle作为编译工具.  说完前文说正题: 开发完新的模块后打算写单元测试来验证一下自己的功能. 但是在运行的时候遇到了一些问题, 由于整个项目都没进行单元测试的编写, 因此需要搭建起这个环境,

其实环境很简单只要单元测试能编译通过运行就ok了，但是仍然发生了一些问题,由于项目是从JDK7升级上来的, 因此有些地方还是采用的配置, 因此当发现在编译时遇到了
* class版本不支持
* 提示:无效的源发行版: 1.8
我们要检查俩个地方
* build.gradle 这个文件里指定了编译器版本(例如sourceCompatibility, 需要注意的是看看是否依赖了其他的gradle配置, 我就踩中了这个坑)
* Intellij IDEA -Preference-Build, Execution, Deployment-Gradle -Gradle JVM, 看一下这个配置里面的配置, 当我将gradle的配置修改之后如果不修改这里就会出现  无效的源发行版: 1.8

另外我们一般在项目里会设置JDK版本(项目右击Open Moudle Setting), 这个只是设置整个idea的项目相关的, 也就是和你写代码相关, 和项目的构建等等没啥关系

## 使用eclipse格式化文件
IDEA如果要使用eclipse的格式化文件需要安装`Eclipse Code Formatter`插件, 然后在setting里的`Other Seetings`里的`Eclipse Code Formatter`里引用格式化文件就好了

## Idea打包与classpath
当我们使用maven创建一个项目的时候会创建出如下的目录
![](https://raw.githubusercontent.com/ming15/blog-website/images/other/idea%20classpath.png)
我们会看到如下的几个目录, 这几个目录都是设置在classpath上
* Sources ：src/main/java
* Resources : src/main/resources
如果我们要新添加一个classpath时, 点击add Content Root就好. 
> 注意我们不要使用`Run/Debug Configurations`里的VM options, 这个路径有时候是让启动我们自己程序的Idea进程使用的.
我们在程序里可以使用
```java
InputStream in = TestReadFile.class.getClassLoader().getResourceAsStream("./mybatis-config.xml");
```
这种直接读取classpath也就是resources中的文件. 当使用Maven打包之后, 也可以直接从jar包中读取
![]()
我们看到resources目录直接放到了jar包的根目录下,但是我们需要配置一些maven的打包方式
```xml

```

