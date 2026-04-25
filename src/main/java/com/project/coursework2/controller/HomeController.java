package com.project.coursework2.controller;

import com.project.coursework2.data.ResourcesDatabaseManager;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HomeController {

    @FXML private Label todayRequestsCount;
    @FXML private Label pendingCount;
    @FXML private Label totalCount;
    @FXML private ComboBox<String> resourceDropdown;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField timeField;

    @FXML
    public void initialize() {
        try {
            for (ResourcesDatabaseManager.ResourceRow resource : ResourcesDatabaseManager.getAllResources()) {
                resourceDropdown.getItems().add(resource.getName());
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.out.println("Could not load resources into dropdown: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        System.out.println("Submit: " + resourceDropdown.getValue() + " on " + startDatePicker.getValue() + " at " + timeField.getText());
    }

    @FXML
    private void handleCreateBooking() {
        System.out.println("Create Booking clicked");
    }
}