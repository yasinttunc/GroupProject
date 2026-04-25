package com.project.coursework2.model;

public abstract class Resource {

    private String resourceId;
    private String resourceName;
    private ResourceType resourceType;
    private String requiredRole;
    private int maxBookingDuration;
    private String building;
    private String room;
    private boolean available;


    // Constructor
    public Resource(String resourceId, String resourceName, ResourceType resourceType) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.available = true;
    }

    public Resource(int maxBookingDuration, String resourceId, String resourceName, ResourceType resourceType, String requiredRole, String building, String room, boolean available) {
        this.maxBookingDuration = maxBookingDuration;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.requiredRole = requiredRole;
        this.building = building;
        this.room = room;
        this.available = available;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(String requiredRole) {
        this.requiredRole = requiredRole;
    }

    public int getMaxBookingDuration() {
        return maxBookingDuration;
    }

    public void setMaxBookingDuration(int maxBookingDuration) {
        this.maxBookingDuration = maxBookingDuration;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // toString
    @Override
    public String toString() {
        return resourceName + " (" + resourceType + ")";
    }
}