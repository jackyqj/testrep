##### 在您的工作站上打开一个终端，并使用 ssh-keygen 命令生成新的密钥。指定 -C 标志以添加一条带有您的用户名的注释。

`ssh-keygen -t rsa -f ~/.ssh/[KEY_FILENAME] -C [USERNAME]`

##### 向实例提供 SSH 公钥

##### 在本地终端中，使用 ssh 命令以及您的 SSH 私钥文件、用户名和要连接到的实例的外部 IP 地址。例如：

`ssh -i path-to-private-key username@external-ip`


##### 从多个压缩文件解压

`cat lotus-make.* | tar xzvf -`



### Disable Swap

`sudo swapon --show`
`sudo swapoff -v /swapfile`
`sudo vi /etc/fstab`   --remove /swapfile                                 none            swap    sw              0       0

`sudo rm /swapfile`

### Verify the MAC address and product_uuid are unique for every node

Product ID: `sudo cat /sys/class/dmi/id/product_uuid`

## setup iptables
`modprobe br_netfilter` 
( Check command `lsmod | grep br_netfilter` )

`sudo vi /etc/sysctl.d/k8s.conf` with text below:\
`net.bridge.bridge-nf-call-ip6tables = 1` \
`net.bridge.bridge-nf-call-iptables = 1`

`sudo sysctl --system`

## K8s Runtime - docker

# Install Docker CE
## Set up the repository:
### Install packages to allow apt to use a repository over HTTPS
`sudo apt-get update`  
`sudo apt-get install -y 
  apt-transport-https ca-certificates curl software-properties-common gnupg2`

### Add Docker’s official GPG key
`curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -`

### Add Docker apt repository.
`sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) 
  stable" `

## Install Docker CE.
`sudo apt-get update && sudo apt-get install -y containerd.io=1.2.13-1 docker-ce=5:19.03.8~3-0~ubuntu-$(lsb_release -cs) docker-ce-cli=5:19.03.8~3-0~ubuntu-$(lsb_release -cs)`

# Setup daemon.
`sudo vi /etc/docker/daemon.json` with content below: 
`
{\
  "exec-opts": ["native.cgroupdriver=systemd"],\
  "log-driver": "json-file",\
  "log-opts": {\
    "max-size": "100m"\
  },\
  "storage-driver": "overlay2"\
}


`sudo mkdir -p /etc/systemd/system/docker.service.d`

# Restart docker.
`sudo systemctl daemon-reload` \
`sudo systemctl restart docker`


## Installing kubeadm, kubelet and kubectl
`sudo apt-get update && sudo apt-get install -y apt-transport-https curl`
`curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -`\
cat <<EOF | sudo tee /etc/apt/sources.list.d/kubernetes.list\
deb https://apt.kubernetes.io/ kubernetes-xenial main\
EOF

`sudo apt-get update`
`sudo apt-get install -y kubelet kubeadm kubectl`
`sudo apt-mark hold kubelet kubeadm kubectl
`

## Configure cgroup driver used by kubelet on control-plane node
