$(document).ready(function() {
    $("#nav-ador-live").addClass("active");
    $("#suggestLogin").hide();
    jQuery.ajaxSetup({async:false});
    setupMenu();
    jQuery.ajaxSetup({async:true});
    prepareInfo();
    setupLiveCommunication();
    timer = window.setInterval("heartBeat()", 15000); //15 sec
    redirectAsNecessary();
});

function prepareInfo() {
    //if not logged in -> suggest to log in / if logged in - be happy
    if (typeof loggedInUserInfo != "undefined") {
        if (!loggedInUserInfo.isLoggedIn) {
            //not logged in
            $("#suggestLogin").show();
        }
    } else { //not logged in
        $("#suggestLogin").show();
    }
}

function redirectAsNecessary() {
    //if logged in - be happy and redirect
    if (typeof loggedInUserInfo != "undefined") {
        if (loggedInUserInfo.isLoggedIn) {
            //user logged in so can redirect to live site
            window.location.href = "https://orokimadas-vac.click2stream.com/";
        }
    }
}

var timer;
var hashCode;
function setupLiveCommunication() {
    $.ajax({
        type: "GET",
        url: "/adoration/registerLiveAdorator/",
        async: false,
        success: function(data) {
            hashCode = data.hash[0];
        }
    });
}

function heartBeat() {
    var legend = document.querySelector('legend');
    if ($(legend).is(':visible')) {
        console.log('HeartBeat: ', hashCode);
        $.get("/adoration/liveAdorator/" + hashCode, function(data) {});
    } else {
        console.log('Not visible');
    }
}

function isInViewport(elem) {
    var bounding = elem.getBoundingClientRect();
    return (
        bounding.top >= 0 &&
        bounding.left >= 0 &&
        bounding.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        bounding.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
}