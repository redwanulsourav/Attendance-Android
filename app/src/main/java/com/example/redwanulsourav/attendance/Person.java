package com.example.redwanulsourav.attendance;

import java.util.Map;

public class Person {
    private String firstName;
    private String lastName;
    private String email;
    private String id;
    private String roll;
    private byte[] image;


    public String getHas_image() {
        return has_image;
    }

    public void setHas_image(String has_image) {
        this.has_image = has_image;
    }

    private String has_image;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    Map<String,String> courses;

    public Map<String, String> getCourses() {
        return courses;
    }

    public void setCourses(Map<String, String> courses) {
        this.courses = courses;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person() {
    }
}
