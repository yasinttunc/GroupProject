package com.project.coursework2.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static com.project.coursework2.data.UserDatabaseManager.getConnection;
import com.project.coursework2.model.Booking;

public class BookingsDatabaseManager {

    public static ArrayList<Booking> getAllBookings() throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.bookingID, b.userID, b.resourceID, r.name AS resourceName, " +
                "b.startTime, b.endTime, b.date, b.status, b.quantityBooked, (b.createdDate || ' ' || b.createdTime) AS createdAt " +
                "FROM Booking b LEFT JOIN Resource r ON b.resourceID = r.resourceID";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getString("bookingID"),
                        rs.getString("userID"),
                        rs.getString("resourceID"),
                        rs.getString("resourceName"),
                        rs.getString("startTime"),
                        rs.getString("endTime"),
                        rs.getString("date"),
                        rs.getString("status"),
                        rs.getInt("quantityBooked"),
                        rs.getString("createdAt")
                );
                bookings.add(booking);
            }
        }
        return bookings;
    }

    /**
     * Returns only the bookings that belong to the current user.
     */
    public static ArrayList<Booking> getBookingsByUser(String userID) throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.bookingID, b.userID, b.resourceID, r.name AS resourceName, " +
                "b.startTime, b.endTime, b.date, b.status, b.quantityBooked, " +
                "(b.createdDate || ' ' || b.createdTime) AS createdAt " +
                "FROM Booking b LEFT JOIN Resource r ON b.resourceID = r.resourceID " +
                "WHERE b.userID = ? " +
                "ORDER BY b.date DESC, b.startTime DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking(
                            rs.getString("bookingID"),
                            rs.getString("userID"),
                            rs.getString("resourceID"),
                            rs.getString("resourceName"),
                            rs.getString("startTime"),
                            rs.getString("endTime"),
                            rs.getString("date"),
                            rs.getString("status"),
                            rs.getInt("quantityBooked"),
                            rs.getString("createdAt")
                    );
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    /**
     * Inserts a new booking into the database with 'Pending'.
     */
    public static void addBooking(String bookingID, String userID, String resourceID,
                                  String date, String startTime, String endTime) throws SQLException {
        String query = "INSERT INTO Booking (bookingID, userID, resourceID, date, startTime, endTime, " +
                    "status, quantityBooked, createdDate, createdTime) VALUES (?, ?, ?, ?, ?, ?, 'pending', 1, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookingID);
            stmt.setString(2, userID);
            stmt.setString(3, resourceID);
            stmt.setString(4, date);
            stmt.setString(5, startTime);
            stmt.setString(6, endTime);
            stmt.setString(7, LocalDate.now().toString());
            stmt.setString(8, LocalTime.now().toString());
            stmt.executeUpdate();
        }
    }

    /**
     * Updates the status of an existing booking.
     */
    public static void updateBookingStatus(String bookingID, String status) throws SQLException {
        String query = "UPDATE Booking SET status = ? WHERE bookingID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, bookingID);
            stmt.executeUpdate();
        }
    }
}