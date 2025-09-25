package edu.ccrm.cli;

import edu.ccrm.domain.*;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.io.BackupService;
import edu.ccrm.io.ImportExportService;
import edu.ccrm.service.*;
import edu.ccrm.util.Validator;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    // Instantiate all services, including the new InstructorService
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentService studentService = new StudentServiceImpl();
    private static final CourseService courseService = new CourseServiceImpl();
    private static final InstructorService instructorService = new InstructorServiceImpl();
    private static final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private static final ImportExportService ioService = new ImportExportService();
    private static final BackupService backupService = new BackupService();

    public static void main(String[] args) {
        System.out.println("Welcome to the Campus Course & Records Manager!");

        // --- UPDATED IMPORT SECTION ---
        System.out.println("Loading data from files...");
        ioService.importStudents(studentService);
        ioService.importInstructors(instructorService);
        // Corrected importCourses call to include instructorService
        ioService.importCourses(courseService, instructorService);
        ioService.importEnrollments(studentService, courseService, enrollmentService);

        if (studentService.getAllStudents().isEmpty() && courseService.getAllCourses().isEmpty()) {
            System.out.println("No data found. You can add new students and courses.");
        }

        boolean exit = false;
        do {
            printMainMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> handleStudentMenu();
                case "2" -> handleInstructorMenu(); // Added handler
                case "3" -> handleCourseMenu();
                case "4" -> handleEnrollmentMenu();
                case "5" -> handleFileMenu();
                case "9" -> exit = true;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (!exit);

        // --- UPDATED EXPORT SECTION ---
        System.out.println("Saving all data to files...");
        ioService.exportStudents(studentService.getAllStudents());
        ioService.exportInstructors(instructorService.getAllInstructors()); // Added export
        ioService.exportCourses(courseService.getAllCourses());
        ioService.exportEnrollments(studentService.getAllStudents());
        System.out.println("Thank you for using CCRM. Goodbye!");
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Student Management");
        System.out.println("2. Instructor Management"); // Added menu option
        System.out.println("3. Course Management");
        System.out.println("4. Enrollment & Grades");
        System.out.println("5. File Utilities");
        System.out.println("9. Save and Exit");
        System.out.print("Enter your choice: ");
    }

    // --- STUDENT MENU ---
    private static void handleStudentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Student Management --");
            System.out.println("1. Add New Student");
            System.out.println("2. List All Students");
            System.out.println("3. Find Student by Registration Number");
            System.out.println("4. Update Student Details");
            System.out.println("5. Deactivate Student");
            System.out.println("9. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> addStudent();
                case "2" -> listAllStudents();
                case "3" -> findStudent();
                case "4" -> updateStudent();
                case "5" -> deactivateStudent();
                case "9" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // --- INSTRUCTOR MENU (NEW) ---
    private static void handleInstructorMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Instructor Management --");
            System.out.println("1. Add New Instructor");
            System.out.println("2. List All Instructors");
            System.out.println("9. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> addInstructor();
                case "2" -> listAllInstructors();
                case "9" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // --- COURSE MENU (UPDATED) ---
    private static void handleCourseMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Course Management --");
            System.out.println("1. Add New Course");
            System.out.println("2. List All Courses (with Instructors)");
            System.out.println("3. Assign Instructor to Course");
            System.out.println("4. Search Courses by Department");
            System.out.println("9. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> addCourse();
                case "2" -> listAllCourses();
                case "3" -> assignInstructorToCourse();
                case "4" -> searchCourses();
                case "9" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // --- Other handlers remain the same... ---
    private static void handleEnrollmentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Enrollment & Grades --");
            System.out.println("1. Enroll Student in Course");
            System.out.println("2. Unenroll Student from Course");
            System.out.println("3. Assign Grade");
            System.out.println("4. Print Student Transcript");
            System.out.println("9. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> enrollStudentInCourse();
                case "2" -> unenrollStudentFromCourse();
                case "3" -> assignGradeToStudent();
                case "4" -> printStudentTranscript();
                case "9" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleFileMenu() {
        System.out.println("\n-- File Utilities --");
        System.out.println("1. Create Backup of Current Data");
        System.out.println("2. Show Backup Directory Size");
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine();
        switch(choice) {
            case "1" -> backupService.performBackup();
            case "2" -> {
                long size = backupService.calculateDirectorySize(Paths.get("backups"));
                System.out.printf("Total size of backups directory: %.2f KB%n", size / 1024.0);
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    // --- ACTION METHODS ---

    private static void addStudent() {
        try {
            System.out.print("Enter Full Name: "); String name = scanner.nextLine();

            String email;
            do {
                System.out.print("Enter Email: ");
                email = scanner.nextLine();
                if (!Validator.isValidEmail(email)) {
                    System.err.println("❌ Error: Invalid email format.");
                }
            } while (!Validator.isValidEmail(email));

            LocalDate dob = null;
            do {
                System.out.print("Enter Date of Birth (dd-MM-yyyy): ");
                try {
                    dob = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } catch (DateTimeParseException e) {
                    System.err.println("❌ Error: Invalid date format. Please use dd-MM-yyyy.");
                }
            } while (dob == null);

            System.out.print("Enter Registration Number: "); String regNo = scanner.nextLine();

            studentService.addStudent(new Student(name, email, dob, regNo));
            System.out.println("✅ Student '" + name + "' added successfully.");
        } catch (Exception e) {
            System.err.println("❌ An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void addInstructor() {
        try {
            System.out.print("Enter Instructor ID (e.g., I101): "); String id = scanner.nextLine();
            System.out.print("Enter Full Name: "); String name = scanner.nextLine();
            System.out.print("Enter Email: "); String email = scanner.nextLine();
            System.out.print("Enter Date of Birth (dd-MM-yyyy): "); LocalDate dob = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            System.out.print("Enter Employee ID (e.g., E12345): "); String empId = scanner.nextLine();
            System.out.print("Enter Department: "); String dept = scanner.nextLine();

            instructorService.addInstructor(new Instructor(id, name, email, dob, empId, dept));
            System.out.println("✅ Instructor '" + name + "' added successfully.");
        } catch (DateTimeParseException e) {
            System.err.println("❌ Error: Invalid date format. Please use dd-MM-yyyy.");
        } catch (Exception e) {
            System.err.println("❌ An error occurred: " + e.getMessage());
        }
    }

    private static void assignInstructorToCourse() {
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();
        Optional<Course> courseOpt = courseService.findCourseByCode(courseCode);
        if (courseOpt.isEmpty()) {
            System.err.println("❌ Error: Course not found.");
            return;
        }

        System.out.print("Enter Instructor's Employee ID: ");
        String empId = scanner.nextLine();
        Optional<Instructor> instructorOpt = instructorService.findInstructorByEmployeeId(empId);
        if (instructorOpt.isEmpty()) {
            System.err.println("❌ Error: Instructor not found.");
            return;
        }

        Course course = courseOpt.get();
        Instructor instructor = instructorOpt.get();
        course.setInstructor(instructor);

        System.out.println("✅ Successfully assigned Prof. " + instructor.getFullName() + " to " + course.getTitle());
    }

    private static void listAllStudents() {
        System.out.println("\n--- All Students ---");
        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            students.forEach(student -> {
                String status = student.isActive() ? "" : " [DEACTIVATED]";
                System.out.println(student.getProfileDetails() + status);
            });
        }
    }

    private static void listAllInstructors() {
        System.out.println("\n--- All Instructors ---");
        List<Instructor> instructors = instructorService.getAllInstructors();
        if (instructors.isEmpty()) {
            System.out.println("No instructors found.");
        } else {
            instructors.forEach(instructor -> System.out.println(instructor.getProfileDetails()));
        }
    }

    private static void listAllCourses() {
        System.out.println("\n--- All Courses ---");
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            courses.forEach(course -> {
                String instructorName = (course.getInstructor() != null) ? course.getInstructor().getFullName() : "Not Assigned";
                System.out.printf("%s | Instructor: %s%n", course, instructorName);
            });
        }
    }

    private static void findStudent() {
        System.out.print("Enter student registration number to find: ");
        String regNo = scanner.nextLine();
        studentService.findStudentByRegNo(regNo)
            .ifPresentOrElse(student -> {
                System.out.println("\n--- Student Profile ---");
                System.out.println("Full Name: " + student.getFullName());
                System.out.println("Reg No: " + student.getRegNo());
                System.out.println("Email: " + student.getEmail());
                System.out.println("Status: " + (student.isActive() ? "Active" : "Deactivated"));
                System.out.println("-------------------------");

                List<Enrollment> enrollments = student.getEnrolledCourses();
                if (enrollments.isEmpty()) {
                    System.out.println("Not enrolled in any courses.");
                } else {
                    System.out.println("Enrolled Courses:");
                    AtomicInteger totalCredits = new AtomicInteger(0);
                    enrollments.forEach(enrollment -> {
                        Course course = enrollment.getCourse();
                        System.out.printf("  - [%s] %s (%s, %d credits)\n",
                                course.getCode(), course.getTitle(), course.getSemester(), course.getCredits());
                        totalCredits.addAndGet(course.getCredits());
                    });
                    System.out.println("-------------------------");
                    System.out.println("Total Enrolled Courses: " + enrollments.size());
                    System.out.println("Total Credits: " + totalCredits.get());
                }
                System.out.println("--- End of Profile ---");

            }, () -> System.out.println("❌ No student found with registration number: " + regNo));
    }


    private static void updateStudent() {
        System.out.print("Enter Registration Number of student to update: ");
        String regNo = scanner.nextLine();

        Optional<Student> studentOpt = studentService.findStudentByRegNo(regNo);
        if (studentOpt.isEmpty()) {
            System.out.println("❌ Student not found.");
            return;
        }

        Student student = studentOpt.get();
        if (!student.isActive()) {
            System.err.println("❌ Error: Cannot update details for a deactivated student.");
            return;
        }

        System.out.print("Enter new Full Name (or press Enter to keep '" + student.getFullName() + "'): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) {
            student.setFullName(name);
        }

        System.out.print("Enter new Email (or press Enter to keep '" + student.getEmail() + "'): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) {
            if (Validator.isValidEmail(email)) {
                student.setEmail(email);
            } else {
                System.err.println("❌ Invalid email format. Email not updated.");
            }
        }

        studentService.updateStudent(student);
        System.out.println("✅ Student record updated successfully.");
    }

    private static void deactivateStudent() {
        System.out.print("Enter Registration Number of student to deactivate: ");
        String regNo = scanner.nextLine();
        Optional<Student> studentOpt = studentService.findStudentByRegNo(regNo);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setActive(false);
            studentService.updateStudent(student);
            System.out.println("✅ Student " + student.getFullName() + " has been deactivated.");
        } else {
            System.out.println("❌ Student not found.");
        }
    }

    private static void addCourse() {
        try {
            System.out.print("Enter Course Code: ");
            String code = scanner.nextLine();

            System.out.print("Enter Course Title: ");
            String title = scanner.nextLine();

            System.out.print("Enter Credits: ");
            int credits = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Department: ");
            String dept = scanner.nextLine();

            System.out.print("Enter Semester (FALL/INTERIM/WINTER): ");
            Semester semester = Semester.valueOf(scanner.nextLine().toUpperCase());

            Course newCourse = new Course.Builder(code, title)
                    .credits(credits)
                    .department(dept)
                    .semester(semester)
                    .build();

            courseService.addCourse(newCourse);
            System.out.println("✅ Course added successfully: " + title);

        } catch (IllegalArgumentException e) {
             System.err.println("❌ Error: Invalid semester or number format.");
        } catch (Exception e) {
            System.err.println("❌ An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void searchCourses() {
        System.out.print("Enter department to search for: ");
        String dept = scanner.nextLine();
        List<Course> results = courseService.findCoursesByDepartment(dept);
        System.out.println("\n--- Courses in '" + dept + "' ---");
        if (results.isEmpty()) System.out.println("No courses found for this department.");
        else results.forEach(System.out::println);
    }

    private static void enrollStudentInCourse() {
        System.out.print("Enter Student Registration Number: ");
        String regNo = scanner.nextLine();
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        Optional<Student> studentOpt = studentService.findStudentByRegNo(regNo);
        Optional<Course> courseOpt = courseService.findCourseByCode(courseCode);

        if (studentOpt.isEmpty() || courseOpt.isEmpty()) {
            System.err.println("❌ Error: Invalid student registration number or course code.");
            return;
        }

        try {
            enrollmentService.enrollStudent(studentOpt.get(), courseOpt.get());
        } catch (DuplicateEnrollmentException | MaxCreditLimitExceededException e) {
            System.err.println("❌ Enrollment Error: " + e.getMessage());
        }
    }

    private static void unenrollStudentFromCourse() {
        System.out.print("Enter Student Registration Number: ");
        String regNo = scanner.nextLine();
        System.out.print("Enter Course Code to unenroll from: ");
        String courseCode = scanner.nextLine();

        Optional<Student> studentOpt = studentService.findStudentByRegNo(regNo);
        Optional<Course> courseOpt = courseService.findCourseByCode(courseCode);

        if (studentOpt.isPresent() && courseOpt.isPresent()) {
            enrollmentService.unenrollStudent(studentOpt.get(), courseOpt.get());
        } else {
            System.err.println("❌ Error: Invalid student or course code.");
        }
    }

    private static void assignGradeToStudent() {
        System.out.print("Enter Student Registration Number: ");
        String regNo = scanner.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        Grade grade = null;
        do {
            System.out.print("Enter Letter Grade (S, A, B, C, D, E, F): ");
            String gradeStr = scanner.nextLine().toUpperCase();
            try {
                grade = Grade.valueOf(gradeStr);
            } catch (IllegalArgumentException e) {
                 System.err.println("❌ Error: Invalid grade. Please enter one of: S, A, B, C, D, E, F.");
            }
        } while (grade == null);

        try {
            Optional<Student> studentOpt = studentService.findStudentByRegNo(regNo);
            Optional<Course> courseOpt = courseService.findCourseByCode(courseCode);

            if (studentOpt.isPresent() && courseOpt.isPresent()) {
                enrollmentService.assignGrade(studentOpt.get(), courseOpt.get(), grade);
                System.out.println("✅ Grade assigned successfully.");
            } else {
                System.err.println("❌ Error: Invalid student or course specified.");
            }

        } catch (NoSuchElementException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void printStudentTranscript() {
        System.out.print("Enter student registration number for transcript: ");
        String regNo = scanner.nextLine();
        studentService.findStudentByRegNo(regNo)
            .ifPresentOrElse(enrollmentService::generateTranscript,
                () -> System.out.println("❌ No student found with registration number: " + regNo));
    }
}