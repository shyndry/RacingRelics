package control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import dao.OrdineDAO;
import dao.OrdineDAOImpl;
import dao.ProdottoDAO;
import dao.ProdottoDAOImpl;
import model.Ordine;
import model.Prodotto;

@WebServlet("/admin/dashboard")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
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
                nuovoProdotto.setPrezzo(Double.parseDouble(request.getParameter("prezzo")));
                nuovoProdotto.setQuantitaDisponibile(Integer.parseInt(request.getParameter("quantita")));
                
                String textImage = request.getParameter("immaginePath");
                String fallbackImage = (textImage != null && !textImage.trim().isEmpty()) ? textImage.trim() : "default.jpg";
                String savedImage = salvaImmagineCaricata(request, "immagineFile", fallbackImage);
                nuovoProdotto.setImmagine(savedImage);

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
                        
                        String textImage = request.getParameter("immaginePath");
                        String fallbackImage = (textImage != null && !textImage.trim().isEmpty()) ? textImage.trim() : p.getImmagine();
                        String savedImage = salvaImmagineCaricata(request, "immagineFile", fallbackImage);
                        p.setImmagine(savedImage);

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

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));

        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String errorFlash = (String) session.getAttribute("adminError");
            if (errorFlash != null) {
                session.removeAttribute("adminError");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(String.format("{\"status\":\"error\", \"message\":\"%s\"}", errorFlash));
            } else {
                response.getWriter().write(String.format("{\"status\":\"success\", \"action\":\"%s\"}", action));
            }
            return;
        }

        // PRG garantito al 100%: svuota i dati del payload POST evitando inserimenti
        // duplicati al refresh
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private String salvaImmagineCaricata(HttpServletRequest request, String inputName, String fallbackName)
            throws IOException, ServletException {
        try {
            Part filePart = request.getPart(inputName);
            if (filePart != null && filePart.getSize() > 0) {
                String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                if (submittedFileName != null && !submittedFileName.trim().isEmpty()) {
                    String cleanName = submittedFileName.replaceAll("[^a-zA-Z0-9\\._-]", "_");
                    String fileName = System.currentTimeMillis() + "_" + cleanName;

                    // 1. Salvataggio nella cartella di deployment del server
                    String uploadPath = request.getServletContext().getRealPath("/images/prodotti");
                    if (uploadPath != null) {
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        filePart.write(uploadPath + File.separator + fileName);
                    }

                    // 2. Salvataggio nella cartella sorgente del progetto webapp/images/prodotti per persistenza permanente
                    try {
                        String realRoot = request.getServletContext().getRealPath("/");
                        File srcFolder = null;
                        if (realRoot != null) {
                            File cur = new File(realRoot);
                            while (cur != null) {
                                File checkWebapp = new File(cur, "webapp/images/prodotti");
                                if (checkWebapp.exists() || (cur.getName().equalsIgnoreCase("RacingRelics") && new File(cur, "webapp").exists())) {
                                    srcFolder = new File(cur, "webapp/images/prodotti");
                                    break;
                                }
                                cur = cur.getParentFile();
                            }
                        }
                        if (srcFolder == null) {
                            srcFolder = new File("C:/Users/andry/Desktop/RacingRelics/webapp/images/prodotti");
                        }
                        if (!srcFolder.exists()) {
                            srcFolder.mkdirs();
                        }
                        filePart.write(srcFolder.getAbsolutePath() + File.separator + fileName);
                    } catch (Exception e) {
                        System.err.println("Impossibile salvare nella sorgente del progetto: " + e.getMessage());
                    }

                    return fileName;
                }
            }
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio dell'immagine: " + e.getMessage());
        }
        return fallbackName;
    }
}