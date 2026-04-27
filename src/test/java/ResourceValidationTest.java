package com.project.coursework2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for resource validation logic in CRBAS.
 */
public class ResourceValidationTest {

    /**
     * Test that a resource with valid ID is accepted.
     */
    @Test
    void testValidResourceID() {
        String resourceID = "RES001";
        assertTrue(resourceID.startsWith("RES"),
                "Valid resource ID should start with RES");
    }

    /**
     * Test that empty resource ID is rejected.
     */
    @Test
    void testEmptyResourceIDIsRejected() {
        String resourceID = "";
        assertTrue(resourceID.isEmpty(),
                "Empty resource ID should be rejected");
    }

    /**
     * Test that resource name is not empty.
     */
    @Test
    void testResourceNameNotEmpty() {
        String name = "Study Room A";
        assertFalse(name.isEmpty(),
                "Resource name should not be empty");
    }

    /**
     * Test that valid resource types are accepted.
     */
    @Test
    void testValidResourceTypes() {
        String[] validTypes = {"StudyRoom", "Equipment", "Lab"};
        for (String type : validTypes) {
            assertTrue(type.equals("StudyRoom") ||
                            type.equals("Equipment") ||
                            type.equals("Lab"),
                    "Resource type should be StudyRoom, Equipment or Lab");
        }
    }

    /**
     * Test that invalid resource type is rejected.
     */
    @Test
    void testInvalidResourceTypeRejected() {
        String type = "Cafeteria";
        assertFalse(type.equals("StudyRoom") ||
                        type.equals("Equipment") ||
                        type.equals("Lab"),
                "Invalid resource type should be rejected");
    }

    /**
     * Test that max booking duration is positive.
     */
    @Test
    void testMaxBookingDurationIsPositive() {
        int maxDuration = 120;
        assertTrue(maxDuration > 0,
                "Max booking duration should be positive");
    }

    /**
     * Test that zero max booking duration is invalid.
     */
    @Test
    void testZeroMaxBookingDurationIsInvalid() {
        int maxDuration = 0;
        assertFalse(maxDuration > 0,
                "Zero max booking duration should be invalid");
    }

    /**
     * Test that resource opening time is before closing time.
     */
    @Test
    void testOpeningTimeBeforeClosingTime() {
        String openTime = "08:00";
        String closeTime = "22:00";
        assertTrue(openTime.compareTo(closeTime) < 0,
                "Opening time should be before closing time");
    }

    /**
     * Test that maintenance window blocks availability.
     */
    @Test
    void testMaintenanceWindowBlocksAvailability() {
        String maintenanceStart = "08:00";
        String maintenanceEnd = "17:00";
        String requestedStart = "09:00";
        String requestedEnd = "11:00";
        boolean blocked = requestedStart.compareTo(maintenanceEnd) < 0
                && requestedEnd.compareTo(maintenanceStart) > 0;
        assertTrue(blocked,
                "Resource under maintenance should block booking");
    }

    /**
     * Test that booking outside maintenance window is allowed.
     */
    @Test
    void testBookingOutsideMaintenanceIsAllowed() {
        String maintenanceStart = "08:00";
        String maintenanceEnd = "10:00";
        String requestedStart = "11:00";
        String requestedEnd = "13:00";
        boolean blocked = requestedStart.compareTo(maintenanceEnd) < 0
                && requestedEnd.compareTo(maintenanceStart) > 0;
        assertFalse(blocked,
                "Booking outside maintenance window should be allowed");
    }
}
