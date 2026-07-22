package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.Ordine;

public interface OrdineDAO {

    int doSave(Ordine ordine, Connection con) throws SQLException;

    void doSaveComposizione(int idOrdine, int idProdotto, int quantita, double prezzoAcquisto, Connection con)
            throws SQLException;

    List<Ordine> doRetrieveByUser(int idUtente) throws SQLException;

    List<Ordine> doRetrieveAll(String dataInizio, String dataFine) throws SQLException;

    List<Ordine> doRetrieveAll(String dataInizio, String dataFine, Integer idUtente) throws SQLException;

    double doRetrieveTotaleOrdine(int idOrdine) throws SQLException;
}