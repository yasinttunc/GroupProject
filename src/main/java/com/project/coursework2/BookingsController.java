package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Bookings page.
 * Displays and manages user booking records.
 * @author Jasmine Eagles
 * @version 1.0
 */
public class BookingsController {

    /** Search field for filtering bookings. */
    @FXML private TextField searchField;
    /** Table displaying all bookings. */
    @FXML private TableView bookingsTable;
    /** Column for booking date. */
    @FXML private TableColumn dateCol;
    /** Column for booking time. */
    @FXML private TableColumn timeCol;
    /** Column for booking name. */
    @FXML private TableColumn bookingCol;
    /** Column for seating information. */
    @FXML private TableColumn seatingCol;
    /** Column for user name. */
    @FXML private TableColumn userCol;
    /** Column for booking ID. */
    @FXML private TableColumn idCol;
    /** Column for booking status. */
    @FXML private TableColumn statusCol;

    /**
     * Initialises the Bookings page.
     */
    @FXML
    public void initialize() {
        System.out.println("Bookings page loaded");
    }
}
