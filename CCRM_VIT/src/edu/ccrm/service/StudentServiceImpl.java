package edu.ccrm.service;

import edu.ccrm.config.DataStore;
import edu.ccrm.domain.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentServiceImpl implements StudentService {

    // Get the single instance of our in-memory data store.
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public void addStudent(Student student) {
        if (student == null || student.getRegNo() == null) {
            System.err.println("Cannot add a null student or a student with no registration number.");
            return;
        }
        // Use the registration number as the unique key in our map.
        dataStore.students.put(student.getRegNo(), student);
    }

    @Override
    public Optional<Student> findStudentByRegNo(String regNo) {
        // Optional.ofNullable handles cases where the key might not exist.
        return Optional.ofNullable(dataStore.students.get(regNo));
    }

    @Override
    public List<Student> getAllStudents() {
        // Return a new ArrayList to prevent modification of the original map's values.
        return new ArrayList<>(dataStore.students.values());
    }

    @Override
    public void updateStudent(Student student) {
        if (student == null || student.getRegNo() == null) {
            return;
        }
        // `put` will overwrite the existing entry if the key exists.
        if (dataStore.students.containsKey(student.getRegNo())) {
            dataStore.students.put(student.getRegNo(), student);
        }
    }
}