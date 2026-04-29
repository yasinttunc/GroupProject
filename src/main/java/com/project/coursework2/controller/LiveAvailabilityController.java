package com.project.coursework2.controller;

import com.project.coursework2.data.ResourcesDatabaseManager.ResourceRow;
import com.project.coursework2.model.AvailabilitySlot;
import com.project.coursework2.model.ResourceType;
import com.project.coursework2.model.SlotStatus;
import com.project.coursework2.service.AvailabilityService;
import com.project.coursework2.service.ResourceService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LiveAvailabilityController {

    // ── FXML fields — names must match fx:id exactly ──
    @FXML private ToggleButton btnRoomA, btnRoomB, btnRoomC;
    @FXML private HBox         hourHeader;
    @FXML private GridPane     grid;
    @FXML private Label        hint;

    private static final int DAY_COUNT   = 4;
    private static final int START_HOUR  = 8;
    private static final int END_HOUR    = 18;
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("EEE\ndd/MM");

    // Tracks the resource currently being displayed — lets us discard stale Task results
    private volatile String currentResourceId = "";

    // ── Slot colours ──
    private static final String COLOR_FREE   = "#B0E8E2";
    private static final String COLOR_BOOKED = "#F5C0B0";
    private static final String COLOR_YOURS  = "#B0D4F5";
    private static final String COLOR_CLOSED = "#F5E6A0";

    @FXML
    public void initialize() {
        buildHourHeader();
        loadRoomsAndSetupButtons();
    }

    // ── Step 1: build the "08  09  10 …" header ──────────────────────────────

    private void buildHourHeader() {
        // Column 0 is the day label column — leave it blank in the header
        hourHeader.getChildren().add(spacer(50));

        for (int h = START_HOUR; h < END_HOUR; h++) {
            Label lbl = new Label(String.format("%02d", h));
            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #7A7A7A;");
            lbl.setPrefWidth(34);
            lbl.setAlignment(Pos.CENTER);
            hourHeader.getChildren().add(lbl);
        }
    }

    // ── Step 2: load study rooms from DB and wire the toggle buttons ─────────

    private void loadRoomsAndSetupButtons() {
        // Load study rooms on a background thread (DB call)
        Task<List<ResourceRow>> task = new Task<>() {
            @Override
            protected List<ResourceRow> call() throws Exception {
                return ResourceService.getByType(ResourceType.STUDY_ROOM);
            }
        };

        task.setOnSucceeded(e -> {
            List<ResourceRow> rooms = task.getValue();
            ToggleButton[] buttons = { btnRoomA, btnRoomB, btnRoomC };

            // ToggleGroup ensures only one button is selected at a time
            ToggleGroup group = new ToggleGroup();

            for (int i = 0; i < buttons.length; i++) {
                if (i < rooms.size()) {
                    ResourceRow room = rooms.get(i);
                    buttons[i].setText(room.getName());
                    // Store the resource ID in getUserData() — we retrieve it when clicked
                    buttons[i].setUserData(room.getResourceId());
                    buttons[i].setToggleGroup(group);
                    buttons[i].setVisible(true);
                } else {
                    buttons[i].setVisible(false); // hide if DB has fewer than 3 rooms
                    buttons[i].setManaged(false);
                }
            }

            // Listen for toggle selection changes
            group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle == null) {
                    // Prevent deselecting — always keep one room selected
                    if (oldToggle != null) oldToggle.setSelected(true);
                    return;
                }
                String resourceId = (String) newToggle.getUserData();
                loadGridAsync(resourceId);
            });

            // Auto-select the first room on load
            if (!rooms.isEmpty()) {
                buttons[0].setSelected(true);
                loadGridAsync(rooms.get(0).getResourceId());
            }
        });

        task.setOnFailed(e -> hint.setText("Could not load rooms: " + task.getException().getMessage()));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    // ── Step 3: load the availability grid in the background ─────────────────

    private void loadGridAsync(String resourceId) {
        currentResourceId = resourceId;
        hint.setText("Loading...");
        grid.getChildren().clear();

        Task<List<List<AvailabilitySlot>>> task = new Task<>() {
            @Override
            protected List<List<AvailabilitySlot>> call() {
                return AvailabilityService.loadGrid(resourceId, LocalDate.now(), DAY_COUNT);
            }
        };

        task.setOnSucceeded(e -> {
            // Discard result if user switched rooms while this Task was running
            if (!resourceId.equals(currentResourceId)) return;
            buildGrid(task.getValue());
            hint.setText("Click a free slot to go to the booking form.");
        });

        task.setOnFailed(e -> hint.setText("Could not load grid: " + task.getException().getMessage()));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    // ── Step 4: turn the List<List<AvailabilitySlot>> into visual GridPane cells

    private void buildGrid(List<List<AvailabilitySlot>> gridData) {
        grid.getChildren().clear();

        LocalDate today = LocalDate.now();

        for (int dayIndex = 0; dayIndex < gridData.size(); dayIndex++) {
            // Column 0: day label  (Mon\n28/04)
            LocalDate day = today.plusDays(dayIndex);
            Label dayLabel = new Label(day.format(DAY_FMT));
            dayLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7A7A7A; -fx-alignment: CENTER;");
            dayLabel.setPrefWidth(50);
            // GridPane.add(node, column, row)
            grid.add(dayLabel, 0, dayIndex);

            // Columns 1–10: one cell per hour slot
            List<AvailabilitySlot> row = gridData.get(dayIndex);
            for (int slotIndex = 0; slotIndex < row.size(); slotIndex++) {
                AvailabilitySlot slot = row.get(slotIndex);
                StackPane cell = makeCell(slot);
                grid.add(cell, slotIndex + 1, dayIndex);
            }
        }
    }

    // ── Step 5: create one coloured cell ─────────────────────────────────────

    private StackPane makeCell(AvailabilitySlot slot) {
        Rectangle rect = new Rectangle(32, 32);
        rect.setArcWidth(6);
        rect.setArcHeight(6);

        // Pick colour based on status
        switch (slot.status()) {
            case BOOKED -> rect.setFill(Color.web(COLOR_BOOKED));
            case YOURS  -> rect.setFill(Color.web(COLOR_YOURS));
            case CLOSED -> rect.setFill(Color.web(COLOR_CLOSED));
            default     -> rect.setFill(Color.web(COLOR_FREE));   // FREE
        }

        StackPane cell = new StackPane(rect);

        // Tooltip shows the time on hover
        String timeStr = String.format("%02d:00 – %02d:00",
                slot.start().getHour(), slot.end().getHour());
        Tooltip.install(cell, new Tooltip(timeStr + " · " + slot.status()));

        // FREE slots are clickable — navigate to the booking form
        if (slot.status() == SlotStatus.FREE) {
            rect.setStroke(Color.web("#4AADA8"));
            rect.setStrokeWidth(1);
            cell.setStyle("-fx-cursor: hand;");
            cell.setOnMouseClicked(e -> navigateToBookings());
        }

        return cell;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void navigateToBookings() {
        try {
            java.net.URL url = getClass().getResource("/com/project/coursework2/bookings-view.fxml");
            if (url == null) { hint.setText("Booking form not found."); return; }
            javafx.scene.Node page = javafx.fxml.FXMLLoader.load(url);
            SidebarController.mainContentArea.getChildren().setAll(page);
        } catch (java.io.IOException e) {
            hint.setText("Could not open booking form.");
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private Region spacer(double width) {
        Region r = new Region();
        r.setPrefWidth(width);
        return r;
    }
}
