# 使用 MySQL APT 仓库

## Steps for a Fresh Installation of MySQL

### 添加 MySQL APT 仓库

从 [MySQL :: Download MySQL APT Repository](https://dev.mysql.com/downloads/repo/apt/) 下载 MySQL APT 仓库。

安装仓库：

```bash
sudo dpkg -i mysql-apt-config_version_all.deb
```

更新包信息：

```bash
sudo apt-get update
```



### 安装 MySQL

```bash
sudo apt-get install mysql-server
```



### 启动和停止 MySQL Server

```bash
# 启动、停止、重启、查看状态
systemctl {start|stop|restart|status} mysqld
```

