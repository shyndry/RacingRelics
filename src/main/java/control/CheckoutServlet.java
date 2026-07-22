package control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.ConnessioneDB;
import dao.IndirizzoDAO;
import dao.IndirizzoDAOImpl;
import dao.OrdineDAO;
import dao.OrdineDAOImpl;
import dao.ProdottoDAO;
import dao.ProdottoDAOImpl;
import model.Indirizzo;
import model.Ordine;
import model.Prodotto;
import model.Utente;

@WebServlet("/Checkout")
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrdineDAO ordineDAO = new OrdineDAOImpl();
    private final ProdottoDAO prodottoDAO = new ProdottoDAOImpl();
    private final IndirizzoDAO indirizzoDAO = new IndirizzoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/Carrello");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteLoggato") : null;
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> carrello = (Map<Integer, Integer>) session.getAttribute("carrello");
        if (carrello == null || carrello.isEmpty()) {
            session.setAttribute("carrelloError", "Il carrello è vuoto. Impossibile procedere al checkout.");
            response.sendRedirect(request.getContextPath() + "/Carrello");
            return;
        }

        String idIndirizzoStr = request.getParameter("idIndirizzo");
        String via = request.getParameter("via");
        String citta = request.getParameter("citta");
        String provincia = request.getParameter("provincia");
        String cap = request.getParameter("cap");
        String tipologia = request.getParameter("tipologia");

        try (Connection con = ConnessioneDB.getConnection()) {
            con.setAutoCommit(false);

            try {
                int idIndirizzoFinal = 0;

                if (idIndirizzoStr != null && !idIndirizzoStr.trim().isEmpty() && !"new".equals(idIndirizzoStr.trim())) {
                    idIndirizzoFinal = Integer.parseInt(idIndirizzoStr);
                } else if (via != null && !via.trim().isEmpty()) {
                    Indirizzo ind = new Indirizzo();
                    ind.setIdUtente(utente.getIdUtente());
                    ind.setVia(via.trim());
                    ind.setCitta(citta != null ? citta.trim() : "");
                    ind.setProvincia(provincia != null ? provincia.trim().toUpperCase() : "RM");
                    ind.setCap(cap != null ? cap.trim() : "00100");
                    ind.setTipologia(tipologia != null && !tipologia.trim().isEmpty() ? tipologia.trim() : "Spedizione");

                    idIndirizzoFinal = indirizzoDAO.doSave(ind, con);
                }

                if (idIndirizzoFinal <= 0) {
                    throw new SQLException("Seleziona o inserisci un indirizzo di spedizione valido.");
                }

                Ordine nuovoOrdine = new Ordine();
                nuovoOrdine.setIdUtente(utente.getIdUtente());
                nuovoOrdine.setIdIndirizzoConsegna(idIndirizzoFinal);
                nuovoOrdine.setStato("IN_ELABORAZIONE");

                int idOrdineGenerato = ordineDAO.doSave(nuovoOrdine, con);

                String updateStockQuery = "UPDATE Prodotto SET quantita_disponibile = ? WHERE id_prodotto = ?";

                try (PreparedStatement psStock = con.prepareStatement(updateStockQuery)) {
                    for (Map.Entry<Integer, Integer> entry : carrello.entrySet()) {
                        int idProdotto = entry.getKey();
                        int quantitaRichiesta = entry.getValue();

                        Prodotto prodotto = prodottoDAO.doRetrieveByKey(idProdotto);

                        if (prodotto != null && prodotto.isAttivo() && prodotto.getQuantitaDisponibile() >= quantitaRichiesta) {
                            ordineDAO.doSaveComposizione(idOrdineGenerato, idProdotto, quantitaRichiesta, prodotto.getPrezzo(), con);

                            int nuovaDisponibilita = prodotto.getQuantitaDisponibile() - quantitaRichiesta;
                            psStock.setInt(1, nuovaDisponibilita);
                            psStock.setInt(2, idProdotto);
                            psStock.executeUpdate();
                        } else {
                            throw new SQLException("Scorte insufficienti per il cimelio: " + (prodotto != null ? prodotto.getNome() : idProdotto));
                        }
                    }
                }

                con.commit();

                carrello.clear();
                session.setAttribute("carrello", carrello);

                response.sendRedirect(request.getContextPath() + "/Carrello?checkoutSuccess=true");

            } catch (Exception e) {
                con.rollback();
                throw e;
            }

        } catch (Exception e) {
            System.err.println("Fallimento durante la transazione di Checkout: " + e.getMessage());
            session.setAttribute("carrelloError", e.getMessage() != null ? e.getMessage() : "Errore durante il completamento dell'ordine.");
            response.sendRedirect(request.getContextPath() + "/Carrello");
        }
    }
}