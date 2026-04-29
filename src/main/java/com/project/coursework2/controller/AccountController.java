package com.project.coursework2.controller;

import com.project.coursework2.model.User;
import com.project.coursework2.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AccountController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        setFieldsEditable(false);

        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        firstNameField.setText(user.getFirstName() != null ? user.getFirstName() : "");
        lastNameField.setText(user.getLastName() != null ? user.getLastName() : "");
        emailField.setText(user.getEmail() != null ? user.getEmail() : "");
        idField.setText(user.getUserID() != null ? user.getUserID() : "");
    }

    private void setFieldsEditable(boolean editable) {
        firstNameField.setEditable(editable);
        lastNameField.setEditable(editable);
        emailField.setEditable(editable);
        passwordField.setEditable(editable);
    }

    @FXML
    private void handleEdit() {
        if (!verifyPassword("Enter your current password to enable editing.")) return;
        setFieldsEditable(true);
        setStatus("Fields unlocked. Make your changes and press Save.", false);
    }

    @FXML
    private void handleSave() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();
        String newPassword = passwordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            setStatus("First name, last name and email cannot be empty.", true);
            return;
        }

        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        try {
            if (newPassword.isEmpty()) {
                UserService.updateProfile(user.getUserID(), firstName, lastName, email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
            } else {
                UserService.updateProfileWithPassword(user.getUserID(), firstName, lastName, email, newPassword);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPassword(newPassword);
            }
            passwordField.clear();
            setFieldsEditable(false);
            setStatus("Profile updated successfully.", false);
        } catch (java.sql.SQLException e) {
            setStatus("Could not save changes: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleDeleteAccount() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        if (!verifyPassword("Enter your password to confirm account deletion.")) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "This will permanently delete your account and cannot be undone.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText(null);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            UserService.deleteAccount(user.getUserID());
            SessionManager.clear();
            Parent loginView = FXMLLoader.load(
                    getClass().getResource("/com/project/coursework2/login-view.fxml"));
            firstNameField.getScene().setRoot(loginView);
        } catch (Exception e) {
            setStatus("Could not delete account: " + e.getMessage(), true);
        }
    }

    private boolean verifyPassword(String prompt) {
        PasswordField pwField = new PasswordField();
        pwField.setPromptText("Current password");

        VBox content = new VBox(8, new Label(prompt), pwField);
        content.setPadding(new Insets(10, 0, 0, 0));

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Password Required");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? pwField.getText() : null);

        String entered = dialog.showAndWait().orElse(null);
        if (entered == null || entered.isEmpty()) return false;

        try {
            User user = SessionManager.getCurrentUser();
            boolean valid = UserService.verifyPassword(user.getEmail(), entered);
            if (!valid) setStatus("Incorrect password.", true);
            return valid;
        } catch (Exception e) {
            setStatus("Could not verify password.", true);
            return false;
        }
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (isError ? "#E8735A" : "#4AADA8") + ";");
    }
}
