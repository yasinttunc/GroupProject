package com.project.coursework2.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

/**
 * Controls the sidebar navigation, including the expandable Admin Panel submenu.
 */
public class SidebarController {

    @FXML private Button adminBtn;
    @FXML private VBox adminSubMenu;
    @FXML private Button adminUsersBtn;
    @FXML private Button adminResourcesBtn;
    @FXML private Button adminBookingsBtn;

    public static StackPane mainContentArea;

    /** Tracks whether the admin submenu is currently expanded. */
    private boolean adminMenuExpanded = false;

    @FXML
    public void initialize() {
        String role = SessionManager.getUserRole();
        boolean isAdmin = "Admin".equals(role);
        adminBtn.setVisible(isAdmin);
        adminBtn.setManaged(isAdmin);
        // Submenu starts hidden
        adminSubMenu.setVisible(false);
        adminSubMenu.setManaged(false);
    }

    // ───────────────── Page Loading ─────────────────

    private void loadPage(String fxmlFile) {
        try {
            if (mainContentArea != null) {
                Node page = FXMLLoader.load(getClass().getResource(
                        "/com/project/coursework2/" + fxmlFile));
                mainContentArea.getChildren().setAll(page);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ───────────────── Main Navigation ─────────────────

    @FXML
    private void goHome() {
        collapseAdminMenu();
        loadPage("home-view.fxml");
    }

    @FXML
    private void goBookings() {
        collapseAdminMenu();
        loadPage("bookings-view.fxml");
    }

    @FXML
    private void goResources() {
        collapseAdminMenu();
        loadPage("resources-view.fxml");
    }

    @FXML
    private void goAccount() {
        collapseAdminMenu();
        loadPage("account-view.fxml");
    }

    // ───────────────── Admin Submenu ─────────────────

    /**
     * Toggles the admin submenu open/closed.
     */
    @FXML
    private void toggleAdminMenu() {
        adminMenuExpanded = !adminMenuExpanded;
        adminSubMenu.setVisible(adminMenuExpanded);
        adminSubMenu.setManaged(adminMenuExpanded);

        if (adminMenuExpanded) {
            // Default to Users tab when opening
            goAdminUsers();
        }
    }

    /**
     * Collapses the admin submenu (used when navigating away).
     */
    private void collapseAdminMenu() {
        adminMenuExpanded = false;
        adminSubMenu.setVisible(false);
        adminSubMenu.setManaged(false);
        clearAdminSubActive();
    }

    /**
     * Loads the admin view with the Users tab active.
     */
    @FXML
    private void goAdminUsers() {
        setAdminSubActive(adminUsersBtn);
        SessionManager.setAdminActiveTab("users");
        loadPage("admin-view.fxml");
    }

    /**
     * Loads the admin view with the Resources tab active.
     */
    @FXML
    private void goAdminResources() {
        setAdminSubActive(adminResourcesBtn);
        SessionManager.setAdminActiveTab("resources");
        loadPage("admin-view.fxml");
    }

    /**
     * Loads the admin view with the Bookings tab active.
     */
    @FXML
    private void goAdminBookings() {
        setAdminSubActive(adminBookingsBtn);
        SessionManager.setAdminActiveTab("bookings");
        loadPage("admin-view.fxml");
    }

    // ───────────────── Submenu Styling ─────────────────

    /**
     * Highlights the active submenu button.
     */
    private void setAdminSubActive(Button activeBtn) {
        clearAdminSubActive();
        if (!activeBtn.getStyleClass().contains("sidebar-sub-active")) {
            activeBtn.getStyleClass().add("sidebar-sub-active");
        }
    }

    /**
     * Clears the active style from all submenu buttons.
     */
    private void clearAdminSubActive() {
        adminUsersBtn.getStyleClass().remove("sidebar-sub-active");
        adminResourcesBtn.getStyleClass().remove("sidebar-sub-active");
        adminBookingsBtn.getStyleClass().remove("sidebar-sub-active");
    }
}