# Software Requirements Specification (SRS)
## Waste Collection Management and Environmental Data Analysis System
**Academic Year:** 2026 - 2027 (Odd Semester)  
**Course Code & Title:** 25ITT33 - Software Engineering  
**Department:** Department of Information Technology  
**Institution:** Velalar College of Engineering and Technology, Erode - 12  
**Student Name:** DHARANI K K  
**Register No.:** 23  
**Class:** 25IT3A&B  
**Scenario:** Scenario 23  

---

## 1. Introduction

### 1.1 Purpose
The purpose of this document is to define the functional, non-functional, and operational requirements for the **Waste Collection Management and Environmental Data Analysis System (WCM-EDAS)**. This system is designed for municipal administrations to replace error-prone, manual record-keeping with an automated, data-driven platform that manages waste collection schedules, vehicle fleets, residential areas, and categorised waste disposal metrics.

### 1.2 Scope of the System
The system encompasses:
- Management of residential sectors, population densities, and geographical allocations.
- Waste collection vehicle fleet tracking, type specification, and capacity monitoring.
- Scheduling, dispatching, and status auditing of collection activities.
- Multi-category waste quantification (Biodegradable, Recyclable, Non-Recyclable, Hazardous).
- Database-backed reporting on area generation loads, vehicle utilization efficiency, and completion percentages.
- Python-powered environmental analytics and visual pattern discovery to support green policy decisions and route optimization.

### 1.3 Definitions, Acronyms, and Abbreviations
- **WCM-EDAS**: Waste Collection Management and Environmental Data Analysis System
- **SRS**: Software Requirements Specification
- **DBMS**: Database Management System
- **CRUD**: Create, Read, Update, Delete
- **EDA**: Exploratory Data Analysis
- **1NF / 2NF / 3NF**: First, Second, and Third Normal Forms
- **BCNF**: Boyce-Codd Normal Form
- **JDBC**: Java Database Connectivity

### 1.4 References
- IEEE Std 830-1998, Recommended Practice for Software Requirements Specifications.
- Pressman, R. S., *Software Engineering: A Practitioner's Approach*, McGraw-Hill.
- Syllabus for 25ITT33 (Software Engineering), 25ITT31 (DBMS), 25ITT32 (Java Programming), 25ITC31 (Foundations of Data Science).

---

## 2. Overall Description

### 2.1 Product Perspective
The WCM-EDAS is a modular multi-tier municipal management software:
1. **Application Layer (Java)**: Business logic, entity management, data validation, console interface, and transaction routing.
2. **Persistence Layer (SQL / DBMS)**: Relational schema ensuring ACID compliance, relational integrity, triggers, and views.
3. **Analytics & Visualization Layer (Python / Pandas / Matplotlib / Seaborn)**: Preprocessing, statistical aggregation, exploratory data analysis, and chart generation.

### 2.2 Product Functions
- Maintain residential area registry (Area ID, Name, Ward/Location, Population).
- Maintain vehicle registry (Vehicle ID, Type, Volumetric/Tonnage Capacity).
- Record and monitor daily collection schedules and assignment to vehicles.
- Record category-segregated waste weights (Biodegradable, Recyclable, Non-Recyclable).
- Real-time vehicle payload utilization checking (preventing vehicle overloading).
- Automated reporting on waste distribution, vehicle efficiency, and pending activities.
- Data export and exploratory analysis generating visual trends for municipal planners.

### 2.3 User Classes and Characteristics
1. **Sanitation Supervisor / Administrator**:
   - Manages area details, vehicle allocations, and schedules.
   - Monitors completion statuses and vehicle payload utilization.
2. **Field Vehicle Operator**:
   - Updates task status (Scheduled -> In-Progress -> Completed).
   - Inputs measured waste weights collected at residential stops.
3. **Environmental Analyst / Municipal Commissioner**:
   - Reviews statistical reports, per-capita waste indicators, and Python charts.
   - Plans future fleet expansions and recycling incentives.

### 2.4 Operating Environment
- **Operating System**: Cross-platform (Windows 10/11, Linux, macOS).
- **Runtime Engines**: Java SE 11+ (JDK), Python 3.9+ runtime.
- **Database Engine**: Relational SQL Engine (MySQL / SQLite).

### 2.5 Design and Implementation Constraints
- Compliance with relational normalization principles up to 3NF/BCNF.
- Strong object-oriented design adhering to SOLID principles.
- Strict validation on waste inputs (non-negative weights, capacity overflow alerts).
- Pure open-source stack using standard data science and database libraries.

---

## 3. Specific Functional Requirements

### FR-01: Residential Area Management
- **FR-01.1**: The system shall allow the administrator to create, update, view, and delete residential areas.
- **FR-01.2**: Each area must record Area ID (Primary Key), Area Name, Ward/Location, and Population count.
- **FR-01.3**: The system shall validate that population is a positive integer greater than zero.

### FR-02: Waste Collection Vehicle Fleet Management
- **FR-02.1**: The system shall record vehicle details including Vehicle ID (Primary Key), Vehicle Type (e.g., Compactor Truck, Tipper, Electric Cart), and Maximum Carrying Capacity in kilograms (kg).
- **FR-02.2**: The system shall maintain vehicle operational status (Available, On-Duty, Under-Maintenance).

### FR-03: Collection Activity Scheduling and Assignment
- **FR-03.1**: The system shall assign a specific vehicle and driver to a residential area for a scheduled date.
- **FR-03.2**: The system shall track the operational status across four states: `SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.
- **FR-03.3**: The system shall prevent assigning vehicles whose status is `Under-Maintenance`.

### FR-04: Waste Quantification and Categorization
- **FR-04.1**: The system shall log segregated waste collections under standardized categories:
  - Biodegradable (Organic/Kitchen waste)
  - Recyclable (Paper, Plastic, Metal, Glass)
  - Non-Recyclable (Inert, Sanitary, Construction debris)
- **FR-04.2**: The system shall record collected quantities in kilograms with decimal precision.
- **FR-04.3**: The system shall dynamically compute total collected waste per trip and verify whether it exceeds the assigned vehicle's rated capacity, raising a warning when payload exceeds 95%.

### FR-05: Analytical Reporting (Application & SQL)
- **FR-05.1**: Generate area-wise waste generation reports with per-capita calculation:
  $$\text{Per-Capita Waste} = \frac{\text{Total Waste (kg)}}{\text{Area Population}}$$
- **FR-05.2**: Generate vehicle fleet utilization reports:
  $$\text{Utilization \%} = \left( \frac{\text{Collected Waste (kg)}}{\text{Vehicle Rated Capacity (kg)}} \right) \times 100$$
- **FR-05.3**: Summary of task completion rates and pending collection alerts.

### FR-06: Python Environmental Data Retrieval and Visualization
- **FR-06.1**: Direct automated extraction of collection datasets from the database.
- **FR-06.2**: Data preprocessing pipeline: null checking, date parsing, outlier detection using IQR/Z-score, and feature engineering.
- **FR-06.3**: Generation of analytical visualizations:
  1. Area-wise Total Waste Generation (Bar Chart).
  2. Waste Segregation Category Proportions (Donut/Pie Chart).
  3. Vehicle Capacity Utilization Distribution (Box Plot & Bar Plot).
  4. Time-Series Daily Waste Collection Trend (Line Chart).
  5. Correlation between Residential Population and Waste Volume (Scatter Plot with Regression Line).
  6. Task Completion Status Breakdown (Categorical Count Plot).

---

## 4. Non-Functional Requirements

### 4.1 Performance Requirements
- **Response Time**: Core database CRUD operations must execute within $\le 100\text{ ms}$.
- **Analytics Speed**: Python EDA pipeline and chart rendering must complete within $\le 5\text{ seconds}$ for datasets up to 100,000 records.

### 4.2 Security Requirements
- Role-based input boundaries ensuring unauthorized personnel cannot delete historical logs.
- SQL injection prevention through parameterized prepared statements in Java and Python.

### 4.3 Reliability and Availability
- Zero data loss: All operations persist to the database with transactional atomicity (ACID).
- Graceful exception handling for file errors, invalid input types, and database connectivity interruptions.

### 4.4 Maintainability and Portability
- Decoupled Model-DAO-Service architectural pattern in Java.
- Platform-independent SQL scripts standard across ANSI SQL / MySQL / SQLite engines.
- Documented Python code following PEP-8 style guidelines.

---

## 5. Verification & Acceptance Criteria
- 100% compliance with Scenario 23 functional requirements.
- Clean execution of Java application with verifiable console inputs and outputs.
- Normalized relational database schema satisfying 1NF, 2NF, and 3NF.
- Successful generation of 6 distinct high-resolution analytical visual charts.
- Verified test suite with documented test cases, expected outcomes, and actual results.
