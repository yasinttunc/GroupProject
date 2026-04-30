package com.project.coursework2.service;

import com.project.coursework2.data.ResourcesDatabaseManager;
import com.project.coursework2.data.ResourcesDatabaseManager.CapacityRow;
import com.project.coursework2.data.ResourcesDatabaseManager.ResourceRow;
import com.project.coursework2.model.ResourceType;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for resource queries.
 * Controllers call this class instead of ResourcesDatabaseManager directly.
 */
public class ResourceService {

    /**
     * Returns all active and inactive resources.
     */
    public static List<ResourceRow> getAllResources() throws SQLException {
        return ResourcesDatabaseManager.getAllResources();
    }

    /**
     * Returns resources filtered by type (StudyRoom, Equipment, Lab).
     */
    public static List<ResourceRow> getByType(ResourceType type) throws SQLException {
        return getAllResources().stream()
                .filter(r -> r.isActive() && type == ResourceType.from(r.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Returns today's seat occupancy for every active study room.
     */
    public static List<CapacityRow> getStudyRoomCapacity() throws SQLException {
        return ResourcesDatabaseManager.getStudyRoomCapacity();
    }
}
