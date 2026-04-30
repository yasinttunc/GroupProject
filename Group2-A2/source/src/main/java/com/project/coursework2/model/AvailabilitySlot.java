package com.project.coursework2.model;

import java.time.LocalDateTime;

/**
 * Represents one one-hour slot in the availability grid.
 * bookingId is null when status is FREE or CLOSED.
 */
public record AvailabilitySlot(LocalDateTime start, LocalDateTime end,
                                SlotStatus status, String bookingId) {}
