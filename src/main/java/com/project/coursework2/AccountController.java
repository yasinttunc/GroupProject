package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Account page.
 * Handles user profile editing and admin help requests.
 * @author Jasmine Eagles
 * @version 1.0
 */
public class AccountController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField fiscalIdField;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> helpSubjectDropdown;
    @FXML private TextArea helpMessageField;
    @FXML private Label helpConfirmLabel;

    /**
     * Initialises the Account page and populates the help subject dropdown.
     */
    @FXML
    public void initialize() {
        helpSubjectDropdown.getItems().addAll(
            "Booking Issue",
            "Account Problem",
            "Resource Unavailable",
            "Technical Support",
            "Other"
        );
        System.out.println("Account page loaded");
    }

    /** Handles the delete account button action. */
    @FXML private void handleDeleteAccount() { System.out.println("Delete account clicked"); }

    /** Handles the edit button action. */
    @FXML private void handleEdit() { System.out.println("Edit clicked"); }

    /** Handles the save button action. */
    @FXML private void handleSave() { System.out.println("Save clicked"); }

    /** Handles the help request clear button action. */
    @FXML
    private void handleHelpClear() {
        helpSubjectDropdown.setValue(null);
        helpMessageField.clear();
        helpConfirmLabel.setText("");
    }

    /** Handles the help request submit button action. */
    @FXML
    private void handleHelpSubmit() {
        if (helpSubjectDropdown.getValue() == null || helpMessageField.getText().isEmpty()) {
            helpConfirmLabel.setStyle("-fx-text-fill: #E8735A; -fx-font-size: 11px;");
            helpConfirmLabel.setText("Please select a subject and enter a message.");
        } else {
            helpConfirmLabel.setStyle("-fx-text-fill: #4AADA8; -fx-font-size: 11px;");
            helpConfirmLabel.setText("Request submitted successfully!");
            System.out.println("Help request submitted: " + helpSubjectDropdown.getValue());
        }
    }
}
