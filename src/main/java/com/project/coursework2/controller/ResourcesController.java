package com.project.coursework2.controller;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.project.coursework2.data.ResourcesDatabaseManager;
import com.project.coursework2.data.ResourcesDatabaseManager.ResourceRow;
import com.project.coursework2.model.ResourceType;
import com.project.coursework2.service.ResourceService;

import java.util.List;
import java.util.function.Function;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controller for the Resources page (resources-view.fxml).
 * Fetches all resources from the database on load and populates three
 * horizontal card lists — Rooms, Equipment, and Labs — by filtering on
 * resource type. Cards are built programmatically at runtime rather than
 * being hardcoded in FXML.
 *
 * @author Group 2
 * @version 1.0
 */
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
            System.out.println("Resources page loaded");
        }catch (SQLException e){
            System.out.println("Error loading resources: " + e.getMessage());
        }

    }

    // Helper method to populate a container with resource cards.
    private void populate(HBox container, ScrollPane scrollPane, List<ResourceRow> resources, Function<ResourceRow, VBox> cardBuilder) {
        if (resources.isEmpty()) {
            // If there are no resources, display a message.
            VBox empty = new VBox();
            empty.setStyle("-fx-background-color: white; -fx-padding: 18; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #DDE3EE; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(36,48,68,0.07), 7, 0, 0, 2);");
            empty.setPrefWidth(210);

            Label label = new Label("No resources available");
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: #243044;");
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
        return buildCard(resource);
    }

    private VBox buildEquipmentCard(ResourceRow resource) {
        return buildCard(resource);
    }

    private VBox buildLabCard(ResourceRow resource) {
        return buildCard(resource);
    }

    /**
     * Builds a resource card showing the name, building/room location,
     * opening hours, and a Book button. All three resource types (study room,
     * equipment, lab) use the same card layout.
     *
     * @param resource the resource to display
     * @return a styled {@link VBox} card ready to add to a scroll container
     */
    private VBox buildCard(ResourceRow resource) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 16; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #DDE3EE; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(36,48,68,0.07), 7, 0, 0, 2);");
        card.setPrefWidth(210);
        card.setMinHeight(142);

        Label nameLabel = new Label(resource.getName());
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #243044; -fx-font-size: 13px;");

        Label locationLabel = new Label(resource.getBuilding() + " " + resource.getRoom());
        locationLabel.setStyle("-fx-text-fill: #687385; -fx-font-size: 11px;");

        String hours = resource.getOpeningHours();
        Label hoursLabel = new Label("Hours: " + (hours != null && !hours.isBlank() ? hours : "Not set"));
        hoursLabel.setStyle("-fx-text-fill: #4F7CFF; -fx-font-size: 11px;");

        Label statusLabel = new Label(resource.isActive() ? "Available" : "Unavailable");
        statusLabel.setStyle(resource.isActive()
                ? "-fx-background-color: #EAF7EF; -fx-text-fill: #3F8F57; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 10;"
                : "-fx-background-color: #F7EDEE; -fx-text-fill: #B45454; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 10;");

        Button bookButton = new Button("Book");
        bookButton.setStyle(resource.isActive()
                ? "-fx-background-color: #4F7CFF; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 5 13;"
                : "-fx-background-color: #DDE3EE; -fx-text-fill: #687385; -fx-background-radius: 8; -fx-padding: 5 13;");
        bookButton.setDisable(!resource.isActive());
        if (resource.isActive()) {
            enableBookButton(bookButton, resource);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox footer = new HBox(8, statusLabel, spacer, bookButton);
        footer.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(nameLabel, locationLabel, hoursLabel, footer);
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
