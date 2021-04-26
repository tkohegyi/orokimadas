$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    setupMenu();
    setupSocialTable();
    loadStructure();
});

var structureInfo;
var imgSrc; //used by renderer
var hourInfo;

function loadStructure() {
    $.get("/resources/json/dataTables_socialStructure.json", function(data) {
        structureInfo = data;
    });
}

function setupSocialTable() {
    $('#social').DataTable( {
        "ajax": "/adorationSecure/getSocialTable",
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "lengthMenu": [[5, 50, 100, -1], [5, 50, 100, "All"]],
        "scrollX": true,
        "columns": [
            { "data": "id" },
            { "data": "personId", "width": "200px" },
            { "data": "socialStatus" },
            { "data": "googleUserName" },
            { "data": "googleUserPicture" },
            { "data": "googleEmail" },
            { "data": "googleUserId" },
            { "data": "facebookUserName" },
            { "data": "facebookFirstName" },
            { "data": "facebookEmail" },
            { "data": "facebookUserId" },
            { "data": "comment" }
        ],
        "columnDefs": [
            {
                "className": "text-center",
                "targets": [0,1,2,3,4,5,6,7,8,9,10,11]
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
                    if (data != null) {
                        z = data;
                    } else {
                        z = "Nem tudjuk ki...";
                    }
                    return z;
                },
                "targets": 1
            },
            {
                "render": function ( data, type, row ) {
                    var z;
                    switch (data) {
                    case 1: z = 'Azonosításra vár'; break;
                    case 2: z = 'Azonosított adoráló'; break;
                    case 3: z = 'Vendég/érdeklődő'; break;
                    default: z = '???';
                    }
                    return z;
                },
                "targets": 2
            },
            {
                "render": function ( data, type, row ) {
                    var z;
                    if ((data != null) && (data.startsWith("http"))) {
                        z = "<img height=\"60\" src=\"" + data + "\" alt=\"Google User Image\"/>";
                    } else {
                        z = "";
                    }
                    return z;
                },
                "targets": 4
            }
        ]
    } );
    var filter = findGetParameter("filter");
    if ((filter != null) && (filter.length > 0)) {
        var table = $('#social').DataTable();
        table.search(filter).draw();
    }
}

function processEntityUpdated() {
    var table = $('#social').DataTable();
    var filter = table.search(); //preserve filter
    var path = "/adorationSecure/social";
    if (typeof filter != "undefined" && filter.length > 0) {
        path = path + "?filter=" + filter;
    }
    window.location = path;
}

function changeHistoryClick(data) {
   reBuildHistoryModal(data);
}

function reBuildHistoryModal(id) {
    var hc = $("<tbody id=\"historyContent\"/>");
    $.get('/adorationSecure/getSocialHistory/' + id , function(data) {
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

function changeClick(e) {
   $("#editId").val(e);
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
    var social;
    var editId = $("#editId").val(); //filled by the button's onclick method
    $.ajax({
        type: "GET",
        url: '/adorationSecure/getSocial/' + editId,
        async: false,
        success: function(response) {
            social = response.data;
        }
    });
    //identify the social
    if (typeof social == "undefined") return; //if social is not available, there is no point to rebuild
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
            let command = "social." + row.id;
            var original = eval(command); //NOSONAR
            if (row.type == "fixText") {
                text = original;
            }
            if (row.type == "id-nullable") {
                if (original == null) {
                    original = "N/A"
                }
                text = "<input class=\"customField\" onchange=\"valueChanged(this,'" + row.type + "')\" type=\"text\" name=\"" + nameText + "\" id=\"" + idText + "\" value=\"" + original + "\" />";
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
        if ((typeof row.edit != "undefined") && (typeof row.edit.visible != "undefined") && (row.edit.visible == false)) continue;
        if (row.type == "fixText") continue; //don't bother us with such a value
        let v;
        var type = row.type.split("-")[0]; // dateString , input etc
        var idText = "field-" + row.id;
        var o = $("#" + idText);
        var isNull = false;
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
            case "id": //id-nullable, id-nonNullable
                v = o.prop("value");
                var nullable = row.type.split("-")[1]; // null, nullable or nonNullable
                if ((v != null) && (v.localeCompare("N/A") == 0)) {
                    v = null;  //make null from N/A value
                    isNull = true;
                }
                if ((typeof row.mandatory != "undefined") && (row.mandatory == true)) { // if mandatory, shall be a number
                    if ((v == null) || isNaN(parseInt(v))) {
                        eStr = "Value of \"" + row.name + "\" must be valid ID, pls specify!";
                        bad = 1;
                    }
                }
                if ((typeof nullable != "undefined") && (nullable.localeCompare("nullable") == 0)) { // if nullable, can be number or null
                    if ((v != null) && isNaN(parseInt(v))) {
                        eStr = "Value of \"" + row.name + "\" must be null or valid ID, pls specify!";
                        bad = 1;
                    }
                }
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
        if ((typeof v == "undefined") || (v == null)) {
            v = "null"; //ensure that toString will work
        }
        if (!isNull) {
            let command = "b." + row.id + "=\"" + v.toString() + "\"";
            eval(command); //NOSONAR - add object to b structure
        }
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
        url : '/adorationSecure/updateSocial',
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

function deleteSocial() {
    showConfirm("Megerősítés kérdés", "Biztosan törölni akarja ezt a Közösségi belépést - végleg?", function () { deleteSocialConfirmOk() });
    }

function deleteSocialConfirmOk() {
    var entityId = $("#editId").val(); //filled by the button's onclick method
    var req = {
        entityId : entityId,
    };
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/deleteSocial',
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
