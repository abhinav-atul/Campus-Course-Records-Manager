package edu.ccrm.util;

import edu.ccrm.domain.*;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utility class for validating various data inputs in the CCRM system.
 * All validation methods are static for easy access throughout the application.
 */
public class Validator {

    // Email pattern for basic email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Registration number pattern (e.g., 24BCE10001)
    private static final Pattern REG_NO_PATTERN = Pattern.compile(
        "^\\d{2}[A-Z]{3}\\d{5}$"
    );
    
    // Course code pattern (e.g., CSE0001)
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile(
        "^[A-Z]{3}\\d{4}$"
    );
    
    // Employee ID pattern (e.g., EMP001)
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile(
        "^[A-Z]{3}\\d{3}$"
    );

    /**
     * Validates email format using regex pattern
     * @param email The email to validate
     * @return true if email format is valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates registration number format
     * Expected format: 2 digits + 3 letters + 5 digits (e.g., 24BCE10001)
     * @param regNo The registration number to validate
     * @return true if registration number format is valid
     */
    public static boolean isValidRegistrationNumber(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            return false;
        }
        return REG_NO_PATTERN.matcher(regNo.trim().toUpperCase()).matches();
    }

    /**
     * Validates course code format
     * Expected format: 3 letters + 4 digits (e.g., CSE0001)
     * @param courseCode The course code to validate
     * @return true if course code format is valid
     */
    public static boolean isValidCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return false;
        }
        return COURSE_CODE_PATTERN.matcher(courseCode.trim().toUpperCase()).matches();
    }

    /**
     * Validates employee ID format
     * Expected format: 3 letters + 3 digits (e.g., EMP001)
     * @param employeeId The employee ID to validate
     * @return true if employee ID format is valid
     */
    public static boolean isValidEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return false;
        }
        return EMPLOYEE_ID_PATTERN.matcher(employeeId.trim().toUpperCase()).matches();
    }

    /**
     * Validates date of birth - should not be null and not in the future
     * @param dateOfBirth The date to validate
     * @return true if date is valid
     */
    public static boolean isValidDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }
        return !dateOfBirth.isAfter(LocalDate.now());
    }

    /**
     * Validates course credits - should be positive and within reasonable limits
     * @param credits The credits to validate
     * @return true if credits are valid
     */
    public static boolean isValidCredits(int credits) {
        return credits > 0 && credits <= 10; // Assuming max 10 credits per course
    }

    /**
     * Validates that a string is not null or empty
     * @param value The string to validate
     * @return true if string has content
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates grade string against Grade enum values
     * @param gradeStr The grade string to validate
     * @return true if grade is valid
     */
    public static boolean isValidGrade(String gradeStr) {
        if (gradeStr == null || gradeStr.trim().isEmpty()) {
            return false;
        }
        try {
            Grade.valueOf(gradeStr.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates semester enum value
     * @param semester The semester to validate
     * @return true if semester is not null
     */
    public static boolean isValidSemester(Semester semester) {
        return semester != null;
    }

    /**
     * Validates semester string against Semester enum values
     * @param semesterStr The semester string to validate
     * @return true if semester string is valid
     */
    public static boolean isValidSemesterString(String semesterStr) {
        if (semesterStr == null || semesterStr.trim().isEmpty()) {
            return false;
        }
        try {
            Semester.valueOf(semesterStr.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates enrollment prerequisites - checks for duplicates and credit limits
     * @param student The student to enroll
     * @param course The course to enroll in
     * @param maxCreditsPerSemester Maximum allowed credits per semester
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateEnrollment(Student student, Course course, int maxCreditsPerSemester) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        // Check if student is active
        if (!student.isActive()) {
            throw new IllegalArgumentException("Cannot enroll inactive student");
        }
        
        // Check for duplicate enrollment
        boolean alreadyEnrolled = student.getEnrolledCourses().stream()
            .anyMatch(enrollment -> enrollment.getCourse().getCode().equals(course.getCode()));
        
        if (alreadyEnrolled) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }
        
        // Check credit limit
        int currentCredits = student.getEnrolledCourses().stream()
            .mapToInt(enrollment -> enrollment.getCourse().getCredits())
            .sum();
            
        if (currentCredits + course.getCredits() > maxCreditsPerSemester) {
            throw new IllegalArgumentException("Enrollment would exceed maximum credit limit of " + maxCreditsPerSemester);
        }
    }

    /**
     * Validates student data for creation/update
     * @param fullName The student's full name
     * @param email The student's email
     * @param dateOfBirth The student's date of birth
     * @param regNo The student's registration number
     * @return ValidationResult containing validation status and messages
     */
    public static ValidationResult validateStudentData(String fullName, String email, LocalDate dateOfBirth, String regNo) {
        ValidationResult result = new ValidationResult();
        
        if (!isNotEmpty(fullName)) {
            result.addError("Full name is required");
        }
        
        if (!isValidEmail(email)) {
            result.addError("Invalid email format");
        }
        
        if (!isValidDateOfBirth(dateOfBirth)) {
            result.addError("Invalid date of birth - cannot be in the future");
        }
        
        if (!isValidRegistrationNumber(regNo)) {
            result.addError("Invalid registration number format (expected: Year + Branch Code + Serial Number)");
        }
        
        return result;
    }

    /**
     * Validates course data for creation/update
     * @param code The course code
     * @param title The course title
     * @param credits The course credits
     * @param department The course department
     * @return ValidationResult containing validation status and messages
     */
    public static ValidationResult validateCourseData(String code, String title, int credits, String department) {
        ValidationResult result = new ValidationResult();
        
        if (!isValidCourseCode(code)) {
            result.addError("Invalid course code format (expected: Subject Code + Serial Number)");
        }
        
        if (!isNotEmpty(title)) {
            result.addError("Course title is required");
        }
        
        if (!isValidCredits(credits)) {
            result.addError("Invalid credits - must be between 1 and 20");
        }
        
        if (!isNotEmpty(department)) {
            result.addError("Department is required");
        }
        
        return result;
    }
}