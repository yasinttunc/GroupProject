package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

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
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your email and password");
            return;
        }

        // placeholder login logic
        // admin login
        if (username.equals("admin") && password.equals("admin")) {
            System.out.println("Admin logged in");
            loadMainApp();
        }
        // regular user login
        else if (!username.isEmpty() && !password.isEmpty()) {
            System.out.println("User logged in: " + username);
            loadMainApp();
        } else {
            errorLabel.setText("Invalid credentials");
        }
    }

    private void loadMainApp() {
        try {
            Node mainLayout = FXMLLoader.load(getClass().getResource("/com/project/coursework2/main-layout.fxml"));
            usernameField.getScene().getRoot().getScene().setRoot((javafx.scene.Parent) mainLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
