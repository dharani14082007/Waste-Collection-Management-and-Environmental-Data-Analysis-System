-- ============================================================================
-- Analytical SQL Queries for Municipal Waste Management
-- Author: DHARANI K K (Register No.: 23)
-- Covers: Aggregate Functions, Joins, Subqueries, Group By, Window Ordering
-- ============================================================================

-- Query 1: Total Waste Collected by Residential Area (Ranked Highest to Lowest)
SELECT 
    a.area_id,
    a.area_name,
    a.ward_location,
    a.population,
    ROUND(SUM(r.quantity_kg), 2) AS total_waste_kg,
    ROUND(SUM(r.quantity_kg) / a.population, 4) AS per_capita_waste_kg
FROM residential_areas a
JOIN collection_tasks t ON a.area_id = t.area_id
JOIN waste_records r ON t.task_id = r.task_id
WHERE t.status = 'COMPLETED'
GROUP BY a.area_id, a.area_name, a.ward_location, a.population
ORDER BY total_waste_kg DESC;

-- Query 2: Waste Breakdown by Segregation Category (Quantity & Percentage)
SELECT 
    c.category_name,
    ROUND(SUM(r.quantity_kg), 2) AS category_total_kg,
    ROUND((SUM(r.quantity_kg) * 100.0) / (SELECT SUM(quantity_kg) FROM waste_records), 2) AS percentage_share
FROM waste_categories c
JOIN waste_records r ON c.category_id = r.category_id
GROUP BY c.category_id, c.category_name
ORDER BY category_total_kg DESC;

-- Query 3: Vehicle Capacity Utilization Efficiency Report
SELECT 
    v.vehicle_number,
    v.vehicle_type,
    v.capacity_kg AS rated_capacity_kg,
    COUNT(t.task_id) AS trips_completed,
    ROUND(AVG(trip_waste.trip_total), 2) AS avg_payload_per_trip_kg,
    ROUND((AVG(trip_waste.trip_total) / v.capacity_kg) * 100, 2) AS avg_capacity_utilization_pct
FROM vehicles v
JOIN collection_tasks t ON v.vehicle_id = t.vehicle_id
JOIN (
    SELECT task_id, SUM(quantity_kg) AS trip_total
    FROM waste_records
    GROUP BY task_id
) trip_waste ON t.task_id = trip_waste.task_id
WHERE t.status = 'COMPLETED'
GROUP BY v.vehicle_id, v.vehicle_number, v.vehicle_type, v.capacity_kg
ORDER BY avg_capacity_utilization_pct DESC;

-- Query 4: Subquery to Identify Overloaded Collection Runs (> 95% capacity)
SELECT 
    t.task_id,
    t.scheduled_date,
    a.area_name,
    v.vehicle_number,
    v.capacity_kg,
    ROUND(SUM(r.quantity_kg), 2) AS actual_load_kg,
    ROUND((SUM(r.quantity_kg) / v.capacity_kg) * 100, 2) AS utilization_pct,
    CASE 
        WHEN SUM(r.quantity_kg) > v.capacity_kg THEN 'OVERLOADED (>100%)'
        WHEN SUM(r.quantity_kg) >= (0.95 * v.capacity_kg) THEN 'NEAR CAPACITY (95-100%)'
        ELSE 'NORMAL'
    END AS operational_alert
FROM collection_tasks t
JOIN residential_areas a ON t.area_id = a.area_id
JOIN vehicles v ON t.vehicle_id = v.vehicle_id
JOIN waste_records r ON t.task_id = r.task_id
GROUP BY t.task_id, t.scheduled_date, a.area_name, v.vehicle_number, v.capacity_kg
HAVING SUM(r.quantity_kg) >= (0.90 * v.capacity_kg)
ORDER BY utilization_pct DESC;

-- Query 5: Daily Waste Generation Trend Across Entire Municipality
SELECT 
    t.scheduled_date,
    COUNT(DISTINCT t.task_id) AS trips_executed,
    ROUND(SUM(r.quantity_kg), 2) AS daily_total_kg,
    ROUND(SUM(CASE WHEN r.category_id = 1 THEN r.quantity_kg ELSE 0 END), 2) AS biodegradable_kg,
    ROUND(SUM(CASE WHEN r.category_id = 2 THEN r.quantity_kg ELSE 0 END), 2) AS recyclable_kg,
    ROUND(SUM(CASE WHEN r.category_id = 3 THEN r.quantity_kg ELSE 0 END), 2) AS non_recyclable_kg
FROM collection_tasks t
JOIN waste_records r ON t.task_id = r.task_id
GROUP BY t.scheduled_date
ORDER BY t.scheduled_date ASC;

-- Query 6: Collection Task Status Metric
SELECT 
    status,
    COUNT(task_id) AS task_count,
    ROUND((COUNT(task_id) * 100.0) / (SELECT COUNT(*) FROM collection_tasks), 2) AS status_percentage
FROM collection_tasks
GROUP BY status;
