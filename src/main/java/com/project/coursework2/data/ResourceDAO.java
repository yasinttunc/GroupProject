/*package com.project.coursework2.data;

import com.project.coursework2.model.Equipment;
import com.project.coursework2.model.Lab;
import com.project.coursework2.model.Resource;
import com.project.coursework2.model.StudyRoom;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                String resourceId = rs.getString("resourceID");
                String name = rs.getString("name");
                String type = rs.getString("type");

                Resource resource = null;

                if ("StudyRoom".equalsIgnoreCase(type)) {
                    resource = new StudyRoom(resourceId, name, 0);
                } else if ("Lab".equalsIgnoreCase(type)) {
                    resource = new Lab(resourceId, name, "General", 1);
                } else if ("Equipment".equalsIgnoreCase(type)) {
                    resource = new Equipment(resourceId, name, "General", 1);
                }

                if (resource != null) {
                    int isActive = rs.getInt("isActive");
                    resource.setAvailable(isActive == 1);
                    resources.add(resource);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resources;
    }
}*/