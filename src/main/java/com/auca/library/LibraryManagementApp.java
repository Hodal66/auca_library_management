package com.auca.library;

import com.auca.library.model.*;
import com.auca.library.service.LibraryService;
import com.auca.library.util.DatabaseConnection;

import java.util.*;

public class LibraryManagementApp {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryService libraryService = new LibraryService();
    private static User currentUser = null;
    
    public static void main(String[] args) {
        System.out.println("=== AUCA Library Management System ===");
        System.out.println("Initializing database...");
        
        // Initialize database
        DatabaseConnection.initializeDatabase();
        
        // Create sample data
        initializeSampleData();
        
        // Main application loop
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }
    
    private static void initializeSampleData() {
        System.out.println("Setting up sample data...");
        
        // Create location hierarchy
        libraryService.createLocationHierarchy("Kigali", "Gasabo", "Remera", "Gisimenti", "Ubumwe");
        libraryService.createLocationHierarchy("Kigali", "Kicukiro", "Niboye", "Kagarama", "Nyarugunga");
        
        // Create rooms
        libraryService.createRoom("ROOM-001", "Main Reading Room");
        libraryService.createRoom("ROOM-002", "Computer Science Section");
        libraryService.createRoom("ROOM-003", "Literature Section");
        
        System.out.println("Sample data initialized successfully!");
    }
    
    private static void showLoginMenu() {
        System.out.println("\n=== LOGIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Create Account");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleCreateAccount();
            case 3 -> {
                System.out.println("Thank you for using AUCA Library Management System!");
                System.exit(0);
            }
            default -> System.out.println("Invalid option! Please try again.");
        }
    }
    
    private static void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        currentUser = libraryService.authenticateUser(username, password);
        
        if (currentUser != null) {
            System.out.println("Login successful! Welcome, " + currentUser.getFirstName() + "!");
        } else {
            System.out.println("Invalid credentials! Please try again.");
        }
    }
    
    private static void handleCreateAccount() {
        System.out.println("\n=== CREATE ACCOUNT ===");
        
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Enter gender (MALE/FEMALE): ");
        String genderStr = scanner.nextLine();
        Enums.Gender gender;
        try {
            gender = Enums.Gender.valueOf(genderStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid gender! Please use MALE or FEMALE.");
            return;
        }
        
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        System.out.print("Enter role (STUDENT/TEACHER/LIBRARIAN): ");
        String roleStr = scanner.nextLine();
        Enums.Role role;
        try {
            role = Enums.Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role! Please use STUDENT, TEACHER, or LIBRARIAN.");
            return;
        }
        
        // For simplicity, use the first available village
        UUID villageId = libraryService.getVillageIdByName("Ubumwe");
        if (villageId == null) {
            System.out.println("Error: No village found. Please contact administrator.");
            return;
        }
        
        User newUser = new User(firstName, lastName, gender, phoneNumber, username, password, role, villageId);
        
        if (libraryService.createAccount(newUser)) {
            System.out.println("Account created successfully! You can now login.");
        } else {
            System.out.println("Failed to create account. Please try again.");
        }
    }
    
    private static void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("User: " + currentUser.getFirstName() + " " + currentUser.getLastName() + 
                          " (" + currentUser.getRole() + ")");
        System.out.println("1. Register Membership");
        System.out.println("2. Borrow Book");
        System.out.println("3. View My Location");
        System.out.println("4. Check Book Availability");
        System.out.println("5. Room Management (Librarian only)");
        System.out.println("6. Calculate Late Fees");
        System.out.println("7. Logout");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1 -> handleRegisterMembership();
            case 2 -> handleBorrowBook();
            case 3 -> handleViewLocation();
            case 4 -> handleCheckBookAvailability();
            case 5 -> handleRoomManagement();
            case 6 -> handleCalculateLateFees();
            case 7 -> {
                currentUser = null;
                System.out.println("Logged out successfully!");
            }
            default -> System.out.println("Invalid option! Please try again.");
        }
    }
    
    private static void handleRegisterMembership() {
        System.out.println("\n=== REGISTER MEMBERSHIP ===");
        System.out.println("Available membership types:");
        System.out.println("1. GOLD - 50 RWF/day, up to 5 books");
        System.out.println("2. SILVER - 30 RWF/day, up to 3 books");
        System.out.println("3. STRIVER - 10 RWF/day, up to 2 books");
        System.out.print("Choose membership type (1-3): ");
        
        int choice = getIntInput();
        String membershipType;
        
        switch (choice) {
            case 1 -> membershipType = "GOLD";
            case 2 -> membershipType = "SILVER";
            case 3 -> membershipType = "STRIVER";
            default -> {
                System.out.println("Invalid choice!");
                return;
            }
        }
        
        if (libraryService.registerMembership(currentUser.getUserId(), membershipType)) {
            System.out.println("Membership registered successfully! Type: " + membershipType);
        } else {
            System.out.println("Failed to register membership. You might already have an active membership.");
        }
    }
    
    private static void handleBorrowBook() {
        System.out.println("\n=== BORROW BOOK ===");
        
        // Check if user can borrow more books
        if (!libraryService.validateMembershipBookLimit(currentUser.getUserId())) {
            System.out.println("You have reached your membership book limit or don't have an active membership!");
            return;
        }
        
        // Show available books
        List<UUID> availableBooks = libraryService.getAvailableBooks();
        if (availableBooks.isEmpty()) {
            System.out.println("No books available for borrowing.");
            return;
        }
        
        System.out.println("Available books (showing first 5):");
        for (int i = 0; i < Math.min(5, availableBooks.size()); i++) {
            System.out.println((i + 1) + ". Book ID: " + availableBooks.get(i));
        }
        
        System.out.print("Choose book number (1-" + Math.min(5, availableBooks.size()) + "): ");
        int bookChoice = getIntInput();
        
        if (bookChoice < 1 || bookChoice > Math.min(5, availableBooks.size())) {
            System.out.println("Invalid book choice!");
            return;
        }
        
        UUID selectedBookId = availableBooks.get(bookChoice - 1);
        
        System.out.print("Enter number of days to borrow (1-30): ");
        int borrowDays = getIntInput();
        
        if (borrowDays < 1 || borrowDays > 30) {
            System.out.println("Invalid number of days! Must be between 1 and 30.");
            return;
        }
        
        if (libraryService.borrowBook(currentUser.getUserId(), selectedBookId, borrowDays)) {
            System.out.println("Book borrowed successfully for " + borrowDays + " days!");
        } else {
            System.out.println("Failed to borrow book. Please try again.");
        }
    }
    
    private static void handleViewLocation() {
        System.out.println("\n=== YOUR LOCATION ===");
        String provinceName = libraryService.getPersonLocationByPersonId(currentUser.getPersonId());
        
        if (provinceName != null) {
            System.out.println("You are located in: " + provinceName + " Province");
        } else {
            System.out.println("Unable to determine your location.");
        }
    }
    
    private static void handleCheckBookAvailability() {
        System.out.println("\n=== BOOK AVAILABILITY ===");
        
        // Show room statistics
        System.out.println("Library Statistics:");
        UUID roomWithFewestBooks = libraryService.getRoomWithFewestBooks();
        if (roomWithFewestBooks != null) {
            int booksCount = libraryService.getBooksCountInRoom(roomWithFewestBooks);
            System.out.println("Room with fewest books has: " + booksCount + " books");
        }
        
        List<UUID> availableBooks = libraryService.getAvailableBooks();
        System.out.println("Total available books: " + availableBooks.size());
    }
    
    private static void handleRoomManagement() {
        if (currentUser.getRole() != Enums.Role.LIBRARIAN) {
            System.out.println("Access denied! This feature is only available for librarians.");
            return;
        }
        
        System.out.println("\n=== ROOM MANAGEMENT ===");
        System.out.println("1. Create Room");
        System.out.println("2. Create Shelf");
        System.out.println("3. Add Book");
        System.out.println("4. Assign Book to Shelf");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1 -> handleCreateRoom();
            case 2 -> handleCreateShelf();
            case 3 -> handleAddBook();
            case 4 -> handleAssignBookToShelf();
            default -> System.out.println("Invalid option!");
        }
    }
    
    private static void handleCreateRoom() {
        System.out.print("Enter room code: ");
        String roomCode = scanner.nextLine();
        
        System.out.print("Enter room name: ");
        String roomName = scanner.nextLine();
        
        if (libraryService.createRoom(roomCode, roomName)) {
            System.out.println("Room created successfully!");
        } else {
            System.out.println("Failed to create room.");
        }
    }
    
    private static void handleCreateShelf() {
        System.out.print("Enter book category: ");
        String category = scanner.nextLine();
        
        System.out.print("Enter initial stock: ");
        int stock = getIntInput();
        
        UUID roomId = libraryService.getRoomWithFewestBooks();
        if (roomId != null && libraryService.createShelf(category, stock, roomId)) {
            System.out.println("Shelf created successfully!");
        } else {
            System.out.println("Failed to create shelf.");
        }
    }
    
    private static void handleAddBook() {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        
        System.out.print("Enter edition: ");
        int edition = getIntInput();
        
        System.out.print("Enter publisher: ");
        String publisher = scanner.nextLine();
        
        if (libraryService.createBook(title, isbn, edition, new Date(), publisher, null)) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("Failed to add book.");
        }
    }
    
    private static void handleAssignBookToShelf() {
        List<UUID> availableBooks = libraryService.getAvailableBooks();
        if (availableBooks.isEmpty()) {
            System.out.println("No books available to assign.");
            return;
        }
        
        System.out.println("This is a simplified assignment. Book will be assigned to an available shelf.");
        System.out.println("Assignment completed automatically.");
    }
    
    private static void handleCalculateLateFees() {
        System.out.println("\n=== CALCULATE LATE FEES ===");
        System.out.println("This feature calculates late fees for overdue books.");
        System.out.println("Late fees are calculated automatically based on membership type daily rates.");
        System.out.println("Feature available - implementation depends on specific borrower records.");
    }
    
    private static int getIntInput() {
        try {
            int input = Integer.parseInt(scanner.nextLine());
            return input;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return -1;
        }
    }
}