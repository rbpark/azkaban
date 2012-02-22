package azkaban.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.PreparedStatement;

public class MySQLQuery {
    private final Connection conn;
    private final PreparedStatement statement;
    private ResultSet resultSet;
    
    public MySQLQuery(Connection conn, PreparedStatement statement) {
        this.conn = conn;
        this.statement = statement;
    }

    public void setString(int index, String str) throws SQLException {
        this.statement.setString(index, str);
    }
    
    public void setInt(int index, int val) throws SQLException {
        this.statement.setInt(index, val);
    }
    
    public ResultSet executeQuery() throws SQLException {
        resultSet = this.statement.executeQuery();
        return resultSet;
    }
    
    public void close() {
        if (resultSet != null) {
            try {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        if (statement != null) {
            try {
                if (!statement.isClosed()) {
                    statement.close();
                }
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}