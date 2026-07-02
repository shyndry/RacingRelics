package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import model.Ordine;

public class OrdineDAOImpl implements OrdineDAO {
    
    private static DataSource ds;

    static {
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/RacingRelicsDB");
        } catch (NamingException e) {
            System.err.println("Errore nel lookup JNDI del DataSource in OrdineDAO: " + e.getMessage());
        }
    }

    // Riceve la connessione dalla Servlet per poter far parte di una transazione
    @Override
    public int doSave(Ordine ordine, Connection con) throws SQLException {
        String query = "INSERT INTO Ordine (id_utente, id_indirizzo_consegna, stato) VALUES (?, ?, ?)";
        int idGenerato = 0;
        
        // NOTA: Non usiamo il try-with-resources sulla Connection qui, perché deve chiuderla la Servlet alla fine della transazione
        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, ordine.getIdUtente());
            ps.setInt(2, ordine.getIdIndirizzoConsegna());
            ps.setString(3, ordine.getStato());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerato = rs.getInt(1);
                    ordine.setIdOrdine(idGenerato);
                }
            }
        }
        return idGenerato;
    }

    @Override
    public void doSaveComposizione(int idOrdine, int idProdotto, int quantita, double prezzoAcquisto, Connection con) throws SQLException {
        String query = "INSERT INTO ComposizioneOrdine (id_ordine, id_prodotto, quantita, prezzo_acquisto) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idOrdine);
            ps.setInt(2, idProdotto);
            ps.setInt(3, quantita);
            ps.setDouble(4, prezzoAcquisto); 
            
            ps.executeUpdate();
        }
    }

    @Override
    public List<Ordine> doRetrieveByUser(int idUtente) throws SQLException {
        String query = "SELECT * FROM Ordine WHERE id_utente = ? ORDER BY data_ordine DESC";
        List<Ordine> ordini = new ArrayList<>();
        
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordini.add(estraiOrdine(rs));
                }
            }
        }
        return ordini;
    }

    @Override
    public List<Ordine> doRetrieveAll(String dataInizio, String dataFine) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM Ordine WHERE 1=1");
        
        if (dataInizio != null && !dataInizio.isEmpty()) {
            query.append(" AND data_ordine >= ?");
        }
        if (dataFine != null && !dataFine.isEmpty()) {
            query.append(" AND data_ordine <= ?");
        }
        query.append(" ORDER BY data_ordine DESC");
        
        List<Ordine> ordini = new ArrayList<>();
        
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            if (dataInizio != null && !dataInizio.isEmpty()) {
                ps.setString(paramIndex++, dataInizio + " 00:00:00");
            }
            if (dataFine != null && !dataFine.isEmpty()) {
                ps.setString(paramIndex++, dataFine + " 23:59:59");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordini.add(estraiOrdine(rs));
                }
            }
        }
        return ordini;
    }

    @Override
    public double doRetrieveTotaleOrdine(int idOrdine) throws SQLException {
    String query = "SELECT SUM(quantita * prezzo_acquisto) AS totale FROM ComposizioneOrdine WHERE id_ordine = ?";
    try (Connection con = ds.getConnection();
         PreparedStatement ps = con.prepareStatement(query)) {
        ps.setInt(1, idOrdine);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("totale");
            }
        }
    }
    return 0.0;
}
    private Ordine estraiOrdine(ResultSet rs) throws SQLException {
        Ordine o = new Ordine();
        o.setIdOrdine(rs.getInt("id_ordine"));
        o.setIdUtente(rs.getInt("id_utente"));
        o.setIdIndirizzoConsegna(rs.getInt("id_indirizzo_consegna"));
        o.setDataOrdine(rs.getTimestamp("data_ordine"));
        o.setStato(rs.getString("stato"));
        return o;
    }
}