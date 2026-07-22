package model;

import java.io.Serializable;

public class ItemCarrello implements Serializable {
    private static final long serialVersionUID = 1L;

    private Prodotto prodotto;
    private int quantita;

    public ItemCarrello(Prodotto prodotto, int quantita) {
        this.prodotto = prodotto;
        this.quantita = quantita;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public void setProdotto(Prodotto prodotto) {
        this.prodotto = prodotto;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    // Calcolo del totale della riga basato su getPrezzo()
    public double getPrezzoTotale() {
        if (prodotto == null) {
            return 0.0;
        }
        return prodotto.getPrezzo() * quantita;
    }
}