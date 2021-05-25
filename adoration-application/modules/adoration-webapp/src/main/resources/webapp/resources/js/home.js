$(document).ready(function() {
    $("#nav-home").addClass("active");
    setupMenu();
    setupCoverage();
    $("#adorationVersion").text(loggedInUserInfo.adorationApplicationVersion);
});
