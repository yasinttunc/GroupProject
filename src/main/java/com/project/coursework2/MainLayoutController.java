package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

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
