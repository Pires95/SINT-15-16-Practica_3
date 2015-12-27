<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
    <title>Servicio de consulta de información musical</title>
    <link rel='stylesheet' href='iml.css'>
    <link href="https://fonts.googleapis.com/css?family=Montserrat" rel="stylesheet" type="text/css">
</head>
<body>
<div>
    <h1>Bienvenido al servicio de consulta de informacíon musical (Marcos Pires)</h1>
    <h2>Seleccione una de las siguientes opciones</h2>
</div>

    Se han producido los siguientes errores:
        <div class="errrores">

            <c:forEach items="${errorBean.error}" var="tipoError">
                <br>
                <c:out value="${tipoError}"></c:out>
                <br>
            </c:forEach>
        </div>

<form method="post"  name="form" action="?fase1">
    <input type="hidden" name="fase" value="1">
    <input type="radio" name="consulta" value="lista" checked>Lista. <br>
    <input type="radio" name="consulta" value="estilo">Canciones por género. <br>
    <br>
    <input type="submit" value="Enviar">
</form>

<footer>Creada por Marcos Pires Filgueira</footer>
</body>
</html>