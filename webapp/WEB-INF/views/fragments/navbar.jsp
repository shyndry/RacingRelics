<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="main-navbar">
    <div class="navbar-container">
        <a href="${pageContext.request.contextPath}/Catalogo" class="navbar-logo">
            RACING <span class="text-red">RELICS</span>
        </a>

        <div class="navbar-search-wrapper">
            <form action="${pageContext.request.contextPath}/Catalogo" method="GET" class="navbar-search-form" id="navbarSearchForm">
                <input type="text" 
                       id="navbarSearchInput"
                       name="q" 
                       value="<c:out value='${paramQuery}'/>" 
                       placeholder="Cerca reperto, pilota, scuderia..." 
                       class="navbar-search-input" 
                       autocomplete="off">
                <button type="button" id="navbarSearchClear" class="navbar-search-clear" style="display: none;" title="Cancella">&times;</button>
                <button type="submit" class="navbar-search-btn" title="Cerca">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                </button>
            </form>
            <div id="navbarSearchResults" class="navbar-search-dropdown" style="display: none;"></div>
        </div>

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

<script>
document.addEventListener('DOMContentLoaded', function() {
    const input = document.getElementById('navbarSearchInput');
    const resultsContainer = document.getElementById('navbarSearchResults');
    const clearBtn = document.getElementById('navbarSearchClear');
    const contextPath = '${pageContext.request.contextPath}';
    let debounceTimer;

    if (!input || !resultsContainer) return;

    if (input.value.trim().length > 0 && clearBtn) {
        clearBtn.style.display = 'block';
    }

    input.addEventListener('input', function() {
        const query = input.value.trim();
        if (clearBtn) {
            clearBtn.style.display = query ? 'block' : 'none';
        }

        clearTimeout(debounceTimer);
        if (query.length === 0) {
            resultsContainer.style.display = 'none';
            resultsContainer.innerHTML = '';
            return;
        }

        debounceTimer = setTimeout(function() {
            fetch(contextPath + '/Catalogo?q=' + encodeURIComponent(query), {
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                resultsContainer.innerHTML = '';
                if (!Array.isArray(data) || data.length === 0) {
                    resultsContainer.innerHTML = '<div class="search-no-results">Nessun reperto trovato per "' + escapeHtml(query) + '"</div>';
                } else {
                    data.forEach(function(item) {
                        const row = document.createElement('a');
                        row.href = contextPath + '/Prodotto?id=' + item.idProdotto;
                        row.className = 'search-result-item';
                        
                        const imgName = (item.immagine && item.immagine !== 'null') ? item.immagine : 'default.svg';
                        const imgSrc = contextPath + '/images/prodotti/' + imgName;
                        
                        const meta = [item.scuderia, item.pilota].filter(Boolean).join(' • ');

                        row.innerHTML = `
                            <img src="${imgSrc}" alt="${escapeHtml(item.nome)}" onerror="this.onerror=null; this.src='${contextPath}/images/prodotti/default.svg';">
                            <div class="search-result-info">
                                <div class="search-result-title">${escapeHtml(item.nome)}</div>
                                <div class="search-result-meta">${meta ? escapeHtml(meta) : 'Reperto Storico F1'}</div>
                            </div>
                            <div class="search-result-price">€ ${parseFloat(item.prezzo).toFixed(2)}</div>
                        `;
                        resultsContainer.appendChild(row);
                    });
                }
                resultsContainer.style.display = 'block';
            })
            .catch(function(err) {
                console.error('Errore ricerca live:', err);
            });
        }, 150);
    });

    if (clearBtn) {
        clearBtn.addEventListener('click', function() {
            input.value = '';
            clearBtn.style.display = 'none';
            resultsContainer.style.display = 'none';
            resultsContainer.innerHTML = '';
            input.focus();
        });
    }

    document.addEventListener('click', function(e) {
        if (!e.target.closest('.navbar-search-wrapper')) {
            resultsContainer.style.display = 'none';
        }
    });

    input.addEventListener('focus', function() {
        if (input.value.trim().length > 0 && resultsContainer.children.length > 0) {
            resultsContainer.style.display = 'block';
        }
    });

    function escapeHtml(str) {
        if (!str || str === 'null') return '';
        return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
    }
});
</script>