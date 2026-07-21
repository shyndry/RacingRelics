<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="../fragments/header.jsp" />
<jsp:include page="../fragments/navbar.jsp" />

<main class="login-page-main">
    <div class="login-box">
        <div class="login-header">
            <h2>Registrazione Pilota</h2>
            <p>Crea il tuo account per accedere alla House of Auction di Racing Relics</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert-error">
                <p><c:out value="${errorMessage}" /></p>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/Registrazione" method="POST" id="registrationForm" class="login-form">
            <div class="form-group">
                <label for="nome">Nome</label>
                <input type="text" id="nome" name="nome" required placeholder="Es. Michael">
                <span id="nomeError" class="error-msg" style="color: #e10600; font-family: var(--font-mono); font-size: 0.72rem; margin-top: 4px; display: block;"></span>
            </div>

            <div class="form-group">
                <label for="cognome">Cognome</label>
                <input type="text" id="cognome" name="cognome" required placeholder="Es. Schumacher">
                <span id="cognomeError" class="error-msg" style="color: #e10600; font-family: var(--font-mono); font-size: 0.72rem; margin-top: 4px; display: block;"></span>
            </div>

            <div class="form-group">
                <label for="email">Indirizzo Email</label>
                <input type="email" id="email" name="email" required placeholder="lewis.hamilton@ferrari.com">
                <span id="emailError" class="error-msg" style="color: #e10600; font-family: var(--font-mono); font-size: 0.72rem; margin-top: 4px; display: block;"></span>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required placeholder="Minimo 6 caratteri">
                <span id="passwordError" class="error-msg" style="color: #e10600; font-family: var(--font-mono); font-size: 0.72rem; margin-top: 4px; display: block;"></span>
            </div>

            <button type="submit" class="btn-submit">Accendi i Motori</button>
        </form>
    </div>
</main>

<script src="${pageContext.request.contextPath}/scripts/registrazione.js" defer></script>
<jsp:include page="../fragments/footer.jsp" />