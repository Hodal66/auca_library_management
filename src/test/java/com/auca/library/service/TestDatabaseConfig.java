// Add this to your LibraryService class or create a separate TestDatabaseConfig class

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDatabaseConfig {
    
    // Test database connection - using H2 in-memory database
    public static final String TEST_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    public static final String TEST_DB_USER = "sa";
    public static final String TEST_DB_PASSWORD = "";
    
    // Production database connection
    public static final String PROD_DB_URL = "jdbc:postgresql://localhost:5432/library_db";
    public static final String PROD_DB_USER = "postgres";
    public static final String PROD_DB_PASSWORD = "Mhthodol@2022%"; // Change this to your PostgreSQL password
    
    public static Connection getTestConnection() throws SQLException {
        return DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
    }
    
    public static Connection getProductionConnection() throws SQLException {
        return DriverManager.getConnection(PROD_DB_URL, PROD_DB_USER, PROD_DB_PASSWORD);
    }
}