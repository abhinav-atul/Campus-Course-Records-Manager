package edu.ccrm.service;

import edu.ccrm.domain.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    void addStudent(Student student);
    Optional<Student> findStudentByRegNo(String regNo);
    List<Student> getAllStudents();
    void updateStudent(Student student);
}