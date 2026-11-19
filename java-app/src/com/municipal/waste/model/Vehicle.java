package com.municipal.waste.model;

/**
 * Domain entity representing a municipal waste collection vehicle.
 * Author: DHARANI K K (Register No.: 23)
 */
public class Vehicle {
    private int vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    private double capacityKg;
    private String operationalStatus;
    private String lastMaintenance;

    public Vehicle(int vehicleId, String vehicleNumber, String vehicleType, double capacityKg, String operationalStatus, String lastMaintenance) {
        this.vehicleId = vehicleId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.capacityKg = capacityKg;
        this.operationalStatus = operationalStatus;
        this.lastMaintenance = lastMaintenance;
    }

    public Vehicle(String vehicleNumber, String vehicleType, double capacityKg) {
        this(0, vehicleNumber, vehicleType, capacityKg, "AVAILABLE", "2026-11-01");
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public double getCapacityKg() {
        return capacityKg;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public String getLastMaintenance() {
        return lastMaintenance;
    }

    @Override
    public String toString() {
        return String.format("[ID: %-2d] %-15s | %-22s | Cap: %,8.1f kg | Status: %s", 
                vehicleId, vehicleNumber, vehicleType, capacityKg, operationalStatus);
    }
}
