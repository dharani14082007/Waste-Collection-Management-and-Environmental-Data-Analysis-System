package com.municipal.waste.util;

/**
 * Utility class providing validation methods for user inputs and domain entities.
 * Author: DHARANI K K (Register No.: 23)
 */
public class InputValidator {

    public static void validateArea(String name, String ward, int population) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Area name cannot be empty.");
        }
        if (ward == null || ward.trim().isEmpty()) {
            throw new ValidationException("Ward location cannot be empty.");
        }
        if (population <= 0) {
            throw new ValidationException("Population must be a positive integer greater than zero.");
        }
    }

    public static void validateVehicle(String vehicleNumber, String vehicleType, double capacityKg) throws ValidationException {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new ValidationException("Vehicle registration number cannot be empty.");
        }
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            throw new ValidationException("Vehicle type cannot be empty.");
        }
        if (capacityKg <= 0.0) {
            throw new ValidationException("Vehicle capacity must be greater than 0 kg.");
        }
    }

    public static void validateWasteQuantity(double quantityKg) throws ValidationException {
        if (quantityKg < 0.0) {
            throw new ValidationException("Waste quantity cannot be negative.");
        }
    }
}
