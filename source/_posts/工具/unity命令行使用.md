category: 工具
date: 2015-08-08
title: unity命令行使用
---
# Command line arguments

当从命令行启动`Unity`时,它可以在启动时接受一些参数和信息, 这种方式可以用于测试用例，自动构建和其他的任务。

在`MacOS`系统下，你可以像下面这样启动
```
 /Applications/Unity/Unity.app/Contents/MacOS/Unity
```
当在windows系统里，你就需要执行下面的命令了
```
 "C:\Program Files (x86)\Unity\Editor\Unity.exe"
```

## Options

正如上面提到的,`editor`也可以在启动时通过一些额外的命令来构建游戏, 下面就列举出了这些命令：

* `-assetServerUpdate <IP[:port] projectName username password [r <revision>]>`	从`IP:port`上的`Asset Server`强制更新项目. `port`是可选的,如果不指定的话,会默认选择`10733`这个端口. 这个命令可与`-projectPath`参数一起使用, 这样可确保你不会更新错项目.如果不指定项目名称的话,那么会默认的对`Unity`上次打开的项目进行更新. 如果`-projectPath`路径下不存在项目,那么会自动创建一个.
* `-batchmode`  在`batch`模式下运行`Unity`.这个命令我们强烈建议你与其他命令一起使用, 它会确保不会弹出Edtior. 当由脚本代码抛出异常或者`Asset Server`更新失败或者其他操作引起的异常,`Unity`会直接返回错误码`1`并退出. 需要注意的是,在`batch`模式下,`Unity`会在控制台输出一些基础日志. 还有当在`batch`模式下打开一个项目,那么`Editor`就不能再开打这个相同的项目.
* `-buildLinux32Player <pathname>`	构建一个32位的linux版应用.(例如 `-buildLinux32Player path/to/your/build`).
* `-buildLinux64Player <pathname>`	构建一个64位的linux版应用.(例如 `-buildLinux64Player path/to/your/build`).
* `-buildLinuxUniversalPlayer <pathname>`	构建一个32位和64位的linux混合版应用.(例如 `-buildLinuxUniversalPlayer path/to/your/build`).
* `-buildOSXPlayer <pathname>`	构建一个32位的MacOS版应用.(例如 `-buildOSXPlayer path/to/your/build.app`).
* `-buildOSX64Player <pathname>`	构建一个64位的MacOS版应用.(例如 `-buildOSX64Player path/to/your/build.app`).
* `-buildOSXUniversalPlayer <pathname>`	构建一个32位和64位的MacOs混合版应用.(例如 `-buildOSXUniversalPlayer path/to/your/build.app`).
* `-buildTarget <name>`	当项目被加载之前允许用户选择的构建目标：`win32, win64, osx, linux, linux64, ios, android, web, webstreamed, webgl, xbox360, xboxone, ps3, ps4, psp2, wsa, wp8, bb10, tizen, samsungtv`.
* `-buildWebPlayer <pathname>`	构建一个在web上运行的应用.(例如`-buildWebPlayer path/to/your/build`).
* `-buildWebPlayerStreamed <pathname>`	Build a streamed WebPlayer (`-buildWebPlayerStreamed path/to/your/build`).
* `-buildWindowsPlayer <pathname>`	构建一个32位的Windows版应用.(例如  `-buildWindowsPlayer path/to/your/build.exe`).
* `-buildWindows64Player <pathname>`	构建一个64位的Windows版应用.(例如  `-buildWindows64Player path/to/your/build.exe`).
* `-createProject <pathname>`	在指定路径下(`pathname`)创建一个空项目.
* `-executeMethod <ClassName.MethodName>`	只要Unity启动完成并且项目打开(可能还需要等待asset server更新完成)就执行该静态方法. 该功能可用于持续集成, 运行测试用例, 执行构建任务, 准备一些数据等等. 如果你想让程序在命令行中返回一个错误码,那么你可以直接在Unity中直接抛出一个异常,这时命令行返回的错误码是1，或者你可以调用`EditorApplication.Exit`,这时会返回一个非0的状态码. 如果你想要向该方法传递参数,那么你可以直接将这些参数添加到命令行上,然后在程序中使用`System.Environment.GetCommandLineArgs`获得这些参数.To use -executeMethod, you need to place the enclosing script in an Editor folder. The method to be executed must be defined as static.
* `-exportPackage <exportAssetPath1 exportAssetPath2 ExportAssetPath3 exportFileName>`	在指定的路径下导出`package`. `exportAssetPath`是一个从Unity项目导出到的文件夹(路径相对于Unity项目的根路径), `exportFileName`是package的名字. 一般这个选项可以一次性地导出整个文件夹.这个命令一般需要和`-projectPath`参数一起使用.
* `-force-d3d9 (Windows only)`	设置editor使用`Direct3D 9`进行渲染. 这个是默认选项,一般不需要你去设置这个值.
* `-force-d3d11 (Windows only)`	设置editor使用`Direct3D 11`进行渲染.
* `-force-opengl (Windows only)`	设置editor使用`OpenGL`进行渲染. 即使`Direct3D`可用我们也可以说使用`OpenGL`进行渲染. 一般我们是在` Direct3D 9.0c`不可用的情况下才选择使用`openGL`
* `-force-free`	让edtior在`Unity license`下运行, 即使我们安装了`Unity Pro license`
* `-importPackage <pathname>`	导入指定的`package`. 如果不进行导入的话,会出现一个对话框.
* `-logFile <pathname>`	指定Editor或者`Windows/Linux/OSX`版本应用的日志输出路径.
* `-silent-crashes`	不显示crashe对话框.
* `-projectPath <pathname>`	在指定的路径下打开项目.
* `-quit`	当其他命令都执行完之后退出Unity. 注意这个会将错误日志隐藏掉，但是可以在`Editor.log`中找到它.
* `-serial <serial>`	Activates Unity with the specified serial key. It is recommended to pass “-batchmode -quit” arguments as well, in order to quit Unity when done, if using this for automated activation of Unity. Please allow a few seconds before license file is created, as Unity needs to communicate with the license server. Make sure that License file folder exists, and has appropriate permissions before running Unity with this argument. In case activation fails, see the Editor.log for info. This option is new in Unity 5.0.

#### Example usage
```
// C# example
using UnityEditor;
class MyEditorScript
{
     static void PerformBuild ()
     {
         string[] scenes = { "Assets/MyScene.unity" };
         BuildPipeline.BuildPlayer(scenes, ...);
     }
}


// JavaScript example
static void PerformBuild ()
{
    string[] scenes = { "Assets/MyScene.unity" };
    BuildPipeline.BuildPlayer(scenes, ...);
}
```
下面的命令在`batch`模式下运行Unity, 同时执行`MyEditorScript.MyMethod`完成, 当该方法执行完之后退出.
* `Windows`: `C:\program files\Unity\Editor\Unity.exe -quit -batchmode -executeMethod MyEditorScript.MyMethod`
* `Mac OS`: `/Applications/Unity/Unity.app/Contents/MacOS/Unity -quit -batchmode -executeMethod MyEditorScript.MyMethod`

下面的命令在`batch`执行Unity, 同时从`asset server`更新指定项目. 当全部的`asset`下载完之后, 指定的方法会被执行,当方法被完全执行之后,Unity会自动退出.
```
/Applications/Unity/Unity.app/Contents/MacOS/Unity -batchmode -projectPath ~/UnityProjects/AutobuildProject -assetServerUpdate 192.168.1.1 MyGame AutobuildUser l33tpa33 -executeMethod MyEditorScript.PerformBuild -quit
```

