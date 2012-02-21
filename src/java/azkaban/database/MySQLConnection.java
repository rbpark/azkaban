package azkaban.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import azkaban.utils.Props;

public class MySQLConnection {
    private static final Logger logger = Logger.getLogger(MySQLConnection.class);
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
	}
	
	public void connect() {
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
}