package com.project.coursework2.model;

public class Student extends User {
    private int yearOfStudy;

    public Student(String id, String name, String email, int yearOfStudy) {
        super(id, name, email, "Student");
        this.yearOfStudy = yearOfStudy;
    }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
}
