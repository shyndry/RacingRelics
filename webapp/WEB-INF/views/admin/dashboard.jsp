<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="../fragments/header.jsp" />
<jsp:include page="../fragments/navbar.jsp" />

<main class="admin-dashboard-main">
    <h1>Pannello di Controllo Direzione Gara (Admin)</h1>

    <c:if test="${not empty errorMessage}">
        <div class="alert-error">
            <p><c:out value="${errorMessage}" /></p>
        </div>
    </c:if>

    <section class="admin-section insert-section">
        <h2>Aggiungi un Nuovo Reperto Storico</h2>
        <form action="${pageContext.request.contextPath}/admin/dashboard" method="POST" class="admin-form">
            <input type="hidden" name="action" value="insert">

            <div class="form-group-row">
                <div class="form-group">
                    <label for="nome">Nome Cimelio *</label>
                    <input type="text" id="nome" name="nome" required placeholder="es. Volante Ferrari F2004">
                </div>
                <div class="form-group">
                    <label for="prezzo">Prezzo Attuale (€) *</label>
                    <input type="number" id="prezzo" name="prezzo" step="0.01" min="0.01" required placeholder="0.00">
                </div>
                <div class="form-group">
                    <label for="quantita">Quantità Iniziale *</label>
                    <input type="number" id="quantita" name="quantita" min="1" required placeholder="1">
                </div>
            </div>

            <div class="form-group">
                <label for="descrizione">Storia e Descrizione del Reperto</label>
                <textarea id="descrizione" name="descrizione" rows="4" placeholder="Scrivi la storia ufficiale del cimelio..."></textarea>
            </div>

            <div class="form-group-row">
                <div class="form-group">
                    <label for="scuderia">Scuderia F1</label>
                    <input type="text" id="scuderia" name="scuderia" placeholder="es. Ferrari">
                </div>
                <div class="form-group">
                    <label for="pilota">Pilota Correlato</label>
                    <input type="text" id="pilota" name="pilota" placeholder="es. Michael Schumacher">
                </div>
                <div class="form-group">
                    <label for="anno">Anno Stagione</label>
                    <input type="number" id="anno" name="anno" placeholder="es. 2004">
                </div>
            </div>

            <div class="form-group-row">
                <div class="form-group">
                    <label for="granPremio">Gran Premio di Riferimento</label>
                    <input type="text" id="granPremio" name="granPremio" placeholder="es. Monza GP">
                </div>
                <div class="form-group">
                    <label for="immaginePath">Nome File Immagine</label>
                    <input type="text" id="immaginePath" name="immaginePath" placeholder="es. volante_f2004.jpg" value="default.jpg">
                </div>
            </div>

            <button type="submit" class="btn-admin-submit">Immetti nel Catalogo Ufficiale</button>
        </form>
    </section>

    <hr class="admin-divider">

    <section class="admin-section inventory-section">
        <h2>Inventario Completo Cimeli</h2>
        <table class="admin-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Cimelio</th>
                    <th>Scuderia / Anno</th>
                    <th>Prezzo</th>
                    <th>Stock</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${prodotti}">
                    <tr>
                        <td>#<c:out value="${p.idProdotto}"/></td>
                        <td><strong><c:out value="${p.nome}"/></strong></td>
                        <td><c:out value="${p.scuderia}"/> (<c:out value="${p.anno}"/>)</td>
                        <td>${p.prezzo} €</td>
                        <td>${p.quantitaDisponibile} pz</td>
                        <td>
                            <span class="badge-status ${p.attivo ? 'status-active' : 'status-archived'}">
                                ${p.attivo ? "Attivo" : "Archiviato"}
                            </span>
                        </td>
                        <td>
                            <c:if test="${p.attivo}">
                                <form action="${pageContext.request.contextPath}/admin/dashboard" method="POST" onsubmit="return confirm('Sicuro di voler archiviare questo reperto?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="idProdotto" value="${p.idProdotto}">
                                    <button type="submit" class="btn-table-delete">Archivia (Soft-Delete)</button>
                                </form>
                            </c:if>
                            <c:if test="${not p.attivo}">
                                <span class="text-muted">Nessuna azione</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </section>

    <hr class="admin-divider">

    <section class="admin-section orders-section">
        <h2>Registro Globale degli Ordini Clienti</h2>
        <table class="admin-table">
            <thead>
                <tr>
                    <th>ID Ordine</th>
                    <th>ID Utente</th>
                    <th>Data Transazione</th>
                    <th>Stato Logistico</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="o" items="${ordini}">
                    <tr>
                        <td><strong>#<c:out value="${o.idOrdine}"/></strong></td>
                        <td>Utente #<c:out value="${o.idUtente}"/></td>
                        <td>
                            <fmt:formatDate value="${o.dataOrdine}" pattern="dd/MM/yyyy - HH:mm" />
                        </td>
                        <td>
                            <span class="badge-stato ${o.stato.toLowerCase()}">
                                <c:out value="${o.stato}"/>
                            </span>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </section>
</main>

<jsp:include page="../fragments/footer.jsp" />