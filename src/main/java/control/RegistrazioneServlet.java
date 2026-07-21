package control;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.UtenteDAO;
import dao.UtenteDAOImpl;
import model.Utente;

@WebServlet("/Registrazione")
public class RegistrazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UtenteDAO utenteDAO = new UtenteDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/common/registrazione.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Utente nuovoUtente = new Utente();
            nuovoUtente.setNome(nome);
            nuovoUtente.setCognome(cognome);
            nuovoUtente.setEmail(email);
            nuovoUtente.setPassword(password);
            nuovoUtente.setRuolo("USER");

            utenteDAO.doSave(nuovoUtente);
            
            response.sendRedirect(request.getContextPath() + "/Login");
            return;

        } catch (SQLException e) {
            System.err.println("Anomalia durante la registrazione utente: " + e.getMessage());
            
            request.setAttribute("errorMessage", "Impossibile completare la registrazione: l'indirizzo email potrebbe essere già associato a un account.");
            request.getRequestDispatcher("/WEB-INF/views/common/registrazione.jsp").forward(request, response);
        }
    }
}