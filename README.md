# Municipal Waste Collection Management and Environmental Data Analysis System (WCM-EDAS)

[![Academic Year](https://img.shields.io/badge/Academic%20Year-2026--27%20(Odd)-blue.svg)](#)
[![Department](https://img.shields.io/badge/Department-Information%20Technology-green.svg)](#)
[![Institution](https://img.shields.io/badge/VCET-Erode--12-orange.svg)](#)
[![Java](https://img.shields.io/badge/Java-SE%2011%2B-red.svg)](#)
[![Python](https://img.shields.io/badge/Python-3.9%2B-yellow.svg)](#)
[![Database](https://img.shields.io/badge/Database-SQLite%20%7C%20MySQL-blue.svg)](#)

---

## Student & Course Details
* **Student Name:** DHARANI K K  
* **Register Number:** 23  
* **Class:** 25IT3A&B  
* **Assigned Assessment Scenario:** Scenario 23  
* **Department:** Department of Information Technology  
* **Institution:** Velalar College of Engineering and Technology (Autonomous), Erode - 12  
* **Integrated Courses:**
  * **25ITT33** – Software Engineering
  * **25ITT31** – Database Management Systems
  * **25ITT32** – Java Programming
  * **25ITC31** – Foundations of Data Science

---

## 1. Project Overview & Problem Statement

A municipal corporation manages waste collection schedules, vehicle fleets, residential zones, and disposal facilities. Manual record-keeping leads to:
* Difficulty in monitoring day-to-day collection runs and pending routes.
* Delayed identification of high-density areas with excessive waste generation.
* Frequent vehicle overloading and suboptimal fleet utilization.
* Lack of actionable data to drive segregation and recycling policies.

### The Solution: WCM-EDAS
**Waste Collection Management and Environmental Data Analysis System (WCM-EDAS)** is a cross-platform solution integrating:
1. **Interactive Municipal Web Portal (`web-app/` & `website/`)**: Modern responsive web application featuring live KPI cards, interactive Chart.js charts, modal dialogs for area/vehicle/schedule registration, and real-time vehicle payload utilization gauge with overload alerts.
2. **Java Object-Oriented Management System (`java-app/`)**: Console application for recording residential sectors, vehicle fleets, assigning schedules, logging multi-category waste weights, and verifying payload limits.
3. **Relational Database Management System (`database/`)**: 3NF/BCNF compliant database storing entities, integrity constraints, triggers, and analytical views.
4. **Python Environmental Data Science Pipeline (`python-analysis/`)**: Automated database retrieval, preprocessing (outlier detection, missing value treatment), and exploratory data analysis yielding high-resolution visual patterns.

---

## 2. Functional Requirements Implementation

| Requirement | Description | System Implementation |
|:---|:---|:---|
| **FR-01** | Maintain residential area details (ID, name, ward, population). | `Area.java`, `residential_areas` SQL table, `AreaDAO.java` |
| **FR-02** | Maintain collection vehicle details (ID, type, capacity). | `Vehicle.java`, `vehicles` SQL table, `VehicleDAO.java` |
| **FR-03** | Record waste collection activities (date, area, waste type, quantity). | `CollectionTask.java`, `WasteRecord.java`, `collection_tasks` table |
| **FR-04** | Assign collection activities to vehicles and track completion status. | Status lifecycle: `SCHEDULED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `COMPLETED` $\rightarrow$ `CANCELLED` |
| **FR-05** | Record waste quantities by category (Biodegradable, Recyclable, Non-Recyclable, Hazardous). | Categorized entries logged with unit weights in kilograms |
| **FR-06** | Generate reports on area waste, vehicle utilization, and completion metrics. | `ReportService.java` + SQL analytical views `v_area_waste_summary` & `v_vehicle_utilization` |
| **FR-07** | Retrieve data from database, preprocess it, and generate visualizations using Python. | `waste_analysis.py` & `waste_analysis.ipynb` utilizing Pandas, NumPy, Matplotlib & Seaborn |

---

## 3. Project Directory Structure

```text
waste-collection-system/
│
├── docs/                                    # Academic Documentation
│   ├── SRS.md                               # Complete IEEE 830 Software Requirements Specification
│   ├── SYSTEM_DESIGN.md                     # UML Architecture, Use Case & Class Diagrams (Mermaid)
│   ├── DATABASE_DESIGN_AND_NORMALIZATION.md # ER Diagram, Relational Schema, Normalization (1NF-3NF)
│   └── TESTING_AND_TEST_CASES.md            # Test Cases, Equivalence Partitioning, Quality Metrics
│
├── database/                                # Database Scripts & Engine
│   ├── schema.sql                           # DDL: Tables, constraints, views, triggers
│   ├── sample_data.sql                      # DML: Realistic municipal seed dataset (Erode zones)
│   ├── queries.sql                          # Analytical SQL queries (joins, subqueries, aggregations)
│   ├── init_db.py                           # SQLite automated database generator
│   └── waste_management.db                  # Ready-to-use SQLite relational database
│
├── java-app/                                # Java Application Source Code
│   ├── compile_and_run.bat                  # Automated compilation & execution script (Windows)
│   ├── run.sh                               # Shell script (Linux / macOS)
│   └── src/com/municipal/waste/
│       ├── Main.java                        # Menu-driven interactive console application
│       ├── model/                           # Domain Entities (Area, Vehicle, Task, WasteRecord, Enums)
│       ├── dao/                             # Data Access Layer & JDBC/Memory Store
│       ├── service/                         # Business Logic, Capacity Checking & Reports
│       └── util/                            # Input Validators & Custom Exceptions
│
├── python-analysis/                         # Data Science & Visualizations
│   ├── waste_analysis.py                    # End-to-end Python preprocessing & visualization script
│   ├── waste_analysis.ipynb                 # Interactive Jupyter Notebook
│   ├── preprocessed_waste_data.csv          # Cleaned dataset exported by pipeline
│   └── visualizations/                      # High-resolution generated charts (.png)
│       ├── 1_area_wise_waste_distribution.png
│       ├── 2_waste_category_breakdown.png
│       ├── 3_vehicle_capacity_utilization.png
│       ├── 4_daily_collection_trends.png
│       ├── 5_population_vs_waste_correlation.png
│       └── 6_collection_status_overview.png
│
├── web-app/                                 # Flask Web Application & REST API
│   ├── app.py                               # Flask server with REST API routes
│   ├── templates/index.html                 # Interactive web dashboard with Chart.js
│   └── static/images/                       # Static assets & generated visualizations
│
├── website/                                 # Standalone zero-install web version
│   └── index.html                           # Double-clickable direct browser dashboard
│
├── start_website.bat                        # One-click web server & browser launcher
├── sample-outputs/                          # Verifiable Test Execution Logs
│   ├── java_execution_output.txt            # Console output from Java application
│   └── python_analysis_output.txt           # Console output from Python EDA pipeline
│
└── README.md                                # Master Project Documentation
```

---

## 4. Setup and Execution Instructions

### Prerequisites
* **Java**: JDK 11 or higher (`javac` and `java`).
* **Python**: Python 3.9+ (`pandas`, `numpy`, `matplotlib`, `seaborn`, `flask`).

---

### Step 1: Running the Interactive Web Portal
You can run the web dashboard in two ways:
* **Option A (Full Flask Web Server with Live SQLite Database)**:
  Double-click `start_website.bat` or run:
  ```bash
  python web-app/app.py
  ```
  Open `http://localhost:5000` in your web browser.
* **Option B (Direct Zero-Server HTML5 View)**:
  Simply double-click `website/index.html` to open the full interactive portal directly in Google Chrome, Microsoft Edge, or Firefox!

---

### Step 2: Database Setup
The SQLite database is pre-built at `database/waste_management.db`. To recreate or inspect it:
```bash
cd database
python init_db.py
```
*(For MySQL users, execute `database/schema.sql` followed by `database/sample_data.sql` in MySQL Workbench or CLI).*

---

### Step 3: Running the Java Application
**On Windows:**
Double-click `compile_and_run.bat` or run:
```cmd
cd java-app
compile_and_run.bat
```

**Automated Demo Mode:**
To instantly execute and verify all functional requirements without manual typing:
```cmd
compile_and_run.bat --demo
```

---

### Step 3: Running the Python Data Science & Visualization Pipeline
```bash
cd python-analysis
python waste_analysis.py
```
Or open and run the notebook:
```bash
jupyter notebook waste_analysis.ipynb
```
All charts will be generated and saved to `python-analysis/visualizations/`.

---

## 5. Visualizations & Environmental Insights

The Python analysis produces 6 key visualizations:

1. **Area-wise Waste Distribution (`1_area_wise_waste_distribution.png`)**: Highlights top waste-producing municipal wards (Surampatti Junction Colony and Veerappanchatram Town lead total waste volume).
2. **Waste Category Proportions (`2_waste_category_breakdown.png`)**: Reveals that **Biodegradable/Organic waste accounts for ~58%** of municipal output, followed by **Recyclable (28%)**, **Non-Recyclable (12%)**, and **Hazardous (2%)**.
3. **Vehicle Fleet Utilization (`3_vehicle_capacity_utilization.png`)**: Box plots demonstrate that Compactor Heavy Trucks run at ~88-94% capacity, indicating high operational efficiency.
4. **Daily Trends (`4_daily_collection_trends.png`)**: Multi-category time-series line plot illustrating daily variation and post-weekend collection surges.
5. **Population vs. Waste Correlation (`5_population_vs_waste_correlation.png`)**: Strong positive Pearson correlation ($r \approx 0.82$) confirming waste load scale directly with ward density.
6. **Task Completion Status (`6_collection_status_overview.png`)**: Operational bar chart displaying high task completion rates ($> 84\%$) with minimal backlog.

---

## 6. Software Testing Summary

* **Unit Testing**: Entity attribute validation, population $> 0$, non-negative waste weights.
* **Boundary Value Analysis**: Testing $0\text{ kg}$, $95\%$ payload, $100\%$ rated vehicle capacity, and overload states ($>100\%$).
* **Integration Testing**: Persistent CRUD operations between Java DAO and SQLite; dataset ingestion into Pandas DataFrames.
* **Pass Rate**: $13 / 13$ Test Cases Passed ($100\%$). Complete testing matrix documented in [TESTING_AND_TEST_CASES.md](docs/TESTING_AND_TEST_CASES.md).

---

## 7. Author Information
* **Name:** DHARANI K K
* **Register Number:** 23
* **Class:** 25IT3A&B
* **Department:** Information Technology
* **College:** Velalar College of Engineering and Technology, Erode - 12
