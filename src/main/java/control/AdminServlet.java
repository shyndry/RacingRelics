package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.OrdineDAO;
import dao.OrdineDAOImpl;
import dao.ProdottoDAO;
import dao.ProdottoDAOImpl;
import model.Ordine;
import model.Prodotto;


@WebServlet("/admin/dashboard")
public class AdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ProdottoDAO prodottoDAO = new ProdottoDAOImpl();
    private final OrdineDAO ordineDAO = new OrdineDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<Prodotto> prodotti = prodottoDAO.doRetrieveAll(); 
            List<Ordine> ordini = ordineDAO.doRetrieveAll(null, null); 

            request.setAttribute("prodotti", prodotti);
            request.setAttribute("ordini", ordini);

            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            System.err.println("Errore nel caricamento della dashboard amministrativa: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Catalogo");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("delete".equals(action)) {
                String idStr = request.getParameter("idProdotto");
                if (idStr != null) {
                    int idProdotto = Integer.parseInt(idStr);
                    prodottoDAO.doDelete(idProdotto);
                }
            } 
            else if ("insert".equals(action)) {
                
                Prodotto nuovoProdotto = new Prodotto();
                nuovoProdotto.setNome(request.getParameter("nome"));
                nuovoProdotto.setDescrizione(request.getParameter("descrizione"));
                nuovoProdotto.setPrezzoAttuale(Double.parseDouble(request.getParameter("prezzo")));
                nuovoProdotto.setQuantitaDisponibile(Integer.parseInt(request.getParameter("quantita")));
                nuovoProdotto.setImmaginePath(request.getParameter("immaginePath"));
                nuovoProdotto.setScuderia(request.getParameter("scuderia"));
                nuovoProdotto.setPilota(request.getParameter("pilota"));
                nuovoProdotto.setAnnoCampionato(Integer.parseInt(request.getParameter("anno")));
                nuovoProdotto.setGranPremio(request.getParameter("granPremio"));
                nuovoProdotto.setAttivo(true); 

                prodottoDAO.doSave(nuovoProdotto);
            }
            
            
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            
        } catch (NumberFormatException | SQLException e) {
            System.err.println("Errore nell'esecuzione dell'operazione di backoffice: " + e.getMessage());
            request.setAttribute("errorMessage", "Impossibile completare l'operazione sul catalogo.");
            doGet(request, response); 
        }
    }
}