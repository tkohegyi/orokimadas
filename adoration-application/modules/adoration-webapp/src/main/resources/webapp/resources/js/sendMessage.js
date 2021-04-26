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
        eStr = "A megadott elérhetőségben el nem fogadható karakterek is vannak, kérjük javítását!";
    }
    if (!patt.test(b.text)) {
        bad = 1;
        eStr = "Az Üzenetben el nem fogadható karakterek is vannak, kérjük fogalmazza át az üzenetet!";
    }
    if (b.captcha.length == 0 ) {
        showAlert("Figyelem!", "Kérem jelölje be a \"Nem vagyok robot\" négyzetet!");
        return;
    }
    if (bad > 0) {
        showAlert("Hiba az üzenetben!", eStr, function () {window.scrollTo(0, 0)});
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
        showAlert("Figyelem!", "Az üzenet elküldése sikertelen volt, próbálkozzon ismét.",
            function () {window.location.pathname = "/adorationSecure/information"});
        return;
    });
}

function enableButtons() {
    $('#cancelButton').removeAttr('disabled');
    $('#sendButton').removeAttr('disabled');
}

function dismissModal() {
    showAlert("Elküldve", "Az üzenet elküldése sikeres volt.",
        function () {window.location.pathname = "/adorationSecure/information"});
    return;
}
