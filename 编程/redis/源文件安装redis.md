# 源文件安装redis

## 下载源文件

The Redis source files are available from the [Download](https://redis.io/download) page. You can verify the integrity of these downloads by checking them against the digests in the [redis-hashes git repository](https://github.com/redis/redis-hashes).

To obtain the source files for the latest stable version of Redis from the Redis downloads site, run:

```bash
wget https://download.redis.io/redis-stable.tar.gz
```

## Compiling Redis

To compile Redis, first the tarball, change to the root directory, and then run `make`:

```bash
tar -xzvf redis-stable.tar.gz
cd redis-stable
make
```

If the compile succeeds, you'll find several Redis binaries in the `src` directory, including:

- **redis-server**: the Redis Server itself
- **redis-cli** is the command line interface utility to talk with Redis.

To install these binaries in `/usr/local/bin`, run:

```bash
make install
```

## Starting and stopping Redis in the foreground

Once installed, you can start Redis by running

```bash
redis-server
```

If successful, you'll see the startup logs for Redis, and Redis will be running in the foreground.

To stop Redis, enter `Ctrl-C`.