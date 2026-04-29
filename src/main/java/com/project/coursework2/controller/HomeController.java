package com.project.coursework2.controller;

import com.project.coursework2.data.BookingsDatabaseManager;
import com.project.coursework2.data.ResourcesDatabaseManager;
import com.project.coursework2.model.*;
import com.project.coursework2.service.AvailabilityService;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.project.coursework2.data.DatabaseConnection.getConnection;

/**
 * Controller for the Home Dashboard (home-view.fxml).
 * Aggregates a personalised summary for the logged-in user: active/pending booking
 * counts, total hours used this month, live resource availability grid, the next
 * upcoming booking, upcoming-this-week list, recent booking history, study room
 * capacity bars, and maintenance alerts.
 *
 * @author CRBAS Team
 * @version 1.0
 */
public class HomeController {

    // ── Stat bar ──
    @FXML private Label activeCount;
    @FXML private Label pendingCount;
    @FXML private Label resourcesCount;
    @FXML private Label hoursUsed;
    @FXML private ProgressBar activeProgress;

    // ── Welcome banner ──
    @FXML private Label welcomeLabel;
    @FXML private Label welcomeRoleLabel;

    // ── Left column ──
    @FXML private ToggleButton btnRoomA;
    @FXML private ToggleButton btnRoomB;
    @FXML private ToggleButton btnRoomC;
    @FXML private HBox availabilityHourHeader;
    @FXML private GridPane availabilityGrid;
    @FXML private Label availabilityHint;
    @FXML private VBox recentHistoryContainer;

    // ── Right column ──
    @FXML private VBox nextUpCard;
    @FXML private VBox upcomingContainer;
    @FXML private VBox capacityContainer;
    @FXML private VBox maintenanceContainer;

    private static final int AVAILABILITY_DAY_COUNT = 4;
    private static final int AVAILABILITY_START_HOUR = 8;
    private static final int AVAILABILITY_END_HOUR = 18;
    private static final DateTimeFormatter AVAILABILITY_DAY_FMT = DateTimeFormatter.ofPattern("EEE\ndd/MM");

    // ── Selected Booking Slot ──

    private volatile String currentAvailabilityResourceId = "";

    @FXML
    public void initialize() {
        loadWelcomeBanner();
        loadActiveBookingsStat();
        loadPendingBookingsStat();
        loadAvailableResourcesStat();
        loadHoursUsedStat();
        loadAvailabilityGrid();
        loadNextBooking();
        loadUpcomingThisWeek();
        loadRecentHistory();
        loadCapacityStatus();
        loadMaintenanceAlerts();
    }

    // ══════════════════════ Welcome Banner ══════════════════════

    /**
     * Populates the welcome banner with a time-aware greeting, the user's first name,
     * and their role badge.
     */
    private void loadWelcomeBanner() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        int hour = LocalTime.now().getHour();
        String greeting = hour < 12 ? "Good morning" : hour < 17 ? "Good afternoon" : "Good evening";
        String firstName = user.getFirstName() != null ? user.getFirstName() : user.getName();

        welcomeLabel.setText(greeting + ", " + firstName + "!");
        welcomeRoleLabel.setText(user.getRole());
    }

    // ══════════════════════ Stat Bar ══════════════════════

    private void loadActiveBookingsStat() {
        // Query: SELECT COUNT(*) FROM Booking
        //        WHERE userID = SessionManager.getUserID()
        //        AND LOWER(status) IN ('pending','confirmed')
        // Set activeCount.setText(count + " / " + user.getMaxActiveBookings())
        // Set activeProgress.setProgress((double) count / maxActive)
        String query = "SELECT COUNT(*) FROM Booking WHERE userID = ? AND LOWER(status) IN ('pending', 'confirmed')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, SessionManager.getUserID());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    int maxActive = SessionManager.getCurrentUser().getMaxActiveBookings();
                    activeCount.setText(count + " / " + maxActive);
                    activeProgress.setProgress(maxActive > 0 ? (double) count / maxActive : 0);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadPendingBookingsStat() {
        // Query: SELECT COUNT(*) FROM Booking
        //        WHERE userID = SessionManager.getUserID()
        //        AND LOWER(status) = 'pending'
        // Set pendingCount.setText(String.valueOf(count))

        String query = "SELECT COUNT(*) FROM Booking WHERE userID = ? AND LOWER(status) = 'pending'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, SessionManager.getUserID());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    pendingCount.setText(String.valueOf(count));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAvailableResourcesStat() {
        // Query: SELECT COUNT(*) FROM Resource WHERE isActive = 1
        // Set resourcesCount.setText(String.valueOf(count))
        String query = "SELECT COUNT(*) FROM Resource WHERE isActive = 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    resourcesCount.setText(String.valueOf(count));
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHoursUsedStat() {
        // Query: SELECT startTime, endTime FROM Booking
        //        WHERE userID = SessionManager.getUserID()
        //        AND LOWER(status) = 'confirmed'
        //        AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')
        // For each row parse LocalTime, compute duration in minutes, sum and convert to hours
        // Set hoursUsed.setText(totalHours + "h")
        if (SessionManager.getCurrentUser() == null){
            hoursUsed.setText("0h");
            return;
        }

        String query = "SELECT startTime, endTime FROM Booking WHERE userID = ? AND LOWER(status) = 'confirmed' and strftime('%Y-%m', date) = strftime('%Y-%m', 'now')";
        long totalMins = 0;

        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, SessionManager.getUserID());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalTime start = LocalTime.parse(rs.getString("startTime"));
                    LocalTime end = LocalTime.parse(rs.getString("endTime"));
                    totalMins += Duration.between(start, end).toMinutes();
                }

            double totalHours = (double) totalMins / 60;
                hoursUsed.setText(String.format("%.1fh", totalHours));

            }
            } catch (Exception e) {
            e.printStackTrace();
            hoursUsed.setText("0h");
        }
    }

    // ══════════════════════ Availability Grid ══════════════════════

    private void loadAvailabilityGrid() {
        buildAvailabilityHourHeader();
        loadAvailabilityRooms();
    }

    private void buildAvailabilityHourHeader() {
        availabilityHourHeader.getChildren().clear();
        availabilityHourHeader.getChildren().add(spacer(50));

        for (int h = AVAILABILITY_START_HOUR; h < AVAILABILITY_END_HOUR; h++) {
            Label lbl = new Label(String.format("%02d", h));
            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #7A7A7A;");
            lbl.setPrefWidth(34);
            lbl.setAlignment(Pos.CENTER);
            availabilityHourHeader.getChildren().add(lbl);
        }
    }

    private void loadAvailabilityRooms() {
        ToggleButton[] buttons = { btnRoomA, btnRoomB, btnRoomC };
        String[] roomNames = { "Study Room A", "Study Room B", "Study Room C" };

        Task<List<ResourcesDatabaseManager.ResourceRow>> task = new Task<>() {
            @Override
            protected List<ResourcesDatabaseManager.ResourceRow> call() throws Exception {
                return ResourcesDatabaseManager.getAllResources().stream()
                        .filter(r -> ResourceType.STUDY_ROOM == ResourceType.from(r.getType()))
                        .filter(r -> matchesNamedRooms(r, roomNames))
                        .sorted((a, b) -> Integer.compare(roomOrder(a.getName(), roomNames), roomOrder(b.getName(), roomNames)))
                        .toList();
            }
        };

        task.setOnSucceeded(e -> {
            List<ResourcesDatabaseManager.ResourceRow> rooms = task.getValue();
            ToggleGroup group = new ToggleGroup();

            for (int i = 0; i < buttons.length; i++) {
                ToggleButton button = buttons[i];
                if (i < rooms.size()) {
                    ResourcesDatabaseManager.ResourceRow room = rooms.get(i);
                    button.setText(room.getName());
                    button.setUserData(room);
                    button.setToggleGroup(group);
                    button.setVisible(true);
                    button.setManaged(true);
                    styleAvailabilityButton(button, false);
                } else {
                    button.setVisible(false);
                    button.setManaged(false);
                }
            }

            group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle == null) {
                    if (oldToggle != null) oldToggle.setSelected(true);
                    return;
                }
                for (ToggleButton button : buttons) {
                    styleAvailabilityButton(button, button.isSelected());
                }
                ResourcesDatabaseManager.ResourceRow room =
                        (ResourcesDatabaseManager.ResourceRow) newToggle.getUserData();
                loadAvailabilityGridAsync(room);
            });

            if (!rooms.isEmpty()) {
                buttons[0].setSelected(true);
                styleAvailabilityButton(buttons[0], true);
                loadAvailabilityGridAsync(rooms.get(0));
            } else {
                availabilityGrid.getChildren().clear();
                availabilityHint.setText("Study Room A, B and C could not be found.");
            }
        });

        task.setOnFailed(e -> availabilityHint.setText("Could not load study rooms."));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private boolean matchesNamedRooms(ResourcesDatabaseManager.ResourceRow room, String[] roomNames) {
        for (String roomName : roomNames) {
            if (roomName.equalsIgnoreCase(room.getName())) return true;
        }
        return false;
    }

    private int roomOrder(String roomName, String[] roomNames) {
        for (int i = 0; i < roomNames.length; i++) {
            if (roomNames[i].equalsIgnoreCase(roomName)) return i;
        }
        return roomNames.length;
    }

    private void loadAvailabilityGridAsync(ResourcesDatabaseManager.ResourceRow room) {
        currentAvailabilityResourceId = room.getResourceId();
        availabilityGrid.getChildren().clear();
        availabilityHint.setText("Loading availability...");

        Task<List<List<AvailabilitySlot>>> task = new Task<>() {
            @Override
            protected List<List<AvailabilitySlot>> call() {
                return AvailabilityService.loadGrid(room.getResourceId(), LocalDate.now(), AVAILABILITY_DAY_COUNT);
            }
        };

        task.setOnSucceeded(e -> {
            if (!room.getResourceId().equals(currentAvailabilityResourceId)) return;
            buildSelectedRoomAvailabilityGrid(task.getValue());
            availabilityHint.setText("Click a free slot to go to the booking form.");
        });

        task.setOnFailed(e -> availabilityHint.setText("Could not load availability."));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void buildSelectedRoomAvailabilityGrid(List<List<AvailabilitySlot>> gridData) {
        availabilityGrid.getChildren().clear();
        LocalDate today = LocalDate.now();

        for (int dayIndex = 0; dayIndex < gridData.size(); dayIndex++) {
            LocalDate day = today.plusDays(dayIndex);
            Label dayLabel = new Label(day.format(AVAILABILITY_DAY_FMT));
            dayLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7A7A7A; -fx-alignment: CENTER;");
            dayLabel.setPrefWidth(50);
            availabilityGrid.add(dayLabel, 0, dayIndex);

            List<AvailabilitySlot> row = gridData.get(dayIndex);
            for (int slotIndex = 0; slotIndex < row.size(); slotIndex++) {
                availabilityGrid.add(makeAvailabilityCell(row.get(slotIndex)), slotIndex + 1, dayIndex);
            }
        }
    }

    private StackPane makeAvailabilityCell(AvailabilitySlot slot) {
        Rectangle rect = new Rectangle(42, 42);
        rect.setArcWidth(6);
        rect.setArcHeight(6);

        switch (slot.status()) {
            case BOOKED -> rect.setFill(Color.web("#F5C0B0"));
            case YOURS  -> rect.setFill(Color.web("#B0D4F5"));
            case CLOSED -> rect.setFill(Color.web("#F5E6A0"));
            default     -> rect.setFill(Color.web("#B0E8E2"));
        }

        StackPane cell = new StackPane(rect);
        String timeStr = String.format("%02d:00 - %02d:00", slot.start().getHour(), slot.end().getHour());
        Tooltip.install(cell, new Tooltip(timeStr + " | " + slot.status()));

        if (slot.status() == com.project.coursework2.model.SlotStatus.FREE) {
            rect.setStroke(Color.web("#4AADA8"));
            rect.setStrokeWidth(1);
            cell.setStyle("-fx-cursor: hand;");
            cell.setOnMouseClicked(e -> handleRebook());
        }

        return cell;
    }

    private void styleAvailabilityButton(ToggleButton button, boolean selected) {
        String background = selected ? "#2C3E50" : "#EEF0F2";
        String text = selected ? "white" : "#333333";
        button.setStyle("-fx-background-radius: 8; -fx-padding: 6 14; -fx-background-color: "
                + background + "; -fx-text-fill: " + text + ";");
    }

    private Region spacer(double width) {
        Region r = new Region();
        r.setPrefWidth(width);
        return r;
    }

    // ══════════════════════ Recent Booking History ══════════════════════

    /**
     * Loads the 5 most recent bookings for the current user (any status) and
     * renders each as a compact row with resource name, date/time, and a colour-coded
     * status pill.
     */
    private void loadRecentHistory() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;
        recentHistoryContainer.getChildren().clear();
        try {
            List<Booking> recent = BookingsDatabaseManager.getRecentBookings(user.getUserID(), 5);
            if (recent.isEmpty()) {
                recentHistoryContainer.getChildren().add(emptyLabel("No booking history yet"));
                return;
            }
            for (Booking b : recent) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(6, 8, 6, 8));
                row.setStyle("-fx-background-color: #F8F8F8; -fx-background-radius: 6;");

                VBox info = new VBox(2);
                Label name = new Label(b.getResourceName() != null ? b.getResourceName() : "Unknown resource");
                name.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #3D3D3D;");
                Label when = new Label(b.getDate() + "  " + b.getStartTime() + " – " + b.getEndTime());
                when.setStyle("-fx-font-size: 11px; -fx-text-fill: #7A7A7A;");
                info.getChildren().addAll(name, when);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label pill = new Label(b.getStatus());
                pill.setPadding(new Insets(2, 8, 2, 8));
                pill.setStyle("-fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold; "
                        + statusPillStyle(b.getStatus()));

                row.getChildren().addAll(info, spacer, pill);
                recentHistoryContainer.getChildren().add(row);
            }
        } catch (Exception e) {
            recentHistoryContainer.getChildren().add(emptyLabel("Could not load history"));
        }
    }

    // ══════════════════════ Study Room Capacity ══════════════════════

    /**
     * Loads today's seat occupancy for every active study room and renders each as a
     * labelled progress bar: room name on the left, "X / Y seats" on the right,
     * with the bar filling from teal (empty) to orange (nearly full) to red (full).
     */
    private void loadCapacityStatus() {
        capacityContainer.getChildren().clear();
        try {
            List<ResourcesDatabaseManager.CapacityRow> rows =
                    ResourcesDatabaseManager.getStudyRoomCapacity();
            if (rows.isEmpty()) {
                capacityContainer.getChildren().add(emptyLabel("No study rooms found"));
                return;
            }
            for (ResourcesDatabaseManager.CapacityRow cr : rows) {
                VBox entry = new VBox(4);

                HBox header = new HBox();
                Label nameLabel = new Label(cr.name);
                nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #3D3D3D;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label seats = new Label(cr.seatsBooked + " / " + cr.capacity + " seats");
                seats.setStyle("-fx-font-size: 11px; -fx-text-fill: #7A7A7A;");
                header.getChildren().addAll(nameLabel, spacer, seats);

                ProgressBar bar = new ProgressBar(cr.occupancy());
                bar.setPrefWidth(Double.MAX_VALUE);
                bar.setStyle(capacityBarStyle(cr.occupancy()));

                entry.getChildren().addAll(header, bar);
                capacityContainer.getChildren().add(entry);
            }
        } catch (Exception e) {
            capacityContainer.getChildren().add(emptyLabel("Could not load capacity data"));
        }
    }

    // ══════════════════════ Next Up ══════════════════════

    private void loadNextBooking() {
        // Query: SELECT b.bookingID, b.date, b.startTime, b.endTime, r.name, r.building, r.room
        //        FROM Booking b JOIN Resource r ON b.resourceID = r.resourceID
        //        WHERE b.userID = SessionManager.getUserID()
        //        AND LOWER(b.status) IN ('confirmed','pending')
        //        AND (b.date > date('now') OR (b.date = date('now') AND b.startTime >= time('now')))
        //        ORDER BY b.date ASC, b.startTime ASC LIMIT 1
        // Populate nextUpCard labels: resource name, formatted date+time, building/room
        // If empty show "No upcoming bookings" placeholder
        // Clear the FXML placeholder (keep the "Next up" title at index 0)
        if (nextUpCard.getChildren().size() > 1) {
            nextUpCard.getChildren().remove(1, nextUpCard.getChildren().size());
        }
        if (SessionManager.getCurrentUser() == null) {
            nextUpCard.getChildren().add(emptyLabel("No upcoming bookings"));
            return;
        }
        String query = "SELECT b.bookingID, b.date, b.startTime, b.endTime, " +
                "r.name, r.building, r.room " +
                "FROM Booking b JOIN Resource r ON b.resourceID = r.resourceID " +
                "WHERE b.userID = ? " +
                "AND LOWER(b.status) IN ('confirmed', 'pending') " +
                "AND (b.date > date('now') OR (b.date = date('now') AND b.startTime >= time('now'))) " +
                "ORDER BY b.date ASC, b.startTime ASC " +
                "LIMIT 1";

        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, SessionManager.getUserID());
            try(ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String resourceName = rs.getString("name");
                    String date = rs.getString("date");
                    String startTime = rs.getString("startTime");
                    String endTime = rs.getString("endTime");
                    String building = rs.getString("building");
                    String room = rs.getString("room");

                    Label title = new Label(resourceName);
                    title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    Label time = new Label(date + " | " + startTime + " - " + endTime);
                    Label location = new Label(
                            ((building != null ? building : "") + " " + (room != null ? room : "")).trim());

                    nextUpCard.getChildren().addAll(title, time, location);
                } else {
                    nextUpCard.getChildren().add(emptyLabel("No upcoming bookings"));
                }
            }
        }catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    // ══════════════════════ Upcoming This Week ══════════════════════

    private void loadUpcomingThisWeek() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;
        upcomingContainer.getChildren().clear();

        String today = LocalDate.now().toString();
        String endOfWeek = LocalDate.now().plusDays(6).toString();
        String query = "SELECT b.date, b.startTime, b.endTime, r.name, b.status " +
                "FROM Booking b JOIN Resource r ON b.resourceID = r.resourceID " +
                "WHERE b.userID = ? AND LOWER(b.status) IN ('confirmed','pending') " +
                "AND b.date BETWEEN ? AND ? " +
                "ORDER BY b.date ASC, b.startTime ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUserID());
            stmt.setString(2, today);
            stmt.setString(3, endOfWeek);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(6, 8, 6, 8));
                    row.setStyle("-fx-background-color: #F8F8F8; -fx-background-radius: 6;");

                    VBox info = new VBox(2);
                    Label name = new Label(rs.getString("name"));
                    name.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #3D3D3D;");
                    Label when = new Label(rs.getString("date") + "  " + rs.getString("startTime") + " – " + rs.getString("endTime"));
                    when.setStyle("-fx-font-size: 11px; -fx-text-fill: #7A7A7A;");
                    info.getChildren().addAll(name, when);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    String status = rs.getString("status");
                    Label pill = new Label(status);
                    pill.setPadding(new Insets(2, 8, 2, 8));
                    pill.setStyle("-fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold; " + statusPillStyle(status));

                    row.getChildren().addAll(info, spacer, pill);
                    upcomingContainer.getChildren().add(row);
                }
                if (!found) {
                    upcomingContainer.getChildren().add(emptyLabel("No bookings this week"));
                }
            }
        } catch (Exception e) {
            upcomingContainer.getChildren().add(emptyLabel("Could not load upcoming bookings"));
        }
    }

    // ══════════════════════ Maintenance Alerts ══════════════════════

    private void loadMaintenanceAlerts() {
        maintenanceContainer.getChildren().clear();
        String query = "SELECT r.name, m.startDate, m.startTime, m.endDate, m.endTime, m.reason " +
                "FROM MaintenanceWindow m JOIN Resource r ON m.resourceID = r.resourceID " +
                "WHERE datetime(m.startDate || ' ' || m.startTime) >= datetime('now') " +
                "ORDER BY m.startDate ASC, m.startTime ASC LIMIT 5";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                String reason = rs.getString("reason");
                String text = rs.getString("name") + " — "
                        + rs.getString("startDate") + " " + rs.getString("startTime")
                        + " to " + rs.getString("endDate") + " " + rs.getString("endTime")
                        + (reason != null && !reason.isBlank() ? " (" + reason + ")" : "");
                Label alert = new Label(text);
                alert.setWrapText(true);
                alert.setStyle("-fx-font-size: 11px; -fx-text-fill: #C62828;");
                maintenanceContainer.getChildren().add(alert);
            }
            if (!found) {
                maintenanceContainer.getChildren().add(emptyLabel("No maintenance scheduled"));
            }
        } catch (Exception e) {
            maintenanceContainer.getChildren().add(emptyLabel("Could not load maintenance alerts"));
        }
    }

    // ══════════════════════ Action Handlers ══════════════════════

    @FXML
    private void handleRebook() {
        try {
            Node page = FXMLLoader.load(getClass().getResource(
                    "/com/project/coursework2/bookings-view.fxml"));
            SidebarController.mainContentArea.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMaintenance() {
        if (com.project.coursework2.model.Role.ADMIN != com.project.coursework2.model.Role.from(SessionManager.getUserRole())) return;
        try {
            SessionManager.setAdminActiveTab("bookings");
            Node page = FXMLLoader.load(getClass().getResource(
                    "/com/project/coursework2/admin-view.fxml"));
            SidebarController.mainContentArea.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ══════════════════════ Helpers ══════════════════════

    private Label emptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #7A7A7A;");
        return lbl;
    }

    private String statusPillStyle(String status) {
        if (status == null) return "-fx-background-color: #E0E0E0; -fx-text-fill: #555;";
        return switch (status.toLowerCase()) {
            case "confirmed"  -> "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;";
            case "pending"    -> "-fx-background-color: #FFF8E1; -fx-text-fill: #F57F17;";
            case "cancelled"  -> "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828;";
            case "completed"  -> "-fx-background-color: #E3F2FD; -fx-text-fill: #1565C0;";
            default           -> "-fx-background-color: #E0E0E0; -fx-text-fill: #555;";
        };
    }

    private String capacityBarStyle(double occupancy) {
        String colour = occupancy >= 1.0 ? "#E8735A"   // full → red-orange
                      : occupancy >= 0.7 ? "#F5C842"   // busy → amber
                      :                    "#4AADA8";  // free → teal
        return "-fx-accent: " + colour + ";";
    }
}
