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

            boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < prodotti.size(); i++) {
                    Prodotto p = prodotti.get(i);
                    json.append("{")
                        .append("\"idProdotto\":").append(p.getIdProdotto()).append(",")
                        .append("\"nome\":").append(jsonEscape(p.getNome())).append(",")
                        .append("\"scuderia\":").append(jsonEscape(p.getScuderia())).append(",")
                        .append("\"pilota\":").append(jsonEscape(p.getPilota())).append(",")
                        .append("\"prezzo\":").append(p.getPrezzo()).append(",")
                        .append("\"immagine\":").append(jsonEscape(p.getImmagine()))
                        .append("}");
                    if (i < prodotti.size() - 1) json.append(",");
                }
                json.append("]");
                response.getWriter().write(json.toString());
                return;
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

    private String jsonEscape(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ") + "\"";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}