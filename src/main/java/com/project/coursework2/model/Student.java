package com.project.coursework2.model;

/**
 * Represents a student user in the system.
 * Extends {@link User} with year of study and course name.
 * Students can only book resources that require the "Student" role.
 *
 * @author Group 2
 * @version 1.0
 */
public class Student extends User {

    private int yearOfStudy;
    private String courseName;

    /**
     * Creates a student user.
     *
     * @param id          unique user ID
     * @param firstName   first name
     * @param lastName    last name
     * @param email       email address
     * @param yearOfStudy current year of study (1–7)
     */
    public Student(String id, String firstName, String lastName,
                   String email, int yearOfStudy) {
        super(id, firstName, lastName, email, "Student");
        this.yearOfStudy = yearOfStudy;
    }

    /** @return the current year of study */
    public int getYearOfStudy() { return yearOfStudy; }

    /** @param yearOfStudy the new year of study */
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    /** @return the course name */
    public String getCourseName() { return courseName; }

    /** @param courseName the new course name */
    public void setCourseName(String courseName) { this.courseName = courseName; }
}
