<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="../fragments/header.jsp" />
<jsp:include page="../fragments/navbar.jsp" />

<main class="login-main">
    <div class="login-box">
        <div class="login-header">
            <h2>Accedi a Racing Relics</h2>
            <p>Inserisci le tue credenziali per accedere al tuo Garage o al Pannello Admin</p>
        </div>

        <c:if test="${not empty errors}">
            <div class="alert-error">
                <c:forEach var="error" items="${errors}">
                    <p><c:out value="${error}" /></p>
                </c:forEach>
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/Login" method="POST" class="login-form">
            <div class="form-group">
                <label for="email">Indirizzo Email</label>
                <input type="email" id="email" name="email" required placeholder="lewis.hamilton@mercedes.com">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required placeholder="••••••••">
            </div>

            <button type="submit" class="btn-submit">Ingrana la marcia</button>
        </form>
    </div>
</main>

<jsp:include page="../fragments/footer.jsp" />