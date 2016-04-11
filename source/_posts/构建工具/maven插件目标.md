category: 构建工具
date: 2015-06-08
title: maven插件目标
---

插件里会包含多个目标,每个目标都对应着特定的功能,也就是说插件里的功能是通过目标来实现了.

例如`maven-compiler-plugin`的`compile`目标的写法为`compiler:compile`.

### 插件绑定
我们可以将插件的目标与生命周期的阶段相绑定.

default生命周期与内置插件绑定关系及具体任务:

生命周期阶段                  | 插件目标                              |执行任务
-----------------------------|--------------------------------------|--------------
process-resources            |maven-resources-plugin:resources      |复制主资源文件至主输出目录
compile                      |maven-compile-plugin:compile	        |编译主代码至主输出目录
process-test-resources       |maven-resources-plugin:testRresources |复制测试资源文件至测试输出目录
test-compile                 |maven-compiler-plugin:testCompile     |编译测试代码至测试输出目录
test	                     |maven-surefire-plugin:test            |执行测试用例
package	                     |maven-jar-plugin:jar                  |创建项目jar包
install	                     |maven-install-plugin:install          |将项目输出构件安装到本地仓库
deploy                       |maven-deploy-plugin:deploy            |将项目输出构件部署到远程仓库


我们来自定义绑定：
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>1.1.1</version>
            <executions>
                <execution>
                    <phase>install</phase>
                    <goals>
                        <goal>java</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
我们在`install`阶段绑定了`exec-maven-plugin`插件的`java`目标.

## 带有依赖包构建
采用下面的插件, 当构建时所需的依赖包也会输出到target目录下
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>
                            ${project.build.directory}
                        </outputDirectory>
                    </configuration>
                </execution>
            </executions>
        </plugin>

    </plugins>
</build>
```

## 打可运行jar包
