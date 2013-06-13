<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">
        <link rel="stylesheet" href="css/normalize.css">
        <link rel="stylesheet" href="css/main.css">
    </head>
    <body>
        <div id="nav" onclick="this.className = this.className == 'selected' ? '' : 'selected'">
            <h1>I</h1>
            <ul>
                <li class="logo">INDITEX</li>
                <li class="app">Porters (version)</li>
                <c:if test="${sessionScope.porter != null}">
                    <li class="user">${sessionScope.porter}</li>
                    <c:forEach var="navOption" items="${navOptions}">
                        <li><a href="?nav=${navOption.key}">${navOption.label}</a></li>
                    </c:forEach>
                    <c:if test="${sessionScope.task != null}">
                        <li><a href="?nav=cancel">Cancelar</a></li>
                    </c:if>
                    <li><a href="?nav=logout">Logout</a></li>
                </c:if>
            </ul>
        </div>
        <div class="content">
            <c:if test="${sessionScope.porter == null}">
                <form method="post">
                    <c:if test="error != null">
                        <div class="error">${error}</div>
                    </c:if>
                    <label for="input">User</label>
                    <input name="porter" type="text" autofocus="true" />
                    <label for="input">Password</label>
                    <input name="password" type="password" />
                    <div class="buttons">
                        <button name="button" value="login">Acceder</button>
                    </div>
                </form>
            </c:if>
            <c:if test="${sessionScope.porter != null && sessionScope.task == null}">
                <form method="post">
                    <c:if test="${error != null}">
                        <div class="error">${error}</div>
                    </c:if>
                    <ul id="operations">
                        <c:forEach var="taskType" items="${taskTypes}">
                            <li><button name="taskType" value="${taskType}">${taskType}</button></li>
                        </c:forEach>
                    </ul>
                    <input name="code" type="text" autofocus="true" />
                </form>
            </c:if>
            <c:if test="${sessionScope.task != null}">
                <div id="info">
                    <c:forEach var="info" items="${sessionScope.task.messages}">
                        <div class="blockInfo">${info}</div>
                    </c:forEach>
                </div>
                <form method="post">
                    <c:if test="${error != null}">
                        <div class="error">${error}</div>
                    </c:if>
                    <c:if test="${inputLabel != null}">
                        <label for="input">${inputLabel}</label>
                        <input name="input" autofocus="true" value="${inputDefaultValue}"/>
                        <button name="actionKey" value="inputOk">&gt;</button>
                    </c:if>
                    <div class="buttons">
                        <c:forEach var="button" items="${buttons}">
                            <button name="actionKey" value="${button}">${button}</button>
                        </c:forEach>
                    </div>
                </form>
            </c:if>
        </div>
    </body>
</html>