$(document).ready(function() {
    $("#nav-ador-list").addClass("active");
    hidePeople();
    setupMenu();
    getInfo();
    setupPersonTable();
    loadStructure();
    adoratorListClick();
});

var structureInfo;
var peopleInfo;
var hourInfo;
var dayNames;

function loadStructure() {
    $.get("/resources/json/dataTables_cooEditStructure.json", function(data) {
        structureInfo = data;
    });
}

function getInfo() {
    jQuery.ajaxSetup({async:false});
    $.get("/adorationSecure/getAdoratorList", function(data) {
        peopleInfo = data.data.relatedPersonList;
        hourInfo = data.data.linkList;
        dayNames = data.data.dayNames;
    });
    jQuery.ajaxSetup({async:true});
}

function hidePeople() {
    //hide adoratorCooList and AdoratorList
    $("#adoratorList").hide();
    $("#adoratorCooList").hide();
}

function adoratorListClick() {
    if (loggedInUserInfo.isPrivilegedAdorator) {
        $("#adoratorCooList").show();
        $('#personCoo').DataTable().draw();
    } else {
        $("#adoratorList").show();
        $('#person').DataTable().draw();
    }
}

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "adorList-pre": function ( a ) {
        var str = "commentClick(";
        var startPos = a.indexOf(str) + str.length;
        var endPos = a.indexOf(")");
        str = a.substring(startPos, endPos);
        return parseInt( str );
    },
    "adorList-asc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },
    "adorList-desc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} );

function setupPersonTable() {
    //person - coordinator table
    $('#personCoo').DataTable( {
        data: peopleInfo,
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[10, 50, 100, -1], [10, 50, 100, "All"]],
        "columns": [
            { "data": "id" },
            { "data": "name" },
            { "data": "mobile" },
            { "data": "email" },
            { "data": null,
                "defaultContent": "Nincs adat." }, //this is to compile committed hours
            { "data": "coordinatorComment" },
            { "data": "visibleComment" }
        ],
        "columnDefs": [
            { type: 'adorList', targets: 0 },
            {
                "className": "text-center",
                "targets": [0,1,2,3]
            },
            {
                "render": function ( data, type, row ) {
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data-toggle=\"modal\" data-target=\"#editModal\" onclick=\"commentClick(" + data + ")\">" + data + "</button>";
                    return z;
                },
                "targets": 0
            },
            {
                "render": function ( data, type, row ) {
                    var z = buildHours(data.id);
                    return z;
                },
                "targets": 4
            }
        ]
    } );
    //person - adorator table
    $('#person').DataTable( {
        data: peopleInfo,
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[10, 50, 100, -1], [10, 50, 100, "All"]],
        "columns": [
            { "data": "id" },
            { "data": "name" },
            { "data": "mobile" },
            { "data": "email" },
            { "data": null,
                "defaultContent": "Nincs adat." }, //this is to compile committed hours
            { "data": "visibleComment" }
        ],
        "columnDefs": [
            { type: 'adorList', targets: 0 },
            {
                "className": "text-center",
                "targets": [0,1,2,3]
            },
            {
                "render": function ( data, type, row ) {
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data=\"commentClick(" + data + ")\">" + data + "</button>";
                    return z;
                },
                "targets": 0
            },
            {
                "render": function ( data, type, row ) {
                    var z = buildHours(data.id);
                    return z;
                },
                "targets": 4
            }
        ]
    } );
}

function buildHours(personId) {
    var data = "";
    if (hourInfo != "undefined" && hourInfo.length > 0) {
        for (var i = 0; i < hourInfo.length; i++) {
            if (hourInfo[i].personId == personId) {
                var hourId = hourInfo[i].hourId;
                var z = "<div>" + getDayNameLocalized(hourId, dayNames) + ", " + getHourName(hourId) + " óra";
                if (hourInfo[i].type == 0) {
                    z = z + ", Kápolnában"
                }
                if (hourInfo[i].type == 1) {
                    z = z + ", Online"
                }
                if (hourInfo[i].type == 2) {
                    z = z + ", Egyszeri alkalom"
                }
                if (hourInfo[i].type == 3) {
                    z = z + ", Következő alkalom lemondva"
                }
                if (hourInfo[i].publicComment.length > 0) {
                    z = z + ", Megjegyzés:" + hourInfo[i].publicComment;
                }
                data = data + z + "</div><br/>";
            }
        }
        return data;
    } else {
        return "Hibás adat.";
    }
}

function commentClick(e) {
    //coordinators may update coordinator comments
    $("#editId").val(e);
    $('#resetChangesButton').attr('onclick', 'reBuildModal()');
    $('#editCenterTitle').text('Módosítás');
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
        url: '/adorationSecure/getPerson/' + editId,
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
        showAlert("Hiba történt!", "Sajnos a mentés most nem lehetséges, olvassa újra be az oldalt.");
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
        showAlert("Hiba történt!", eStr);
        return;
    }
    //save
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    beforeRequest();
    $.ajax({
        url : '/adorationSecure/updatePersonByCoo',
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
    var path = "/adorationSecure/adorationListPeople";
    window.location = path;
}

