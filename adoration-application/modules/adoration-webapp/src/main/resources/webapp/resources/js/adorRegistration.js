$(document).ready(function() {
    $("#nav-ador-registration").addClass("active");
    $("#suggestLogin").hide();
    jQuery.ajaxSetup({async:false});
    setupMenu();
    jQuery.ajaxSetup({async:true});
    setupCoverage();
    prepareInfo();
});

function prepareInfo() {
    //if not logged in -> suggest to log in / if logged in -> prefill fields
    if (typeof loggedInUserInfo != "undefined") {
        if (loggedInUserInfo.isLoggedIn) {
            $("#name").val(loggedInUserInfo.userName);
            $("#email").val(loggedInUserInfo.socialEmail);
        } else { //not logged in
            $("#suggestLogin").show();
        }
    } else { //not logged in
        $("#suggestLogin").show();
    }
}

function notRegisterClick() {
    showAlert("Figyelem!", "Ön végül úgy döntött, hogy mégsem jelentkezik az Örökimádásra.",
        function () {window.location.pathname = "/adoration/"});
}

function doRegisterClick() {
    //fill data
    $("#nameError").attr('style', 'display:none');
    $("#emailError").attr('style', 'display:none');
    $("#mobileError").attr('style', 'display:none');
    $("#dhcError").attr('style', 'display:none');
    var b = {}; //empty object
    b.name = $("#name").val();
    b.email = $("#email").val();
    b.mobile = $("#mobile").val();
    b.dayId = parseInt($("#daySelect").find(":selected").val());
    b.hourId = parseInt($("#hourSelect").find(":selected").val());
    b.method = parseInt($("#method").find(":selected").val());
    b.comment = $("#comment").val();
    b.coordinate = $("#coordinate").find(":selected").val();
    b.dhc = $("#dhc").find(":selected").val();
    //verification
    var eStr = "";
    var bad = 0;
    if (b.name.length == 0) {
        bad = 1;
        eStr = "Név megadása szükséges!";
        $("#nameError").removeAttr('style');
    } else {
        var patt = /^[0-9a-zA-ZöüóőúéáűíÖÜÓŐÚÉÁŰÍ\.\!\?\,\-\:\n ]*$/
        if (!patt.test(b.name)) {
            bad = 1;
            eStr = "A megadott Névben el nem fogadható karakterek is vannak, kérjük a név javítását!";
            $("#nameError").removeAttr('style');
        }
    }
    if ((b.email.length > 0) && (!validateEmail(b.email))) {
        bad = 1;
        eStr = "A megadott e-mail cím nem helyes!";
        $("#emailError").removeAttr('style');
    }
    var patt = /^[0-9\+\- ]*$/; //NOSONAR
    if ((b.mobile.length > 0) && (!patt.test(b.mobile))) {
        bad = 1;
        eStr = "A megadott telefonszám nem helyes!";
        $("#mobileError").removeAttr('style');
    }
    if (b.email.length + b.mobile.length == 0) {
        bad = 1;
        eStr = "Telefonszám és e-mail cím közül legalább az egyik megadása kötelező!";
        $("#emailError").removeAttr('style');
        $("#mobileError").removeAttr('style');
    }
    if (b.dhc.indexOf("yes") <= 0) {
        bad = 1;
        eStr = "Adatkezelési hozzájárulás nélkül a jelentkezést nem tudjuk elfogadni!";
        $("#dhcError").removeAttr('style');
    }
    if (bad > 0) {
        showAlert("Hiba az adatokban!", eStr, function () {window.scrollTo(0, 0)});
        return;
    }
    //everything is ok, send registration request
    beforeRequest();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adoration/registerAdorator',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(b),
        dataType: 'json',
        success : processRegisterSuccess,
        beforeSend : function(request) {
            if (token.length > 0) {
            request.setRequestHeader(header, token);
            }
        },
        complete : afterRequest,
    }).fail( function(xhr, status) {
        showAlert("Hiba történt!", xhr.responseText);
    });
}

function beforeRequest() {
    $('#registerButton').attr('disabled', 'disabled');
}

function afterRequest() {
    $('#registerButton').removeAttr('disabled');
}

function processRegisterSuccess() {
    window.location.pathname = "/adoration/registrationSuccess"
}
