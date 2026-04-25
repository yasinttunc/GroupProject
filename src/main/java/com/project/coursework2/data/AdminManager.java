package com.project.coursework2.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import com.project.coursework2.model.Booking;
import com.project.coursework2.model.User;

public class AdminManager {

    private static final String DB_URL = "jdbc:sqlite:university_booking (2).db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static User getUser(String email, String password) throws SQLException {
        String query = "SELECT userID, name, firstName, lastName, email, role, maxActiveBookings FROM User WHERE email = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

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

            stmt.setString(1, (firstName + " "+lastName));
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, email);
            stmt.setString(5, password);
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
                               String email, String password, String role, LocalDate createdDate, LocalTime createdTime ) throws SQLException {

        ArrayList<User> userList = UserDatabaseManager.getAllUsers();

        String prefix = "USR";
        int maxNum = userList.stream()
                .map(User::getUserID)
                .filter(id -> id != null && id.startsWith(prefix))
                .map(id -> id.substring(prefix.length()))
                .mapToInt(num -> { try { return Integer.parseInt(num); } catch (NumberFormatException e) { return 0; } })
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
            stmt.setString(6, password);
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
             while(rs.next()) {
                 String id = rs.getString("studentID");
                 if(id != null && id.startsWith("STU") && id.length() >= 10) {
                     try {
                         int num = Integer.parseInt(id.substring(7));
                         if (num > maxNum) maxNum = num;
                     } catch(NumberFormatException ignored) {}
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
        int maxNum = 10000;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryMax)) {
             while(rs.next()) {
                 String id = rs.getString("staffID");
                 if(id != null && id.startsWith("STF")) {
                     try {
                         int num = Integer.parseInt(id.substring(3));
                         if (num > maxNum) maxNum = num;
                     } catch(NumberFormatException ignored) {}
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
                .mapToInt(num -> { try { return Integer.parseInt(num); } catch (NumberFormatException e) { return 0; } })
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
        }
        catch (SQLException e) {
            System.out.println("Error deleting resource: " + e.getMessage());
            throw e;
        }
    }

    public void updateResource(String resourceId, String name, String type, String requiredRole, int maxBookingDuration,
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
             }
        catch (SQLException e) {
            System.out.println("Error updating resource: " + e.getMessage());
        }
    }

    //===========Bookings==============

    public static void updateBookingStatus(String bookingId, String status) throws SQLException {
        try(Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement("UPDATE Booking SET status = ? WHERE bookingID = ?")) {
            stmt.setString(1, status);
            stmt.setString(2, bookingId);
            stmt.executeUpdate();
        }
    }  


}
