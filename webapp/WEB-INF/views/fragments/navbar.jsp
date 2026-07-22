<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="main-navbar">
    <div class="navbar-container">
        <a href="${pageContext.request.contextPath}/Catalogo" class="navbar-logo">
            RACING <span class="text-red">RELICS</span>
        </a>

        <ul class="navbar-links">
            <li>
                <a href="${pageContext.request.contextPath}/Catalogo">Catalogo Reperti</a>
            </li>

            <c:if test="${empty sessionScope.utenteLoggato || sessionScope.utenteLoggato.ruolo == 'REGISTRATO'}">
                <li>
                    <a href="${pageContext.request.contextPath}/Carrello" class="nav-cart">
                        Carrello
                        <c:if test="${not empty sessionScope.carrello && sessionScope.carrello.size() > 0}">
                            <span class="cart-badge">${sessionScope.carrello.size()}</span>
                        </c:if>
                    </a>
                </li>
            </c:if>

            <c:if test="${empty sessionScope.utenteLoggato}">
                <li>
                    <a href="${pageContext.request.contextPath}/Login" class="btn-nav-login">Accedi</a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/Registrazione" class="btn-nav-register">Registrati</a>
                </li>
            </c:if>

            <c:if test="${not empty sessionScope.utenteLoggato && sessionScope.utenteLoggato.ruolo == 'REGISTRATO'}">
                <li>
                    <a href="${pageContext.request.contextPath}/Ordini">Il mio Garage</a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/Logout" class="btn-nav-logout">Esci</a>
                </li>
            </c:if>

            <c:if test="${not empty sessionScope.utenteLoggato && sessionScope.utenteLoggato.ruolo == 'ADMIN'}">
                <li>
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-admin-link">Dashboard Admin</a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/Logout" class="btn-nav-logout">Esci</a>
                </li>
            </c:if>
        </ul>
    </div>
</nav>