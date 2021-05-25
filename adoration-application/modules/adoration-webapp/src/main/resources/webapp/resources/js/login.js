$(document).ready(function() {
    $("#nav-login").addClass("active");
    setupMenu();
    $("#adorationVersion").text(loggedInUserInfo.adorationApplicationVersion);
});
