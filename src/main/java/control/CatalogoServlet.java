package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.ProdottoDAO;
import dao.ProdottoDAOImpl;
import model.Prodotto;

@WebServlet("/Catalogo")
public class CatalogoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ProdottoDAO prodottoDAO = new ProdottoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String q = request.getParameter("q");
        String scuderia = request.getParameter("scuderia");
        String pilota = request.getParameter("pilota");
        String annoStr = request.getParameter("anno");
        
        Integer anno = null;
        
        if (annoStr != null && !annoStr.trim().isEmpty()) {
            try {
                anno = Integer.parseInt(annoStr);
            } catch (NumberFormatException e) {
                System.err.println("Formato anno non valido nella ricerca: " + annoStr);
            }
        }

        try {
            List<Prodotto> prodotti;

            if ((q != null && !q.trim().isEmpty()) ||
                (scuderia != null && !scuderia.trim().isEmpty()) || 
                (pilota != null && !pilota.trim().isEmpty()) || 
                anno != null) {
                
                prodotti = prodottoDAO.doRetrieveByFiltri(q, scuderia, anno, pilota);
                
                request.setAttribute("paramQuery", q);
                request.setAttribute("paramScuderia", scuderia);
                request.setAttribute("paramPilota", pilota);
                request.setAttribute("paramAnno", anno);
            } else {
                prodotti = prodottoDAO.doRetrieveAll();
            }

            request.setAttribute("prodotti", prodotti);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/common/catalogo.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            System.err.println("Errore SQL nel recupero del catalogo: " + e.getMessage());
            request.setAttribute("errorMessage", "Impossibile caricare il catalogo in questo momento. Riprova più tardi.");
            request.getRequestDispatcher("/WEB-INF/views/common/catalogo.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}