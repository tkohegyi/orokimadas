<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "ex" uri = "/WEB-INF/custom.tld"%>
<!DOCTYPE html>
<html lang="hu">
<head>
<meta charset="UTF-8">
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes" />
<meta name="HandheldFriendly" content="true" />
<meta name="mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<script src="/resources/js/external/jquery-3.6.4.min.js"></script>
<script src="/resources/js/external/bootstrap.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/applog.js" nonce></script>
<title>Örökimádás - Vác - Adminisztráció</title>
<link href="/resources/css/external/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

  </div>
  <div class="container">
    <fieldset class="form-horizontal">
    <legend class="message-legend">Válassz az alábbi lehetőségek közül...</legend>
    <div class="control-group">
    <a id="people-button" class="btn btn-primary" href="/adorationSecure/adorators">Adorálók</a>
    <a id="link-button" class="btn btn-primary" href="/adorationSecure/links">Órák</a>
    <a id="social-button" class="btn btn-primary" href="/adorationSecure/social">Azonosítások</a>
    <a id="coordinator-button" class="btn btn-primary" href="/adorationSecure/coordinators">Koordinátorok</a>
    <a id="audit-button" class="btn btn-primary" href="/adorationSecure/audit">Audit Log</a>
    <a id="translator-button" class="btn btn-primary" href="/adorationSecure/getExcelFull">Export to Excel</a>
    </div>
    </fieldset>

    <div id="logArea">
    <fieldset class="form-horizontal">
        <legend>Logok</legend>
        <div class="control-group">
            <span class="help-block">Válassz az alább elérhető log file-ok közül.</span>
        </div>
        <div class="control-group">
            <ol id="div-log-files"></ol>
        </div>
    </fieldset>
    </div>
  </div>
</body>
</html>
