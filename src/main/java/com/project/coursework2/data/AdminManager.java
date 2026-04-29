package com.project.coursework2.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import com.project.coursework2.model.Booking;
import com.project.coursework2.model.User;
import com.project.coursework2.util.PasswordUtils;

import static com.project.coursework2.data.DatabaseConnection.getConnection;

/**
 * This class is used for admin database work.
 * It can add, update, delete and read users, resources, bookings and maintenance.
 * Passwords are hashed before they are saved in the database.
 * The database connection comes from {@link DatabaseConnection}.
 */
public class AdminManager {

    public static User getUser(String email, String password) throws SQLException {
        String query = "SELECT userID, name, firstName, lastName, email, role, maxActiveBookings FROM User WHERE email = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, PasswordUtils.hash(password));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("userID"),
                            rs.getString("name"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
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

        String query = "SELECT userID, name, firstName, lastName, email, role, maxActiveBookings FROM User";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("userID"),
                        rs.getString("name"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
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
            stmt.setString(2, PasswordUtils.hash(password));

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
            stmt.setString(2, PasswordUtils.hash(password));

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
        // Use LEFT JOIN in case a resource was deleted or mismatch occurs resulting in missing rows
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

    public static void updateUser(String userID, String firstName,
                                  String lastName, String email, String password, String role) throws SQLException {

        String query = "UPDATE User SET name = ?, firstName = ?, lastName = ?, email = ?, password = ? , role = ? WHERE userID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, (firstName + " " + lastName));
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, email);
            stmt.setString(5, PasswordUtils.hash(password));
            stmt.setString(6, role);
            stmt.setString(7, userID);

            stmt.executeUpdate();
        }

    }

    public static void deleteUser(String userID) throws SQLException {
        String query = "DELETE FROM User WHERE userID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.executeUpdate();
        }
    }

    public static String addUser(String firstName, String lastName,
                                 String email, String password, String role, LocalDate createdDate, LocalTime createdTime) throws SQLException {

        ArrayList<User> userList = UserDatabaseManager.getAllUsers();

        String prefix = "USR";
        int maxNum = userList.stream()
                .map(User::getUserID)
                .filter(id -> id != null && id.startsWith(prefix))
                .map(id -> id.substring(prefix.length()))
                .mapToInt(num -> {
                    try {
                        return Integer.parseInt(num);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        String generatedId = prefix + String.format("%03d", maxNum + 1);

        String query = "INSERT INTO User (userID, name, firstName, lastName, email, password, role, createdDate,createdTime) VALUES (?, ?, ?, ?, ?, ?, ?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, generatedId);
            stmt.setString(2, firstName + " " + lastName);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, email);
            stmt.setString(6, PasswordUtils.hash(password));
            stmt.setString(7, role);
            stmt.setString(8, createdDate.toString());
            stmt.setString(9, createdTime.toString());
            stmt.executeUpdate();
        }
        return generatedId;
    }

    public static void addStudent(String userID, String course, int yearOfStudy, String enrolledAt) throws SQLException {
        String queryMax = "SELECT studentID FROM Student";
        int maxNum = 0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryMax)) {
            while (rs.next()) {
                String id = rs.getString("studentID");
                if (id != null && id.startsWith("STU") && id.length() >= 10) {
                    try {
                        int num = Integer.parseInt(id.substring(7));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        String generatedStudentId = "STU" + LocalDate.now().getYear() + String.format("%03d", maxNum + 1);

        String query = "INSERT INTO Student (userID, studentID, course, yearOfStudy, enrolledAt) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setString(2, generatedStudentId);
            stmt.setString(3, course);
            stmt.setInt(4, yearOfStudy);
            stmt.setString(5, enrolledAt != null && !enrolledAt.isEmpty() ? enrolledAt : LocalDate.now().toString());
            stmt.executeUpdate();
        }
    }

    public static void addStaff(String userID, String jobTitle, String department) throws SQLException {
        String queryMax = "SELECT staffID FROM Staff";
        int maxNum = 0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryMax)) {
            while (rs.next()) {
                String id = rs.getString("staffID");
                if (id != null && id.startsWith("STF")) {
                    try {
                        int num = Integer.parseInt(id.substring(3));
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        String generatedStaffId = "STF" + (maxNum + 1);

        String query = "INSERT INTO Staff (userID, staffID, jobTitle, department) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setString(2, generatedStaffId);
            stmt.setString(3, jobTitle);
            stmt.setString(4, department);
            stmt.executeUpdate();
        }
    }

    public static void addAdmin(String userID, int adminLevel) throws SQLException {
        String query = "INSERT INTO Admin (userID, adminLevel) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setInt(2, adminLevel);
            stmt.executeUpdate();
        }
    }

    // ============ Resources ============

    public static void addResource(String name, String type, String requiredRole, int maxBookingDuration,
                                   String building, String room, boolean isActive) throws SQLException {

        ArrayList<ResourcesDatabaseManager.ResourceRow> resourceList = ResourcesDatabaseManager.getAllResources();

        String prefix = "RES";
        int maxNum = resourceList.stream()
                .map(ResourcesDatabaseManager.ResourceRow::getResourceId)
                .filter(id -> id != null && id.startsWith(prefix))
                .map(id -> id.substring(prefix.length()))
                .mapToInt(num -> {
                    try {
                        return Integer.parseInt(num);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        String generatedId = prefix + String.format("%03d", maxNum + 1);

        String query = "INSERT INTO Resource (resourceID, name, type, requiredRole, maxBookingDuration, building, room, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, generatedId);
            stmt.setString(2, name);
            stmt.setString(3, type);
            stmt.setString(4, requiredRole);
            stmt.setInt(5, maxBookingDuration);
            stmt.setString(6, building);
            stmt.setString(7, room);
            stmt.setInt(8, isActive ? 1 : 0);
            stmt.executeUpdate();
        }
    }

    public static void deleteResource(String resourceId) throws SQLException {
        String query = "DELETE FROM Resource WHERE resourceID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, resourceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting resource: " + e.getMessage());
            throw e;
        }
    }

    public static void updateResource(String resourceId, String name, String type, String requiredRole, int maxBookingDuration,
                                      String building, String room, boolean isActive) throws SQLException {
        String query = "UPDATE Resource SET name = ?, type = ?, requiredRole = ?, maxBookingDuration = ?, building = ?, room = ?, isActive = ? WHERE resourceID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setString(3, requiredRole);
            stmt.setInt(4, maxBookingDuration);
            stmt.setString(5, building);
            stmt.setString(6, room);
            stmt.setInt(7, isActive ? 1 : 0);
            stmt.setString(8, resourceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating resource: " + e.getMessage());
            throw e;
        }
    }

    //===========Bookings==============

    public static void updateBookingStatus(String bookingId, String status) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("UPDATE Booking SET status = ? WHERE bookingID = ?")) {
            stmt.setString(1, status);
            stmt.setString(2, bookingId);
            stmt.executeUpdate();
        }
    }

    public static void deleteBooking(String bookingId) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM Booking WHERE bookingID = ?")) {
            stmt.setString(1, bookingId);
            stmt.executeUpdate();
        }
    }

    public static void updateBooking(String bookingId, String date, String status, String startTime, String endTime, String updatedDate, String updatedTime) throws SQLException {
        String query = "UPDATE Booking SET date = ?, status = ?, startTime = ?, endTime = ?, updatedDate = ?, updatedTime = ? WHERE bookingID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, date);
            stmt.setString(2, status);
            stmt.setString(3, startTime);
            stmt.setString(4, endTime);
            stmt.setString(5, updatedDate);
            stmt.setString(6, updatedTime);
            stmt.setString(7, bookingId);
            stmt.executeUpdate();
        }
    }

    // =========== Maintenance ===========

    public static void addMaintenanceWindow(String resourceId, String startDate, String startTime, String endDate, String endTime, String reason) throws SQLException {
        String query = "INSERT INTO MaintenanceWindow (resourceID, startDate, startTime, endDate, endTime, reason) VALUES (?, ?, ?, ?, ?,?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, resourceId);
            stmt.setString(2, startDate);
            stmt.setString(3, startTime);
            stmt.setString(4, endDate);
            stmt.setString(5, endTime);
            stmt.setString(6, reason);
            stmt.executeUpdate();
        }
    }

    public static void updateResourceStatusMaintenance(String resourceID, String status,
                                                       String startDate, String startTime,
                                                       String endDate, String endTime) {
        boolean isMaintenance = status.equalsIgnoreCase("Maintenance");

        String resQuery = "UPDATE Resource SET isActive = ? WHERE resourceID = ?";

        try (Connection resConn = getConnection();
             PreparedStatement rps = resConn.prepareStatement(resQuery)) {
            rps.setBoolean(1, !isMaintenance);
            rps.setString(2, resourceID);
            rps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (!isMaintenance) {
            return;
        }

        String bookQuery = "UPDATE Booking " +
                "SET status = ?, updatedDate = ?, updatedTime = ? " +
                "WHERE resourceID = ? " +
                "AND LOWER(status) IN ('pending', 'confirmed') " +
                "AND datetime(date || ' ' || startTime) < datetime(? || ' ' || ?) " +
                "AND datetime(date || ' ' || endTime) > datetime(? || ' ' || ?)";

        try (Connection bookConn = getConnection();
             PreparedStatement bps = bookConn.prepareStatement(bookQuery)) {
            bps.setString(1, "cancelled");
            bps.setString(2, LocalDate.now().toString());
            bps.setString(3, LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            bps.setString(4, resourceID);

            bps.setString(5, endDate);
            bps.setString(6, endTime);

            bps.setString(7, startDate);
            bps.setString(8, startTime);

            bps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void cancelActiveBookingsByResource(String resourceId) throws SQLException {
        String query = "UPDATE Booking SET status = 'cancelled' WHERE resourceID = ? AND LOWER(status) IN ('pending', 'confirmed')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, resourceId);
            stmt.executeUpdate();
        }
    }

    public static void updateUserNoPassword(String userID, String firstName,
                                            String lastName, String email, String role) throws SQLException {
        String query = "UPDATE User SET name = ?, firstName = ?, lastName = ?, email = ?, role = ? WHERE userID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName + " " + lastName);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, email);
            stmt.setString(5, role);
            stmt.setString(6, userID);
            stmt.executeUpdate();
        }
    }
}
