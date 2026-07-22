<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="../fragments/header.jsp" />
<jsp:include page="../fragments/navbar.jsp" />

<main class="main-content">
    <div class="container">
        <h1>Il tuo Garage Temporaneo</h1>
        <p class="subtitle">Riepilogo dei reperti storici F1 selezionati prima del completamento dell'ordine.</p>

        <c:if test="${param.checkoutSuccess == 'true'}">
            <div class="alert-success" style="background: rgba(63, 163, 92, 0.15); border: 1px solid var(--ok); color: #4ade80; padding: 14px 18px; border-radius: 6px; margin-bottom: 20px;">
                <p><strong>✓ Ordine Completato!</strong> Il tuo ordine è stato registrato ed è ora in elaborazione. Puoi monitorarlo nella sezione <a href="${pageContext.request.contextPath}/Ordini" style="text-decoration: underline; color: inherit; font-weight: bold;">Il mio Garage</a>.</p>
            </div>
        </c:if>

        <%-- Box per messaggi di errore sul DOM (No Alert!) --%>
        <div id="errorBox" class="alert-error" style="${empty errorMessage ? 'display: none;' : ''}">
            <c:out value="${errorMessage}" />
        </div>

        <div id="carrelloContainer">
            <c:choose>
                <c:when test="${empty dettagliCarrello}">
                    <div class="carrello-vuoto">
                        <p>Il tuo carrello è attualmente vuoto. Nessun cimelio selezionato.</p>
                        <a href="${pageContext.request.contextPath}/Catalogo" class="btn-primary">Esplora il Catalogo</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="carrello-table">
                        <thead>
                            <tr>
                                <th>Reperto</th>
                                <th>Prezzo Unitario</th>
                                <th>Quantità</th>
                                <th>Totale Riga</th>
                                <th>Azione</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${dettagliCarrello}">
                                <tr id="row-item-${item.prodotto.idProdotto}">
                                     <td>
                                         <div class="td-prodotto" style="display: flex; gap: 14px; align-items: center;">
                                             <img src="${pageContext.request.contextPath}/images/prodotti/${item.prodotto.immagine}" 
                                                  alt="<c:out value='${item.prodotto.nome}'/>" 
                                                  style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid var(--border);">
                                             <div>
                                                 <h3><c:out value="${item.prodotto.nome}" /></h3>
                                                 <small><c:out value="${item.prodotto.scuderia}" /> - <c:out value="${item.prodotto.anno}" /></small>
                                             </div>
                                         </div>
                                     </td>
                                    <td class="td-price">
                                        € <fmt:formatNumber value="${item.prodotto.prezzo}" pattern="#,##0.00"/>
                                    </td>
                                    <td>
                                        <input type="number" 
                                               class="input-quantita-ajax" 
                                               data-id="${item.prodotto.idProdotto}" 
                                               value="${item.quantita}" 
                                               min="1" 
                                               max="${item.prodotto.quantitaDisponibile}">
                                    </td>
                                    <td class="td-price" id="totale-item-${item.prodotto.idProdotto}">
                                        € <fmt:formatNumber value="${item.prezzoTotale}" pattern="#,##0.00"/>
                                    </td>
                                    <td>
                                        <button type="button" class="btn-remove btn-remove-ajax" data-id="${item.prodotto.idProdotto}">Rimuovi</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div class="carrello-summary">
                        <h2>Totale Complessivo: <span id="totaleCarrelloDisplay">€ <fmt:formatNumber value="${totaleComplessivo}" pattern="#,##0.00"/></span></h2>
                        
                        <div class="carrello-actions" style="margin-bottom: 25px; display: flex; gap: 15px; align-items: center;">
                            <a href="${pageContext.request.contextPath}/Catalogo" class="btn-secondary">Continua gli Acquisti</a>
                            <button type="button" id="btnSvuotaCarrello" class="btn-clear-ajax" style="background: var(--racing-red, #e10600); color: #fff; border: none; padding: 10px 18px; border-radius: 6px; cursor: pointer; font-weight: 600; transition: background 0.2s;">Svuota Carrello</button>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${not empty sessionScope.utenteLoggato}">
                            <div class="checkout-section" style="background: var(--surface); padding: 24px; border-radius: 8px; border: 1px solid var(--border); margin-top: 20px;">
                                <h3 style="margin-bottom: 15px; color: var(--text);">Dettagli e Indirizzo di Spedizione</h3>
                                <form action="${pageContext.request.contextPath}/Checkout" method="POST" class="checkout-form">
                                    <c:if test="${not empty listaIndirizzi}">
                                        <div class="form-group" style="margin-bottom: 18px;">
                                            <label for="idIndirizzoSelect" style="display: block; margin-bottom: 6px; font-weight: 600;">Seleziona Indirizzo Salvato:</label>
                                            <select name="idIndirizzo" id="idIndirizzoSelect" class="form-control" style="width: 100%; padding: 10px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                                                <c:forEach var="ind" items="${listaIndirizzi}">
                                                    <option value="${ind.idIndirizzo}">
                                                        <c:out value="${ind.tipologia}"/>: <c:out value="${ind.via}"/>, <c:out value="${ind.citta}"/> (<c:out value="${ind.provincia}"/>) - CAP <c:out value="${ind.cap}"/>
                                                    </option>
                                                </c:forEach>
                                                <option value="new">+ Aggiungi e Usa un Nuovo Indirizzo</option>
                                            </select>
                                        </div>
                                    </c:if>

                                    <div id="newAddressContainer" style="${not empty listaIndirizzi ? 'display: none; ' : ''}border-top: 1px solid var(--border-soft); padding-top: 15px; margin-top: 15px;">
                                        <h4 style="font-size: 0.95rem; margin-bottom: 12px; color: var(--brass);">Nuovo Indirizzo di Spedizione</h4>
                                        <div class="form-group" style="margin-bottom: 12px;">
                                            <label for="via">Via e Civico *</label>
                                            <input type="text" id="via" name="via" placeholder="es. Via Enzo Ferrari 1" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                                        </div>
                                        <div style="display: flex; gap: 12px; margin-bottom: 12px;">
                                            <div style="flex: 2;">
                                                <label for="citta">Città *</label>
                                                <input type="text" id="citta" name="citta" placeholder="es. Maranello" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                                            </div>
                                            <div style="flex: 1;">
                                                <label for="provincia">Provincia (2 Lettere) *</label>
                                                <input type="text" id="provincia" name="provincia" maxlength="2" placeholder="MO" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px; text-transform: uppercase;">
                                            </div>
                                            <div style="flex: 1;">
                                                <label for="cap">CAP *</label>
                                                <input type="text" id="cap" name="cap" maxlength="5" placeholder="41053" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                                            </div>
                                        </div>
                                    </div>

                                    <button type="submit" class="btn-primary" style="width: 100%; margin-top: 20px; padding: 14px; font-size: 1.05rem; cursor: pointer;">
                                        Conferma ed Invia Ordine
                                    </button>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="checkout-login-prompt" style="margin-top: 25px; padding: 20px; background: var(--surface); border: 1px solid var(--border); border-radius: 8px; text-align: center;">
                                <p style="font-size: 1rem; color: var(--text-muted); margin-bottom: 12px;">Per completare l'acquisto ed inviare l'ordine è necessario accedere con un account registrato.</p>
                                <a href="${pageContext.request.contextPath}/Login" class="btn-primary" style="display: inline-block;">Accedi al tuo Account</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<script>
document.addEventListener("DOMContentLoaded", function() {
    var select = document.getElementById("idIndirizzoSelect");
    var newBox = document.getElementById("newAddressContainer");
    if (select && newBox) {
        select.addEventListener("change", function() {
            if (this.value === "new") {
                newBox.style.display = "block";
            } else {
                newBox.style.display = "none";
            }
        });
    }
});
</script>

<script src="${pageContext.request.contextPath}/scripts/carrello.js" defer></script>
<jsp:include page="../fragments/footer.jsp" />