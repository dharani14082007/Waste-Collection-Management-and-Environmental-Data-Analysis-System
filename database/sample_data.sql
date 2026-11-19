-- ============================================================================
-- Municipal Waste Collection Management - Realistic Dataset
-- Author: DHARANI K K (Register No.: 23)
-- Locality Context: Erode Municipal Corporation Zones
-- ============================================================================

-- 1. Insert Waste Categories
INSERT INTO waste_categories (category_id, category_name, description) VALUES
(1, 'Biodegradable', 'Organic kitchen waste, vegetables, fruits, yard waste, leaves'),
(2, 'Recyclable', 'Dry paper, plastics, glass bottles, metals, cardboard'),
(3, 'Non-Recyclable', 'Inert debris, dust, sanitary hygiene waste, ceramic shards'),
(4, 'Hazardous', 'E-waste, battery cells, chemical cleaners, paint residues');

-- 2. Insert Residential Areas
INSERT INTO residential_areas (area_id, area_name, ward_location, population) VALUES
(1, 'Perundurai Road Extension', 'Ward 04 - West Zone', 18500),
(2, 'Brough Road Commercial-Res', 'Ward 12 - Central Zone', 24200),
(3, 'Thindal Hills Enclave', 'Ward 18 - South-West Zone', 15800),
(4, 'Solar Urban Nagar', 'Ward 22 - South Zone', 21400),
(5, 'Surampatti Junction Colony', 'Ward 08 - South-Central Zone', 28900),
(6, 'Veerappanchatram Town', 'Ward 02 - North Zone', 32500),
(7, 'Rangampalayam Layout', 'Ward 15 - East Zone', 14200),
(8, 'Kasipalayam Riverside Ward', 'Ward 19 - East Zone', 19600),
(9, 'Kollampalayam Bypass Area', 'Ward 27 - South-East Zone', 17300),
(10, 'Chithode Satellite Colony', 'Ward 33 - Outer North Zone', 11800);

-- 3. Insert Vehicles Fleet
INSERT INTO vehicles (vehicle_id, vehicle_number, vehicle_type, capacity_kg, operational_status, last_maintenance) VALUES
(1, 'TN-33-AX-1011', 'Compactor Heavy Truck', 6000.0, 'AVAILABLE', '2026-11-01'),
(2, 'TN-33-AX-1012', 'Compactor Heavy Truck', 6000.0, 'AVAILABLE', '2026-11-03'),
(3, 'TN-33-BT-2021', 'Hydraulic Tipper Truck', 4500.0, 'AVAILABLE', '2026-10-28'),
(4, 'TN-33-BT-2022', 'Hydraulic Tipper Truck', 4500.0, 'AVAILABLE', '2026-11-05'),
(5, 'TN-33-CM-3031', 'Mini Tipper LCV', 2500.0, 'AVAILABLE', '2026-11-10'),
(6, 'TN-33-CM-3032', 'Mini Tipper LCV', 2500.0, 'AVAILABLE', '2026-11-08'),
(7, 'TN-33-EV-4041', 'Electric Waste Cart', 1200.0, 'AVAILABLE', '2026-11-12'),
(8, 'TN-33-EV-4042', 'Electric Waste Cart', 1200.0, 'MAINTENANCE', '2026-11-14');

-- 4. Insert Collection Tasks across dates (November 2026)
INSERT INTO collection_tasks (task_id, area_id, vehicle_id, scheduled_date, status) VALUES
(101, 1, 1, '2026-11-10', 'COMPLETED'),
(102, 2, 2, '2026-11-10', 'COMPLETED'),
(103, 3, 5, '2026-11-10', 'COMPLETED'),
(104, 4, 3, '2026-11-10', 'COMPLETED'),
(105, 5, 2, '2026-11-11', 'COMPLETED'),
(106, 6, 1, '2026-11-11', 'COMPLETED'),
(107, 7, 7, '2026-11-11', 'COMPLETED'),
(108, 8, 4, '2026-11-11', 'COMPLETED'),
(109, 9, 6, '2026-11-12', 'COMPLETED'),
(110, 10, 7, '2026-11-12', 'COMPLETED'),
(111, 1, 1, '2026-11-13', 'COMPLETED'),
(112, 2, 2, '2026-11-13', 'COMPLETED'),
(113, 3, 5, '2026-11-13', 'COMPLETED'),
(114, 4, 3, '2026-11-13', 'COMPLETED'),
(115, 5, 1, '2026-11-14', 'COMPLETED'),
(116, 6, 2, '2026-11-14', 'COMPLETED'),
(117, 7, 7, '2026-11-14', 'COMPLETED'),
(118, 8, 4, '2026-11-14', 'COMPLETED'),
(119, 9, 6, '2026-11-15', 'COMPLETED'),
(120, 10, 5, '2026-11-15', 'COMPLETED'),
(121, 1, 3, '2026-11-16', 'IN_PROGRESS'),
(122, 2, 1, '2026-11-16', 'IN_PROGRESS'),
(123, 5, 2, '2026-11-16', 'SCHEDULED'),
(124, 6, 4, '2026-11-16', 'SCHEDULED');

-- 5. Insert Detailed Waste Records for Completed and In-Progress Tasks
INSERT INTO waste_records (task_id, category_id, quantity_kg) VALUES
-- Task 101: Area 1 (Perundurai Rd), Vehicle 1 (Cap: 6000kg) -> Total: 4950 kg (82.5% util)
(101, 1, 2850.0),
(101, 2, 1320.0),
(101, 3, 720.0),
(101, 4, 60.0),

-- Task 102: Area 2 (Brough Rd), Vehicle 2 (Cap: 6000kg) -> Total: 5400 kg (90% util)
(102, 1, 3100.0),
(102, 2, 1550.0),
(102, 3, 680.0),
(102, 4, 70.0),

-- Task 103: Area 3 (Thindal Hills), Vehicle 5 (Cap: 2500kg) -> Total: 2150 kg (86% util)
(103, 1, 1250.0),
(103, 2, 620.0),
(103, 3, 250.0),
(103, 4, 30.0),

-- Task 104: Area 4 (Solar Urban), Vehicle 3 (Cap: 4500kg) -> Total: 3950 kg (87.7% util)
(104, 1, 2200.0),
(104, 2, 1180.0),
(104, 3, 520.0),
(104, 4, 50.0),

-- Task 105: Area 5 (Surampatti), Vehicle 2 (Cap: 6000kg) -> Total: 5800 kg (96.6% util)
(105, 1, 3450.0),
(105, 2, 1600.0),
(105, 3, 670.0),
(105, 4, 80.0),

-- Task 106: Area 6 (Veerappanchatram), Vehicle 1 (Cap: 6000kg) -> Total: 5950 kg (99.1% util)
(106, 1, 3600.0),
(106, 2, 1580.0),
(106, 3, 690.0),
(106, 4, 80.0),

-- Task 107: Area 7 (Rangampalayam), Vehicle 7 (Cap: 1200kg) -> Total: 1080 kg (90% util)
(107, 1, 650.0),
(107, 2, 280.0),
(107, 3, 135.0),
(107, 4, 15.0),

-- Task 108: Area 8 (Kasipalayam), Vehicle 4 (Cap: 4500kg) -> Total: 3820 kg (84.8% util)
(108, 1, 2150.0),
(108, 2, 1120.0),
(108, 3, 500.0),
(108, 4, 50.0),

-- Task 109: Area 9 (Kollampalayam), Vehicle 6 (Cap: 2500kg) -> Total: 2280 kg (91.2% util)
(109, 1, 1350.0),
(109, 2, 610.0),
(109, 3, 290.0),
(109, 4, 30.0),

-- Task 110: Area 10 (Chithode), Vehicle 7 (Cap: 1200kg) -> Total: 980 kg (81.6% util)
(110, 1, 580.0),
(110, 2, 270.0),
(110, 3, 115.0),
(110, 4, 15.0),

-- Task 111: Area 1 (Perundurai Rd), Vehicle 1 (Cap: 6000kg) -> Total: 4890 kg
(111, 1, 2800.0),
(111, 2, 1340.0),
(111, 3, 700.0),
(111, 4, 50.0),

-- Task 112: Area 2 (Brough Rd), Vehicle 2 (Cap: 6000kg) -> Total: 5350 kg
(112, 1, 3050.0),
(112, 2, 1540.0),
(112, 3, 700.0),
(112, 4, 60.0),

-- Task 113: Area 3 (Thindal Hills), Vehicle 5 (Cap: 2500kg) -> Total: 2190 kg
(113, 1, 1280.0),
(113, 2, 630.0),
(113, 3, 250.0),
(113, 4, 30.0),

-- Task 114: Area 4 (Solar Urban), Vehicle 3 (Cap: 4500kg) -> Total: 4020 kg
(114, 1, 2250.0),
(114, 2, 1200.0),
(114, 3, 520.0),
(114, 4, 50.0),

-- Task 115: Area 5 (Surampatti), Vehicle 1 (Cap: 6000kg) -> Total: 5900 kg
(115, 1, 3500.0),
(115, 2, 1620.0),
(115, 3, 700.0),
(115, 4, 80.0),

-- Task 116: Area 6 (Veerappanchatram), Vehicle 2 (Cap: 6000kg) -> Total: 6050 kg (OVERLOAD: 100.8%)
(116, 1, 3700.0),
(116, 2, 1580.0),
(116, 3, 680.0),
(116, 4, 90.0),

-- Task 117: Area 7 (Rangampalayam), Vehicle 7 (Cap: 1200kg) -> Total: 1040 kg
(117, 1, 620.0),
(117, 2, 270.0),
(117, 3, 135.0),
(117, 4, 15.0),

-- Task 118: Area 8 (Kasipalayam), Vehicle 4 (Cap: 4500kg) -> Total: 3880 kg
(118, 1, 2200.0),
(118, 2, 1140.0),
(118, 3, 490.0),
(118, 4, 50.0),

-- Task 119: Area 9 (Kollampalayam), Vehicle 6 (Cap: 2500kg) -> Total: 2310 kg
(119, 1, 1380.0),
(119, 2, 610.0),
(119, 3, 290.0),
(119, 4, 30.0),

-- Task 120: Area 10 (Chithode), Vehicle 5 (Cap: 2500kg) -> Total: 1950 kg
(120, 1, 1150.0),
(120, 2, 530.0),
(120, 3, 240.0),
(120, 4, 30.0),

-- Task 121 (In Progress): Area 1, Vehicle 3
(121, 1, 1800.0),
(121, 2, 900.0);
