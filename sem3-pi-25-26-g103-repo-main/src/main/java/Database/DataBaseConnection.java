package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides access to the application database via JDBC.
 */
public class DataBaseConnection {

    /**
     * Creates a new JDBC connection to the configured Oracle instance.
     *
     * @return an open JDBC Connection
     * @throws SQLException if a connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:oracle:thin:@//vsgate-s1.dei.isep.ipp.pt:10930/XE";
        String user = "SYSTEM";
        String password = "Admin124";

        return DriverManager.getConnection(url, user, password);
    }
}
