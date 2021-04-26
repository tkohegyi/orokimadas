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
<title>Örökimádás - Vác - Sikeres Regisztráció</title>
<link href="/resources/css/external/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
<div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	<div class="centerwidediv centerDiv">
        <legend class="message-legend h4"d>Sikeres Regisztráció</legend>
		<p>
			Jelentkezését hamarosan feldolgozzuk, és a megadott telefonszámon, vagy e-mailen felkeressük Önt adategyeztetésre.
			<br /><br />
			Addig is, kérjük olvassa el dr. Beer Miklós püspök atya levelét <a href="/resources/img/BishopLetter-BeerM.pdf" target="new">itt</a>,
			a szentségimádóknak szóló <a href="/resources/img/AlapvetoSzabalyok.pdf" target="new2">tájékoztatót</a>,
			<br/>valamint nyugodtan - a vállalt órájában - kezdje meg a heti rendszeres szentségimádást.
			 <br /><br />
			 Néhány másodperc múlva átirányítjuk Önt az Örökimádás
		főoldalára. <br /> Amennyiben ez mégsem történne meg, kérjük folytassa
		az Örökimádás oldalán <a href="/adoration/" target="_self">erre a linkre kattintva</a>.
		</p>
	</div>
</div>
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/registrationSuccess.js"></script>
</body>
</html>
