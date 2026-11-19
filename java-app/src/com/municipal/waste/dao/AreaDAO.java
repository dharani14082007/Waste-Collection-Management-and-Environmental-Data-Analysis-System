package com.municipal.waste.dao;

import com.municipal.waste.model.Area;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Residential Areas.
 * Author: DHARANI K K (Register No.: 23)
 */
public class AreaDAO {

    // Fallback in-memory cache pre-populated with municipal seed data
    private final Map<Integer, Area> memoryStore = new LinkedHashMap<>();
    private int nextId = 11;

    public AreaDAO() {
        seedInitialData();
    }

    private void seedInitialData() {
        memoryStore.put(1, new Area(1, "Perundurai Road Extension", "Ward 04 - West Zone", 18500));
        memoryStore.put(2, new Area(2, "Brough Road Commercial-Res", "Ward 12 - Central Zone", 24200));
        memoryStore.put(3, new Area(3, "Thindal Hills Enclave", "Ward 18 - South-West Zone", 15800));
        memoryStore.put(4, new Area(4, "Solar Urban Nagar", "Ward 22 - South Zone", 21400));
        memoryStore.put(5, new Area(5, "Surampatti Junction Colony", "Ward 08 - South-Central Zone", 28900));
        memoryStore.put(6, new Area(6, "Veerappanchatram Town", "Ward 02 - North Zone", 32500));
        memoryStore.put(7, new Area(7, "Rangampalayam Layout", "Ward 15 - East Zone", 14200));
        memoryStore.put(8, new Area(8, "Kasipalayam Riverside Ward", "Ward 19 - East Zone", 19600));
        memoryStore.put(9, new Area(9, "Kollampalayam Bypass Area", "Ward 27 - South-East Zone", 17300));
        memoryStore.put(10, new Area(10, "Chithode Satellite Colony", "Ward 33 - Outer North Zone", 11800));
    }

    public List<Area> getAllAreas() {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            List<Area> list = new ArrayList<>();
            String sql = "SELECT area_id, area_name, ward_location, population FROM residential_areas ORDER BY area_id";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new Area(
                            rs.getInt("area_id"),
                            rs.getString("area_name"),
                            rs.getString("ward_location"),
                            rs.getInt("population")
                    ));
                }
                return list;
            } catch (SQLException e) {
                // Fall back to memoryStore
            }
        }
        return new ArrayList<>(memoryStore.values());
    }

    public Area getAreaById(int id) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "SELECT area_id, area_name, ward_location, population FROM residential_areas WHERE area_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Area(
                                rs.getInt("area_id"),
                                rs.getString("area_name"),
                                rs.getString("ward_location"),
                                rs.getInt("population")
                        );
                    }
                }
            } catch (SQLException ignored) {
            }
        }
        return memoryStore.get(id);
    }

    public boolean insertArea(Area area) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO residential_areas (area_name, ward_location, population) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, area.getAreaName());
                pstmt.setString(2, area.getWardLocation());
                pstmt.setInt(3, area.getPopulation());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            area.setAreaId(rs.getInt(1));
                        }
                    }
                    memoryStore.put(area.getAreaId(), area);
                    return true;
                }
            } catch (SQLException ignored) {
            }
        }
        area.setAreaId(nextId++);
        memoryStore.put(area.getAreaId(), area);
        return true;
    }

    public boolean updateArea(Area area) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "UPDATE residential_areas SET area_name = ?, ward_location = ?, population = ? WHERE area_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, area.getAreaName());
                pstmt.setString(2, area.getWardLocation());
                pstmt.setInt(3, area.getPopulation());
                pstmt.setInt(4, area.getAreaId());
                pstmt.executeUpdate();
            } catch (SQLException ignored) {
            }
        }
        if (memoryStore.containsKey(area.getAreaId())) {
            memoryStore.put(area.getAreaId(), area);
            return true;
        }
        return false;
    }

    public boolean deleteArea(int areaId) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            String sql = "DELETE FROM residential_areas WHERE area_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, areaId);
                pstmt.executeUpdate();
            } catch (SQLException ignored) {
            }
        }
        return memoryStore.remove(areaId) != null;
    }
}
