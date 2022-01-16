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
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/sendMessage.js"></script>
<script src="/resources/js/information.js"></script>
<title><ex:i18n messageId="information.jsp.title"/></title>
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
        <legend class="message-legend h4"><ex:i18n messageId="guestinfo.jsp.infoTitle"/></legend>
			<div id="name">...</div>
			<div id="status">...</div>
			<div id="adoratorId">...</div><br/>
	</div>
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="allocatedHours"><ex:i18n messageId="information.jsp.hours"/></legend>
        <div id="noOfferedHours"><ex:i18n messageId="information.jsp.noHours"/></div>
		<p><table id="yesOfferedHours" role="presentation"><tbody></tbody></table></p>
	</div>
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="allocatedHours"><ex:i18n messageId="information.jsp.adorators"/></legend>
        <div id="noAdoratorNow"><ex:i18n messageId="information.jsp.noData"/></div>
		<p><table id="yesAdoratorNow" role="presentation"><tbody></tbody></table></p>
	</div>
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="adoratorsNextHour"><ex:i18n messageId="information.jsp.nextAdorators"/></legend>
        <div id="noAdoratorNext"><ex:i18n messageId="information.jsp.noData"/></div>
		<p><table id="yesAdoratorNext" role="presentation"><tbody></tbody></table></p>
	</div>
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="dailyCoordinators"><ex:i18n messageId="information.jsp.coordinators"/></legend>
        <div class="centerwidediv centerDiv">
            <div class="container centerDiv" style="padding:5px"><button id="message-button" type="button" class="btn btn-primary" data-toggle="modal" data-target="#sendMessageModal" onclick="msgClick()"><ex:i18n messageId="guestinfo.jsp.msgInfo"/></button></div>
        </div>
        <%@include file="../include/sendMessage.html" %>

        <div id="noLeadership"><ex:i18n messageId="information.jsp.noData"/></div>
		<p><table id="yesLeadership" role="presentation"><tbody></tbody></table></p>
	</div>

    <div class="centerwidediv centerDiv" id="downloads">
        <legend class="message-legend h4"><ex:i18n messageId="information.jsp.downloads"/></legend>
        <a id="forDc" class="btn btn-primary" href="/adorationSecure/getExcelDailyInfo"><ex:i18n messageId="information.jsp.getExcelDailyInfo"/></a>
        <a id="forHc" class="btn btn-primary" href="/adorationSecure/getExcelHourlyInfo"><ex:i18n messageId="information.jsp.getExcelHourlyInfo"/></a>
        <a id="forStdA" class="btn btn-primary" href="/adorationSecure/getExcelAdoratorInfo"><ex:i18n messageId="information.jsp.getExcelAdoratorInfo"/></a>
        <p/>
    </div>

	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4" id="hourlyCoordinators"><ex:i18n messageId="information.jsp.hourlyCoordinators"/></legend>
        <div id="noSubLeadership"><ex:i18n messageId="information.jsp.noData"/></div>
		<p><table id="yesSubLeadership" role="presentation"><tbody></tbody></table></p>
	</div>

    <%@include file="../include/commonAlert.html" %>
    <%@include file="../include/commonConfirm.html" %>
</body>
</html>
