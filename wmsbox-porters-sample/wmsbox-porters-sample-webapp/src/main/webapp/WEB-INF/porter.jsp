<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>ASR Zaragoza 1.0.0</title>
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
                        <input type="submit" class="button" name="login" value="Acceder" />
                    </div>
                </form>
            </c:if>
            <c:if test="${sessionScope.porter != null && sessionScope.operation == null}">
                <c:if test="${endMessage != null}">
                    <div id="endMessage">${endMessage.text}</div>
                </c:if>
                <form method="post">
                    <c:if test="${error != null}">
                        <div class="error">${error}</div>
                    </c:if>
                    <c:if test="${sessionScope.operation.error != null}">
                        <div class="error">${sessionScope.operation.error.text}</div>
                    </c:if>
                    <ul id="operations">
                        <c:forEach var="operationType" items="${operationTypes}">
                            <li><input type="submit" class="button" name="operationType" value="${operationType}" /></li>
                        </c:forEach>
                    </ul>
                    <input name="code" type="text" autofocus="true" />
                    <input type="submit" class="button" name="operationType" value="&gt;" />
                </form>
            </c:if>
            <c:if test="${sessionScope.operation != null}">
                <div id="info">
                    <c:forEach var="info" items="${sessionScope.operation.messages}">
                        <div class="blockInfo">${info.text}</div>
                    </c:forEach>
                </div>
                <c:if test="${endMessage != null}">
                    <div id="endMessage">${endMessage.text}</div>
                </c:if>
                <form method="post">
                    <c:if test="${sessionScope.operation.error != null}">
                        <div class="error">${sessionScope.operation.error.text}</div>
                    </c:if>
                    <c:if test="${input != null}">
                        <label for="input">${input.text}</label>
                        <c:if test="${input.mode == 'NUMBER'}">
                            <button onclick="document.forms[0].input.value--; return false;">-</button>
                            <input name="input" autofocus="true" class="integer" size="8" value="${input.initialValue}"/>
                            <button onclick="document.forms[0].input.value++; return false;">+</button>
                        </c:if>
                        <c:if test="${input.mode != 'NUMBER'}">
                            <input name="input" autofocus="true" value="${input.initialValue}"/>
                        </c:if>
                        <input name="inputKey" type="hidden" value="${input.key}" />
                        <input type="submit" class="button" name="inputButton" value="&gt;" />
                    </c:if>
                    <c:if test="${confirm != null}">
                        <div class="confirm">${confirm.text}</div>
                        <div class="buttons">
                            <input name="actionKey" type="hidden" value="${confirm.key}"/>
                            <input type="submit" class="button" name="confirm" value="SI" />
                            <input type="submit" class="button" name="confirm" value="NO" />
                        </div>
                    </c:if>
                    <c:if test="${confirmText == null}">
                        <div class="buttons">
                            <c:forEach var="button" items="${buttons}">
                                <input type="submit" class="button" name="actionKey" value="${button.text}" />
                            </c:forEach>
                        </div>
                    </c:if>
                </form>
            </c:if>
        </div>
    </body>
</html>