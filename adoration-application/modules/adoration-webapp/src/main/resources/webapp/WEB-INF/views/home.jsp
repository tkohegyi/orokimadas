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
<title>Örökimádás - Vác</title>
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
        <img alt="Örökimádás" src="/resources/img/topimage3.jpg" style="width:100%; max-width:635px">
    </div>

    <fieldset class="form-horizontal" id="actualCoverage">
        <legend class="message-legend h4">Az órák aktuális fedettsége
            <span style="float: right">
                <button id="uniqueRegister" style="display:none; vertical-align: super" type="button" class="btn btn-secondary" data-toggle="modal" data-target="#oneTimeModal" onclick="registerOneTimeSetup()">Jelentkezés helyettesítésre...</button>
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
				<th scope="col">Jelmagyarázat:</th>
				<th scope="col"></th>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="veryBadCoverage textRight">2 - Piros&nbsp;</td>
				<td style="padding-left:4px">színűek azok az órák, amelyekben a jelentkezőkre leginkább szükség van. Ezért, ha teheted, jelentkezz a pirossal jelölt órák valamelyikére.</td>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="badCoverage textRight">1 - Sárga&nbsp;</td>
				<td style="padding-left:4px">színűek azok az órák, amelyekben az Örökimádás folyamatos, de nem megfelelően biztosított -
					ezekre az időpontokra is örömmel várunk még jelentkezőket.</td>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="goodCoverage textRight">Zöld&nbsp;</td>
				<td style="padding-left:4px">színűek azok az órák, amelyekben az Örökimádás folytonossága megfelelően biztosított. Természetesen ezekre az órákra is lehet még jelentkezni.</td>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="onlineAdorator textRight">Kék keret&nbsp;&nbsp;</td>
				<td style="padding-left:4px">-tel rendelkeznek azok az órák, amelyekben Online módon is biztosítva van az Örökimádás. Online adorálásra az ország azaz a világ bármely pontjáról lehet jelentkezni, illetve azon idősebb vagy beteg testvéreinknek is ezt a módot ajánljuk, akik személyesen nem tudják felkeresni a kápolnát.</td>
			</tr>
			<tr>
				<td style="vertical-align:middle;" class="lowPriorityColumn textRight">Szürke&nbsp;&nbsp;</td>
				<td style="padding-left:4px">színűek azok az órák, amelyekben a kápolnában ideiglenesen szünetel az Örökimádás. Ezeknél az óráknál csak Online adorálásra van lehetőség, így javasoljuk, hogy válassza azt.</td>
			</tr>
			<tr>
				<td style="vertical-align:middle;background-color:#60ABF3" class="textRight" >Kék&nbsp;&nbsp;</td>
				<td style="padding-left:4px">színűek azok az órák, amelyekben a kápolnában ideiglenesen szünetel az Örökimádás, de van bejegyzett Online adoráló, aki távolról végzi az Örökimádást.</td>
			</tr>
		</table>
		<br />
	</div>

    <div class="centerwidediv">
        <table aria-describedby="actualCoverage">
            <tr>
                <th scope="col">Jelentkezni lehet:</th>
                <th scope="col"></th>
            </tr>
            <tr>
                <td />
                <td>- Közvetlenül, ezen az oldalon: <a href="/adoration/adorRegistration" target="_self">Jelentkezés örökimádásra</a>.</td>
            </tr>
            <tr>
                <td />
                <td>- A kápolnában elhelyezett jelentkezési lapokon, kitöltés után azt bedobva a piarista rendház postaládájába.</td>
            </tr>
            <tr>
                <td />
                <td>- E-mailben erre a címre írva: <a href="mailto:prhvac@gmail.com">prhvac@gmail.com</a></td>
            </tr>
            <tr>
                <td />
                <td>- A következő telefonszámok egyikén: <em>30-524-8291, 70-375-4140</em>.</td>
            </tr>
        </table>
    </div>
    <div class="centerwidediv">
        <br />
        <table aria-describedby="actualCoverage">
            <tr>
                <th scope="col">Letölthető dokumentumok:</th>
                <th scope="col"></th>
            </tr>
            <tr>
                <td />
                <td>- <a href="/resources/img/BishopLetter-BeerM.pdf" target="new">dr. Beer Miklós püspök atya levele a regisztrált szentségimádókhoz</a></td>
            </tr>
            <tr>
                <td />
                <td>- <a href="/resources/img/AlapvetoSzabalyok.pdf" target="new">Alapvető szabályok szentségimádók számára</a></td>
            </tr>
            <tr>
                <td />
                <td>- <a href="/resources/img/AdatkezelesiSzabalyzat.pdf" target="new">Adatkezelési Szabályzat</a>.</td>
            </tr>
        </table>
    </div>
    <br />

    <hr />
    <a href="http://vacitemplom.piarista.hu/">Urgás</a> a Váci Szent Anna Piarista Templom oldalára.
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

  <%@include file="../include/commonAlert.html" %>
  <%@include file="../include/commonConfirm.html" %>

</body>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/coverage.js"></script>
<script src="/resources/js/home.js"></script>
</html>
