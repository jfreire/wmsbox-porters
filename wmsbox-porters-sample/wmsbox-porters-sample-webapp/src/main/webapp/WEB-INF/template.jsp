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
                <li class="app">ASR Zaragoza 1.0.0</li>
                <c:if test="${sessionScope.porter != null}">
                    <li class="user">${sessionScope.porter}</li>
                    <c:forEach var="navOption" items="${navOptions}">
                        <li><a href="?nav=${navOption.key}">${navOption.label}</a></li>
                    </c:forEach>
                    <c:if test="${sessionScope.operation != null}">
                        <li><a href="?nav=cancel">Cancelar operaci&oacute;n actual</a></li>
                    </c:if>
                    <li><a href="?nav=logout">Salir</a></li>
                </c:if>
            </ul>
        </div>
        <div class="content">
            <c:if test="${sessionScope.porter == null}">
                <form method="post">
                    <c:if test="${error != null}">
                        <div class="error">${error}</div>
                    </c:if>
                    <label for="porter">User</label>
                    <input name="porter" type="text" autofocus="true" />
                    <label for="password">Password</label>
                    <input name="password" type="password" />
                    <div class="buttons">
                        <button name="button" value="login">Acceder</button>
                    </div>
                </form>
            </c:if>
            <c:if test="${sessionScope.porter != null && sessionScope.operation == null}">
                <form method="post">
                    <c:if test="${error != null}">
                        <div class="error">${error}</div>
                    </c:if>
                    <ul id="operations">
                        <c:forEach var="operationType" items="${operationTypes}">
                            <li><button name="operationType" value="${operationType}">${operationType}</button></li>
                        </c:forEach>
                    </ul>
                    <input name="code" type="text" autofocus="true" />
                    <button name="operationType" value="search">&gt;</button>
                </form>
            </c:if>
            <c:if test="${sessionScope.operation != null}">
                <div id="info">
                    <c:forEach var="info" items="${sessionScope.operation.messages}">
                        <div class="blockInfo">${info.text}</div>
                    </c:forEach>
                </div>
                <form method="post">
                    <c:if test="${sessionScope.operation.error != null}">
                        <div class="error">${sessionScope.operation.error.text}</div>
                    </c:if>
                    <c:if test="${inputLabel != null}">
                        <label for="input">${inputLabel}</label>
                        <c:if test="${inputMode == 'integer'}">
                            <button onclick="document.forms[0].input.value--; return false;">-</button>
                            <input name="input" autofocus="true" class="integer" size="8" value="${inputDefaultValue}"/>
                            <button onclick="document.forms[0].input.value++; return false;">+</button>
                        </c:if>
                        <c:if test="${inputMode != 'integer'}">
                            <input name="input" autofocus="true" value="${inputDefaultValue}"/>
                        </c:if>
                        <input name="inputKey" type="hidden" value="${inputKey}"/>
                        <button name="actionKey" value="input">&gt;</button>
                    </c:if>
                    <c:if test="${confirmText != null}">
                        <div class="confirm">${confirmText}</div>
                        <div class="buttons">
                            <input name="actionKey" type="hidden" value="${confirmKey}"/>
                            <button name="confirm" value="si">SI</button>
                            <button name="confirm" value="no">NO</button>
                        </div>
                    </c:if>
                    <c:if test="${confirmText == null}">
                        <div class="buttons">
                            <c:forEach var="button" items="${buttons}">
                                <button name="actionKey" value="${button.key}">${button.text}</button>
                            </c:forEach>
                        </div>
                    </c:if>
                </form>
            </c:if>
        </div>
    </body>
</html>