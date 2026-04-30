package com.project.coursework2.model;

/**
 * Represents an admin user in the system.
 * Extends {@link User} with an admin level (1–3).
 * Admins can book any resource and manage users, resources, and bookings.
 *
 * @author Group 2
 * @version 1.0
 */
public class Admin extends User {

    private int adminLevel;

    /**
     * Creates an admin user.
     *
     * @param id         unique user ID
     * @param firstName  first name
     * @param lastName   last name
     * @param email      email address
     * @param adminLevel privilege level from 1 (lowest) to 3 (highest)
     */
    public Admin(String id, String firstName, String lastName, String email, int adminLevel) {
        super(id, firstName, lastName, email, "Admin");
        this.adminLevel = adminLevel;
    }

    /** @return the admin privilege level */
    public int getAdminLevel() { return adminLevel; }

    /** @param adminLevel the new privilege level */
    public void setAdminLevel(int adminLevel) { this.adminLevel = adminLevel; }

    /**
     * Changes the maximum number of active bookings allowed for a user.
     *
     * @param user  the user to update
     * @param limit the new booking limit
     */
    public void setBookingLimits(User user, int limit) {
        user.setMaxActiveBookings(limit);
    }
}
