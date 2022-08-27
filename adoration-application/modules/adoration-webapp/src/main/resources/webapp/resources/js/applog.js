$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    setupMenu();
    setupAccess();
});

function setupAccess() {
    if (!loggedInUserInfo.isAdoratorAdmin) {
        $("#social-button").hide();
        $("#coordinator-button").hide();
        $("#audit-button").hide();
        $("#translator-button").hide();
        $("#logArea").hide();
    } else { //admin can see everything
        setupLogs();
    }
}

function setupLogs() {
  $.get('/adorationSecure/logs', function(data) {
	data.files.sort();
    var firstElement = data.files.shift();
    data.files = data.files.reverse();
    data.files.unshift(firstElement);
    for (var i = 0; i < data.files.length; i++) {
      var li = $("<li>");
      var a = $("<a href='logs/" + data.files[i] + "'>").text(data.files[i]);
      var btn = $("<a target='_blank' href='logs/" + data.files[i]
          + "?source=true'>").text("[Source]");
      li.append(a);
      li.append("     ");
      li.append(btn);
      $('#div-log-files').append(li);
    }
  });
}
