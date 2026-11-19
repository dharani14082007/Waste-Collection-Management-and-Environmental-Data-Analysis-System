# Software Testing and Quality Assurance Report
## Waste Collection Management and Environmental Data Analysis System
**Author:** DHARANI K K (Register No.: 23)  
**Subject:** 25ITT33 - Software Engineering  
**Class:** 25IT3A&B  

---

## 1. Testing Strategy and Objectives

The objective of testing the **Waste Collection Management and Environmental Data Analysis System** is to ensure functional reliability, data integrity, robust validation, and failure resistance.

### 1.1 Scope of Testing
- **Unit Testing**: Testing individual Java methods, entity constructors, and calculation routines (e.g., payload utilization %, per-capita formula).
- **Integration Testing**: Verifying data persistence from Java models to Database tables and data extraction from SQL into Python Pandas DataFrames.
- **Validation & Boundary Value Testing (Black-Box)**: Testing boundary values for population, vehicle capacity limits, negative weights, and invalid status values.
- **System & End-to-End Testing**: Testing complete user workflows from area registration to schedule creation, waste logging, overload alerts, and Python chart exports.

---

## 2. Test Design Techniques Applied

### 2.1 Equivalence Partitioning (EP)
Input domains are divided into valid and invalid equivalence classes:
- **Population**:
  - Valid: $[1, \infty)$ (e.g., 25,000)
  - Invalid: $(-\infty, 0]$ (e.g., $-500, 0$)
- **Waste Quantity (kg)**:
  - Valid: $[0.0, \text{VehicleCapacity}]$
  - Invalid: $(< 0.0)$, $(> \text{VehicleCapacity})$ (triggers warning/exception)
- **Collection Status**:
  - Valid: `SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`
  - Invalid: Any undefined string (e.g., `DONE`, `FINISHED`)

### 2.2 Boundary Value Analysis (BVA)
Testing values at edges:
- Vehicle Payload: At $0\text{ kg}$, $50\%\text{ capacity}$, $95\%\text{ capacity}$ (near overload), $100\%\text{ capacity}$ (maximum), and $> 100\%\text{ capacity}$ (overload triggered).
- Population: Boundary at $0$ (rejected) and $1$ (accepted).

---

## 3. Comprehensive Test Cases and Results Matrix

| Test Case ID | Test Category | Test Objective | Test Input Data | Expected Result | Actual Result | Status |
|:---|:---|:---|:---|:---|:---|:---:|
| **TC-01** | Unit / Validation | Register area with valid attributes | Name: "Perundurai Road", Ward: "Ward 4", Population: 18500 | Area created with generated ID; persists in DB | Area created with ID 1; persisted | **PASS** |
| **TC-02** | Unit / Boundary | Attempt to register area with negative population | Name: "Test Area", Ward: "Ward 9", Population: -120 | Rejection with `ValidationException: Population must be greater than zero` | Rejection handled gracefully; error logged | **PASS** |
| **TC-03** | Unit / Validation | Register vehicle with valid capacity | Number: "TN-33-AB-1234", Type: "Compactor", Capacity: 5000.0 kg | Vehicle registered with status `AVAILABLE` | Vehicle successfully registered | **PASS** |
| **TC-04** | Unit / Boundary | Register vehicle with zero or negative capacity | Number: "TN-33-XY-9999", Capacity: -500.0 kg | Rejection with `ValidationException: Capacity must be positive` | Rejected with validation message | **PASS** |
| **TC-05** | Integration | Schedule collection task linking valid Area and Vehicle | Area ID: 1, Vehicle ID: 1, Date: "2026-11-20" | Task created with status `SCHEDULED`; foreign keys resolved | Task created with ID 101; status `SCHEDULED` | **PASS** |
| **TC-06** | Validation | Attempt scheduling with non-existent Area ID | Area ID: 9999, Vehicle ID: 1, Date: "2026-11-20" | Rejection: Area not found | Throws exception; schedule blocked | **PASS** |
| **TC-07** | Unit / Business Logic | Record valid waste quantities (Bio, Recyclable, Non-Recyclable) | Task ID: 101, Category: `BIODEGRADABLE`, Quantity: 1450.5 kg | Quantity logged; task cumulative weight updated | 1450.5 kg logged; aggregate updated | **PASS** |
| **TC-08** | Business Logic | Vehicle capacity overload detection | Vehicle Cap: 2000 kg; Attempted Waste Log: 2200 kg | Overload warning flagged to supervisor; payload = 110% | Warning emitted: Payload exceeded by 200 kg | **PASS** |
| **TC-09** | Integration | Transition task status from `SCHEDULED` to `COMPLETED` | Task ID: 101, Status: `COMPLETED` | Status successfully updated in database | Status updated; verified via DAO query | **PASS** |
| **TC-10** | System / Reporting | Calculate area-wise total waste and per-capita rate | Area Pop: 10,000; Total Waste: 3,500 kg | Per-capita waste computed accurately as 0.35 kg/person | Value = 0.35 kg/person | **PASS** |
| **TC-11** | Integration (Python) | Retrieve collection dataset via Python connector | SQL query execution from Python | Clean DataFrame loaded with 50+ rows and expected columns | 52 rows loaded; 0 missing critical keys | **PASS** |
| **TC-12** | Analytics (Python) | Check outlier detection and data cleaning | Null values or negative anomalies in raw logs | Anomalous rows filtered or imputed; log reported | Cleaned DataFrame ready for visualization | **PASS** |
| **TC-13** | System / Visual | Generate 6 distinct publication-grade charts | Cleaned Pandas DataFrame | 6 `.png` charts generated and saved in `visualizations/` | All 6 charts exported successfully | **PASS** |

---

## 4. White-Box Testing (Logic & Branch Coverage)

The core method `WasteManagementService.recordWasteCollection()` was evaluated for full branch coverage:
- **Branch 1**: Task status is `CANCELLED` $\rightarrow$ Rejection.
- **Branch 2**: Quantity $\le 0$ $\rightarrow$ Validation exception thrown.
- **Branch 3**: Added weight $\le$ Rated vehicle capacity $\rightarrow$ Normal logging with utilization %.
- **Branch 4**: Added weight $>$ Rated vehicle capacity $\rightarrow$ Warning logged + overload status recorded.

All branches achieved **100% path coverage** during automated unit testing.

---

## 5. Software Quality Metrics

- **Defect Density**: $0$ critical defects per KLOC in final release.
- **Code Modularity**: Clear separation into `model`, `dao`, `service`, `util` layers.
- **Test Pass Rate**: $13 / 13$ test cases passed ($100\%$).
- **Reliability Assessment**: System withstands invalid inputs without abnormal termination.
