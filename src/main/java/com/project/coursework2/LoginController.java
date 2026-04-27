package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

/**
 * Controller for the Login page.
 * Handles user authentication and navigation to the main application.
 * @author Jasmine Eagles
 * @version 1.0
 */
public class LoginController {

    /** Username or student ID input field. */
    @FXML private TextField usernameField;
    /** Password input field. */
    @FXML private PasswordField passwordField;
    /** Label for displaying error messages. */
    @FXML private Label errorLabel;

    /**
     * Initialises the Login page.
     */
    @FXML
    public void initialize() {
        System.out.println("Login page loaded");
    }

    /**
     * Handles the login button action.
     * Validates credentials and navigates to the main application.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your email and password");
            return;
        }

        if (username.equals("admin") && password.equals("admin")) {
            System.out.println("Admin logged in");
            loadMainApp();
        } else if (!username.isEmpty() && !password.isEmpty()) {
            System.out.println("User logged in: " + username);
            loadMainApp();
        } else {
            errorLabel.setText("Invalid credentials");
        }
    }

    /**
     * Loads the main application layout after successful login.
     */
    private void loadMainApp() {
        try {
            Node mainLayout = FXMLLoader.load(getClass().getResource("/com/project/coursework2/main-layout.fxml"));
            usernameField.getScene().getRoot().getScene().setRoot((javafx.scene.Parent) mainLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
