package control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    
    private static DataSource ds;

    static {
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/RacingRelicsDB");
        } catch (NamingException e) {
            System.err.println("Errore nel lookup JNDI del DataSource in CheckoutServlet: " + e.getMessage());
        }
    }

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
    
        try (Connection con = ds.getConnection()) {
            
            con.setAutoCommit(false); 
            
            try {
                int idIndirizzo = Integer.parseInt(idIndirizzoStr);
        
                Ordine nuovoOrdine = new Ordine();
                nuovoOrdine.setIdUtente(utente.getIdUtente());
                nuovoOrdine.setIdIndirizzoConsegna(idIndirizzo);
                nuovoOrdine.setStato("IN_ELABORAZIONE");
            
                // Passiamo la connessione condivisa al DAO della testata
                int idOrdineGenerato = ordineDAO.doSave(nuovoOrdine, con);
                
                // Query per l'aggiornamento transazionale del magazzino prodotti
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
                            throw new SQLException("Scorte insufficienti o reperto non più attivo per l'ID prodotto: " + idProdotto);
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
    
        } catch (NumberFormatException | SQLException e) { 
            System.err.println("Fallimento critico durante la transazione di Checkout: " + e.getMessage());
            request.setAttribute("errorMessage", "Transazione annullata. Verificare la disponibilità dei reperti selezionati.");
            request.getRequestDispatcher("/WEB-INF/views/common/carrello.jsp").forward(request, response);
        }
    }
}