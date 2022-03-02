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
<title><ex:i18n messageId="adorRegistration.jsp.title"/></title>
<link href="/resources/css/external/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link href="/resources/css/coverageBar.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
<div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4"></legend>
        <fieldset id="suggestLogin" class="form-horizontal">
            <div class="alert alert-danger" role="alert">
                <legend class="message-legend" style="text-align:center; color:#E05050; font-family: Oswald"><ex:i18n messageId="adorRegistration.jsp.login1"/><a id="gLoginAnchor" class="login" href="/adoration/loginGoogle"><img src="./../resources/img/google_login.png" alt="Google"/></a><ex:i18n messageId="adorRegistration.jsp.login2"/><a id="fLoginAnchor" class="login" href="/adoration/loginFacebook"><img src="./../resources/img/facebook_login.png" alt="Facebook"/></a><ex:i18n messageId="adorRegistration.jsp.login3"/>
                </legend>
            </div>
            <div>
                <ex:i18n messageId="adorRegistration.jsp.yes"/>
            </div>
        </fieldset>
	</div>
    <div class="centerwidediv centerDiv">
        <p>
        <table class="jelentkezes" role="presentation">
            <tr>
                <td class="right"><ex:i18n messageId="adorRegistration.jsp.name"/>&nbsp;<span style="color:red">*</span></td>
                <td class="left"><input type="text" id="name" value="">
                <span id="nameError" class="error" style="display:none"><ex:i18n messageId="adorRegistration.jsp.nameError"/></span></td>
            </tr>
            <tr>
                <td class="right"><ex:i18n messageId="adorRegistration.jsp.email"/>&nbsp;<span style="color:red">*</span></td>
                <td class="left"><input type="text" id="email" value="">
                <span id="emailError" class="error" style="display:none"><ex:i18n messageId="adorRegistration.jsp.emailError"/></span></td>
            </tr>
            <tr>
                <td class="right"><ex:i18n messageId="adorRegistration.jsp.phone"/>&nbsp;<span style="color:red">*</span></td>
                <td class="left"><input type="text" id="mobile" value="">
                <span id="mobileError" class="error" style="display:none"><ex:i18n messageId="adorRegistration.jsp.phoneError"/></span></td>
            </tr>
        </table>
        </p>

        <span style="font-weight:bold"><ex:i18n messageId="adorRegistration.jsp.select"/></span><ex:i18n messageId="adorRegistration.jsp.selectHelp"/>
        <p/>
        <div class="control-group form-horizontal">
            <%@include file="../include/coverageBar.html" %>
            <%@include file="../include/coverageBarVertical.html" %>
        </div>

        <br /><ex:i18n messageId="adorRegistration.jsp.coverageExplanation"/><br /><ex:i18n messageId="adorRegistration.jsp.coverageExplanation2"/>
        <br /><ex:i18n messageId="adorRegistration.jsp.coverageExplanation3"/>
		<br />
        <p/>
        <table class="jelentkezes" role="presentation">
            <tr>
                <td class="right"><ex:i18n messageId="adorRegistration.jsp.selectedDay"/>&nbsp;<span style="color:red">*</span></td>
                <td class="left"><select id="daySelect">
                        <option id="daySelect-0" value="0"><ex:i18n messageId="coverageBar.html.day.0"/></option>
                        <option id="daySelect-1" value="1"><ex:i18n messageId="coverageBar.html.day.1"/></option>
                        <option id="daySelect-2" value="2"><ex:i18n messageId="coverageBar.html.day.2"/></option>
                        <option id="daySelect-3" value="3"><ex:i18n messageId="coverageBar.html.day.3"/></option>
                        <option id="daySelect-4" value="4"><ex:i18n messageId="coverageBar.html.day.4"/></option>
                        <option id="daySelect-5" value="5"><ex:i18n messageId="coverageBar.html.day.5"/></option>
                        <option id="daySelect-6" value="6"><ex:i18n messageId="coverageBar.html.day.6"/></option>
                </select></td>
            </tr>
            <tr>
                <td class="right"><ex:i18n messageId="adorRegistration.jsp.selectedHour"/>&nbsp;<span style="color:red">*</span></td>
                <td class="left"><select id="hourSelect">
                        <option id="hourSelect-0" value="0">0</option>
                        <option id="hourSelect-1" value="1">1</option>
                        <option id="hourSelect-2" value="2">2</option>
                        <option id="hourSelect-3" value="3">3</option>
                        <option id="hourSelect-4" value="4">4</option>
                        <option id="hourSelect-5" value="5">5</option>
                        <option id="hourSelect-6" value="6">6</option>
                        <option id="hourSelect-7" value="7">7</option>
                        <option id="hourSelect-8" value="8">8</option>
                        <option id="hourSelect-9" value="9">9</option>
                        <option id="hourSelect-10" value="10">10</option>
                        <option id="hourSelect-11" value="11">11</option>
                        <option id="hourSelect-12" value="12">12</option>
                        <option id="hourSelect-13" value="13">13</option>
                        <option id="hourSelect-14" value="14">14</option>
                        <option id="hourSelect-15" value="15">15</option>
                        <option id="hourSelect-16" value="16">16</option>
                        <option id="hourSelect-17" value="17">17</option>
                        <option id="hourSelect-18" value="18">18</option>
                        <option id="hourSelect-19" value="19">19</option>
                        <option id="hourSelect-20" value="20">20</option>
                        <option id="hourSelect-21" value="21">21</option>
                        <option id="hourSelect-22" value="22">22</option>
                        <option id="hourSelect-23" value="23">23</option>
                </select></td>
            </tr>
            <tr>
                <td class="right"><ex:i18n messageId="adorRegistration.jsp.selectedMethod"/>&nbsp;<span style="color:red">*</span></td>
                <td class="left"><select id="method">
                        <option id="method-1" value="1"><ex:i18n messageId="adorRegistration.jsp.methodA"/></option>
                        <option id="method-2" value="2"><ex:i18n messageId="adorRegistration.jsp.methodB"/></option>
                        <option id="method-3" value="3"><ex:i18n messageId="adorRegistration.jsp.methodC"/></option>
                </select></td>
            </tr>
            <tr>
                <td class="right"><ex:i18n messageId="adorRegistration.jsp.otherComment"/></td>
                <td class="left"><textarea id="comment" rows="5" cols="55"></textarea></td>
            </tr>
            <tr>
                <td class="right"><ex:i18n messageId="adorRegistration.jsp.volunteerOrganizer"/><span style="color:red">*</span></td>
                <td class="left"><select id="coordinate">
                        <option id="coordinate-yes" value="szervezo"><ex:i18n messageId="common.yes"/></option>
                        <option id="coordinate-no" value="nemszervezo" selected><ex:i18n messageId="common.no"/></option>
                </select></td>
            </tr
            <p height=5px />
            <tr>
                <td class="right"><ex:i18n messageId="adorRegistration.jsp.consent"/><span style="color:red">*</span></td>
                <td class="left">
                        <span id="dhcError" class="error" style="display:none"><ex:i18n messageId="adorRegistration.jsp.consentIssue"/></span>
                        <select class="wideselect" id="dhc">
                        <option id="consent-yes" value="consent-yes"><ex:i18n messageId="adorRegistration.jsp.consentYes"/></option>
                        <option id="consent-no" value="consent-no" selected><ex:i18n messageId="adorRegistration.jsp.consentNo"/></option>
                </select><br/><a href="/resources/img/AdatkezelesiSzabalyzat.pdf" target="new"><ex:i18n messageId="adorRegistration.jsp.dataProcessingPolicy"/>
                </td>
            </tr>
        </table>
        <br />
        <button type="button" class="btn btn-warning btn-sm" onclick="notRegisterClick()"><ex:i18n messageId="adorRegistration.jsp.registerNo"/></button>
        &nbsp;&nbsp;&nbsp;
        <button id="registerButton" type="button" class="btn btn-success btn-sm" onclick="doRegisterClick()"><ex:i18n messageId="adorRegistration.jsp.registerYes"/></button>
        <br />
        <p/>
        <hr />
        <style type="text/css">
            #adorationVersion {display:inline-block;}
            #sslSign {display:inline-block;}
        </style>
        <div id="sslSign" class="right">
            <script type="text/javascript"> //<![CDATA[
              var tlJsHost = ((window.location.protocol == "https:") ? "https://secure.trust-provider.com/" : "http://www.trustlogo.com/");
              document.write(unescape("%3Cscript src='" + tlJsHost + "trustlogo/javascript/trustlogo.js' type='text/javascript'%3E%3C/script%3E"));
            //]]></script>
            <script language="JavaScript" type="text/javascript">
              TrustLogo("https://www.positivessl.com/images/seals/positivessl_trust_seal_sm_124x32.png", "POSDV", "none");
            </script>
        </div>

    <%@include file="../include/commonAlert.html" %>
</div>
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/coverage.js"></script>
<script src="/resources/js/adorRegistration.js"></script>
</body>
</html>
