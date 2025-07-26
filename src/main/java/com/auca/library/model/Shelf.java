package com.auca.library.model;

import java.util.Date;
import java.util.UUID;

class Shelf {
    private UUID shelfId;
    private int availableStock;
    private String bookCategory;
    private int borrowedNumber;
    private int initialStock;
    private UUID roomId;
    
    public Shelf() {
        this.shelfId = UUID.randomUUID();
        this.borrowedNumber = 0;
    }
    
    public Shelf(String bookCategory, int initialStock, UUID roomId) {
        this();
        this.bookCategory = bookCategory;
        this.initialStock = initialStock;
        this.availableStock = initialStock;
        this.roomId = roomId;
    }
    
    // Getters and Setters
    public UUID getShelfId() { return shelfId; }
    public void setShelfId(UUID shelfId) { this.shelfId = shelfId; }
    
    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }
    
    public String getBookCategory() { return bookCategory; }
    public void setBookCategory(String bookCategory) { this.bookCategory = bookCategory; }
    
    public int getBorrowedNumber() { return borrowedNumber; }
    public void setBorrowedNumber(int borrowedNumber) { this.borrowedNumber = borrowedNumber; }
    
    public int getInitialStock() { return initialStock; }
    public void setInitialStock(int initialStock) { this.initialStock = initialStock; }
    
    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }
}
