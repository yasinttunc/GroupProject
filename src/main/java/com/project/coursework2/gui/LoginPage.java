package com.project.coursework2.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginPage {

    public void start(Stage primaryStage) {

        // Title
        Label titleLabel = new Label("CRBAS Login");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setStyle("-fx-text-fill: #3D3D3D;");

        Label subtitleLabel = new Label("Please sign in to continue");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A7A7A;");

        // Username
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        userField.setPromptText("Enter your username");
        userField.setMaxWidth(300);

        // Password
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");
        passField.setMaxWidth(300);

        // Buttons
        Button loginButton = new Button("Login");
        Button backButton = new Button("Back");

        String buttonStyle = "-fx-min-width: 220px; -fx-min-height: 45px; -fx-font-size: 14px; " +
                "-fx-background-color: #4AADA8; -fx-text-fill: white; -fx-background-radius: 10px; " +
                "-fx-cursor: hand;";

        String hoverStyle = "-fx-min-width: 220px; -fx-min-height: 45px; -fx-font-size: 14px; " +
                "-fx-background-color: #E8735A; -fx-text-fill: white; -fx-background-radius: 10px; " +
                "-fx-cursor: hand;";

        String backStyle = "-fx-min-width: 220px; -fx-min-height: 45px; -fx-font-size: 14px; " +
                "-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-background-radius: 10px; " +
                "-fx-cursor: hand;";

        loginButton.setStyle(buttonStyle);
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(hoverStyle));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(buttonStyle));

        backButton.setStyle(backStyle);

        // Message label
        Label messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: red;");

        // Login button action
        loginButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter both username and password.");
                return;
            }

            // TODO: replace with real User authentication from model package
            if (username.equals("admin") && password.equals("admin")) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Login successful!");
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Invalid username or password.");
            }
        });

        // Back button goes back to MainPage
        backButton.setOnAction(e -> {
            MainPage mainPage = new MainPage();
            mainPage.start(primaryStage);
        });

        // Layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(50));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDF6EC;");
        layout.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                new Separator(),
                userLabel, userField,
                passLabel, passField,
                loginButton,
                backButton,
                messageLabel
        );

        Scene scene = new Scene(layout, 550, 500);
        primaryStage.setTitle("CRBAS - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}