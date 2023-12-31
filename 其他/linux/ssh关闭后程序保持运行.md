# ssh 关闭后程序保持运行

## 问题描述

SSH 远程连接到服务器后运行程序

```sh
java -jar xxx.jar --spring.profiles.active=prod
```

终端开闭（切断 SSH 连接）之后,发现该程序中断。

## 原因

主要元凶：挂断信号(SIGHUP) 信号

概念介绍
在Linux/Unix中，有这样几个概念：
进程组(process group): 一个或多个进程的集合,每一个进程组有唯一一个进程组ID,即进程组长进程的ID.
会话期(session): 一个或多个进程组的集合,有唯一一个会话期首进程(session leader). 会话期ID为首进程的ID.
会话期可以有一个单独的控制终端(controlling terminal).
与控制终端连接的会话期首进程叫做控制进程(controlling process).
当前与终端交互的进程称为前台进程组.
其余进程组称为后台进程组.
根据POSIX.1定义: 挂断信号(SIGHUP)默认的动作是终止程序。

解释
当终端接口检测到网络连接断开, 将挂断信号发送给控制进程(会话期首进程).
如果会话期首进程终止,则该信号发送到该会话期前台进程组.
一个进程退出导致一个孤儿进程组产生时, 如果任意一个孤儿进程组进程处于STOP状态, 发送 SIGHUP 和 SIGCONT 信号到该进程组中所有进程.
孤儿进程参照

结论
因此当网络断开或终端窗口关闭后, 也就是SSH断开以后, 控制进程收到 SIGHUP 信号退出, 会导致该会话期内其他进程退出.
简而言之: 就是 ssh 打开以后, bash等都是他的子程序, 一旦ssh关闭, 系统将所有相关进程杀掉!! 导致一旦ssh关闭, 执行中的任务就取消了.

相关问题
为什么守护程序就算是 ssh 打开的, 关闭ssh也不会影响其运行？
因为他们的程序特殊, 比如httpd –k start运行这个以后, 他不属于sshd这个进程组, 而是单独的进程组, 所以就算关闭了ssh, 和他也没有任何关系!
使用后台运行命令 & 能否将程序摆脱ssh进程组控制? 即关闭 ssh, 后台程序能否继续运行?
只要是ssh 打开执行的一般命令，不是守护程序，无论加不加&，一旦关闭ssh，系统就会用SIGHUP终止.

## 解决方案

一分钟了解nohup和&的功效（不挂断地运行命令）
https://blog.csdn.net/hl449006540/article/details/80216061

### 1.nohup

用途：不挂断地运行命令。

语法：nohup Command [ Arg … ] [　& ]

无论是否将 nohup 命令的输出重定向到终端，输出都将附加到当前目录的 nohup.out 文件中。

如果当前目录的 nohup.out 文件不可写，输出重定向到 $HOME/nohup.out 文件中。

如果没有文件能创建或打开以用于追加，那么 Command 参数指定的命令不可调用。

退出状态：该命令返回下列出口值： 　　
　　126 可以查找但不能调用 Command 参数指定的命令。 　　
　　127 nohup 命令发生错误或不能查找由 Command 参数指定的命令。 　　
　　否则，nohup 命令的退出状态是 Command 参数指定命令的退出状态。

### 2.&

用途：在后台运行
一般两个一起用

```sh
nohup java -jar xxx.jar --spring.profiles.active=prod &
```

