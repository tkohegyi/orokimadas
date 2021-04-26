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
<script src="/resources/js/information.js"></script>
<title>Örökimádás - Vác - Információk</title>
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
        <legend class="message-legend h4">Információk</legend>
			<div id="name">...</div>
			<div id="status">...</div>
			<div id="adoratorId">...</div><br/>
	</div>
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="allocatedHours">Vállalt órák</legend>
        <div id="noOfferedHours">Önnek nincs vállalt órája.</div>
		<p><table id="yesOfferedHours" role="presentation"><tbody></tbody></table></p>
	</div>
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="allocatedHours">Szentségimádók most</legend>
        <div id="noAdoratorNow">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesAdoratorNow" role="presentation"><tbody></tbody></table></p>
	</div>
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="adoratorsNextHour">Szentségimádók a következő órában</legend>
        <div id="noAdoratorNext">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesAdoratorNext" role="presentation"><tbody></tbody></table></p>
	</div>
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="dailyCoordinators">Napszak koordinátorok elérhetősége</legend>
        <div class="centerwidediv centerDiv">
            <div class="container centerDiv" style="padding:5px"><button id="message-button" type="button" class="btn btn-primary" data-toggle="modal" data-target="#sendMessageModal" onclick="msgClick()">Üzenet küldése az általános koordinátornak...</button></div>
        </div>
        <%@include file="../include/sendMessage.html" %>

        <div id="noLeadership">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesLeadership" role="presentation"><tbody></tbody></table></p>
	</div>

    <div class="centerwidediv centerDiv" id="downloads">
        <legend class="message-legend h4">Letöltések</legend>
        <a id="forDc" class="btn btn-primary" href="/adorationSecure/getExcelDailyInfo">Napszakok fedettségtáblázata</a>
        <a id="forHc" class="btn btn-primary" href="/adorationSecure/getExcelHourlyInfo">Órakoordinátor információ letöltése</a>
        <a id="forStdA" class="btn btn-primary" href="/adorationSecure/getExcelAdoratorInfo">Saját adatok letöltése</a>
        <p/>
    </div>

	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="hourlyCoordinators">Óra koordinátorok elérhetősége</legend>
        <div id="noSubLeadership">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesSubLeadership" role="presentation"><tbody></tbody></table></p>
	</div>

    <%@include file="../include/commonAlert.html" %>
    <%@include file="../include/commonConfirm.html" %>
</body>
</html>
