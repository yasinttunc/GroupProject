package com.project.coursework2.controller;

import com.project.coursework2.data.ResourcesDatabaseManager.ResourceRow;
import com.project.coursework2.model.ResourceType;
import com.project.coursework2.service.ResourceService;

import java.util.List;
import java.util.function.Function;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller for the Resources page (resources-view.fxml).
 * Fetches all resources from the database on load and populates three
 * horizontal card lists — Rooms, Equipment, and Labs — by filtering on
 * resource type. Cards are built programmatically at runtime rather than
 * being hardcoded in FXML.
 *
 * @author CRBAS Team
 * @version 1.0
 */
public class ResourcesController {
    @FXML private HBox roomsContainer;
    @FXML private HBox equipmentContainer;
    @FXML private HBox labsContainer;

    @FXML
    public void initialize() {
        System.out.println("Resources page loaded");

        try {
            List<ResourceRow> studyRooms = ResourceService.getByType(ResourceType.STUDY_ROOM);
            List<ResourceRow> equipment  = ResourceService.getByType(ResourceType.EQUIPMENT);
            List<ResourceRow> labs       = ResourceService.getByType(ResourceType.LAB);
            populate(roomsContainer, studyRooms, this::buildStudyRoomCard);
            populate(equipmentContainer, equipment, this::buildEquipmentCard);
            populate(labsContainer, labs, this::buildLabCard);
        } catch (Exception e) {
            System.out.println("Error loading resources: " + e.getMessage());
        }
    }

    private void populate(HBox container, List<ResourceRow> resources, Function<ResourceRow, VBox> cardBuilder) {
        for (ResourceRow resource : resources) {
            container.getChildren().add(cardBuilder.apply(resource));
        }
    }

    private VBox buildStudyRoomCard(ResourceRow resource) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        card.setPrefWidth(150);

        Label nameLabel = new Label(resource.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3D3D3D;");

        Label locationLabel = new Label(resource.getBuilding() + " " + resource.getRoom());
        locationLabel.setStyle("-fx-text-fill: #7A7A7A; -fx-font-size: 11px;");

        Button bookButton = new Button("Book");
        bookButton.setStyle("-fx-background-color: #4AADA8; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        card.getChildren().addAll(nameLabel, locationLabel, bookButton);
        return card;
    }

    private VBox buildEquipmentCard(ResourceRow resource) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        card.setPrefWidth(150);

        Label nameLabel = new Label(resource.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3D3D3D;");

        Label locationLabel = new Label(resource.getBuilding() + " " + resource.getRoom());
        locationLabel.setStyle("-fx-text-fill: #7A7A7A; -fx-font-size: 11px;");

        Button bookButton = new Button("Book");
        bookButton.setStyle("-fx-background-color: #4AADA8; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        card.getChildren().addAll(nameLabel, locationLabel, bookButton);
        return card;
    }

    private VBox buildLabCard(ResourceRow resource) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        card.setPrefWidth(150);

        Label nameLabel = new Label(resource.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3D3D3D;");

        Label locationLabel = new Label(resource.getBuilding() + " " + resource.getRoom());
        locationLabel.setStyle("-fx-text-fill: #7A7A7A; -fx-font-size: 11px;");

        Button bookButton = new Button("Book");
        bookButton.setStyle("-fx-background-color: #4AADA8; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        card.getChildren().addAll(nameLabel, locationLabel, bookButton);
        return card;
    }

}