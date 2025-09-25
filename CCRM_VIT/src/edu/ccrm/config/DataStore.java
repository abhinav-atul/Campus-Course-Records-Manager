package edu.ccrm.config;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Instructor;
import edu.ccrm.domain.Student;
import java.util.HashMap;
import java.util.Map;

/**
 * A Singleton class to hold all in-memory application data.
 * This ensures a single source of truth for students, courses, etc.
 */
public class DataStore {
    // 1. The single, private, static instance of the class
    private static DataStore instance;

    // Data maps to act as in-memory tables
    public final Map<String, Student> students = new HashMap<>();
    public final Map<String, Course> courses = new HashMap<>();
    public final Map<String, Instructor> instructors = new HashMap<>();

    // 2. A private constructor to prevent direct instantiation
    private DataStore() {
        // Private constructor to prevent anyone else from creating an instance.
    }

    // 3. A public, static method to get the single instance
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }
}