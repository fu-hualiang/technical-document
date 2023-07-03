# Windows下安装MySQL详细教程

### 1、安装包下载

下载地址：[MySQL :: Download MySQL Community Server](https://dev.mysql.com/downloads/mysql/)



### 2、配置环境变量

变量名：MYSQL_HOME
变量值：E:\tools\mysql（示例）

在path中添加 %MYSQL_HOME%\bin;



### 3、生成data文件

以**管理员身份**运行cmd，并进入 E:\tools\mysql\bin> 下

执行命令：mysqld --initialize-insecure --user=mysql

在 E:\tools\mysql\bin 目录下生成 data 目录



### 4、安装MySQL

继续执行命令：mysqld -install



### 5、启动服务

继续执行命令：net start MySQL



### 6、登录MySQL

登录mysql:(因为之前没设置密码，所以密码为空，不用输入密码，直接回车即可）

执行命令：mysql -u root -p



### 7、查询用户密码

查询用户密码命令：select host,user,authentication_string from mysql.user;



### 8、设置（或修改）root用户密码

选择数据库
执行命令：use mysql

设置或修改密码
执行命令：ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '123456';

保存
执行命令：flush privileges; 

### 9、退出并再次登录

quit

mysql -u root -p

输入密码