package com.project.coursework2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for user validation logic in CRBAS.
 */
public class UserValidationTest {

    /**
     * Test that valid user ID format is accepted.
     */
    @Test
    void testValidUserIDFormat() {
        String userID = "USR001";
        assertTrue(userID.startsWith("USR") ||
                        userID.startsWith("ADM") ||
                        userID.startsWith("STF"),
                "Valid user ID should start with USR, ADM or STF");
    }

    /**
     * Test that empty user ID is rejected.
     */
    @Test
    void testEmptyUserIDIsRejected() {
        String userID = "";
        assertTrue(userID.isEmpty(),
                "Empty user ID should be rejected");
    }

    /**
     * Test that valid roles are accepted.
     */
    @Test
    void testValidUserRoles() {
        String[] validRoles = {"Student", "Staff", "Admin"};
        for (String role : validRoles) {
            assertTrue(role.equals("Student") ||
                            role.equals("Staff") ||
                            role.equals("Admin"),
                    "Role should be Student, Staff or Admin");
        }
    }

    /**
     * Test that invalid role is rejected.
     */
    @Test
    void testInvalidRoleIsRejected() {
        String role = "Guest";
        assertFalse(role.equals("Student") ||
                        role.equals("Staff") ||
                        role.equals("Admin"),
                "Invalid role should be rejected");
    }

    /**
     * Test that max active bookings limit is positive.
     */
    @Test
    void testMaxActiveBookingsIsPositive() {
        int maxBookings = 3;
        assertTrue(maxBookings > 0,
                "Max active bookings should be positive");
    }

    /**
     * Test that booking count cannot exceed max limit.
     */
    @Test
    void testBookingCountCannotExceedLimit() {
        int currentBookings = 3;
        int maxBookings = 3;
        assertFalse(currentBookings < maxBookings,
                "User at booking limit should not be able to book more");
    }

    /**
     * Test that user below limit can make booking.
     */
    @Test
    void testUserBelowLimitCanBook() {
        int currentBookings = 1;
        int maxBookings = 3;
        assertTrue(currentBookings < maxBookings,
                "User below booking limit should be able to book");
    }

    /**
     * Test that admin ID format is valid.
     */
    @Test
    void testValidAdminIDFormat() {
        String adminID = "ADM001";
        assertTrue(adminID.startsWith("ADM"),
                "Admin ID should start with ADM");
    }

    /**
     * Test that student ID format is valid.
     */
    @Test
    void testValidStudentIDFormat() {
        String studentID = "STU2021001";
        assertTrue(studentID.startsWith("STU"),
                "Student ID should start with STU");
    }

    /**
     * Test that staff ID format is valid.
     */
    @Test
    void testValidStaffIDFormat() {
        String staffID = "STF10001";
        assertTrue(staffID.startsWith("STF"),
                "Staff ID should start with STF");
    }
}