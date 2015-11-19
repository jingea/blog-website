category: gradle
date: 2015-11-18
title: 初探Gradle
---
我使用idea创建一个gradle项目
```
group 'wang.ming15.gradle'
version '1.0-SNAPSHOT'

apply plugin: 'java'

sourceCompatibility = 1.5

repositories {
    mavenCentral()
}

dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.11'
}

```
然后我们打开idea右侧的Gradle标签：
![](https://raw.githubusercontent.com/ming15/blog-website/images/gradle/gradle_ui.jpg)
