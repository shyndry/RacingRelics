package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.IndirizzoDAO;
import dao.IndirizzoDAOImpl;
import dao.ProdottoDAO;
import dao.ProdottoDAOImpl;
import model.Indirizzo;
import model.Prodotto;
import model.Utente;

@WebServlet("/Carrello")
public class CarrelloServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ProdottoDAO prodottoDAO = new ProdottoDAOImpl();
    private final IndirizzoDAO indirizzoDAO = new IndirizzoDAOImpl();

    public static class ItemCarrello {
        private Prodotto prodotto;
        private int quantita;

        public ItemCarrello(Prodotto prodotto, int quantita) {
            this.prodotto = prodotto;
            this.quantita = quantita;
        }

        public Prodotto getProdotto() {
            return prodotto;
        }

        public int getQuantita() {
            return quantita;
        }

        public double getPrezzoTotale() {
            return prodotto.getPrezzo() * quantita;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> carrello = (Map<Integer, Integer>) session.getAttribute("carrello");

        String errorFlash = (String) session.getAttribute("carrelloError");
        if (errorFlash != null) {
            request.setAttribute("errorMessage", errorFlash);
            session.removeAttribute("carrelloError");
        }

        Utente utenteLoggato = (Utente) session.getAttribute("utenteLoggato");
        if (utenteLoggato != null) {
            try {
                List<Indirizzo> listaIndirizzi = indirizzoDAO.doRetrieveByUser(utenteLoggato.getIdUtente());
                request.setAttribute("listaIndirizzi", listaIndirizzi);
            } catch (SQLException e) {
                System.err.println("Errore nel recupero indirizzi per checkout: " + e.getMessage());
            }
        }

        List<ItemCarrello> dettagliCarrello = new ArrayList<>();
        double totaleComplessivo = 0;

        if (carrello != null && !carrello.isEmpty()) {
            try {
                for (Map.Entry<Integer, Integer> entry : carrello.entrySet()) {
                    Prodotto p = prodottoDAO.doRetrieveByKey(entry.getKey());
                    if (p != null && p.isAttivo()) {
                        ItemCarrello item = new ItemCarrello(p, entry.getValue());
                        dettagliCarrello.add(item);
                        totaleComplessivo += item.getPrezzoTotale();
                    }
                }
            } catch (SQLException e) {
                System.err.println("Errore nel recupero dettagli carrello: " + e.getMessage());
                request.setAttribute("errorMessage", "Impossibile caricare i dati del tuo Garage.");
            }
        }

        request.setAttribute("dettagliCarrello", dettagliCarrello);
        request.setAttribute("totaleComplessivo", totaleComplessivo);

        request.getRequestDispatcher("/WEB-INF/views/common/carrello.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> carrello = (Map<Integer, Integer>) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new HashMap<>();
            session.setAttribute("carrello", carrello);
        }

        String action = request.getParameter("action");
        String idProdottoStr = request.getParameter("idProdotto");
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));

        String errorMessage = null;

        if (idProdottoStr != null && action != null) {
            try {
                int idProdotto = Integer.parseInt(idProdottoStr);

                if ("add".equals(action)) {
                    Prodotto p = prodottoDAO.doRetrieveByKey(idProdotto);
                    if (p != null && p.isAttivo()) {
                        int quantitaAttuale = carrello.getOrDefault(idProdotto, 0);
                        int quantitaDaAggiungere = 1;
                        String quantitaStr = request.getParameter("quantita");
                        if (quantitaStr != null && !quantitaStr.trim().isEmpty()) {
                            try {
                                quantitaDaAggiungere = Integer.parseInt(quantitaStr);
                            } catch (NumberFormatException ignored) {
                            }
                        }

                        if (quantitaDaAggiungere > 0 && quantitaAttuale + quantitaDaAggiungere <= p.getQuantitaDisponibile()) {
                            carrello.put(idProdotto, quantitaAttuale + quantitaDaAggiungere);
                        } else {
                            errorMessage = "Scorte insufficienti in magazzino per questo pezzo storico.";
                        }
                    }
                } else if ("remove".equals(action)) {
                    carrello.remove(idProdotto);
                } else if ("update".equals(action)) {
                    String quantitaStr = request.getParameter("quantita");
                    if (quantitaStr != null) {
                        int nuovaQuantita = Integer.parseInt(quantitaStr);
                        Prodotto p = prodottoDAO.doRetrieveByKey(idProdotto);

                        if (p != null && nuovaQuantita > 0 && nuovaQuantita <= p.getQuantitaDisponibile()) {
                            carrello.put(idProdotto, nuovaQuantita);
                        } else if (nuovaQuantita <= 0) {
                            carrello.remove(idProdotto);
                        } else {
                            errorMessage = "Quantità richiesta non disponibile in magazzino.";
                        }
                    }
                }
            } catch (NumberFormatException | SQLException e) {
                System.err.println("Errore nella gestione del carrello: " + e.getMessage());
                errorMessage = "Si è verificato un errore nell'aggiornamento del carrello.";
            }
        }

        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if (errorMessage != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(String.format("{\"status\":\"error\", \"message\":\"%s\"}", errorMessage));
            } else {
                double totaleComplessivo = 0.0;
                double totaleItem = 0.0;
                int targetId = (idProdottoStr != null) ? Integer.parseInt(idProdottoStr) : 0;

                for (Map.Entry<Integer, Integer> entry : carrello.entrySet()) {
                    try {
                        Prodotto p = prodottoDAO.doRetrieveByKey(entry.getKey());
                        if (p != null && p.isAttivo()) {
                            double subtotale = p.getPrezzo() * entry.getValue();
                            totaleComplessivo += subtotale;
                            if (entry.getKey() == targetId) {
                                totaleItem = subtotale;
                            }
                        }
                    } catch (SQLException ignored) {
                    }
                }

                response.getWriter().write(String.format(Locale.US,
                        "{\"status\":\"success\", \"totaleComplessivo\":%.2f, \"totaleItem\":%.2f, \"isVuoto\":%b}",
                        totaleComplessivo, totaleItem, carrello.isEmpty()));
            }
            return;
        }

        if (errorMessage != null) {
            session.setAttribute("carrelloError", errorMessage);
        }

        response.sendRedirect(request.getContextPath() + "/Carrello");
    }
}