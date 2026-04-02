package com.project.coursework2.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String userID;
    private String name;
    private String email;
    private String role;
    private String password;
    private int maxActiveBookings;
    private List<String> certifications;
    private List<Booking> bookings;
    private static int totalUsers;

    public User(String id, String name, String email, String role){
        this.userID = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = "";
        maxActiveBookings = 3;
        this.certifications = new ArrayList<>();
        this.bookings = new ArrayList<>();
        totalUsers++;
    }

    public boolean requestBooking(){
        //Implement next week
        return true;
    }

    public void cancelBooking(){
        //Implement next week
    }
    // --- Getters and Setters ---

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMaxActiveBookings() { return maxActiveBookings; }
    public void setMaxActiveBookings(int max) { this.maxActiveBookings = max; }

    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certs) { this.certifications = certs; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public static int getTotalUsers() { return totalUsers; }
    public static void setTotalUsers(int total) { totalUsers = total; }


}
