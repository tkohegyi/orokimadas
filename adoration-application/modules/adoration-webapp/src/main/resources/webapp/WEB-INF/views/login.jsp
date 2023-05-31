<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "ex" uri = "/WEB-INF/custom.tld"%>
<!DOCTYPE html>
<head>
<meta charset="UTF-8">
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes" />
<meta name="HandheldFriendly" content="true" />
<meta name="apple-mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<meta property="og:image" content="https://orokimadas.magyar.website/resources/img/topimage3.jpg"/>
<title><ex:i18n messageId="login.jsp.title"/></title>
<link href="/resources/css/external/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
    <div class="container">
        <%@include file="../include/navbar.html" %>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <fieldset class="form-horizontal">
            <legend class="message-legend" style="text-align:center; color:#E05050; padding: 0px; font-family: Oswald"><ex:i18n messageId="login.jsp.notLoggedIn"/></legend>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <div id="login-possibilities">
                <br />
                <p><span style="font-weight:bold"><ex:i18n messageId="login.jsp.firstLoginCase"/></span></p>
                <br /><ex:i18n messageId="login.jsp.loginText1"/><br />
                <ex:i18n messageId="login.jsp.loginText2"/><a id="gLoginAnchor" class="login" href="/adoration/loginGoogle"><img src="/resources/img/google_login.png" alt="Google logo"/></a><ex:i18n messageId="login.jsp.loginText3"/><br/>
                <ex:i18n messageId="login.jsp.loginText4"/><a id="fLoginAnchor" class="login" href="/adoration/loginFacebook"><img src="/resources/img/facebook_login.png" alt="Facebook logo"/></a><ex:i18n messageId="login.jsp.loginText5"/><br/>
                <br /></p>
            </div>
        </fieldset>
        <style type="text/css">
            #adorationVersion {display:inline-block;}
            #sslSign {display:inline-block;}
        </style>
        <hr />
        <div id="sslSign"><ex:i18n messageId="login.jsp.cookieWarning"/></div>
        <hr />
        <div id="adorationVersion"></div>
    </div>
    <script src="/resources/js/external/jquery-3.6.4.min.js"></script>
    <script src="/resources/js/external/bootstrap.min.js"></script>
    <script src="/resources/js/common.js"></script>
    <script src="/resources/js/login.js"></script>
</body>
</html>