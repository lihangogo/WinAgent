<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!-- 使用JSTL的标准标签 -->
<!DOCTYPE HTML>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
	<head>
		<title>WeFaster</title>
		<link rel="shortcut icon" href="Images/logo.png" type="image/x-icon">
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<!--[if lte IE 8]><script src="assets/js/ie/html5shiv.js"></script><![endif]-->
		<link rel="stylesheet" href="assets/css/main.css" />
		<!--[if lte IE 8]><link rel="stylesheet" href="assets/css/ie8.css" /><![endif]-->
		<!--[if lte IE 9]><link rel="stylesheet" href="assets/css/ie9.css" /><![endif]-->
		<script src="assets/js/jquery.min.js"></script>
		<script src="assets/js/jquery.dropotron.min.js"></script>
		<script src="assets/js/jquery.scrolly.min.js"></script>
		<script src="assets/js/jquery.scrollgress.min.js"></script>
		<script src="assets/js/skel.min.js"></script>
		<script src="assets/js/util.js"></script>
		<!--[if lte IE 8]><script src="assets/js/ie/respond.min.js"></script><![endif]-->
		<script src="assets/js/main.js"></script>
	</head>
	<body class="index">
		<div id="page-wrapper">

			<!-- Header -->
				<header id="header" class="alt">
					<h1 id="logo"><a href="${pageContext.request.contextPath}/index.jsp">Work&nbsp;&nbsp;&nbsp;
						<span>by Cookie</span></a></h1>
					<nav id="nav">
						<ul>
							<li class="current">
								<a href="index.jsp">欢迎</a>
							</li>
							<li class="submenu">
								<a href="#">其他业务</a>
								<ul>
									<li>
										<a href="${pageContext.request.contextPath}/feedback.jsp">反馈问题</a>
									</li>
									<li>
										<c:if test="${!empty admin}">
											<a href="${pageContext.request.contextPath}/UserServlet?op=feedbackInfor">查看反馈信息</a>
										</c:if>
									</li>
									<li>
										<a href="#">敬请期待</a>
									</li>
								</ul>
							</li>
							<li>
								<c:if test="${empty user}">
									<a href="${pageContext.request.contextPath}/userLogin.jsp" class="button special">登录</a>
								</c:if>
								<c:if test="${!empty user}">
									${user.nickName}
									<a href="${pageContext.request.contextPath}/UserServlet?op=logout" class="button special">退出</a>
								</c:if>
							</li>
						</ul>
					</nav>
				</header>

			<!-- Banner -->
				<section id="banner">
					<div class="inner">
						<header>
							<h4>欢迎加入两栋山SDWAN测试俱乐部</h4>
							<h3>203CLUB</h3>
						</header>
						<p>This is <strong>WinAgent</strong>, a "free"
							<br/>internet software<br/>
							by&nbsp;<a href="#">Cookie</a>.
						</p>
						<div>&nbsp;</div>
						<a href="${pageContext.request.contextPath}/userRegister.jsp"
						   class="button special">会员注册</a>
						&nbsp;&nbsp;
						<a href="${pageContext.request.contextPath}/UserServlet?op=payment"
						   class="button special">免费充值</a>
						&nbsp;&nbsp;
						<a href="${pageContext.request.contextPath}/instruction.jsp"
						   class="button special">下载及使用说明</a>
						<footer>
							<ul class="buttons vertical">
								<li><a href="#main" class="button fit scrolly">了解两栋山</a></li>
							</ul>
						</footer>
					</div>
				</section>
                <div class="copyrights">Collect from <a href="http://www.baidu.com/" >友情链接</a></div>

			<!-- Main -->
				<article id="main">

					<header class="special container">
						<h3><strong>力博睿生 --两栋山官方微信公众号</strong></h3>
						<img src="Images/wechat_logo.png"/>
					</header>

					<!-- Two -->
						<!--<section class="wrapper style1 container special">

						</section>
						-->
					<!-- Three -->
						<section class="wrapper style3 container special">
							<header class="major">
								<h2>About 2018 <strong>summer courses</strong></h2>
							</header>
							<div class="row">
								<div class="6u 12u(narrower)">
									<section>
										<a href="#" class="image featured"><img src="assets/pic/no1.png" alt="" /></a>
										<header>
											<h3>宁宇 </h3>
										</header>
										<p>从漫游费的存废谈运营商运营的过去与未来. </p>
										<p>Talking about the past and future of operator operation from the abolition of roaming charges.</p>
									</section>
								</div>
								<div class="6u 12u(narrower)">
									<section>
										<a href="#" class="image featured"><img src="assets/pic/no2.png" alt="" /></a>
										<header>
											<h3>苏远超 </h3>
										</header>
										<p>详解Segment Routing. 对SR的技术架构、设计理念以及关键技术和应用案例进行详细的剖析，在课堂上，苏远超老师延续了脱俗的演讲风格，时刻吸引同学们的眼球.</p>
										<p>Detailed Segment Routing. He detailed analysis of SR's technical architecture, design concepts, and key technologies and application cases. In the classroom, Su Yuanchao continued the refined style of speech and attracted the attention of the students.</p>
									</section>
								</div>
							</div>
							<div class="row">
								<div class="6u 12u(narrower)">
									<section>
										<a href="#" class="image featured"><img src="assets/pic/no3.png" alt="" /></a>
										<header>
											<h3>代码医生</h3>
										</header>
										<p>人工智能科普.</p>
										<p>Artificial intelligence science.</p>
									</section>
								</div>
								<div class="6u 12u(narrower)">
									<section>
										<a href="#" class="image featured"><img src="assets/pic/no4.png" alt="" /></a>
										<header>
											<h3>立普威陆 冯智慧</h3>
										</header>
										<p>IPV6的魔幻与现实.</p>
										<p>The magic and reality of IPV6</p>
									</section>
								</div>
							</div>
							<footer class="major">
								<ul class="buttons">
									<li><a href="#" class="button">See More</a></li>
								</ul>
							</footer>
						</section>
				</article>

			<!-- CTA -->
				<section id="cta">
					<header>
						<h2>Ready to <strong>use </strong>it?</h2>
						<p>Hurry up to sign up and become our user!</p>
					</header>
					<footer>
						<ul class="buttons">
							<li><label id="sponsorLI" class="button special" onclick="sponsorLIClick()">Sponsor</label></li>
							<li><label class="button">Not ready yet...</label></li>
						</ul>
					</footer>
				</section>

			<!-- Footer -->
				<footer id="footer">
					<ul class="icons">
						<li><a href="#" class="icon circle fa-twitter"><span class="label">Twitter</span></a></li>
						<li><a href="#" class="icon circle fa-facebook"><span class="label">Facebook</span></a></li>
						<li><a href="#" class="icon circle fa-google-plus"><span class="label">Google+</span></a></li>
						<li><a href="#" class="icon circle fa-github"><span class="label">Github</span></a></li>
						<li><a href="#" class="icon circle fa-dribbble"><span class="label">Dribbble</span></a></li>
					</ul>
					<ul class="copyright">
						<li>&copy;BUPT-Club203</li>
					</ul>
				</footer>
		</div>

	<script>
		function sponsorLIClick() {
			alert("Just a joke");
        }
	</script>
	</body>
</html>