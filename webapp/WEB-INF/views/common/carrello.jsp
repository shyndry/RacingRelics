<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="../fragments/header.jsp" />
<jsp:include page="../fragments/navbar.jsp" />

<main class="carrello-main">
    <h1>Il tuo Garage</h1>

    <c:if test="${not empty errorMessage}">
        <div class="alert-error">
            <p><c:out value="${errorMessage}" /></p>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty dettagliCarrello}">
            <div class="carrello-vuoto">
                <p>Non hai ancora aggiunto nessun reperto storico al tuo Garage.</p>
                <a href="${pageContext.request.contextPath}/Catalogo" class="btn-back">Esplora il Catalogo</a>
            </div>
        </c:when>
        
        <c:otherwise>
            <table class="carrello-table">
                <thead>
                    <tr>
                        <th>Cimelio</th>
                        <th>Prezzo Unitario</th>
                        <th>Quantità</th>
                        <th>Totale</th>
                        <th>Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${dettagliCarrello}">
                        <tr>
                            <td class="td-prodotto">
                                <img src="${pageContext.request.contextPath}/images/prodotti/${not empty item.prodotto.immagine ? item.prodotto.immagine : 'default.jpg'}" alt="<c:out value='${item.prodotto.nome}'/>" class="img-carrello">
                                <div>
                                    <h3><c:out value="${item.prodotto.nome}"/></h3>
                                    <small><c:out value="${item.prodotto.scuderia}"/> - <c:out value="${item.prodotto.anno}"/></small>
                                </div>
                            </td>
                            <td>${item.prodotto.prezzo} €</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/Carrello" method="POST" class="form-update-qty">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="idProdotto" value="${item.prodotto.idProdotto}">
                                    <input type="number" name="quantita" value="${item.quantita}" min="1" max="${item.prodotto.quantitaDisponibile}" onchange="this.form.submit()">
                                </form>
                            </td>
                            <td>${item.prezzoTotale} €</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="idProdotto" value="${item.prodotto.idProdotto}">
                                    <button type="submit" class="btn-remove">Rimuovi</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="carrello-summary">
                <h2>Totale Complessivo: <span>${totaleComplessivo} €</span></h2>
                <div class="carrello-actions">
                    <a href="${pageContext.request.contextPath}/Catalogo" class="btn-continue">Continua lo Shopping</a>
                    <a href="${pageContext.request.contextPath}/Checkout" class="btn-checkout">Procedi all'Ordine</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="../fragments/footer.jsp" />