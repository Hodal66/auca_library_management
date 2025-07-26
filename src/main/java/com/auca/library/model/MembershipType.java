package com.auca.library.model;

import java.util.Date;
import java.util.UUID;

// MembershipType Model
class MembershipType {
    private UUID membershipTypeId;
    private int maxBooks;
    private String membershipName;
    private int price;
    
    public MembershipType() {
        this.membershipTypeId = UUID.randomUUID();
    }
    
    public MembershipType(String membershipName, int maxBooks, int price) {
        this();
        this.membershipName = membershipName;
        this.maxBooks = maxBooks;
        this.price = price;
    }
    
    // Getters and Setters
    public UUID getMembershipTypeId() { return membershipTypeId; }
    public void setMembershipTypeId(UUID membershipTypeId) { this.membershipTypeId = membershipTypeId; }
    
    public int getMaxBooks() { return maxBooks; }
    public void setMaxBooks(int maxBooks) { this.maxBooks = maxBooks; }
    
    public String getMembershipName() { return membershipName; }
    public void setMembershipName(String membershipName) { this.membershipName = membershipName; }
    
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}