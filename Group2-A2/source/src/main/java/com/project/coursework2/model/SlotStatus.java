package com.project.coursework2.model;

/**
 * Status of a single one-hour time slot in the availability grid.
 */
public enum SlotStatus {
    FREE,    // nobody has booked it
    BOOKED,  // someone else booked it
    YOURS,   // the logged-in user booked it
    CLOSED   // under maintenance
}
