package edu.ccrm.service;

import edu.ccrm.domain.Instructor;
import java.util.List;
import java.util.Optional;

public interface InstructorService {
    void addInstructor(Instructor instructor);
    Optional<Instructor> findInstructorByEmployeeId(String employeeId);
    List<Instructor> getAllInstructors();
}