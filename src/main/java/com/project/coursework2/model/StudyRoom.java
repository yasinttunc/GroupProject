package com.project.coursework2.model;

/**
 * Represents a study room resource.
 * Study rooms have a fixed seat capacity and may have a projector or whiteboard.
 *
 * @author Group 2
 * @version 1.0
 */
public class StudyRoom extends Resource {

    private int capacity;
    private boolean hasProjector;
    private boolean hasWhiteboard;

    /**
     * Creates a study room with a name and seat capacity.
     *
     * @param resourceId   unique resource ID
     * @param resourceName display name (e.g. "Study Room A")
     * @param capacity     maximum number of seats
     */
    public StudyRoom(String resourceId, String resourceName, int capacity) {
        super(resourceId, resourceName, ResourceType.STUDY_ROOM);
        this.capacity = capacity;
    }

    /** @return the maximum number of seats */
    public int getCapacity() { return capacity; }

    /** @return true if the room has a projector */
    public boolean isHasProjector() { return hasProjector; }

    /** @return true if the room has a whiteboard */
    public boolean isHasWhiteboard() { return hasWhiteboard; }

    /** @param hasProjector true if the room has a projector */
    public void setHasProjector(boolean hasProjector) { this.hasProjector = hasProjector; }

    /** @param hasWhiteboard true if the room has a whiteboard */
    public void setHasWhiteboard(boolean hasWhiteboard) { this.hasWhiteboard = hasWhiteboard; }

    /**
     * Returns a short description of the room's amenities.
     *
     * @return e.g. "Projector: true, Whiteboard: false"
     */
    public String checkAmenities() {
        return "Projector: " + hasProjector + ", Whiteboard: " + hasWhiteboard;
    }
}
