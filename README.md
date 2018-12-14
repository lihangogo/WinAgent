# WinAgent 基于SD-WAN的客户端软件
## 代码分为如下四部分
* WinAgent客户端(主体)
* WinAgentWeb(辅助网站)
* AccessServer(接入虚机端)
* RemoteServer(远程配置虚机端)
## WinAgent客户端
```网络拓扑```
![jiben](https://github.com/lihangogo/WinAgent/raw/master/Images/topo.jpg)
* 设计ShadowSocks、HTTP全局、PAC、OpenVPN全局等多种方式，目前仅开通校内线路(OpenVPN全局)
## WinAgentWeb
1. 让更多人了解两栋山
2. 用于WinAgent安装包的下载、查看安装使用说明，账号的注册、登录、`免费充值`、站内反馈等
## AccessServer
1. 对用户的在线状态进行维护
2. 计费以及辅助登录。
## RemoteServer
1. 对WinAgent的各类配置文件、投诉二维码图片等进行远程配置更新，避免非代码问题情况下的客户端频繁安装更新


