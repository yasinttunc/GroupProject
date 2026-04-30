package com.project.coursework2.model;

/**
 * Represents the three user roles in the system.
 * Using an enum instead of raw strings prevents typos and makes
 * role comparisons consistent everywhere in the codebase.
 */
public enum Role {
    STUDENT, STAFF, ADMIN;

    /** Returns the numeric level used for permission checks (higher = more access). */
    public int level() {
        switch (this) {
            case STUDENT: return 1;
            case STAFF:   return 2;
            case ADMIN:   return 3;
            default:      return 0;
        }
    }

    /**
     * Parses a role string from the database (case-insensitive).
     * Returns null if the value is unrecognised.
     */
    public static Role from(String value) {
        if (value == null) return null;
        switch (value.trim().toLowerCase()) {
            case "student": return STUDENT;
            case "staff":   return STAFF;
            case "admin":   return ADMIN;
            default:        return null;
        }
    }

    /** Returns the capitalised form stored in the database (e.g. "Admin"). */
    @Override
    public String toString() {
        String n = name();
        return n.charAt(0) + n.substring(1).toLowerCase();
    }
}
