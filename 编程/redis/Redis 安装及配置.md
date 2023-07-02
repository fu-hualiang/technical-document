# Redis 安装及配置

## 安装

[Install Redis on Linux | Redis](https://redis.io/docs/getting-started/installation/install-redis-on-linux/)

### Install on Ubuntu/Debian

You can install recent stable versions of Redis from the official `packages.redis.io` APT repository.

> Prerequisites
>
> If you're running a very minimal distribution (such as a Docker container) you may need to install `lsb-release` first:
>
> ```bash
> sudo apt install lsb-release
> ```

Add the repository to the `apt` index, update it, and then install:

```bash
curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg

echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list

sudo apt-get update

sudo apt-get install redis
```



## 配置

- Copy the template configuration file you'll find in the root directory of the Redis distribution into `/etc/redis/` using the port number as name, for instance:

  ```sh
    sudo cp /etc/redis/redis.conf /etc/redis/6379.conf
  ```

- Create a directory inside `/var/lib/redis` that will work as data and working directory for this Redis instance:

  ```sh
    sudo mkdir /var/lib/redis/6379
  ```

- Edit the configuration file, making sure to perform the following changes:
  - Set **daemonize** to yes (by default it is set to no).
  - Set the **pidfile** to `/run/redis/redis_6379.pid` (modify the port if needed).
  - Change the **port** accordingly. In our example it is not needed as the default port is already 6379.
  - Set your preferred **loglevel**.
  - Set the **logfile** to `/var/log/redis/redis_6379.log`
  - Set the **dir** to `/var/lib/redis/6379` (very important step!)





配置文件：/etc/redis/**{端口}**.conf

**pidfile** ：/run/redis/redis-**{端口}**.pid

**logfile** ：/var/log/redis/redis-**{端口}**.log

**dir** ：/var/lib/redis/**{端口}**