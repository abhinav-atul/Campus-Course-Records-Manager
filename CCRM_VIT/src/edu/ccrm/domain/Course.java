package edu.ccrm.domain;

public class Course {
    private final String code;
    private final String title;
    private final int credits;
    private Instructor instructor;
    private Semester semester;
    private String department; // Added for filtering functionality

    private Course(Builder builder) {
        this.code = builder.code;
        this.title = builder.title;
        this.credits = builder.credits;
        this.instructor = builder.instructor;
        this.semester = builder.semester;
        this.department = builder.department;
    }

    @Override
    public String toString() {
        return String.format("Course: [%s] %s (%d credits)", code, title, credits);
    }

    // --- Getters ---
    public String getCode() {
        return code;
    }

    // MISSING GETTER ADDED
    public String getTitle() {
        return title;
    }

    // MISSING GETTER ADDED
    public int getCredits() {
        return credits;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public Semester getSemester() {
        return semester;
    }
    
    public String getDepartment() {
        return department;
    }
    
    // --- Setters for mutable fields ---
    public void setInstructor(Instructor instructor){
        this.instructor = instructor;
    }

    // --- Static nested Builder class ---
    public static class Builder {
        private String code;
        private String title;
        private int credits;
        private Instructor instructor;
        private Semester semester;
        private String department;

        public Builder(String code, String title) {
            this.code = code;
            this.title = title;
        }

        public Builder credits(int credits) {
            this.credits = credits;
            return this;
        }

        public Builder instructor(Instructor instructor) {
            this.instructor = instructor;
            return this;
        }

        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}