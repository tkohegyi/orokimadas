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
<meta name="apple-mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="/resources/js/external/dataTables/datatables.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/adoratorListHours.js"></script>
<title>Örökimádás - Vác - Adorátorok által vállalt Órák listája</title>
<link href="/resources/css/external/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/js/external/dataTables/datatables.min.css" rel="stylesheet" type="text/css"/>
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </div>
  <div class="container">
    <fieldset id="linkList" class="form-horizontal">
        <legend>Vállalt Órák Listája</legend>
        <div class="control-group">
            <table id="link" class="table table-striped table-bordered table-hover compact cell-border" style="width:100%" aria-describedby="linkList">
                    <thead>
                        <tr>
                            <th scope="col">Nap</th>
                            <th scope="col">Óra</th>
                            <th scope="col">Adoráló Név</th>
                            <th scope="col">Adoráló Telefonszám</th>
                            <th scope="col">Adoráló E-mail</th>
                            <th scope="col">Prioritás</th>
                            <th scope="col">Hely</th>
                            <th scope="col">Megjegyzés</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th scope="col">Nap</th>
                            <th scope="col">Óra</th>
                            <th scope="col">Adoráló Név</th>
                            <th scope="col">Adoráló Telefonszám</th>
                            <th scope="col">Adoráló E-mail</th>
                            <th scope="col">Prioritás</th>
                            <th scope="col">Hely</th>
                            <th scope="col">Megjegyzés</th>
                        </tr>
                    </tfoot>
            </table>
        </div>
    </fieldset>
  </div>
</body>
</html>
