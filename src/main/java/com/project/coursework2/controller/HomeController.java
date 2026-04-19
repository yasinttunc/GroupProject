package com.project.coursework2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HomeController {

    @FXML private Label todayRequestsCount;
    @FXML private Label pendingCount;
    @FXML private Label totalCount;
    @FXML private TextField startNameField;
    @FXML private TextField bookingField;
    @FXML private ComboBox<String> resourceDropdown;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML
    public void initialize() {
        resourceDropdown.getItems().addAll(
            "Study Room 101",
            "Laptop 1 (Dell XPS)",
            "Camera Kit 2",
            "Lab 105",
            "VR Headset 3"
        );
    }

    @FXML
    private void handleSubmit() {
        System.out.println("Submit: " + resourceDropdown.getValue() + " from " + startDatePicker.getValue() + " to " + endDatePicker.getValue());
    }

    @FXML
    private void handleCreateBooking() {
        System.out.println("Create Booking clicked");
    }
}