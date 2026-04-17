package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BookingsController {

    @FXML private TextField searchField;
    @FXML private TableView bookingsTable;
    @FXML private TableColumn dateCol;
    @FXML private TableColumn timeCol;
    @FXML private TableColumn bookingCol;
    @FXML private TableColumn seatingCol;
    @FXML private TableColumn userCol;
    @FXML private TableColumn idCol;
    @FXML private TableColumn statusCol;

    @FXML
    public void initialize() {
        System.out.println("Bookings page loaded");
    }
}
