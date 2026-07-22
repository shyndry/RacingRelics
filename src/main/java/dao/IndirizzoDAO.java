package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.Indirizzo;

public interface IndirizzoDAO {
    int doSave(Indirizzo indirizzo) throws SQLException;
    int doSave(Indirizzo indirizzo, Connection con) throws SQLException;
    void doDelete(int idIndirizzo) throws SQLException;
    Indirizzo doRetrieveByKey(int idIndirizzo) throws SQLException;
    List<Indirizzo> doRetrieveByUser(int idUtente) throws SQLException;
}

