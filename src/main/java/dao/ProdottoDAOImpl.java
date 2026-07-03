package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import model.Prodotto;

public class ProdottoDAOImpl implements ProdottoDAO {
    
    private static DataSource ds;

    static {
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/RacingRelicsDB");
        } catch (NamingException e) {
            System.err.println("Errore nel lookup JNDI del DataSource: " + e.getMessage());
        }
    }

    @Override
    public void doSave(Prodotto prodotto) throws SQLException {
        String query = "INSERT INTO Prodotto (nome, descrizione, prezzo_attuale, quantita_disponibile, immagine_path, scuderia, pilota, anno_campionato, gran_premio, attivo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, prodotto.getNome());
            ps.setString(2, prodotto.getDescrizione());
            ps.setDouble(3, prodotto.getPrezzo());
            ps.setInt(4, prodotto.getQuantitaDisponibile());
            ps.setString(5, prodotto.getImmagine());
            ps.setString(6, prodotto.getScuderia());
            ps.setString(7, prodotto.getPilota());
            ps.setInt(8, prodotto.getAnno());
            ps.setString(9, prodotto.getGranPremio());
            ps.setBoolean(10, prodotto.isAttivo());
            ps.executeUpdate();
        }    
    }

    @Override
    public void doUpdate(Prodotto prodotto) throws SQLException {
        String query = "UPDATE Prodotto SET nome=?, descrizione=?, prezzo_attuale=?, quantita_disponibile=?, immagine_path=?, scuderia=?, pilota=?, anno_campionato=?, gran_premio=?, attivo=? WHERE id_prodotto=?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, prodotto.getNome());
            ps.setString(2, prodotto.getDescrizione());
            ps.setDouble(3, prodotto.getPrezzo());
            ps.setInt(4, prodotto.getQuantitaDisponibile());
            ps.setString(5, prodotto.getImmagine());
            ps.setString(6, prodotto.getScuderia());
            ps.setString(7, prodotto.getPilota());
            ps.setInt(8, prodotto.getAnno());
            ps.setString(9, prodotto.getGranPremio());
            ps.setBoolean(10, prodotto.isAttivo());
            ps.setInt(11, prodotto.getIdProdotto());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean doDelete(int idProdotto) throws SQLException {
        String query = "UPDATE Prodotto SET attivo = false WHERE id_prodotto = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idProdotto);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Prodotto doRetrieveByKey(int idProdotto) throws SQLException {
        String query = "SELECT * FROM Prodotto WHERE id_prodotto = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return estraiProdottoDaResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Prodotto> doRetrieveAll() throws SQLException {
        String query = "SELECT * FROM Prodotto WHERE attivo = true";
        List<Prodotto> prodotti = new ArrayList<>();
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                prodotti.add(estraiProdottoDaResultSet(rs));
            }
        }
        return prodotti;
    }

    @Override
    public List<Prodotto> doRetrieveByFiltri(String scuderia, Integer anno, String pilota) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM Prodotto WHERE attivo = true");
        
        if (scuderia != null && !scuderia.trim().isEmpty()) {
            query.append(" AND scuderia = ?");
        }
        if (anno != null) {
            query.append(" AND anno_campionato = ?");
        }
        if (pilota != null && !pilota.trim().isEmpty()) {
            query.append(" AND pilota = ?");
        }

        List<Prodotto> prodotti = new ArrayList<>();
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            if (scuderia != null && !scuderia.trim().isEmpty()) {
                ps.setString(paramIndex++, scuderia);
            }
            if (anno != null) {
                ps.setInt(paramIndex++, anno);
            }
            if (pilota != null && !pilota.trim().isEmpty()) {
                ps.setString(paramIndex++, pilota);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(estraiProdottoDaResultSet(rs));
                }
            }
        }
        return prodotti;
    }

    private Prodotto estraiProdottoDaResultSet(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto();
        p.setIdProdotto(rs.getInt("id_prodotto"));
        p.setNome(rs.getString("nome"));
        p.setDescrizione(rs.getString("descrizione"));
        p.setPrezzo(rs.getDouble("prezzo_attuale"));
        p.setQuantitaDisponibile(rs.getInt("quantita_disponibile"));
        p.setImmagine(rs.getString("immagine_path"));
        p.setScuderia(rs.getString("scuderia"));
        p.setPilota(rs.getString("pilota"));
        p.setAnno(rs.getInt("anno_campionato"));
        p.setGranPremio(rs.getString("gran_premio"));
        p.setAttivo(rs.getBoolean("attivo"));
        return p;
    }

    @Override
    public List<Prodotto> doRetrieveAllAdmin() throws SQLException {
    String query = "SELECT * FROM Prodotto";
    List<Prodotto> prodotti = new ArrayList<>();
    try (Connection con = ds.getConnection();
         PreparedStatement ps = con.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            prodotti.add(estraiProdottoDaResultSet(rs));
        }
    }
    return prodotti;
    }
}