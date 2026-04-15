package com.project.coursework2.gui;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainPage extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Title
        Label title = new Label("Campus Resource Booking System");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #3D3D3D;");

        Label subtitle = new Label("Book rooms, equipment and labs with ease");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A7A7A;");

        // Buttons
        Button loginBtn = new Button("Login");
        Button viewResourcesBtn = new Button("View Resources");
        Button myBookingsBtn = new Button("My Bookings");

        String buttonStyle = "-fx-min-width: 220px; -fx-min-height: 45px; -fx-font-size: 14px; " +
                "-fx-background-color: #4AADA8; -fx-text-fill: white; -fx-background-radius: 10px; " +
                "-fx-cursor: hand;";

        String hoverStyle = "-fx-min-width: 220px; -fx-min-height: 45px; -fx-font-size: 14px; " +
                "-fx-background-color: #E8735A; -fx-text-fill: white; -fx-background-radius: 10px; " +
                "-fx-cursor: hand;";

        for (Button btn : new Button[]{loginBtn, viewResourcesBtn, myBookingsBtn}) {
            btn.setStyle(buttonStyle);
            btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(buttonStyle));
        }

        // Button actions
        loginBtn.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
        });

        viewResourcesBtn.setOnAction(e -> System.out.println("View Resources clicked"));
        myBookingsBtn.setOnAction(e -> System.out.println("My Bookings clicked"));

        // Layout
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(60));
        layout.setStyle("-fx-background-color: #FDF6EC;");
        layout.getChildren().addAll(title, subtitle, loginBtn, viewResourcesBtn, myBookingsBtn);

        Scene scene = new Scene(layout, 550, 450);
        primaryStage.setTitle("CRBAS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

