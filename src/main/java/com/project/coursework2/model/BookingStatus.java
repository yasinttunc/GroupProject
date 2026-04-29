package com.project.coursework2.model;

/**
 * Represents the lifecycle states of a booking.
 * Centralises the status strings so controllers and queries
 * don't scatter raw literals like "pending" / "cancelled" everywhere.
 */
public enum BookingStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED;

    /** Returns the lowercase string stored in the database (e.g. "pending"). */
    @Override
    public String toString() {
        return name().toLowerCase();
    }

    /** Parses a status string from the database (case-insensitive). Defaults to PENDING. */
    public static BookingStatus from(String value) {
        if (value == null) return PENDING;
        switch (value.trim().toLowerCase()) {
            case "confirmed":  return CONFIRMED;
            case "cancelled":  return CANCELLED;
            case "completed":  return COMPLETED;
            default:           return PENDING;
        }
    }

    /** Returns true if this status counts toward the user's active booking quota. */
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED;
    }
}
