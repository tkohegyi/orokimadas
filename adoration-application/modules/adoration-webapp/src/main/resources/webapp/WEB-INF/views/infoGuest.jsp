<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<meta name="apple-mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/sendMessage.js"></script>
<script src="/resources/js/infoGuest.js"></script>
<title>Örökimádás - Vác - Személyes Információk</title>
<link href="/resources/css/external/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
<div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	<div class="centerwidediv centerDiv">
	    <br/>
        <legend class="message-legend h4"d>Információk</legend>
        	<div class="centerwidediv centerDiv">
                <div id="noGoogle"></div>
                <div id="yesGoogle"><span style="font-weight:bold">Az Önhöz rendelt Google fiók adatai:</span>
                			<div id="nameGoogle">...</div>
                			<div id="emailGoogle">...</div>
                </div>
        	</div>
            <div class="centerwidediv centerDiv">
                <div id="noFacebook"></div>
                <div id="yesFacebook"><span style="font-weight:bold">Az Önhöz rendelt Facebook fiók adatai:</span>
                			<div id="nameFacebook">...</div>
                			<div id="emailFacebook">...</div>
                </div>
            </div>
            <div id="socialServiceUsed">...</div>
            <div id="status">...</div>
	</div><br/><div/>
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4"d>Fő koordinátorok elérhetősége</legend>
        <div id="noLeadership">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesLeadership" role="presentation"><tbody></tbody></table></p>
	</div>

	<div class="centerwidediv centerDiv">
        <div class="container centerDiv" style="padding:5px"><button id="message-button" type="button" class="btn btn-primary" data-toggle="modal" data-target="#sendMessageModal" onclick="msgClick()">Üzenet küldése az általános koordinátornak...</button></div>
	</div>

    <%@include file="../include/sendMessage.html" %>

    <div class="form-horizontal" id="downloads">
        <div class="centerwidediv centerDiv">
        </div>
    </div>

    <hr />
    <%@include file="../include/commonAlert.html" %>

</body>
</html>
