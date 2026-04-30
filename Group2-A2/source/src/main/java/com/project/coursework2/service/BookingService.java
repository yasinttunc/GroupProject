package com.project.coursework2.service;

import com.project.coursework2.data.BookingsDatabaseManager;
import com.project.coursework2.data.ResourcesDatabaseManager;
import com.project.coursework2.model.Booking;
import com.project.coursework2.model.BookingStatus;
import com.project.coursework2.model.Role;
import com.project.coursework2.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Business logic for creating, editing and cancelling bookings.
 * Controllers call this class — they never talk to BookingsDatabaseManager directly.
 */
public class BookingService {

    /**
     * Validates and creates a booking.
     * Checks (in order): date not in past → role permission → quota → time conflict.
     *
     * @return the generated booking ID
     * @throws IllegalArgumentException if any validation check fails (show message to user)
     * @throws SQLException             on database error
     */
    public static String createBooking(User user,
                                       ResourcesDatabaseManager.ResourceRow resource,
                                       String date, String startTime, String endTime)
            throws IllegalArgumentException, SQLException {

        if (LocalDate.parse(date).isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Cannot book a date in the past.");

        if (!resource.isActive())
            throw new IllegalArgumentException("This resource is not available right now.");

        Role userRole     = Role.from(user.getRole());
        Role requiredRole = Role.from(resource.getRequiredRole());
        if (userRole == null || requiredRole == null || userRole.level() < requiredRole.level())
            throw new IllegalArgumentException("You do not have the required role to book this resource.");

        long active = BookingsDatabaseManager.getBookingsByUser(user.getUserID())
                .stream()
                .filter(BookingService::countsAsActiveBooking)
                .count();
        int max = user.getMaxActiveBookings() > 0 ? user.getMaxActiveBookings() : 3;
        if (active >= max)
            throw new IllegalArgumentException(
                    "You have reached your limit of " + max + " active bookings. Cancel one first.");

        if (BookingsDatabaseManager.hasMaintenanceConflict(resource.getResourceId(), date, startTime, endTime))
            throw new IllegalArgumentException("This resource is closed for maintenance at the selected time.");

        if (BookingsDatabaseManager.hasBookingConflict(resource.getResourceId(), date, startTime, endTime))
            throw new IllegalArgumentException("This resource is already booked at the selected time.");

        String bookingId = "BKG" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        BookingsDatabaseManager.addBooking(bookingId, user.getUserID(),
                resource.getResourceId(), date, startTime, endTime);
        return bookingId;
    }

    /**
     * Validates and updates an existing booking owned by the user.
     *
     * @param user the user editing the booking
     * @param bookingId the booking ID to update
     * @param resource the new resource
     * @param date the new date
     * @param startTime the new start time
     * @param endTime the new end time
     * @throws IllegalArgumentException if the edit is not allowed
     * @throws SQLException if the database has a problem
     */
    public static void updateBooking(User user,
                                     String bookingId,
                                     ResourcesDatabaseManager.ResourceRow resource,
                                     String date, String startTime, String endTime)
            throws IllegalArgumentException, SQLException {

        Booking current = BookingsDatabaseManager.getBookingsByUser(user.getUserID())
                .stream()
                .filter(b -> bookingId.equals(b.getBookingID()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("This booking could not be found."));

        BookingStatus status = BookingStatus.from(current.getStatus());
        if (status == null || !status.isActive())
            throw new IllegalArgumentException("Only pending or confirmed bookings can be edited.");

        validateBookingFields(user, resource, date, startTime, endTime);

        if (BookingsDatabaseManager.hasMaintenanceConflict(resource.getResourceId(), date, startTime, endTime))
            throw new IllegalArgumentException("This resource is closed for maintenance at the selected time.");

        if (BookingsDatabaseManager.hasBookingConflictExcluding(bookingId, resource.getResourceId(), date, startTime, endTime))
            throw new IllegalArgumentException("This resource is already booked at the selected time.");

        BookingsDatabaseManager.updateBookingDetails(bookingId, resource.getResourceId(), date, startTime, endTime);
    }

    /**
     * Cancels an existing booking.
     */
    public static void cancelBooking(String bookingId) throws SQLException {
        BookingsDatabaseManager.updateBookingStatus(bookingId, BookingStatus.CANCELLED.toString());
    }

    public static boolean countsAsActiveBooking(Booking booking) {
        if (!BookingStatus.from(booking.getStatus()).isActive()) return false;
        try {
            LocalDate bookingDate = LocalDate.parse(booking.getDate());
            if (bookingDate.isBefore(LocalDate.now())) return false;
            if (bookingDate.isEqual(LocalDate.now())) {
                return LocalTime.parse(booking.getEndTime()).isAfter(LocalTime.now());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void validateBookingFields(User user,
                                             ResourcesDatabaseManager.ResourceRow resource,
                                             String date, String startTime, String endTime) {
        if (resource == null)
            throw new IllegalArgumentException("Please select a resource.");

        LocalDate bookingDate = LocalDate.parse(date);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        if (bookingDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Cannot book a date in the past.");

        if (!end.isAfter(start))
            throw new IllegalArgumentException("End time must be after start time.");

        if (!resource.isActive())
            throw new IllegalArgumentException("This resource is not available right now.");

        long duration = java.time.Duration.between(start, end).toMinutes();
        if (duration > resource.getMaxBookingDuration())
            throw new IllegalArgumentException("This booking is longer than the resource limit.");

        Role userRole = Role.from(user.getRole());
        Role requiredRole = Role.from(resource.getRequiredRole());
        if (userRole == null || requiredRole == null || userRole.level() < requiredRole.level())
            throw new IllegalArgumentException("You do not have the required role to book this resource.");
    }
}
