package com.project.coursework2.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.project.coursework2.model.User;
import com.project.coursework2.util.PasswordUtils;

import static com.project.coursework2.data.DatabaseConnection.getConnection;

/**
 * Data-access layer for the {@code User} table.
 * Provides authentication, retrieval, update, and deletion of user records.
 * All password parameters are hashed with SHA-256 via {@link PasswordUtils}
 * before being compared or stored.
 *
 * @author CRBAS Team
 * @version 1.0
 */
public class UserDatabaseManager {

    /**
     * Checks login details for a user.
     * The login can be email, user ID, student ID or staff ID.
     *
     * @param login    the email or ID typed by the user
     * @param password the plain password typed by the user
     * @return the matching user, or null if login is wrong
     * @throws SQLException if database has a problem
     */
    public static User getUser(String login, String password) throws SQLException {
        String query = "SELECT u.userID, u.name, u.firstName, u.lastName, u.email, u.role, u.maxActiveBookings " +
                "FROM User u " +
                "LEFT JOIN Student s ON u.userID = s.userID " +
                "LEFT JOIN Staff st ON u.userID = st.userID " +
                "WHERE (u.email = ? OR u.userID = ? OR s.studentID = ? OR st.staffID = ?) " +
                "AND u.password = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, login);
            stmt.setString(2, login);
            stmt.setString(3, login);
            stmt.setString(4, login);
            stmt.setString(5, PasswordUtils.hash(password));

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
     * Returns all users in the system without password data.
     *
     * @return list of {@link User} objects
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
     * Updates name, email, and password for an existing user.
     * The password is SHA-256 hashed before storage.
     *
     * @param userID    the ID of the user to update
     * @param firstName updated first name
     * @param lastName  updated last name
     * @param email     updated email address
     * @param password  new plaintext password (hashed internally)
     * @throws SQLException on database access error
     */
    public static void updateUser(String userID, String firstName,
                                  String lastName, String email, String password) throws SQLException {

        String query = "UPDATE User SET name = ?, firstName = ?, lastName = ?, email = ?, password = ? WHERE userID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, (firstName + " "+lastName));
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, email);
            stmt.setString(5, PasswordUtils.hash(password));
            stmt.setString(6, userID);

            stmt.executeUpdate();
        }
    }
    /**
     * Updates name and email for an existing user without touching the stored password.
     * Use this when the user leaves the password field blank during a profile edit.
     *
     * @param userID     the ID of the user to update
     * @param firstName  updated first name
     * @param lastName   updated last name
     * @param email      updated email address
     * @throws SQLException on database access error
     */
    public static void updateUserNoPassword(String userID, String firstName,
                                            String lastName, String email) throws SQLException {
        String query = "UPDATE User SET name = ?, firstName = ?, lastName = ?, email = ? WHERE userID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName + " " + lastName);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, email);
            stmt.setString(5, userID);
            stmt.executeUpdate();
        }
    }

    /**
     * Permanently removes a user record from the database.
     *
     * @param userID the ID of the user to delete
     * @throws SQLException on database access error
     */
    public static void deleteUser(String userID) throws SQLException {
        String deleteHistoryByUser = "DELETE FROM BookingHistory WHERE changedBy = ?";
        String clearCancelledBy = "UPDATE Booking SET cancelledBy = NULL WHERE cancelledBy = ?";
        String deleteBookings = "DELETE FROM Booking WHERE userID = ?";
        String deleteUser = "DELETE FROM User WHERE userID = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement historyStmt = conn.prepareStatement(deleteHistoryByUser);
                 PreparedStatement cancelledByStmt = conn.prepareStatement(clearCancelledBy);
                 PreparedStatement bookingStmt = conn.prepareStatement(deleteBookings);
                 PreparedStatement userStmt = conn.prepareStatement(deleteUser)) {

                historyStmt.setString(1, userID);
                historyStmt.executeUpdate();

                cancelledByStmt.setString(1, userID);
                cancelledByStmt.executeUpdate();

                bookingStmt.setString(1, userID);
                bookingStmt.executeUpdate();

                userStmt.setString(1, userID);
                userStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

}
