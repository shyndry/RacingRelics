package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Ordine implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idOrdine;
    private int idUtente;
    private int idIndirizzoConsegna;
    private Timestamp dataOrdine; 
    private String stato;

    public Ordine() {
    }

    public int getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public int getIdIndirizzoConsegna() {
        return idIndirizzoConsegna;
    }

    public void setIdIndirizzoConsegna(int idIndirizzoConsegna) {
        this.idIndirizzoConsegna = idIndirizzoConsegna;
    }

    public Timestamp getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(Timestamp dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {
        return "Ordine{" +
                "idOrdine=" + idOrdine +
                ", idUtente=" + idUtente +
                ", dataOrdine=" + dataOrdine +
                ", stato='" + stato + '\'' +
                '}';
    }
}