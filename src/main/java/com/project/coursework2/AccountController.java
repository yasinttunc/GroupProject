package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AccountController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField fiscalIdField;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private TextField adminFirstNameField;
    @FXML private TextField adminEmailField;
    @FXML private TextField labIdsField;
    @FXML private TextArea adminDescField;

    @FXML
    public void initialize() {
        System.out.println("Account page loaded");
    }

    @FXML private void handleDeleteAccount() { System.out.println("Delete account clicked"); }
    @FXML private void handleEdit() { System.out.println("Edit clicked"); }
    @FXML private void handleSave() { System.out.println("Save clicked"); }
    @FXML private void handleAdminCancel() { System.out.println("Admin cancel clicked"); }
    @FXML private void handleAdminSave() { System.out.println("Admin save clicked"); }
}
