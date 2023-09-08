# 使用 ZIP 压缩包安装 MySQL

参考自官方文档 [Installing MySQL on Microsoft Windows Using a noinstall ZIP Archive ](https://dev.mysql.com/doc/refman/8.0/en/windows-install-archive.html) 

## 下载安装包并解压

解压后创建系统变量，并将 bin 文件夹路径添加至 path。



## 创建选项文件

在解压的 MySQL 文件夹中创建 my.ini 文件：

```ini
[mysqld]
# set basedir to your installation path
basedir=E:\\mysql
# set datadir to the location of your data directory
datadir=E:\\mydata\\data
```



## 初始化 data 目录

在 MySQL 文件夹位置执行  --initialize 或 --initialize-insecure 生成 data 文件夹：

```bash
# 生成一个随机密码的 root 账户，密码出现在控制台打印的信息中，如下：
# [Warning] A temporary password is generated for root@localhost:
# iTag*AfrH5ej
bin\mysqld --initialize --console

# 生成一个没有密码的 root 账户
bin\mysqld --initialize-insecure --console
```



## 首次启动 MySQL 服务器

```bash
C:\> "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld" --console
```



## 将 MySQL 作为 Windows 服务启动

简单命令：

```bash
C:\> "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld" --install
```

复杂命令：

```bash
C:\> "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld"
          --install MySQL --defaults-file=C:\my-opts.cnf
```

--install 后的参数设置 Windows 服务的名称，没有设置则默认为 MySQL

--defaults-file 设置选项文件的位置

启动服务：

```bash
sc start MySQL
net start MySQL
```

> 注意：如果无法启动服务，打开服务界面找到 MySQL，查看可执行文件的路径，mysqld 和 my.ini 的路径是否正确。



## 连接 MySQL 服务器

```bash
# 使用 --initialize
mysql -u root -p
Enter password: (enter the random root password here)

# 使用 --initialize-insecure
mysql -u root --skip-password
```

修改 root 密码：

```mysql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root-password';
```

