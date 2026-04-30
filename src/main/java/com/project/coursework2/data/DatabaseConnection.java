package com.project.coursework2.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central connection factory for the SQLite database.
 * Every data-access class calls {@link #getConnection()} instead of creating
 * its own connection, so the database URL is defined in one place.
 * The database file path is relative to the JVM working directory,
 * which is the project root when running from IntelliJ or Maven.
 *
 * @author Group 2
 * @version 1.0
 */
public class DatabaseConnection {

    /** JDBC URL pointing to the SQLite database file. */
    private static final String DB_URL = "jdbc:sqlite:university_booking.db";

    private DatabaseConnection() {}

    /**
     * Opens and returns a new SQLite connection with foreign-key enforcement enabled.
     * A 3-second busy timeout is also set so that concurrent writes do not immediately
     * throw an exception — SQLite will retry for up to 3 seconds before failing.
     * The caller is responsible for closing the connection (use try-with-resources).
     *
     * @return a new {@link Connection} ready to use
     * @throws SQLException if the database file cannot be opened or the PRAGMAs fail
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (java.sql.Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA busy_timeout = 3000");
            // Add openingHours column if it doesn't already exist (SQLite ignores duplicate columns at runtime)
            try { st.execute("ALTER TABLE Resource ADD COLUMN openingHours TEXT DEFAULT '08:00 - 18:00'"); }
            catch (java.sql.SQLException ignored) {} // column already exists on subsequent runs
        }
        return conn;
    }
}
