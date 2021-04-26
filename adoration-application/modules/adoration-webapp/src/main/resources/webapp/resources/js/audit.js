$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    setupMenu();
    setupAuditTable();
});

function setupAuditTable() {
    $('#audit').DataTable( {
        "ajax": "/adorationSecure/getAuditTrailByDays/31",
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[5, 50, 100, -1], [5, 50, 100, "All"]],
        "columns": [
            { "data": "id" },
            { "data": "refId" },
            { "data": "atWhen" },
            { "data": "byWho" },
            { "data": "activityType" },
            { "data": "description" , "width": "200px" },
            { "data": "data" }
        ],
        "columnDefs": [
            {
                "className": "text-center",
                "targets": [0,1,2,3,4]
            }
        ]
    } );
}

function processEntityUpdated() {
    var path = "/adorationSecure/audit";
    window.location = path;
}
