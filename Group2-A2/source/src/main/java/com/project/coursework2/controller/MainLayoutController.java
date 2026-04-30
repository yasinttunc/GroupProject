package com.project.coursework2.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * Controller for the main application shell (main-layout.fxml).
 * Wires the sidebar's static {@code mainContentArea} reference to the central
 * {@link StackPane} and loads the home page as the default view on startup.
 *
 * @author Group 2
 * @version 1.0
 */
public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    /**
     * Connects the content area to {@link SidebarController} and displays the home page.
     * Called automatically by JavaFX after FXML injection.
     */
    @FXML
    public void initialize() {
        SidebarController.mainContentArea = contentArea;
        try {
            Node home = FXMLLoader.load(getClass().getResource("/com/project/coursework2/home-view.fxml"));
            contentArea.getChildren().setAll(home);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
