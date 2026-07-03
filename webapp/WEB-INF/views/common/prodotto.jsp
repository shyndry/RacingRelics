<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="../fragments/header.jsp" />
<jsp:include page="../fragments/navbar.jsp" />

<main class="prodotto-detail-main">
    <div class="back-to-catalog">
        <a href="${pageContext.request.contextPath}/Catalogo" class="btn-back">← Torna al Catalogo</a>
    </div>

    <section class="prodotto-detail-container">
        <div class="prodotto-detail-image">
            <img src="${pageContext.request.contextPath}/images/prodotti/${not empty prodotto.immagine ? prodotto.immagine : 'default.jpg'}" alt="<c:out value='${prodotto.nome}'/>">
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

            <p class="price-display">${prodotto.prezzo} €</p>

            <div class="description-box">
                <h2>Storia del Reperto</h2>
                <p><c:out value="${prodotto.descrizione}"/></p>
            </div>

            <form action="${pageContext.request.contextPath}/Carrello" method="POST" class="add-to-cart-form">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="idProdotto" value="${prodotto.idProdotto}">
                
                <div class="quantity-selector">
                    <label for="quantita">Quantità:</label>
                    <input type="number" id="quantita" name="quantita" value="1" min="1" max="5">
                </div>

                <button type="submit" class="btn-add-cart">Aggiungi al Garage</button>
            </form>
        </div>
    </section>
</main>

<jsp:include page="../fragments/footer.jsp" />