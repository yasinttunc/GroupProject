package com.project.coursework2.model;

/**
 * Represents a piece of equipment that can be borrowed.
 * Equipment has a stock quantity and may be portable.
 * Examples: laptops, cameras, projectors.
 *
 * @author Group 2
 * @version 1.0
 */
public class Equipment extends Resource {

    private int quantity;
    private String equipmentType;
    private String modelNumber;
    private boolean isPortable;

    /**
     * Creates an equipment resource.
     *
     * @param resourceId    unique resource ID
     * @param resourceName  display name
     * @param equipmentType category of equipment (e.g. "Laptop")
     * @param quantity      number of units in stock
     */
    public Equipment(String resourceId, String resourceName,
                     String equipmentType, int quantity) {
        super(resourceId, resourceName, ResourceType.EQUIPMENT);
        this.equipmentType = equipmentType;
        this.quantity = quantity;
    }

    /** @return the number of units available */
    public int getQuantity() { return quantity; }

    /** @return the equipment category */
    public String getEquipmentType() { return equipmentType; }

    /** @return the model or serial number */
    public String getModelNumber() { return modelNumber; }

    /** @return true if the equipment can be taken off-site */
    public boolean isPortable() { return isPortable; }

    /** @param equipmentType the equipment category */
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    /** @param modelNumber the model or serial number */
    public void setModelNumber(String modelNumber) { this.modelNumber = modelNumber; }

    /** @param portable true if the equipment can be taken off-site */
    public void setPortable(boolean portable) { this.isPortable = portable; }
}
