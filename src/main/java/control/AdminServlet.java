package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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

@WebServlet("/admin/dashboard")
public class AdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ProdottoDAO prodottoDAO = new ProdottoDAOImpl();
    private final OrdineDAO ordineDAO = new OrdineDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Recupero di eventuali messaggi di errore flash dal reindirizzamento POST
        String errorFlash = (String) session.getAttribute("adminError");
        if (errorFlash != null) {
            request.setAttribute("errorMessage", errorFlash);
            session.removeAttribute("adminError");
        }

        try {
            // L'admin usa il metodo ad hoc per vedere sia i cimeli attivi che quelli
            // archiviati
            List<Prodotto> prodotti = prodottoDAO.doRetrieveAllAdmin();

            String dataInizio = request.getParameter("dataInizio");
            String dataFine = request.getParameter("dataFine");
            String idUtenteStr = request.getParameter("idUtente");
            Integer idUtente = null;

            if (idUtenteStr != null && !idUtenteStr.trim().isEmpty()) {
                try {
                    idUtente = Integer.parseInt(idUtenteStr);
                } catch (NumberFormatException ignored) {
                }
            }

            List<Ordine> ordini = ordineDAO.doRetrieveAll(dataInizio, dataFine, idUtente);

            request.setAttribute("prodotti", prodotti);
            request.setAttribute("ordini", ordini);
            request.setAttribute("filtroDataInizio", dataInizio);
            request.setAttribute("filtroDataFine", dataFine);
            request.setAttribute("filtroIdUtente", idUtenteStr);

            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            System.err.println("Errore nel caricamento della dashboard amministrativa: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Catalogo");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        try {
            if ("delete".equals(action)) {
                String idStr = request.getParameter("idProdotto");
                if (idStr != null) {
                    int idProdotto = Integer.parseInt(idStr);
                    prodottoDAO.doDelete(idProdotto);
                }
            } else if ("insert".equals(action)) {
                Prodotto nuovoProdotto = new Prodotto();
                nuovoProdotto.setNome(request.getParameter("nome"));
                nuovoProdotto.setDescrizione(request.getParameter("descrizione"));

                // Allineamento geometrico con i nuovi nomi dei metodi del JavaBean Prodotto
                nuovoProdotto.setPrezzo(Double.parseDouble(request.getParameter("prezzo")));
                nuovoProdotto.setQuantitaDisponibile(Integer.parseInt(request.getParameter("quantita")));
                nuovoProdotto.setImmagine(request.getParameter("immaginePath"));
                nuovoProdotto.setScuderia(request.getParameter("scuderia"));
                nuovoProdotto.setPilota(request.getParameter("pilota"));
                nuovoProdotto.setAnno(Integer.parseInt(request.getParameter("anno")));

                nuovoProdotto.setGranPremio(request.getParameter("granPremio"));
                nuovoProdotto.setAttivo(true);

                prodottoDAO.doSave(nuovoProdotto);
            } else if ("update".equals(action)) {
                String idStr = request.getParameter("idProdotto");
                if (idStr != null) {
                    int idProdotto = Integer.parseInt(idStr);
                    Prodotto p = prodottoDAO.doRetrieveByKey(idProdotto);
                    if (p != null) {
                        p.setNome(request.getParameter("nome"));
                        p.setDescrizione(request.getParameter("descrizione"));
                        p.setPrezzo(Double.parseDouble(request.getParameter("prezzo")));
                        p.setQuantitaDisponibile(Integer.parseInt(request.getParameter("quantita")));
                        p.setImmagine(request.getParameter("immaginePath"));
                        p.setScuderia(request.getParameter("scuderia"));
                        p.setPilota(request.getParameter("pilota"));
                        p.setAnno(Integer.parseInt(request.getParameter("anno")));
                        p.setGranPremio(request.getParameter("granPremio"));

                        String attivoStr = request.getParameter("attivo");
                        if (attivoStr != null) {
                            p.setAttivo(Boolean.parseBoolean(attivoStr));
                        }

                        prodottoDAO.doUpdate(p);
                    }
                }
            }

        } catch (NumberFormatException | SQLException e) {
            System.err.println("Errore nell'esecuzione dell'operazione di backoffice: " + e.getMessage());
            session.setAttribute("adminError", "Impossibile completare l'operazione sul catalogo: dati non validi.");
        }

        // PRG garantito al 100%: svuota i dati del payload POST evitando inserimenti
        // duplicati al refresh
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}