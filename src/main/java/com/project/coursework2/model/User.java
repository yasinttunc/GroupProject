package com.project.coursework2.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Domain model representing a system user.
 * A user has a unique ID, display name, role ({@code Student}, {@code Staff},
 * or {@code Admin}), email, and a cap on concurrent active bookings.
 * Booking history is maintained as an in-memory list and is
 * populated by the data layer on demand.
 *
 * @author CRBAS Team
 * @version 1.0
 */
public class User {

    private String userID;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String password;
    private int maxActiveBookings;
    private List<Booking> bookings;

    public User(String id, String firstName, String lastName , String email, String role){
        this.userID = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.password = "";
        maxActiveBookings = 3;

        this.bookings = new ArrayList<>();

    }

    public User(String id, String name, String firstName, String lastName , String email, String role){
        this.userID = id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.password = "";
        maxActiveBookings = 3;

        this.bookings = new ArrayList<>();

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

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMaxActiveBookings() { return maxActiveBookings; }
    public void setMaxActiveBookings(int max) { this.maxActiveBookings = max; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }



}
