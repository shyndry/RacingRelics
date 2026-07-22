package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Indirizzo;

public class IndirizzoDAOImpl implements IndirizzoDAO {
    @Override
    public int doSave(Indirizzo indirizzo) throws SQLException {
        try (Connection con = ConnessioneDB.getConnection()) {
            return doSave(indirizzo, con);
        }
    }

    @Override
    public int doSave(Indirizzo indirizzo, Connection con) throws SQLException {
        String query = "INSERT INTO Indirizzo (id_utente, via, citta, provincia, cap, tipologia) VALUES (?, ?, ?, ?, ?, ?)";
        int idGenerato = 0;
        try (PreparedStatement ps = con.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, indirizzo.getIdUtente());
            ps.setString(2, indirizzo.getVia());
            ps.setString(3, indirizzo.getCitta());
            ps.setString(4, indirizzo.getProvincia());
            ps.setString(5, indirizzo.getCap());
            ps.setString(6, indirizzo.getTipologia());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerato = rs.getInt(1);
                    indirizzo.setIdIndirizzo(idGenerato);
                }
            }
        }
        return idGenerato;
    }

    @Override
    public void doDelete (int idIndirizzo) throws SQLException{
        String query = "DELETE FROM Indirizzo WHERE id_indirizzo = ?";
        try (Connection con = ConnessioneDB.getConnection();
        PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, idIndirizzo);
            ps.executeUpdate();
        }
    }

    @Override
    public Indirizzo doRetrieveByKey(int idIndirizzo) throws SQLException{
        String query = "SELECT * FROM Indirizzo WHERE id_indirizzo = ?";
        Indirizzo indirizzo = null;
        try (Connection con = ConnessioneDB.getConnection();
        PreparedStatement ps = con.prepareStatement(query)){
            
            ps.setInt(1, idIndirizzo);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    indirizzo = estraiIndirizzo(rs);
                }
            }
        }
        return indirizzo;
    }

    @Override
    public List<Indirizzo> doRetrieveByUser(int idUtente) throws SQLException{
        String query = "SELECT * FROM Indirizzo WHERE id_utente = ?";
        List<Indirizzo> indirizzi = new ArrayList<>();
        try (Connection con = ConnessioneDB.getConnection();
        PreparedStatement ps = con.prepareStatement(query)){

            ps.setInt(1, idUtente);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    indirizzi.add(estraiIndirizzo(rs));
                }
            }
        }
        return indirizzi;
    }
    private Indirizzo estraiIndirizzo(ResultSet rs) throws SQLException {
        Indirizzo ind = new Indirizzo();
        ind.setIdIndirizzo(rs.getInt("id_indirizzo"));
        ind.setIdUtente(rs.getInt("id_utente"));
        ind.setVia(rs.getString("via"));
        ind.setCitta(rs.getString("citta"));
        ind.setProvincia(rs.getString("provincia"));
        ind.setCap(rs.getString("cap"));
        ind.setTipologia(rs.getString("tipologia"));
        return ind;
    }

}