# Spring Boot 整合 MyBatis

推荐安装 Intellij idea 插件：MyBatisX

## 目录结构

```
/my-application
	/src/main
		/java
			/com/example
				/controller
				/service
				/mapper			此处放 mapper 接口
		/resources
			/mapper				此处放 xml 文件
```

一个 mapper 对应一个 xml 文件



## 扫描所有 MyBatis 文件

在启动类上添加注解 @MapperScan( basePackages = "com.example.mapper") 指向 mapper 接口所在的文件夹，扫描所有 mapper 接口。

在 spring 配置文件中添加 MyBatis 相关配置，扫描所有的 xml 文件。

```yaml
mybatis:
	mapper-locations: classpath:/mapper/*.xml
```

mapper 接口的**全类名**与 xml 文件的 **namespace** 一一对应。