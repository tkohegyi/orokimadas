<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "ex" uri = "/WEB-INF/custom.tld"%>
<!DOCTYPE html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes" />
<meta name="HandheldFriendly" content="true" />
<meta name="apple-mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<title><ex:i18n messageId="registrationSuccess.jsp.title"/></title>
<link href="/resources/css/external/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
<div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4"d><ex:i18n messageId="registrationSuccess.jsp.success"/></legend>
		<p>
			<ex:i18n messageId="registrationSuccess.jsp.successText1"/><br /><br />
			<ex:i18n messageId="registrationSuccess.jsp.successText2"/>
			<a href="/resources/img/BishopLetter-BeerM.pdf" target="new"><ex:i18n messageId="registrationSuccess.jsp.successText3"/></a>
			<ex:i18n messageId="registrationSuccess.jsp.successText4"/><a href="/resources/img/AlapvetoSzabalyok.pdf" target="new2"><ex:i18n messageId="registrationSuccess.jsp.successText5"/></a>
			<br/><ex:i18n messageId="registrationSuccess.jsp.successText6"/><br /><br />
			<ex:i18n messageId="registrationSuccess.jsp.successText7"/><br /><ex:i18n messageId="registrationSuccess.jsp.successText8"/>
			<a href="/adoration/" target="_self"><ex:i18n messageId="registrationSuccess.jsp.successText9"/></a>.
		</p>
	</div>
</div>
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/registrationSuccess.js"></script>
</body>
</html>
