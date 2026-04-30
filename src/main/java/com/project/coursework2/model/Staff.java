package com.project.coursework2.model;

/**
 * Represents a staff member in the system.
 * Extends {@link User} with job title and department information.
 * Staff members can book resources that require the "Staff" role or lower.
 *
 * @author Group 2
 * @version 1.0
 */
public class Staff extends User {

    private String staffID;
    private String jobTitle;
    private String department;

    /**
     * Creates a staff member with their basic details.
     *
     * @param id         unique user ID
     * @param firstName  first name
     * @param lastName   last name
     * @param email      email address
     * @param jobTitle   job title (e.g. "Lecturer")
     * @param department department name (e.g. "Computer Science")
     */
    public Staff(String id, String firstName, String lastName,
                 String email, String jobTitle, String department) {
        super(id, firstName, lastName, email, "Staff");
        this.staffID = id;
        this.jobTitle = jobTitle;
        this.department = department;
    }

    /** @return the staff ID */
    public String getStaffID() { return staffID; }

    /** @param staffID the new staff ID */
    public void setStaffID(String staffID) { this.staffID = staffID; }

    /** @return the job title */
    public String getJobTitle() { return jobTitle; }

    /** @param jobTitle the new job title */
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    /** @return the department name */
    public String getDepartment() { return department; }

    /** @param department the new department name */
    public void setDepartment(String department) { this.department = department; }
}
