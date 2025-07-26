package com.auca.library.model;

import java.util.UUID;

public class Person {
    protected UUID personId;
    protected String firstName;
    protected String lastName;
    protected Enums.Gender gender;
    protected String phoneNumber;
    
    // Constructors
    public Person() {
        this.personId = UUID.randomUUID();
    }
    
    public Person(String firstName, String lastName, Enums.Gender gender, String phoneNumber) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
    public UUID getPersonId() { return personId; }
    public void setPersonId(UUID personId) { this.personId = personId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public Enums.Gender getGender() { return gender; }
    public void setGender(Enums.Gender gender) { this.gender = gender; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    @Override
    public String toString() {
        return "Person{" +
                "personId=" + personId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}