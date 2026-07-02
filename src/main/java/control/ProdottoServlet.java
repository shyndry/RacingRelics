package control;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.ProdottoDAO;
import dao.ProdottoDAOImpl;
import model.Prodotto;

@WebServlet("/Prodotto")
public class ProdottoServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private final ProdottoDAO prodottoDAO = new ProdottoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/Catalogo");
            return;
        }
        
        try {
            int idProdotto = Integer.parseInt(idStr);
            Prodotto prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
            
            if (prodotto != null) {
                request.setAttribute("prodotto", prodotto);
                request.getRequestDispatcher("/WEB-INF/views/common/prodotto.jsp").forward(request, response);
            } else {
                // Se l'ID non esiste nel DB, rimandiamo al catalogo
                response.sendRedirect(request.getContextPath() + "/Catalogo");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Formato ID prodotto non valido: " + idStr);
            response.sendRedirect(request.getContextPath() + "/Catalogo");
        } catch (SQLException e) {
            System.err.println("Errore SQL nel recupero del prodotto: " + e.getMessage());
            request.setAttribute("errorMessage", "Impossibile caricare i dettagli del reperto in questo momento.");
            request.getRequestDispatcher("/WEB-INF/views/common/catalogo.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}