package dao;

import java.sql.SQLException;
import model.Utente;

public interface UtenteDAO {
    
    void doSave(Utente utente) throws SQLException;

    Utente doRetrieveByEmailPassword(String email, String password) throws SQLException;

    Utente doRetrieveByKey(int idUtente) throws SQLException;

    void doUpdate(Utente utente) throws SQLException;
    
}
