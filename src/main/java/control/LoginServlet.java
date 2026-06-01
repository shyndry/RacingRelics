package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.UtenteDAO;
import dao.UtenteDAOImpl;
import model.Utente;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private final UtenteDAO utenteDAO = new UtenteDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> errors = new ArrayList<>();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        email = validateField(email, "Email", errors);
        password = validateField(password, "Password", errors);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(request, response);
            return;
        }

        try {
            Utente utente = utenteDAO.doRetrieveByEmailPassword(email, password);

            if (utente != null) {
                request.getSession().setAttribute("utenteLoggato", utente);

                if ("ADMIN".equalsIgnoreCase(utente.getRuolo())) {
                    response.sendRedirect(request.getContextPath() + "/Catalogo");   
                } else {
                    response.sendRedirect(request.getContextPath() + "/Catalogo");
                }
            } else {
                errors.add("Email o password non validi.");
                request.setAttribute("errors", errors);
                request.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL durante il login: " + e.getMessage());
            errors.add("Si è verificato un errore tecnico. Riprova più tardi.");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(request, response);
        }
    }

    private String validateField(String value, String fieldName, List<String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.add("Il campo " + fieldName + " non può essere vuoto.");
            return "";
        }
        return value.trim();
    }
}