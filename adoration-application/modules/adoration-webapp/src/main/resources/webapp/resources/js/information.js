$(document).ready(function() {
    $("#nav-information").addClass("active");
    jQuery.ajaxSetup({async:false});
    setupMenu();
    jQuery.ajaxSetup({async:true});
    getInformation();
});

function getInformation() {
    $("#downloads").hide();
    $("#forDc").hide();
    $("#forHc").hide();
    $("#forStdA").hide();
    $.get('/adorationSecure/getInformation', function(data) {
        var information = data.data;
        if (typeof information == "undefined" || information == null || information.error != null) {
            //something was wrong with either the server or with the request, let's go back
            window.location.pathname = "/adoration/";
            return;
        }
        //we have something to show
        $("#name").text(loggedInUserInfo.languagePack["information.yourName"] + ": " + information.name);
        $("#status").text(loggedInUserInfo.languagePack["information.yourStatus"] + ": " + information.status);
        $("#adoratorId").text(loggedInUserInfo.languagePack["information.yourId"] + ": " + information.id);
        //show offered hours
        $("#yesOfferedHours").empty();
        var tr;
        var i;
        var coordinator;
        var offeredHour;
        var person;
        if ((information.linkList != null) && (information.linkList.length > 0)) {
            //has offered hours
            $("#noOfferedHours").hide();
            $("#yesOfferedHours").show();
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\" colspan=\"3\">" + loggedInUserInfo.languagePack["information.yourHours"] + ":</th><th class=\"infoTable\" colspan=\"3\">" + loggedInUserInfo.languagePack["information.yourCoordinator"] + ":</th></tr>");
            $("#yesOfferedHours").append(tr);
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">" + loggedInUserInfo.languagePack["information.day"] + ":</th><th colspan=\"2\" class=\"infoTable\">" + loggedInUserInfo.languagePack["information.hour"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.name"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.phone"] + ":</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesOfferedHours").append(tr);
            for (i = 0; i < information.linkList.length; i++) {
                offeredHour = information.linkList[i];
                var dayName = getDayNameLocalized(offeredHour.hourId, information.dayNames);
                var hourName = getHourName(offeredHour.hourId);
                tr = $("<tr/>");
                if (i % 2 == 0) {
                    tr.addClass("evenInfo");
                } else {
                    tr.addClass("oddInfo");
                }
                var coordinatorName = "N/A";
                var phone = "N/A";
                var email = "N/A";
                if (information.leadership.length > 0) { //detect hour coordinator
                    for (var j = 0; j < information.leadership.length; j++) {
                        coordinator = information.leadership[j];
                        if (parseInt(coordinator.coordinatorType) == hourName) {
                            coordinatorName = coordinator.personName;
                            phone = coordinator.phone;
                            email = coordinator.eMail;
                        }
                    }
                } // else N/A, already set
                var hourTd;
                switch (offeredHour.type) {
                default:
                case 0: //ph
                    if (information.hoursCancelled.indexOf(offeredHour.hourId) >= 0) { //if cancelled
                        hourTd = "<td class=\"infoTable\">" + loggedInUserInfo.languagePack["information.inChapel"] + loggedInUserInfo.languagePack["information.nextMissing"] + "</td>";
                    } else {
                        hourTd = "<td class=\"infoTable\">" + loggedInUserInfo.languagePack["information.inChapel"] + " <button type=\"button\" class=\"btn btn-outline-danger btn-sm\" onclick=\"registerOneTimeMiss(" + offeredHour.hourId + ")\">" + loggedInUserInfo.languagePack["information.oneTimeMiss"] + "</button></td>";
                    }
                    break;
                case 1: //online
                    hourTd = "<td class=\"infoTable\">" + loggedInUserInfo.languagePack["information.onlineHour"] + "</td>";
                    break;
                case 2: //one time on
                    hourTd = "<td class=\"infoTable\">" + loggedInUserInfo.languagePack["information.oneTimeHour"] + " <button type=\"button\" class=\"btn btn-outline-danger btn-sm\" onclick=\"unRegisterOneTimeAdoration(" + offeredHour.id + ")\">" + loggedInUserInfo.languagePack["information.cancelHour"] + "</button></td>";
                    break;
                case 3: //one time off
                    hourTd = "<td class=\"infoTable\">" + loggedInUserInfo.languagePack["information.cancelled"] + " <button type=\"button\" class=\"btn btn-outline-danger btn-sm\" onclick=\"unRegisterOneTimeMiss(" + offeredHour.id + ")\">" + loggedInUserInfo.languagePack["information.undoCancelHour"] + "</button></td>";
                    break;
                }
                tr.append($("<td class=\"infoTable\">"
                    + dayName + "</td><td class=\"infoTable\">"
                    + hourName + "</td>" + hourTd + "<td class=\"infoTable\">"
                    + coordinatorName + "</td><td class=\"infoTable\">"
                    + phone + "</td><td class=\"infoTable\">"
                    + email +"</td>"));
                $("#yesOfferedHours").append(tr);
            }
        } else {
            //has no offered hours
            $("#noOfferedHours").show();
            $("#yesOfferedHours").hide();
        }
        //show leadership
        $("#yesLeadership").empty();
        if (information.leadership.length > 0) {
            //has leadership info
            $("#noLeadership").hide();
            $("#yesLeadership").show();
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\" colspan=\"4\">" + loggedInUserInfo.languagePack["information.coordinatorList"] + ":</th></tr>");
            $("#yesLeadership").append(tr);
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">" + loggedInUserInfo.languagePack["information.serving"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.name"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.phone"] + ":</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesLeadership").append(tr);
            for (i = 0; i < information.leadership.length; i++) {
                coordinator = information.leadership[i];
                if (parseInt(coordinator.coordinatorType) > 23) { //only for main coordinators
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
        //show sub-leadership
        $("#yesSubLeadership").empty();
        if (information.leadership.length > 0) {
            //has leadership info
            $("#noSubLeadership").hide();
            $("#yesSubLeadership").show();
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\" colspan=\"4\">" + loggedInUserInfo.languagePack["information.hourCoordinators"] + ":</th></tr>");
            $("#yesSubLeadership").append(tr);
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">" + loggedInUserInfo.languagePack["information.hour"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.name"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.phone"] + ":</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesSubLeadership").append(tr);
            for (i = 0; i < information.leadership.length; i++) {
                coordinator = information.leadership[i];
                if (parseInt(coordinator.coordinatorType) <= 23) { //only for hour coordinators
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
                    $("#yesSubLeadership").append(tr);
                }
            }
        } else {
            //has no leadership
            $("#noSubLeadership").show();
            $("#yesSubLeadership").hide();
        }
        //show actual hour
        $("#yesAdoratorNow").empty();
        if (information.currentHourList.length > 0) {
            //has offered hours
            $("#noAdoratorNow").hide();
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.name"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.phone"] + ":</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesAdoratorNow").append(tr);
            for (i = 0; i < information.currentHourList.length; i++) {
                offeredHour = information.currentHourList[i];
                person = getPerson(information.relatedPersonList, offeredHour.personId);
                tr = $("<tr/>");
                if (i % 2 == 0) {
                    tr.addClass("evenInfo");
                } else {
                    tr.addClass("oddInfo");
                }
                tr.append($("<td class=\"infoTable\">"
                    + person.name + "</td><td class=\"infoTable\">"
                    + person.mobile + "</td><td class=\"infoTable\">"
                    + person.email +"</td>"));
                $("#yesAdoratorNow").append(tr);
            }
        } else {
            //has no adorator now
            $("#noAdoratorNow").show();
        }
        var c = getCoordinator(information.leadership, information.hourInDayNow);
        if (c != null && c.personName.length > 0) {
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\" colspan=\"3\">" + loggedInUserInfo.languagePack["information.yourCoordinator"] + ", "
                + information.hourInDayNow + " " + loggedInUserInfo.languagePack["common.hour"] + ":</th></tr>");
            $("#yesAdoratorNow").append(tr);
            tr = $("<td class=\"infoTable\">"
                + c.personName + "</td><td class=\"infoTable\">"
                + c.phone + "</td><td class=\"infoTable\">"
                + c.eMail +"</td>");
            $("#yesAdoratorNow").append(tr);
        }
        //show future hour
        $("#yesAdoratorNext").empty();
        if (information.futureHourList.length > 0) {
            //has offered hours
            $("#noAdoratorNext").hide();
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.name"] + ":</th><th class=\"infoTable\">" + loggedInUserInfo.languagePack["common.phone"] + ":</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesAdoratorNext").append(tr);
            for (i = 0; i < information.futureHourList.length; i++) {
                offeredHour = information.futureHourList[i];
                person = getPerson(information.relatedPersonList, offeredHour.personId);
                tr = $("<tr/>");
                if (i % 2 == 0) {
                    tr.addClass("evenInfo");
                } else {
                    tr.addClass("oddInfo");
                }
                tr.append($("<td class=\"infoTable\">"
                    + person.name + "</td><td class=\"infoTable\">"
                    + person.mobile + "</td><td class=\"infoTable\">"
                    + person.email +"</td>"));
                $("#yesAdoratorNext").append(tr);
            }
        } else {
            //has no adorator now
            $("#noAdoratorNext").show();
        }
        c = getCoordinator(information.leadership, information.hourInDayNext);
        if (c != null && c.personName.length > 0) {
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\" colspan=\"3\">" + loggedInUserInfo.languagePack["information.yourCoordinator"] + ", "
                + information.hourInDayNext + " " + loggedInUserInfo.languagePack["common.hour"] + ":</th></tr>");
            $("#yesAdoratorNext").append(tr);
            tr = $("<td class=\"infoTable\">"
                + c.personName + "</td><td class=\"infoTable\">"
                + c.phone + "</td><td class=\"infoTable\">"
                + c.eMail + "</td>");
            $("#yesAdoratorNext").append(tr);
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

function registerOneTimeMiss(h) {
  hour = getDayName(h) + "/" + getHourName(h) + " " + loggedInUserInfo.languagePack["common.hour"];
  showConfirm(loggedInUserInfo.languagePack["information.pleaseConfirm"], loggedInUserInfo.languagePack["information.cancelNextEvent"] + ": " + hour + "?", function () { registerOneMissConfirmOk(h) });
}

function registerOneMissConfirmOk(h) {
    var req = {
        entityId : h,
    };
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/registerOneTimeMiss',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(req),
        dataType: 'json',
        success : reloadLocation,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        showAlert(loggedInUserInfo.languagePack["common.errorOccurred"], obj.entityCreate);
    });
}

function unRegisterOneTimeAdoration(id) {
  showConfirm(loggedInUserInfo.languagePack["information.pleaseConfirm"], loggedInUserInfo.languagePack["information.doCancel"], function () { unRegisterOneTimeAdorationConfirmOk(id) });
}

function unRegisterOneTimeAdorationConfirmOk(h) {
    var req = {
        entityId : h,
    };
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/unRegisterOneTimeAdoration',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(req),
        dataType: 'json',
        success : reloadLocation,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        showAlert(loggedInUserInfo.languagePack["common.errorOccurred"], obj.entityCreate);
    });
}

function unRegisterOneTimeMiss(id) {
  showConfirm(loggedInUserInfo.languagePack["information.pleaseConfirm"], loggedInUserInfo.languagePack["information.doCancelCancel"], function () { unRegisterOneTimeMissConfirmOk(id) });
}

function unRegisterOneTimeMissConfirmOk(h) {
    var req = {
        entityId : h,
    };
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/unRegisterOneTimeMiss',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(req),
        dataType: 'json',
        success : reloadLocation,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        showAlert(loggedInUserInfo.languagePack["common.errorOccurred"], obj.entityCreate);
    });
}
