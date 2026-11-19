package com.municipal.waste.dao;

import com.municipal.waste.model.CollectionStatus;
import com.municipal.waste.model.CollectionTask;
import com.municipal.waste.model.WasteCategory;
import com.municipal.waste.model.WasteRecord;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Collection Schedules, Task Assignments, and Categorized Waste Records.
 * Author: DHARANI K K (Register No.: 23)
 */
public class CollectionDAO {

    private final Map<Integer, CollectionTask> taskStore = new LinkedHashMap<>();
    private final List<WasteRecord> recordStore = new ArrayList<>();
    private int nextTaskId = 125;
    private int nextRecordId = 1001;

    public CollectionDAO(AreaDAO areaDAO, VehicleDAO vehicleDAO) {
        seedInitialData(areaDAO, vehicleDAO);
    }

    private void seedInitialData(AreaDAO areaDAO, VehicleDAO vehicleDAO) {
        // Seed initial tasks
        createSeedTask(101, 1, 1, "2026-11-10", CollectionStatus.COMPLETED, areaDAO, vehicleDAO);
        addSeedRecord(101, WasteCategory.BIODEGRADABLE, 2850.0);
        addSeedRecord(101, WasteCategory.RECYCLABLE, 1320.0);
        addSeedRecord(101, WasteCategory.NON_RECYCLABLE, 720.0);
        addSeedRecord(101, WasteCategory.HAZARDOUS, 60.0);

        createSeedTask(102, 2, 2, "2026-11-10", CollectionStatus.COMPLETED, areaDAO, vehicleDAO);
        addSeedRecord(102, WasteCategory.BIODEGRADABLE, 3100.0);
        addSeedRecord(102, WasteCategory.RECYCLABLE, 1550.0);
        addSeedRecord(102, WasteCategory.NON_RECYCLABLE, 680.0);
        addSeedRecord(102, WasteCategory.HAZARDOUS, 70.0);

        createSeedTask(103, 3, 5, "2026-11-10", CollectionStatus.COMPLETED, areaDAO, vehicleDAO);
        addSeedRecord(103, WasteCategory.BIODEGRADABLE, 1250.0);
        addSeedRecord(103, WasteCategory.RECYCLABLE, 620.0);
        addSeedRecord(103, WasteCategory.NON_RECYCLABLE, 250.0);
        addSeedRecord(103, WasteCategory.HAZARDOUS, 30.0);

        createSeedTask(104, 4, 3, "2026-11-10", CollectionStatus.COMPLETED, areaDAO, vehicleDAO);
        addSeedRecord(104, WasteCategory.BIODEGRADABLE, 2200.0);
        addSeedRecord(104, WasteCategory.RECYCLABLE, 1180.0);
        addSeedRecord(104, WasteCategory.NON_RECYCLABLE, 520.0);
        addSeedRecord(104, WasteCategory.HAZARDOUS, 50.0);

        createSeedTask(105, 5, 2, "2026-11-11", CollectionStatus.COMPLETED, areaDAO, vehicleDAO);
        addSeedRecord(105, WasteCategory.BIODEGRADABLE, 3450.0);
        addSeedRecord(105, WasteCategory.RECYCLABLE, 1600.0);
        addSeedRecord(105, WasteCategory.NON_RECYCLABLE, 670.0);
        addSeedRecord(105, WasteCategory.HAZARDOUS, 80.0);

        createSeedTask(106, 6, 1, "2026-11-11", CollectionStatus.COMPLETED, areaDAO, vehicleDAO);
        addSeedRecord(106, WasteCategory.BIODEGRADABLE, 3600.0);
        addSeedRecord(106, WasteCategory.RECYCLABLE, 1580.0);
        addSeedRecord(106, WasteCategory.NON_RECYCLABLE, 690.0);
        addSeedRecord(106, WasteCategory.HAZARDOUS, 80.0);

        createSeedTask(121, 1, 3, "2026-11-16", CollectionStatus.IN_PROGRESS, areaDAO, vehicleDAO);
        addSeedRecord(121, WasteCategory.BIODEGRADABLE, 1800.0);
        addSeedRecord(121, WasteCategory.RECYCLABLE, 900.0);

        createSeedTask(123, 5, 2, "2026-11-16", CollectionStatus.SCHEDULED, areaDAO, vehicleDAO);
    }

    private void createSeedTask(int taskId, int areaId, int vehicleId, String date, CollectionStatus status, AreaDAO aDao, VehicleDAO vDao) {
        CollectionTask task = new CollectionTask(taskId, areaId, vehicleId, date, status);
        if (aDao.getAreaById(areaId) != null) {
            task.setAreaName(aDao.getAreaById(areaId).getAreaName());
        }
        if (vDao.getVehicleById(vehicleId) != null) {
            task.setVehicleNumber(vDao.getVehicleById(vehicleId).getVehicleNumber());
            task.setVehicleCapacityKg(vDao.getVehicleById(vehicleId).getCapacityKg());
        }
        taskStore.put(taskId, task);
    }

    private void addSeedRecord(int taskId, WasteCategory cat, double qty) {
        WasteRecord rec = new WasteRecord(nextRecordId++, taskId, cat, qty, "2026-11-10 14:00:00");
        recordStore.add(rec);
        CollectionTask t = taskStore.get(taskId);
        if (t != null) {
            t.addWasteRecord(rec);
        }
    }

    public List<CollectionTask> getAllTasks() {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            List<CollectionTask> list = new ArrayList<>();
            String sql = "SELECT t.task_id, t.area_id, t.vehicle_id, t.scheduled_date, t.status, " +
                         "a.area_name, v.vehicle_number, v.capacity_kg " +
                         "FROM collection_tasks t " +
                         "JOIN residential_areas a ON t.area_id = a.area_id " +
                         "JOIN vehicles v ON t.vehicle_id = v.vehicle_id " +
                         "ORDER BY t.task_id DESC";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    CollectionTask task = new CollectionTask(
                            rs.getInt("task_id"),
                            rs.getInt("area_id"),
                            rs.getInt("vehicle_id"),
                            rs.getString("scheduled_date"),
                            CollectionStatus.valueOf(rs.getString("status"))
                    );
                    task.setAreaName(rs.getString("area_name"));
                    task.setVehicleNumber(rs.getString("vehicle_number"));
                    task.setVehicleCapacityKg(rs.getDouble("capacity_kg"));
                    
                    // Attach records
                    for (WasteRecord wr : getWasteRecordsByTaskId(task.getTaskId())) {
                        task.addWasteRecord(wr);
                    }
                    list.add(task);
                }
                return list;
            } catch (SQLException ignored) {
            }
        }
        return new ArrayList<>(taskStore.values());
    }

    public CollectionTask getTaskById(int taskId) {
        CollectionTask task = taskStore.get(taskId);
        if (task != null) return task;
        
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "SELECT t.task_id, t.area_id, t.vehicle_id, t.scheduled_date, t.status, " +
                         "a.area_name, v.vehicle_number, v.capacity_kg " +
                         "FROM collection_tasks t " +
                         "JOIN residential_areas a ON t.area_id = a.area_id " +
                         "JOIN vehicles v ON t.vehicle_id = v.vehicle_id " +
                         "WHERE t.task_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, taskId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        task = new CollectionTask(
                                rs.getInt("task_id"),
                                rs.getInt("area_id"),
                                rs.getInt("vehicle_id"),
                                rs.getString("scheduled_date"),
                                CollectionStatus.valueOf(rs.getString("status"))
                        );
                        task.setAreaName(rs.getString("area_name"));
                        task.setVehicleNumber(rs.getString("vehicle_number"));
                        task.setVehicleCapacityKg(rs.getDouble("capacity_kg"));
                        for (WasteRecord wr : getWasteRecordsByTaskId(task.getTaskId())) {
                            task.addWasteRecord(wr);
                        }
                        return task;
                    }
                }
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

    public boolean insertTask(CollectionTask task) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO collection_tasks (area_id, vehicle_id, scheduled_date, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, task.getAreaId());
                pstmt.setInt(2, task.getVehicleId());
                pstmt.setString(3, task.getScheduledDate());
                pstmt.setString(4, task.getStatus().name());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            task.setTaskId(rs.getInt(1));
                        }
                    }
                    taskStore.put(task.getTaskId(), task);
                    return true;
                }
            } catch (SQLException ignored) {
            }
        }
        task.setTaskId(nextTaskId++);
        taskStore.put(task.getTaskId(), task);
        return true;
    }

    public boolean updateTaskStatus(int taskId, CollectionStatus status) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "UPDATE collection_tasks SET status = ? WHERE task_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status.name());
                pstmt.setInt(2, taskId);
                pstmt.executeUpdate();
            } catch (SQLException ignored) {
            }
        }
        CollectionTask task = taskStore.get(taskId);
        if (task != null) {
            task.setStatus(status);
            return true;
        }
        return false;
    }

    public boolean insertWasteRecord(WasteRecord record) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO waste_records (task_id, category_id, quantity_kg) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, record.getTaskId());
                pstmt.setInt(2, record.getCategory().getId());
                pstmt.setDouble(3, record.getQuantityKg());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            record.setRecordId(rs.getInt(1));
                        }
                    }
                    recordStore.add(record);
                    CollectionTask task = taskStore.get(record.getTaskId());
                    if (task != null) task.addWasteRecord(record);
                    return true;
                }
            } catch (SQLException ignored) {
            }
        }
        record.setRecordId(nextRecordId++);
        recordStore.add(record);
        CollectionTask task = taskStore.get(record.getTaskId());
        if (task != null) task.addWasteRecord(record);
        return true;
    }

    public List<WasteRecord> getWasteRecordsByTaskId(int taskId) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            List<WasteRecord> list = new ArrayList<>();
            String sql = "SELECT record_id, task_id, category_id, quantity_kg, recorded_at FROM waste_records WHERE task_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, taskId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(new WasteRecord(
                                rs.getInt("record_id"),
                                rs.getInt("task_id"),
                                WasteCategory.fromId(rs.getInt("category_id")),
                                rs.getDouble("quantity_kg"),
                                rs.getString("recorded_at")
                        ));
                    }
                    return list;
                }
            } catch (SQLException ignored) {
            }
        }
        List<WasteRecord> filtered = new ArrayList<>();
        for (WasteRecord wr : recordStore) {
            if (wr.getTaskId() == taskId) filtered.add(wr);
        }
        return filtered;
    }

    public List<WasteRecord> getAllWasteRecords() {
        return new ArrayList<>(recordStore);
    }
}
