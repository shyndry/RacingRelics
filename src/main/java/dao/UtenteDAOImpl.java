package dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Utente;

public class UtenteDAOImpl implements UtenteDAO {
    
    @Override
    public void doSave (Utente utente) throws SQLException{
        String query = "INSERT INTO Utente (email, password, nome, cognome, ruolo) VALUES (?, ?, ?, ?, ?)";
    }
}
