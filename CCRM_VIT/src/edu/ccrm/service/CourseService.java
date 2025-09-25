package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Semester;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    void addCourse(Course course);
    Optional<Course> findCourseByCode(String courseCode);
    List<Course> getAllCourses();
    List<Course> findCoursesByDepartment(String department);
    List<Course> findCoursesBySemester(Semester semester);
}