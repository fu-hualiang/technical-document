# 安装 Nginx

## Ubuntu

安装先决条件：

> ```
> Install the prerequisites:
> ```

```sh
sudo apt install curl gnupg2 ca-certificates lsb-release ubuntu-keyring
```



导入一个正式的 nginx 签名密钥，这样可以验证包的真实性。获取密钥：

> ```
> Import an official nginx signing key so apt could verify the packages authenticity. Fetch the key:
> ```

```sh
curl https://nginx.org/keys/nginx_signing.key | gpg --dearmor \
    | sudo tee /usr/share/keyrings/nginx-archive-keyring.gpg >/dev/null
```



验证下载的文件是否包含正确的密钥：

> ```
> Verify that the downloaded file contains the proper key:
> ```

```sh
gpg --dry-run --quiet --no-keyring --import --import-options import-show /usr/share/keyrings/nginx-archive-keyring.gpg
```



输出应包含完整指纹`573BFD6B3D8FBC641079A6ABABF5BD827BD9BF62`如下：

> ```
> The output should contain the full fingerprint `573BFD6B3D8FBC641079A6ABABF5BD827BD9BF62` as follows:
> ```

```
pub   rsa2048 2011-08-19 [SC] [expires: 2024-06-14]
      573BFD6B3D8FBC641079A6ABABF5BD827BD9BF62
uid                      nginx signing key <signing-key@nginx.com>
```

如果指纹不同，删除文件。

> If the fingerprint is different, remove the file.



要为稳定的 nginx 包设置 apt 存储库，请运行以下命令：

> ```
> To set up the apt repository for stable nginx packages, run the following command:
> ```

```sh
echo "deb [signed-by=/usr/share/keyrings/nginx-archive-keyring.gpg] \
http://nginx.org/packages/ubuntu `lsb_release -cs` nginx" \
    | sudo tee /etc/apt/sources.list.d/nginx.list
```



如果希望使用 mainline nginx 包，请改为运行以下命令：

> ```
> If you would like to use mainline nginx packages, run the following command instead:
> ```

```
echo "deb [signed-by=/usr/share/keyrings/nginx-archive-keyring.gpg] \
http://nginx.org/packages/mainline/ubuntu `lsb_release -cs` nginx" \
    | sudo tee /etc/apt/sources.list.d/nginx.list
```



设置存储库固定，使其更喜欢我们的软件包而不是发行版提供的软件包：

> ```
> Set up repository pinning to prefer our packages over distribution-provided ones:
> ```

```sh
echo -e "Package: *\nPin: origin nginx.org\nPin: release o=nginx\nPin-Priority: 900\n" \
    | sudo tee /etc/apt/preferences.d/99nginx
```



要安装 nginx，请运行以下命令：

> ```
> To install nginx, run the following commands:
> ```

```
sudo apt update
sudo apt install nginx
```

