package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * Controller for the sidebar navigation component.
 * Handles page navigation throughout the application.
 * @author Jasmine Eagles
 * @version 1.0
 */
public class SidebarController {

    /** Shared reference to the main content area for page loading. */
    public static StackPane mainContentArea;

    /**
     * Loads an FXML page into the main content area.
     * @param fxmlFile the name of the FXML file to load
     */
    private void loadPage(String fxmlFile) {
        try {
            if (mainContentArea != null) {
                Node page = FXMLLoader.load(getClass().getResource("/com/project/coursework2/" + fxmlFile));
                mainContentArea.getChildren().setAll(page);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Navigates to the Home Dashboard page. */
    @FXML
    private void goHome() { loadPage("home-view.fxml"); }

    /** Navigates to the Bookings page. */
    @FXML
    private void goBookings() { loadPage("bookings-view.fxml"); }

    /** Navigates to the Resources page. */
    @FXML
    private void goResources() { loadPage("resources-view.fxml"); }

    /** Navigates to the Admin Panel page. */
    @FXML
    private void goAdmin() { loadPage("admin-view.fxml"); }

    /** Navigates to the Account page. */
    @FXML
    private void goAccount() { loadPage("account-view.fxml"); }
}
