package com.project.coursework2.controller;

import com.project.coursework2.data.ResourcesDatabaseManager;
import com.project.coursework2.data.UserDatabaseManager;
import com.project.coursework2.model.Resource;
import com.project.coursework2.model.User;

import java.time.LocalDate;

/**
 * Application-wide session state holder.
 * Stores the authenticated {@link User}, their role, user ID, and the last
 * active admin tab as static fields so all controllers can share session data
 * without passing objects between scenes.
 * Call {@link #clear()} on logout to reset all fields.
 *
 * @author Group 2
 * @version 1.0
 */
public class SessionManager {

    private static User currentUser;
    private static String userRole;
    private static String userID;
    private static String adminActiveTab = "users";

    private static Resource currentSelectedResource;
    private static String currentSelectedResourceID;
    private static LocalDate currentSelectedDate;
    private static String currentSelectedStartTime;
    private static String currentSelectedEndTime;

    /** @param user the authenticated user to store in the session */
    public static void setCurrentUser(User user) { currentUser = user; }

    /** @return the currently logged-in user, or null if no one is logged in */
    public static User getCurrentUser() { return currentUser; }

    /** @return the role string of the current user (e.g. "Admin") */
    public static String getUserRole() { return userRole; }

    /** @param userRole the role string to store */
    public static void setUserRole(String userRole) { SessionManager.userRole = userRole; }

    /** @return the currently selected resource, or null if none */
    public static Resource getCurrentSelectedResource() { return currentSelectedResource; }

    /** @return the admin tab that was last active ("users", "resources", or "bookings") */
    public static String getAdminActiveTab() { return adminActiveTab; }

    /** @param tab the admin tab to activate ("users", "resources", or "bookings") */
    public static void setAdminActiveTab(String tab) { adminActiveTab = tab; }

    /** @return the unique ID of the current user */
    public static String getUserID() { return userID; }

    /** @param userID the user ID to store */
    public static void setUserID(String userID) { SessionManager.userID = userID; }

    /**
     * Clears all session data.
     * Should be called when the user logs out.
     */
    public static void clear() {
        currentUser = null;
        userRole = null;
        userID = null;
        adminActiveTab = "users";
    }

    /**
     * Updates the current user's profile both in the session and in the database.
     *
     * @param userID   the user's ID
     * @param fName    new first name
     * @param lName    new last name
     * @param email    new email address
     * @param password new plaintext password (will be hashed before storage)
     */
    public static void saveEditAccountController(String userID, String fName, String lName,
                                                  String email, String password) {
        if (currentUser == null) return;
        currentUser.setFirstName(fName);
        currentUser.setLastName(lName);
        currentUser.setEmail(email);
        currentUser.setPassword(password);
        try {
            UserDatabaseManager.updateUser(userID, fName, lName, email, password);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}
