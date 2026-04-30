package com.project.coursework2.model;

/**
 * Immutable data-transfer object representing a resource booking.
 * Maps directly to a row in the {@code Booking} database table.
 * Status values are {@code pending}, {@code confirmed}, {@code cancelled},
 * and {@code completed}.
 *
 * @author Group 2
 * @version 1.0
 */
public class Booking {

    private String bookingID;
    private String userID;
    private String resourceID;
    private String resourceName;
    private String startTime;
    private String endTime;
    private String date;
    private String status;      // defaults to 'pending'
    private int bookingCount;
    private String createdAt;

    /**
     * Creates a booking with all fields from the database row.
     *
     * @param bookingID    unique booking ID (e.g. "BKG1A2B3C4D")
     * @param userID       ID of the user who made the booking
     * @param resourceID   ID of the booked resource
     * @param resourceName display name of the booked resource
     * @param startTime    start time in "HH:MM" format
     * @param endTime      end time in "HH:MM" format
     * @param date         booking date in "YYYY-MM-DD" format
     * @param status       current status ("pending", "confirmed", "cancelled", "completed")
     * @param bookingCount quantity booked (relevant for equipment)
     * @param createdAt    timestamp when the booking was created
     */
    public Booking(String bookingID, String userID, String resourceID, String resourceName, String startTime, String endTime, String date, String status, int bookingCount, String createdAt) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.resourceID = resourceID;
        this.resourceName = resourceName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.status = status;
        this.bookingCount = bookingCount;
        this.createdAt = createdAt;
    }


    /** @return the unique booking ID */
    public String getBookingID() { return bookingID; }
    /** @param bookingID the new booking ID */
    public void setBookingID(String bookingID) { this.bookingID = bookingID; }

    /** @return the ID of the user who made this booking */
    public String getUserID() { return userID; }
    /** @param userID the user ID to set */
    public void setUserID(String userID) { this.userID = userID; }

    /** @return the ID of the booked resource */
    public String getResourceID() { return resourceID; }
    /** @param resourceID the resource ID to set */
    public void setResourceID(String resourceID) { this.resourceID = resourceID; }

    /** @return the display name of the booked resource */
    public String getResourceName() { return resourceName; }
    /** @param resourceName the resource name to set */
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    /** @return the start time in "HH:MM" format */
    public String getStartTime() { return startTime; }
    /** @param startTime the start time to set */
    public void setStartTime(String startTime) { this.startTime = startTime; }

    /** @return the end time in "HH:MM" format */
    public String getEndTime() { return endTime; }
    /** @param endTime the end time to set */
    public void setEndTime(String endTime) { this.endTime = endTime; }

    /** @return the booking date in "YYYY-MM-DD" format */
    public String getDate() { return date; }
    /** @param date the date to set */
    public void setDate(String date) { this.date = date; }

    /** @return the current status ("pending", "confirmed", "cancelled", "completed") */
    public String getStatus() { return status; }
    /** @param status the new status */
    public void setStatus(String status) { this.status = status; }

    /** @return the quantity booked (for equipment resources) */
    public int getBookingCount() { return bookingCount; }
    /** @param bookingCount the quantity to set */
    public void setBookingCount(int bookingCount) { this.bookingCount = bookingCount; }

    /** @return the timestamp when this booking was created */
    public String getCreatedAt() { return createdAt; }
    /** @param createdAt the creation timestamp to set */
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
