<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="main-navbar">
    <div class="navbar-container">
        <a href="${pageContext.request.contextPath}/Catalogo" class="navbar-logo">
            RACING <span class="text-red">RELICS</span>
        </a>

        <form action="${pageContext.request.contextPath}/Catalogo" method="GET" class="navbar-search-form">
            <input type="text" name="q" value="<c:out value='${paramQuery}'/>" placeholder="Cerca reperto, pilota, scuderia..." class="navbar-search-input">
            <button type="submit" class="navbar-search-btn" title="Cerca">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
            </button>
        </form>

        <ul class="navbar-links">
            <li>
                <a href="${pageContext.request.contextPath}/Catalogo">Catalogo Reperti</a>
            </li>

            <li>
                <a href="${pageContext.request.contextPath}/Carrello" class="nav-cart">
                    Carrello
                    <c:if test="${not empty sessionScope.carrello && sessionScope.carrello.size() > 0}">
                        <span class="cart-badge">${sessionScope.carrello.size()}</span>
                    </c:if>
                </a>
            </li>

            <c:if test="${empty sessionScope.utenteLoggato}">
                <li>
                    <a href="${pageContext.request.contextPath}/Login" class="btn-nav-login">Accedi</a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/Registrazione" class="btn-nav-register">Registrati</a>
                </li>
            </c:if>

            <c:if test="${not empty sessionScope.utenteLoggato}">
                <c:if test="${sessionScope.utenteLoggato.ruolo == 'ADMIN' || sessionScope.utenteLoggato.ruolo == 'admin'}">
                    <li>
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-admin-link">Dashboard Admin</a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.utenteLoggato.ruolo != 'ADMIN' && sessionScope.utenteLoggato.ruolo != 'admin'}">
                    <li>
                        <a href="${pageContext.request.contextPath}/Ordini">Il mio Garage</a>
                    </li>
                </c:if>
                <li>
                    <a href="${pageContext.request.contextPath}/Logout" class="btn-nav-logout">Esci</a>
                </li>
            </c:if>
        </ul>
    </div>
</nav>