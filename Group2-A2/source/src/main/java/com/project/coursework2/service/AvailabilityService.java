package com.project.coursework2.service;

import com.project.coursework2.controller.SessionManager;
import com.project.coursework2.model.AvailabilitySlot;
import com.project.coursework2.model.SlotStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.project.coursework2.data.DatabaseConnection.getConnection;

/**
 * Builds the live availability grid shown on the Home dashboard.
 * Each cell is a one-hour slot: FREE, BOOKED, YOURS, or CLOSED (maintenance).
 */
public class AvailabilityService {

    private static final int START_HOUR = 8;
    private static final int END_HOUR   = 18;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private AvailabilityService() {}

    /**
     * Returns a grid of dayCount rows × (END_HOUR - START_HOUR) columns.
     */
    public static List<List<AvailabilitySlot>> loadGrid(String resourceId,
                                                         LocalDate firstDay,
                                                         int dayCount) {
        List<List<AvailabilitySlot>> grid = new ArrayList<>();
        String currentUser = SessionManager.getUserID();

        try (Connection conn = getConnection()) {
            for (int d = 0; d < dayCount; d++) {
                LocalDate day = firstDay.plusDays(d);
                List<AvailabilitySlot> row = new ArrayList<>();
                for (int h = START_HOUR; h < END_HOUR; h++) {
                    LocalDateTime slotStart = day.atTime(h, 0);
                    LocalDateTime slotEnd   = day.atTime(h + 1, 0);
                    row.add(resolveSlot(conn, resourceId, slotStart, slotEnd, currentUser));
                }
                grid.add(row);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Availability load failed: " + ex.getMessage(), ex);
        }
        return grid;
    }

    private static AvailabilitySlot resolveSlot(Connection conn, String resourceId,
                                                 LocalDateTime slotStart, LocalDateTime slotEnd,
                                                 String currentUser) throws SQLException {
        String startStr  = slotStart.format(FMT);
        String endStr    = slotEnd.format(FMT);
        String dateStr   = slotStart.toLocalDate().toString();
        String startTime = String.format("%02d:00", slotStart.getHour());
        String endTime   = String.format("%02d:00", slotEnd.getHour());

        // 1. Maintenance window check
        //    Table: MaintenanceWindow  Columns: resourceID, startDate, startTime, endDate, endTime
        String mQuery =
            "SELECT 1 FROM MaintenanceWindow " +
            "WHERE resourceID = ? " +
            "AND datetime(startDate || ' ' || startTime) < ? " +
            "AND datetime(endDate   || ' ' || endTime)   > ? " +
            "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(mQuery)) {
            ps.setString(1, resourceId);
            ps.setString(2, endStr);
            ps.setString(3, startStr);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new AvailabilitySlot(slotStart, slotEnd, SlotStatus.CLOSED, null);
            }
        }

        // 2. Booking conflict check
        //    Table: Booking  Columns: resourceID, date, startTime, endTime, status, bookingID, userID
        String cQuery =
            "SELECT bookingID, userID FROM Booking " +
            "WHERE resourceID = ? " +
            "AND LOWER(status) IN ('pending', 'confirmed') " +
            "AND date = ? " +
            "AND startTime < ? " +
            "AND endTime   > ? " +
            "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(cQuery)) {
            ps.setString(1, resourceId);
            ps.setString(2, dateStr);
            ps.setString(3, endTime);
            ps.setString(4, startTime);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String bookingId = rs.getString("bookingID");
                    String owner     = rs.getString("userID");
                    SlotStatus status = (owner != null && owner.equals(currentUser))
                            ? SlotStatus.YOURS : SlotStatus.BOOKED;
                    return new AvailabilitySlot(slotStart, slotEnd, status, bookingId);
                }
            }
        }

        // 3. No conflict — slot is free
        return new AvailabilitySlot(slotStart, slotEnd, SlotStatus.FREE, null);
    }
}
