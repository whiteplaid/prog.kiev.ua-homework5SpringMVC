<%--
  Created by IntelliJ IDEA.
  User: Andrey
  Date: 01.04.2020
  Time: 20:01
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Prog.kiev.ua</title>
</head>
<body>
    <div align="center" >
        <h1> All Photos:</h1>
    </div>
    <div align="left">
        <br/>
        <form action="/delete_checked" method="post">
        <c:forEach var="id" items="${photo_ids}">
            <input type="checkbox" name="checked[]" value="${id}"> ${id}
                <img src="/photo/${id}" width="50px" height="50px"/><br/>
        </c:forEach>
            <br/>
            <input type="submit" value="Delete Photos">
        </form>
        <form action="/" method="post">
            <br/>
            <input type="submit" value="Back"/>
        </form>
    </div>
</body>
</html>
