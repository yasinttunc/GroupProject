package com.project.coursework2.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessControlDAO {

    public boolean canUserBookResource(String userID, String resourceID, String bookingID) {
        String userRole = getUserRole(userID);
        String requiredRole = getRequiredRole(resourceID);

        boolean canBook = false;
        String denialReason = null;
        String denialType = null;

        if (userRole == null) {
            denialReason = "User not found";
            denialType = "USER_NOT_FOUND";
        } else if (requiredRole == null) {
            denialReason = "Resource not found";
            denialType = "RESOURCE_NOT_FOUND";
        } else if (userRole.equalsIgnoreCase("Admin")) {
            canBook = true;
        } else if (userRole.equalsIgnoreCase("Staff")) {
            canBook = requiredRole.equalsIgnoreCase("Staff")
                    || requiredRole.equalsIgnoreCase("Student");
            if (!canBook) {
                denialReason = "Staff cannot access this resource";
                denialType = "ROLE_DENIED";
            }
        } else if (userRole.equalsIgnoreCase("Student")) {
            canBook = requiredRole.equalsIgnoreCase("Student");
            if (!canBook) {
                denialReason = "Student cannot book staff-only resource";
                denialType = "ROLE_DENIED";
            }
        } else {
            denialReason = "Unknown role";
            denialType = "INVALID_ROLE";
        }

        saveAccessCheck(userID, resourceID, bookingID, canBook, denialReason, denialType);
        return canBook;
    }

    private String getUserRole(String userID) {
        String sql = "SELECT role FROM User WHERE userID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getRequiredRole(String resourceID) {
        String sql = "SELECT requiredRole FROM Resource WHERE resourceID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resourceID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("requiredRole");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveAccessCheck(String userID, String resourceID, String bookingID,
                                 boolean canBook, String denialReason, String denialType) {

        String sql = "INSERT INTO AccessControl (userID, resourceID, bookingID, canBook, denialReason, denialType) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userID);
            stmt.setString(2, resourceID);
            stmt.setString(3, bookingID);
            stmt.setInt(4, canBook ? 1 : 0);
            stmt.setString(5, denialReason);
            stmt.setString(6, denialType);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}