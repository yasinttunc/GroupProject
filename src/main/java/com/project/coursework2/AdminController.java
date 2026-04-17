package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminController {

    @FXML private TextField userSearchField;
    @FXML private TableView usersTable;
    @FXML private TableColumn userNameCol;
    @FXML private TableColumn userActionCol;
    @FXML private ComboBox roomDropdown;
    @FXML private ComboBox resourceDropdown;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField riskNameField;
    @FXML private DatePicker mainStartDate;
    @FXML private DatePicker mainEndDate;
    @FXML private TextArea descriptionField;

    @FXML
    public void initialize() {
        roomDropdown.getItems().addAll("Study Room 101", "Meeting Room AR1", "Lab 105", "Lecture Hall B");
        resourceDropdown.getItems().addAll("Laptop 1 (Dell XPS)", "Camera Kit 2", "VR Headset 2", "Projector A");
        System.out.println("Admin page loaded");
    }

    @FXML
    private void handleCancel() { System.out.println("Cancel clicked"); }

    @FXML
    private void handleSaveUser() { System.out.println("Save user clicked"); }

    @FXML
    private void handleActivate() { System.out.println("Activate maintenance clicked"); }

    @FXML
    private void handleSaveResource() { System.out.println("Save resource clicked"); }
}
