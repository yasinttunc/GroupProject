package com.project.coursework2.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class opens the database connection.
 * Other database classes use this class when they need to talk with SQLite.
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:university_booking.db";

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (java.sql.Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA busy_timeout = 3000");
        }
        return conn;
    }
}
