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
 * Data-access layer for admin-level database operations.
 * Covers full CRUD for users (including role-specific sub-tables), resources,
 * bookings, and maintenance windows.
 * All password parameters are hashed with SHA-256 via {@link com.project.coursework2.util.PasswordUtils}
 * before being compared or written to the database.
 *
 * @author Group 2
 * @version 1.0
 */
public class AdminManager {

    /**
     * Looks up a user by email and password for authentication.
     * The password is hashed before the query runs.
     *
     * @param email    the user's email address
     * @param password the plaintext password to verify
     * @return the matching {@link User}, or {@code null} if the credentials are wrong
     * @throws SQLException on database access error
     */
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

    /**
     * Returns every user in the system without password data.
     *
     * @return list of all {@link User} records
     * @throws SQLException on database access error
     */
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

    /**
     * Checks whether an email and password match a record in the database.
     * Used to verify the current user's password before allowing sensitive actions
     * (e.g. editing a profile or deleting an account).
     *
     * @param email    the email address to look up
     * @param password the plaintext password to verify
     * @return {@code true} if the credentials are correct
     * @throws SQLException on database access error
     */
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

    /**
     * Returns the role of a user identified by email and password.
     *
     * @param email    the user's email address
     * @param password the plaintext password
     * @return the role string ("Student", "Staff", or "Admin"), or {@code null} if not found
     * @throws SQLException on database access error
     */
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

    /**
     * Returns every booking in the system, joined with the resource name.
     * Uses a LEFT JOIN so bookings for deleted resources still appear with a null name.
     *
     * @return list of all {@link Booking} records
     * @throws SQLException on database access error
     */
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

    /**
     * Updates a user's name, email, password, and role.
     * The password is SHA-256 hashed before being stored.
     * Also updates the {@code name} column with "firstName lastName".
     *
     * @param userID    the ID of the user to update
     * @param firstName the new first name
     * @param lastName  the new last name
     * @param email     the new email address
     * @param password  the new plaintext password (hashed before storage)
     * @param role      the new role ("Student", "Staff", or "Admin")
     * @throws SQLException on database access error
     */
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

    /**
     * Deletes a user directly from the User table.
     * Note: unlike {@link com.project.coursework2.data.UserDatabaseManager#deleteUser},
     * this version does not cascade-clean BookingHistory or Booking records first —
     * it relies on database-level ON DELETE CASCADE constraints being active.
     *
     * @param userID the ID of the user to delete
     * @throws SQLException on database access error
     */
    public static void deleteUser(String userID) throws SQLException {
        String query = "DELETE FROM User WHERE userID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.executeUpdate();
        }
    }

    /**
     * Inserts a new user into the {@code User} table and returns the generated ID.
     * The ID is generated by finding the highest existing "USR###" number and adding one
     * (e.g. if "USR005" is the highest, the new user gets "USR006").
     * After calling this method, call {@link #addStudent}, {@link #addStaff}, or {@link #addAdmin}
     * with the returned ID to fill in the role-specific sub-table.
     *
     * @param firstName   the user's first name
     * @param lastName    the user's last name
     * @param email       the user's email address
     * @param password    the plaintext password (hashed before storage)
     * @param role        the role string ("Student", "Staff", or "Admin")
     * @param createdDate the date the account was created
     * @param createdTime the time the account was created
     * @return the generated user ID (e.g. "USR006")
     * @throws SQLException on database access error
     */
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

    /**
     * Inserts a row into the {@code Student} sub-table for an existing user.
     * Generates a student ID in the format "STU{year}{###}" (e.g. "STU2026001").
     * If {@code enrolledAt} is null or blank, today's date is used.
     * Must be called after {@link #addUser} with the returned user ID.
     *
     * @param userID      the ID of the user who is a student
     * @param course      the course name the student is enrolled in
     * @param yearOfStudy the current year of study (1–7)
     * @param enrolledAt  the enrolment date (YYYY-MM-DD), or null to use today
     * @throws SQLException on database access error
     */
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

    /**
     * Inserts a row into the {@code Staff} sub-table for an existing user.
     * Generates a staff ID in the format "STF{###}" (e.g. "STF42").
     * Must be called after {@link #addUser} with the returned user ID.
     *
     * @param userID     the ID of the user who is a staff member
     * @param jobTitle   the staff member's job title (e.g. "Lecturer")
     * @param department the department the staff member belongs to
     * @throws SQLException on database access error
     */
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

    /**
     * Inserts a row into the {@code Admin} sub-table for an existing user.
     * Must be called after {@link #addUser} with the returned user ID.
     *
     * @param userID     the ID of the user who is an admin
     * @param adminLevel privilege level from 1 (lowest) to 3 (highest)
     * @throws SQLException on database access error
     */
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

    /**
     * Inserts a new resource into the {@code Resource} table.
     * Generates a resource ID in the format "RES{###}" (e.g. "RES012") by finding
     * the current highest ID and incrementing it.
     *
     * @param name               display name of the resource
     * @param type               resource type ("StudyRoom", "Equipment", or "Lab")
     * @param requiredRole       minimum role needed to book ("Student", "Staff", or "Admin")
     * @param maxBookingDuration maximum allowed booking length in minutes
     * @param building           building name where the resource is located
     * @param room               room name or number
     * @param isActive           whether the resource is available for booking
     * @param openingHours       free-text opening hours displayed on resource cards (e.g. "08:00 - 18:00")
     * @throws SQLException on database access error
     */
    public static void addResource(String name, String type, String requiredRole, int maxBookingDuration,
                                   String building, String room, boolean isActive, String openingHours) throws SQLException {

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

        String query = "INSERT INTO Resource (resourceID, name, type, requiredRole, maxBookingDuration, building, room, isActive, openingHours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            stmt.setString(9, openingHours);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a resource from the {@code Resource} table.
     * Call {@link #cancelActiveBookingsByResource} before this to cancel any
     * pending or confirmed bookings so they are not left orphaned.
     *
     * @param resourceId the ID of the resource to delete
     * @throws SQLException on database access error
     */
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

    /**
     * Updates all editable fields of an existing resource.
     *
     * @param resourceId         the ID of the resource to update
     * @param name               new display name
     * @param type               new type ("StudyRoom", "Equipment", or "Lab")
     * @param requiredRole       new minimum role required to book
     * @param maxBookingDuration new maximum booking duration in minutes
     * @param building           new building name
     * @param room               new room name or number
     * @param isActive           new availability status
     * @param openingHours       updated opening hours text (e.g. "08:00 - 18:00")
     * @throws SQLException on database access error
     */
    public static void updateResource(String resourceId, String name, String type, String requiredRole, int maxBookingDuration,
                                      String building, String room, boolean isActive, String openingHours) throws SQLException {
        String query = "UPDATE Resource SET name = ?, type = ?, requiredRole = ?, maxBookingDuration = ?, building = ?, room = ?, isActive = ?, openingHours = ? WHERE resourceID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setString(3, requiredRole);
            stmt.setInt(4, maxBookingDuration);
            stmt.setString(5, building);
            stmt.setString(6, room);
            stmt.setInt(7, isActive ? 1 : 0);
            stmt.setString(8, openingHours);
            stmt.setString(9, resourceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating resource: " + e.getMessage());
            throw e;
        }
    }

    //===========Bookings==============

    /**
     * Sets a new status on an existing booking (e.g. "confirmed" or "cancelled").
     *
     * @param bookingId the ID of the booking to update
     * @param status    the new status value
     * @throws SQLException on database access error
     */
    public static void updateBookingStatus(String bookingId, String status) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("UPDATE Booking SET status = ? WHERE bookingID = ?")) {
            stmt.setString(1, status);
            stmt.setString(2, bookingId);
            stmt.executeUpdate();
        }
    }

    /**
     * Permanently removes a booking record from the database.
     *
     * @param bookingId the ID of the booking to delete
     * @throws SQLException on database access error
     */
    public static void deleteBooking(String bookingId) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM Booking WHERE bookingID = ?")) {
            stmt.setString(1, bookingId);
            stmt.executeUpdate();
        }
    }

    /**
     * Updates the date, time, and status of an existing booking and records the update timestamp.
     *
     * @param bookingId   the ID of the booking to update
     * @param date        the new booking date (YYYY-MM-DD)
     * @param status      the new status value
     * @param startTime   the new start time (HH:mm)
     * @param endTime     the new end time (HH:mm)
     * @param updatedDate the date this update was made (YYYY-MM-DD)
     * @param updatedTime the time this update was made (HH:mm:ss)
     * @throws SQLException on database access error
     */
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

    /**
     * Inserts a new maintenance window for a resource.
     * While the window is active, {@link com.project.coursework2.data.BookingsDatabaseManager#hasMaintenanceConflict}
     * will block new bookings from overlapping with it.
     *
     * @param resourceId the ID of the resource under maintenance
     * @param startDate  the first day of the maintenance period (YYYY-MM-DD)
     * @param startTime  the start time on that day (HH:mm)
     * @param endDate    the last day of the maintenance period (YYYY-MM-DD)
     * @param endTime    the end time on that day (HH:mm)
     * @param reason     a short description of why maintenance is happening (can be empty)
     * @throws SQLException on database access error
     */
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

    /**
     * Marks a resource as inactive when status is "Maintenance", or active otherwise.
     * When switching to maintenance mode it also cancels all pending and confirmed bookings
     * that overlap with the given maintenance window.
     *
     * @param resourceID the ID of the resource to update
     * @param status     "Maintenance" to deactivate the resource, anything else to reactivate it
     * @param startDate  start date of the maintenance period (YYYY-MM-DD)
     * @param startTime  start time of the maintenance period (HH:mm)
     * @param endDate    end date of the maintenance period (YYYY-MM-DD)
     * @param endTime    end time of the maintenance period (HH:mm)
     */
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

    /**
     * Cancels all pending or confirmed bookings for a resource that overlap with a given time range.
     * Called automatically after adding a maintenance window so that users with conflicting
     * bookings are notified of the cancellation via the booking status change.
     * A booking overlaps if it starts before the maintenance ends AND ends after the maintenance starts.
     *
     * @param resourceID the ID of the resource under maintenance
     * @param startDate  start date of the maintenance window (YYYY-MM-DD)
     * @param startTime  start time of the maintenance window (HH:mm)
     * @param endDate    end date of the maintenance window (YYYY-MM-DD)
     * @param endTime    end time of the maintenance window (HH:mm)
     * @throws SQLException on database access error
     */
    public static void cancelBookingsForMaintenanceWindow(String resourceID,
                                                          String startDate, String startTime,
                                                          String endDate, String endTime) throws SQLException {
        String query = "UPDATE Booking " +
                "SET status = ?, updatedDate = ?, updatedTime = ? " +
                "WHERE resourceID = ? " +
                "AND LOWER(status) IN ('pending', 'confirmed') " +
                "AND datetime(date || ' ' || startTime) < datetime(? || ' ' || ?) " +
                "AND datetime(date || ' ' || endTime) > datetime(? || ' ' || ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "cancelled");
            stmt.setString(2, LocalDate.now().toString());
            stmt.setString(3, LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            stmt.setString(4, resourceID);
            stmt.setString(5, endDate);
            stmt.setString(6, endTime);
            stmt.setString(7, startDate);
            stmt.setString(8, startTime);
            stmt.executeUpdate();
        }
    }

    /**
     * Cancels all pending or confirmed bookings for a resource regardless of date.
     * Used before deleting a resource to ensure no active bookings are left without
     * a valid resource reference.
     *
     * @param resourceId the ID of the resource whose bookings to cancel
     * @throws SQLException on database access error
     */
    public static void cancelActiveBookingsByResource(String resourceId) throws SQLException {
        String query = "UPDATE Booking SET status = 'cancelled' WHERE resourceID = ? AND LOWER(status) IN ('pending', 'confirmed')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, resourceId);
            stmt.executeUpdate();
        }
    }

    /**
     * Updates a user's name, email, and role without changing their stored password.
     * Use this when an admin edits a user's details but leaves the password field blank.
     *
     * @param userID    the ID of the user to update
     * @param firstName the new first name
     * @param lastName  the new last name
     * @param email     the new email address
     * @param role      the new role ("Student", "Staff", or "Admin")
     * @throws SQLException on database access error
     */
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
