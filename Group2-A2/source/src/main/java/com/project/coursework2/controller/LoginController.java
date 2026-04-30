package com.project.coursework2.controller;

import java.sql.SQLException;

import com.project.coursework2.model.Role;
import com.project.coursework2.model.User;
import com.project.coursework2.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the login screen (login-view.fxml).
 * Authenticates users against the database using email and password,
 * stores the session via {@link SessionManager}, and routes the user
 * to the appropriate application layout based on their role.
 *
 * @author Group 2
 * @version 1.0
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    /** Called automatically by JavaFX after FXML injection. */
    @FXML
    public void initialize() {
        System.out.println("Login page loaded");
    }

    /**
     * Validates the entered credentials and navigates to the main application on success.
     * Displays an inline error label if credentials are empty or invalid.
     */
    @FXML
    private void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your email and password");
            return;
        }
        try {
            User user = UserService.login(email, password);
            if (user != null) {
                SessionManager.setCurrentUser(user);
                SessionManager.setUserRole(user.getRole());
                SessionManager.setUserID(user.getUserID());
                Role role = Role.from(user.getRole());
                if (role == Role.ADMIN || role == Role.STAFF) loadAdminApp();
                else loadMainApp();
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
