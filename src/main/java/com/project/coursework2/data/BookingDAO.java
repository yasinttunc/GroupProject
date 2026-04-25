/*
package com.project.coursework2.data;

import com.project.coursework2.model.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookingDAO {

    // 🔹 Step 1: Submit booking request (ALWAYS pending)
    public boolean submitBookingRequest(Booking booking) {
        String sql = "INSERT INTO Booking (bookingID, userID, resourceID, startTime, endTime, date, status, createdAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, booking.getBookingID());
            stmt.setString(2, booking.getUserID());
            stmt.setString(3, booking.getResourceID());
            stmt.setString(4, booking.getStartTime());
            stmt.setString(5, booking.getEndTime());
            stmt.setString(6, booking.getDate());

            // FORCE pending for all users
            stmt.setString(7, "pending");

            stmt.setString(8, booking.getCreatedAt());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Submit booking WITH access control check
    public boolean submitBookingWithCheck(Booking booking) {

        AccessControlDAO accessDAO = new AccessControlDAO();

        boolean canBook = accessDAO.canUserBookResource(
                booking.getUserID(),
                booking.getResourceID(),
                booking.getBookingID()
        );

        if (!canBook) {
            System.out.println("Booking denied: access control failed");
            return false;
        }

        //  If allowed → save booking as pending
        return submitBookingRequest(booking);
    }

    //  Step 3: Admin approves booking
    public boolean approveBooking(String bookingID) {
        String sql = "UPDATE Booking SET status = 'approved', updatedAt = datetime('now') WHERE bookingID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bookingID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Step 4: Admin rejects booking
    public boolean rejectBooking(String bookingID) {
        String sql = "UPDATE Booking SET status = 'rejected', updatedAt = datetime('now') WHERE bookingID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bookingID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}*/