package com.project.coursework2.service;

import com.project.coursework2.data.AdminManager;
import com.project.coursework2.data.UserDatabaseManager;
import com.project.coursework2.model.User;

import java.sql.SQLException;

/**
 * Business logic for user authentication and profile management.
 * Controllers call this class instead of touching UserDatabaseManager or AdminManager directly.
 */
public class UserService {

    /**
     * Authenticates a user by email and password.
     * @return the matching User, or null if credentials are wrong
     */
    public static User login(String email, String password) throws SQLException {
        return UserDatabaseManager.getUser(email, password);
    }

    /**
     * Checks whether the given plaintext password matches what is stored for this email.
     */
    public static boolean verifyPassword(String email, String password) throws SQLException {
        return AdminManager.validateUser(email, password);
    }

    /**
     * Updates name and email without changing the password.
     */
    public static void updateProfile(String userId, String firstName,
                                     String lastName, String email) throws SQLException {
        UserDatabaseManager.updateUserNoPassword(userId, firstName, lastName, email);
    }

    /**
     * Updates name, email, and password together.
     */
    public static void updateProfileWithPassword(String userId, String firstName,
                                                  String lastName, String email,
                                                  String newPassword) throws SQLException {
        UserDatabaseManager.updateUser(userId, firstName, lastName, email, newPassword);
    }

    /**
     * Permanently deletes a user account.
     */
    public static void deleteAccount(String userId) throws SQLException {
        UserDatabaseManager.deleteUser(userId);
    }
}
