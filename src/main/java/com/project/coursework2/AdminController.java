package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Admin Panel page.
 * Handles user management, schedule maintenance and resource management.
 * @author Jasmine Eagles
 * @version 1.0
 */
public class AdminController {

    /** Search field for filtering users. */
    @FXML private TextField userSearchField;
    /** Table displaying all users. */
    @FXML private TableView usersTable;
    /** Column for user name. */
    @FXML private TableColumn userNameCol;
    /** Column for user actions. */
    @FXML private TableColumn userActionCol;
    /** Dropdown for room selection. */
    @FXML private ComboBox roomDropdown;
    /** Dropdown for resource selection. */
    @FXML private ComboBox resourceDropdown;
    /** Date picker for end date. */
    @FXML private DatePicker endDatePicker;
    /** Risk name input field. */
    @FXML private TextField riskNameField;
    /** Start date picker for maintenance. */
    @FXML private DatePicker mainStartDate;
    /** End date picker for maintenance. */
    @FXML private DatePicker mainEndDate;
    /** Description text area for maintenance. */
    @FXML private TextArea descriptionField;

    /**
     * Initialises the Admin Panel page.
     */
    @FXML
    public void initialize() {
        roomDropdown.getItems().addAll("Study Room 101", "Meeting Room AR1", "Lab 105", "Lecture Hall B");
        resourceDropdown.getItems().addAll("Laptop 1 (Dell XPS)", "Camera Kit 2", "VR Headset 2", "Projector A");
        System.out.println("Admin page loaded");
    }

    /** Handles the cancel button action. */
    @FXML
    private void handleCancel() { System.out.println("Cancel clicked"); }

    /** Handles the save user button action. */
    @FXML
    private void handleSaveUser() { System.out.println("Save user clicked"); }

    /** Handles the activate maintenance button action. */
    @FXML
    private void handleActivate() { System.out.println("Activate maintenance clicked"); }

    /** Handles the save resource button action. */
    @FXML
    private void handleSaveResource() { System.out.println("Save resource clicked"); }
}
