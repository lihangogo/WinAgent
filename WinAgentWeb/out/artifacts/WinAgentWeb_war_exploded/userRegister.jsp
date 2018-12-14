<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>会员注册</title>
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
                <ul>
                    <li class="col-xs-4 on">
                        <span class="num"><em class="f-r5"></em><i>1</i></span>
                        <span class="line_bg lbg-r"></span>
                        <p class="lbg-txt">填写账户信息</p>
                    </li>
                    <li class="col-xs-4">
                        <span class="num"><em class="f-r5"></em><i>2</i></span>
                        <span class="line_bg lbg-l"></span>
                        <span class="line_bg lbg-r"></span>
                        <p class="lbg-txt" >验证账户信息</p>
                    </li>
                    <li class="col-xs-4">
                        <span class="num"><em class="f-r5"></em><i>3</i></span>
                        <span class="line_bg lbg-l"></span>
                        <p class="lbg-txt">注册成功</p>
                    </li>
                </ul>
            </div>
            <div class="reg-box" id="verifyCheck" style="margin-top:20px;">
                <div class="part1">
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>用户名：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" maxlength="20" class="txt03 f-r3 required" tabindex="1" data-valid="isNonEmpty||between:3-20||isUname" data-error="用户名不能为空||用户名长度3-20位||只能输入中文、字母、数字、下划线，且以中文或字母开头" id="username" />
                            <span class="ie8 icon-close close hide"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus"><span>3-20位，中文、字母、数字、下划线的组合，以中文或字母开头</span></label>
                            <label class="focus valid"></label>
                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>昵称：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" maxlength="20" class="txt03 f-r3 required" tabindex="2" data-valid="isNonEmpty||between:3-20||isUname" data-error="用户名不能为空||用户名长度3-20位||只能输入中文、字母、数字、下划线，且以中文或字母开头" id="nickname" />
                            <span class="ie8 icon-close close hide"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus"><span>3-20位，中文、字母、数字、下划线的组合，以中文或字母开头</span></label>
                            <label class="focus valid"></label>
                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>手机号：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" class="txt03 f-r3 required" keycodes="tel" tabindex="3" data-valid="isNonEmpty||isPhone" data-error="手机号码不能为空||手机号码格式不正确" maxlength="11" id="call_phone" />
                            <span class="ie8 icon-close close hide"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus">请填写11位有效的手机号码</label>
                            <label class="focus valid"></label>
                        </div>
                    </div>

                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>电子邮箱：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" class="txt03 f-r3 required" tabindex="4" maxlength="25" id="email" name="email"
                                   data-valid="isNonEmpty||isEmail" data-error="邮箱地址不能为空||邮箱地址格式不对"/>
                            <span class="ie8 icon-close close hide"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus">请填写有效的电子邮箱地址</label>
                            <label class="focus valid"></label>
                        </div>
                    </div>

                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>密码：</span>
                        <div class="f-fl item-ifo">
                            <input type="password" id="password" maxlength="20" class="txt03 f-r3 required" tabindex="5" style="ime-mode:disabled;" onpaste="return  false" autocomplete="off" data-valid="isNonEmpty||between:6-20||level:2" data-error="密码不能为空||密码长度6-20位||该密码太简单，有被盗风险，建议字母+数字的组合" />
                            <span class="ie8 icon-close close hide" style="right:55px"></span>
                            <span class="showpwd" data-eye="password"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus">6-20位英文（区分大小写）、数字、字符的组合</label>
                            <label class="focus valid"></label>
                            <span class="clearfix"></span>
                            <label class="strength">
                                <span class="f-fl f-size12">安全程度：</span>
                                <b><i>弱</i><i>中</i><i>强</i></b>
                            </label>
                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>确认密码：</span>
                        <div class="f-fl item-ifo">
                            <input type="password" maxlength="20" class="txt03 f-r3 required" tabindex="6" style="ime-mode:disabled;" onpaste="return  false" autocomplete="off" data-valid="isNonEmpty||between:6-16||isRepeat:password" data-error="密码不能为空||密码长度6-16位||两次密码输入不一致" id="rePassword" />
                            <span class="ie8 icon-close close hide" style="right:55px"></span>
                            <span class="showpwd" data-eye="rePassword"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus">请再输入一遍上面的密码</label>
                            <label class="focus valid"></label>
                        </div>
                    </div>

                    <div class="item col-xs-12" >
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>验证码：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" style="width: 50%" maxlength="4" class="txt03 f-r3 f-fl required" tabindex="7" style="width:167px"
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
                    <div class="item col-xs-12" style="height:auto">
                        <span class="intelligent-label f-fl">&nbsp;</span>
                        <p class="f-size14 required"  data-valid="isChecked" data-error="请先同意条款">
                            <input type="checkbox" checked /><a href="javascript:showoutc();" class="f-ml5">我已阅读并同意条款</a>
                        </p>
                        <label class="focus valid"></label>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl">&nbsp;</span>
                        <div class="f-fl item-ifo">
                            <a href="javascript:;" class="btn btn-blue f-r3" id="btn_part1">下一步</a>
                        </div>
                    </div>
                </div>
                <div class="part2" style="display:none">
                    <div class="alert alert-info" style="width:700px">短信已发送至您手机，请输入短信中的验证码，确保您的手机号真实有效。<p style="color: red">ps:测试期间，验证码随便填6位数字即可！</p> </div>
                    <div class="item col-xs-12 f-mb10" style="height:auto">
                        <span class="intelligent-label f-fl">手机号：</span>
                        <div class="f-fl item-ifo c-blue">
                            xxxxxxxxxxx
                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>验证码：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" maxlength="6" id="verifyNo" class="txt03 f-r3 f-fl required" tabindex="4" style="width:167px" data-valid="isNonEmpty||isInt" data-error="验证码不能为空||请输入6位数字验证码" />
                            <span class="btn btn-gray f-r3 f-ml5 f-size13" id="time_box" disabled style="width:97px;display:none;">发送验证码</span>
                            <span class="btn btn-gray f-r3 f-ml5 f-size13" id="verifyYz" style="width:97px;">发送验证码</span>
                            <span class="ie8 icon-close close hide" style="right:130px"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus"><span>请查收手机短信，并填写短信中的验证码（此验证码3分钟内有效）</span></label>
                            <label class="focus valid"></label>
                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl">&nbsp;</span>
                        <div class="f-fl item-ifo">
                            <a href="javascript:;" class="btn btn-blue f-r3" id="btn_part2">注册</a>
                        </div>
                    </div>
                </div>
                <div class="part3" style="display:none">
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>身份：</span>
                        <div class="f-fl item-ifo">
                            <select class="txt03 f-r3" style="height: 200%" id="select_ident">
                                <option value="student" selected="selected">学生</option>
                                <option value="teacher">教职工</option>
                            </select>
                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>真实姓名：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" maxlength="10" class="txt03 f-r3 required" tabindex="1" data-valid="isNonEmpty||between:2-10||isZh" data-error="真实姓名不能为空||真实姓名长度2-10位||只能输入中文" id="real_name" />
                            <span class="ie8 icon-close close hide"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus">2-10位，中文真实姓名</label>
                            <label class="focus valid"></label>
                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl"><b class="ftx04">*</b>学号/工号：</span>
                        <div class="f-fl item-ifo">
                            <input type="text" class="txt03 f-r3 required" tabindex="2" data-valid="isNonEmpty||isStudentID" data-error="学号不能为空||学号格式不正确" maxlength="10" id="studentID" />
                            <span class="ie8 icon-close close hide"></span>
                            <label class="icon-sucessfill blank hide"></label>
                            <label class="focus">请填写10位有效的编号</label>
                            <label class="focus valid"></label>
                        </div>
                    </div>
                    <div class="item col-xs-12">
                        <span class="intelligent-label f-fl">&nbsp;</span>
                        <div class="f-fl item-ifo">
                            <a href="javascript:;" class="btn btn-blue f-r3" id="btn_part3">下一步</a>
                        </div>
                    </div>
                </div>
                <div class="part4 text-center" style="display:none">
                    <h3>
                        <p id="msg_reg_suc">注册中，请稍后...</p>
                    </h3>
                    <p class="c-666 f-mt30 f-mb50">页面将在 <strong id="times" class="f-size18">10</strong> 秒钟后，跳转到 <a href="index.jsp" class="c-blue">首页</a></p>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="m-sPopBg" style="z-index:998;"></div>
<div class="m-sPopCon regcon">
    <div class="m-sPopTitle"><strong>服务协议条款</strong><b id="sPopClose" class="m-sPopClose" onClick="closeClause()">×</b></div>
    <div class="apply_up_content">
        <pre class="f-r0">
        <h4><strong>1、尽量找出客户端的bug，别给整瘫了就行</strong></h4>
        <h4><strong>2、整瘫之前请先联系开发者，做好心理准备</strong></h4>
        <h4><strong>3、信息尽量真实，Club203保证不会用于他用</strong></h4>
        <h4><strong>4、本客户端使用的openvpn、shadowsocks等工具,
            <br>仅用于教学，未违反网络安全法之相关规定</strong></h4>
        <h4><strong>5、辛苦大家，祝各位幸福!</strong></h4>
        <strong></strong>
        <strong>同意以上服务条款，提交注册信息</strong>
        </pre>
    </div>
    <center><a class="btn btn-blue btn-lg f-size12 b-b0 b-l0 b-t0 b-r0 f-pl50 f-pr50 f-r3" href="javascript:closeClause();">已阅读并同意此条款</a></center>
</div>
<script>
    $(function(){
        var verifyCode = new GVerify("v_container");
        //第一页的确定按钮
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
            $(".part2").show();
            $(".step li").eq(1).addClass("on");
        });
        //第二页的确定按钮
        $("#btn_part2").click(function(){
            if(!verifyCheck._click()) return;
            $(".part2").hide();
            $(".part3").show();
        });
        //第三页的确定按钮
        $("#btn_part3").click(function(){
            if(!verifyCheck._click()) return;
            $(".part3").hide();
            $(".part4").show();
            $(".step li").eq(2).addClass("on");
            reg_upload();
            countdown({
                maxTime:10,
                ing:function(c){
                    $("#times").text(c);
                },
                after:function(){
                    window.location.href="index.jsp";
                }
            });
        });
    });
    function showoutc(){$(".m-sPopBg,.m-sPopCon").show();}
    function closeClause(){
        $(".m-sPopBg,.m-sPopCon").hide();
    }
    function reg_upload() {
        var userName=document.getElementById("username").value;
        var nickName=document.getElementById("nickname").value;
        var phone=document.getElementById("call_phone").value;
        var email=document.getElementById("email").value;
        var password=document.getElementById("password").value;
        var real_name=document.getElementById("real_name").value;
        var studentID=document.getElementById("studentID").value;
        var select_ident=document.getElementById("select_ident").value;
        $.post("${pageContext.request.contextPath}/UserServlet?op=register",{'userName':userName,
            'nickName':nickName,'phone':phone,'email':email,'password':password,
            'real_name':real_name,'studentID':studentID,'ident':select_ident},function (data) {
                if(data=="suc")
                    document.getElementById("msg_reg_suc").innerText="用户 "+
                        document.getElementById("username").value+" 已注册成功，开始使用吧！";
                else if(data=="noStudent")
                    document.getElementById("msg_reg_suc").innerText="实名制注册失败，请使用您的真实信息重新注册";
                else
                    document.getElementById("msg_reg_suc").innerText="未知错误，注册失败，请尝试更换用户名或联系管理员...";
        });
    }
</script>
</body>
</html>
