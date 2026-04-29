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
 * @author CRBAS Team
 * @version 1.0
 */
public class SessionManager {
    private static User currentUser;
    private static String userRole;
    private static String userID;
    private static String adminActiveTab = "users"; // default tab

    private static Resource currentSelectedResource;
    private static String currentSelectedResourceID;
    private static LocalDate currentSelectedDate;
    private static String currentSelectedStartTime;
    private static String currentSelectedEndTime;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    public static User getCurrentUser() {
        return currentUser;
    }
    public static String getUserRole() {
        return userRole;
    }
    public static void setUserRole(String userRole) {
        SessionManager.userRole = userRole;
    }

    public static Resource getCurrentSelectedResource() {
        return currentSelectedResource;
    }

    public static String getAdminActiveTab() {
        return adminActiveTab;
    }

    public static void setAdminActiveTab(String tab) {
        adminActiveTab = tab;
    }
    public static String getUserID() {
        return userID;
    }
    public static void setUserID(String userID) {
        SessionManager.userID = userID;
    }


    public static void clear() {
        currentUser = null;
        userRole = null;
        userID = null;
        adminActiveTab = "users";
    }

    public static void saveEditAccountController(String userID, String fName, String lName, String email, String password) {
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
