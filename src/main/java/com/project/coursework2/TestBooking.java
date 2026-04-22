package com.project.coursework2.model;

import com.project.coursework2.data.BookingDAO;

public class TestBooking {
    public static void main(String[] args) {
        Booking booking = new Booking(
                "B002",
                "USR001",
                "PUT_REAL_RESOURCE_ID_HERE",
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