package control;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Utente;

@WebFilter("/*")
public class AuthFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    
        String path = request.getServletPath();

        boolean richiedeAdmin = path.startsWith("/admin");
        boolean richiedeRegistrato = path.equals("/Checkout") || path.equals("/Ordini");

        if (!richiedeAdmin && !richiedeRegistrato) {
            chain.doFilter(request, response);
            return; 
        }

       
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteLoggato") : null;

        boolean autorizzato = false;

        if (utente != null) {
            String ruolo = utente.getRuolo(); 
            
            if (richiedeAdmin) {
                autorizzato = "ADMIN".equals(ruolo);
            } else if (richiedeRegistrato) {
                autorizzato = "ADMIN".equals(ruolo) || "REGISTRATO".equals(ruolo);
            }
        }

        if (autorizzato) {
            chain.doFilter(request, response); 
        } else {
            response.sendRedirect(request.getContextPath() + "/Login");
        }
    }
}