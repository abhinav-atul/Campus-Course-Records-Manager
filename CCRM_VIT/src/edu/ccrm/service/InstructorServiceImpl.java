package edu.ccrm.service;

import edu.ccrm.config.DataStore;
import edu.ccrm.domain.Instructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InstructorServiceImpl implements InstructorService {

    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public void addInstructor(Instructor instructor) {
        if (instructor != null && instructor.getEmployeeId() != null) {
            dataStore.instructors.put(instructor.getEmployeeId(), instructor);
        }
    }

    @Override
    public Optional<Instructor> findInstructorByEmployeeId(String employeeId) {
        return Optional.ofNullable(dataStore.instructors.get(employeeId));
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return new ArrayList<>(dataStore.instructors.values());
    }
}