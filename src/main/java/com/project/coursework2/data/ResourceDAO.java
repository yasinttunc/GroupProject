package com.project.coursework2.data;

import com.project.coursework2.model.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public List<Resource> getAllResources() {

        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT * FROM Resource";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Resource r = new Resource();

                r.setResourceID(rs.getString("resourceID"));
                r.setName(rs.getString("name"));
                r.setLocation(rs.getString("location"));
                r.setType(rs.getString("type"));
                r.setRequiredRole(rs.getString("requiredRole"));
                r.setMaxBookingDuration(rs.getInt("maxBookingDuration"));
                r.setActive(rs.getInt("isActive") == 1);
                r.setCreatedAt(rs.getString("createdAt"));

                resources.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resources;
    }
}