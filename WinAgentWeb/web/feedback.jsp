<%--
  Created by IntelliJ IDEA.
  User: 李瀚
  Date: 2018/11/24
  Time: 13:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>反馈问题</title>
    <link rel="shortcut icon" href="Images/logo.png" type="image/x-icon">
    <link href="Css/bootstrap.min.css" rel="stylesheet">
    <link href="Css/gloab.css" rel="stylesheet">
    <link href="Css/index.css" rel="stylesheet">
    <link href="Css/feedback.css" rel="stylesheet">
    <script src="Scripts/jquery-1.11.1.min.js"></script>
    <script src="Scripts/register.js"></script>
    <script src="Scripts/gVerify.js"></script>
</head>
<body class="bgf4">
<div class="login-box f-mt10 f-pb50">
    <div class="main bgf">
        <div class="reg-box-pan display-inline">
            <a href="index.jsp"><img src="Images/logo.png"/></a>
        </div>
        <div class="Content-Main">
            <form action="" method="post" class="form-report">
                <label>
                <span>分类</span>
                <select id="selection" name="select1" class="select1">
                    <option value="client" selected="selected">客户端问题</option>
                    <option value="web">WEB问题</option>
                    <option value="other">其他</option>
                </select>
                </label>
                <label>
                    <span>问题:</span>
                    <textarea id="message" name="message" placeholder="请尽量将问题描述清楚"></textarea>
                </label>
                <label>
                    <input type="button" class="button" value="提交" onclick="upload()">
                </label>
            </form>
        </div>
        <br><br><br><br><br><br>
    </div>
</div>
<script>
    function upload() {
        var category=document.getElementById("selection").value;
        var content=document.getElementById("message").value;
        $.post(
            "${pageContext.request.contextPath}/UserServlet?op=addFeedback",
            {'category':category,'content':content},
            function (data) {
                if(data=='suc')
                    alert('提交成功');
                else if(data=='loginFirst'){
                    alert('请先登录');
                    window.location.href="userLogin.jsp";
                } else
                    alert('反馈失败');
            }
        );
    }
</script>
</body>
</html>
