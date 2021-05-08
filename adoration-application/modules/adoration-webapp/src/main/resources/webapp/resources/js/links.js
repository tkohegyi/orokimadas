$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    setupMenu();
    setupLinkTable();
});

var linkInfo;

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "hourBased-pre": function ( a ) {
        var str = " óra";
        var endPos = a.indexOf(str);
        str = a.substring(0, endPos);
        return parseInt( str );
    },
    "hourBased-desc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },
    "hourBased-asc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} );

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "dayBased-pre": function ( a ) {
        var dayNames = ['vasárnap', 'hétfő', 'kedd', 'szerda', 'csütörtök', 'péntek', 'szombat'];
        for (var index = 0; index < dayNames.length; index++) {
            if (a.indexOf(dayNames[index])==0) {
                return index;
            }
        }
        return 0;
    },
    "dayBased-desc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },
    "dayBased-asc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} );

function setupLinkTable() {
    //we must have this response before going forward
    jQuery.ajaxSetup({async:false});
    $.get("/adorationSecure/getLinkTable", function(data) {
        linkInfo = data.data;
    });
    jQuery.ajaxSetup({async:true});
    $('#link').DataTable( {
        data: linkInfo.linkList,
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[5, 50, 100, -1], [5, 50, 100, "All"]],
        "columns": [
            { "data": "id" },
            { "data": "day" },
            { "data": "hour" },
            { "data": "name" },
            { "data": "phone" },
            { "data": "email" },
            { "data": "priority" },
            { "data": "type" },
            { "data": "adminComment" },
            { "data": "publicComment" }
        ],
        "columnDefs": [
            { type: 'numeric-id', targets: 0 },
            { type: 'dayBased', targets: 1 },
            { type: 'hourBased', targets: 2 },
            {
                "className": "text-center",
                "targets": [0,1,2,3,4,5,6,7,8,9]
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
                    var z = getDayNameLocalized(row.hourId, linkInfo.dayNames)
                    return z;
                },
                "targets": 1
            },
            {
                "render": function ( data, type, row ) {
                    var z = getHourName(row.hourId);
                    return z + " óra";
                },
                "targets": 2
            },
            {
                "render": function ( data, type, row ) {
                    var z = getPerson(linkInfo.relatedPersonList, row.personId);
                    if (z != "undefined") {
                        z = z.name;
                    } else {
                        z = "Ismeretlen";
                    }
                    return z;
                },
                "targets": 3
            },
            {
                "render": function ( data, type, row ) {
                    var z = getPerson(linkInfo.relatedPersonList, row.personId);
                    if (z != "undefined") {
                        z = z.mobile;
                    } else {
                        z = "-";
                    }
                    return z;
                },
                "targets": 4
            },
            {
                "render": function ( data, type, row ) {
                    var z = getPerson(linkInfo.relatedPersonList, row.personId);
                    if (z != "undefined") {
                        z = z.email;
                    } else {
                        z = "-";
                    }
                    return z;
                },
                "targets": 5
            },
            {
                "render": function ( data, type, row ) {
                    var z = row.type;
                    switch (z) {
                    case 0: z = 'Kápolnában'; break;
                    case 1: z = 'Online'; break;
                    case 2: z = 'Kápolnában - egyszer'; break;
                    case 3: z = 'Kápolnából kimaradás'; break;
                    default: z = '???';
                    }
                    return z;
                },
                "targets": 7
            }
        ]
    } );
    var filter = findGetParameter("filter");
    if ((filter != null) && (filter.length > 0)) {
        var table = $('#link').DataTable();
        table.search(filter).draw();
    }
}

function changeHistoryClick(data) {
   reBuildHistoryModal(data);
}

function reBuildHistoryModal(id) {
    var hc = $("<tbody id=\"historyContent\"/>");
    $.get('/adorationSecure/getLinkHistory/' + id , function(data) {
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
    $("#deleteButton").show();
    $('#resetChangesButton').attr('onclick', 'reBuildModal()');
    $('#editCenterTitle').text('Módosítás');
    reBuildModal();
}

function requestComplete() {
    $('#saveChangesButton').removeAttr('disabled');
}


function reBuildModal() {
    requestComplete(); //ensure availability of save button when the modal is refreshed
    //get and fill modal
    var retObj;
    var editId = $("#editId").val(); //filled by the button's onclick method
    $.ajax({
        type: "GET",
        url: '/adorationSecure/getLink/' + editId,
        async: false,
        success: function(response) {
            retObj = response.data;
        }
    });
    //identify the retObj
    if (typeof retObj == "undefined") return; //if link is not available, there is no point to rebuild
    var personObj = retObj.relatedPersonList[0];
    retObj = retObj.linkList[0];
    $("#adoratorName").text("Adoráló azonosítója (" + personObj.name + ")");
    $("#newAdorator").val(retObj.personId);
    $("#newDay").val((getDay(retObj.hourId) * 24).toString());
    $("#newHour").val(getHourName(retObj.hourId).toString());
    $("#newOnline").prop("checked", retObj.type == 1);
    $("#newPriority").val(retObj.priority);
    $("#newAdminComment").val(retObj.adminComment);
    $("#newPublicComment").val(retObj.publicComment);
}

function deleteLink() {
    showConfirm("Megerősítés kérdés", "Biztosan törölni akarja ezt az óra hozzárendelést - végleg?", function () { deleteLinkConfirmOk() });
    }

function deleteLinkConfirmOk() {
    var entityId = $("#editId").val(); //filled by the button's onclick method
    var req = {
        entityId : entityId,
    };
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/deletePersonCommitment',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(req),
        dataType: 'json',
        success : processEntityDeleted,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
        complete : requestComplete,
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        showAlert("Hiba történt!", obj.entityUpdate);
    });
}

function processEntityDeleted() {
    console.log("---=== Entity DELETED, going back to Entity list... ===---");
    processEntityUpdated();
}

function processEntityUpdated() {
    var table = $('#link').DataTable();
    var filter = table.search(); //preserve filter
    var path = "/adorationSecure/links";
    if (typeof filter != "undefined" && filter.length > 0) {
        path = path + "?filter=" + filter;
    }
    window.location = path;
}

function addClick() {
    requestComplete(); //ensure availability of save button when the modal is refreshed
    $("#editId").val(0);
    $("#adoratorName").text("Adoráló azonosítója");
    $("#newAdorator").val("");
    $("#newDay").val("0");
    $("#newHour").val("0");
    $("#newOnline").prop("checked", false);
    $("#newPriority").val("1");
    $("#newAdminComment").val("");
    $("#newPublicComment").val("");
    $('#editCenterTitle').text('Új óra rögzítése');
    $("#deleteButton").hide();
}

function saveNew() {
    var b = {}; //empty object
	//validations + prepare object
	var eStr = "";
    var bad = 0;
    //let v;
    b.id = $("#editId").val(); //filled by the button's onclick method, should be 0 in case of new, or the ID of the link in case of update
    b.hourId = parseInt($("#newDay").find(":selected").val()) + parseInt($("#newHour").val());
    b.personId = $("#newAdorator").val();
    b.priority = $("#newPriority").val();
    b.adminComment = $("#newAdminComment").val();
    b.publicComment = $("#newPublicComment").val();
    if ($("#newOnline").prop("checked").toString() == "true") {
        b.type = 1;
    } else {
        b.type = 0;
    }
    // b is ready
    //validation
    if (b.personId <= 0) {
        bad = 1;
        eStr = "Adoráló megadása kötelező!";
    }
    if (b.priority == "") {
        bad = 1;
        eStr = "Prioritás megadása kötelező!";
    }
    //validation done (cannot validate more at client level)
    if (bad == 1) {
        showAlert("Figyelem!", eStr);
        return;
    }
    //save
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    //beforeRequest();
    $.ajax({
        url : '/adorationSecure/updatePersonCommitment',
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
