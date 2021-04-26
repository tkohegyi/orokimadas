<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="hu">
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
<title>Örökimádás - Vác - Belépés</title>
<link href="/resources/css/external/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
    <div class="container">
        <%@include file="../include/navbar.html" %>
        <fieldset class="form-horizontal">
            <legend class="message-legend" style="text-align:center; color:#E05050; padding: 0px; font-family: Oswald">Ön nincs bejelentkezve, kérjük lépjen be (vagy regisztráljon), hogy további oldalakhoz is hozzáférjen.</legend>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <div id="login-possibilities">
                <br />
                <p><span style="font-weight:bold">Kérjük vegye figyelembe, hogy a legelső bejelentkezés után még eltelhet pár nap mire a végleges azonosítás megtörténik, és további információkhoz is hozzáférhet.</span></p>
                <br />A belépés többféleképpen is történhet:<br />
                Használhatja például a Google-t azonosításra: <a id="gLoginAnchor" class="login" href="/adoration/loginGoogle"><img src="/resources/img/google_login.png" alt="Google logo"/></a>, (kattintson a Google logora!)<br/>
                de beléphet Facebook fiókjával is: <a id="fLoginAnchor" class="login" href="/adoration/loginFacebook"><img src="/resources/img/facebook_login.png" alt="Facebook logo"/></a>, (kattintson a Facebook logora!)<br/>
                <br /></p>
            </div>
        </fieldset>
        <hr />
        <div class="right">
            <script type="text/javascript"> //<![CDATA[
              var tlJsHost = ((window.location.protocol == "https:") ? "https://secure.trust-provider.com/" : "http://www.trustlogo.com/");
              document.write(unescape("%3Cscript src='" + tlJsHost + "trustlogo/javascript/trustlogo.js' type='text/javascript'%3E%3C/script%3E"));
            //]]></script>
            <script language="JavaScript" type="text/javascript">
              TrustLogo("https://www.positivessl.com/images/seals/positivessl_trust_seal_sm_124x32.png", "POSDV", "none");
            </script>
        </div>
    </div>
    <script src="/resources/js/external/jquery-3.4.1.js"></script>
    <script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
    <script src="/resources/js/common.js"></script>
    <script src="/resources/js/login.js"></script>
</body>
</html>