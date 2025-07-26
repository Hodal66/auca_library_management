package com.auca.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/auca_library_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "Mhthodol@2022%"; // Change this to your PostgreSQL password
    
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC Driver not found", e);
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create tables
            createTables(stmt);
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTables(Statement stmt) throws SQLException {
        // Location table
        stmt.execute("CREATE TABLE IF NOT EXISTS location (" +
                    "location_code VARCHAR(20) PRIMARY KEY, " +
                    "location_id UUID UNIQUE, " +
                    "location_name VARCHAR(255), " +
                    "location_type VARCHAR(50), " +
                    "parent_id UUID" +
                    ")");
        
        // Person table
        stmt.execute("CREATE TABLE IF NOT EXISTS person (" +
                    "person_id UUID PRIMARY KEY, " +
                    "first_name VARCHAR(255), " +
                    "last_name VARCHAR(255), " +
                    "gender VARCHAR(10), " +
                    "phone_number VARCHAR(20)" +
                    ")");
        
        // User table
        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id UUID PRIMARY KEY, " +
                    "person_id UUID REFERENCES person(person_id), " +
                    "password VARCHAR(255), " +
                    "role VARCHAR(20), " +
                    "user_name VARCHAR(255), " +
                    "village_id UUID REFERENCES location(location_id)" +
                    ")");
        
        // Membership type table
        stmt.execute("CREATE TABLE IF NOT EXISTS membership_type (" +
                    "membership_type_id UUID PRIMARY KEY, " +
                    "max_books INTEGER, " +
                    "membership_name VARCHAR(50), " +
                    "price INTEGER" +
                    ")");
        
        // Membership table
        stmt.execute("CREATE TABLE IF NOT EXISTS membership (" +
                    "membership_id UUID PRIMARY KEY, " +
                    "expiring_time DATE, " +
                    "membership_code VARCHAR(50), " +
                    "membership_type_id UUID REFERENCES membership_type(membership_type_id), " +
                    "membership_status VARCHAR(20), " +
                    "reader_id UUID REFERENCES users(user_id), " +
                    "registration_date DATE" +
                    ")");
        
        // Room table
        stmt.execute("CREATE TABLE IF NOT EXISTS room (" +
                    "room_id UUID PRIMARY KEY, " +
                    "room_code VARCHAR(50), " +
                    "room_name VARCHAR(255)" +
                    ")");
        
        // Shelf table
        stmt.execute("CREATE TABLE IF NOT EXISTS shelf (" +
                    "shelf_id UUID PRIMARY KEY, " +
                    "available_stock INTEGER, " +
                    "book_category VARCHAR(255), " +
                    "borrowed_number INTEGER, " +
                    "initial_stock INTEGER, " +
                    "room_id UUID REFERENCES room(room_id)" +
                    ")");
        
        // Book table
        stmt.execute("CREATE TABLE IF NOT EXISTS book (" +
                    "book_id UUID PRIMARY KEY, " +
                    "book_status VARCHAR(20), " +
                    "edition INTEGER, " +
                    "isbn_code VARCHAR(50), " +
                    "publication_year DATE, " +
                    "publisher_name VARCHAR(255), " +
                    "shelf_id UUID REFERENCES shelf(shelf_id), " +
                    "title VARCHAR(255)" +
                    ")");
        
        // Borrower table
        stmt.execute("CREATE TABLE IF NOT EXISTS borrower (" +
                    "borrower_id UUID PRIMARY KEY, " +
                    "book_id UUID REFERENCES book(book_id), " +
                    "due_date DATE, " +
                    "fine INTEGER DEFAULT 0, " +
                    "late_charges_fees INTEGER DEFAULT 0, " +
                    "pickup_date DATE, " +
                    "reader_id UUID REFERENCES users(user_id), " +
                    "return_date DATE" +
                    ")");
        
        // Insert default membership types
        stmt.execute("INSERT INTO membership_type (membership_type_id, max_books, membership_name, price) " +
                    "VALUES " +
                    "('11111111-1111-1111-1111-111111111111', 5, 'GOLD', 50), " +
                    "('22222222-2222-2222-2222-222222222222', 3, 'SILVER', 30), " +
                    "('33333333-3333-3333-3333-333333333333', 2, 'STRIVER', 10) " +
                    "ON CONFLICT (membership_type_id) DO NOTHING");
    }
}