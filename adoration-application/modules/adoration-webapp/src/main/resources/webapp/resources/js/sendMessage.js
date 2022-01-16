function msgClick() {
    //cleaning up the message text
    $("#emailOrPhone").val("");
    $("#messageContent").val("");
    enableButtons();
    grecaptcha.reset();
}

function sendMessage() {
    var b = {}; //empty object
    b.info = $("#emailOrPhone").val();
    b.text = $("#messageContent").val();
    b.captcha = grecaptcha.getResponse();
    //verification
    var eStr = "";
    var bad = 0;
    var patt = /^[0-9a-zA-ZöüóőúéáűíÖÜÓŐÚÉÁŰÍ\.\!\?\,\-\n\: ]*$/
    if (!patt.test(b.info)) {
        bad = 1;
        eStr = loggedInUserInfo.languagePack["sendMessage.js.badInfo"];
    }
    if (!patt.test(b.text)) {
        bad = 1;
        eStr = loggedInUserInfo.languagePack["sendMessage.js.badMsg"];
    }
    if (b.captcha.length == 0 ) {
        showAlert(loggedInUserInfo.languagePack["common.warning"], loggedInUserInfo.languagePack["sendMessage.js.badCaptcha"]);
        return;
    }
    if (bad > 0) {
        showAlert(loggedInUserInfo.languagePack["sendMessage.js.msgIssue"], eStr, function () {window.scrollTo(0, 0)});
        return;
    }
    //everything is ok, send registration request
    $('#cancelButton').attr('disabled', 'disabled');
    $('#sendButton').attr('disabled', 'disabled');
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/messageToCoordinator',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(b),
        dataType: 'json',
        success : dismissModal,
        beforeSend : function(request) {
            if (token.length > 0) {
            request.setRequestHeader(header, token);
            }
        },
        complete : enableButtons,
    }).fail( function(xhr, status) {
        showAlert(loggedInUserInfo.languagePack["common.warning"], loggedInUserInfo.languagePack["sendMessage.js.failed"],
            function () {window.location.pathname = "/adorationSecure/information"});
        return;
    });
}

function enableButtons() {
    $('#cancelButton').removeAttr('disabled');
    $('#sendButton').removeAttr('disabled');
}

function dismissModal() {
    showAlert(loggedInUserInfo.languagePack["sendMessage.js.sent"], loggedInUserInfo.languagePack["sendMessage.js.sentDone"],
        function () {window.location.pathname = "/adorationSecure/information"});
    return;
}
