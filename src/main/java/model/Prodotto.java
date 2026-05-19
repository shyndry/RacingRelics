package model;

import java.io.Serializable;


public class Prodotto implements Serializable {
    
    private static final long serialVersionUID = 1L;

   
    private int idProdotto;
    private String nome;
    private String descrizione;
    private double prezzoAttuale; 
    private int quantitaDisponibile;
    private String immaginePath;
    
    
    private String scuderia;
    private String pilota;
    private int annoCampionato;
    private String granPremio;
    private boolean attivo; 

    
    public Prodotto() {
    }


    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public double getPrezzoAttuale() {
        return prezzoAttuale;
    }

    public void setPrezzoAttuale(double prezzoAttuale) {
        this.prezzoAttuale = prezzoAttuale;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(int quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public String getImmaginePath() {
        return immaginePath;
    }

    public void setImmaginePath(String immaginePath) {
        this.immaginePath = immaginePath;
    }

    public String getScuderia() {
        return scuderia;
    }

    public void setScuderia(String scuderia) {
        this.scuderia = scuderia;
    }

    public String getPilota() {
        return pilota;
    }

    public void setPilota(String pilota) {
        this.pilota = pilota;
    }

    public int getAnnoCampionato() {
        return annoCampionato;
    }

    public void setAnnoCampionato(int annoCampionato) {
        this.annoCampionato = annoCampionato;
    }

    public String getGranPremio() {
        return granPremio;
    }

    public void setGranPremio(String granPremio) {
        this.granPremio = granPremio;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    
    @Override
    public String toString() {
        return "Prodotto{" +
                "idProdotto=" + idProdotto +
                ", nome='" + nome + '\'' +
                ", prezzoAttuale=" + prezzoAttuale +
                ", quantitaDisponibile=" + quantitaDisponibile +
                ", scuderia='" + scuderia + '\'' +
                ", pilota='" + pilota + '\'' +
                ", attivo=" + attivo +
                '}';
    }
}