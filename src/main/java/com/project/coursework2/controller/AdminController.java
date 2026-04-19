package com.project.coursework2.controller;

import com.project.coursework2.data.DatabaseManager;
import com.project.coursework2.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;

public class AdminController {

    @FXML private TextField userSearchField;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userNameCol;
    @FXML private TableColumn<User, String> userActionCol;
    @FXML private ComboBox<String> roomDropdown;
    @FXML private ComboBox<String> resourceDropdown;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField riskNameField;
    @FXML private DatePicker mainStartDate;
    @FXML private DatePicker mainEndDate;
    @FXML private TextArea descriptionField;

    @FXML
    public void initialize() {
        roomDropdown.getItems().addAll("Study Room 101", "Meeting Room AR1", "Lab 105", "Lecture Hall B");
        resourceDropdown.getItems().addAll("Laptop 1 (Dell XPS)", "Camera Kit 2", "VR Headset 2", "Projector A");

        userNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userActionCol.setCellValueFactory(new PropertyValueFactory<>("role")); // or email if you want

        loadUsers();

        System.out.println("Admin page loaded");
    }

    private void loadUsers() {
        try {
            ArrayList<User> userList = DatabaseManager.getAllUsers();
            ObservableList<User> observableUsers = FXCollections.observableArrayList(userList);
            usersTable.setItems(observableUsers);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load users");
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancel clicked");
    }

    @FXML
    private void handleSaveUser() {
        System.out.println("Save user clicked");
    }

    @FXML
    private void handleActivate() {
        System.out.println("Activate maintenance clicked");
    }

    @FXML
    private void handleSaveResource() {
        System.out.println("Save resource clicked");
    }
}