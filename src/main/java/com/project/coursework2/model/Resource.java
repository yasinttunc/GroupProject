package com.project.coursework2.model;

public abstract class Resource {

    private String resourceId;
    private String resourceName;
    private ResourceType resourceType;
    private boolean available;

    // Constructor
    public Resource(String resourceId, String resourceName, ResourceType resourceType) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.available = true;
    }

    // Getters
    public String getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public boolean isAvailable() {
        return available;
    }

    // Setter
    public void setAvailable(boolean available) {
        this.available = available;
    }

    // toString
    @Override
    public String toString() {
        return resourceName + " (" + resourceType + ")";
    }
}