package com.auca.library.model;

import java.util.Date;
import java.util.UUID;

class Borrower {
    private UUID borrowerId;
    private UUID bookId;
    private Date dueDate;
    private int fine;
    private int lateChargesFees;
    private Date pickupDate;
    private UUID readerId;
    private Date returnDate;
    
    public Borrower() {
        this.borrowerId = UUID.randomUUID();
        this.fine = 0;
        this.lateChargesFees = 0;
        this.pickupDate = new Date();
    }
    
    public Borrower(UUID bookId, UUID readerId, Date dueDate) {
        this();
        this.bookId = bookId;
        this.readerId = readerId;
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
    public UUID getBorrowerId() { return borrowerId; }
    public void setBorrowerId(UUID borrowerId) { this.borrowerId = borrowerId; }
    
    public UUID getBookId() { return bookId; }
    public void setBookId(UUID bookId) { this.bookId = bookId; }
    
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    
    public int getFine() { return fine; }
    public void setFine(int fine) { this.fine = fine; }
    
    public int getLateChargesFees() { return lateChargesFees; }
    public void setLateChargesFees(int lateChargesFees) { this.lateChargesFees = lateChargesFees; }
    
    public Date getPickupDate() { return pickupDate; }
    public void setPickupDate(Date pickupDate) { this.pickupDate = pickupDate; }
    
    public UUID getReaderId() { return readerId; }
    public void setReaderId(UUID readerId) { this.readerId = readerId; }
    
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
}