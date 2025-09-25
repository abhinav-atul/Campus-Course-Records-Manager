package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Grade;
import edu.ccrm.domain.Student;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


public class EnrollmentServiceImpl implements EnrollmentService {
    private static final int MAX_CREDITS_PER_SEMESTER = 27;

    @Override
    public void enrollStudent(Student student, Course course) throws DuplicateEnrollmentException, MaxCreditLimitExceededException {
        boolean alreadyEnrolled = student.getEnrolledCourses().stream()
                .anyMatch(enrollment -> enrollment.getCourse().getCode().equals(course.getCode()));
        if (alreadyEnrolled) {
            throw new DuplicateEnrollmentException(student.getFullName() + " is already enrolled in " + course.getTitle());
        }

        int currentCredits = student.getEnrolledCourses().stream()
                .mapToInt(enrollment -> enrollment.getCourse().getCredits())
                .sum();
        if (currentCredits + course.getCredits() > MAX_CREDITS_PER_SEMESTER) {
            throw new MaxCreditLimitExceededException("Enrollment failed. Max credit limit of " + MAX_CREDITS_PER_SEMESTER + " would be exceeded.");
        }

        Enrollment newEnrollment = new Enrollment(student, course);
        student.enrollCourse(newEnrollment); // This method should be on the Student class to add to its internal list
        System.out.println("Successfully enrolled " + student.getFullName() + " in " + course.getTitle());
    }

    @Override
    public void assignGrade(Student student, Course course, Grade grade) {
        student.getEnrolledCourses().stream()
                .filter(enrollment -> enrollment.getCourse().getCode().equals(course.getCode()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Student is not enrolled in this course."))
                .setGrade(grade);
    }

    @Override
    public double calculateGpa(Student student) {
        List<Enrollment> gradedCourses = student.getEnrolledCourses().stream()
                .filter(e -> e.getGrade() != null)
                .toList();

        if (gradedCourses.isEmpty()) return 0.0;

        double totalPoints = gradedCourses.stream()
                .mapToDouble(e -> e.getGrade().getGradePoint() * e.getCourse().getCredits())
                .sum();

        int totalCredits = gradedCourses.stream()
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();

        return (totalCredits == 0) ? 0.0 : totalPoints / totalCredits;
    }
    
    @Override
    public void unenrollStudent(Student student, Course course) {
        Optional<Enrollment> enrollmentOpt = student.getEnrolledCourses().stream()
                .filter(e -> e.getCourse().getCode().equals(course.getCode()))
                .findFirst();

        if (enrollmentOpt.isPresent()) {
            student.unenrollCourse(enrollmentOpt.get()); // Use the method from the Student class
            System.out.println("Successfully unenrolled " + student.getFullName() + " from " + course.getTitle());
        } else {
            System.err.println("Error: Student is not enrolled in that course.");
        }
    }

    @Override
    public void generateTranscript(Student student) {
        System.out.println("\n--- TRANSCRIPT ---");
        System.out.println(student.getProfileDetails());
        System.out.println("--------------------------------------------------");

        if (student.getEnrolledCourses().isEmpty()) {
            System.out.println("No courses enrolled.");
        } else {
            // This demonstrates polymorphism, as each 'enrollment' object's
            // specific toString() method is called automatically.
            for (Enrollment enrollment : student.getEnrolledCourses()) {
                System.out.println(enrollment);
            }
        }
        
        System.out.println("--------------------------------------------------");
        System.out.printf("Cumulative GPA: %.2f\n", calculateGpa(student));
        System.out.println("--- END OF TRANSCRIPT ---\n");
    }
}