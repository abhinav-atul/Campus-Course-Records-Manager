package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Grade;
import edu.ccrm.domain.Student;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;

public interface EnrollmentService {
    void enrollStudent(Student student, Course course) throws DuplicateEnrollmentException, MaxCreditLimitExceededException;
    void assignGrade(Student student, Course course, Grade grade);
    double calculateGpa(Student student);
    void unenrollStudent(Student student, Course course);
    void generateTranscript(Student student);
}