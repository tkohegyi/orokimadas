var coverageAdoratorInfo;
var coverageAllHourInfo;
var coverageOnlineHourInfo;
var coverageDayNames;

window.addEventListener('resize', function(event){
    let intViewportWidth = window.innerWidth;
    handleSize(intViewportWidth);
});

function handleSize(width) {
    if (width < 1000) {
        if ($("#horizontalCoverage").is(":visible")) {
            $("#horizontalCoverage").hide();
        }
        if (!$("#verticalCoverage").is(":visible")) {
            $("#verticalCoverage").show();
        }
    } else {
        if (!$("#horizontalCoverage").is(":visible")) {
            $("#horizontalCoverage").show();
        }
        if ($("#verticalCoverage").is(":visible")) {
            $("#verticalCoverage").hide();
        }
    }
}

function setupCoverage() {
    $("#horizontalCoverage").hide();
    $("#verticalCoverage").hide();
    let intViewportWidth = window.innerWidth;
    handleSize(intViewportWidth);
    $.get('/adoration/getCoverageInformation', function(data) {
        var coverageInfo = data.coverageInfo;
        var hours = coverageInfo.visibleHours; // hour is 0-167,  eg: 75: 0
        coverageAllHourInfo = coverageInfo.allHours; // hour is 0-167,  eg: 75: [327, 34, 8]
        coverageMissingInfo = coverageInfo.missHours; //like allHours, but contains ppl those cannot participate
        coverageOneTimeInfo = coverageInfo.oneTimeHours; //like allHours, but contains ppl those substitute somebody
        coverageOnlineHourInfo = coverageInfo.onlineHours; // hour is 0-167,  eg: 75: [327, 34, 8]
        coverageAdoratorInfo = coverageInfo.adorators; //id - info pairs
        coverageDayNames = coverageInfo.dayNames; // eg FRIDAY: "péntek"

/*
        var days = document.getElementsByClassName('dayName');
        for (var i = 0; i < days.length; ++i) {
            var item = days[i];
            var targetDayName = item.id;
            targetDayName = targetDayName.split("-")[0];
            let command = "item.textContent = coverageDayNames." + targetDayName;
            eval(command); //NOSONAR
        }
*/
        for (i = 0; i < 168; i++) { //since we have 168 hours altogether
            item = $("#hour-" + i);
            var item2 = $("#hour-" + i + "-2");
            var value = hours[i];
            item.removeClass("oneTimeCandidate");
            item.removeClass("goodCoverage");
            item.removeClass("badCoverage");
            item.removeClass("veryBadCoverage");
            item2.removeClass("oneTimeCandidate");
            item2.removeClass("goodCoverage");
            item2.removeClass("badCoverage");
            item2.removeClass("veryBadCoverage");
            if (value == 0) {
                if (!item.hasClass("lowPriority")) {
                    item.text("2");
                    item.addClass("veryBadCoverage");
                    item.addClass("oneTimeCandidate");
                    item2.text("2");
                    item2.addClass("veryBadCoverage");
                    item2.addClass("oneTimeCandidate");
                } else {
                    item.text("");
                    item.addClass("lowPriorityColumn");
                    item2.text("");
                    item2.addClass("lowPriorityColumn");
                }
            }
            if (value == 1) {
                if (!item.hasClass("lowPriority")) {
                    item.text("1");
                    item.addClass("badCoverage");
                    item2.text("1");
                    item2.addClass("badCoverage");
                } else {
                    item.text("");
                    item.addClass("lowPriorityColumn");
                    item2.text("");
                    item2.addClass("lowPriorityColumn");
                }
            }
            if (value > 1) {
                if (!item.hasClass("lowPriority")) {
                    item.text("");
                    item.addClass("goodCoverage");
                    item2.text("");
                    item2.addClass("goodCoverage");
                } else {
                    item.text("");
                    item.addClass("lowPriorityColumn");
                    item2.text("");
                    item2.addClass("lowPriorityColumn");
                }
            }

            value = coverageMissingInfo[i];
            if (value != undefined && value.length > 0) {
                var htmlVal = "<img class=\"missingHour\" src=\"/resources/img/black-cross-th.png\"/>";
                item.html(htmlVal);
                item2.html(htmlVal);
            }

            value = coverageOneTimeInfo[i];
            if (value != undefined && value.length > 0) {
                var htmlVal = "<img class=\"oneTimeHour\" src=\"/resources/img/light-green-check-mark-th.png\"/>";
                item.html(htmlVal);
                item.removeClass("oneTimeCandidate");
                item2.html(htmlVal);
                item2.removeClass("oneTimeCandidate");
            }

            var onlineValue = coverageOnlineHourInfo[i];
            if (onlineValue.length > 0) {
                if (item.hasClass("lowPriority")) { //only online is possible, so emphasize it
                    item.removeClass("lowPriorityColumn");
                    item.addClass("exclusiveOnlineAdorator");
                    item2.removeClass("lowPriorityColumn");
                    item2.addClass("exclusiveOnlineAdorator");
                } else { //normal hour with online
                    item.addClass("onlineAdorator");
                    item2.addClass("onlineAdorator");
                }
            } else {
                item.removeClass("onlineAdorator");
                item2.removeClass("onlineAdorator");
            }
        }
    });
}

function coverageClick(h) {
    if (typeof coverageAdoratorInfo != "undefined" && coverageAdoratorInfo != null) {
        $("#coveragePopup").empty();
        var mlTimeText = loggedInUserInfo.languagePack["coverage.js.time"];
        var mlHourText = " " + loggedInUserInfo.languagePack["common.hour"];
        var rTime = $("<tr><td align=\"center\" colspan=\"2\">" + mlTimeText + getDayName(h) + "/" + getHourName(h) + mlHourText + "</td><tr/>");
        $("#coveragePopup").append(rTime);
        rTime = $("<tr><td align=\"center\" colspan=\"2\"> --------- </td><tr/>");
        $("#coveragePopup").append(rTime);
        var personArray = []; //empty object;
        var users = coverageAllHourInfo[h];
        if ((users != null) && (users.length > 0)) {
            for (var i=0; i<users.length; i++) {
                var personId = users[i];
                var personInfo = coverageAdoratorInfo[personId];
                if (typeof personInfo != "undefined") {
                    personArray.push(personInfo);
                }
            }
        }
        var counter = 1;
        var mlCommentText = loggedInUserInfo.languagePack["common.comment"] + ": ";
        var mlNameText = ", " + loggedInUserInfo.languagePack["common.name"] + ": ";
        var mlPhoneText = loggedInUserInfo.languagePack["common.phone"] + ": ";
        if (personArray.length > 0) {
            for (i=0; i<personArray.length; i++,counter++) {
                var r = $("<tr/>");
                var p = personArray[i];
                var commentContent = "";
                if (p.visibleComment.length > 0) {
                    commentContent = "<tr><td>" + mlCommentText + p.visibleComment + "</td></tr>";
                }
                var rContent = "<td><table><tbody><tr><td class=\"coverageDay goodCoverage\" align=\"center\" width=\"25px\">" + counter + "</td><td>" +
                    "<table><tbody><tr><td>ID: " + p.id + mlNameText + p.name + "</td></tr>"
                        + "<tr><td>E-mail: " + p.email + "</td></tr>"
                        + "<tr><td>" + mlPhoneText + p.mobile + "</td></tr>"
                        + commentContent
                    + "</tbody></table>"
                    + "</td></tr></tbody></table></td>";
                r.append($(rContent));
                $("#coveragePopup").append(r);
            }
        }
        //next round is about online adorators
        personArray = []; //empty object;
        users = coverageOnlineHourInfo[h];
        if ((users != null) && (users.length > 0)) {
            for (i=0; i<users.length; i++) {
                personId = users[i];
                personInfo = coverageAdoratorInfo[personId];
                if (typeof personInfo != "undefined") {
                    personArray.push(personInfo);
                }
            }
        }
        if (personArray.length > 0) {
            for (i=0; i<personArray.length; i++,counter++) {
                r = $("<tr/>");
                p = personArray[i];
                commentContent = "";
                if (p.visibleComment.length > 0) {
                    commentContent = "<tr><td>" + mlCommentText + p.visibleComment + "</td></tr>";
                }
                rContent = "<td><table><tr><td class=\"coverageDay onlineAdorator\" align=\"center\" width=\"25px\">" + counter + "</td><td>" +
                    "<table><tbody><tr><td>ID: " + p.id + mlNameText + p.name + "</td></tr>"
                        + "<tr><td>E-mail: " + p.email + "</td></tr>"
                        + "<tr><td>" + mlPhoneText + p.mobile + "</td></tr>"
                        + commentContent
                    + "</tbody></table>"
                    + "</td></tr></tbody></table></td>";
                r.append($(rContent));
                $("#coveragePopup").append(r);
            }
        }
        item = $("#hour-" + h);
        if (item.hasClass("oneTimeCandidate") && !item.hasClass("lowPriorityColumn") && !item.hasClass("plannedHour")) {
            //offer possibility of one-time adoration
            r = $("<tr/>");
            var mlCoverageOneTimeText = loggedInUserInfo.languagePack["coverage.oneTime"];
            rContent = "<td><button type=\"button\" class=\"btn btn-success\" onclick=\"registerOneTimeAdoration(" + h + ")\">" + mlCoverageOneTimeText + "</button></td>";
            r.append($(rContent));
            $("#coveragePopup").append(r);
        }
        if (!loggedInUserInfo.isRegisteredAdorator || item.hasClass("lowPriorityColumn")) {
            //don't show anything
            $(".pop").hide(500);
            $("#coveragePopup").empty();
        } else { //adorator user logged in and worth to show the info
            if (!item.hasClass("plannedHour")) {
                coverageModal.style.display="block";
            }
        }
    }
}

var coverageModal = document.getElementById("coverageModal");
var coverageSpan = document.getElementsByClassName("coverageClose")[0];

// When the user clicks on <span> (x), close the modal
coverageSpan.onclick = function() {
  coverageModal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == coverageModal) {
    coverageModal.style.display = "none";
  }
}

function registerOneTimeAdoration(h) {
  coverageModal.style.display = "none"; //hide the modal first
  var mlCommonHourText = loggedInUserInfo.languagePack["common.hour"];
  var mlCommonConfirmText = loggedInUserInfo.languagePack["common.confirmRequest"];
  var mlCoverageThisTimeText = loggedInUserInfo.languagePack["coverage.thisTime"];
  hour = getDayName(h) + "/" + getHourName(h) + " " + mlCommonHourText;
  showConfirm(mlCommonConfirmText, mlCoverageThisTimeText + hour + "?", function () { registerOneTimeConfirmOk(h) });
}

function registerOneTimeConfirmOk(h) {
    var req = {
        entityId : h,
    };
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/registerOneTimeAdoration',
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
