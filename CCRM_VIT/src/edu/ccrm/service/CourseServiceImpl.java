package edu.ccrm.service;

import edu.ccrm.config.DataStore;
import edu.ccrm.domain.Course;
import edu.ccrm.domain.Semester;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CourseServiceImpl implements CourseService {

    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public void addCourse(Course course) {
        if (course == null || course.getCode() == null) {
            System.err.println("Cannot add a null course or a course with no code.");
            return;
        }
        dataStore.courses.put(course.getCode(), course);
    }

    @Override
    public Optional<Course> findCourseByCode(String courseCode) {
        return Optional.ofNullable(dataStore.courses.get(courseCode));
    }

    @Override
    public List<Course> getAllCourses() {
        return new ArrayList<>(dataStore.courses.values());
    }

    /**
     * Demonstrates using the Stream API and a lambda expression for filtering.
     */
    @Override
    public List<Course> findCoursesByDepartment(String department) {
        return dataStore.courses.values().stream()
                .filter(course -> department.equalsIgnoreCase(course.getDepartment()))
                .collect(Collectors.toList());
    }

    /**
     * Demonstrates using the Stream API to filter by an Enum.
     */
    @Override
    public List<Course> findCoursesBySemester(Semester semester) {
        return dataStore.courses.values().stream()
                .filter(course -> course.getSemester() == semester)
                .collect(Collectors.toList());
    }
}