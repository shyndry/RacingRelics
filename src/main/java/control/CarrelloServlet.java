package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.ProdottoDAO;
import dao.ProdottoDAOImpl;
import model.Prodotto;


@WebServlet("/Carrello")
public class CarrelloServlet extends HttpServlet{
    
    private static final long serialVersionUID = 1L;
    private final ProdottoDAO prodottoDAO = new ProdottoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
                request.getRequestDispatcher("/WEB-INF/views/common/carrello.jsp").forward(request, response);
            }
        
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

            HttpSession session = request.getSession(true);

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> carrello = (Map<Integer, Integer>) session.getAttribute("carrello");
            if (carrello == null) {
            carrello = new HashMap<>();
            session.setAttribute("carrello", carrello);
            }

            String action = request.getParameter("action");
            String idProdottoStr = request.getParameter("idProdotto");

        if (idProdottoStr != null && action != null) {
            try {
                int idProdotto = Integer.parseInt(idProdottoStr);
                
                if ("add".equals(action)) {
                    Prodotto p = prodottoDAO.doRetrieveByKey(idProdotto);
                    if (p != null && p.isAttivo()) {
                        int quantitaAttuale = carrello.getOrDefault(idProdotto, 0);
                        
                        if (quantitaAttuale + 1 <= p.getQuantitaDisponibile()) {
                            carrello.put(idProdotto, quantitaAttuale + 1);
                        } else {
                            request.setAttribute("errorMessage", "Scorte insufficienti in magazzino per questo pezzo storico.");
                        }
                    }
                } 
                else if ("remove".equals(action)) {
                    carrello.remove(idProdotto);
                } 
                else if ("update".equals(action)) {
                    String quantitaStr = request.getParameter("quantita");
                    if (quantitaStr != null) {
                        int nuovaQuantita = Integer.parseInt(quantitaStr);
                        Prodotto p = prodottoDAO.doRetrieveByKey(idProdotto);
                        
                        if (p != null && nuovaQuantita > 0 && nuovaQuantita <= p.getQuantitaDisponibile()) {
                            carrello.put(idProdotto, nuovaQuantita);
                        } else if (nuovaQuantita <= 0) {
                            carrello.remove(idProdotto);
                        } else {
                            request.setAttribute("errorMessage", "Quantità richiesta non disponibile.");
                        }
                    }
                }
            } catch (NumberFormatException | SQLException e) {
                System.err.println("Errore nella gestione del carrello: " + e.getMessage());
                request.setAttribute("errorMessage", "Si è verificato un errore nell'aggiornamento del carrello.");
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/common/carrello.jsp").forward(request, response);
    }
}