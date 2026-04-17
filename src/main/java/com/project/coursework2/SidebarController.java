package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class SidebarController {

    public static StackPane mainContentArea;

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

    @FXML
    private void goHome() { loadPage("home-view.fxml"); }

    @FXML
    private void goBookings() { loadPage("bookings-view.fxml"); }

    @FXML
    private void goResources() { loadPage("resources-view.fxml"); }

    @FXML
    private void goAdmin() { loadPage("admin-view.fxml"); }

    @FXML
    private void goAccount() { loadPage("account-view.fxml"); }
}
