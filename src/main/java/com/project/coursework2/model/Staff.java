package com.project.coursework2.model;

public class Staff extends User {

    private String staffID;
    private String jobTitle;
    private String department;

    public Staff (String id, String firstName,String lastName, String email, String jobTitle, String department){
        super(id, firstName,lastName, email, "Staff");
        this.staffID = id;
        this.jobTitle = jobTitle;
        this.department = department;
    }
    //Getter and Setters
    public String getStaffID() {
        return staffID;
    }
    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

}