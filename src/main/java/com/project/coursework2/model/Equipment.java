package com.project.coursework2.model;

public class Equipment extends Resource {
    private int quantity;
    private String equipmentType;
    private String modelNumber;
    private boolean isPortable;

    // Constructor
    public Equipment(String resourceId, String resourceName, String equipmentType, int quantity) {
        super(resourceId, resourceName, ResourceType.AV_EQUIPMENT);
        this.equipmentType = equipmentType;
        this.quantity = quantity;
    }

    // Getters
    public int getQuantity() {
        return this.quantity;
    }

    public String getEquipmentType() {
        return this.equipmentType;
    }

    public String getModelNumber() {
        return this.modelNumber;
    }

    public boolean isPortable() {
        return this.isPortable;
    }

    // Setters
    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public void setPortable(boolean portable) {
        this.isPortable = portable;
    }
}
