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
    
        try (Connection con = ConnessioneDB.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, utente.getEmail());
            ps.setString(2, toHash(utente.getPassword()));
            ps.setString(3, utente.getNome());
            ps.setString(4, utente.getCognome());
            ps.setString(5, utente.getRuolo());

            ps.executeUpdate();
            }
    }

    @Override
    public Utente doRetrieveByEmailPassword(String email, String password) throws SQLException{
        String query = "SELECT * FROM Utente WHERE email = ? AND password = ?";
        Utente utente = null;

        try (Connection con = ConnessioneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, email);
           
            ps.setString(2, toHash(password));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    utente = estraiUtenteDaResultSet(rs);
                }
            }
        }
        return utente; 
    }

    @Override
    public Utente doRetrieveByKey(int idUtente) throws SQLException {
        String query = "SELECT * FROM Utente WHERE id_utente = ?";
        Utente utente = null;
        
        try (Connection con = ConnessioneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, idUtente);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    utente = estraiUtenteDaResultSet(rs);
                }
            }
        }
        return utente;
    }

    @Override
    public void doUpdate(Utente utente) throws SQLException {
        String query = "UPDATE Utente SET email=?, password=?, nome=?, cognome=?, ruolo=? WHERE id_utente=?";
        
        try (Connection con = ConnessioneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setString(1, utente.getEmail());
            ps.setString(2, toHash(utente.getPassword()));
            ps.setString(3, utente.getNome());
            ps.setString(4, utente.getCognome());
            ps.setString(5, utente.getRuolo());
            ps.setInt(6, utente.getIdUtente());
            
            ps.executeUpdate();
        }
    }

    private Utente estraiUtenteDaResultSet(ResultSet rs) throws SQLException {
        Utente u = new Utente();
        u.setIdUtente(rs.getInt("id_utente"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setRuolo(rs.getString("ruolo"));
        return u;
    }

    private String toHash(String password) {
        if (password == null) {
            return null;
        }
        if (password.matches("^[0-9a-fA-F]{128}$")) {
            return password;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digestBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digestBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo di crittografia SHA-512 non trovato", e);
        }
    }
}
