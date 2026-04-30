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
 * @author Group 2
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
        private final String openingHours;

        /**
         * Creates a resource row from database column values.
         *
         * @param resourceId         unique resource identifier
         * @param name               display name
         * @param type               resource type ("StudyRoom", "Equipment", or "Lab")
         * @param building           building where the resource is located
         * @param room               room name or number
         * @param requiredRole       minimum role needed to book this resource
         * @param maxBookingDuration maximum booking duration in minutes
         * @param active             whether the resource is available for booking
         * @param openingHours       free-text opening hours shown on resource cards (e.g. "08:00 - 18:00")
         */
        public ResourceRow(String resourceId, String name, String type,
                           String building, String room, String requiredRole,
                           int maxBookingDuration, boolean active, String openingHours) {
            this.resourceId = resourceId;
            this.name = name;
            this.type = type;
            this.building = building;
            this.room = room;
            this.requiredRole = requiredRole;
            this.maxBookingDuration = maxBookingDuration;
            this.active = active;
            this.openingHours = openingHours;
        }

        /** @return the unique resource ID (e.g. "RES001") */
        public String getResourceId() { return resourceId; }
        /** @return the display name of the resource */
        public String getName() { return name; }
        /** @return the resource type string ("StudyRoom", "Equipment", or "Lab") */
        public String getType() { return type; }
        /** @return the building where the resource is located */
        public String getBuilding() { return building; }
        /** @return the room name or number */
        public String getRoom() { return room; }
        /** @return the minimum role required to book this resource */
        public String getRequiredRole() { return requiredRole; }
        /** @return the maximum booking duration in minutes */
        public int getMaxBookingDuration() { return maxBookingDuration; }
        /** @return {@code true} if the resource is currently available for booking */
        public boolean isActive() { return active; }
        /** @return "Yes" if the resource is active, "No" otherwise — used for table display */
        public String getActiveText() { return active ? "Yes" : "No"; }
        /** @return the opening hours string (e.g. "08:00 – 22:00"), may be null */
        public String getOpeningHours() { return openingHours; }
    }

    /**
     * Snapshot of a study room's seat capacity and today's booking load.
     * Used to render the capacity progress bars on the home dashboard.
     */
    public static class CapacityRow {
        /** Display name of the study room. */
        public final String name;
        /** Maximum number of seats in the room. */
        public final int capacity;
        /** Total attendee count across all pending/confirmed bookings today, capped at capacity. */
        public final int seatsBooked;

        /**
         * Creates a capacity snapshot.
         *
         * @param name        display name of the study room
         * @param capacity    maximum number of seats
         * @param seatsBooked total attendees booked today (capped at capacity to avoid going over 100%)
         */
        public CapacityRow(String name, int capacity, int seatsBooked) {
            this.name = name;
            this.capacity = capacity;
            this.seatsBooked = Math.min(seatsBooked, capacity);
        }

        /**
         * Returns the fraction of seats booked today as a value between 0.0 and 1.0.
         * Returns 0.0 if the room has no capacity to avoid division by zero.
         *
         * @return occupancy ratio (0.0 = empty, 1.0 = full)
         */
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
     * Returns every resource in the database, both active and inactive.
     * Results are not filtered — callers should filter by {@link ResourceRow#isActive()}
     * or type themselves, or use {@link com.project.coursework2.service.ResourceService#getByType}.
     *
     * @return list of all {@link ResourceRow} records ordered by insertion order
     * @throws SQLException on database access error
     */
    public static ArrayList<ResourceRow> getAllResources() throws SQLException {
        ArrayList<ResourceRow> resources = new ArrayList<>();

        String query = "SELECT resourceID, name, type, building, room, " +
                       "requiredRole, maxBookingDuration, isActive, openingHours FROM Resource";

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
                        rs.getInt("isActive") == 1,
                        rs.getString("openingHours")
                );
                resources.add(row);
            }
        }
        return resources;
    }
}
