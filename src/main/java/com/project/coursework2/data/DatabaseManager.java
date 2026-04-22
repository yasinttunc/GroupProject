package com.project.coursework2.data;

import com.project.coursework2.model.Booking;
import com.project.coursework2.model.User;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:university_booking (2).db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static User getUser(String email, String password) throws SQLException {
        String query = "SELECT userID, name, email, role, maxActiveBookings FROM User WHERE email = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("userID"),
                            rs.getString("name"),
                            "",
                            rs.getString("email"),
                            rs.getString("role")
                    );
                    user.setMaxActiveBookings(rs.getInt("maxActiveBookings"));
                    return user;
                }
            }
        }
        return null;
    }

    public static ArrayList<User> getAllUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        String query = "SELECT userID, name, email, role, maxActiveBookings FROM User";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("userID"),
                        rs.getString("name"),
                        "",
                        rs.getString("email"),
                        rs.getString("role")
                );
                user.setMaxActiveBookings(rs.getInt("maxActiveBookings"));
                users.add(user);
            }
        }

        return users;
    }

    public static boolean validateUser(String email, String password) throws SQLException {
        String query = "SELECT 1 FROM User WHERE email = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static String getUserRole(String email, String password) throws SQLException {
        String query = "SELECT role FROM User WHERE email = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        }

        return null;
    }

    public static ArrayList<Booking> getAllBookings() throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.bookingID, b.userID, b.resourceID, r.name AS resourceName, " +
                       "b.startTime, b.endTime, b.date, b.status, b.quantityBooked, b.createdAt " +
                       "FROM Booking b JOIN Resource r ON b.resourceID = r.resourceID";
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
}