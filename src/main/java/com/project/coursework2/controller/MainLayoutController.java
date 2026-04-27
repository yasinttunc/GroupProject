package com.project.coursework2.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * Controller for the main application layout.
 * Initialises the sidebar and loads the home page on startup.
 * @author Jasmine Eagles
 * @version 1.0
 */
public class MainLayoutController {

    /** The central content area where pages are loaded. */
    @FXML
    private StackPane contentArea;

    /**
     * Initialises the layout by connecting the sidebar to the content area
     * and loading the home page.
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