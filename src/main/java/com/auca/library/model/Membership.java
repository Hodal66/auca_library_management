package com.auca.library.model;

import java.util.Date;
import java.util.UUID;

class Membership {
    private UUID membershipId;
    private Date expiringTime;
    private String membershipCode;
    private UUID membershipTypeId;
    private Enums.Status membershipStatus;
    private UUID readerId;
    private Date registrationDate;
    
    public Membership() {
        this.membershipId = UUID.randomUUID();
        this.registrationDate = new Date();
        this.membershipStatus = Enums.Status.PENDING;
    }
    
    // Getters and Setters
    public UUID getMembershipId() { return membershipId; }
    public void setMembershipId(UUID membershipId) { this.membershipId = membershipId; }
    
    public Date getExpiringTime() { return expiringTime; }
    public void setExpiringTime(Date expiringTime) { this.expiringTime = expiringTime; }
    
    public String getMembershipCode() { return membershipCode; }
    public void setMembershipCode(String membershipCode) { this.membershipCode = membershipCode; }
    
    public UUID getMembershipTypeId() { return membershipTypeId; }
    public void setMembershipTypeId(UUID membershipTypeId) { this.membershipTypeId = membershipTypeId; }
    
    public Enums.Status getMembershipStatus() { return membershipStatus; }
    public void setMembershipStatus(Enums.Status membershipStatus) { this.membershipStatus = membershipStatus; }
    
    public UUID getReaderId() { return readerId; }
    public void setReaderId(UUID readerId) { this.readerId = readerId; }
    
    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
}