package com.municipal.waste.service;

import com.municipal.waste.dao.AreaDAO;
import com.municipal.waste.dao.CollectionDAO;
import com.municipal.waste.dao.VehicleDAO;
import com.municipal.waste.model.*;
import com.municipal.waste.util.InputValidator;
import com.municipal.waste.util.ValidationException;

import java.util.List;

/**
 * Core business logic service orchestrating waste management operations.
 * Author: DHARANI K K (Register No.: 23)
 */
public class WasteManagementService {

    private final AreaDAO areaDAO;
    private final VehicleDAO vehicleDAO;
    private final CollectionDAO collectionDAO;

    public WasteManagementService(AreaDAO areaDAO, VehicleDAO vehicleDAO, CollectionDAO collectionDAO) {
        this.areaDAO = areaDAO;
        this.vehicleDAO = vehicleDAO;
        this.collectionDAO = collectionDAO;
    }

    // ---------------- Area Operations ----------------
    public Area registerArea(String name, String ward, int population) throws ValidationException {
        InputValidator.validateArea(name, ward, population);
        Area area = new Area(name, ward, population);
        boolean success = areaDAO.insertArea(area);
        if (!success) throw new ValidationException("Failed to register residential area.");
        return area;
    }

    public List<Area> listAllAreas() {
        return areaDAO.getAllAreas();
    }

    public Area getArea(int areaId) {
        return areaDAO.getAreaById(areaId);
    }

    // ---------------- Vehicle Operations ----------------
    public Vehicle registerVehicle(String number, String type, double capacityKg) throws ValidationException {
        InputValidator.validateVehicle(number, type, capacityKg);
        Vehicle vehicle = new Vehicle(number, type, capacityKg);
        boolean success = vehicleDAO.insertVehicle(vehicle);
        if (!success) throw new ValidationException("Failed to register collection vehicle.");
        return vehicle;
    }

    public List<Vehicle> listAllVehicles() {
        return vehicleDAO.getAllVehicles();
    }

    public Vehicle getVehicle(int vehicleId) {
        return vehicleDAO.getVehicleById(vehicleId);
    }

    public boolean updateVehicleStatus(int vehicleId, String status) throws ValidationException {
        Vehicle v = vehicleDAO.getVehicleById(vehicleId);
        if (v == null) throw new ValidationException("Vehicle ID not found: " + vehicleId);
        return vehicleDAO.updateStatus(vehicleId, status);
    }

    // ---------------- Collection Scheduling ----------------
    public CollectionTask scheduleCollection(int areaId, int vehicleId, String scheduledDate) throws ValidationException {
        Area area = areaDAO.getAreaById(areaId);
        if (area == null) {
            throw new ValidationException("Cannot schedule: Invalid Area ID " + areaId);
        }

        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
        if (vehicle == null) {
            throw new ValidationException("Cannot schedule: Invalid Vehicle ID " + vehicleId);
        }

        if ("MAINTENANCE".equalsIgnoreCase(vehicle.getOperationalStatus())) {
            throw new ValidationException("Cannot assign vehicle " + vehicle.getVehicleNumber() + " - currently under MAINTENANCE.");
        }

        CollectionTask task = new CollectionTask(0, areaId, vehicleId, scheduledDate, CollectionStatus.SCHEDULED);
        task.setAreaName(area.getAreaName());
        task.setVehicleNumber(vehicle.getVehicleNumber());
        task.setVehicleCapacityKg(vehicle.getCapacityKg());

        boolean success = collectionDAO.insertTask(task);
        if (!success) throw new ValidationException("Database error while creating collection task.");
        return task;
    }

    public List<CollectionTask> listAllTasks() {
        return collectionDAO.getAllTasks();
    }

    public CollectionTask getTask(int taskId) {
        return collectionDAO.getTaskById(taskId);
    }

    public boolean updateTaskStatus(int taskId, CollectionStatus newStatus) throws ValidationException {
        CollectionTask task = collectionDAO.getTaskById(taskId);
        if (task == null) {
            throw new ValidationException("Task ID " + taskId + " does not exist.");
        }
        return collectionDAO.updateTaskStatus(taskId, newStatus);
    }

    // ---------------- Waste Recording & Overload Check ----------------
    public double recordWaste(int taskId, WasteCategory category, double quantityKg) throws ValidationException {
        InputValidator.validateWasteQuantity(quantityKg);

        CollectionTask task = collectionDAO.getTaskById(taskId);
        if (task == null) {
            throw new ValidationException("Invalid Task ID: " + taskId);
        }

        if (task.getStatus() == CollectionStatus.CANCELLED) {
            throw new ValidationException("Cannot record waste for a CANCELLED task.");
        }

        // If task was scheduled, auto-transition to IN_PROGRESS upon first collection log
        if (task.getStatus() == CollectionStatus.SCHEDULED) {
            collectionDAO.updateTaskStatus(taskId, CollectionStatus.IN_PROGRESS);
            task.setStatus(CollectionStatus.IN_PROGRESS);
        }

        WasteRecord record = new WasteRecord(taskId, category, quantityKg);
        boolean success = collectionDAO.insertWasteRecord(record);
        if (!success) throw new ValidationException("Failed to persist waste record.");

        // Calculate updated payload and return capacity utilization %
        double currentTotal = task.getTotalWasteKg();
        double capacity = task.getVehicleCapacityKg();
        if (capacity <= 0.0) {
            Vehicle v = vehicleDAO.getVehicleById(task.getVehicleId());
            if (v != null) capacity = v.getCapacityKg();
        }

        return (capacity > 0) ? (currentTotal / capacity) * 100.0 : 0.0;
    }
}
