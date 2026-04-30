package com.project.coursework2.model;

/**
 * Base class for all bookable resources in the system.
 * A resource has a location (building + room), a required role to book it,
 * and a maximum booking duration in minutes.
 * Concrete types are {@link StudyRoom}, {@link Equipment}, and {@link Lab}.
 *
 * @author Group 2
 * @version 1.0
 */
public abstract class Resource {

    private String resourceId;
    private String resourceName;
    private ResourceType resourceType;
    private String requiredRole;
    private int maxBookingDuration;
    private String building;
    private String room;
    private boolean available;

    /**
     * Creates a resource with the minimum required fields.
     * Available is set to true by default.
     *
     * @param resourceId   unique identifier
     * @param resourceName display name
     * @param resourceType type of resource
     */
    public Resource(String resourceId, String resourceName, ResourceType resourceType) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.available = true;
    }

    /**
     * Creates a resource with all fields.
     *
     * @param maxBookingDuration maximum allowed booking length in minutes
     * @param resourceId         unique identifier
     * @param resourceName       display name
     * @param resourceType       type of resource
     * @param requiredRole       minimum role needed to book ("Student", "Staff", "Admin")
     * @param building           building name
     * @param room               room name or number
     * @param available          whether the resource is currently available
     */
    public Resource(int maxBookingDuration, String resourceId, String resourceName,
                    ResourceType resourceType, String requiredRole,
                    String building, String room, boolean available) {
        this.maxBookingDuration = maxBookingDuration;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.requiredRole = requiredRole;
        this.building = building;
        this.room = room;
        this.available = available;
    }

    /** @return the unique resource ID */
    public String getResourceId() { return resourceId; }

    /** @param resourceId the new resource ID */
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    /** @return the display name of the resource */
    public String getResourceName() { return resourceName; }

    /** @param resourceName the new display name */
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    /** @return the type of this resource */
    public ResourceType getResourceType() { return resourceType; }

    /** @param resourceType the new resource type */
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }

    /** @return the minimum role required to book this resource */
    public String getRequiredRole() { return requiredRole; }

    /** @param requiredRole the minimum role required */
    public void setRequiredRole(String requiredRole) { this.requiredRole = requiredRole; }

    /** @return maximum booking duration in minutes */
    public int getMaxBookingDuration() { return maxBookingDuration; }

    /** @param maxBookingDuration maximum booking duration in minutes */
    public void setMaxBookingDuration(int maxBookingDuration) { this.maxBookingDuration = maxBookingDuration; }

    /** @return the building this resource is located in */
    public String getBuilding() { return building; }

    /** @param building the building name */
    public void setBuilding(String building) { this.building = building; }

    /** @return the room identifier */
    public String getRoom() { return room; }

    /** @param room the room name or number */
    public void setRoom(String room) { this.room = room; }

    /** @return true if this resource is currently available for booking */
    public boolean isAvailable() { return available; }

    /** @param available true to mark as available, false to mark as unavailable */
    public void setAvailable(boolean available) { this.available = available; }

    /**
     * Returns the resource name and type as a readable string.
     *
     * @return e.g. "Study Room A (StudyRoom)"
     */
    @Override
    public String toString() {
        return resourceName + " (" + resourceType + ")";
    }
}
