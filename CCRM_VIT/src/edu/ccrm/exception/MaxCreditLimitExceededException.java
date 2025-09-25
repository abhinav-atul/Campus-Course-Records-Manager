package edu.ccrm.exception;

// Another checked exception for a business rule.
public class MaxCreditLimitExceededException extends Exception {
	private static final long serialVersionUID = 1L;
    public MaxCreditLimitExceededException(String message) {
        super(message);
    }
}