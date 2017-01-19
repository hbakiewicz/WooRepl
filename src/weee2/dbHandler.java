/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weee2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import eventlog.IEventJournal;

/**
 *
 * @author hbakiewicz
 */
public class dbHandler {

    private Connection conn;
    private final String connString;
    private final String user;
    private final String password;
    private final IEventJournal log;
    private final String dbName;

    public dbHandler(String connString, String user, String password, IEventJournal log, String dbName) throws SQLException {
        this.connString = connString;
        this.user = user;
        this.password = password;
        this.log = log;
        this.dbName = dbName;
        conn = dbConnect();
    }

    public final Connection dbConnect() throws SQLException {

        Connection lacze = DriverManager.getConnection(connString + ";databaseName=" + dbName, user, password);

        log.logEvent(Level.SEVERE, "connected to  " + lacze.getCatalog());

        return lacze;
    }

    public final Connection reConnect() throws SQLException {
        if (this.conn.isValid(10)) {
            return conn;
        } else {
            conn.close();
            conn = DriverManager.getConnection(connString + ";databaseName=" + dbName, user, password);

        }
        return conn;
    }

    public Connection getConn() {
        return conn;
    }

}
