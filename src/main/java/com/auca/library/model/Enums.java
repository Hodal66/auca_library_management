package com.auca.library.model;

public class Enums {
    
    public enum Gender {
        MALE, FEMALE
    }
    
    public enum LocationType {
        PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
    }
    
    public enum Role {
        STUDENT, MANAGER, TEACHER, DEAN, HOD, LIBRARIAN
    }
    
    public enum BookStatus {
        BORROWED, RESERVED, AVAILABLE
    }
    
    public enum Status {
        APPROVED, REJECTED, PENDING
    }
}