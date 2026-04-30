package com.project.coursework2.model;

public class StudyRoom extends Resource {
    private int capacity;
    private boolean hasProjector;
    private boolean hasWhiteboard;

    // Constructor
    // It is worth noting hasProjector & hasWhiteboard are not included here,
    // according to the design document.
    public StudyRoom(String resourceId, String resourceName, int capacity) {
        super(resourceId, resourceName, ResourceType.STUDY_ROOM);
        this.capacity = capacity;
    }

    // Getters
    public int getCapacity() {
        return this.capacity;
    }

    // Implemented this according to the design document, but
    // it's worth thinking about if regular getters are better here.
    public String checkAmenities() {
        return "Projector: " + this.hasProjector + ", Whiteboard: " + this.hasWhiteboard;
    }

    // Setters
    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public void setHasWhiteboard(boolean hasWhiteboard) {
        this.hasWhiteboard = hasWhiteboard;
    }
}