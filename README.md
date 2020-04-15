##### 在您的工作站上打开一个终端，并使用 ssh-keygen 命令生成新的密钥。指定 -C 标志以添加一条带有您的用户名的注释。

`ssh-keygen -t rsa -f ~/.ssh/[KEY_FILENAME] -C [USERNAME]`

##### 向实例提供 SSH 公钥

##### 在本地终端中，使用 ssh 命令以及您的 SSH 私钥文件、用户名和要连接到的实例的外部 IP 地址。例如：

`ssh -i path-to-private-key username@external-ip`


##### 从多个压缩文件解压

`cat lotus-make.* | tar xzvf -`
