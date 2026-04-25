package com.project.coursework2.data;

import com.project.coursework2.model.Resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.project.coursework2.data.UserDatabaseManager.getConnection;

/**
 * Handles database operations for the Resource table.
 */
public class ResourcesDatabaseManager {
    private ArrayList<Resource> resourcesList;

    public static class ResourceRow {
        private final String resourceId;
        private final String name;
        private final String type;
        private final int maxBookingDuration;
        private final String requiredRole;
        private final String building;
        private final String room;
        private final boolean active;


        public ResourceRow(String resourceId, String name, String type,
                           String building, String room, String requiredRole,
                           int maxBookingDuration, boolean active) {
            this.resourceId = resourceId;
            this.name = name;
            this.type = type;
            this.building = building;
            this.room = room;
            this.requiredRole = requiredRole;
            this.maxBookingDuration = maxBookingDuration;
            this.active = active;
        }

        public String getResourceId() { return resourceId; }
        public String getName() { return name; }
        public String getType() { return type; }
        public String getBuilding() { return building; }
        public String getRoom() { return room; }
        public String getRequiredRole() { return requiredRole; }
        public int getMaxBookingDuration() { return maxBookingDuration; }
        public boolean isActive() { return active; }
        public String getActiveText() { return active ? "Yes" : "No"; }
    }

    /**
     * Gathers all resources from the database.
     *
     * @return list of ResourceRow objects for table display
     * @throws SQLException if a database access error occurs
     */
    public static ArrayList<ResourceRow> getAllResources() throws SQLException {
        ArrayList<ResourceRow> resources = new ArrayList<>();

        String query = "SELECT resourceID, name, type, building, room, " +
                       "requiredRole, maxBookingDuration, isActive FROM Resource";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ResourceRow row = new ResourceRow(
                        rs.getString("resourceID"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("building"),
                        rs.getString("room"),
                        rs.getString("requiredRole"),
                        rs.getInt("maxBookingDuration"),
                        rs.getInt("isActive") == 1
                );
                resources.add(row);
            }
        }

        return resources;
    }
}
