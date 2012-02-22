package azkaban.database;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import azkaban.utils.Props;

public class MySQLConnection {
    private static final String USER_TABLE_NAME = "user";

    private static final Logger logger = Logger
            .getLogger(MySQLConnection.class);
    private final String host;
    private final String dbName;
    private final String user;
    private final String password;
    private final int port;

    private MysqlConnectionPoolDataSource dataSource;

    public MySQLConnection(Props props) {
        host = props.getString("mysql.host", "");
        dbName = props.getString("mysql.name", "");
        port = props.getInt("mysql.port", 3306);
        user = props.getString("mysql.user", "");
        password = props.getString("mysql.password", "");

        dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setDatabaseName(dbName);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        logger.info("Connecting to " + host + ":" + port + " db:" + dbName + " u:" + user + " p:" + password);
        
        checkTables();
    }

    private void checkTables() {
       // Set<String> tables = findTables(); 
    }

    @SuppressWarnings("unused")
    private MySQLQuery createQuery(String sql) {
        Connection conn = null;
        PreparedStatement statement = null;
        MySQLQuery query = null;

        try {
            conn = dataSource.getConnection();
            statement = (com.mysql.jdbc.PreparedStatement) conn
                    .prepareStatement(sql);
            query = new MySQLQuery(conn, statement);
        }
        catch (SQLException e) {
            e.printStackTrace();
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if (query != null) {
                query = null;
            }
        }

        return query;
    }

    private Set<String> findTables() {
        MySQLQuery tableQuery = createQuery("SHOW TABLES");
        Set<String> tables = new HashSet<String>();
        try { 
            ResultSet result = tableQuery.executeQuery();
            while(result.next()) {
                tables.add(result.getString(0));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tableQuery.close();
        return tables;
    }
    

}