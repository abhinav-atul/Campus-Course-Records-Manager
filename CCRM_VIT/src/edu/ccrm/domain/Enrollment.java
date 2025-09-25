package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents the enrollment of a Student in a Course.
 * This class links domain models and includes a grade and a timestamp.
 */
public class Enrollment {
    private final Student student;
    private final Course course;
    private Grade grade; // Can be null until graded
    private final LocalDateTime enrollmentDate;

    public Enrollment(Student student, Course course) {
        if (student == null || course == null) {
            throw new IllegalArgumentException("Student and Course cannot be null for an enrollment.");
        }
        this.student = student;
        this.course = course;
        this.enrollmentDate = LocalDateTime.now(); // Uses the modern Date/Time API
        this.grade = null; // Initially no grade is assigned
    }

    // --- Getters and a specific Setter for the grade ---

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public Grade getGrade() {
        return grade;
    }
    
    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    /**
     * Sets the grade for this enrollment. This is the primary mutable field.
     * @param grade The Grade enum value.
     */
    public void setGrade(Grade grade) {
        this.grade = grade;
    }
    
    /**
     * A friendly string representation of the enrollment details.
     * Demonstrates overriding toString().
     */
    @Override
    public String toString() {
        String gradeString = (grade != null) ? grade.toString() : "Not Graded";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return String.format("Course: %-25s | Grade: %-12s | Credits: %d | Enrolled on: %s",
                course.getTitle() + " (" + course.getCode() + ")",
                gradeString,
                course.getCredits(),
                enrollmentDate.format(formatter)
        );
    }
}