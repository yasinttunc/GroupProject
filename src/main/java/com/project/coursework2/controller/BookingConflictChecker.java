package com.project.coursework2.controller;

import com.project.coursework2.data.BookingsDatabaseManager;
import com.project.coursework2.data.ResourcesDatabaseManager;
import com.project.coursework2.model.Booking;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Utility class for detecting scheduling conflicts among bookings.
 * Loads all bookings from the database and can be extended to implement
 * time-overlap detection logic.
 *
 * <p>Note: conflict checking is currently also performed inline in
 * {@link com.project.coursework2.data.BookingsDatabaseManager#hasBookingConflict}
 * at the database level.</p>
 *
 * @author CRBAS Team
 * @version 1.0
 */
public class BookingConflictChecker {

    private ArrayList<Booking> bookings = new ArrayList<>();

    /**
     * Loads all bookings from the database into the local list for conflict analysis.
     * Logs a message to stdout if the database query fails.
     */
    public void checkConflicts(){
        try {bookings.addAll(BookingsDatabaseManager.getAllBookings());}
        catch (SQLException e) { System.out.println("Could not load resources: " + e.getMessage()); }

    }


}
