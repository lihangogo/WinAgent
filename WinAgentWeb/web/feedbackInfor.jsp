<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: club203LH
  Date: 2018/11/26
  Time: 18:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>吐槽</title>
    <link rel="shortcut icon" href="Images/logo.png" type="image/x-icon">
    <link href="Css/bootstrap.min.css" rel="stylesheet">
    <link href="Css/gloab.css" rel="stylesheet">
    <link href="Css/index.css" rel="stylesheet">
    <script src="Scripts/jquery-1.11.1.min.js"></script>
    <script src="Scripts/register.js"></script>
    <script src="Scripts/gVerify.js"></script>
</head>
<body onload="check()" class="bgf4">
<div class="login-box f-mt10 f-pb50">
    <div class="main bgf">
        <div class="reg-box-pan display-inline">
            <a href="index.jsp"><img src="Images/logo.png"/></a>
        </div><br><br>
        <table border="1" align="center" style="border-color: #1a97d7;width: 80%;">
            <caption style="text-align: center"><h3><strong>问题反馈</strong></h3></caption><br>

            <tr>
                <th style="width: 15%;text-align: center">用户编号</th>
                <th style="width: 15%;text-align: center">问题类别</th>
                <th style="width: 47%;text-align: center">内容</th>
                <th style="width: 23%;text-align: center">时间</th>
            </tr>
            <c:forEach items="${feedbackList}" var="feedback" varStatus="idx">
                <tr>
                    <td style="text-align: center">${feedback.uid}</td>
                    <td style="text-align: center">${feedback.category}</td>
                    <td style="text-align: center">${feedback.content}</td>
                    <td style="text-align: center">${feedback.time}</td>
                </tr>
            </c:forEach>
        </table>
        <br><br><br><br><br><br><br><br>
    </div>
</div>
</body>
<script>
    function check() {
        var v=${empty admin};
        if(v==true)
            window.location.href="${pageContext.request.contextPath}/index.jsp";
    }
</script>
</html>
