package ris.xmlParser.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = createConnection();
            }
            catch (IOException exc){
                throw new SQLException("Create connection exception", exc);
            }
        }
        return connection;
    }


    private static Connection createConnection() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(ConnectionManager.class.getResourceAsStream("/db.properties"));

        String url = properties.getProperty("URL");
        String login = properties.getProperty("LOGIN");
        String password = properties.getProperty("PASSWORD");

        connection = DriverManager.getConnection(url, login, password);
        connection.setAutoCommit(false);
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
