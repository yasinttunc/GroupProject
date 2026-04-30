package com.project.coursework2.controller;

import com.project.coursework2.service.BookingService;
import com.project.coursework2.data.BookingsDatabaseManager;
import com.project.coursework2.data.ResourcesDatabaseManager;
import com.project.coursework2.model.Booking;
import com.project.coursework2.model.User;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller for the Bookings page (bookings-view.fxml).
 * Lets the logged-in user browse resources, pick a date and time, submit
 * new booking requests, and cancel existing ones.
 * Client-side validation covers past dates, past time slots (today),
 * role permissions, booking limits, and scheduling conflicts.
 *
 * @author Group 2
 * @version 1.0
 */
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
    @FXML private Button submitBtn;
    @FXML private Button cancelEditBtn;
    @FXML private Label formTitleLabel;

    /** Holds the booking ID currently being edited, or null when creating a new booking. */
    private String editingBookingId = null;

    private static final String[] TIME_SLOTS = {
        "08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30",
        "12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30",
        "16:00","16:30","17:00","17:30","18:00"
    };

    private final ObservableList<Booking> masterData = FXCollections.observableArrayList();

    /**
     * Sets up the form, table columns, and loads the user's bookings.
     * Called automatically by JavaFX after FXML injection.
     */
    @FXML
    public void initialize() {
        setupForm();
        setupTableColumns();
        loadBookings();
    }

    // ─── Form setup ───

    /**
     * Populates the resource dropdown, sets up the time combo boxes,
     * and wires the live search field.
     */
    private void setupForm() {
        updateDatePickerForResource(null);

        // Resource dropdown
        if (resourceDropdown != null) {
            resourceDropdown.setConverter(new StringConverter<>() {
                @Override public String toString(ResourcesDatabaseManager.ResourceRow r) { return r != null ? r.getName() : ""; }
                @Override public ResourcesDatabaseManager.ResourceRow fromString(String s) { return null; }
            });
            try {
                for (ResourcesDatabaseManager.ResourceRow resource : ResourcesDatabaseManager.getAllResources()) {
                    if (resource.isActive()) {
                        resourceDropdown.getItems().add(resource);
                    }
                }
            }
            catch (SQLException e) { System.out.println("Could not load resources: " + e.getMessage()); }

            resourceDropdown.valueProperty().addListener((o, old, val) -> {
                updateDatePickerForResource(val);
                if (startTimeCombo != null && startTimeCombo.getValue() != null) {
                    String cur = startTimeCombo.getValue();
                    startTimeCombo.setValue(null);
                    startTimeCombo.setValue(cur);
                }
            });
        }

        // Re-filter start times whenever the date changes
        if (startDatePicker != null && startTimeCombo != null) {
            startDatePicker.valueProperty().addListener((o, old, val) -> refreshStartTimes(val));
        }

        // Time combos — end times filtered by resource max duration
        if (startTimeCombo != null && endTimeCombo != null) {
            refreshStartTimes(null); // initialise with all slots (no date selected yet)
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

    /**
     * Pre-selects a resource in the dropdown.
     * Called by {@link SidebarController} when the user navigates from the Resources page.
     *
     * @param resource the resource to pre-select, or null to do nothing
     */
    public void prefillResourceField(ResourcesDatabaseManager.ResourceRow resource) {
        if (resource == null || resourceDropdown == null) { return; }

        for (ResourcesDatabaseManager.ResourceRow item : resourceDropdown.getItems()) {
            if (item.getResourceId().equals(resource.getResourceId())) {
                resourceDropdown.setValue(item);
                break;
            } 
        }
    }

    // ─── Table columns ───

    /**
     * Binds each table column to the matching field on {@link Booking}.
     * Also sets up the coloured status pill and the Cancel button column.
     */
    private void setupTableColumns() {
        bookingIDCol.setCellValueFactory(d -> { Booking r = d.getValue(); return new SimpleStringProperty(r == null ? "" : safeStr(r.getBookingID())); });
        resourceNameCol.setCellValueFactory(d -> { Booking r = d.getValue(); return new SimpleStringProperty(r == null ? "" : safeStr(r.getResourceName())); });
        dateCol.setCellValueFactory(d -> { Booking r = d.getValue(); return new SimpleStringProperty(r == null ? "" : safeStr(r.getDate())); });
        timeRangeCol.setCellValueFactory(d -> { Booking r = d.getValue(); return new SimpleStringProperty(r == null ? "" : safeStr(r.getStartTime()) + " - " + safeStr(r.getEndTime())); });
        statusCol.setCellValueFactory(d -> { Booking r = d.getValue(); return new SimpleStringProperty(r == null ? "" : safeStr(r.getStatus(), "Pending")); });

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

        // Edit + Cancel buttons
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("Edit");
            private final Button cancelBtn = new Button("Cancel");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, editBtn, cancelBtn);
            {
                editBtn.setStyle("-fx-background-color: #4F7CFF; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 6; -fx-cursor: hand;");
                cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #DDE3EE; -fx-border-radius: 6; -fx-text-fill: #687385; -fx-font-size: 11px; -fx-padding: 4 10; -fx-cursor: hand;");
                box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Booking b = getTableView().getItems().get(getIndex());
                boolean active = "pending".equalsIgnoreCase(b.getStatus())
                        || "confirmed".equalsIgnoreCase(b.getStatus());
                if (!active) { setGraphic(null); return; }
                editBtn.setOnAction(e -> populateFormForEdit(b));
                cancelBtn.setOnAction(e -> cancelBooking(b));
                setGraphic(box);
            }
        });
    }

    // ─── Data loading ───

    /**
     * Fetches the current user's bookings from the database and refreshes the table.
     * Also updates the active bookings count and progress bar.
     */
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

    /**
     * Filters the bookings table to rows whose resource name contains the given text.
     *
     * @param query the search text (case-insensitive), or null/empty to show all
     */
    private void filterBookings(String query) {
        if (query == null || query.isEmpty()) { bookingsTable.setItems(masterData); return; }
        String q = query.toLowerCase();
        bookingsTable.setItems(masterData.filtered(b ->
                b.getResourceName() != null && b.getResourceName().toLowerCase().contains(q)));
    }

    // ─── Actions ───

    /**
     * Reads the form and either creates a new booking or saves edits to an existing one,
     * depending on whether {@code editingBookingId} is set.
     * Shows a warning dialog if validation fails, or a success message on completion.
     */
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

        if (date.equals(LocalDate.now()) && LocalTime.parse(start).isBefore(LocalTime.now())) {
            showAlert(Alert.AlertType.WARNING, "Start time cannot be in the past. Please select a future time slot.");
            return;
        }

        try {
            if (editingBookingId != null) {
                BookingService.updateBooking(user, editingBookingId, res, date.toString(), start, end);
                showAlert(Alert.AlertType.INFORMATION, "Booking updated successfully.");
            } else {
                BookingService.createBooking(user, res, date.toString(), start, end);
                showAlert(Alert.AlertType.INFORMATION, "Booking request submitted successfully.");
            }
            clearEditState();
            loadBookings();
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.WARNING, ex.getMessage());
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage());
        }
    }

    /**
     * Pre-fills the booking form with the values of an existing booking so the user can edit it.
     * Switches the form into edit mode: the submit button changes to "Update Booking" and a
     * "Cancel Edit" button appears.
     *
     * @param b the booking to edit
     */
    private void populateFormForEdit(Booking b) {
        editingBookingId = b.getBookingID();

        // Pre-fill resource
        prefillResourceField(resourceDropdown.getItems().stream()
                .filter(r -> r.getResourceId().equals(b.getResourceID()))
                .findFirst().orElse(null));

        // Pre-fill date
        try { startDatePicker.setValue(LocalDate.parse(b.getDate())); }
        catch (Exception ignored) {}

        // Pre-fill times — refreshStartTimes first so the value is in the list
        refreshStartTimes(startDatePicker.getValue());
        startTimeCombo.setValue(b.getStartTime());
        endTimeCombo.setValue(b.getEndTime());

        // Switch form to edit mode
        if (formTitleLabel  != null) formTitleLabel.setText("Edit Booking");
        if (submitBtn       != null) submitBtn.setText("Update Booking");
        if (cancelEditBtn   != null) { cancelEditBtn.setVisible(true); cancelEditBtn.setManaged(true); }
    }

    /**
     * Exits edit mode and resets the form back to its default "new booking" state.
     */
    @FXML
    private void handleCancelEdit() {
        clearEditState();
    }

    /** Resets all edit-mode state and clears the form fields. */
    private void clearEditState() {
        editingBookingId = null;
        resourceDropdown.setValue(null);
        startDatePicker.setValue(null);
        startTimeCombo.setValue(null);
        endTimeCombo.getItems().clear();
        if (formTitleLabel != null) formTitleLabel.setText("New Booking Request");
        if (submitBtn      != null) submitBtn.setText("Submit Request");
        if (cancelEditBtn  != null) { cancelEditBtn.setVisible(false); cancelEditBtn.setManaged(false); }
    }

    /**
     * Asks the user to confirm, then cancels the given booking.
     *
     * @param b the booking to cancel
     */
    private void cancelBooking(Booking b) {
        if ("Cancelled".equalsIgnoreCase(b.getStatus())) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancel booking for " + b.getResourceName() + " on " + b.getDate() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            try {
                BookingService.cancelBooking(b.getBookingID());
                loadBookings();
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Failed to cancel: " + ex.getMessage());
            }
        });
    }

    /**
     * Configures the date picker for the selected resource.
     * Disables past dates and highlights dates that already have bookings in orange.
     *
     * @param resource the selected resource, or null to show no highlights
     */
    private void updateDatePickerForResource(ResourcesDatabaseManager.ResourceRow resource) {
        if (startDatePicker == null) return;
        Set<LocalDate> bookedDates = new HashSet<>();
        if (resource != null) {
            try { bookedDates.addAll(BookingsDatabaseManager.getBookedDates(resource.getResourceId())); }
            catch (SQLException ignored) {}
        }
        Set<LocalDate> finalBooked = bookedDates;
        startDatePicker.setDayCellFactory(p -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
                if (!empty && finalBooked.contains(date)) {
                    setStyle("-fx-background-color: #FFE0B2;");
                    setTooltip(new Tooltip("This resource has bookings on this date"));
                }
            }
        });
    }

    // ─── Helpers ───

    /**
     * Counts how many of the loaded bookings are currently active.
     *
     * @return number of active bookings
     */
    private long getActiveCount() {
        return masterData.stream()
                .filter(BookingService::countsAsActiveBooking)
                .count();
    }

    private int getMaxActive() {
        User user = SessionManager.getCurrentUser();
        int max = (user != null) ? user.getMaxActiveBookings() : 3;
        return max > 0 ? max : 3;
    }

    /**
     * Refreshes the start time dropdown based on the selected date.
     * When today is selected only future time slots are shown.
     * When a future date is selected all time slots are shown.
     * If the previously selected time is no longer valid it is cleared.
     *
     * @param date the selected date, or null if no date is chosen yet
     */
    private void refreshStartTimes(LocalDate date) {
        if (startTimeCombo == null) return;
        String selected = startTimeCombo.getValue();
        startTimeCombo.getItems().clear();

        boolean isToday = date != null && date.equals(LocalDate.now());
        LocalTime now = LocalTime.now();

        for (String slot : TIME_SLOTS) {
            if (!isToday || LocalTime.parse(slot).isAfter(now)) {
                startTimeCombo.getItems().add(slot);
            }
        }

        // Keep selection if still available, otherwise clear
        if (selected != null && startTimeCombo.getItems().contains(selected)) {
            startTimeCombo.setValue(selected);
        } else {
            startTimeCombo.setValue(null);
            if (endTimeCombo != null) endTimeCombo.getItems().clear();
        }
    }

    private static String safeStr(String val) { return val != null ? val : ""; }
    private static String safeStr(String val, String fallback) { return val != null ? val : fallback; }

    private static void stylePill(Label pill, String status) {
        switch (status.toLowerCase()) {
            case "confirmed" -> { pill.setBackground(bg("#E8F5E9")); pill.setTextFill(Color.web("#2E7D32")); }
            case "pending"   -> { pill.setBackground(bg("#EEF4FF")); pill.setTextFill(Color.web("#4F7CFF")); }
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
