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
        <form action="${pageContext.request.contextPath}/admin/dashboard" method="POST" enctype="multipart/form-data" class="admin-form">
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
                    <label for="immagineFile">Carica Immagine Reperto</label>
                    <input type="file" id="immagineFile" name="immagineFile" accept="image/*" style="width: 100%; padding: 6px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
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
                        <td>€ <fmt:formatNumber value="${p.prezzo}" pattern="#,##0.00"/></td>
                        <td>${p.quantitaDisponibile} pz</td>
                        <td>
                            <span id="status-badge-${p.idProdotto}" class="badge-status ${p.attivo ? 'status-active' : 'status-archived'}">
                                ${p.attivo ? "Attivo" : "Archiviato"}
                            </span>
                        </td>
                        <td id="azioni-cell-${p.idProdotto}" style="display: flex; gap: 8px; align-items: center;">
                            <button type="button" class="btn-table-edit" 
                                    data-id="${p.idProdotto}" 
                                    data-nome="<c:out value="${p.nome}"/>" 
                                    data-prezzo="${p.prezzo}" 
                                    data-quantita="${p.quantitaDisponibile}" 
                                    data-scuderia="<c:out value="${p.scuderia}"/>" 
                                    data-pilota="<c:out value="${p.pilota}"/>" 
                                    data-anno="${p.anno}" 
                                    data-granpremio="<c:out value="${p.granPremio}"/>" 
                                    data-immagine="<c:out value="${p.immagine}"/>" 
                                    data-attivo="${p.attivo}" 
                                    data-descrizione="<c:out value="${p.descrizione}"/>" 
                                    style="background: var(--brass, #c5a059); color: #000; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-weight: 600;">Modifica</button>
                            <c:if test="${p.attivo}">
                                <button type="button" class="btn-table-delete btn-archivia-modal" data-id="${p.idProdotto}" data-nome="<c:out value="${p.nome}"/>">Archivia (Soft-Delete)</button>
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

        <form action="${pageContext.request.contextPath}/admin/dashboard" method="GET" class="admin-filter-form" style="display: flex; gap: 15px; align-items: flex-end; margin-bottom: 20px; flex-wrap: wrap; background: var(--surface, #1e1e1e); padding: 15px; border-radius: 6px; border: 1px solid var(--border, #333);">
            <div class="form-group" style="flex: 1; min-width: 150px;">
                <label for="dataInizio" style="display: block; margin-bottom: 5px; font-size: 0.85rem; font-weight: 600;">Data Inizio:</label>
                <input type="date" id="dataInizio" name="dataInizio" value="${filtroDataInizio}" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
            </div>
            <div class="form-group" style="flex: 1; min-width: 150px;">
                <label for="dataFine" style="display: block; margin-bottom: 5px; font-size: 0.85rem; font-weight: 600;">Data Fine:</label>
                <input type="date" id="dataFine" name="dataFine" value="${filtroDataFine}" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
            </div>
            <div class="form-group" style="flex: 1; min-width: 150px;">
                <label for="idUtente" style="display: block; margin-bottom: 5px; font-size: 0.85rem; font-weight: 600;">ID Utente / Cliente:</label>
                <input type="number" id="idUtente" name="idUtente" value="${filtroIdUtente}" placeholder="es. 1" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
            </div>
            <div style="display: flex; gap: 10px;">
                <button type="submit" class="btn-primary" style="padding: 8px 16px; cursor: pointer;">Filtra Ordini</button>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn-secondary" style="padding: 8px 16px; text-decoration: none; display: inline-block;">Reset</a>
            </div>
        </form>

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
                <c:if test="${empty ordini}">
                    <tr>
                        <td colspan="4" style="text-align: center; color: var(--text-muted, #888); padding: 20px;">Nessun ordine trovato con i filtri selezionati.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </section>

    <!-- Modal Modifica Prodotto -->
    <div id="modalModificaProdotto" class="modal" style="display:none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.7); backdrop-filter: blur(4px);">
        <div class="modal-content" style="background-color: var(--surface, #1e1e1e); margin: 5% auto; padding: 25px; border: 1px solid var(--border, #333); border-radius: 8px; width: 90%; max-width: 650px; color: var(--text, #fff);">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid var(--border, #333); padding-bottom: 10px;">
                <h2 style="margin: 0; font-size: 1.3rem;">Modifica Reperto Storico #<span id="editIdProdottoDisplay"></span></h2>
                <button type="button" onclick="chiudiModalModifica()" style="background: none; border: none; color: #999; font-size: 1.5rem; cursor: pointer;">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/dashboard" method="POST" enctype="multipart/form-data" class="admin-form">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="editIdProdotto" name="idProdotto">

                <div class="form-group-row" style="display: flex; gap: 15px; margin-bottom: 15px;">
                    <div class="form-group" style="flex: 2;">
                        <label for="editNome" style="display: block; margin-bottom: 5px; font-weight: 600;">Nome Cimelio *</label>
                        <input type="text" id="editNome" name="nome" required style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label for="editPrezzo" style="display: block; margin-bottom: 5px; font-weight: 600;">Prezzo (€) *</label>
                        <input type="number" id="editPrezzo" name="prezzo" step="0.01" min="0.01" required style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label for="editQuantita" style="display: block; margin-bottom: 5px; font-weight: 600;">Quantità *</label>
                        <input type="number" id="editQuantita" name="quantita" min="0" required style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                    </div>
                </div>

                <div class="form-group" style="margin-bottom: 15px;">
                    <label for="editDescrizione" style="display: block; margin-bottom: 5px; font-weight: 600;">Storia e Descrizione</label>
                    <textarea id="editDescrizione" name="descrizione" rows="3" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;"></textarea>
                </div>

                <div class="form-group-row" style="display: flex; gap: 15px; margin-bottom: 15px;">
                    <div class="form-group" style="flex: 1;">
                        <label for="editScuderia" style="display: block; margin-bottom: 5px; font-weight: 600;">Scuderia</label>
                        <input type="text" id="editScuderia" name="scuderia" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label for="editPilota" style="display: block; margin-bottom: 5px; font-weight: 600;">Pilota</label>
                        <input type="text" id="editPilota" name="pilota" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label for="editAnno" style="display: block; margin-bottom: 5px; font-weight: 600;">Anno</label>
                        <input type="number" id="editAnno" name="anno" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                    </div>
                </div>

                <div class="form-group-row" style="display: flex; gap: 15px; margin-bottom: 20px;">
                    <div class="form-group" style="flex: 1;">
                        <label for="editGranPremio" style="display: block; margin-bottom: 5px; font-weight: 600;">Gran Premio</label>
                        <input type="text" id="editGranPremio" name="granPremio" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label for="editImmagineFile" style="display: block; margin-bottom: 5px; font-weight: 600;">Carica Nuova Immagine</label>
                        <input type="file" id="editImmagineFile" name="immagineFile" accept="image/*" style="width: 100%; padding: 6px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                        <input type="hidden" id="editImmaginePath" name="immaginePath">
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label for="editAttivo" style="display: block; margin-bottom: 5px; font-weight: 600;">Stato Attivo</label>
                        <select id="editAttivo" name="attivo" style="width: 100%; padding: 8px; background: var(--bg); border: 1px solid var(--border); color: var(--text); border-radius: 4px;">
                            <option value="true">Attivo</option>
                            <option value="false">Archiviato</option>
                        </select>
                    </div>
                </div>

                <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 15px;">
                    <button type="button" onclick="chiudiModalModifica()" class="btn-secondary" style="padding: 10px 18px; cursor: pointer;">Annulla</button>
                    <button type="submit" class="btn-primary" style="padding: 10px 18px; cursor: pointer;">Salva Modifiche</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal Conferma Archiviazione Prodotto -->
    <div id="modalConfermaArchivia" class="modal" style="display:none; position: fixed; z-index: 1100; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.7); backdrop-filter: blur(4px);">
        <div class="modal-content" style="background-color: var(--surface, #1e1e1e); margin: 12% auto; padding: 25px; border: 1px solid var(--border, #333); border-radius: 8px; width: 90%; max-width: 480px; color: var(--text, #fff); text-align: center;">
            <h3 style="margin-top: 0; color: var(--racing-red, #e10600); font-size: 1.3rem;">Conferma Archiviazione Reperto</h3>
            <p style="margin: 15px 0 25px 0; font-size: 0.95rem; color: #ccc;">
                Sei sicuro di voler archiviare (soft-delete) il reperto <strong id="archiviaNomeProdotto" style="color: #fff;"></strong> (#<span id="archiviaIdProdottoDisplay"></span>)?
            </p>
            <div style="display: flex; justify-content: center; gap: 15px;">
                <button type="button" onclick="chiudiModalArchivia()" class="btn-secondary" style="padding: 10px 20px; cursor: pointer;">Annulla</button>
                <button type="button" id="btnConfermaArchiviazione" class="btn-danger" style="background: var(--racing-red, #e10600); color: #fff; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 600;">Sì, Archivia Reperto</button>
            </div>
        </div>
    </div>

</main>

<script>
let idProdottoDaArchiviare = null;

document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll(".btn-table-edit").forEach(function(btn) {
        btn.addEventListener("click", function() {
            document.getElementById('editIdProdottoDisplay').textContent = this.dataset.id;
            document.getElementById('editIdProdotto').value = this.dataset.id;
            document.getElementById('editNome').value = this.dataset.nome || '';
            document.getElementById('editPrezzo').value = this.dataset.prezzo || '';
            document.getElementById('editQuantita').value = this.dataset.quantita || '';
            document.getElementById('editScuderia').value = this.dataset.scuderia || '';
            document.getElementById('editPilota').value = this.dataset.pilota || '';
            document.getElementById('editAnno').value = this.dataset.anno || '';
            document.getElementById('editGranPremio').value = this.dataset.granpremio || '';
            document.getElementById('editImmaginePath').value = this.dataset.immagine || 'default.jpg';
            document.getElementById('editAttivo').value = (this.dataset.attivo === 'true') ? 'true' : 'false';
            document.getElementById('editDescrizione').value = this.dataset.descrizione || '';
            document.getElementById('modalModificaProdotto').style.display = 'block';
        });
    });

    document.querySelectorAll(".btn-archivia-modal").forEach(function(btn) {
        btn.addEventListener("click", function() {
            idProdottoDaArchiviare = this.dataset.id;
            document.getElementById("archiviaIdProdottoDisplay").textContent = idProdottoDaArchiviare;
            document.getElementById("archiviaNomeProdotto").textContent = this.dataset.nome || "";
            document.getElementById("modalConfermaArchivia").style.display = "block";
        });
    });

    const btnConferma = document.getElementById("btnConfermaArchiviazione");
    if (btnConferma) {
        btnConferma.addEventListener("click", function() {
            if (!idProdottoDaArchiviare) return;
            
            const contextPath = "${pageContext.request.contextPath}";
            const formData = new URLSearchParams();
            formData.append("action", "delete");
            formData.append("idProdotto", idProdottoDaArchiviare);

            fetch(contextPath + "/admin/dashboard", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "X-Requested-With": "XMLHttpRequest"
                },
                body: formData.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === "success") {
                    const badge = document.getElementById("status-badge-" + idProdottoDaArchiviare);
                    if (badge) {
                        badge.className = "badge-status status-archived";
                        badge.textContent = "Archiviato";
                    }
                    const cell = document.getElementById("azioni-cell-" + idProdottoDaArchiviare);
                    if (cell) {
                        const deleteBtn = cell.querySelector(".btn-archivia-modal");
                        if (deleteBtn) deleteBtn.remove();
                    }
                    chiudiModalArchivia();
                } else {
                    alert(data.message || "Errore durante l'archiviazione.");
                }
            })
            .catch(err => {
                console.error(err);
                chiudiModalArchivia();
            });
        });
    }
});

function chiudiModalModifica() {
    document.getElementById('modalModificaProdotto').style.display = 'none';
}

function chiudiModalArchivia() {
    idProdottoDaArchiviare = null;
    document.getElementById('modalConfermaArchivia').style.display = 'none';
}

window.onclick = function(event) {
    var modalEdit = document.getElementById('modalModificaProdotto');
    var modalDelete = document.getElementById('modalConfermaArchivia');
    if (event.target === modalEdit) {
        chiudiModalModifica();
    }
    if (event.target === modalDelete) {
        chiudiModalArchivia();
    }
};
</script>

<jsp:include page="../fragments/footer.jsp" />