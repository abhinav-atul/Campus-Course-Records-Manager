package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Instructor extends Person {
    private String id; // id field moved here from Person
    private String employeeId;
    private String department;
    private List<Course> assignedCourses;

    // Constructor updated: id is now a direct parameter, not from super()
    public Instructor(String id, String fullName, String email, LocalDate dateOfBirth, String employeeId, String department) {
        super(fullName, email, dateOfBirth); // super() call updated
        this.id = id; // Initialize the new id field
        this.employeeId = employeeId;
        this.department = department;
        this.assignedCourses = new ArrayList<>();
    }
    
    @Override
    public String getProfileDetails() {
        return String.format("Instructor: %s (ID: %s, Dept: %s)", getFullName(), this.employeeId, this.department);
    }

    public void assignCourse(Course course) {
        this.assignedCourses.add(course);
    }

    // --- Standard Getters and Setters (New id getter/setter added) ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public List<Course> getAssignedCourses() { return List.copyOf(assignedCourses); }
    
    @Override
    public String toString() {
        return getFullName();
    }
}