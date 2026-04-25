package com.project.coursework2.controller;

import com.project.coursework2.data.UserDatabaseManager;
import com.project.coursework2.model.User;

//Provides user datas for current session
public class SessionManager {
    private static User currentUser;
    private static String userRole;
    private static String userID;
    private static String adminActiveTab = "users"; // default tab
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

    public static void saveEditAccountController(String userID, String fName, String lName, String email , String password){
        currentUser.setFirstName(fName);
        currentUser.setLastName(lName);
        currentUser.setEmail(email);
        currentUser.setPassword(password);
        String userId = currentUser.getUserID();    
        try {
            UserDatabaseManager.updateUser(userID, fName, lName, email, password);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}
