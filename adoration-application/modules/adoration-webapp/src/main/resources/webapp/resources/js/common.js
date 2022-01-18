var loggedInUserInfo;

function setupMenu() {
    jQuery.ajaxSetup({async:false});
    $.get('/adoration/getLoggedInUserInfo', function(data) {
        loggedInUserInfo = data.loggedInUserInfo;
        if (loggedInUserInfo.isLoggedIn) {
            $("#loggedInUserLegend").text(loggedInUserInfo.languagePack["common.loggedInUserLegend"] + loggedInUserInfo.loggedInUserName);
            $("#nav-information").show();
            $("#nav-exit").show();
        } else {
            $("#loggedInUserLegend").text("");
            $("#nav-login").show();
        }
        if (loggedInUserInfo.isRegisteredAdorator) {
            $("#nav-ador-list").show();
        } else {
            $("#nav-ador-registration").show();
        }
        if (loggedInUserInfo.isAdoratorAdmin) {
            $("#nav-application-log").show();
        }
    });
    jQuery.ajaxSetup({async:true});
}

function getReadableLanguageCode(code) {
    var z;
    switch (code) {
        case "hu": z = 'magyar'; break;
        case "en": z = 'English'; break;
        case "it": z = 'Italiano'; break;
        case "es": z = 'spanyol'; break;
        case "fr": z = 'francia'; break;
        case "ge": z = 'német'; break;
        default: z = '???';
    }
    return z;
}

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/; //NOSONAR
    return re.test(String(email).toLowerCase());
}

function findGetParameter(parameterName) {
    var result = null,
        tmp = [];
    var items = location.search.substr(1).split("&");
    for (var index = 0; index < items.length; index++) {
        tmp = items[index].split("=");
        if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
    }
    return result;
}

function getDayNameLocalized(hourId, dayNames) {
    var x = getDay(hourId);
    return dayNames[x];
}

function getDayName(hourId) {
    var dayNames = [loggedInUserInfo.languagePack["common.day.0"], loggedInUserInfo.languagePack["common.day.1"], loggedInUserInfo.languagePack["common.day.2"], loggedInUserInfo.languagePack["common.day.3"], loggedInUserInfo.languagePack["common.day.4"], loggedInUserInfo.languagePack["common.day.5"], loggedInUserInfo.languagePack["common.day.6"]]
    var x = getDay(hourId);
    return dayNames[x];
}

/**
 * Get Day ID (0..6) from hourID.
*/
function getDay(hourId) {
    var x = Math.floor(hourId / 24);
    return x;
}

function getHourName(hourId) {
    return hourId % 24;
}

function getPerson(list, personId) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id == personId) {
            return list[i];
        }
    }
    return null;
}

function getCoordinator(list, hourInDayId) {
    for (var i = 0; i < list.length; i++) {
        if (parseInt(list[i].coordinatorType) == hourInDayId) {
            return list[i];
        }
    }
    return null;
}

//replacing alert
var alertModal;
var alertSpan;
var alertAfterCloseFunction;

function showAlert(title, message, afterCloseFunction) {
    $("#commonAlertTitle").text(title);
    $("#commonAlertBody").text(message);
    alertModal = document.getElementById("commonAlertModal");
    alertSpan = document.getElementsByClassName("commonAlertClose")[0];
    alertAfterCloseFunction = afterCloseFunction;
    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == alertModal) {
        hideAlert();
      }
    }
    alertModal.style.display="block";
}

function hideAlert() {
    alertModal.style.display="none";
    if (typeof alertAfterCloseFunction != "undefined") {
        alertAfterCloseFunction();
    }
}

//replacing confirm
var confirmModal;
var confirmSpan;
var confirmAfterCloseFunctionOk;
var confirmAfterCloseFunctionCancel;

function showConfirm(title, message, afterCloseFunctionOk, afterCloseFunctionCancel) {
    $("#commonConfirmTitle").text(title);
    $("#commonConfirmBody").text(message);
    confirmModal = document.getElementById("commonConfirmModal");
    confirmSpan = document.getElementsByClassName("commonConfirmClose")[0];
    confirmAfterCloseFunctionOk = afterCloseFunctionOk;
    confirmAfterCloseFunctionCancel = afterCloseFunctionCancel;
    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == confirmModal) {
        hideConfirmCancel();
      }
    }
    confirmModal.style.display="block";
}

function hideConfirmCancel() {
    confirmModal.style.display="none";
    if (typeof confirmAfterCloseFunctionCancel != "undefined") {
        confirmAfterCloseFunctionCancel();
    }
}

function hideConfirmOk() {
    confirmModal.style.display="none";
    if (typeof confirmAfterCloseFunctionOk != "undefined") {
        confirmAfterCloseFunctionOk();
    }
}

function reloadLocation() {
    location.reload();
}

function toggleLanguageBar() {
    $("#languageBar").toggle();
}

function setLanguage(language) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    jQuery.ajax({
        url: '/adoration/setLanguage?language=' + language,
        async: false,
        beforeSend : function(request) {
                    request.setRequestHeader(header, token);
                }
        });
    window.location.href = window.location.href.split('#')[0];
}