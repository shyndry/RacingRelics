package model;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Carrello implements Serializable {

    private static final long serialVersionUID = 1L;

    public static class ElementoCarrello implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Prodotto prodotto;
        private int quantita;

        public ElementoCarrello(Prodotto prodotto, int quantita) {
            this.prodotto = prodotto;
            this.quantita = quantita;
        }

        public Prodotto getProdotto() { return prodotto; }
        public int getQuantita() { return quantita; }
        public void setQuantita(int quantita) { this.quantita = quantita; }
        public double getPrezzoTotale() { return prodotto.getPrezzo() * quantita; }
    }

    
    private final Map<Integer, ElementoCarrello> elementi;

    public Carrello() {
        this.elementi = new LinkedHashMap<>();
    }

    
    public void aggiungiProdotto(Prodotto prodotto, int quantita) {
        ElementoCarrello esistente = elementi.get(prodotto.getIdProdotto());
        if (esistente != null) {
            esistente.setQuantita(esistente.getQuantita() + quantita);
        } else {
            elementi.put(prodotto.getIdProdotto(), new ElementoCarrello(prodotto, quantita));
        }
    }

   
    public void rimuoviProdotto(int idProdotto) {
        elementi.remove(idProdotto);
    }

    
    public void modificaQuantita(int idProdotto, int nuovaQuantita) {
        ElementoCarrello esistente = elementi.get(idProdotto);
        if (esistente != null && nuovaQuantita > 0) {
            esistente.setQuantita(nuovaQuantita);
        }
    }

   
    public Collection<ElementoCarrello> getElementi() {
        return elementi.values();
    }

    
    public double getPrezzoTotaleComplessivo() {
        return elementi.values().stream()
                .mapToDouble(ElementoCarrello::getPrezzoTotale)
                .sum();
    }

    
    public void svuota() {
        elementi.clear();
    }

    
    public boolean isVuoto() {
        return elementi.isEmpty();
    }
}