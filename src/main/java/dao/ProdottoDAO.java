package dao;

import java.sql.SQLException;
import java.util.List;
import model.Prodotto;


public interface ProdottoDAO {
    
    void doSave(Prodotto prodotto) throws SQLException;

    void doUpdate(Prodotto prodotto) throws SQLException;

    boolean doDelete(int idProdotto) throws SQLException;

    Prodotto doRetrieveByKey (int idProdotto) throws SQLException;

    List<Prodotto> doRetrieveAll() throws SQLException;

    List<Prodotto> doRetrieveByFiltri(String scuderia, Integer anno, String pilota) throws SQLException;

    List<Prodotto> doRetrieveAllAdmin() throws SQLException;

    
}
