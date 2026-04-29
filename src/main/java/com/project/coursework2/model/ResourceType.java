package com.project.coursework2.model;

/**
 * Represents the three resource categories in the system.
 * The database stores these as "StudyRoom", "Equipment", "Lab" —
 * toDbString() returns that exact form.
 */
public enum ResourceType {
    STUDY_ROOM("StudyRoom"),
    EQUIPMENT("Equipment"),
    LAB("Lab");

    private final String dbValue;

    ResourceType(String dbValue) {
        this.dbValue = dbValue;
    }

    /** Returns the string stored in the database (e.g. "StudyRoom"). */
    public String toDbString() {
        return dbValue;
    }

    /** Parses a resource type string from the database (case-insensitive). Returns null if unknown. */
    public static ResourceType from(String value) {
        if (value == null) return null;
        for (ResourceType t : values()) {
            if (t.dbValue.equalsIgnoreCase(value)) return t;
        }
        return null;
    }
}
