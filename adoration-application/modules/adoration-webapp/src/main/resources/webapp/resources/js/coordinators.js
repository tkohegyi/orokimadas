$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    setupMenu();
    setupTable();
    loadStructure();
});

var structureInfo;

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "coordinator-pre": function ( a ) {
        var str = "st";
        var startPos = a.indexOf(str) + str.length;
        var endPos = a.indexOf("end");
        str = a.substring(startPos, endPos);
        return parseInt( str );
    },
    "coordinator-desc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },
    "coordinator-asc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} );

function loadStructure() {
    $.get("/resources/json/dataTables_coordinatorStructure.json", function(data) {
        structureInfo = data;
    });
}

function setupTable() {
    $('#coordinator').DataTable( {
        "ajax": "/adorationSecure/getCoordinators",
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[50, 100, -1], [50, 100, "All"]],
        "order": [[ 1, "asc" ]],
        "columns": [
            { "data": "id" },
            { "data": "coordinatorType", "width": "200px" },
            { "data": "personId" },
            { "data": "personName" }
        ],
        "columnDefs": [
            { type: 'coordinator', targets: 1 },
            {
                "className": "text-center",
                "targets": [0,1,2,3]
            },
            {
                "render": function ( data, type, row ) {
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data-toggle=\"modal\" data-target=\"#editModal\" onclick=\"changeClick(" + data + ")\">" + data + "</button>";
                    z = z + "<button type=\"button\" class=\"btn btn-secondary btn-sm\" data-toggle=\"modal\" data-target=\"#historyModal\" onclick=\"changeHistoryClick(" + data + ")\">Log</button>";
                    return z;
                },
                "targets": 0
            },
            {
                "render": function ( data, type, row ) {
                    var z;
                    switch (data) {
                    case "0": z = '<input type="hidden" value="st0end">0. óra órakoordinátora'; break;
                    case "1": z = '<input type="hidden" value="st1end">1. óra órakoordinátora'; break;
                    case "2": z = '<input type="hidden" value="st2end">2. óra órakoordinátora'; break;
                    case "3": z = '<input type="hidden" value="st3end">3. óra órakoordinátora'; break;
                    case "4": z = '<input type="hidden" value="st4end">4. óra órakoordinátora'; break;
                    case "5": z = '<input type="hidden" value="st5end">5. óra órakoordinátora'; break;
                    case "6": z = '<input type="hidden" value="st6end">6. óra órakoordinátora'; break;
                    case "7": z = '<input type="hidden" value="st7end">7. óra órakoordinátora'; break;
                    case "8": z = '<input type="hidden" value="st8end">8. óra órakoordinátora'; break;
                    case "9": z = '<input type="hidden" value="st9end">9. óra órakoordinátora'; break;
                    case "10": z = '<input type="hidden" value="st10end">10. óra órakoordinátora'; break;
                    case "11": z = '<input type="hidden" value="st11end">11. óra órakoordinátora'; break;
                    case "12": z = '<input type="hidden" value="st12end">12. óra órakoordinátora'; break;
                    case "13": z = '<input type="hidden" value="st13end">13. óra órakoordinátora'; break;
                    case "14": z = '<input type="hidden" value="st14end">14. óra órakoordinátora'; break;
                    case "15": z = '<input type="hidden" value="st15end">15. óra órakoordinátora'; break;
                    case "16": z = '<input type="hidden" value="st16end">16. óra órakoordinátora'; break;
                    case "17": z = '<input type="hidden" value="st17end">17. óra órakoordinátora'; break;
                    case "18": z = '<input type="hidden" value="st18end">18. óra órakoordinátora'; break;
                    case "19": z = '<input type="hidden" value="st19end">19. óra órakoordinátora'; break;
                    case "20": z = '<input type="hidden" value="st20end">20. óra órakoordinátora'; break;
                    case "21": z = '<input type="hidden" value="st21end">21. óra órakoordinátora'; break;
                    case "22": z = '<input type="hidden" value="st22end">22. óra órakoordinátora'; break;
                    case "23": z = '<input type="hidden" value="st23end">23. óra órakoordinátora'; break;
                    case "24": z = '<input type="hidden" value="st24end">Éjszakai napszak-koordinátor'; break;
                    case "30": z = '<input type="hidden" value="st30end">Délelőtti napszak-koordinátor'; break;
                    case "36": z = '<input type="hidden" value="st36end">Délutáni napszak-koordinátor'; break;
                    case "42": z = '<input type="hidden" value="st42end">Esti napszak-koordinátor'; break;
                    case "48": z = '<input type="hidden" value="st48end">Általános koordinátor'; break;
                    case "96": z = '<input type="hidden" value="st96end">Spirituális vezető'; break;
                    default: z = '<input type="hidden" value="st255end">???';
                    }
                    return z;
                },
                "targets": 1
            }
        ]
    } );
    var filter = findGetParameter("filter");
    if ((filter != null) && (filter.length > 0)) {
        var table = $('#coordinator').DataTable();
        table.search(filter).draw();
    }
}

function addClick() {
    $("#editId").val(0);
    $("#deleteButton").hide();
    $('#resetChangesButton').attr('onclick', 'reBuildAddModal()');
    if ((typeof structureInfo != "undefined") && (typeof structureInfo.new != "undefined") && (typeof structureInfo.new.title != "undefined")) {
       $('#editCenterTitle').text(structureInfo.new.title);
    } else {
       $('#editCenterTitle').text('Új felvétele');
    }
    reBuildAddModal();
}

function changeClick(e) {
    $("#editId").val(e);
    $("#deleteButton").show();
    $('#resetChangesButton').attr('onclick', 'reBuildModal()');
    if ((typeof structureInfo != "undefined") && (typeof structureInfo.edit != "undefined") && (typeof structureInfo.edit.title != "undefined")) {
       $('#editCenterTitle').text(structureInfo.edit.title);
    } else {
       $('#editCenterTitle').text('Módosítás');
    }
    reBuildModal();
}

function requestComplete() {
    $('#saveChangesButton').removeAttr('disabled');
}

function beforeRequest() {
    $('#saveChangesButton').attr('disabled', 'disabled');
}

function reBuildModal() {
    requestComplete(); //ensure availability of save button when the modal is refreshed
    //reconstruct modal
    var c = $('#editContent');
    c.remove();
    var t = $('#editTable');
    c = $("<tbody id=\"editContent\"/>");
    t.append(c);
    //get and fill modal
    var retObj;
    var editId = $("#editId").val(); //filled by the button's onclick method
    $.ajax({
        type: "GET",
        url: '/adorationSecure/getCoordinator/' + editId,
        async: false,
        success: function(response) {
            retObj = response.data;
        }
    });
    //identify the retObj
    if (typeof retObj == "undefined") return; //if person is not available, there is no point to rebuild
    if (typeof structureInfo != "undefined") {
        //we have structureInfo
        var info = structureInfo.info;
        for (var i = 0; i < info.length; i++) { //iterate through columns
            var row = info[i];
            //first is about visibility - if not visible, skip
            if ((typeof row.edit != "undefined") && (typeof row.edit.visible != "undefined") && (row.edit.visible == false)) continue;
            var r = $("<tr/>");
            //additional help text, based on behavior
            let addMandatory = "";
            let mandatoryFlag = row.mandatory;
            if ((typeof row.mandatory != "undefined") && mandatoryFlag) {
                addMandatory = "-m-";  // -m- is added
            }
            var td1 = $("<th>" + row.text + "</th>");
            var text = "";
            var idText = "field-" + row.id;
            var nameText = idText + "-" + row.type + addMandatory;
            let command = "retObj." + row.id;
            var original = eval(command); //NOSONAR
            if (row.type == "fixText") {
                text = original;
            }
            if (row.type.split("-")[0] == "input") { //input-100, input-1000 etc
                text = "<input class=\"customField\" onchange=\"valueChanged(this,'" + row.type + "')\" type=\"text\" name=\"" + nameText + "\" id=\"" + idText + "\" value=\"" + original + "\" />";
            }
            if (row.type == "dateString-nullable") {
                text =  "<input onchange=\"valueChanged(this,'" + row.type + "')\" type=\"date\" name=\"" + nameText + "\" id=\"" + idText + "\"  value=\"" + original + "\"/>";
            }
            if (row.type == "singleSelect") {
                text = "";
                for (var j = 0; j < row.selection.length; j++) {
                    let selected = "";
                    if (original == row.selection[j].id) {
                        selected = " selected ";
                    }
                    text += "<option value=\"" + row.selection[j].id + "\"" + selected + ">" + row.selection[j].value + "</option>";
                }
                text = "<select id=\"" + idText + "\" class=\"custom-select\" onchange=\"valueChanged(this,'" + row.type + "')\">" + text + "</select>"
            }
            if (row.type == "i/n-boolean") {
                let checked = "";
                if (original == true) {
                    checked = " checked ";
                }
                text =  "<input onchange=\"valueChanged(this,'" + row.type + "')\" type=\"checkbox\" " + checked + " name=\"" + nameText + "\" id=\"" + idText + "\" />";
            }
            //preserve original value too
            var originalValue = "<input id=\"orig-" + idText + "\" type=\"hidden\" value=\"" + original + "\">";
            var td2 = $("<td id=\"td-" + idText + "\">" + text + originalValue + "</td>");
            //help text
            var td3 = $("<td>" + row.helpText + "</td>");
            r.append(td1);r.append(td2);r.append(td3);
            c.append(r);
        }
    }
}

function reBuildAddModal() {
    requestComplete(); //ensure availability of save button when the modal is refreshed
    //reconstruct modal
    var c = $('#editContent');
    c.remove();
    var t = $('#editTable');
    c = $("<tbody id=\"editContent\"/>");
    t.append(c);
    //get and fill modal
    if (typeof structureInfo != "undefined") {
        //we have structureInfo
        var info = structureInfo.info;
        for (var i = 0; i < info.length; i++) { //iterate through columns
            var row = info[i];
            //first is about visibility - if not visible, skip
            if ((typeof row.new != "undefined") && (typeof row.new.visible != "undefined") && (row.new.visible == false)) continue;
            var r = $("<tr/>");
            //additional help text, based on behavior
            let addMandatory = "";
            let mandatoryFlag = row.mandatory;
            if ((typeof row.mandatory != "undefined") && mandatoryFlag) {
                addMandatory = "-m-";  // -m- is added
            }
            var td1 = $("<th>" + row.text + "</th>");
            var text = "";
            var idText = "field-" + row.id;
            var nameText = idText + "-" + row.type + addMandatory;
            var original = "";
            if ((typeof row.new != "undefined") && (typeof row.new.default != "undefined")) {
                original = row.new.default;
            }
            if (row.type == "fixText") {
                text = original;
            }
            if (row.type.split("-")[0] == "input") { //input-100, input-1000 etc
                text = "<input class=\"customField\" onchange=\"valueChanged(this,'" + row.type + "')\" type=\"text\" name=\"" + nameText + "\" id=\"" + idText + "\" value=\"" + original + "\" />";
            }
            if (row.type == "dateString-nullable") {
                text =  "<input onchange=\"valueChanged(this,'" + row.type + "')\" type=\"date\" name=\"" + nameText + "\" id=\"" + idText + "\"  value=\"" + original + "\"/>";
            }
            if (row.type == "singleSelect") {
                text = "";
                for (var j = 0; j < row.selection.length; j++) {
                    let selected = "";
                    if (original == row.selection[j].id) {
                        selected = " selected ";
                    }
                    text += "<option value=\"" + row.selection[j].id + "\"" + selected + ">" + row.selection[j].value + "</option>";
                }
                text = "<select id=\"" + idText + "\" class=\"custom-select\" onchange=\"valueChanged(this,'" + row.type + "')\">" + text + "</select>"
            }
            if (row.type == "i/n-boolean") {
                let checked = "";
                if (original == true) {
                    checked = " checked ";
                }
                text =  "<input onchange=\"valueChanged(this,'" + row.type + "')\" type=\"checkbox\" " + checked + " name=\"" + nameText + "\" id=\"" + idText + "\" />";
            }
            //preserve original value too
            var originalValue = "<input id=\"orig-" + idText + "\" type=\"hidden\" value=\"" + original + "\">";
            var td2 = $("<td id=\"td-" + idText + "\">" + text + originalValue + "</td>");
            //help text
            var td3 = $("<td>" + row.helpText + "</td>");
            r.append(td1);r.append(td2);r.append(td3);
            c.append(r);
        }
    }
}

function valueChanged(theObject, type) {
	var o = $("#" + theObject.id);
	var origO = $("#" + "orig-" + theObject.id);
	var td = $("#" + "td-" + theObject.id);
	let v;
	type = type.split("-")[0]; // dateString , input etc
	switch (type) {
	    case "dateString": // val();
	        v = o.val();
	        break;
	    case "singleSelect": // select
	        v = o.find(":selected").val();
	        break;
	    case "input":
	        v = o.prop("value");
	        break;
	    case "i/n":
	        v = o.prop("checked").toString();
	}
	if (v == origO.val()) {
	    td.removeClass("table-danger");
	} else {
	    td.addClass("table-danger");
	}
}

function saveChanges() {
    var b = {}; //empty object
    var editId = $("#editId").val(); //filled by the button's onclick method
    b.id = editId;
	//validations + prepare object
	var eStr = "";
    var bad = 0;
    if (typeof structureInfo == "undefined") {
        showAlert("Hiba történt!", "Sajnos a mentés most nem lehetséges, olvassa be újra az oldalt.");
        return;
    }
    //we have structureInfo
    var info = structureInfo.info;
    for (var i = 0; i < info.length; i++) { //iterate through columns
        var row = info[i];
        //first is about visibility - if not visible, skip
        if (b.id == 0) {
            if ((typeof row.new != "undefined") && (typeof row.new.visible != "undefined") && (row.new.visible == false)) continue;
        } else {
            if ((typeof row.edit != "undefined") && (typeof row.edit.visible != "undefined") && (row.edit.visible == false)) continue;
        }
        if (row.type == "fixText") continue; //don't bother us with such a value
        let v;
        var type = row.type.split("-")[0]; // dateString , input etc
        var idText = "field-" + row.id;
        var o = $("#" + idText);
        switch (type) {
            case "dateString": // val();
                v = o.val();
                break;
            case "singleSelect": // select
                v = o.find(":selected").val();
                break;
            case "i/n":
                v = o.prop("checked").toString();
                break;
            case "input":
            default:
                v = o.prop("value");
                if ((typeof row.mandatory != "undefined") && (row.mandatory == true)) { // if mandatory, cannot be empty
                    if (v.length <= 0) {
                        eStr = "Value of \"" + row.name + "\" is not specified, pls specify!";
                        bad = 1;
                        }
                }
                break;
        } //value in v
        let command = "b." + row.id + "=\"" + v.toString() + "\"";
        eval(command); //NOSONAR - add object to b structure
    }
    // b is ready
    //validation done (cannot validate more at client level
    if (bad == 1) {
        showAlert("Figyelem!", eStr);
        return;
    }
    //save
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    beforeRequest();
    $.ajax({
        url : '/adorationSecure/updateCoordinator',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(b),
        dataType: 'json',
        success : processEntityUpdated,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
        complete : requestComplete,
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        showAlert("Hiba történt!", obj.entityUpdate);
    });
}

function processEntityUpdated() {
    var table = $('#coordinator').DataTable();
    var filter = table.search(); //preserve filter
    var path = "/adorationSecure/coordinators";
    if (typeof filter != "undefined" && filter.length > 0) {
        path = path + "?filter=" + filter;
    }
    window.location = path;
}

function changeHistoryClick(data) {
   reBuildHistoryModal(data);
}

function reBuildHistoryModal(personId) {
    var hc = $("<tbody id=\"historyContent\"/>");
    $.get('/adorationSecure/getPersonHistory/' + personId , function(data) {
        if ((typeof data != "undefined") && (typeof data.data != "undefined")) {
            var info = data.data;
            for (var i = 0; i < info.length; i++) {
              var r = $("<tr/>");
              var d = $("<td>" + info[i].activityType + "</td>");r.append(d);
              d = $("<td>" + info[i].atWhen + "</td>");r.append(d);
              d = $("<td>" + info[i].byWho + "</td>");r.append(d);
              d = $("<td>" + info[i].description + "</td>");r.append(d);
              if (info[i].data != null) {
                  d = $("<td>" + info[i].data + "</td>");r.append(d);
              } else {
                  d = $("<td/>");r.append(d);
              }
              hc.append(r);
            }
        } else { //logged out or other error at server side
            showAlert("Figyelem!", "Önnek ismét be kell jelentkeznie.",
                function () {window.location.pathname = "/adoration/"});
            return;
        }
    });
    $('#historyContent').replaceWith(hc);
}

function changeTimeClick(data) {
   reBuildTimeModal(data);
}

function cancelNewPartOfModal() {
    $('#newButton').show(500);
}

function showNewPartOfModal() {
    $('#newButton').hide(500);
}

function deleteEntity() {
    showConfirm("Megerősítés kérdés", "Biztosan törölni akarja ezt a Koordinátor hozzárendelést - végleg?", function () { deleteEntityConfirmOk() });
    }

function deleteEntityConfirmOk() {
    var entityId = $("#editId").val(); //filled by the button's onclick method
    var req = {
        entityId : entityId,
    };
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/deleteCoordinator',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(req),
        dataType: 'json',
        success : processEntityUpdated,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
        complete : requestComplete,
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        showAlert("Hiba történt!", obj.entityUpdate);
    });
}
