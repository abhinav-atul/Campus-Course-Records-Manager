package edu.ccrm.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to encapsulate validation results with success/failure status and error messages
 */
public class ValidationResult {
    private boolean valid;
    private List<String> errorMessages;
    
    public ValidationResult() {
        this.valid = true;
        this.errorMessages = new ArrayList<>();
    }
    
    /**
     * Adds an error message and marks the validation as failed
     * @param message The error message to add
     */
    public void addError(String message) {
        this.valid = false;
        this.errorMessages.add(message);
    }
    
    /**
     * @return true if validation passed, false otherwise
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * @return List of all error messages
     */
    public List<String> getErrorMessages() {
        return new ArrayList<>(errorMessages);
    }
    
    /**
     * @return All error messages as a single formatted string
     */
    public String getErrorMessagesAsString() {
        if (errorMessages.isEmpty()) {
            return "";
        }
        return String.join("; ", errorMessages);
    }
    
    /**
     * @return First error message or empty string if no errors
     */
    public String getFirstErrorMessage() {
        return errorMessages.isEmpty() ? "" : errorMessages.get(0);
    }
}