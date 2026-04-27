package com.project.coursework2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Controller for the Home Dashboard page.
 * Displays live availability, smart suggestions, certifications and maintenance alerts.
 * @author Jasmine Eagles
 * @version 1.0
 */
public class HomeController {

    @FXML private Label activeCount;
    @FXML private Label pendingCount;
    @FXML private Label certsCount;
    @FXML private Label hoursUsed;
    @FXML private ProgressBar activeProgress;
    @FXML private GridPane availabilityGrid;

    @FXML
    public void initialize() {
        buildAvailabilityGrid();
    }

    private void buildAvailabilityGrid() {
        String[] times = {"08","09","10","11","12","13","14","15","16","17"};
        String[][] slots = {
            {"free","booked","booked","free","free","free","yours","yours","free","free"},
            {"free","free","booked","booked","booked","free","booked","free","free","free"},
            {"free","free","free","booked","free","free","free","free","closed","free"},
            {"booked","booked","booked","booked","free","free","free","free","free","free"},
        };

        for (int col = 0; col < times.length; col++) {
            Label timeLabel = new Label(times[col]);
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7A7A7A;");
            availabilityGrid.add(timeLabel, col + 1, 0);
        }

        for (int row = 0; row < slots.length; row++) {
            for (int col = 0; col < slots[row].length; col++) {
                Rectangle rect = new Rectangle(28, 28);
                rect.setArcWidth(5);
                rect.setArcHeight(5);
                switch (slots[row][col]) {
                    case "booked" -> rect.setFill(Color.web("#F5C0B0"));
                    case "yours" -> rect.setFill(Color.web("#B0D4F5"));
                    case "closed" -> rect.setFill(Color.web("#F5E6A0"));
                    default -> rect.setFill(Color.web("#B0E8E2"));
                }
                availabilityGrid.add(rect, col + 1, row + 1);
            }
        }
    }

    @FXML
    private void handleRebook() { System.out.println("Rebook clicked"); }

    @FXML
    private void handleViewCert() { System.out.println("View cert clicked"); }
}
