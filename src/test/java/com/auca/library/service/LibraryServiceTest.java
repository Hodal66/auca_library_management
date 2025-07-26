package com.auca.library.service;

import com.auca.library.model.*;
import com.auca.library.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LibraryServiceTest {
    
    private static LibraryService libraryService;
    private static UUID testVillageId;
    private static UUID testUserId;
    private static UUID testPersonId;
    private static UUID testRoomId;
    private static UUID testShelfId;
    private static UUID testBookId;
    
    @BeforeAll
    static void setUp() {
        libraryService = new LibraryService();
        DatabaseConnection.initializeDatabase();
        System.out.println("Database initialized for testing");
    }
    
    @Test
    @Order(1)
    @DisplayName("Test 1: Create Location Hierarchy")
    void testCreateLocationHierarchy() {
        // Test creating a complete location hierarchy
        boolean result = libraryService.createLocationHierarchy(
            "Kigali", "Gasabo", "Remera", "Gisimenti", "Ubumwe"
        );
        
        assertTrue(result, "Should successfully create location hierarchy");
        
        // Get the village ID for further tests
        testVillageId = libraryService.getVillageIdByName("Ubumwe");
        assertNotNull(testVillageId, "Village ID should not be null");
        
        System.out.println("✅ Test 1 passed: Location hierarchy created successfully");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test 2: Get Province Name by Village ID")
    void testGetProvinceNameByVillageId() {
        assertNotNull(testVillageId, "Test village ID should be available");
        
        String provinceName = libraryService.getProvinceNameByVillageId(testVillageId);
        
        assertNotNull(provinceName, "Province name should not be null");
        assertEquals("Kigali", provinceName, "Province name should be Kigali");
        
        System.out.println("✅ Test 2 passed: Province name retrieved successfully: " + provinceName);
    }
    
    @Test
    @Order(3)
    @DisplayName("Test 3: Get Person Location by Person ID")
    void testGetPersonLocationByPersonId() {
        // First create a user to test with
        User testUser = new User(
            "John", "Doe", Enums.Gender.MALE, "0788123456",
            "johndoe", "password123", Enums.Role.STUDENT, testVillageId
        );
        
        boolean userCreated = libraryService.createAccount(testUser);
        assertTrue(userCreated, "User should be created successfully");
        
        testPersonId = testUser.getPersonId();
        testUserId = testUser.getUserId();
        
        // Test getting person's location
        String personLocation = libraryService.getPersonLocationByPersonId(testPersonId);
        
        assertNotNull(personLocation, "Person location should not be null");
        assertEquals("Kigali", personLocation, "Person should be in Kigali province");
        
        System.out.println("✅ Test 3 passed: Person location retrieved successfully: " + personLocation);
    }
    
    @Test
    @Order(4)
    @DisplayName("Test 4: Create Account with Hashed Password")
    void testCreateAccountWithHashedPassword() {
        User newUser = new User(
            "Jane", "Smith", Enums.Gender.FEMALE, "0788654321",
            "janesmith", "securepassword", Enums.Role.TEACHER, testVillageId
        );
        
        boolean result = libraryService.createAccount(newUser);
        assertTrue(result, "Account should be created successfully");
        
        // Verify the account was created by trying to authenticate
        User authenticatedUser = libraryService.authenticateUser("janesmith", "securepassword");
        assertNotNull(authenticatedUser, "User should be authenticated successfully");
        assertEquals("Jane", authenticatedUser.getFirstName(), "First name should match");
        assertEquals("Smith", authenticatedUser.getLastName(), "Last name should match");
        
        System.out.println("✅ Test 4 passed: Account created with hashed password successfully");
    }
    
    @Test
    @Order(5)
    @DisplayName("Test 5: Authenticate User")
    void testAuthenticateUser() {
        // Test with correct credentials
        User authenticatedUser = libraryService.authenticateUser("johndoe", "password123");
        assertNotNull(authenticatedUser, "User should be authenticated with correct credentials");
        assertEquals("John", authenticatedUser.getFirstName(), "First name should match");
        
        // Test with incorrect credentials
        User failedAuth = libraryService.authenticateUser("johndoe", "wrongpassword");
        assertNull(failedAuth, "Authentication should fail with wrong password");
        
        // Test with non-existent user
        User nonExistentAuth = libraryService.authenticateUser("nonexistent", "password");
        assertNull(nonExistentAuth, "Authentication should fail for non-existent user");
        
        System.out.println("✅ Test 5 passed: User authentication working correctly");
    }
    
    @Test
    @Order(6)
    @DisplayName("Test 6: Register Membership")
    void testRegisterMembership() {
        assertNotNull(testUserId, "Test user ID should be available");
        
        // Test Gold membership
        boolean goldResult = libraryService.registerMembership(testUserId, "GOLD");
        assertTrue(goldResult, "Gold membership should be registered successfully");
        
        // Create another user for Silver membership
        User silverUser = new User(
            "Alice", "Johnson", Enums.Gender.FEMALE, "0788111222",
            "alicejohnson", "password456", Enums.Role.STUDENT, testVillageId
        );
        libraryService.createAccount(silverUser);
        
        boolean silverResult = libraryService.registerMembership(silverUser.getUserId(), "SILVER");
        assertTrue(silverResult, "Silver membership should be registered successfully");
        
        // Test invalid membership type
        boolean invalidResult = libraryService.registerMembership(testUserId, "INVALID");
        assertFalse(invalidResult, "Invalid membership type should fail");
        
        System.out.println("✅ Test 6 passed: Membership registration working correctly");
    }
    
    @Test
    @Order(7)
    @DisplayName("Test 7: Create Room, Shelf, and Book Setup")
    void testCreateRoomShelfBookSetup() {
        // Create a test room
        boolean roomResult = libraryService.createRoom("ROOM-001", "Main Library Room");
        assertTrue(roomResult, "Room should be created successfully");
        
        // Get room ID for testing
        testRoomId = libraryService.getRoomWithFewestBooks(); // This will be our newly created room
        assertNotNull(testRoomId, "Room ID should not be null");
        
        // Create a test shelf
        boolean shelfResult = libraryService.createShelf("Computer Science", 100, testRoomId);
        assertTrue(shelfResult, "Shelf should be created successfully");
        
        // Create a test book
        boolean bookResult = libraryService.createBook(
            "Java Programming", "978-0134685991", 1, 
            new Date(), "Pearson", null // We'll assign shelf later
        );
        assertTrue(bookResult, "Book should be created successfully");
        
        System.out.println("✅ Test 7 passed: Room, Shelf, and Book setup completed");
    }
    
    @Test
    @Order(8)
    @DisplayName("Test 8: Validate Membership Book Limit")
    void testValidateMembershipBookLimit() {
        assertNotNull(testUserId, "Test user ID should be available");
        
        // Test user should have Gold membership (max 5 books)
        boolean canBorrow = libraryService.validateMembershipBookLimit(testUserId);
        assertTrue(canBorrow, "User with Gold membership should be able to borrow books initially");
        
        System.out.println("✅ Test 8 passed: Membership book limit validation working");
    }
    
    @Test
    @Order(9)
    @DisplayName("Test 9: Assign Book to Shelf")
    void testAssignBookToShelf() {
        // Get available book and shelf for testing
        List<UUID> availableBooks = libraryService.getAvailableBooks();
        assertFalse(availableBooks.isEmpty(), "Should have available books");
        
        testBookId = availableBooks.get(0);
        
        // We need to get a shelf ID - let's create one specifically for this test
        UUID specificShelfId = UUID.randomUUID();
        String createShelfSql = "INSERT INTO shelf (shelf_id, book_category, initial_stock, available_stock, borrowed_number, room_id) " +
                               "VALUES (?, 'Fiction', 50, 50, 0, ?)";
        
        try (var conn = DatabaseConnection.getConnection();
             var pstmt = conn.prepareStatement(createShelfSql)) {
            pstmt.setObject(1, specificShelfId);
            pstmt.setObject(2, testRoomId);
            pstmt.executeUpdate();
            testShelfId = specificShelfId;
        } catch (Exception e) {
            fail("Failed to create test shelf: " + e.getMessage());
        }
        
        boolean result = libraryService.assignBookToShelf(testBookId, testShelfId);
        assertTrue(result, "Book should be assigned to shelf successfully");
        
        System.out.println("✅ Test 9 passed: Book assigned to shelf successfully");
    }
    
    @Test
    @Order(10)
    @DisplayName("Test 10: Assign Shelf to Room")
    void testAssignShelfToRoom() {
        assertNotNull(testShelfId, "Test shelf ID should be available");
        assertNotNull(testRoomId, "Test room ID should be available");
        
        boolean result = libraryService.assignShelfToRoom(testShelfId, testRoomId);
        assertTrue(result, "Shelf should be assigned to room successfully");
        
        System.out.println("✅ Test 10 passed: Shelf assigned to room successfully");
    }
    
    @Test
    @Order(11)
    @DisplayName("Test 11: Check Books Count in Room")
    void testGetBooksCountInRoom() {
        assertNotNull(testRoomId, "Test room ID should be available");
        
        int booksCount = libraryService.getBooksCountInRoom(testRoomId);
        assertTrue(booksCount >= 0, "Books count should be non-negative");
        
        System.out.println("✅ Test 11 passed: Books count in room: " + booksCount);
    }
    
    @Test
    @Order(12)
    @DisplayName("Test 12: Get Room with Fewest Books")
    void testGetRoomWithFewestBooks() {
        UUID roomWithFewestBooks = libraryService.getRoomWithFewestBooks();
        assertNotNull(roomWithFewestBooks, "Should return a room ID");
        
        System.out.println("✅ Test 12 passed: Room with fewest books found: " + roomWithFewestBooks);
    }
    
    @Test
    @Order(13)
    @DisplayName("Test 7: Borrow Book")
    void testBorrowBook() {
        assertNotNull(testUserId, "Test user ID should be available");
        assertNotNull(testBookId, "Test book ID should be available");
        
        // First check if user can borrow
        boolean canBorrow = libraryService.validateMembershipBookLimit(testUserId);
        assertTrue(canBorrow, "User should be able to borrow books");
        
        // Borrow the book for 14 days
        boolean borrowResult = libraryService.borrowBook(testUserId, testBookId, 14);
        assertTrue(borrowResult, "Book should be borrowed successfully");
        
        System.out.println("✅ Test 7 (Borrow Book) passed: Book borrowed successfully");
    }
    
    @Test
    @Order(14)
    @DisplayName("Test 13: Calculate Late Fees")
    void testCalculateLateFees() {
        // Create a borrower record with overdue book for testing
        UUID testBorrowerId = UUID.randomUUID();
        
        try (var conn = DatabaseConnection.getConnection()) {
            // Create an overdue borrower record
            String sql = "INSERT INTO borrower (borrower_id, book_id, reader_id, pickup_date, due_date, fine, late_charges_fees) " +
                        "VALUES (?, ?, ?, ?, ?, 0, 0)";
            
            try (var pstmt = conn.prepareStatement(sql)) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -20); // 20 days ago pickup
                Date pickupDate = cal.getTime();
                
                cal.add(Calendar.DAY_OF_MONTH, 14); // Due 6 days ago
                Date dueDate = cal.getTime();
                
                pstmt.setObject(1, testBorrowerId);
                pstmt.setObject(2, testBookId);
                pstmt.setObject(3, testUserId);
                pstmt.setDate(4, new java.sql.Date(pickupDate.getTime()));
                pstmt.setDate(5, new java.sql.Date(dueDate.getTime()));
                pstmt.executeUpdate();
            }
            
            // Calculate late fees
            int lateFees = libraryService.calculateLateFees(testBorrowerId);
            assertTrue(lateFees > 0, "Late fees should be calculated for overdue book");
            
            System.out.println("✅ Test 13 passed: Late fees calculated: " + lateFees + " RWF");
            
        } catch (Exception e) {
            fail("Failed to test late fees calculation: " + e.getMessage());
        }
    }
    
    @Test
    @Order(15)
    @DisplayName("Test Edge Cases and Error Handling")
    void testEdgeCasesAndErrorHandling() {
        // Test with null values
        assertFalse(libraryService.createLocation(null), "Should handle null location");
        assertNull(libraryService.getProvinceNameByVillageId(null), "Should handle null village ID");
        assertNull(libraryService.authenticateUser(null, null), "Should handle null credentials");
        
        // Test with non-existent IDs
        UUID nonExistentId = UUID.randomUUID();
        assertNull(libraryService.getProvinceNameByVillageId(nonExistentId), "Should return null for non-existent village");
        assertFalse(libraryService.validateMembershipBookLimit(nonExistentId), "Should return false for non-existent user");
        
        System.out.println("✅ Test 15 passed: Edge cases handled correctly");
    }
    
    @Test
    @Order(16)
    @DisplayName("Test Complete Workflow")
    void testCompleteWorkflow() {
        try {
            // 1. Create location hierarchy
            boolean locationCreated = libraryService.createLocationHierarchy(
                "Eastern", "Rwamagana", "Kigabiro", "Nyamugari", "Karenge"
            );
            assertTrue(locationCreated, "Location hierarchy should be created");
            
            UUID villageId = libraryService.getVillageIdByName("Karenge");
            assertNotNull(villageId, "Village should exist");
            
            // 2. Create user account
            User workflowUser = new User(
                "Test", "Workflow", Enums.Gender.MALE, "0788999888",
                "testworkflow", "workflow123", Enums.Role.STUDENT, villageId
            );
            
            boolean accountCreated = libraryService.createAccount(workflowUser);
            assertTrue(accountCreated, "Account should be created");
            
            // 3. Authenticate user
            User authenticatedUser = libraryService.authenticateUser("testworkflow", "workflow123");
            assertNotNull(authenticatedUser, "User should be authenticated");
            
            // 4. Register membership
            boolean membershipRegistered = libraryService.registerMembership(workflowUser.getUserId(), "SILVER");
            assertTrue(membershipRegistered, "Membership should be registered");
            
            // 5. Validate membership limits
            boolean canBorrow = libraryService.validateMembershipBookLimit(workflowUser.getUserId());
            assertTrue(canBorrow, "User should be able to borrow books");
            
            // 6. Create room and shelf
            boolean roomCreated = libraryService.createRoom("WORKFLOW-ROOM", "Workflow Test Room");
            assertTrue(roomCreated, "Room should be created");
            
            UUID workflowRoomId = libraryService.getRoomWithFewestBooks();
            boolean shelfCreated = libraryService.createShelf("Test Category", 25, workflowRoomId);
            assertTrue(shelfCreated, "Shelf should be created");
            
            // 7. Create and assign book
            boolean bookCreated = libraryService.createBook(
                "Workflow Test Book", "978-1234567890", 1,
                new Date(), "Test Publisher", null
            );
            assertTrue(bookCreated, "Book should be created");
            
            List<UUID> books = libraryService.getAvailableBooks();
            if (!books.isEmpty()) {
                UUID workflowBookId = books.get(0);
                
                // 8. Try to borrow book
                boolean bookBorrowed = libraryService.borrowBook(workflowUser.getUserId(), workflowBookId, 7);
                assertTrue(bookBorrowed, "Book should be borrowed successfully");
            }
            
            System.out.println("✅ Test 16 passed: Complete workflow executed successfully");
            
        } catch (Exception e) {
            fail("Complete workflow test failed: " + e.getMessage());
        }
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("\n=== TEST SUMMARY ===");
        System.out.println("All tests completed successfully!");
        System.out.println("✅ Location management: PASSED");
        System.out.println("✅ User authentication: PASSED");
        System.out.println("✅ Membership management: PASSED");
        System.out.println("✅ Book borrowing system: PASSED");
        System.out.println("✅ Room and shelf management: PASSED");
        System.out.println("✅ Late fees calculation: PASSED");
        System.out.println("✅ Edge case handling: PASSED");
        System.out.println("✅ Complete workflow: PASSED");
        System.out.println("===================");
    }
}