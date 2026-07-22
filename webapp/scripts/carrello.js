document.addEventListener("DOMContentLoaded", function () {
    const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
    const errorBox = document.getElementById("errorBox");

    function mostraErrore(messaggio) {
        if (errorBox) {
            errorBox.textContent = messaggio;
            errorBox.style.display = "block";
        }
    }

    function nascondiErrore() {
        if (errorBox) {
            errorBox.textContent = "";
            errorBox.style.display = "none";
        }
    }

    // Evento 'change' sull'input numerico della quantità
    const inputsQuantita = document.querySelectorAll(".input-quantita-ajax");
    inputsQuantita.forEach(input => {
        input.addEventListener("change", function () {
            nascondiErrore();
            const idProdotto = this.getAttribute("data-id");
            const nuovaQuantita = parseInt(this.value);

            if (isNaN(nuovaQuantita) || nuovaQuantita < 1) {
                this.value = 1;
                return;
            }

            aggiornaCarrelloServer(idProdotto, nuovaQuantita, "update");
        });
    });

    // Evento 'click' sui pulsanti di rimozione
    const btnsRimuovi = document.querySelectorAll(".btn-remove-ajax");
    btnsRimuovi.forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            nascondiErrore();
            const idProdotto = this.getAttribute("data-id");
            aggiornaCarrelloServer(idProdotto, 0, "remove");
        });
    });

    // Evento 'click' sul pulsante Svuota Carrello
    const btnSvuota = document.getElementById("btnSvuotaCarrello");
    if (btnSvuota) {
        btnSvuota.addEventListener("click", function (e) {
            e.preventDefault();
            if (confirm("Sei sicuro di voler svuotare interamente il carrello?")) {
                nascondiErrore();
                aggiornaCarrelloServer(null, 0, "clear");
            }
        });
    }

    function aggiornaCarrelloServer(idProdotto, quantita, azione) {
        const formData = new URLSearchParams();
        formData.append("action", azione);
        if (idProdotto !== null && idProdotto !== undefined) {
            formData.append("idProdotto", idProdotto);
        }
        if (azione === "update") {
            formData.append("quantita", quantita);
        }

        fetch(`${contextPath}/Carrello`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "X-Requested-With": "XMLHttpRequest" // Permette alla Servlet di riconoscere la chiamata AJAX
            },
            body: formData.toString()
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message || "Errore durante l'operazione."); });
            }
            return response.json();
        })
        .then(data => {
            if (data.status === "success") {
                // Se era una rimozione, eliminiamo la riga dal DOM
                if (azione === "remove") {
                    const row = document.getElementById(`row-item-${idProdotto}`);
                    if (row) row.remove();
                } else if (azione === "update") {
                    // Aggiorniamo il totale di riga
                    const totaleItemDisplay = document.getElementById(`totale-item-${idProdotto}`);
                    if (totaleItemDisplay) {
                        totaleItemDisplay.textContent = `€ ${parseFloat(data.totaleItem).toLocaleString('it-IT', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
                    }
                }

                // Aggiorniamo il totale complessivo nel DOM
                const totaleComplessivoDisplay = document.getElementById("totaleCarrelloDisplay");
                if (totaleComplessivoDisplay) {
                    totaleComplessivoDisplay.textContent = `€ ${parseFloat(data.totaleComplessivo).toLocaleString('it-IT', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
                }

                // Se il carrello è svuotato del tutto, ricarichiamo la pagina per mostrare il box "Carrello Vuoto"
                if (data.isVuoto) {
                    window.location.reload();
                }
            }
        })
        .catch(error => {
            mostraErrore(error.message);
        });
    }
});