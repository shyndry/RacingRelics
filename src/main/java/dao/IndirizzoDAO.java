package dao;

import java.sql.SQLException;
import java.util.List;
import model.Indirizzo;

public interface IndirizzoDAO {
    void doSave(Indirizzo indirizzo) throws SQLException;
    void doDelete(int idIndirizzo) throws SQLException;
    Indirizzo doRetrieveByKey(int idIndirizzo) throws SQLException;
    List<Indirizzo> doRetrieveByUser(int idUtente) throws SQLException;
}
