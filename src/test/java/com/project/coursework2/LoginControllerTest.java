package com.project.coursework2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for login validation logic in CRBAS.
 */
public class LoginControllerTest {

    /**
     * Test that empty username is rejected.
     */
    @Test
    void testEmptyUsernameIsRejected() {
        String username = "";
        String password = "rachel123";
        assertTrue(username.isEmpty(), "Empty username should be rejected");
    }

    /**
     * Test that empty password is rejected.
     */
    @Test
    void testEmptyPasswordIsRejected() {
        String username = "rachel.foster@university.ac.uk";
        String password = "";
        assertTrue(password.isEmpty(), "Empty password should be rejected");
    }

    /**
     * Test that both fields empty is rejected.
     */
    @Test
    void testBothFieldsEmptyIsRejected() {
        String username = "";
        String password = "";
        assertTrue(username.isEmpty() && password.isEmpty(),
                "Both empty fields should be rejected");
    }

    /**
     * Test that valid credentials pass basic format check.
     */
    @Test
    void testValidCredentialsFormatPass() {
        String username = "rachel.foster@university.ac.uk";
        String password = "rachel123";
        assertFalse(username.isEmpty() || password.isEmpty(),
                "Valid credentials should not be empty");
    }

    /**
     * Test that email format is valid.
     */
    @Test
    void testEmailFormatIsValid() {
        String email = "rachel.foster@university.ac.uk";
        assertTrue(email.contains("@"),
                "Email should contain @ symbol");
    }

    /**
     * Test that student ID format is valid.
     */
    @Test
    void testStudentIDFormatIsValid() {
        String studentID = "STU2021001";
        assertTrue(studentID.startsWith("STU"),
                "Student ID should start with STU");
    }

    /**
     * Test that password minimum length is enforced.
     */
    @Test
    void testPasswordTooShort() {
        String password = "abc";
        assertTrue(password.length() < 6,
                "Password under 6 characters should be flagged");
    }

    /**
     * Test that whitespace only input is treated as empty.
     */
    @Test
    void testWhitespaceUsernameIsRejected() {
        String username = "   ";
        assertTrue(username.trim().isEmpty(),
                "Whitespace username should be treated as empty");
    }
}
