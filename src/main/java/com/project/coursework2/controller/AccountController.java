package com.project.coursework2.controller;

import com.project.coursework2.data.UserDatabaseManager;
import com.project.coursework2.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.project.coursework2.HelloApplication;
import javafx.stage.Stage;
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
        setFieldsEditable(false);

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;

        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
        emailField.setText(currentUser.getEmail());
    }

    private void setFieldsEditable(boolean editable) {
        firstNameField.setEditable(editable);
        lastNameField.setEditable(editable);
        emailField.setEditable(editable);
        fiscalIdField.setEditable(editable);
        passwordField.setEditable(editable);
    }

    @FXML private void handleDeleteAccount() {
        User currentUser = SessionManager.getCurrentUser();
        String userID = currentUser.getUserID();
        try {
            UserDatabaseManager.deleteUser(userID);
            
            // Open HelloApplication
            HelloApplication app = new HelloApplication();
            Stage newStage = new Stage();
            app.start(newStage);
            
            // Close main page
            Stage currentStage = (Stage) firstNameField.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Delete account clicked");
    }
    
    @FXML private void handleEdit() { 
        setFieldsEditable(true); 
    }
    

    @FXML private void handleSave() { 
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = SessionManager.getCurrentUser();
        SessionManager.saveEditAccountController(user.getUserID(), firstName, lastName, email, password);
        initialize();
    }
    @FXML private void handleAdminCancel() { System.out.println("Admin cancel clicked"); }
    @FXML private void handleAdminSave() { System.out.println("Admin save clicked"); }
}
