package com.project.coursework2.model;

public class Lab extends Resource {
    private String labType;
    private int safetyLevel;
    private int maxOccupancy;
    private boolean supervisorRequired;

    // Constructor
    public Lab(String resourceId, String resourceName, String labType, int safetyLevel) {
        super(resourceId, resourceName, ResourceType.LAB);
        this.labType = labType;
        this.safetyLevel = safetyLevel;
    }

    // Getters
    public String getLabType() {
        return this.labType;
    }

    public int getSafetyLevel() {
        return this.safetyLevel;
    }

    public int getMaxOccupancy() {
        return this.maxOccupancy;
    }

    public boolean isSupervisorRequired() {
        return this.supervisorRequired;
    }

    // Setters
    public void setLabType(String labType) {
        this.labType = labType;
    }

    public void setSafetyLevel(int safetyLevel) {
        this.safetyLevel = safetyLevel;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public void supervisorRequired(boolean supervisorRequired) {
        this.supervisorRequired = supervisorRequired;
    }
}
