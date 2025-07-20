package com.library.model;

public class Person {
    protected String person_id;
    protected String first_name;
    protected String last_name;
    protected Location location;

    public Person(String person_id, String firt_name, String last_name, Location location) {
        this.person_id = person_id;
        this.first_name = firt_name;
        this.last_name = last_name;
        this.location = location;
    }

    public String getPersonId() { return person_id; }
    public String getFirstName() { return first_name; }
    public String getLastName() { return last_name; }
    public Location getLocation() { return location; }
}
