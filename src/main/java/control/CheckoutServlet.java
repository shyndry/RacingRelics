package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import dao.OrdineDAO;
import dao.OrdineDAOImpl;
import dao.ProdottoDAO;
import dao.ProdottoDAOImpl;
import model.Ordine;
import model.Prodotto;
import model.Utente;


@WebServlet("/Checkout")
public class CheckoutServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private final OrdineDAO ordineDAO = new OrdineDAOImpl();
    private final ProdottoDAO prodottoDAO = new ProdottoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException{
        response.sendRedirect(request.getContextPath() + "/Carrello");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteLoggato") : null;
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }   
        
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> carrello = (Map<Integer, Integer>) session.getAttribute("carrello");
        if (carrello == null || carrello.isEmpty()) {
            request.setAttribute("errorMessage", "Il carrello è vuoto. Impossibile procedere al checkout.");
            request.getRequestDispatcher("/WEB-INF/views/common/carrello.jsp").forward(request, response);
            return;
        }
    
        String idIndirizzoStr = request.getParameter("idIndirizzo");
        
        if (idIndirizzoStr == null || idIndirizzoStr.trim().isEmpty()) { 
            request.setAttribute("errorMessage", "Seleziona un indirizzo di spedizione valido");
            request.getRequestDispatcher("/WEB-INF/views/common/carrello.jsp").forward(request, response);
            return;
        }
    
        try {
            int idIndirizzo = Integer.parseInt(idIndirizzoStr);
    
            Ordine nuovoOrdine = new Ordine();
            nuovoOrdine.setIdUtente(utente.getIdUtente());
            nuovoOrdine.setIdIndirizzoConsegna(idIndirizzo);
            nuovoOrdine.setStato("IN_ELABORAZIONE");
        
            int idOrdineGenerato = ordineDAO.doSave(nuovoOrdine);
            
            for (Map.Entry<Integer, Integer> entry : carrello.entrySet()) {
                int idProdotto = entry.getKey();
                int quantitaRichiesta = entry.getValue();
    
                Prodotto prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
                
                
                if (prodotto != null && prodotto.isAttivo() && prodotto.getQuantitaDisponibile() >= quantitaRichiesta) {
                    ordineDAO.doSaveCompositions(idOrdineGenerato, idProdotto, quantitaRichiesta, prodotto.getPrezzo());
                    int nuovaDisponibilita = prodotto.getQuantitaDisponibile() - quantitaRichiesta;
                    prodotto.setQuantitaDisponibile(nuovaDisponibilita);
                    prodottoDAO.doUpdate(prodotto);
                } else {
                    throw new SQLException("Errore di disponibilità magazzino per il prodotto ID: " + idProdotto);
                }
            }
    
            carrello.clear();
            session.setAttribute("carrello", carrello);
            response.sendRedirect(request.getContextPath() + "/common/garage?checkoutSuccess=true");
    
        } catch (NumberFormatException | SQLException e) { 
            System.err.println("Fallimento critico durante la transazione di Checkout: " + e.getMessage());
            request.setAttribute("errorMessage", "Transazione fallita. Verificare la disponibilità dei reperti selezionati.");
            request.getRequestDispatcher("/WEB-INF/views/common/carrello.jsp").forward(request, response);
        }
    }
}
