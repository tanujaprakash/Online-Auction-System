import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/auction_system";  
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "root"; 

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Error connecting to the database", e);
        }
    }
}
