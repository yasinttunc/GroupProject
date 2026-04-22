package com.project.coursework2.model;

import com.project.coursework2.data.BookingDAO;

public class TestBooking {
    public static void main(String[] args) {
        Booking booking = new Booking(
                "B001",
                "USR011",
                "R001",
                "Study Room A",
                "09:00",
                "11:00",
                "2026-04-22",
                "pending",
                1,
                "2026-04-22 10:00:00"
        );

        BookingDAO dao = new BookingDAO();

        boolean result = dao.submitBookingWithCheck(booking);

        System.out.println(result ? "Booking submitted" : "Booking denied");
    }
}