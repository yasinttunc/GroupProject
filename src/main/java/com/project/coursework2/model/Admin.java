package com.project.coursework2.model;

public class Admin extends User {

    private int adminLevel;
    public Admin(String id, String name, String email, int adminLevel) {
        super(id, name, email, "Admin");
        this.adminLevel = adminLevel;
    }

    //Getter and Setters
    public int getAdminLevel() { return adminLevel; }
    public void setAdminLevel(int adminLevel) { this.adminLevel = adminLevel; }

    public void setBookingLimits(User user, int limit) {
        user.setMaxActiveBookings(limit);
    }

}