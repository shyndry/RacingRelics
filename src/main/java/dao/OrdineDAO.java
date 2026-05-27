package dao;

import java.sql.SQLException;
import java.util.List;
import model.Ordine;

public interface OrdineDAO {

    int doSave(Ordine ordine) throws SQLException;
    void doSaveComposizione(int idOrdine, int idProdotto, int quantita, double prezzoAcquisto) throws SQLException;
    List<Ordine> doRetrieveByUser(int idUtente) throws SQLException;
    List<Ordine> doRetrieveAll(String dataInizio, String dataFine) throws SQLException;
    
}
