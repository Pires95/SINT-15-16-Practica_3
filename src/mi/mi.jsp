<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
<title> Fecha y hora </title>
</head>
<body>
<b>Valor actual de fecha y hora obtenida de la bean: </B>
        ${laBean.msg} <p>
<form action="ejBean" method="post">
<input name="Enviar" type="submit" value="Actualizar">
</form>
</body>