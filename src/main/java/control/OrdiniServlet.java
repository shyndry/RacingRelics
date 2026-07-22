package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.OrdineDAO;
import dao.OrdineDAOImpl;
import model.Ordine;
import model.Utente;

@WebServlet("/Ordini")
public class OrdiniServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrdineDAO ordineDAO = new OrdineDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteLoggato") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            List<Ordine> listaOrdini = ordineDAO.doRetrieveByUser(utente.getIdUtente());

            Map<Integer, Double> mappaTotali = new HashMap<>();

            for (Ordine ordine : listaOrdini) {
                double totale = ordineDAO.doRetrieveTotaleOrdine(ordine.getIdOrdine());
                mappaTotali.put(ordine.getIdOrdine(), totale);
            }

            request.setAttribute("listaOrdini", listaOrdini);
            request.setAttribute("mappaTotali", mappaTotali);

        } catch (SQLException e) {
            System.err.println("Errore nel recupero dello storico ordini: " + e.getMessage());
            request.setAttribute("errorMessage", "Impossibile recuperare lo storico dei tuoi ordini.");
        }

        request.getRequestDispatcher("/WEB-INF/views/common/storico.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}