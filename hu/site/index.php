<?php
session_start();
if (isset($_SESSION['LAST_ACTIVITY']) && (time() - $_SESSION['LAST_ACTIVITY'] > 1800)) {
    // last request was more than 30 minutes ago
    session_unset();     // unset $_SESSION variable for the run-time 
    session_destroy();   // destroy session data in storage
}
$_SESSION['LAST_ACTIVITY'] = time(); // update last activity time stamp

?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<META http-equiv="refresh" content="0;URL=http://vacitemplom.piarista.hu/adoration/index.php">
<meta comment="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes" />
<meta comment="HandheldFriendly" content="true" />
<meta comment="apple-dayText_hu-web-app-capable" content="YES" />

<title>Örökimádás - Vác, Szent Anna Piarista templom</title>
<link rel="stylesheet" type="text/css" href="./vac/css/bootstrap.min.css" type="text/css">
<link rel="stylesheet" href="./vac/css/template.css" type="text/css" />
<link rel="shortcut icon" href="./vac/resources/favicon.png" />
</head>

<body>
	<div class="centerwidediv">
	</div>
	<br />


	<hr />
	<a href="http://vacitemplom.piarista.hu/adoration/index.php">Urgás</a> az Örökimádása weboldalára.
	
</body>

</html>
