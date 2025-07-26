package com.auca.library.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.mindrot.jbcrypt.BCrypt;

import com.auca.library.model.Enums;
import com.auca.library.model.Location;
import com.auca.library.model.User;
import com.auca.library.util.DatabaseConnection;

public class LibraryService {
    
    // 1. Create locations from Province, District, Sector, Cell, and Village
    public boolean createLocation(Location location) {
        String sql = "INSERT INTO location (location_code, location_id, location_name, location_type, parent_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, location.getLocationCode());
            pstmt.setObject(2, location.getLocationId());
            pstmt.setString(3, location.getLocationName());
            pstmt.setString(4, location.getLocationType().name());
            pstmt.setObject(5, location.getParentId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating location: " + e.getMessage());
            return false;
        }
    }
    
    // Create location hierarchy
    public boolean createLocationHierarchy(String provinceName, String districtName, 
                                         String sectorName, String cellName, String villageName) {
        try {
            // Create Province
            Location province = new Location("PROV-" + provinceName.toUpperCase(), 
                                           provinceName, Enums.LocationType.PROVINCE);
            if (!createLocation(province)) return false;
            
            // Create District
            Location district = new Location("DIST-" + districtName.toUpperCase(), 
                                           districtName, Enums.LocationType.DISTRICT, 
                                           province.getLocationId());
            if (!createLocation(district)) return false;
            
            // Create Sector
            Location sector = new Location("SECT-" + sectorName.toUpperCase(), 
                                         sectorName, Enums.LocationType.SECTOR, 
                                         district.getLocationId());
            if (!createLocation(sector)) return false;
            
            // Create Cell
            Location cell = new Location("CELL-" + cellName.toUpperCase(), 
                                       cellName, Enums.LocationType.CELL, 
                                       sector.getLocationId());
            if (!createLocation(cell)) return false;
            
            // Create Village
            Location village = new Location("VILL-" + villageName.toUpperCase(), 
                                          villageName, Enums.LocationType.VILLAGE, 
                                          cell.getLocationId());
            return createLocation(village);
            
        } catch (Exception e) {
            System.err.println("Error creating location hierarchy: " + e.getMessage());
            return false;
        }
    }
    
    // 2. Get province name by village id
    public String getProvinceNameByVillageId(UUID villageId) {
        String sql = "WITH RECURSIVE location_hierarchy AS (" +
                    "SELECT location_id, location_name, location_type, parent_id " +
                    "FROM location WHERE location_id = ? " +
                    "UNION ALL " +
                    "SELECT l.location_id, l.location_name, l.location_type, l.parent_id " +
                    "FROM location l " +
                    "INNER JOIN location_hierarchy lh ON l.location_id = lh.parent_id" +
                    ") " +
                    "SELECT location_name FROM location_hierarchy " +
                    "WHERE location_type = 'PROVINCE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, villageId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("location_name");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting province name: " + e.getMessage());
        }
        
        return null;
    }
    
    // 3. Get person's location (province name) by person ID
    public String getPersonLocationByPersonId(UUID personId) {
        String sql = "SELECT l.location_name " +
                    "FROM users u " +
                    "JOIN location l ON u.village_id = l.location_id " +
                    "WHERE u.person_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, personId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                UUID villageId = (UUID) rs.getObject("village_id");
                return getProvinceNameByVillageId(villageId);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting person location: " + e.getMessage());
        }
        
        return null;
    }
    
    // 4. Create account with hashed password
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean createAccount(User user) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert person first
            String personSql = "INSERT INTO person (person_id, first_name, last_name, gender, phone_number) " +
                              "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement personStmt = conn.prepareStatement(personSql)) {
                personStmt.setObject(1, user.getPersonId());
                personStmt.setString(2, user.getFirstName());
                personStmt.setString(3, user.getLastName());
                personStmt.setString(4, user.getGender().name());
                personStmt.setString(5, user.getPhoneNumber());
                personStmt.executeUpdate();
            }
            
            // Hash password and insert user
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            String userSql = "INSERT INTO users (user_id, person_id, password, role, user_name, village_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setObject(1, user.getUserId());
                userStmt.setObject(2, user.getPersonId());
                userStmt.setString(3, hashedPassword);
                userStmt.setString(4, user.getRole().name());
                userStmt.setString(5, user.getUserName());
                userStmt.setObject(6, user.getVillageId());
                userStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error creating account: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 5. Authenticate user
    public User authenticateUser(String username, String password) {
        String sql = "SELECT u.*, p.first_name, p.last_name, p.gender, p.phone_number " +
                    "FROM users u " +
                    "JOIN person p ON u.person_id = p.person_id " +
                    "WHERE u.user_name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    User user = new User();
                    user.setUserId((UUID) rs.getObject("user_id"));
                    user.setPersonId((UUID) rs.getObject("person_id"));
                    user.setUserName(rs.getString("user_name"));
                    user.setRole(Enums.Role.valueOf(rs.getString("role")));
                    user.setVillageId((UUID) rs.getObject("village_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setGender(Enums.Gender.valueOf(rs.getString("gender")));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    return user;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        
        return null;
    }
    
    // 6. Register for membership
    public boolean registerMembership(UUID userId, String membershipType) {
        String getMembershipTypeSql = "SELECT membership_type_id FROM membership_type WHERE membership_name = ?";
        String insertMembershipSql = "INSERT INTO membership (membership_id, membership_code, membership_type_id, " +
                                    "reader_id, registration_date, expiring_time, membership_status) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            UUID membershipTypeId = null;
            
            // Get membership type ID
            try (PreparedStatement pstmt = conn.prepareStatement(getMembershipTypeSql)) {
                pstmt.setString(1, membershipType.toUpperCase());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    membershipTypeId = (UUID) rs.getObject("membership_type_id");
                }
            }
            
            if (membershipTypeId == null) {
                System.err.println("Invalid membership type: " + membershipType);
                return false;
            }
            
            // Create membership
            try (PreparedStatement pstmt = conn.prepareStatement(insertMembershipSql)) {
                UUID membershipId = UUID.randomUUID();
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.YEAR, 1); // Membership expires in 1 year
                Date expiringTime = cal.getTime();
                
                pstmt.setObject(1, membershipId);
                pstmt.setString(2, "MEM-" + System.currentTimeMillis());
                pstmt.setObject(3, membershipTypeId);
                pstmt.setObject(4, userId);
                pstmt.setDate(5, new java.sql.Date(now.getTime()));
                pstmt.setDate(6, new java.sql.Date(expiringTime.getTime()));
                pstmt.setString(7, Enums.Status.APPROVED.name());
                
                return pstmt.executeUpdate() > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error registering membership: " + e.getMessage());
            return false;
        }
    }
    
    // 7. Borrow book
    public boolean borrowBook(UUID userId, UUID bookId, int borrowDays) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if book is available
            String checkBookSql = "SELECT book_status FROM book WHERE book_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkBookSql)) {
                pstmt.setObject(1, bookId);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next() || !rs.getString("book_status").equals("AVAILABLE")) {
                    System.err.println("Book is not available for borrowing");
                    return false;
                }
            }
            
            // Create borrower record
            String insertBorrowerSql = "INSERT INTO borrower (borrower_id, book_id, reader_id, pickup_date, due_date, fine, late_charges_fees) " +
                                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertBorrowerSql)) {
                UUID borrowerId = UUID.randomUUID();
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DAY_OF_MONTH, borrowDays);
                Date dueDate = cal.getTime();
                
                pstmt.setObject(1, borrowerId);
                pstmt.setObject(2, bookId);
                pstmt.setObject(3, userId);
                pstmt.setDate(4, new java.sql.Date(now.getTime()));
                pstmt.setDate(5, new java.sql.Date(dueDate.getTime()));
                pstmt.setInt(6, 0); // Fine initialized to zero
                pstmt.setInt(7, 0); // Late charges fees initialized to zero
                pstmt.executeUpdate();
            }
            
            // Update book status to BORROWED
            String updateBookSql = "UPDATE book SET book_status = 'BORROWED' WHERE book_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
                pstmt.setObject(1, bookId);
                pstmt.executeUpdate();
            }
            
            // Update shelf borrowed number
            String updateShelfSql = "UPDATE shelf SET borrowed_number = borrowed_number + 1, " +
                                   "available_stock = available_stock - 1 " +
                                   "WHERE shelf_id = (SELECT shelf_id FROM book WHERE book_id = ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(updateShelfSql)) {
                pstmt.setObject(1, bookId);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error borrowing book: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 8. Validate if user doesn't borrow more books than membership allows
    public boolean validateMembershipBookLimit(UUID userId) {
        String sql = "SELECT mt.max_books, " +
                    "(SELECT COUNT(*) FROM borrower b WHERE b.reader_id = ? AND b.return_date IS NULL) as current_borrowed " +
                    "FROM membership m " +
                    "JOIN membership_type mt ON m.membership_type_id = mt.membership_type_id " +
                    "WHERE m.reader_id = ? AND m.membership_status = 'APPROVED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, userId);
            pstmt.setObject(2, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int maxBooks = rs.getInt("max_books");
                int currentBorrowed = rs.getInt("current_borrowed");
                return currentBorrowed < maxBooks;
            }
            
        } catch (SQLException e) {
            System.err.println("Error validating membership book limit: " + e.getMessage());
        }
        
        return false;
    }
    
    // 9. Assign book to shelf
    public boolean assignBookToShelf(UUID bookId, UUID shelfId) {
        String sql = "UPDATE book SET shelf_id = ? WHERE book_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, shelfId);
            pstmt.setObject(2, bookId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error assigning book to shelf: " + e.getMessage());
            return false;
        }
    }
    
    // 10. Assign shelf to room
    public boolean assignShelfToRoom(UUID shelfId, UUID roomId) {
        String sql = "UPDATE shelf SET room_id = ? WHERE shelf_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, roomId);
            pstmt.setObject(2, shelfId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error assigning shelf to room: " + e.getMessage());
            return false;
        }
    }
    
    // 11. Check how many books are in a specific room
    public int getBooksCountInRoom(UUID roomId) {
        String sql = "SELECT SUM(s.initial_stock) as total_books " +
                    "FROM shelf s WHERE s.room_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_books");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting books count in room: " + e.getMessage());
        }
        
        return 0;
    }
    
    // 12. Get room with fewest books
    public UUID getRoomWithFewestBooks() {
        String sql = "SELECT r.room_id, COALESCE(SUM(s.initial_stock), 0) as total_books " +
                    "FROM room r " +
                    "LEFT JOIN shelf s ON r.room_id = s.room_id " +
                    "GROUP BY r.room_id " +
                    "ORDER BY total_books ASC " +
                    "LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return (UUID) rs.getObject("room_id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting room with fewest books: " + e.getMessage());
        }
        
        return null;
    }
    
    // 13. Calculate charge fees for late returns
    public int calculateLateFees(UUID borrowerId) {
        String sql = "SELECT b.due_date, b.return_date, mt.price " +
                    "FROM borrower b " +
                    "JOIN membership m ON b.reader_id = m.reader_id " +
                    "JOIN membership_type mt ON m.membership_type_id = mt.membership_type_id " +
                    "WHERE b.borrower_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, borrowerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Date dueDate = rs.getDate("due_date");
                Date returnDate = rs.getDate("return_date");
                int dailyFee = rs.getInt("price");
                
                // If not returned yet, use current date
                if (returnDate == null) {
                    returnDate = new Date();
                }
                
                // Calculate days late
                if (returnDate.after(dueDate)) {
                    long diffInMillies = returnDate.getTime() - dueDate.getTime();
                    long daysLate = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    
                    if (daysLate > 0) {
                        int lateFees = (int) (daysLate * dailyFee);
                        
                        // Update the borrower record with calculated fees
                        String updateSql = "UPDATE borrower SET late_charges_fees = ? WHERE borrower_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, lateFees);
                            updateStmt.setObject(2, borrowerId);
                            updateStmt.executeUpdate();
                        }
                        
                        return lateFees;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating late fees: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Utility methods for testing and management
    
    public boolean createRoom(String roomCode, String roomName) {
        String sql = "INSERT INTO room (room_id, room_code, room_name) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, UUID.randomUUID());
            pstmt.setString(2, roomCode);
            pstmt.setString(3, roomName);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating room: " + e.getMessage());
            return false;
        }
    }
    
    public boolean createShelf(String bookCategory, int initialStock, UUID roomId) {
        String sql = "INSERT INTO shelf (shelf_id, book_category, initial_stock, available_stock, borrowed_number, room_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, UUID.randomUUID());
            pstmt.setString(2, bookCategory);
            pstmt.setInt(3, initialStock);
            pstmt.setInt(4, initialStock);
            pstmt.setInt(5, 0);
            pstmt.setObject(6, roomId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating shelf: " + e.getMessage());
            return false;
        }
    }
    
    public boolean createBook(String title, String isbnCode, int edition, 
                             Date publicationYear, String publisherName, UUID shelfId) {
        String sql = "INSERT INTO book (book_id, title, isbn_code, edition, publication_year, publisher_name, shelf_id, book_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, UUID.randomUUID());
            pstmt.setString(2, title);
            pstmt.setString(3, isbnCode);
            pstmt.setInt(4, edition);
            pstmt.setDate(5, new java.sql.Date(publicationYear.getTime()));
            pstmt.setString(6, publisherName);
            pstmt.setObject(7, shelfId);
            pstmt.setString(8, Enums.BookStatus.AVAILABLE.name());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating book: " + e.getMessage());
            return false;
        }
    }
    
    public UUID getVillageIdByName(String villageName) {
        String sql = "SELECT location_id FROM location WHERE location_name = ? AND location_type = 'VILLAGE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, villageName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return (UUID) rs.getObject("location_id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting village ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<UUID> getAvailableBooks() {
        String sql = "SELECT book_id FROM book WHERE book_status = 'AVAILABLE' LIMIT 10";
        List<UUID> books = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add((UUID) rs.getObject("book_id"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting available books: " + e.getMessage());
        }
        
        return books;
    }
}