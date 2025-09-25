package edu.ccrm.io;

import edu.ccrm.domain.*;
import edu.ccrm.service.CourseService;
import edu.ccrm.service.EnrollmentService;
import edu.ccrm.service.InstructorService; // Import the InstructorService
import edu.ccrm.service.StudentService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportExportService {
    private static final Path DATA_DIRECTORY = Paths.get("data");
    private static final DateTimeFormatter CSV_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // --- Student Methods (MODIFIED) ---
    public void exportStudents(List<Student> students) {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            Path filePath = DATA_DIRECTORY.resolve("students.csv");
            List<String> lines = students.stream()
                .map(s -> String.join(",",
                    s.getFullName(),
                    s.getEmail(),
                    s.getDateOfBirth().format(CSV_DATE_FORMATTER),
                    s.getRegNo(),
                    String.valueOf(s.isActive()))) // <-- ADDED: Save active status
                .collect(Collectors.toList());
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to export students: " + e.getMessage());
        }
    }

    public void importStudents(StudentService studentService) {
        Path filePath = DATA_DIRECTORY.resolve("students.csv");
        if (!Files.exists(filePath)) return;

        try (Stream<String> lines = Files.lines(filePath)) {
            lines.map(line -> {
                String[] parts = line.split(",");
                if (parts.length < 4) return null; // Check for at least 4 parts
                try {
                    Student student = new Student(parts[0], parts[1], LocalDate.parse(parts[2], CSV_DATE_FORMATTER), parts[3]);
                    // If status column exists, parse it. Defaults to true if missing (for backward compatibility).
                    if (parts.length > 4) {
                        student.setActive(Boolean.parseBoolean(parts[4])); // <-- ADDED: Load active status
                    }
                    return student;
                } catch (DateTimeParseException e) {
                    System.err.println("Skipping student line due to invalid date format: " + line);
                    return null;
                }
            })
            .filter(java.util.Objects::nonNull)
            .forEach(studentService::addStudent);
        } catch (IOException e) {
            System.err.println("Failed to import students: " + e.getMessage());
        }
    }

    // --- Instructor Methods (NEW) ---
    public void exportInstructors(List<Instructor> instructors) {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            Path filePath = DATA_DIRECTORY.resolve("instructors.csv");
            List<String> lines = instructors.stream()
                .map(i -> String.join(",",
                    i.getId(),
                    i.getFullName(),
                    i.getEmail(),
                    i.getDateOfBirth().format(CSV_DATE_FORMATTER),
                    i.getEmployeeId(),
                    i.getDepartment()))
                .collect(Collectors.toList());
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to export instructors: " + e.getMessage());
        }
    }

    public void importInstructors(InstructorService instructorService) {
        Path filePath = DATA_DIRECTORY.resolve("instructors.csv");
        if (!Files.exists(filePath)) return;

        try (Stream<String> lines = Files.lines(filePath)) {
            lines.map(line -> {
                String[] parts = line.split(",");
                if (parts.length < 6) return null;
                try {
                    return new Instructor(parts[0], parts[1], parts[2], LocalDate.parse(parts[3], CSV_DATE_FORMATTER), parts[4], parts[5]);
                } catch (DateTimeParseException e) {
                    System.err.println("Skipping instructor line due to invalid date format: " + line);
                    return null;
                }
            })
            .filter(java.util.Objects::nonNull)
            .forEach(instructorService::addInstructor);
        } catch (IOException e) {
            System.err.println("Failed to import instructors: " + e.getMessage());
        }
    }

    // --- Course Methods (UPDATED) ---
    public void exportCourses(List<Course> courses) {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            Path filePath = DATA_DIRECTORY.resolve("courses.csv");
            List<String> lines = courses.stream()
                .map(c -> {
                    String instructorId = (c.getInstructor() != null) ? c.getInstructor().getEmployeeId() : "NULL";
                    return String.join(",",
                        c.getCode(),
                        c.getTitle(),
                        String.valueOf(c.getCredits()),
                        c.getDepartment(),
                        c.getSemester().name(),
                        instructorId); // Add instructor ID to the CSV
                })
                .collect(Collectors.toList());
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to export courses: " + e.getMessage());
        }
    }

    public void importCourses(CourseService courseService, InstructorService instructorService) {
        Path filePath = DATA_DIRECTORY.resolve("courses.csv");
        if (!Files.exists(filePath)) return;
        
        try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(line -> {
                String[] parts = line.split(",");
                if (parts.length < 5) return;

                Course course = new Course.Builder(parts[0], parts[1])
                        .credits(Integer.parseInt(parts[2]))
                        .department(parts[3])
                        .semester(Semester.valueOf(parts[4]))
                        .build();
                
                // If instructor ID is present, find and assign the instructor
                if (parts.length > 5 && !"NULL".equalsIgnoreCase(parts[5])) {
                    instructorService.findInstructorByEmployeeId(parts[5])
                        .ifPresent(course::setInstructor);
                }
                
                courseService.addCourse(course);
            });
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Failed to import courses: " + e.getMessage());
        }
    }

    // --- Enrollment Methods ---
    public void exportEnrollments(List<Student> students) {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            Path filePath = DATA_DIRECTORY.resolve("enrollments.csv");
            List<String> lines = students.stream()
                .flatMap(student -> student.getEnrolledCourses().stream()
                    .map(enrollment -> {
                        String grade = (enrollment.getGrade() == null) ? "NULL" : enrollment.getGrade().name();
                        return String.join(",",
                            student.getRegNo(),
                            enrollment.getCourse().getCode(),
                            grade);
                    }))
                .collect(Collectors.toList());
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to export enrollments: " + e.getMessage());
        }
    }
    
    public void importEnrollments(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService) {
        Path filePath = DATA_DIRECTORY.resolve("enrollments.csv");
        if (!Files.exists(filePath)) return;

        try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(line -> {
                String[] parts = line.split(",");
                if (parts.length < 3) return;

                Optional<Student> studentOpt = studentService.findStudentByRegNo(parts[0]);
                Optional<Course> courseOpt = courseService.findCourseByCode(parts[1]);
                
                if (studentOpt.isPresent() && courseOpt.isPresent()) {
                    try {
                        // Enroll silently, ignoring exceptions for duplicates on load
                        try {
                           enrollmentService.enrollStudent(studentOpt.get(), courseOpt.get());
                        } catch (Exception e) {
                           // This is expected if already enrolled
                        }
                        
                        // Assign grade if it exists
                        if (!"NULL".equalsIgnoreCase(parts[2])) {
                            enrollmentService.assignGrade(studentOpt.get(), courseOpt.get(), Grade.valueOf(parts[2]));
                        }
                    } catch (Exception e) {
                        System.err.println("Could not process enrollment line: " + line + " | Reason: " + e.getMessage());
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to import enrollments: " + e.getMessage());
        }
    }
}