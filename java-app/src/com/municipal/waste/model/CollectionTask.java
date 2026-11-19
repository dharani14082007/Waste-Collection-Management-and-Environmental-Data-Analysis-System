package com.municipal.waste.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity representing an assigned collection schedule for an area and vehicle.
 * Author: DHARANI K K (Register No.: 23)
 */
public class CollectionTask {
    private int taskId;
    private int areaId;
    private int vehicleId;
    private String scheduledDate;
    private CollectionStatus status;
    private List<WasteRecord> wasteRecords;

    // Supplementary display attributes from joins
    private String areaName;
    private String vehicleNumber;
    private double vehicleCapacityKg;

    public CollectionTask(int taskId, int areaId, int vehicleId, String scheduledDate, CollectionStatus status) {
        this.taskId = taskId;
        this.areaId = areaId;
        this.vehicleId = vehicleId;
        this.scheduledDate = scheduledDate;
        this.status = status;
        this.wasteRecords = new ArrayList<>();
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getAreaId() {
        return areaId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public CollectionStatus getStatus() {
        return status;
    }

    public void setStatus(CollectionStatus status) {
        this.status = status;
    }

    public List<WasteRecord> getWasteRecords() {
        return wasteRecords;
    }

    public void addWasteRecord(WasteRecord record) {
        this.wasteRecords.add(record);
    }

    public double getTotalWasteKg() {
        return wasteRecords.stream().mapToDouble(WasteRecord::getQuantityKg).sum();
    }

    public String getAreaName() {
        return areaName != null ? areaName : "Area #" + areaId;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getVehicleNumber() {
        return vehicleNumber != null ? vehicleNumber : "Vehicle #" + vehicleId;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public double getVehicleCapacityKg() {
        return vehicleCapacityKg;
    }

    public void setVehicleCapacityKg(double vehicleCapacityKg) {
        this.vehicleCapacityKg = vehicleCapacityKg;
    }

    public double getCapacityUtilizationPercentage() {
        if (vehicleCapacityKg <= 0.0) return 0.0;
        return (getTotalWasteKg() / vehicleCapacityKg) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("[Task #%-3d] Date: %s | %-25s | Veh: %-14s | Status: %-11s | Total: %,8.2f kg (%.1f%%)",
                taskId, scheduledDate, getAreaName(), getVehicleNumber(), status, getTotalWasteKg(), getCapacityUtilizationPercentage());
    }
}
