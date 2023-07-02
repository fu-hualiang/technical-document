# springboot配置https连接

[(3条消息) Https 生成证书（keytool），并在Springboot中进行配置_一名小码农的博客-CSDN博客_keytool生成证书并设置时间限制](https://blog.csdn.net/zyx1260168395/article/details/112802464)



keytool 是一个 Java 数据证书的管理工具，keytool 将密钥（key）和证书（certificates）存在一个称为 keystore 的文件中。

相关概念：一文看懂HTTPS、证书机构（CA）、证书、数字签名、私钥、公钥

在 keystore 里，包含两种数据：

密钥实体（Key entity）：密钥（secret key）又或者是私钥和配对公钥（采用非对称加密）
可信任的证书实体（trusted certificate entries）：只包含公钥
ailas (别名)：每个 keystore 都关联这一个独一无二的 alias，这个 alias 通常不区分大小写

## 一、keytool生成keystore

keytool 中常用选项：

-genkey：在用户主目录中创建一个默认文件 “.keystore”，还会产生一个 mykey 的别名，mykey 中包含用户的公钥、私钥和证书（在没有指定生成位置的情况下，keystore 会存在用户系统默认目录，如：对于window xp系统，会生成在系统的C:\Documents and Settings\UserName\文件名为.keystore）
-alias：产生别名
-keystore：指定密钥库的名称（产生的各类信息将在 .keystore 文件中）
-keyalg：指定密钥的算法（如 RSA、DSA（如果不指定默认采用 DSA））
-validity：指定创建的证书有效期多少天
-keysize：指定密钥长度
-storepass：指定密钥库的密码（获取 keystore 信息所需的密码）
-keypass：指定别名条目的密码（私钥的密码）
-dname：指定证书拥有者信息。例如：“CN=名字与姓氏，OU=组织单位名称，O=组织名称，L=城市或区域名称，ST=州或省份名称，C=单位的两字母国家代码”
-list：显示密钥库中的证书信息。（keytool -list -v -keystore 指定keystore -storepass 密码）
-v：显示密钥库中的证书详细信息。
-export：将别名指定的证书导出到文件。（keytool -export -alias 需要导出的别名 -keystore 指定keystore -file 指定导出的证书位置及证书名称 -storepass 密码）
-file：参数指定导出到文件的文件名
-delete：删除密钥库中某条目。（keytool -delete -alias 指定需删除的别名 -keystore 指定keystore -storepass 密码）
-printcert：查看导出的证书信息。（keytool -printcert -file yushan.crt）
-keypasswd：修改密钥库中指定条目口令。（keytool -keypasswd -alias 需修改的别名 -keypass 旧密码 -new 新密码 -storepass keystore密码 -keystore sage）
-storepasswd：修改 keystore 口令。（keytool -storepasswd -keystore e:\yushan.keystore(需修改口令的keystore) -storepass 123456(原始密码) -new yushan(新密码)）
-import：将已签名数字证书导入密钥库。（keytool -import -alias 指定导入条目的别名 -keystore 指定keystore -file 需导入的证书）

### 1.生成 keystore 文件

```
# keytool -genkeypair -alias 别名 -keypass 私钥密码 -keyalg 密钥算法 -keysize 密钥长度 -validity 证书有效期 -keystore 密钥库的生成路径、名称 -storepass 密钥库密码
> keytool -genkeypair -alias test -keypass 123456 -keyalg RSA -keysize 1024 -validity 365 -keystore /Users/mac/Desktop/test.keystore -storepass 123456
您的名字与姓氏是什么?
  [Unknown]:  zyx
您的组织单位名称是什么?
  [Unknown]:  zyx
您的组织名称是什么?
  [Unknown]:  zyx
您所在的城市或区域名称是什么?
  [Unknown]:  bj
您所在的省/市/自治区名称是什么?
  [Unknown]:  bj
该单位的双字母国家/地区代码是什么?
  [Unknown]:  CN
CN=zyx, OU=zyx, O=zyx, L=bj, ST=bj, C=CN是否正确?
  [否]:  Y
```

### 2. 查看 keystore 文件详细信息

```
# keytool -list -v -keystore keystore文件 -storepass 密码
keytool -list -v -keystore test.keystore -storepass 123456
```

### 3. 从 keystore 中导出证书（公钥）

```
# keytool -export -alias 别名 -keystore keystore文件 -rfc -file 生成的证书名
keytool -export -alias test -keystore test.keystore -rfc -file test.cer

# .cer 转换成 .crt
openssl x509 -inform PEM -in test.cer -out test.crt
```

### 4. 查看证书详情

```
# keytool -printcert -file 证书名
keytool -printcert -file test.cer
```

### 5. 从 keystore 中导出私钥

特别需要注意的是，私钥是无法从证书库中导出的，因为那样非常不安全。如果你特别需要私钥或是私钥字符串，只能考虑用编程的方式从密钥库文件中去获取了。

由于 jdk 命令无法生成 key，所以需要用代码从 keystore 文件中读取私钥 base64 编码数据，然后格式化为一行64个字符

## 二、证书格式转换

```
# .key 转换成 .pem：
openssl rsa -in test.key -out test.pem

# .crt 转换成 .pem：
openssl x509 -in test.crt -out test.pem

# .cer 转换成 .crt
openssl x509 -inform PEM -in test.cer -out test.crt
```

## 三、springboot 使用 https

将生成的 `keystore` 文件放到项目的 `classpath` 目录下，在 `application.yaml` 配置文件中进行配置：

```
server:
  port: 8080

  #开启https，配置跟证书一一对应
  ssl:
  	#true表示开启HTTPS访问
    enabled: true
    #指定证书
    key-store: classpath:test.keystore
    #使用上面方法生成的格式为JKS
    key-store-type: JKS
    #默认为TLS，
    protocol: TLS
    #别名
    key-alias: test
    #私钥密码
    key-password: 123456
    #store文件密码
    key-store-password: 123456
```

一些 springboot 中 SSL 的配置：

|      |      |
| ---- | ---- |
|      |      |



## 四、Postman 发送 https 请求

Postman 不需要配置证书也可以访问 https 请求，不过对于自定义的证书，需要在设置里关闭 SSL 校验。