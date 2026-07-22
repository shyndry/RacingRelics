<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="../fragments/header.jsp" />
<jsp:include page="../fragments/navbar.jsp" />

<main class="catalogo-main">
    
    <section class="filter-section">
        <h2>Filtra la Griglia</h2>
        <form action="${pageContext.request.contextPath}/Catalogo" method="GET" class="filter-form">
            <div class="form-group">
                <label for="scuderia">Scuderia</label>
                <input type="text" id="scuderia" name="scuderia" value="<c:out value='${paramScuderia}'/>" placeholder="Es. Ferrari, McLaren...">
            </div>
            
            <div class="form-group">
                <label for="pilota">Pilota</label>
                <input type="text" id="pilota" name="pilota" value="<c:out value='${paramPilota}'/>" placeholder="Es. Schumacher, Senna...">
            </div>
            
            <div class="form-group">
                <label for="anno">Anno</label>
                <input type="number" id="anno" name="anno" value="<c:out value='${paramAnno}'/>" placeholder="Es. 1998">
            </div>
            
            <div class="filter-buttons">
                <button type="submit" class="btn-filter">Applica Filtri</button>
                <a href="${pageContext.request.contextPath}/Catalogo" class="btn-reset">Resetta</a>
            </div>
        </form>
    </section>

    <section class="products-section">
        <h2>Cimeli e Reperti Storici</h2>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert-error">
                <p><c:out value="${errorMessage}" /></p>
            </div>
        </c:if>

        <div class="products-grid">
            <c:choose>
                <c:when test="${empty prodotti}">
                    <div class="no-products">
                        <p>Nessun reperto storico corrisponde ai criteri di ricerca selezionati.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="p" items="${prodotti}">
                        <div class="product-card">
                            <div class="product-image">
                                <img src="${pageContext.request.contextPath}/images/prodotti/${not empty p.immagine ? p.immagine : 'default.svg'}" 
                                     alt="<c:out value='${p.nome}'/>"
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/prodotti/default.svg';">
                            </div>
                            <div class="product-info">
                                <h3><c:out value="${p.nome}"/></h3>
                                <p class="product-description"><c:out value="${p.descrizione}"/></p>
                                
                                <div class="product-tags">
                                    <c:if test="${not empty p.scuderia}">
                                        <span class="tag tag-scuderia"><c:out value="${p.scuderia}"/></span>
                                    </c:if>
                                    <c:if test="${not empty p.pilota}">
                                        <span class="tag tag-pilota"><c:out value="${p.pilota}"/></span>
                                    </c:if>
                                    <c:if test="${not empty p.anno && p.anno != 0}">
                                        <span class="tag tag-anno"><c:out value="${p.anno}"/></span>
                                    </c:if>
                                </div>
                                
                                <div class="product-footer">
                                    <span class="product-price">€ <fmt:formatNumber value="${p.prezzo}" pattern="#,##0.00"/></span>
                                    <a href="${pageContext.request.contextPath}/Prodotto?id=${p.idProdotto}" class="btn-details">Esamina</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</main>

<jsp:include page="../fragments/footer.jsp" />