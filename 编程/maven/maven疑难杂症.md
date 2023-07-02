# 程序包XXX不存在，找不到符号

springboot多包工程，在使用maven进行打包的时候，一直提示“程序包xxx不存在，找不到符号”，但项目是可以正常启动运行的，开始一直以为是maven哪里没有配置好导致的，后来才发现原来是**springboot的maven插件导致的**。

## 项目结构

xxx-parent：顶级父工程

xxx-a：子项目1

xxx-b：子项目2

xxx-c：子项目3

## 依赖关系

a -> b -> c，从左到右，依次被依赖（c依赖b，b依赖a）

## 问题场景

在使用mvn package的时候，一直提示“程序包xxx不存在，找不到符号”，开始以为是依赖的上层包没打好，仔细检查后发现都没问题，就算将本地仓库中的依赖包清理之后，重新install打包也还是有这个问题

## 原因分析

因为之前的springmvc出现过类似的问题（[Maven打包失败，提示“找不到符号”](https://www.jiweichengzhu.com/article/aaa381863d6b41f9b21ab9a28a0cf619)），所以一开始也以为是本地仓库的缓存所致，浪费了好长时间，后来才发现原来是springboot自身的编译插件spring-boot-maven-plugin导致的。

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

如上，就是springboot的maven插件，用这个插件打包的Jar包可以直接运行，但是不可依赖！

## 解决方案（三选一）

1、不要将此插件放到顶级父工程中，在需要打成可执行jar的地方添加就好了，如果是需要被依赖的，就不要添加此插件（如上述案例中，就是xxx-a、xxx-b不加，xxx-c需要加）；

2、在需要对外提供依赖的项目的pom里设置（如本项目的xxx-a、xxx-b），这样设置会让项目生成两个jar：一个可执行jar，一个可依赖的jar；

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <!-- 生成可执行的jar的名字：xxx-exec.jar -->
                <!-- 不固定，写成abcd都可以 -->
                <classifier>exec</classifier>
            </configuration>
        </plugin>
    </plugins>
</build>
```

3、在configuration中加入skip标签，取消生成可执行jar；

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <skip>true</skip>
            </configuration>
        </plugin>
    </plugins>
</build>
```
