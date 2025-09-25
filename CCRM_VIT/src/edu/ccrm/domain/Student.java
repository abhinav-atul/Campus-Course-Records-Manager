package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person {
    private String regNo;
    private boolean isActive;
    private List<Enrollment> enrolledCourses;

    // Constructor updated: id parameter removed
    public Student(String fullName, String email, LocalDate dateOfBirth, String regNo) {
        super(fullName, email, dateOfBirth); // super() call updated
        this.regNo = regNo;
        this.isActive = true;
        this.enrolledCourses = new ArrayList<>();
    }

    @Override
    public String getProfileDetails() {
        return String.format("Student: %s (Reg No: %s)", getFullName(), regNo);
    }

    // --- Methods to manage enrollments ---
    public void enrollCourse(Enrollment e) {
        this.enrolledCourses.add(e);
    }

    public void unenrollCourse(Enrollment e) {
        this.enrolledCourses.remove(e);
    }

    // --- Getters and Setters ---
    public List<Enrollment> getEnrolledCourses() {
        return List.copyOf(enrolledCourses);
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return getProfileDetails();
    }
}