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
<meta property="og:image" content="https://orokimadas.info:9092/resources/img/topimage3.jpg"/>
<meta property="og:url" content="https://orokimadas.info:9092/adoration/"/>
<meta property="og:type" content="website"/>
<meta property="og:title" content="Perpetual Adoration - Vác, Hungary"/>
<meta property="og:description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban"/>
<meta property="fb:app_id" content="1696487070576647"/>
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<title><ex:i18n messageId="home.jsp.title"/></title>
<link href="/resources/css/external/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link href="/resources/css/coverageBar.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png">
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

    <div class="centerwidediv" style="text-align: center">
        <br />
        <img alt="<ex:i18n messageId="home.jsp.altImage"/>" src="/resources/img/topimage3.jpg" style="width:100%; max-width:635px">
    </div>

    <fieldset class="form-horizontal" id="actualCoverage">
        <legend class="message-legend h4"><ex:i18n messageId="home.jsp.actualCoverage.legend"/>
            <span style="float: right">
                <button id="uniqueRegister" style="display:none; vertical-align: super" type="button" class="btn btn-secondary" data-toggle="modal" data-target="#oneTimeModal" onclick="registerOneTimeSetup()"><ex:i18n messageId="home.jsp.uniqueRegister"/></button>
            </span>
        </legend>
        <div class="control-group">
            <%@include file="../include/coverageBar.html" %>
            <%@include file="../include/coverageBarVertical.html" %>
        </div>
    </fieldset>

    <div class="centerwidediv">
		<br />
		<table aria-describedby="actualCoverage">
			<tr>
				<th scope="col"><ex:i18n messageId="home.jsp.explanation"/></th>
				<th scope="col"></th>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="veryBadCoverage textRight"><ex:i18n messageId="home.jsp.explanation.two"/></td>
				<td style="padding-left:4px"><ex:i18n messageId="home.jsp.explanation.two.text"/></td>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="badCoverage textRight"><ex:i18n messageId="home.jsp.explanation.one"/></td>
				<td style="padding-left:4px"><ex:i18n messageId="home.jsp.explanation.one.text"/></td>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="goodCoverage textRight"><ex:i18n messageId="home.jsp.explanation.green"/></td>
				<td style="padding-left:4px"><ex:i18n messageId="home.jsp.explanation.green.text"/></td>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="onlineAdorator textRight"><ex:i18n messageId="home.jsp.explanation.blue.frame"/></td>
				<td style="padding-left:4px"><ex:i18n messageId="home.jsp.explanation.blue.frame.text"/></td>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="lowPriorityColumn textRight"><ex:i18n messageId="home.jsp.explanation.gray"/></td>
				<td style="padding-left:4px"><ex:i18n messageId="home.jsp.explanation.gray.text"/></td>
			</tr>
			<tr>
				<td style="vertical-align:middle;background-color:#60ABF3" class="textRight" ><ex:i18n messageId="home.jsp.explanation.blue"/></td>
				<td style="padding-left:4px"><ex:i18n messageId="home.jsp.explanation.blue.text"/></td>
			</tr>
		</table>
		<br />
	</div>

    <div class="centerwidediv">
        <table aria-describedby="actualCoverage">
            <tr>
                <th scope="col"><ex:i18n messageId="home.jsp.application.intro"/></th>
                <th scope="col"></th>
            </tr>
            <tr>
                <td />
                <td><ex:i18n messageId="home.jsp.application.method1"/></td>
            </tr>
            <tr>
                <td />
                <td><ex:i18n messageId="home.jsp.application.method2"/></td>
            </tr>
            <tr>
                <td />
                <td><ex:i18n messageId="home.jsp.application.method3"/></td>
            </tr>
            <tr>
                <td />
                <td><ex:i18n messageId="home.jsp.application.method4"/></td>
            </tr>
        </table>
    </div>
    <div class="centerwidediv">
        <br />
        <table aria-describedby="actualCoverage">
            <tr>
                <th scope="col"><ex:i18n messageId="home.jsp.documents.downloadable"/></th>
                <th scope="col"></th>
            </tr>
            <tr>
                <td />
                <td><ex:i18n messageId="home.jsp.documents.downloadable.mail"/></td>
            </tr>
            <tr>
                <td />
                <td><ex:i18n messageId="home.jsp.documents.downloadable.rules"/></td>
            </tr>
            <tr>
                <td />
                <td><ex:i18n messageId="home.jsp.documents.downloadable.data"/></td>
            </tr>
        </table>
    </div>
    <br />
    <hr />
    <style type="text/css">
        #adorationVersion {display:inline-block;}
        #sslSign {display:inline-block;}
    </style>
    <div id="adorationVersion"></div>
    <div id="sslSign" class="right">
        <script type="text/javascript"> //<![CDATA[
          var tlJsHost = ((window.location.protocol == "https:") ? "https://secure.trust-provider.com/" : "http://www.trustlogo.com/");
          document.write(unescape("%3Cscript src='" + tlJsHost + "trustlogo/javascript/trustlogo.js' type='text/javascript'%3E%3C/script%3E"));
        //]]></script>
        <script language="JavaScript" type="text/javascript">
          TrustLogo("https://www.positivessl.com/images/seals/positivessl_trust_seal_sm_124x32.png", "POSDV", "none");
        </script>
    </div>
  </div>

  <%@include file="../include/commonAlert.html" %>
  <%@include file="../include/commonConfirm.html" %>

</body>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/coverage.js"></script>
<script src="/resources/js/home.js"></script>
</html>
