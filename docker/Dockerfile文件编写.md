# Dockerfile文件编写

## 更换源

制作镜像时直接修改源，将sources.list文件替换为清华源（ubuntu 20.04版本，https改为了http）：

```
# 默认注释了源码镜像以提高 apt update 速度，如有需要可自行取消注释
deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal main restricted universe multiverse
# deb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal main restricted universe multiverse
deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-updates main restricted universe multiverse
# deb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-updates main restricted universe multiverse
deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-backports main restricted universe multiverse
# deb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-backports main restricted universe multiverse
deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-security main restricted universe multiverse
# deb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-security main restricted universe multiverse

# 预发布软件源，不建议启用
# deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-proposed main restricted universe multiverse
# deb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ focal-proposed main restricted universe multiverse
```

## dockerfile

将上面的源保存为sources.list文件，与dockerfile放在同一文件夹下。

```dockerfile
FROM jdk:8
LABEL Description="lighthouse" Vendor="fhl" version="0.0.1"
RUN mkdir -p /opt/lighthouse
RUN mv /etc/apt/sources.list /etc/apt/sources.list.bak
ADD ./lighthouse-0.0.1-SNAPSHOT.jar /opt/lighthouse
ADD ./sources.list /etc/apt
ENV JAVA_HOME /opt/jdk
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH $JAVA_HOME/bin:$PATH
EXPOSE 7000
ENTRYPOINT ["java","-jar","/opt/lighthouse/lighthouse-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
```