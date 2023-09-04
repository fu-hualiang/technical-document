# Windows 添加新建文件类型

以 md 文件为例

## 1.打开注册表

## 2.找对应文件类型

在 HKEY_CLASSES_ROOT 下找对应文件类型

![image-20230409130536767](Windows添加新建文件类型.assets/image-20230409130536767.png)

## 3.修改类型默认值

将默认值修改为指定打开该文件的程序

![image-20230409130642299](Windows添加新建文件类型.assets/image-20230409130642299.png)

该程序需要在 HKEY_CLASSES_ROOT 下

![image-20230409131344701](Windows添加新建文件类型.assets/image-20230409131344701.png)

## 4.新建内容

在 .md 下新建**项** ShellNew，并在 ShellNew 项下新建**字符串值** NullFile

![image-20230409131103161](Windows添加新建文件类型.assets/image-20230409131103161.png)