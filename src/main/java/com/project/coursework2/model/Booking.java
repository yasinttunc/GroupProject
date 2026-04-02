package com.project.coursework2.model;

public class Booking {

    private String bookingID;
    private String userID;
    private String resourceID;
    private String startTime;
    private String endTime;
    private String date;
    private String status;      // defaults to 'pending'
    private int bookingCount;
    private String createdAt;

    public Booking(String bookingID, String userID, String resourceID, String startTime, String endTime, String date, String status, int bookingCount, String createdAt) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.resourceID = resourceID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.status = status;
        this.bookingCount = bookingCount;
        this.createdAt = createdAt;
    }

    public String getBookingID() { return bookingID; }
    public void setBookingID(String bookingID) { this.bookingID = bookingID; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getResourceID() { return resourceID; }
    public void setResourceID(String resourceID) { this.resourceID = resourceID; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getBookingCount() { return bookingCount; }
    public void setBookingCount(int bookingCount) { this.bookingCount = bookingCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
