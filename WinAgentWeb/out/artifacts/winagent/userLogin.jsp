<%--
  Created by IntelliJ IDEA.
  User: 李瀚
  Date: 2018/11/12
  Time: 19:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>会员登录</title>
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
            <a href="index.jsp"><img src="Images/logo.png"/></a>
            <div class="step">
                <li class="col-xs-4">
                </li>
                <li class="col-xs-4 on">
                    <span class="num"><em class="f-r5"></em><i>1</i></span>
                    <p class="lbg-txt" style="font-size: 20px">用户登录</p>
                </li>
                <li class="col-xs-4">
                </li>
            </div>
            <div class="reg-box" id="verifyCheck" style="margin-top:20px;">
                <div class="part1">
                    <div class="item col-xs-12">
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>用户名：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" maxlength="20" class="txt03 f-r3 required" tabindex="1" data-valid="isNonEmpty" data-error="用户名不能为空" id="username" />
                            <span class="ie8 icon-close close hide"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus"><span>3-20位，中文、字母、数字、下划线的组合，以中文或字母开头</span></label>
                            <label class="focus valid"></label>
                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>密码：</span>
                        <div class="f-fl item-ifo">
                            <input type="password" id="password" maxlength="20" class="txt03 f-r3 required" tabindex="2" style="ime-mode:disabled;" onpaste="return  false" autocomplete="off" data-valid="isNonEmpty" data-error="密码不能为空" />
                            <span class="ie8 icon-close close hide" style="right:55px"></span>
                            <span class="showpwd" data-eye="password"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus">6-20位英文（区分大小写）、数字、字符的组合</label>
                            <label class="focus valid"></label>
                            <span class="clearfix"></span>
                        </div>
                    </div>
                    <div class="item col-xs-12" >
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>验证码：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" style="width: 50%" maxlength="4" class="txt03 f-r3 f-fl required" tabindex="3" style="width:167px"
                                   id="randCode" data-valid="isNonEmpty" data-error="验证码不能为空"  />
                            <span class="ie8 icon-close close hide"></span>
                            <label class="f-size12 c-999 f-fl f-pl10">
                                <div id="v_container" style="width: 120px;height: 37px;">
                                </div>
                            </label>
                            <label class="icon-sucessfill blank hide" style="left:380px"></label>
                            <label class="focusa">点击图片换一张</label>
                            <label id="validCode" class="focus valid" style="left:370px"></label>
                        </div>
                    </div>

                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl">&nbsp;</span>
                        <div class="f-fl item-ifo">
                            <a href="javascript:;" class="btn btn-blue f-r3" id="btn_part1">提交</a>
                        </div>
                    </div>
                </div>
                <div class="part4 text-center" style="display:none">
                    <h3>
                        <p id="msg_login_suc">登录中，请稍候...</p>
                    </h3>
                    <p class="c-666 f-mt30 f-mb50">页面将在 <strong id="times" class="f-size18">3</strong> 秒钟后，跳转到 <a href="index.jsp" class="c-blue">首页</a></p>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $(function(){
        var verifyCode = new GVerify("v_container");
        //确定登录按钮
        $("#btn_part1").click(function(){
            var code=document.getElementById("randCode").value;
            if(code!=""&&code!=null){
                if(!verifyCode.validate(code)){
                    document.getElementById("randCode").style.border="1px solid red";
                    document.getElementById("validCode").innerHTML="验证码错误"
                    return;
                }else{
                    document.getElementById("randCode").style.border="";
                }
            }
            if(!verifyCheck._click())
                return;
            $(".part1").hide();
            $(".part4").show();
            login_upload();
            countdown({
                maxTime:3,
                ing:function(c){
                    $("#times").text(c);
                },
                after:function(){
                    if(document.getElementById("msg_login_suc").innerText=="登录失败，请尝试重新登录或联系管理员...")
                        window.location.href="userLogin.jsp";
                    else
                        window.location.href="index.jsp";
                }
            });

        });
    });
    function login_upload() {
        var userName=document.getElementById("username").value;
        var password=document.getElementById("password").value;
        $.post("${pageContext.request.contextPath}/UserServlet?op=login",{'userName':userName,
            'password':password},function (data) {
            if(data=="suc")
                document.getElementById("msg_login_suc").innerText="用户 "+
                    document.getElementById("username").value+" 已登录成功，开始使用您的会员权限吧！";
            else
                document.getElementById("msg_login_suc").innerText="登录失败，请尝试重新登录或联系管理员...";
        });
    }
</script>

</body>
</html>