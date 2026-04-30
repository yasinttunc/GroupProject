CRBAS - Campus Resource Booking and Availability System

This is a JavaFX application for booking campus resources.
Students and staff can request bookings for study rooms, equipment and labs.
Admins can manage users, resources, bookings and maintenance windows.

Main features:
- Login with email, user ID, student ID or staff ID.
- View live study room availability on the Home page.
- Create and cancel bookings.
- Book rooms, equipment and labs.
- Detect booking conflicts before saving.
- Block bookings during maintenance windows.
- Admins can update users, resources, booking status and maintenance windows.
- Data is stored in the SQLite database university_booking.db.

How to compile:
1. Open a terminal in the source folder.
2. Run:

   ./mvnw -q -DskipTests compile

How to run:
1. Open a terminal in the source folder.
2. Run:

   ./mvnw javafx:run

Alternative run class:

   com.project.coursework2.Main

Database:
The main database file is:

   university_booking.db

Keep this file in the same folder as pom.xml when running the project.

Demo notes:
For the video, show:
- creating a booking,
- cancelling a booking,
- live availability updating,
- conflict detection,
- resource booking from the Resources page,
- admin user/resource/booking actions,
- adding a maintenance window.
