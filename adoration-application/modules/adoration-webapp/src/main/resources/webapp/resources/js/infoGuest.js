$(document).ready(function() {
    $("#nav-information").addClass("active");
    jQuery.ajaxSetup({async:false});
    setupMenu();
    jQuery.ajaxSetup({async:true});
    getInformation();
});

function getInformation() {
    $.get('/adorationSecure/getGuestInformation', function(data) {
        var information = data.data;
        if (typeof information == "undefined" || information == null || information.error != null) {
            //something was wrong with either the server or with the request, let's go back
            window.location.pathname = "/adoration/";
            return;
        }
        //we have something to show
        var g;
        if (information.isGoogle) {
            $("#noGoogle").hide();
            $("#yesGoogle").show();
            $("#nameGoogle").empty();
            $("#emailGoogle").empty();
            g = $("<div><b>" + loggedInUserInfo.languagePack["common.name"] + ": </b>" + information.nameGoogle + "</div>");
            $("#nameGoogle").append(g);
            g = $("<div><b>Email: </b>" + information.emailGoogle + "</div>");
            $("#emailGoogle").append(g);
        } else {
            $("#noGoogle").show();
            $("#yesGoogle").hide();
        }
        if (information.isFacebook) {
            $("#noFacebook").hide();
            $("#yesFacebook").show();
            $("#nameFacebook").empty();
            $("#emailFacebook").empty();
            g = $("<div><b>" + loggedInUserInfo.languagePack["common.name"] + ": </b>" + information.nameFacebook + "</div>");
            $("#nameFacebook").append(g);
            g = $("<div><b>Email: </b>" + information.emailFacebook + "</div>");
            $("#emailFacebook").append(g);
        } else {
            $("#noFacebook").show();
            $("#yesFacebook").hide();
        }
        $("#socialServiceUsed").empty();
        g = $("<div><b>" + loggedInUserInfo.languagePack["guestinfo.usedLogin"] + ": </b>" + information.socialServiceUsed + "</div>");
        $("#socialServiceUsed").append(g);
        $("#status").empty();
        g = $("<div><strong>" + loggedInUserInfo.languagePack["guestinfo.status"] + ": </strong>" + information.status + "</div>");
        $("#status").append(g);
        $("#socialId").val(information.id);
        //show leadership
        $("#yesLeadership").empty();
        if (information.leadership.length > 0) {
            //has leadership info
            $("#noLeadership").hide();
            $("#yesLeadership").show();
            var tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">" + loggedInUserInfo.languagePack["guestinfo.coordinator"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.name"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.phone"] + ":</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesLeadership").append(tr);
            for (var i = 0; i < information.leadership.length; i++) {
                var coordinator = information.leadership[i];
                if (parseInt(coordinator.coordinatorType) >= 48) { //only for general and spiritual coordinators
                    tr = $("<tr/>");
                    if (i % 2 == 0) {
                        tr.addClass("evenInfo");
                    } else {
                        tr.addClass("oddInfo");
                    }
                    tr.append($("<td class=\"infoTable\">" + coordinator.coordinatorTypeText
                        + "</td><td class=\"infoTable\">" + coordinator.personName
                        + "</td><td class=\"infoTable\">" + coordinator.phone
                        + "</td><td class=\"infoTable\">" + coordinator.eMail + "</td>"));
                    $("#yesLeadership").append(tr);
                }
            }
        } else {
            //has no leadership
            $("#noLeadership").show();
            $("#yesLeadership").hide();
        }
    });
    //show downloads - if any
    if (loggedInUserInfo.isRegisteredAdorator) {
            $("#downloads").show();
            if (loggedInUserInfo.isDailyCoordinator) {
                $("#forDc").show();
            }
            if (loggedInUserInfo.isHourlyCoordinator) {
                $("#forHc").show();
            }
            $("#forStdA").show();
    }
}
