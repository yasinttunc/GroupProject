package com.project.coursework2.controller;

import com.project.coursework2.data.DatabaseManager;
import com.project.coursework2.model.Booking;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;

public class BookingsController {

    @FXML private TextField searchField;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIDCol;
    @FXML private TableColumn<Booking, String> userIDCol;
    @FXML private TableColumn<Booking, String> resourceIDCol;
    @FXML private TableColumn<Booking, String> resourceNameCol;
    @FXML private TableColumn<Booking, String> startTimeCol;
    @FXML private TableColumn<Booking, String> endTimeCol;
    @FXML private TableColumn<Booking, String> dateCol;
    @FXML private TableColumn<Booking, String> statusCol;

    @FXML
    public void initialize() {
        bookingIDCol.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        resourceIDCol.setCellValueFactory(new PropertyValueFactory<>("resourceID"));
        resourceNameCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadBookings();
    }

    private void loadBookings() {
        try {
            ArrayList<Booking> bookingList = DatabaseManager.getAllBookings();
            bookingsTable.setItems(FXCollections.observableArrayList(bookingList));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
