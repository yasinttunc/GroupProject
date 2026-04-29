package com.project.coursework2.controller;

import java.sql.SQLException;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import com.project.coursework2.data.AdminManager;
import com.project.coursework2.data.BookingsDatabaseManager;
import com.project.coursework2.data.ResourcesDatabaseManager;
import com.project.coursework2.data.ResourcesDatabaseManager.ResourceRow;
import com.project.coursework2.data.UserDatabaseManager;
import com.project.coursework2.model.Booking;
import com.project.coursework2.model.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the admin-view.fxml.
 * Manages page switching between Users, Resources, and Bookings,
 * handles data loading, live search filtering, and pending booking cards.
 */
public class AdminController {

    // ── Page panes ──
    @FXML private VBox usersPane;
    @FXML private VBox resourcesPane;
    @FXML private ScrollPane bookingsPane;

    // ── Users page ──
    @FXML private TextField userSearchField;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userNameCol;
    @FXML private TableColumn<User, String> userEmailCol;
    @FXML private TableColumn<User, String> userRoleCol;
    @FXML private Label userCountLabel;

    // ── Resources page ──
    @FXML private VBox maintenanceWindowsContainer;
    @FXML private TextField resourceSearchField;
    @FXML private TableView<ResourceRow> resourcesTable;
    @FXML private TableColumn<ResourceRow, String> resNameCol;
    @FXML private TableColumn<ResourceRow, String> resTypeCol;
    @FXML private TableColumn<ResourceRow, String> resRequiredRoleCol;
    @FXML private TableColumn<ResourceRow, Integer> resDurationCol;
    @FXML private TableColumn<ResourceRow, String> resBuildingCol;
    @FXML private TableColumn<ResourceRow, String> resRoomCol;
    @FXML private TableColumn<ResourceRow, String> resActiveCol;
    @FXML private Label resourceCountLabel;

    // ── Bookings page ──
    @FXML private VBox pendingRequestsContainer;
    @FXML private TableView<Booking> bookingHistoryTable;
    @FXML private TableColumn<Booking, String> histBookingIdCol;
    @FXML private TableColumn<Booking, String> histUserIdCol;
    @FXML private TableColumn<Booking, String> histResourceCol;
    @FXML private TableColumn<Booking, String> histDateCol;
    @FXML private TableColumn<Booking, String> histTimeCol;
    @FXML private TableColumn<Booking, String> histStatusCol;
    @FXML private Label pendingCountLabel;
    @FXML private Label totalBookingsLabel;

    // ── Edit Forms ──
    @FXML private TextField userFirstNameField;
    @FXML private TextField userLastNameField;
    @FXML private TextField userEmailField;
    @FXML private TextField userPasswordField;
    @FXML private ComboBox<String> userRoleCombo;
    @FXML private TextField userIdField;

    // ── Dynamic Edit Forms for Roles ──
    @FXML private VBox dynamicFieldsContainer;
    @FXML private VBox studentFieldsBox;
    @FXML private TextField studentYearField;
    @FXML private TextField studentCourseField;
    @FXML private TextField studentEnrollmentField;
    
    @FXML private HBox staffFieldsBox;
    @FXML private TextField staffJobTitleField;
    @FXML private TextField staffDeptField;
    
    @FXML private HBox adminFieldsBox;
    @FXML private TextField adminLevelField;

    @FXML private TextField resNameField;
    @FXML private ComboBox<String> resTypeCombo;
    @FXML private ComboBox<String> resRoleCombo;
    @FXML private TextField resMaxDurationField;
    @FXML private TextField resBuildingField;
    @FXML private TextField resRoomField;
    @FXML private ComboBox<String> resActiveCombo;
    @FXML private TextField resIdField;

    @FXML private TextField bookDateField;
    @FXML private ComboBox<String> bookStatusCombo;
    @FXML private TextField bookStartTimeField;
    @FXML private TextField bookEndTimeField;
    @FXML private TextField bookIdField;
    @FXML private TextField bookUserIdField;
    @FXML private TextField bookResIdField;

    /** Master user list for filtering. */
    private ObservableList<User> masterUserList;
    /** Master resource list for filtering. */
    private ObservableList<ResourceRow> masterResourceList;


    // ═══════════════════════════ Initialisation ═══════════════════════════════

    @FXML
    public void initialize() {
        if (com.project.coursework2.model.Role.ADMIN != com.project.coursework2.model.Role.from(SessionManager.getUserRole())) {
            try {
                javafx.scene.Node home = javafx.fxml.FXMLLoader.load(
                        getClass().getResource("/com/project/coursework2/home-view.fxml"));
                if (SidebarController.mainContentArea != null) {
                    SidebarController.mainContentArea.getChildren().setAll(home);
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return;
        }

        userNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        resNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        resTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        resRequiredRoleCol.setCellValueFactory(new PropertyValueFactory<>("requiredRole"));
        resDurationCol.setCellValueFactory(new PropertyValueFactory<>("maxBookingDuration"));
        resBuildingCol.setCellValueFactory(new PropertyValueFactory<>("building"));
        resRoomCol.setCellValueFactory(new PropertyValueFactory<>("room"));
        resActiveCol.setCellValueFactory(new PropertyValueFactory<>("activeText"));

        if (resRoleCombo != null) {
            resRoleCombo.getItems().addAll("Student", "Staff", "Admin");
        }
        if (resTypeCombo != null) {
            resTypeCombo.getItems().addAll("StudyRoom", "Equipment", "Lab");
        }

        histBookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        histUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        histResourceCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        histDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        histTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        histStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // ── Load data ──
        loadUsers();
        loadResources();
        loadBookings();
        loadMaintenanceWindows();

        // ── Set up live search ──
        setupUserSearch();
        setupResourceSearch();

        // ── Populate ComboBoxes ──
        userRoleCombo.setItems(FXCollections.observableArrayList("Student", "Staff", "Admin"));
        resActiveCombo.setItems(FXCollections.observableArrayList("Yes", "No"));
        bookStatusCombo.setItems(FXCollections.observableArrayList("pending", "confirmed", "cancelled", "completed"));

        // ── Listen to role combo box changes ──
        userRoleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateDynamicFieldsVisibility(newVal);
        });

        // ── Set up table listeners for edit forms ──
        setupTableListeners();

        // ── Show the correct page based on sidebar selection ──
        String activeTab = SessionManager.getAdminActiveTab();
        switch (activeTab) {
            case "resources":
                showPage(resourcesPane);
                break;
            case "bookings":
                showPage(bookingsPane);
                break;
            default:
                showPage(usersPane);
                break;
        }

        System.out.println("Admin page loaded");
    }

    // ═══════════════════════════ Page Switching ═══════════════════════════════

    /**
     * Shows the given page node and hides all others.
     */
    private void showPage(Node activePage) {
        Node[] pages = { usersPane, resourcesPane, bookingsPane };
        for (Node page : pages) {
            boolean isActive = (page == activePage);
            page.setVisible(isActive);
            page.setManaged(isActive);
        }
    }

    // ═══════════════════════════ Dynamic Fields Logic ═════════════════════════

    private void updateDynamicFieldsVisibility(String role) {
        studentFieldsBox.setManaged(false);
        studentFieldsBox.setVisible(false);
        staffFieldsBox.setManaged(false);
        staffFieldsBox.setVisible(false);
        adminFieldsBox.setManaged(false);
        adminFieldsBox.setVisible(false);

        if (role == null) return;

        switch (role) {
            case "Student":
                studentFieldsBox.setManaged(true);
                studentFieldsBox.setVisible(true);
                break;
            case "Staff":
                staffFieldsBox.setManaged(true);
                staffFieldsBox.setVisible(true);
                break;
            case "Admin":
                adminFieldsBox.setManaged(true);
                adminFieldsBox.setVisible(true);
                break;
        }
    }

    // ═══════════════════════════ Table Listeners ══════════════════════════════

    private void setupTableListeners() {
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                userFirstNameField.setText(newSel.getFirstName() != null ? newSel.getFirstName() : "");
                userLastNameField.setText(newSel.getLastName() != null ? newSel.getLastName() : "");
                userEmailField.setText(newSel.getEmail() != null ? newSel.getEmail() : "");
                userPasswordField.clear(); // never show stored password
                userRoleCombo.setValue(newSel.getRole());
                userIdField.setText(newSel.getUserID() != null ? newSel.getUserID() : "");

            } else {
                userFirstNameField.clear(); userLastNameField.clear(); userEmailField.clear(); userPasswordField.clear(); userRoleCombo.setValue(null); userIdField.clear();
            }
        });

        resourcesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                resNameField.setText(newSel.getName() != null ? newSel.getName() : "");
                if (resTypeCombo != null) resTypeCombo.setValue(newSel.getType() != null ? newSel.getType() : "");
                resBuildingField.setText(newSel.getBuilding() != null ? newSel.getBuilding() : "");
                resRoomField.setText(newSel.getRoom() != null ? newSel.getRoom() : "");
                resActiveCombo.setValue(newSel.isActive() ? "Yes" : "No");
                resIdField.setText(newSel.getResourceId() != null ? newSel.getResourceId() : "");
                if (resRoleCombo != null) resRoleCombo.setValue(newSel.getRequiredRole() != null ? newSel.getRequiredRole() : "");
                if (resMaxDurationField != null) resMaxDurationField.setText(String.valueOf(newSel.getMaxBookingDuration()));
            } else {
                resNameField.clear(); if (resTypeCombo != null) resTypeCombo.setValue(null); resBuildingField.clear(); resRoomField.clear(); resActiveCombo.setValue(null); resIdField.clear();
                if (resRoleCombo != null) resRoleCombo.setValue(null);
                if (resMaxDurationField != null) resMaxDurationField.clear();
            }
        });

        bookingHistoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                bookDateField.setText(newSel.getDate() != null ? newSel.getDate() : "");
                bookStartTimeField.setText(newSel.getStartTime() != null ? newSel.getStartTime() : "");
                bookEndTimeField.setText(newSel.getEndTime() != null ? newSel.getEndTime() : "");
                bookStatusCombo.setValue(newSel.getStatus() != null ? newSel.getStatus() : "");
                bookIdField.setText(newSel.getBookingID() != null ? newSel.getBookingID() : "");
                bookUserIdField.setText(newSel.getUserID() != null ? newSel.getUserID() : "");
                bookResIdField.setText(newSel.getResourceID() != null ? newSel.getResourceID() : "");
            } else {
                bookDateField.clear(); bookStartTimeField.clear(); bookEndTimeField.clear(); bookStatusCombo.setValue(null); bookIdField.clear(); bookUserIdField.clear(); bookResIdField.clear();
            }
        });
    }

    // ═══════════════════════════ Utility Methods ══════════════════════════════

    private void clearUserForm() {
        userFirstNameField.clear(); userLastNameField.clear(); userEmailField.clear(); userPasswordField.clear(); userRoleCombo.setValue(null); userIdField.clear();
        if (studentYearField != null) studentYearField.clear();
        if (studentCourseField != null) studentCourseField.clear();
        if (studentEnrollmentField != null) studentEnrollmentField.clear();
        if (staffJobTitleField != null) staffJobTitleField.clear();
        if (staffDeptField != null) staffDeptField.clear();
        if (adminLevelField != null) adminLevelField.clear();
    }

    private void clearResourceForm() {
        resNameField.clear(); if (resTypeCombo != null) resTypeCombo.setValue(null); resBuildingField.clear(); resRoomField.clear(); resActiveCombo.setValue(null); resIdField.clear();
        if (resRoleCombo != null) resRoleCombo.setValue(null);
        if (resMaxDurationField != null) resMaxDurationField.clear();
    }

    private void clearBookingForm() {
        bookDateField.clear(); bookStartTimeField.clear(); bookEndTimeField.clear(); bookStatusCombo.setValue(null); bookIdField.clear(); bookUserIdField.clear(); bookResIdField.clear();
    }

     private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML public void handleSaveUser() {

        String firstName = userFirstNameField.getText();
        String lastName = userLastNameField.getText();
        String email = userEmailField.getText();
        String password = userPasswordField.getText();
        String role = userRoleCombo.getValue();
        String userId = userIdField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || role == null || userId.isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }
        try {
            if (password.isEmpty()) {
                AdminManager.updateUserNoPassword(userId, firstName, lastName, email, role);
            } else {
                AdminManager.updateUser(userId, firstName, lastName, email, password, role);
            }
            loadUsers();
            clearUserForm();
            showAlert("Success", "User updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not update user: " + e.getMessage());
        }
    }
    @FXML public void handleDeleteUser() {
        String userID = userIdField.getText();
        if (userID == null || userID.isEmpty()) {
            showAlert("Error", "Please select a user to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete user " + userID + "? This cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;
        try {
            AdminManager.deleteUser(userID);
            loadUsers();
            clearUserForm();
            showAlert("Success", "User deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not delete user: " + e.getMessage());
        }
    }

    @FXML public void handleAddUser() {
        LocalDate createdDate = LocalDate.now();
        LocalTime createdTime = LocalTime.now();
        String firstName = userFirstNameField.getText();
        String lastName = userLastNameField.getText();
        String email = userEmailField.getText();
        String password = userPasswordField.getText();
        String role = userRoleCombo.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Please fill in all required fields including role.");
            return;
        }

        try{
            String generatedID = AdminManager.addUser(firstName,lastName,email,password,role,createdDate,createdTime);
            switch (role) {
                case "Student":
                    int yearOfStudy = 1;
                    try {
                        if (!studentYearField.getText().isEmpty()) {
                            yearOfStudy = Integer.parseInt(studentYearField.getText());
                        }
                    } catch(NumberFormatException ignored) {}
                    String courseName = studentCourseField.getText() != null ? studentCourseField.getText() : "";
                    String enrollmentDate = studentEnrollmentField.getText();
                    if (enrollmentDate == null || enrollmentDate.isEmpty()) enrollmentDate = createdDate.toString();
                    
                    // AdminManager.addStudent expects: (userID, course, yearOfStudy, enrolledAt)
                    AdminManager.addStudent(generatedID, courseName, yearOfStudy, enrollmentDate);
                    break;
                case "Admin":
                    int adminLevel = 1;
                    try {
                        if (!adminLevelField.getText().isEmpty()) {
                            adminLevel = Integer.parseInt(adminLevelField.getText());
                        }
                    } catch(NumberFormatException ignored) {}
                    
                    AdminManager.addAdmin(generatedID, adminLevel);
                    break;
                case "Staff":
                    String staffJobTitle = staffJobTitleField.getText() != null ? staffJobTitleField.getText() : "";
                    String department = staffDeptField.getText() != null ? staffDeptField.getText() : "";
                    
                    AdminManager.addStaff(generatedID, staffJobTitle, department);
                    break;
            }
            
            loadUsers();
            clearUserForm();
            showAlert("Success", "User added successfully. Base ID: " + generatedID);
        }
        catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not add user: " + e.getMessage());
        }
    }
    @FXML
    public void handleAddMaintenance() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add Maintenance Window");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        ComboBox<String> resourceIdBox = new ComboBox<>();

        try {
            ResourcesDatabaseManager.getAllResources()
                    .forEach(r -> resourceIdBox.getItems().add(r.getResourceId() + " – " + r.getName()));
        } catch (Exception e) {
            System.out.println("Could not load resources: " + e.getMessage());
        }

        resourceIdBox.setPromptText("Select resource");
        resourceIdBox.setPrefWidth(220);

        DatePicker startDate = new DatePicker();
        startDate.setPromptText("Start date");

        ComboBox<String> startTime = new ComboBox<>();
        startTime.setPromptText("Start time");
        startTime.setPrefWidth(220);

        DatePicker endDate = new DatePicker();
        endDate.setPromptText("End date");

        ComboBox<String> endTime = new ComboBox<>();
        endTime.setPromptText("End time");
        endTime.setPrefWidth(220);

        for (int h = 0; h < 24; h++) {
            startTime.getItems().add(String.format("%02d:00", h));
            startTime.getItems().add(String.format("%02d:30", h));
            endTime.getItems().add(String.format("%02d:00", h));
            endTime.getItems().add(String.format("%02d:30", h));
        }

        TextField reasonField = new TextField();
        reasonField.setPromptText("Reason");

        grid.add(new Label("Resource:"), 0, 0);
        grid.add(resourceIdBox, 1, 0);

        grid.add(new Label("Start date:"), 0, 1);
        grid.add(startDate, 1, 1);

        grid.add(new Label("Start time:"), 0, 2);
        grid.add(startTime, 1, 2);

        grid.add(new Label("End date:"), 0, 3);
        grid.add(endDate, 1, 3);

        grid.add(new Label("End time:"), 0, 4);
        grid.add(endTime, 1, 4);

        grid.add(new Label("Reason:"), 0, 5);
        grid.add(reasonField, 1, 5);

        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #E8735A; -fx-text-fill: white; -fx-background-radius: 6;");

        saveBtn.setOnAction(e -> {
            String selected = resourceIdBox.getValue();

            if (selected == null || startDate.getValue() == null || endDate.getValue() == null
                    || startTime.getValue() == null || endTime.getValue() == null) {
                showAlert("Error", "Please fill in all required fields.");
                return;
            }

            LocalDateTime maintenanceStart = LocalDateTime.of(
                    startDate.getValue(),
                    LocalTime.parse(startTime.getValue())
            );

            LocalDateTime maintenanceEnd = LocalDateTime.of(
                    endDate.getValue(),
                    LocalTime.parse(endTime.getValue())
            );

            if (!maintenanceStart.isBefore(maintenanceEnd)) {
                showAlert("Error", "End date/time must be after start date/time.");
                return;
            }

            String[] parts = selected.split(" – ");
            if (parts.length < 2) {
                showAlert("Error", "Could not read resource selection. Please try again.");
                return;
            }
            String resourceId = parts[0];

            try {
                AdminManager.addMaintenanceWindow(
                        resourceId,
                        startDate.getValue().toString(),
                        startTime.getValue(),
                        endDate.getValue().toString(),
                        endTime.getValue(),
                        reasonField.getText().trim()
                );

                AdminManager.updateResourceStatusMaintenance(
                        resourceId,
                        "Maintenance",
                        startDate.getValue().toString(),
                        startTime.getValue(),
                        endDate.getValue().toString(),
                        endTime.getValue()
                );
                resourcesTable.refresh();
                loadMaintenanceWindows();
                popup.close();
            } catch (Exception ex) {
                showAlert("Error", "Failed to save: " + ex.getMessage());
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> popup.close());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        grid.add(buttons, 1, 6);

        popup.setScene(new Scene(grid));
        popup.showAndWait();
    }




    // TODO
    @FXML public void handleSaveResource() {
        String resourceID = resIdField.getText();
        if (resourceID == null || resourceID.isEmpty()) {
            showAlert("Error", "Please select a resource to edit.");
            return;
        }
        String name = resNameField.getText();
        String type = (resTypeCombo != null) ? resTypeCombo.getValue() : null;
        String building = resBuildingField.getText();
        String room = resRoomField.getText();
        String activeSelection = resActiveCombo.getValue();
        boolean isActive = "Yes".equals(activeSelection);
        String requiredRole = resRoleCombo.getValue();
        String durationText = resMaxDurationField != null ? resMaxDurationField.getText() : "";
        int duration = 120;
        if (!durationText.isEmpty()) {
            try { duration = Integer.parseInt(durationText); } catch (NumberFormatException ignored) {}
        }

        try{
            AdminManager.updateResource(resourceID,name,type,requiredRole,duration,building,room,isActive);
            loadResources();
            clearResourceForm();
            showAlert("Success", "Resource updated successfully.");
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not update the resource: " + e.getMessage());
        }

    }

    @FXML public void handleDeleteResource() {
        String resourceID = resIdField.getText();
        if (resourceID == null || resourceID.isEmpty()) {
            showAlert("Error", "Please select a resource to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete resource " + resourceID + "? All active bookings for it will be cancelled.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;
        try {
            AdminManager.cancelActiveBookingsByResource(resourceID);
            AdminManager.deleteResource(resourceID);
            loadResources();
            clearResourceForm();
            showAlert("Success", "Resource deleted and active bookings cancelled.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not delete resource: " + e.getMessage());
        }
    }

    @FXML public void handleSaveBooking() {
        String bookingID    = bookIdField.getText();
        String bookDate     = bookDateField.getText();
        String bookStatus   = bookStatusCombo.getValue();
        String bookStartTime = bookStartTimeField.getText().trim();
        String bookEndTime   = bookEndTimeField.getText().trim();

        if (bookingID == null || bookingID.isEmpty()) {
            showAlert("Error", "Please select a booking to edit.");
            return;
        }
        if (bookDate == null || bookDate.isEmpty() || bookStatus == null) {
            showAlert("Error", "Please fill in date and status.");
            return;
        }
        if (!bookStartTime.matches("([01]\\d|2[0-3]):[0-5]\\d") ||
            !bookEndTime.matches("([01]\\d|2[0-3]):[0-5]\\d")) {
            showAlert("Error", "Times must be in HH:mm format (e.g. 09:00).");
            return;
        }
        try {
            LocalDate.parse(bookDate);
        } catch (Exception e) {
            showAlert("Error", "Date must be in YYYY-MM-DD format.");
            return;
        }
        if (!LocalTime.parse(bookStartTime).isBefore(LocalTime.parse(bookEndTime))) {
            showAlert("Error", "End time must be after start time.");
            return;
        }
        if ("pending".equalsIgnoreCase(bookStatus) || "confirmed".equalsIgnoreCase(bookStatus)) {
            try {
                if (BookingsDatabaseManager.hasBookingConflictExcluding(
                        bookingID, bookResIdField.getText(), bookDate, bookStartTime, bookEndTime)) {
                    showAlert("Error", "This resource already has a booking at that time.");
                    return;
                }
            } catch (java.sql.SQLException e) {
                showAlert("Error", "Could not check booking conflict: " + e.getMessage());
                return;
            }
        }
        String updatedDate = LocalDate.now().toString();
        String updatedTime = LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

        try {
            AdminManager.updateBooking(bookingID, bookDate, bookStatus, bookStartTime, bookEndTime, updatedDate, updatedTime);
            loadBookings();
            clearBookingForm();
            showAlert("Success", "Booking updated successfully.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not update booking: " + e.getMessage());
        }
    }
    @FXML public void handleDeleteBooking() {
        String bookingID= bookIdField.getText();
        if (bookingID == null || bookingID.isEmpty()) {
            showAlert("Error", "Please select a booking to delete.");
            return;
        }
        try {
            AdminManager.deleteBooking(bookingID);
            loadBookings();
            clearBookingForm();
            showAlert("Success", "Booking deleted successfully.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not delete booking: " + e.getMessage());
        }
    }


    @FXML public void handleAddResource() {
        String name = resNameField.getText();
        String type = (resTypeCombo != null) ? resTypeCombo.getValue() : null;
        String building = resBuildingField.getText();
        String room = resRoomField.getText();
        String activeSelection = resActiveCombo.getValue();
        boolean isActive = "Yes".equals(activeSelection);
        
        String requiredRole = (resRoleCombo != null && resRoleCombo.getValue() != null) ? resRoleCombo.getValue() : "Student";
        int duration = 120;
        if (resMaxDurationField != null && !resMaxDurationField.getText().isEmpty()) {
            try { duration = Integer.parseInt(resMaxDurationField.getText()); } catch (NumberFormatException ignored) {}
        }

        if (name == null || name.isEmpty() || type == null || type.isEmpty() ||
            building == null || building.isEmpty() || room == null || room.isEmpty() || activeSelection == null) {
            showAlert("Error", "Please fill in all resource fields.");
            return;
        }

        try {
            AdminManager.addResource(name, type, requiredRole, duration, building, room, isActive);
            loadResources();
            clearResourceForm();
            showAlert("Success", "Resource successfully added.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not add resource: " + e.getMessage());
        }
    }



    // ═══════════════════════════ Data Loading ═════════════════════════════════

    /**
     * Loads all users from the database into the Users twhable.
     */
    private void loadUsers() {
        try {
            ArrayList<User> userList = UserDatabaseManager.getAllUsers();
            masterUserList = FXCollections.observableArrayList(userList);
            usersTable.setItems(masterUserList);
            userCountLabel.setText(String.valueOf(userList.size()));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load users");
        }
    }

    /**
     * Loads all resources from the database into the Resources table.
     */
    private void loadResources() {
        try {
            ArrayList<ResourceRow> resourceList = ResourcesDatabaseManager.getAllResources();
            masterResourceList = FXCollections.observableArrayList(resourceList);
            resourcesTable.setItems(masterResourceList);
            resourceCountLabel.setText(String.valueOf(resourceList.size()));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load resources");
        }
    }

    private void loadBookings() {
        try {
            ArrayList<Booking> allBookings = BookingsDatabaseManager.getAllBookings();

            ArrayList<Booking> pendingBookings = new ArrayList<>();
            ArrayList<Booking> historyBookings = new ArrayList<>();

            for (Booking b : allBookings) {
                if ("pending".equalsIgnoreCase(b.getStatus())) {
                    pendingBookings.add(b);
                } else {
                    historyBookings.add(b);
                }
            }

            // Update status labels
            pendingCountLabel.setText(String.valueOf(pendingBookings.size()));
            totalBookingsLabel.setText(String.valueOf(allBookings.size()));

            // Build pending request cards
            buildPendingCards(pendingBookings);

            // Populate history table
            bookingHistoryTable.setItems(FXCollections.observableArrayList(historyBookings));

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load bookings");
        }
    }

    // ═══════════════════════════ Search Filtering ═════════════════════════════

    private void setupUserSearch() {
        if (masterUserList == null) return;

        FilteredList<User> filteredUsers = new FilteredList<>(masterUserList, u -> true);

        userSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredUsers.setPredicate(user -> {
                if (newVal == null || newVal.isBlank()) {
                    return true;
                }

                String filter = newVal.toLowerCase();

                if (user.getName() != null && user.getName().toLowerCase().contains(filter)) return true;
                if (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(filter)) return true;
                if (user.getLastName() != null && user.getLastName().toLowerCase().contains(filter)) return true;
                if (user.getEmail() != null && user.getEmail().toLowerCase().contains(filter)) return true;
                if (user.getRole() != null && user.getRole().toLowerCase().contains(filter)) return true;
                return false;
            });
        });

        usersTable.setItems(filteredUsers);
    }


    private void setupResourceSearch() {
        if (masterResourceList == null) return;

        FilteredList<ResourceRow> filteredResources = new FilteredList<>(masterResourceList, r -> true);

        resourceSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredResources.setPredicate(res -> {
                if (newVal == null || newVal.isBlank()) {
                    return true;
                }
                String filter = newVal.toLowerCase();

                if (res.getName() != null && res.getName().toLowerCase().contains(filter)) return true;
                if (res.getType() != null && res.getType().toLowerCase().contains(filter)) return true;
                if (res.getBuilding() != null && res.getBuilding().toLowerCase().contains(filter)) return true;
                if (res.getRoom() != null && res.getRoom().toLowerCase().contains(filter)) return true;
                return false;
            });
        });

        resourcesTable.setItems(filteredResources);
    }

    // ═══════════════════════════ Pending Cards ════════════════════════════════

    private void buildPendingCards(ArrayList<Booking> pendingBookings) {
        pendingRequestsContainer.getChildren().clear();

        if (pendingBookings.isEmpty()) {
            Label empty = new Label("No pending booking requests at this time.");
            empty.getStyleClass().add("empty-label");
            pendingRequestsContainer.getChildren().add(empty);
            return;
        }

        for (Booking booking : pendingBookings) {
            HBox card = new HBox(15);
            card.getStyleClass().add("pending-card");
            card.setAlignment(Pos.CENTER_LEFT);

            // Info section
            VBox info = new VBox(2);
            
            Label title = new Label(booking.getResourceName());
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2A3F54; -fx-font-size: 14px;");
            
            Label subtitle1 = new Label("ID: " + booking.getBookingID() + " | User: " + booking.getUserID());
            subtitle1.setStyle("-fx-text-fill: #73879C; -fx-font-size: 12px;");
            
            Label subtitle2 = new Label(booking.getDate() + " | " + booking.getStartTime() + " - " + booking.getEndTime());
            subtitle2.setStyle("-fx-text-fill: #73879C; -fx-font-size: 12px;");
            
            info.getChildren().addAll(title, subtitle1, subtitle2);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button acceptBtn = new Button("Accept");
            acceptBtn.getStyleClass().add("accept-btn");
            acceptBtn.setOnAction(e -> handleAcceptBooking(booking));

            Button refuseBtn = new Button("Refuse");
            refuseBtn.getStyleClass().add("refuse-btn");
            refuseBtn.setOnAction(e -> handleRefuseBooking(booking));

            HBox btnBox = new HBox(8, acceptBtn, refuseBtn);
            btnBox.setAlignment(Pos.CENTER_RIGHT);

            card.getChildren().addAll(info, spacer, btnBox);
            pendingRequestsContainer.getChildren().add(card);
        }
    }

    // ═══════════════════════ Booking Action Handlers ═════════════════════════

    /**
     * Handles accepting a pending booking request.
     *
     * @param booking the booking to accept
     */
    private void handleAcceptBooking(Booking booking) {
        System.out.println("Accepted booking: " + booking.getBookingID());
        try {
            AdminManager.updateBookingStatus(booking.getBookingID(), "confirmed");
            loadBookings();
            showAlert("Success", "Booking accepted successfully.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not accept booking: " + e.getMessage());
        }
    }

    /**
     * Handles refusing a pending booking request.
     *
     * @param booking the booking to refuse
     */
    private void handleRefuseBooking(Booking booking) {
        // TODO: update booking status to 'cancelled' in the database
        System.out.println("Refused booking: " + booking.getBookingID());
         try {
            AdminManager.updateBookingStatus(booking.getBookingID(), "cancelled");
            loadBookings();
            showAlert("Success", "Booking refused successfully.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not refuse booking: " + e.getMessage());
        }
    }

    // ═══════════════════════ Maintenance Windows ══════════════════════════════

    private void loadMaintenanceWindows() {
        if (maintenanceWindowsContainer == null) return;
        maintenanceWindowsContainer.getChildren().clear();

        String query = "SELECT r.name, m.startDate, m.startTime, m.endDate, m.endTime, m.reason " +
                "FROM MaintenanceWindow m JOIN Resource r ON m.resourceID = r.resourceID " +
                "WHERE datetime(m.endDate || ' ' || m.endTime) >= datetime('now') " +
                "ORDER BY m.startDate ASC, m.startTime ASC";

        try (java.sql.Connection conn = com.project.coursework2.data.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query);
             java.sql.ResultSet rs = stmt.executeQuery()) {

            boolean found = false;
            while (rs.next()) {
                found = true;

                VBox card = new VBox(3);
                card.getStyleClass().add("maintenance-card");

                Label nameLabel = new Label(rs.getString("name"));
                nameLabel.getStyleClass().add("maintenance-card-title");

                Label timeLabel = new Label(
                        rs.getString("startDate") + "  " + rs.getString("startTime")
                        + "  —  " + rs.getString("endDate") + "  " + rs.getString("endTime"));
                timeLabel.getStyleClass().add("maintenance-card-meta");

                card.getChildren().addAll(nameLabel, timeLabel);

                String reason = rs.getString("reason");
                if (reason != null && !reason.isBlank()) {
                    Label reasonLabel = new Label(reason);
                    reasonLabel.setWrapText(true);
                    reasonLabel.getStyleClass().add("maintenance-card-meta");
                    card.getChildren().add(reasonLabel);
                }

                // Bottom divider between cards
                Region divider = new Region();
                divider.setStyle("-fx-background-color: #E6E9ED;");
                divider.setPrefHeight(1);

                maintenanceWindowsContainer.getChildren().addAll(card, divider);
            }

            if (!found) {
                Label empty = new Label("No upcoming maintenance scheduled");
                empty.setStyle("-fx-font-size: 11px; -fx-text-fill: #73879C; -fx-padding: 10 12;");
                maintenanceWindowsContainer.getChildren().add(empty);
            }
        } catch (Exception e) {
            Label err = new Label("Could not load maintenance");
            err.setStyle("-fx-font-size: 11px; -fx-text-fill: #73879C; -fx-padding: 10 12;");
            maintenanceWindowsContainer.getChildren().add(err);
        }
    }

    // ═══════════════════════════ Helpers ══════════════════════════════════════

    /**
     * Creates a styled Label.
     */
    private Label styledLabel(String text, String style) {
        Label label = new Label(text != null ? text : "N/A");
        label.setStyle(style);
        return label;
    }
}
