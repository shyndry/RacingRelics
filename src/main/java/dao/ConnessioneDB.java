package dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnessioneDB {
    
    private static DataSource ds;

    static{
        try{
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/RacingRelicsDB");
        } catch (NamingException e){
            System.err.println("Errore nella configurazione del DS JNDI: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException{
        if (ds == null ){
            throw new SQLException("DS non configurato correttamente o non trovato");
        }
        return ds.getConnection();
    }
}
