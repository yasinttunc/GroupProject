package com.project.coursework2.model;

/**
 * Represents a laboratory resource.
 * Labs have a safety level (1–5) and may require a supervisor to be present.
 *
 * @author Group 2
 * @version 1.0
 */
public class Lab extends Resource {

    private String labType;
    private int safetyLevel;
    private int maxOccupancy;
    private boolean supervisorRequired;

    /**
     * Creates a lab resource.
     *
     * @param resourceId   unique resource ID
     * @param resourceName display name (e.g. "Chemistry Lab")
     * @param labType      category of lab (e.g. "Chemistry", "Computer")
     * @param safetyLevel  safety rating from 1 (low) to 5 (high)
     */
    public Lab(String resourceId, String resourceName, String labType, int safetyLevel) {
        super(resourceId, resourceName, ResourceType.LAB);
        this.labType = labType;
        this.safetyLevel = safetyLevel;
    }

    /** @return the lab category */
    public String getLabType() { return labType; }

    /** @return the safety level from 1 to 5 */
    public int getSafetyLevel() { return safetyLevel; }

    /** @return the maximum number of people allowed at once */
    public int getMaxOccupancy() { return maxOccupancy; }

    /** @return true if a supervisor must be present during use */
    public boolean isSupervisorRequired() { return supervisorRequired; }

    /** @param labType the lab category */
    public void setLabType(String labType) { this.labType = labType; }

    /** @param safetyLevel the safety rating (1–5) */
    public void setSafetyLevel(int safetyLevel) { this.safetyLevel = safetyLevel; }

    /** @param maxOccupancy the maximum number of people allowed */
    public void setMaxOccupancy(int maxOccupancy) { this.maxOccupancy = maxOccupancy; }

    /** @param supervisorRequired true if a supervisor must be present */
    public void supervisorRequired(boolean supervisorRequired) {
        this.supervisorRequired = supervisorRequired;
    }
}
