<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="../fragments/header.jsp" />
<jsp:include page="../fragments/navbar.jsp" />

<main class="prodotto-detail-main">
    <div class="back-to-catalog">
        <a href="${pageContext.request.contextPath}/Catalogo" class="btn-back">← Torna al Catalogo</a>
    </div>

    <section class="prodotto-detail-container">
        <div class="prodotto-detail-image">
            <img src="${pageContext.request.contextPath}/images/prodotti/${not empty prodotto.immagine ? prodotto.immagine : 'default.svg'}" 
                 alt="<c:out value='${prodotto.nome}'/>"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/prodotti/default.svg';">
        </div>

        <div class="prodotto-detail-info">
            <h1><c:out value="${prodotto.nome}"/></h1>
            
            <div class="prodotto-metadata">
                <c:if test="${not empty prodotto.scuderia}">
                    <span class="meta-tag scuderia"><strong>Scuderia:</strong> <c:out value="${prodotto.scuderia}"/></span>
                </c:if>
                <c:if test="${not empty prodotto.pilota}">
                    <span class="meta-tag pilota"><strong>Pilota:</strong> <c:out value="${prodotto.pilota}"/></span>
                </c:if>
                <c:if test="${not empty prodotto.anno && prodotto.anno != 0}">
                    <span class="meta-tag anno"><strong>Stagione:</strong> <c:out value="${prodotto.anno}"/></span>
                </c:if>
            </div>

            <p class="price-display">€ <fmt:formatNumber value="${prodotto.prezzo}" pattern="#,##0.00"/></p>

            <div class="description-box">
                <h2>Storia del Reperto</h2>
                <p><c:out value="${prodotto.descrizione}"/></p>
            </div>

            <c:choose>
                <c:when test="${prodotto.quantitaDisponibile > 0}">
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST" class="add-to-cart-form">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="idProdotto" value="${prodotto.idProdotto}">
                        
                        <div class="quantity-selector">
                            <label for="quantita">Quantità:</label>
                            <input type="number" id="quantita" name="quantita" value="1" min="1" max="${prodotto.quantitaDisponibile}">
                        </div>

                        <button type="submit" class="btn-add-cart">Aggiungi al Garage</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="out-of-stock-badge" style="padding: 12px; background: rgba(225, 6, 0, 0.15); border: 1px solid var(--red); color: var(--red); border-radius: 6px; text-align: center; font-weight: bold;">
                        Reperto Attualmente Esaurito in Magazzino
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</main>

<jsp:include page="../fragments/footer.jsp" />