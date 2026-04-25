package com.project.coursework2.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.project.coursework2.model.User;

public class UserDatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:university_booking (2).db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static User getUser(String email, String password) throws SQLException {
        String query = "SELECT userID, name, email, role, maxActiveBookings FROM User WHERE email = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("userID"),
                            rs.getString("name"),
                            "",
                            rs.getString("email"),
                            rs.getString("role")
                    );
                    user.setMaxActiveBookings(rs.getInt("maxActiveBookings"));
                    return user;
                }
            }
        }
        return null;
    }

    public static ArrayList<User> getAllUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        String query = "SELECT userID, name, email, role, maxActiveBookings FROM User";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("userID"),
                        rs.getString("name"),
                        "",
                        rs.getString("email"),
                        rs.getString("role")
                );
                user.setMaxActiveBookings(rs.getInt("maxActiveBookings"));
                users.add(user);
            }
        }

        return users;
    }



    public static void updateUser(String userID, String firstName,
                                  String lastName, String email, String password) throws SQLException {

        String query = "UPDATE User SET name = ?, firstName = ?, lastName = ?, email = ?, password = ? WHERE userID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, (firstName + " "+lastName));
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setString(6, userID);

            stmt.executeUpdate();
            conn.close();
        }

    }
    public static void deleteUser(String userID) throws SQLException {
        String query = "DELETE FROM User WHERE userID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.executeUpdate();
            conn.close();
        }
    }

}