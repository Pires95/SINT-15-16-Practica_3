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
Consulta 2, Año-><%=(String)session.getAttribute("anho")%>, Álbum-><%=(String)session.getAttribute("album")%>
<form method="POST" action="?fase4">
    <input type="hidden" name="fase" value="2111">
    <c:forEach items="${resultadoBean.resultados}" var="result">
        <input type="radio" name="estilos" value="<c:out value="${result}"></c:out>"> <c:out value="${result}"></c:out><br>
    </c:forEach>
    <input type="radio" name="estilos" value="todos" checked>Todos<br>
    <input type="submit" value="Enviar">
    <input type='submit' value='Inicio' onclick='form.fase.value=0'>
    <input type='submit' value='Atras' onclick='form.fase.value=<%=((String)session.getAttribute("fase")).substring(0,2)%>'><br>
</form>

<footer>Creada por Marcos Pires Filgueira</footer>
</body>
</html>