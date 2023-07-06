linux 系统下，普通用户的terminal有颜色，切换为 root 用户时变成黑白。

进入 /root 目录下，打开 .bashrc 文件，在底部加上

```sh
PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\$ '

alias ls='ls --color=auto'
alias dir='dir --color=auto'
alias vdir='vdir --color=auto'
alias grep='grep --color=auto'
alias fgrep='fgrep --color=auto'
alias egrep='egrep --color=auto'
alias ll='ls -alF'
alias la='ls -A'
alias l='ls -CF'
```

内容可以参考普通用户目录下的.bashrc 文件。

