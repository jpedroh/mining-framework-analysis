<%@include file="header.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <!-- Bootstrap -->
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.css" rel="stylesheet" media="screen">
    <script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <style type="text/css">
        .bs-example {
            margin: 20px;
        }
    </style>
</head>
<body>

<div class="order">
    <div class="panel-body" style="padding:15px; width:100%; margin-left: 10%">
    <div class="row">
        <div class="col-md-3">
            <h4 style="color:Blue">Mероприятие</h4>
            <form action="${pageContext.request.contextPath}/Order/setSectors.do" method="post">
                <p><select size="10" name="eventId" data-size="3" class="form-control">
                    <c:forEach items="${events}" var="evnt">
                        <c:if test="${event.id==evnt.id}">
                            <option value="${evnt.id}" onclick="this.form.submit()"
                                    selected>${evnt.description}, ${evnt.date}</option>
                        </c:if>
                        <c:if test="${event.id!=evnt.id}">
                            <option value="${evnt.id}"
                                    onclick="this.form.submit()">${evnt.description}, ${evnt.date}</option>
                        </c:if>
                    </c:forEach>
                </select></p>
            </form>
        </div>

        <div class="col-md-3">
            <h4 style="color:Blue">Сектор</h4>
            <form action="${pageContext.request.contextPath}/Order/setRow.do" method="post">
                <p><select size="10" name="sectorId" class="form-control">
                    <c:forEach items="${sectorsMap}" var="sectorEntry">
                        <c:if test="${sector.id==sectorEntry.key.id}">
                            <option value="${sectorEntry.key.id}" onclick="this.form.submit()"
                                    selected>${sectorEntry.key.name}, ${sectorEntry.key.price}
                                грн., свободно: ${sectorEntry.value}</option>
                        </c:if>
                        <c:if test="${sector.id!=sectorEntry.key.id}">
                            <option value="${sectorEntry.key.id}" onclick="this.form.submit()">${sectorEntry.key.name}
                             , ${sectorEntry.key.price} грн., свободно: ${sectorEntry.value} мест</option>
                        </c:if>
                    </c:forEach>
                </select></p>
            </form>
        </div>

        <div class="col-md-3">
            <h4 style="color:Blue">Легенда</h4>
            <form action="${pageContext.request.contextPath}/Order/Order.do" method="post">
                <p><select multiple size="10" name="Legend" >
                    <c:forEach items="${legenda}" var="leg">
                        <option value="${leg}"> ${leg}</option>
                    </c:forEach>
                </select></p>
            </form>
        </div>
    </div>
    <br>

    <div class="row">
        <div class="col-md-3">
            <h4 style="color:Blue">Ряд</h4>
            <form action="${pageContext.request.contextPath}/Order/setSeat.do" method="post">
                <p><select size="10" name="row" class="form-control">
                    <c:forEach items="${rowsMap}" var="rowEntry">
                        <c:if test="${row==rowEntry.key}">
                            <option value="${rowEntry.key}" onclick="this.form.submit()" selected>${rowEntry.key}
                                 ряд, свободно: ${rowEntry.value} мест</option>
                        </c:if>
                        <c:if test="${row!=rowEntry.key}">
                            <option value="${rowEntry.key}" onclick="this.form.submit()">${rowEntry.key}
                                ряд, свободно: ${rowEntry.value} мест</option>
                        </c:if>
                    </c:forEach>
                </select></p>
            </form>
        </div>
        <div class="col-md-3">
            <h4 style="color:Blue">Место</h4>
            <form action="${pageContext.request.contextPath}/Order/Order.do" method="post">
                <p><select multiple size="10" name="seats" class="form-control">
                    <c:forEach items="${seatsMap}" var="seatEntry">
                        <option value="${seatEntry.key}">${seatEntry.key} free: ${seatEntry.value}</option>
                    </c:forEach>
                </select>
            </form>
        </div>


    </div>
    <br>


    </div>
</div>
</body>
</html>
