package com.project.coursework2.controller;

import com.project.coursework2.data.BookingsDatabaseManager;
import com.project.coursework2.data.ResourcesDatabaseManager;
import com.project.coursework2.model.Booking;
import com.project.coursework2.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<ResourcesDatabaseManager.ResourceRow> resourceDropdown;
    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIDCol, resourceNameCol, dateCol, timeRangeCol, statusCol;
    @FXML private TableColumn<Booking, Void> actionCol;
    @FXML private Label activeBookingsLabel;
    @FXML private ProgressBar activeBookingsProgress;

    private static final String[] TIME_SLOTS = {
        "08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30",
        "12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30",
        "16:00","16:30","17:00","17:30","18:00"
    };

    private final ObservableList<Booking> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupForm();
        setupTableColumns();
        loadBookings();
    }

    // ─── Form setup ───

    private void setupForm() {
        // Block past dates
        // TODO : Disable occupied dates
        if (startDatePicker != null) {
            startDatePicker.setDayCellFactory(p -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });
        }

        // Resource dropdown
        if (resourceDropdown != null) {
            resourceDropdown.setConverter(new StringConverter<>() {
                @Override public String toString(ResourcesDatabaseManager.ResourceRow r) { return r != null ? r.getName() : ""; }
                @Override public ResourcesDatabaseManager.ResourceRow fromString(String s) { return null; }
            });
            try { resourceDropdown.getItems().addAll(ResourcesDatabaseManager.getAllResources()); }
            catch (SQLException e) { System.out.println("Could not load resources: " + e.getMessage()); }

            // Re-trigger end time calculation when resource changes
            resourceDropdown.valueProperty().addListener((o, old, val) -> {
                if (startTimeCombo != null && startTimeCombo.getValue() != null) {
                    String cur = startTimeCombo.getValue();
                    startTimeCombo.setValue(null);
                    startTimeCombo.setValue(cur);
                }
            });
        }

        // Time combos — end times filtered by resource max duration
        if (startTimeCombo != null && endTimeCombo != null) {
            startTimeCombo.getItems().addAll(TIME_SLOTS);
            startTimeCombo.valueProperty().addListener((o, old, val) -> {
                endTimeCombo.getItems().clear();
                if (val == null) return;

                int maxMins = 120;
                if (resourceDropdown != null && resourceDropdown.getValue() != null) {
                    int d = resourceDropdown.getValue().getMaxBookingDuration();
                    if (d > 0) maxMins = d;
                }
                LocalTime start = LocalTime.parse(val);
                for (String t : TIME_SLOTS) {
                    long mins = Duration.between(start, LocalTime.parse(t)).toMinutes();
                    if (mins > 0 && mins <= maxMins) endTimeCombo.getItems().add(t);
                }
            });
        }

        // Live search
        if (searchField != null) {
            searchField.textProperty().addListener((o, old, val) -> filterBookings(val));
        }
    }

    // ─── Table columns ───

    private void setupTableColumns() {
        bookingIDCol.setCellValueFactory(d -> new SimpleStringProperty(safeStr(d.getValue().getBookingID())));
        resourceNameCol.setCellValueFactory(d -> new SimpleStringProperty(safeStr(d.getValue().getResourceName())));
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(safeStr(d.getValue().getDate())));
        timeRangeCol.setCellValueFactory(d -> new SimpleStringProperty(
                safeStr(d.getValue().getStartTime()) + " - " + safeStr(d.getValue().getEndTime())));
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(safeStr(d.getValue().getStatus(), "Pending")));

        // Status pill styling
        statusCol.setCellFactory(col -> new TableCell<>() {
            private final Label pill = new Label();
            { pill.setStyle("-fx-padding: 3 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;"); setAlignment(Pos.CENTER_LEFT); }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                pill.setText(item);
                stylePill(pill, item);
                setGraphic(pill);
            }
        });

        // Cancel button
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Cancel");
            { btn.setStyle("-fx-background-color: transparent; -fx-border-color: #E6E9ED; -fx-border-radius: 4; -fx-text-fill: #A0A0A0; -fx-cursor: hand;");
              btn.setOnAction(e -> cancelBooking(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Booking b = getTableView().getItems().get(getIndex());
                setGraphic("Cancelled".equalsIgnoreCase(b.getStatus()) ? null : btn);
            }
        });
    }

    // ─── Data loading ───

    private void loadBookings() {
        User user = SessionManager.getCurrentUser();
        if (user == null) { System.out.println("No user logged in."); return; }

        try {
            masterData.setAll(BookingsDatabaseManager.getBookingsByUser(user.getUserID()));
            bookingsTable.setItems(masterData);

            int max = getMaxActive();
            long active = getActiveCount();
            if (activeBookingsLabel != null) {
                activeBookingsLabel.setText(active + " / " + max);
            }
            if (activeBookingsProgress != null) {
                activeBookingsProgress.setProgress((double) active / max);
            }
        } catch (SQLException e) {
            System.out.println("Failed to load bookings: " + e.getMessage());
        }
    }

    private void filterBookings(String query) {
        if (query == null || query.isEmpty()) { bookingsTable.setItems(masterData); return; }
        String q = query.toLowerCase();
        bookingsTable.setItems(masterData.filtered(b ->
                b.getResourceName() != null && b.getResourceName().toLowerCase().contains(q)));
    }

    // ─── Actions ───

    @FXML
    private void handleSubmitBooking() {
        User user = SessionManager.getCurrentUser();
        if (user == null) { showAlert(Alert.AlertType.ERROR, "You must be logged in to create a booking."); return; }

        ResourcesDatabaseManager.ResourceRow res = resourceDropdown.getValue();
        String start = startTimeCombo.getValue(), end = endTimeCombo.getValue();
        LocalDate date = startDatePicker.getValue();

        if (res == null || start == null || end == null || date == null) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all booking fields.");
            return;
        }

        if (getActiveCount() >= getMaxActive()) {
            showAlert(Alert.AlertType.WARNING, "You've reached your max of " + getMaxActive() +
                    " active bookings. Cancel one before creating a new one.");
            return;
        }

        try {
            String id = "BKG" + (System.currentTimeMillis() % 10000);
            BookingsDatabaseManager.addBooking(id, user.getUserID(), res.getResourceId(), date.toString(), start, end);
            loadBookings();
            resourceDropdown.setValue(null); startDatePicker.setValue(null);
            startTimeCombo.setValue(null); endTimeCombo.setValue(null);
            showAlert(Alert.AlertType.INFORMATION, "Booking request submitted successfully.");
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage());
        }
    }

    private void cancelBooking(Booking b) {
        if ("Cancelled".equalsIgnoreCase(b.getStatus())) return;
        try {
            BookingsDatabaseManager.updateBookingStatus(b.getBookingID(), "Cancelled");
            loadBookings();
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Failed to cancel: " + ex.getMessage());
        }
    }

    // ─── Helpers ───

    private long getActiveCount() {
        return masterData.stream()
                .filter(b -> "Confirmed".equalsIgnoreCase(b.getStatus()) || "Pending".equalsIgnoreCase(b.getStatus()))
                .count();
    }

    private int getMaxActive() {
        User user = SessionManager.getCurrentUser();
        int max = (user != null) ? user.getMaxActiveBookings() : 3;
        return max > 0 ? max : 3;
    }

    private static String safeStr(String val) { return val != null ? val : ""; }
    private static String safeStr(String val, String fallback) { return val != null ? val : fallback; }

    private static void stylePill(Label pill, String status) {
        switch (status.toLowerCase()) {
            case "confirmed" -> { pill.setBackground(bg("#E8F5E9")); pill.setTextFill(Color.web("#2E7D32")); }
            case "pending"   -> { pill.setBackground(bg("#FFF8E1")); pill.setTextFill(Color.web("#F57F17")); }
            case "cancelled" -> { pill.setBackground(bg("#FFEBEE")); pill.setTextFill(Color.web("#C62828")); }
            default          -> { pill.setBackground(bg("#F0F0F0")); pill.setTextFill(Color.DARKGRAY); }
        }
    }

    private static Background bg(String hex) {
        return new Background(new BackgroundFill(Color.web(hex), new CornerRadii(12), Insets.EMPTY));
    }

    private static void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}