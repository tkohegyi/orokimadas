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
<meta name="mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<script src="/resources/js/external/jquery-3.6.4.min.js"></script>
<script src="/resources/js/external/bootstrap.min.js"></script>
<script src="/resources/js/external/dataTables/datatables.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/adoratorListPeople.js"></script>
<title>Örökimádás - Vác - Adorátorok listája</title>
<link href="/resources/css/external/bootstrap.min.css" rel="stylesheet" media="screen">
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
    <fieldset id="adoratorCooList" class="form-horizontal">
        <legend>Adorálók listája</legend>
        <div class="control-group">
            <table id="personCoo" class="table table-striped table-bordered table-hover compact cell-border" style="width:100%" aria-describedby="adoratorCooList">
                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Név</th>
                            <th scope="col">Telefonszám</th>
                            <th scope="col">e-mail</th>
                            <th scope="col">Vállalt órák</th>
                            <th scope="col">Koordinátor megjegyzés</th>
                            <th scope="col">Közös/Látható megjegyzés</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Név</th>
                            <th scope="col">Telefonszám</th>
                            <th scope="col">e-mail</th>
                            <th scope="col">Vállalt órák</th>
                            <th scope="col">Koordinátor megjegyzés</th>
                            <th scope="col">Közös/Látható megjegyzés</th>
                        </tr>
                    </tfoot>
            </table>
         </div>
    </fieldset>

    <fieldset id="adoratorList" class="form-horizontal">
        <legend>Adorálók listája</legend>
        <div class="control-group">
            <table id="person" class="table table-striped table-bordered table-hover compact cell-border" style="width:100%" aria-describedby="adoratorList">
                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Név</th>
                            <th scope="col">Telefonszám</th>
                            <th scope="col">e-mail</th>
                            <th scope="col">Vállalt órák</th>
                            <th scope="col">Megjegyzés</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Név</th>
                            <th scope="col">Telefonszám</th>
                            <th scope="col">e-mail</th>
                            <th scope="col">Vállalt órák</th>
                            <th scope="col">Megjegyzés</th>
                        </tr>
                    </tfoot>
            </table>
         </div>
    </fieldset>

    <!-- Modal Edit Coo Comment -->
    <div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="editCenterTitle" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="editCenterTitle">Módosítás</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Cancel">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
              <form>
                  <table id="editTable" class="table table-hover table-bordered" aria-describedby="editCenterTitle">
                      <thead>
                          <tr>
                              <th scope="col">Oszlop név</th>
                              <th style="width:40%" scope="col">Tartalom</th>
                              <th scope="col">Segítség</th>
                          </tr>
                      </thead>
                      <tbody id="editContent"/>
                  </table>
                  <input id="editId" type="hidden" value="">
               </form>
          </div>
          <div class="modal-footer">
            <table class="fullWidth" role="presentation"><tr>
                <td class="textRight">
                    <button id="resetChangesButton" type="button" class="btn btn-secondary" onclick="reBuildModal()">Eredeti adat visszanyerése</button>
                    <button id="cancelButton" type="button" class="btn btn-info" data-dismiss="modal">Mégsem</button>
                    <button id="saveChangesButton" type="button" class="btn btn-success" onclick="saveChanges()">Mentés</button>
                </td>
            </tr></table>
          </div>
        </div>
      </div>
    </div>

  </div>
</body>
</html>
