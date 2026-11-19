-- ============================================================================
-- Municipal Waste Collection Management & Environmental Data Analysis System
-- Author: DHARANI K K (Register No.: 23)
-- Department of Information Technology, Velalar College of Engineering and Technology
-- Course: 25ITT31 - Database Management Systems
-- ============================================================================

-- Drop existing tables to allow clean re-runs
DROP TABLE IF EXISTS waste_records;
DROP TABLE IF EXISTS collection_tasks;
DROP TABLE IF EXISTS waste_categories;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS residential_areas;

-- ----------------------------------------------------------------------------
-- Table 1: residential_areas
-- Stores geographical zones, ward locations, and populations.
-- ----------------------------------------------------------------------------
CREATE TABLE residential_areas (
    area_id INTEGER PRIMARY KEY AUTOINCREMENT,
    area_name VARCHAR(100) NOT NULL,
    ward_location VARCHAR(100) NOT NULL,
    population INTEGER NOT NULL CHECK (population > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Table 2: vehicles
-- Stores municipal waste collection vehicles, capacity, and operational status.
-- ----------------------------------------------------------------------------
CREATE TABLE vehicles (
    vehicle_id INTEGER PRIMARY KEY AUTOINCREMENT,
    vehicle_number VARCHAR(20) NOT NULL UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL,
    capacity_kg REAL NOT NULL CHECK (capacity_kg > 0),
    operational_status VARCHAR(20) DEFAULT 'AVAILABLE',
    last_maintenance DATE
);

-- ----------------------------------------------------------------------------
-- Table 3: waste_categories
-- Standardized classification of municipal solid waste.
-- ----------------------------------------------------------------------------
CREATE TABLE waste_categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- ----------------------------------------------------------------------------
-- Table 4: collection_tasks
-- Assigns vehicles to residential areas for scheduled dates.
-- ----------------------------------------------------------------------------
CREATE TABLE collection_tasks (
    task_id INTEGER PRIMARY KEY AUTOINCREMENT,
    area_id INTEGER NOT NULL,
    vehicle_id INTEGER NOT NULL,
    scheduled_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (area_id) REFERENCES residential_areas(area_id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- Table 5: waste_records
-- Records categorized waste quantities collected during tasks.
-- ----------------------------------------------------------------------------
CREATE TABLE waste_records (
    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
    task_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    quantity_kg REAL NOT NULL CHECK (quantity_kg >= 0),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES collection_tasks(task_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES waste_categories(category_id)
);

-- ----------------------------------------------------------------------------
-- Views for Analytical Reporting
-- ----------------------------------------------------------------------------

-- View 1: Area-wise Waste Generation Summary
CREATE VIEW IF NOT EXISTS v_area_waste_summary AS
SELECT 
    a.area_id,
    a.area_name,
    a.ward_location,
    a.population,
    COUNT(DISTINCT t.task_id) AS total_trips,
    ROUND(COALESCE(SUM(r.quantity_kg), 0), 2) AS total_waste_kg,
    ROUND(COALESCE(SUM(r.quantity_kg) / a.population, 0), 4) AS per_capita_waste_kg
FROM residential_areas a
LEFT JOIN collection_tasks t ON a.area_id = t.area_id
LEFT JOIN waste_records r ON t.task_id = r.task_id
GROUP BY a.area_id, a.area_name, a.ward_location, a.population;

-- View 2: Vehicle Capacity Utilization Summary
CREATE VIEW IF NOT EXISTS v_vehicle_utilization AS
SELECT 
    v.vehicle_id,
    v.vehicle_number,
    v.vehicle_type,
    v.capacity_kg,
    t.task_id,
    t.scheduled_date,
    t.status,
    ROUND(COALESCE(SUM(r.quantity_kg), 0), 2) AS collected_payload_kg,
    ROUND((COALESCE(SUM(r.quantity_kg), 0) / v.capacity_kg) * 100, 2) AS utilization_percentage
FROM vehicles v
JOIN collection_tasks t ON v.vehicle_id = t.vehicle_id
LEFT JOIN waste_records r ON t.task_id = r.task_id
GROUP BY v.vehicle_id, v.vehicle_number, v.vehicle_type, v.capacity_kg, t.task_id, t.scheduled_date, t.status;

-- View 3: Category-wise Waste Aggregation
CREATE VIEW IF NOT EXISTS v_category_waste_summary AS
SELECT 
    c.category_id,
    c.category_name,
    ROUND(COALESCE(SUM(r.quantity_kg), 0), 2) AS total_collected_kg,
    COUNT(r.record_id) AS record_count
FROM waste_categories c
LEFT JOIN waste_records r ON c.category_id = r.category_id
GROUP BY c.category_id, c.category_name;
