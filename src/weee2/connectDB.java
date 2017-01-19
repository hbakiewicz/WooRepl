/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weee2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author hbakiewicz
 */
public class connectDB {

    Connection conn;

    public Connection dbConnect(String db_connect_string,
            String db_userid,
            String db_password) throws ClassNotFoundException, SQLException {

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conn = DriverManager.getConnection(db_connect_string,
                db_userid, db_password);

        return conn;
    }

}
