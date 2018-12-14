<%--
  Created by IntelliJ IDEA.
  User: 李瀚
  Date: 2018/11/13
  Time: 15:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>会员缴费</title>
    <link rel="shortcut icon" href="Images/logo.png" type="image/x-icon">
    <link href="Css/bootstrap.min.css" rel="stylesheet">
    <link href="Css/gloab.css" rel="stylesheet">
    <link href="Css/index.css" rel="stylesheet">
    <script src="Scripts/jquery-1.11.1.min.js"></script>
    <script src="Scripts/register.js"></script>
    <script src="Scripts/gVerify.js"></script>
</head>
<body class="bgf4" onload="init()">
<div class="login-box f-mt10 f-pb50">
    <div class="main bgf">
        <div class="reg-box-pan display-inline">
            <a href="index.jsp"><img src="Images/logo.png"/></a>
            <div class="step">
                <li class="col-xs-4">
                </li>
                <li class="col-xs-4 on">
                    <span class="num"><em class="f-r5"></em><i>1</i></span>
                    <p class="lbg-txt" style="font-size: 20px">缴费</p>
                </li>
                <li class="col-xs-4">
                </li>
            </div>
            <div class="reg-box" id="verifyCheck" style="margin-top:20px;">
                <div class="part1">
                    <div class="item col-xs-12">
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>余额：</span>
                        <div class="f-fl item-ifo">
                            <input readonly="readonly" type="text" maxlength="20" class="txt03 f-r3 required" tabindex="1"
                                   data-valid="isNonEmpty" data-error="a121" id="nowMoney" />
                            <span class="ie8 icon-close close hide"></span>
                            <label class="icon-sucessfill blank hide"></label>

                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>充值：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" id="addMoney" maxlength="10" class="txt03 f-r3 required" tabindex="2" style="ime-mode:disabled;" onpaste="return  false" autocomplete="off"
                                   data-valid="isNonEmpty||isInt||between1:2000-5000" data-error="充值数不能为空||充值数须为整数||充值数未在规定范围内" />
                            <span class="ie8 icon-close close hide" style="right:55px"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus">2000-5000之间的整数</label>
                            <label class="focus valid"></label>
                            <span class="clearfix"></span>
                        </div>
                    </div>

                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl">&nbsp;</span>
                        <div class="f-fl item-ifo">
                            <a href="javascript:;" class="btn btn-blue f-r3" id="btn_part1">确定</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $(function(){
        $("#btn_part1").click(function() {
            if (!verifyCheck._click())
                return;
            pay_upload();
        });
    });
    function pay_upload() {
        var addMoney=document.getElementById("addMoney").value;
        var nowMoney=document.getElementById("nowMoney").value;
        $.post("${pageContext.request.contextPath}/UserServlet?op=pay",
            {'addMoney':addMoney},
            function (data) {
            if(data=="suc"){
                alert("充值成功");
                document.getElementById("nowMoney").value=parseInt(addMoney)+parseInt(nowMoney);
            }else if(data=="enough"){
                alert("当前余额充足，无需充值");
            }else{
                alert("充值失败，请尝试重新充值或联系管理员...");
            }
        });
    };
    function init() {
        document.getElementById("nowMoney").value=${account.balance};
    }
</script>
</body>
</html>
