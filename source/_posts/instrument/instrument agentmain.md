category: instrument
date: 2015-11-24
title: instrument Agentmain
---
在 Java SE 5 中premain 所作的 Instrumentation 也仅限与 main 函数执行前，这样的方式存在一定的局限性。Java SE 6 针对这种状况做出了改进，开发者可以在 main 函数开始执行以后，再启动自己的 Instrumentation 程序。在 Java SE 6 的 Instrumentation 当中，有一个跟 premain“并驾齐驱”的“agentmain”方法，可以在 main 函数开始运行之后再运行。

首先我们还是需要修改MANIFEST.MF文件, 在其中添加
```
Agent-Class: wang.ming15.instrument.core.Agentmain
```

## 获取对象大小
同样我们还是先输出一下对象的大小
```

```