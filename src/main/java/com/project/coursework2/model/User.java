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
 * @author Group 2
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

    /**
     * Creates a user without a display name.
     *
     * @param id        unique user ID
     * @param firstName first name
     * @param lastName  last name
     * @param email     email address
     * @param role      role string ("Student", "Staff", or "Admin")
     */
    public User(String id, String firstName, String lastName, String email, String role) {
        this.userID = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.password = "";
        maxActiveBookings = 3;
        this.bookings = new ArrayList<>();
    }

    /**
     * Creates a user with a combined display name field as well as first and last name.
     *
     * @param id        unique user ID
     * @param name      full display name
     * @param firstName first name
     * @param lastName  last name
     * @param email     email address
     * @param role      role string ("Student", "Staff", or "Admin")
     */
    public User(String id, String name, String firstName, String lastName, String email, String role) {
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

    /**
     * Placeholder for booking request logic.
     *
     * @return true always (not yet implemented)
     */
    public boolean requestBooking() { return true; }

    /** Placeholder for booking cancellation logic (not yet implemented). */
    public void cancelBooking() {}

    // --- Getters and Setters ---

    /** @return the unique user ID */
    public String getUserID() { return userID; }

    /** @param userID the new user ID */
    public void setUserID(String userID) { this.userID = userID; }

    /** @return the full display name */
    public String getName() { return name; }

    /** @param name the full display name */
    public void setName(String name) { this.name = name; }

    /** @return the first name */
    public String getFirstName() { return firstName; }

    /** @param firstName the new first name */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** @return the last name */
    public String getLastName() { return lastName; }

    /** @param lastName the new last name */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /** @return the email address */
    public String getEmail() { return email; }

    /** @param email the new email address */
    public void setEmail(String email) { this.email = email; }

    /** @return the role string ("Student", "Staff", or "Admin") */
    public String getRole() { return role; }

    /** @param role the new role string */
    public void setRole(String role) { this.role = role; }

    /** @return the hashed password stored for this user */
    public String getPassword() { return password; }

    /** @param password the new password value */
    public void setPassword(String password) { this.password = password; }

    /** @return the maximum number of concurrent active bookings allowed */
    public int getMaxActiveBookings() { return maxActiveBookings; }

    /** @param max the new booking limit */
    public void setMaxActiveBookings(int max) { this.maxActiveBookings = max; }

    /** @return the in-memory list of this user's bookings */
    public List<Booking> getBookings() { return bookings; }

    /** @param bookings the booking list to set */
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }



}
