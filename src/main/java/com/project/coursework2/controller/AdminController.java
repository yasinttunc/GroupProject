package com.project.coursework2.controller;

import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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
                userPasswordField.setText(newSel.getPassword()!= null ? newSel.getPassword() : ""); // Keep blank by default
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

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || role == null || userId.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }
        try {
            AdminManager.updateUser(userId, firstName, lastName, email, password, role);
            loadUsers();
            clearUserForm();
            showAlert("Success", "User updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not update user: " + e.getMessage());
        }
    }
    @FXML public void handleDeleteUser() {
        String userID = userIdField.getText();
        if (userID.isEmpty()) {
            
            try {
                AdminManager.deleteUser(userID);
            }
            catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Could not delete user: " + e.getMessage());
            }
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
// TODO
    @FXML public void handleSaveResource() {}

    @FXML public void handleDeleteResource() {
        String resourceID = resIdField.getText();
        if (resourceID == null || resourceID.isEmpty()) {
            showAlert("Error", "Please select a resource to delete.");
            return;
        }
        try {
            AdminManager.deleteResource(resourceID);
            loadResources();
            clearResourceForm();
            showAlert("Success", "Resource deleted successfully.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not delete resource: " + e.getMessage());
        }
    }

// TODO
    @FXML public void handleSaveBooking() {}
    @FXML public void handleDeleteBooking() {}


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
     * Loads all users from the database into the Users table.
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
            showAlert("Success", "Booking accepted successfully.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not accept booking: " + e.getMessage());
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