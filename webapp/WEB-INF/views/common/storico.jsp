<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="../fragments/header.jsp" />
<jsp:include page="../fragments/navbar.jsp" />

<main class="storico-orders-main">
    <h1>I tuoi Ordini d'Epoca</h1>
    <p class="subtitle">Traccia la cronologia dei cimeli storici che si sono uniti al tuo Garage.</p>

    <c:if test="${not empty errorMessage}">
        <div class="alert-error">
            <p><c:out value="${errorMessage}" /></p>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty listaOrdini}">
            <div class="storico-vuoto">
                <p>Non hai ancora effettuato nessun ordine su Racing Relics.</p>
                <a href="${pageContext.request.contextPath}/Catalogo" class="btn-back">Inizia a Esplorare</a>
            </div>
        </c:when>
        
        <c:otherwise>
            <table class="storico-table">
                <thead>
                    <tr>
                        <th>ID Ordine</th>
                        <th>Data Transazione</th>
                        <th>Stato Configurazione</th>
                        <th>Totale Investito</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ordine" items="${listaOrdini}">
                        <tr>
                            <td><strong>#<c:out value="${ordine.idOrdine}"/></strong></td>
                            <td>
                                <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy - HH:mm" />
                            </td>
                            <td>
                                <span class="badge-stato ${ordine.stato.toLowerCase()}">
                                    <c:out value="${ordine.stato}"/>
                                </span>
                            </td>
                            <td class="td-price">${mappaTotali[ordine.idOrdine]} €</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="../fragments/footer.jsp" />