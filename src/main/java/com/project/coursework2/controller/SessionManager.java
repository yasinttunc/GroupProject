package com.project.coursework2.controller;

import com.project.coursework2.model.User;

public class SessionManager {
    private static User currentUser;
    private static String userRole;

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
}
