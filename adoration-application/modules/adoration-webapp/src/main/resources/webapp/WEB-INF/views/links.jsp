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
<script src="/resources/js/links.js" nonce></script>
<title>Örökimádás - Vác - Adminisztráció - Órák</title>
<link href="/resources/css/external/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/js/external/dataTables/datatables.min.css" rel="stylesheet" type="text/css"/>
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <fieldset class="form-horizontal" id="hourList">
        <legend>Órák listája</legend>
        <div class="container centerDiv" style="padding:5px"><button id="add-button" type="button" class="btn btn-primary" data-toggle="modal" data-target="#editModal" onclick="addClick()">Új óra rögzítése...</button></div>
        <div class="container centerDiv" style="padding:5px"><button id="refreshAll-button" type="button" class="btn btn-secondary" onclick="processEntityUpdated()">Frissítés</button></div>
        <div class="control-group">
            <table id="link" class="table table-striped table-bordered table-hover compact cell-border fullWidth" aria-describedby="hourList">
                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Nap</th>
                            <th scope="col">Óra</th>
                            <th scope="col">Adoráló Név</th>
                            <th scope="col">Adoráló Telefonszám</th>
                            <th scope="col">Adoráló E-mail</th>
                            <th scope="col">Prioritás</th>
                            <th scope="col">Hely</th>
                            <th scope="col">Admin megjegyzés</th>
                            <th scope="col">Publikus megjegyzés</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Nap</th>
                            <th scope="col">Óra</th>
                            <th scope="col">Adoráló Név</th>
                            <th scope="col">Adoráló Telefonszám</th>
                            <th scope="col">Adoráló E-mail</th>
                            <th scope="col">Prioritás</th>
                            <th scope="col">Hely</th>
                            <th scope="col">Admin megjegyzés</th>
                            <th scope="col">Publikus megjegyzés</th>
                        </tr>
                    </tfoot>
            </table>
        </div>
    </fieldset>

    </div>

    <!-- Modal Time -->
    <div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="editCenterTitle" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="editCenterTitle">Órák</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Cancel">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
              <form id="editTable">
                <input id="editId" type="hidden" value="">
                <table class="table table-hover table-bordered" aria-describedby="editCenterTitle">
                      <thead>
                          <tr>
                              <th scope="col">Mező</th>
                              <th scope="col">Érték</th>
                          </tr>
                      </thead>
                      <tbody id="newTimeContent">
                          <tr><td id="adoratorName">Adoráló azonosítója</td><td>
                            <input type="text" id="newAdorator">
                          </td></tr>
                          <tr><td>Nap</td><td>
                            <select id="newDay">
                                <option value="0">vasárnap</option>
                                <option value="24">hétfő</option>
                                <option value="48">kedd</option>
                                <option value="72">szerda</option>
                                <option value="96">csütörtök</option>
                                <option value="120">péntek</option>
                                <option value="144">szombat</option>
                            </select></td></tr>
                          <tr><td>Óra</td><td>
                              <select id="newHour">
                                <option value="0">0</option>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                                <option value="6">6</option>
                                <option value="7">7</option>
                                <option value="8">8</option>
                                <option value="9">9</option>
                                <option value="10">10</option>
                                <option value="11">11</option>
                                <option value="12">12</option>
                                <option value="13">13</option>
                                <option value="14">14</option>
                                <option value="15">15</option>
                                <option value="16">16</option>
                                <option value="17">17</option>
                                <option value="18">18</option>
                                <option value="19">19</option>
                                <option value="20">20</option>
                                <option value="21">21</option>
                                <option value="22">22</option>
                                <option value="23">23</option>
                              </select></td></tr>
                          <tr><td>Online</td><td><input type="checkbox" id="newOnline" value=""></td></tr>
                          <tr><td>Prioritás (1..)</td><td>
                            <input type="number" id="newPriority" min="1" max="25">
                          </td></tr>
                          <tr><td>Admin megjegyzés</td><td>
                            <input type="text" id="newAdminComment">
                          </td></tr>
                          <tr><td>Publikus megjegyzés</td><td>
                            <input type="text" id="newPublicComment">
                          </td></tr>
                      </tbody>
                </table>
              </form>
          </div>
          <div class="modal-footer">
            <table class="fullWidth" role="presentation"><tr>
                <td class="textLeft"><button id="deleteButton" type="button" class="btn btn-danger" onclick="deleteLink()">Óra törlése</button></td>
                <td class="textRight">
                    <button id="cancelButton" type="button" class="btn btn-info" data-dismiss="modal">Mégsem</button>
                    <button id="saveChangesButton" type="button" class="btn btn-success" onclick="saveNew()">Mentés</button>
                </td>
            </tr></table>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Log/History -->
    <div class="modal fade" id="historyModal" tabindex="-1" role="dialog" aria-labelledby="historyCenterTitle" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="historyCenterTitle">Log</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Cancel">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
              <form>
                  <table id="historyTable" class="table table-hover table-bordered" aria-describedby="historyCenterTitle">
                      <thead>
                          <tr>
                              <th scope="col">Típus</th>
                              <th scope="col">Időpont</th>
                              <th scope="col">Végrehajtó</th>
                              <th style="width:40%" scope="col">Leírás</th>
                              <th scope="col">Egyéb</th>
                          </tr>
                      </thead>
                      <tbody id="historyContent"/>
                  </table>
               </form>
          </div>
          <div class="modal-footer">
            <button id="cancelButton" type="button" class="btn btn-info" data-dismiss="modal">Mégsem</button>
          </div>
        </div>
      </div>
    </div>
    <%@include file="../include/commonAlert.html" %>
    <%@include file="../include/commonConfirm.html" %>
</body>
</html>
