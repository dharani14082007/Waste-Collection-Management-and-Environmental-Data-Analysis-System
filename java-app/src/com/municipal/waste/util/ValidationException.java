package com.municipal.waste.util;

/**
 * Custom exception class for business validation errors in the waste management system.
 * Author: DHARANI K K (Register No.: 23)
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
