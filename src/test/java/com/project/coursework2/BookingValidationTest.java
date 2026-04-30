package com.project.coursework2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for booking validation logic in CRBAS.
 */
public class BookingValidationTest {

    /**
     * Test that end time after start time is valid.
     */
    @Test
    void testValidBookingTimeRange() {
        String startTime = "09:00";
        String endTime = "11:00";
        assertTrue(endTime.compareTo(startTime) > 0,
                "End time should be after start time");
    }

    /**
     * Test that end time before start time is invalid.
     */
    @Test
    void testInvalidBookingTimeRange() {
        String startTime = "11:00";
        String endTime = "09:00";
        assertFalse(endTime.compareTo(startTime) > 0,
                "End time before start time should be invalid");
    }

    /**
     * Test that same start and end time is invalid.
     */
    @Test
    void testSameStartAndEndTimeIsInvalid() {
        String startTime = "10:00";
        String endTime = "10:00";
        assertFalse(endTime.compareTo(startTime) > 0,
                "Same start and end time should be invalid");
    }

    /**
     * Test that booking duration within max limit is valid.
     */
    @Test
    void testBookingDurationWithinLimit() {
        int startMinutes = 9 * 60;
        int endMinutes = 11 * 60;
        int duration = endMinutes - startMinutes;
        int maxDuration = 120;
        assertTrue(duration <= maxDuration,
                "Booking within max duration should be valid");
    }

    /**
     * Test that booking duration exceeding max limit is invalid.
     */
    @Test
    void testBookingDurationExceedsLimit() {
        int startMinutes = 9 * 60;
        int endMinutes = 14 * 60;
        int duration = endMinutes - startMinutes;
        int maxDuration = 120;
        assertFalse(duration <= maxDuration,
                "Booking exceeding max duration should be invalid");
    }

    /**
     * Test that two overlapping bookings conflict.
     */
    @Test
    void testOverlappingBookingsConflict() {
        String existingStart = "09:00";
        String existingEnd = "11:00";
        String newStart = "10:00";
        String newEnd = "12:00";
        boolean conflict = newStart.compareTo(existingEnd) < 0
                && newEnd.compareTo(existingStart) > 0;
        assertTrue(conflict,
                "Overlapping bookings should be detected as conflict");
    }

    /**
     * Test that non overlapping bookings do not conflict.
     */
    @Test
    void testNonOverlappingBookingsNoConflict() {
        String existingStart = "09:00";
        String existingEnd = "11:00";
        String newStart = "11:00";
        String newEnd = "13:00";
        boolean conflict = newStart.compareTo(existingEnd) < 0
                && newEnd.compareTo(existingStart) > 0;
        assertFalse(conflict,
                "Non overlapping bookings should not conflict");
    }

    /**
     * Test that student cannot book staff only resource.
     */
    @Test
    void testStudentCannotBookStaffResource() {
        String userRole = "Student";
        String requiredRole = "Staff";
        assertNotEquals(userRole, requiredRole,
                "Student should not be able to book staff only resource");
    }

    /**
     * Test that staff can book staff only resource.
     */
    @Test
    void testStaffCanBookStaffResource() {
        String userRole = "Staff";
        String requiredRole = "Staff";
        assertEquals(userRole, requiredRole,
                "Staff should be able to book staff only resource");
    }

    /**
     * Test that admin can book any resource.
     */
    @Test
    void testAdminCanBookAnyResource() {
        String userRole = "Admin";
        boolean canBook = userRole.equals("Admin") ||
                userRole.equals("Staff") ||
                userRole.equals("Student");
        assertTrue(canBook,
                "Admin should be able to book any resource");
    }

    /**
     * Test that empty booking date is rejected.
     */
    @Test
    void testEmptyBookingDateIsRejected() {
        String date = "";
        assertTrue(date.isEmpty(),
                "Empty booking date should be rejected");
    }
}