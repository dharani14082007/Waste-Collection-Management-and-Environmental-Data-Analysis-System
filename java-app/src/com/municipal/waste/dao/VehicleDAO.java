package com.municipal.waste.dao;

import com.municipal.waste.model.Vehicle;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Waste Collection Vehicles.
 * Author: DHARANI K K (Register No.: 23)
 */
public class VehicleDAO {

    private final Map<Integer, Vehicle> memoryStore = new LinkedHashMap<>();
    private int nextId = 9;

    public VehicleDAO() {
        seedInitialData();
    }

    private void seedInitialData() {
        memoryStore.put(1, new Vehicle(1, "TN-33-AX-1011", "Compactor Heavy Truck", 6000.0, "AVAILABLE", "2026-11-01"));
        memoryStore.put(2, new Vehicle(2, "TN-33-AX-1012", "Compactor Heavy Truck", 6000.0, "AVAILABLE", "2026-11-03"));
        memoryStore.put(3, new Vehicle(3, "TN-33-BT-2021", "Hydraulic Tipper Truck", 4500.0, "AVAILABLE", "2026-10-28"));
        memoryStore.put(4, new Vehicle(4, "TN-33-BT-2022", "Hydraulic Tipper Truck", 4500.0, "AVAILABLE", "2026-11-05"));
        memoryStore.put(5, new Vehicle(5, "TN-33-CM-3031", "Mini Tipper LCV", 2500.0, "AVAILABLE", "2026-11-10"));
        memoryStore.put(6, new Vehicle(6, "TN-33-CM-3032", "Mini Tipper LCV", 2500.0, "AVAILABLE", "2026-11-08"));
        memoryStore.put(7, new Vehicle(7, "TN-33-EV-4041", "Electric Waste Cart", 1200.0, "AVAILABLE", "2026-11-12"));
        memoryStore.put(8, new Vehicle(8, "TN-33-EV-4042", "Electric Waste Cart", 1200.0, "MAINTENANCE", "2026-11-14"));
    }

    public List<Vehicle> getAllVehicles() {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            List<Vehicle> list = new ArrayList<>();
            String sql = "SELECT vehicle_id, vehicle_number, vehicle_type, capacity_kg, operational_status, last_maintenance FROM vehicles ORDER BY vehicle_id";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new Vehicle(
                            rs.getInt("vehicle_id"),
                            rs.getString("vehicle_number"),
                            rs.getString("vehicle_type"),
                            rs.getDouble("capacity_kg"),
                            rs.getString("operational_status"),
                            rs.getString("last_maintenance")
                    ));
                }
                return list;
            } catch (SQLException ignored) {
            }
        }
        return new ArrayList<>(memoryStore.values());
    }

    public Vehicle getVehicleById(int id) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "SELECT vehicle_id, vehicle_number, vehicle_type, capacity_kg, operational_status, last_maintenance FROM vehicles WHERE vehicle_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Vehicle(
                                rs.getInt("vehicle_id"),
                                rs.getString("vehicle_number"),
                                rs.getString("vehicle_type"),
                                rs.getDouble("capacity_kg"),
                                rs.getString("operational_status"),
                                rs.getString("last_maintenance")
                        );
                    }
                }
            } catch (SQLException ignored) {
            }
        }
        return memoryStore.get(id);
    }

    public boolean insertVehicle(Vehicle vehicle) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO vehicles (vehicle_number, vehicle_type, capacity_kg, operational_status, last_maintenance) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, vehicle.getVehicleNumber());
                pstmt.setString(2, vehicle.getVehicleType());
                pstmt.setDouble(3, vehicle.getCapacityKg());
                pstmt.setString(4, vehicle.getOperationalStatus());
                pstmt.setString(5, vehicle.getLastMaintenance());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            vehicle.setVehicleId(rs.getInt(1));
                        }
                    }
                    memoryStore.put(vehicle.getVehicleId(), vehicle);
                    return true;
                }
            } catch (SQLException ignored) {
            }
        }
        vehicle.setVehicleId(nextId++);
        memoryStore.put(vehicle.getVehicleId(), vehicle);
        return true;
    }

    public boolean updateStatus(int vehicleId, String status) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "UPDATE vehicles SET operational_status = ? WHERE vehicle_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, vehicleId);
                pstmt.executeUpdate();
            } catch (SQLException ignored) {
            }
        }
        Vehicle v = memoryStore.get(vehicleId);
        if (v != null) {
            v.setOperationalStatus(status);
            return true;
        }
        return false;
    }
}
