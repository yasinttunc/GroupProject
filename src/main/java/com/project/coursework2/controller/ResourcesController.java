package com.project.coursework2.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ResourcesController {
    // ScrollPanes for each resource type to enable horizontal scrolling when there are many resources.
    @FXML private ScrollPane roomsScrollPane;
    @FXML private ScrollPane equipmentScrollPane;
    @FXML private ScrollPane labsScrollPane;

    // UI containers for each resource type.
    @FXML private HBox roomsContainer;
    @FXML private HBox equipmentContainer;
    @FXML private HBox labsContainer;

    @FXML
    public void initialize() {
        try {
            // Fetch all resources from the database.
            ArrayList<ResourceRow> resources = ResourcesDatabaseManager.getAllResources();

            // Separate resources into lists based on their type.
            List<ResourceRow> studyRooms = filterByType(resources, "StudyRoom");
            List<ResourceRow> equipment = filterByType(resources, "Equipment");
            List<ResourceRow> labs = filterByType(resources, "Lab");

            // Populate the UI containers with resource cards.
            populate(roomsContainer, roomsScrollPane, studyRooms, this::buildStudyRoomCard);
            populate(equipmentContainer, equipmentScrollPane, equipment, this::buildEquipmentCard);
            populate(labsContainer, labsScrollPane, labs, this::buildLabCard);
        } catch (Exception e) {
            System.out.println("Error loading resources: " + e.getMessage());
        }
    }

    // Helper method to populate a container with resource cards.
    private void populate(HBox container, ScrollPane scrollPane, List<ResourceRow> resources, Function<ResourceRow, VBox> cardBuilder) {
        if (resources.isEmpty()) {
            // If there are no resources, display a message.
            VBox empty = new VBox();
            empty.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

            Label label = new Label("No resources available");
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: #3D3D3D;");
            empty.getChildren().add(label);

            container.getChildren().add(empty);

            // Center the message and disable horizontal scrolling since there's only one item.
            container.setAlignment(Pos.CENTER);
            scrollPane.setFitToWidth(true);
            return;
        }

        // Stop the ScrollPane from changing the card sizes.
        scrollPane.setFitToWidth(false);
        for (ResourceRow resource : resources) {
            // Constructs a card for each resource and adds it to the container.
            container.getChildren().add(cardBuilder.apply(resource));
        }
    }

    // Methods to build different types of resource cards.
    // While they are currently similar, they can be easily customized in the future if needed.
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
        enableBookButton(bookButton, resource);

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
        enableBookButton(bookButton, resource);

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
        enableBookButton(bookButton, resource);

        card.getChildren().addAll(nameLabel, locationLabel, bookButton);
        return card;
    }

    private void enableBookButton(Button bookButton, ResourceRow resource) {
        bookButton.setOnAction(e -> {
            SidebarController sidebar = SidebarController.getInstance();
            sidebar.goBookings(resource);
        });
    }

    private List<ResourceRow> filterByType(List<ResourceRow> resources, String type) {
        return resources.stream()
                .filter(r -> type.equalsIgnoreCase(r.getType()))
                .collect(Collectors.toList());
    }
}