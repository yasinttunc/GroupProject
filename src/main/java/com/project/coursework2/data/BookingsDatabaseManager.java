package com.project.coursework2.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.project.coursework2.data.DatabaseConnection.getConnection;

import com.project.coursework2.model.Booking;

/**
 * Data-access layer for the {@code Booking} table.
 * Provides methods for retrieving, creating, updating, and conflict-checking bookings.
 * All queries use parameterised {@link java.sql.PreparedStatement}s to prevent SQL injection.
 *
 * @author Group 2
 * @version 1.0
 */
public class BookingsDatabaseManager {

    /**
     * Returns every booking in the system, joined with the resource name.
     *
     * @return list of all {@link Booking} records
     * @throws SQLException on database access error
     */
    public static ArrayList<Booking> getAllBookings() throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.bookingID, b.userID, b.resourceID, r.name AS resourceName, " +
                "b.startTime, b.endTime, b.date, b.status, b.quantityBooked, " +
                "(b.createdDate || ' ' || b.createdTime) AS createdAt " +
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
     * Returns all bookings belonging to a specific user, ordered by date descending.
     *
     * @param userID the ID of the user whose bookings to retrieve
     * @return list of the user's {@link Booking} records
     * @throws SQLException on database access error
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
     * Checks whether a user role meets the minimum required role for a resource.
     *
     * @param userRole     the role of the user attempting to book
     * @param requiredRole the minimum role required by the resource
     * @return {@code true} if the user's role level is sufficient
     */
    public static boolean hasAccess(String userRole, String requiredRole) {
        return getRoleLevel(userRole) >= getRoleLevel(requiredRole);
    }

    /**
     * Maps a role name to a numeric permission level used for access comparisons.
     * Student = 1, Staff = 2, Admin = 3. Unknown or null roles return 0.
     *
     * @param role the role string to convert
     * @return the numeric permission level
     */
    private static int getRoleLevel(String role) {
        if (role == null) {
            return 0;
        }

        return switch (role.trim().toLowerCase()) {
            case "student" -> 1;
            case "staff" -> 2;
            case "admin" -> 3;
            default -> 0;
        };
    }

    /**
     * Checks whether a proposed booking overlaps with an existing pending or confirmed booking
     * for the same resource on the same date.
     *
     * @param resourceID the resource to check
     * @param date       the booking date (YYYY-MM-DD)
     * @param startTime  proposed start time (HH:mm)
     * @param endTime    proposed end time (HH:mm)
     * @return {@code true} if a conflict exists
     * @throws SQLException on database access error
     */
    public static boolean hasBookingConflict(String resourceID, String date,
                                             String startTime, String endTime) throws SQLException {
        return hasBookingConflictExcluding(null, resourceID, date, startTime, endTime);
    }

    /**
     * Same as {@link #hasBookingConflict} but ignores one specific booking.
     * Used when editing an existing booking so it does not conflict with itself.
     * Two bookings overlap when one starts before the other ends and ends after the other starts.
     *
     * @param bookingID  the booking ID to exclude from the check, or {@code null} to exclude nothing
     * @param resourceID the resource to check
     * @param date       the booking date (YYYY-MM-DD)
     * @param startTime  proposed start time (HH:mm)
     * @param endTime    proposed end time (HH:mm)
     * @return {@code true} if a conflicting booking exists
     * @throws SQLException on database access error
     */
    public static boolean hasBookingConflictExcluding(String bookingID, String resourceID, String date,
                                                      String startTime, String endTime) throws SQLException {
        String query = "SELECT COUNT(*) FROM Booking " +
                "WHERE resourceID = ? " +
                "AND date = ? " +
                "AND LOWER(status) IN ('pending', 'confirmed') " +
                "AND startTime < ? " +
                "AND endTime > ? " +
                "AND (? IS NULL OR bookingID <> ?)";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, resourceID);
            stmt.setString(2, date);
            stmt.setString(3, endTime);
            stmt.setString(4, startTime);
            stmt.setString(5, bookingID);
            stmt.setString(6, bookingID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int numberOfConflicts = rs.getInt(1);
                    return numberOfConflicts > 0;
                }
                return false;
            }
        }
    }

    /**
     * Checks whether the requested time slot falls inside a maintenance window for the resource.
     * A maintenance conflict occurs when the maintenance window overlaps with the booking
     * (maintenance starts before the booking ends AND maintenance ends after the booking starts).
     *
     * @param resourceID the resource to check
     * @param date       the booking date (YYYY-MM-DD)
     * @param startTime  proposed start time (HH:mm)
     * @param endTime    proposed end time (HH:mm)
     * @return {@code true} if the slot is inside a maintenance window
     * @throws SQLException on database access error
     */
    public static boolean hasMaintenanceConflict(String resourceID, String date,
                                                 String startTime, String endTime) throws SQLException {
        String query = "SELECT COUNT(*) FROM MaintenanceWindow " +
                "WHERE resourceID = ? " +
                "AND datetime(startDate || ' ' || startTime) < datetime(? || ' ' || ?) " +
                "AND datetime(endDate || ' ' || endTime) > datetime(? || ' ' || ?)";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, resourceID);
            stmt.setString(2, date);
            stmt.setString(3, endTime);
            stmt.setString(4, date);
            stmt.setString(5, startTime);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }


    /**
     * Inserts a new booking with {@code pending} status and a quantity of 1.
     *
     * @param bookingID  unique identifier for the new booking
     * @param userID     the user making the booking
     * @param resourceID the resource being booked
     * @param date       booking date (YYYY-MM-DD)
     * @param startTime  start time (HH:mm)
     * @param endTime    end time (HH:mm)
     * @throws SQLException on database access error
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
            stmt.setString(8, LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            stmt.executeUpdate();
        }
    }

    /**
     * Updates the selected resource, date and time for an existing booking.
     *
     * @param bookingID the booking to update
     * @param resourceID the new resource ID
     * @param date the new booking date
     * @param startTime the new start time
     * @param endTime the new end time
     * @throws SQLException if the database has a problem
     */
    /**
     * Updates the resource, date, and time of an existing booking and resets its status
     * to {@code pending} so that an admin must re-approve the change before it is confirmed.
     *
     * @param bookingID  the booking to update
     * @param resourceID the new resource ID
     * @param date       the new booking date (YYYY-MM-DD)
     * @param startTime  the new start time (HH:mm)
     * @param endTime    the new end time (HH:mm)
     * @throws SQLException on database access error
     */
    public static void updateBookingDetails(String bookingID, String resourceID,
                                            String date, String startTime, String endTime) throws SQLException {
        String query = "UPDATE Booking SET resourceID = ?, date = ?, startTime = ?, endTime = ?, " +
                "status = 'pending', updatedDate = ?, updatedTime = ? WHERE bookingID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, resourceID);
            stmt.setString(2, date);
            stmt.setString(3, startTime);
            stmt.setString(4, endTime);
            stmt.setString(5, LocalDate.now().toString());
            stmt.setString(6, LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            stmt.setString(7, bookingID);
            stmt.executeUpdate();
        }
    }

    /**
     * Returns the most recent bookings for a user across all statuses, newest first.
     *
     * @param userID the user whose history to fetch
     * @param limit  maximum number of rows to return
     * @return list of {@link Booking} records ordered by date and start time descending
     * @throws SQLException on database access error
     */
    public static List<Booking> getRecentBookings(String userID, int limit) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.bookingID, b.userID, b.resourceID, r.name AS resourceName, " +
                "b.startTime, b.endTime, b.date, b.status, b.quantityBooked, " +
                "(b.createdDate || ' ' || b.createdTime) AS createdAt " +
                "FROM Booking b LEFT JOIN Resource r ON b.resourceID = r.resourceID " +
                "WHERE b.userID = ? ORDER BY b.date DESC, b.startTime DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(new Booking(
                            rs.getString("bookingID"), rs.getString("userID"),
                            rs.getString("resourceID"), rs.getString("resourceName"),
                            rs.getString("startTime"), rs.getString("endTime"),
                            rs.getString("date"), rs.getString("status"),
                            rs.getInt("quantityBooked"), rs.getString("createdAt")));
                }
            }
        }
        return bookings;
    }

    /**
     * Returns all dates that have at least one pending or confirmed booking for the given resource.
     * Used to highlight busy dates in the booking date picker.
     *
     * @param resourceId the resource to query
     * @return set of {@link LocalDate} values with existing bookings
     * @throws SQLException on database access error
     */
    public static Set<LocalDate> getBookedDates(String resourceId) throws SQLException {
        Set<LocalDate> dates = new HashSet<>();
        String query = "SELECT DISTINCT date FROM Booking WHERE resourceID = ? AND LOWER(status) IN ('pending', 'confirmed')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, resourceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try { dates.add(LocalDate.parse(rs.getString("date"))); }
                    catch (Exception ignored) {}
                }
            }
        }
        return dates;
    }

    /**
     * Updates the status of an existing booking (e.g. to {@code confirmed} or {@code cancelled}).
     *
     * @param bookingID the booking to update
     * @param status    the new status value
     * @throws SQLException on database access error
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
