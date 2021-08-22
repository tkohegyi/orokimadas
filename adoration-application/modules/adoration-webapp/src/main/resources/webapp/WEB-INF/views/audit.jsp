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
<script src="/resources/js/commonDataTable.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/audit.js" nonce></script>
<title>Örökimádás - Vác - Adminisztráció - Audit rekordok</title>
<link href="/resources/css/external/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/js/external/dataTables/datatables.min.css" rel="stylesheet" type="text/css"/>
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <fieldset class="form-horizontal" id="auditList">
        <legend>Audit rekordok</legend>
        <div class="container textWebkitRight" style="padding:5px"><button id="refreshAll-button" type="button" class="btn btn-secondary" onclick="processEntityUpdated()">Frissítés</button></div>
        <div class="control-group">
            <table id="audit" class="table table-striped table-bordered table-hover compact cell-border fullWidth" aria-describedby="auditList">
                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Referencia ID</th>
                            <th scope="col">Időpont</th>
                            <th scope="col">Végrehajtó</th>
                            <th scope="col">Típus</th>
                            <th style="width:40%" scope="col">Leírás</th>
                            <th scope="col">Egyéb</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Referencia ID</th>
                            <th scope="col">Időpont</th>
                            <th scope="col">Végrehajtó</th>
                            <th scope="col">Típus</th>
                            <th style="width:40%" scope="col">Leírás</th>
                            <th scope="col">Egyéb</th>
                        </tr>
                    </tfoot>
                </table>
            </div>
    </fieldset>

    </div>

    <%@include file="../include/commonAlert.html" %>
    <%@include file="../include/commonConfirm.html" %>
</body>
</html>
