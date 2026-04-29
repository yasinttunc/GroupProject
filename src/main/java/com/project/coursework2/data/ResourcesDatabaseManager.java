package com.project.coursework2.data;

import com.project.coursework2.model.Resource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.project.coursework2.data.DatabaseConnection.getConnection;

/**
 * Data-access layer for the {@code Resource} table.
 * Exposes a flat {@link ResourceRow} projection used by the UI rather than the
 * full {@link com.project.coursework2.model.Resource} model hierarchy, which simplifies
 * JavaFX table binding and avoids unnecessary joins.
 *
 * @author CRBAS Team
 * @version 1.0
 */
public class ResourcesDatabaseManager {
    private ArrayList<Resource> resourcesList;

    /**
     * Flat projection of a {@code Resource} row for use in JavaFX TableView bindings.
     * Combines columns from the {@code Resource} table into a single, immutable object.
     */
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
     * Snapshot of a study room's seat capacity and how many seats are booked today.
     */
    public static class CapacityRow {
        public final String name;
        public final int capacity;
        public final int seatsBooked;

        public CapacityRow(String name, int capacity, int seatsBooked) {
            this.name = name;
            this.capacity = capacity;
            this.seatsBooked = Math.min(seatsBooked, capacity);
        }

        /** Fraction of seats booked today, capped at 1.0. */
        public double occupancy() {
            return capacity == 0 ? 0.0 : (double) seatsBooked / capacity;
        }
    }

    /**
     * Returns capacity and today's booking load for every active study room.
     * {@code seatsBooked} is the sum of {@code attendeeCount} across all pending/confirmed
     * bookings for that room today.
     *
     * @return list of {@link CapacityRow}, ordered by resource name
     * @throws SQLException on database access error
     */
    public static List<CapacityRow> getStudyRoomCapacity() throws SQLException {
        List<CapacityRow> rows = new ArrayList<>();
        String query =
            "SELECT r.name, s.capacity, " +
            "COALESCE((SELECT SUM(b.attendeeCount) FROM Booking b " +
            " WHERE b.resourceID = r.resourceID AND b.date = date('now') " +
            " AND LOWER(b.status) IN ('pending','confirmed')), 0) AS seatsBooked " +
            "FROM Resource r JOIN StudyRoom s ON r.resourceID = s.resourceID " +
            "WHERE r.isActive = 1 ORDER BY r.name ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                rows.add(new CapacityRow(
                        rs.getString("name"),
                        rs.getInt("capacity"),
                        rs.getInt("seatsBooked")));
            }
        }
        return rows;
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
