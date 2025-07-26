package com.auca.library.model;

import java.util.Date;
import java.util.UUID;

class Book {
    private UUID bookId;
    private Enums.BookStatus bookStatus;
    private int edition;
    private String isbnCode;
    private Date publicationYear;
    private String publisherName;
    private UUID shelfId;
    private String title;
    
    public Book() {
        this.bookId = UUID.randomUUID();
        this.bookStatus = Enums.BookStatus.AVAILABLE;
    }
    
    public Book(String title, String isbnCode, int edition, Date publicationYear, 
                String publisherName, UUID shelfId) {
        this();
        this.title = title;
        this.isbnCode = isbnCode;
        this.edition = edition;
        this.publicationYear = publicationYear;
        this.publisherName = publisherName;
        this.shelfId = shelfId;
    }
    
    // Getters and Setters
    public UUID getBookId() { return bookId; }
    public void setBookId(UUID bookId) { this.bookId = bookId; }
    
    public Enums.BookStatus getBookStatus() { return bookStatus; }
    public void setBookStatus(Enums.BookStatus bookStatus) { this.bookStatus = bookStatus; }
    
    public int getEdition() { return edition; }
    public void setEdition(int edition) { this.edition = edition; }
    
    public String getIsbnCode() { return isbnCode; }
    public void setIsbnCode(String isbnCode) { this.isbnCode = isbnCode; }
    
    public Date getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Date publicationYear) { this.publicationYear = publicationYear; }
    
    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
    
    public UUID getShelfId() { return shelfId; }
    public void setShelfId(UUID shelfId) { this.shelfId = shelfId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
