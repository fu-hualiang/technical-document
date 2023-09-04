# linux 环境变量

1./etc/profile , /etc/profile.d ，~/.bashrc, ~/.bash_file,这几个文件的区别是啥，可能很多新人，很疑惑。即使很多配置一些软件环境变量的人也是很疑惑

  ~/.bashrc, ~/.bash_file这两个看到～这个符合，应该明白，这是宿主目录下的，即里面的环境变量也叫shell变量，是局部的，只对特定的shell有效，修改过别忘了source 命令一下。

  /etc/profile , /etc/profile.d，前面的是文件，后面一看也就明白.d表示目录， /etc/profile里面的变量是全局的，对所有用户的shell有效。