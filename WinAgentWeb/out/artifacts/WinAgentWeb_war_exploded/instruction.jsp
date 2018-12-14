<%--
  Created by IntelliJ IDEA.
  User: 李瀚
  Date: 2018/11/21
  Time: 15:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>使用说明</title>
    <link rel="shortcut icon" href="Images/logo.png" type="image/x-icon">
    <link href="Css/bootstrap.min.css" rel="stylesheet">
    <link href="Css/gloab.css" rel="stylesheet">
    <link href="Css/index.css" rel="stylesheet">
    <script src="Scripts/jquery-1.11.1.min.js"></script>
    <script src="Scripts/register.js"></script>
    <script src="Scripts/gVerify.js"></script>
</head>
<body class="bgf4">
<div class="login-box f-mt10 f-pb50">
    <div class="main bgf">
        <div class="reg-box-pan display-inline">
            <div id="tag"></div>
            <a href="index.jsp"><img src="Images/logo.png"/></a>
            <h2 style="text-align: center">
                <strong>
                    WinAgent使用说明
                </strong>
            </h2><br><br>

            <br><br>
            <h3><strong>安装包</strong></h3>
            <br>
            <div style="margin-left: 30px">
                <h3>
                    1、现提供包含JDK和不含JDK两种版本的客户端安装包和适配版本的OpenVPN安装包<br><br>
                    2、如果PC中未安装JDK1.8版本及以上，可下载setup_jre.exe<strong style="color: red">(推荐使用)</strong>，否则下载setup_nojre.exe<br><br>
                    3、请在使用客户端前安装OpenVP-N
                    <br><br>
                    <strong style="color: red">百度网盘下载：&nbsp;</strong>
                    <a href="https://pan.baidu.com/s/1RgBF34w3MktlU7lr_d5XqA" target="_blank">去下载</a>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;提取密码：tjxo
                    <br><br>
                    <strong style="color: red">站内免流量下载：&nbsp;&nbsp;</strong>
                    <a href="${pageContext.request.contextPath}/UserServlet?op=downloadExe&res=setup_nojre.exe" style="color: #1ed17f">setup_nojre</a>
                    &nbsp;&nbsp;&nbsp;
                    <a href="${pageContext.request.contextPath}/UserServlet?op=downloadExe&res=setup_jre.exe" style="color: #1ed17f">setup_jre</a>
                    &nbsp;&nbsp;&nbsp;
                    <a href="${pageContext.request.contextPath}/UserServlet?op=downloadExe&res=openvpn-install-2.3.6-I601-x86_64.exe" style="color: #1ed17f">
                        openvp-n</a>
                </h3>
            </div>

            <br><br>
            <h3><strong>客户端说明</strong></h3>
            <br>
            <div style="margin-left: 30px">
                <h3>
                    1、使用客户端需提前安装OpenVPN，<a href="#openvpn_install" style="color: #1ed17f">可查看安装步骤</a>。
                    <br><br>
                    2、目前仅支持接入<strong style="color: red">校内接入线路(OpenVPN全局)</strong>线路.
                    <br><br>
                    3、出现连接异常的提示后，可等待客户端5秒左右自动重新连接.
                    <br><br>
                    4、积极反馈问题.
                </h3>
            </div>

            <br><br>
            <h3><strong>本站说明</strong></h3>
            <br>
            <div style="margin-left: 30px">
                <h3>
                    1、推荐使用Microsoft Edge浏览器.
                    <br><br>
                    2、注册时请放心填写个人信息，保证不会用作他用.
                    <br><br>
                    3、注册测试用户后,系统<strong style="color: red">赠送10000币</strong>,客户端收费标准为
                    <strong style="color: red">每分钟一个币</strong>，请注意及时充值哟.
                    <br><br>
                </h3>
            </div>
            <br>

            <br><br>
            <div id="openvpn_install"></div>
            <h3><strong>OpenVPN安装教程</strong></h3><br>
            <div style="margin-left: 30px">
                <h3 style="color: #1ed17f">Step1.</h3><br>
                <div align="center"><img src="Images/step1.png"></div><br><br>
                <h3 style="color: #1ed17f">Step2.</h3><br>
                <div align="center"><img src="Images/step2.png"></div><br><br>
                <h3 style="color: #1ed17f">Step3.&nbsp;&nbsp;默认配置即可</h3><br>
                <div align="center"><img src="Images/step3.png"></div><br><br>
                <h3 style="color: #1ed17f">Step4.&nbsp;&nbsp;安装路径可按个人习惯修改</h3><br>
                <div align="center"><img src="Images/step4.png"></div><br><br>
            </div>

            <br>
            <div align="center"><a href="#tag"><h3>回到顶部</h3></a></div><br>
        </div>
    </div>
</div>
</body>
</html>
