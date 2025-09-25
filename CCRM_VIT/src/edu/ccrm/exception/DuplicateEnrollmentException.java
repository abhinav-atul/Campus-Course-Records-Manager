package edu.ccrm.exception;

// A checked exception for a specific business rule violation.
public class DuplicateEnrollmentException extends Exception {
	private static final long serialVersionUID = 1L;
    public DuplicateEnrollmentException(String message) {
        super(message);
    }
}