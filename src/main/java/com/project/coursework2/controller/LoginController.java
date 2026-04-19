package com.project.coursework2.controller;

import com.project.coursework2.data.DatabaseManager;
import com.project.coursework2.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        System.out.println("Login page loaded");
    }

    @FXML
    private void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your email and password");
            return;
        }
        try {
            User user = DatabaseManager.getUser(email,password);
            if (user != null) {
                SessionManager.setCurrentUser(user);
                SessionManager.setUserRole(user.getRole());
                switch (user.getRole()) {
                    case "Admin" -> loadAdminApp();
                    case "Staff" -> loadStaffApp();
                    default -> loadMainApp();
                }
                return;
            }

        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
        }
        errorLabel.setText("Invalid credentials");
    }

    private void loadMainApp() {
        try {
            Node mainLayout = FXMLLoader.load(getClass().getResource("/com/project/coursework2/main-layout.fxml"));
            usernameField.getScene().getRoot().getScene().setRoot((javafx.scene.Parent) mainLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAdminApp() {
        loadMainApp();
    }

    private void loadStaffApp() {
        loadMainApp();
    }
}