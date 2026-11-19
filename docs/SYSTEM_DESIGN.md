# System Design and Architecture Specification
## Waste Collection Management and Environmental Data Analysis System
**Author:** DHARANI K K (Register No.: 23)  
**Academic Assessment:** 2026 - 2027 (Odd Semester)  
**Subject:** 25ITT33 - Software Engineering  

---

## 1. Overall System Architecture

The system follows a multi-tier modular architecture designed for high cohesion and loose coupling.

```mermaid
flowchart TD
    subgraph UI_Layer ["Presentation / User Interaction Layer"]
        CLI["Java Console Interface (Menu Driven)"]
        Jupyter["Jupyter Notebook / Python CLI Dashboard"]
    end

    subgraph Business_Layer ["Application & Business Logic Layer (Java)"]
        Service["WasteManagementService & ReportService"]
        Validation["InputValidator & Custom Exceptions"]
        Model["Domain Models (Area, Vehicle, CollectionTask, WasteRecord)"]
    end

    subgraph Data_Access_Layer ["Data Access Layer (DAO & Python Connector)"]
        JavaDAO["AreaDAO, VehicleDAO, CollectionDAO (JDBC)"]
        PythonPipeline["Python Data Retrieval & Preprocessing Pipeline (Pandas/SQLAlchemy)"]
    end

    subgraph Persistence_Layer ["Persistence Layer (RDBMS)"]
        DB[("Relational Database (MySQL / SQLite)\nTables: areas, vehicles, collection_tasks, waste_records")]
    end

    subgraph Analytics_Layer ["Analytics & Visualization Layer (Python)"]
        EDA["Exploratory Data Analysis Engine"]
        Charts["Visualizations (Matplotlib & Seaborn)"]
    end

    CLI --> Validation
    Validation --> Service
    Service --> Model
    Service --> JavaDAO
    JavaDAO --> DB

    DB --> PythonPipeline
    PythonPipeline --> EDA
    EDA --> Charts
    Jupyter -.-> PythonPipeline
```

---

## 2. Use Case Diagram

The primary actors interacting with the system are:
1. **Municipal Administrator / Supervisor**: Configures master data, schedules assignments, monitors reports.
2. **Field Vehicle Operator**: Executes collection routes and records waste quantities.
3. **Environmental Analyst**: Analyzes waste generation trends and environmental indicators.

```mermaid
flowchart LR
    Admin(["Municipal Administrator"])
    Driver(["Vehicle Operator"])
    Analyst(["Environmental Analyst"])

    subgraph SystemBoundary ["Waste Collection & Environmental Analysis System"]
        UC1(["UC-01: Manage Residential Areas"])
        UC2(["UC-02: Manage Collection Vehicles"])
        UC3(["UC-03: Schedule Waste Collection"])
        UC4(["UC-04: Assign Vehicles to Routes"])
        UC5(["UC-05: Record Collected Waste Quantities"])
        UC6(["UC-06: Categorize Waste (Bio/Recycle/Non-Recycle)"])
        UC7(["UC-07: Track Task Completion Status"])
        UC8(["UC-08: Check Vehicle Overload Alerts"])
        UC9(["UC-09: Generate Operational Reports"])
        UC10(["UC-10: Preprocess & Clean Datasets"])
        UC11(["UC-11: Generate Environmental Visualizations"])
    end

    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC4
    Admin --> UC8
    Admin --> UC9

    Driver --> UC5
    Driver --> UC6
    Driver --> UC7

    Analyst --> UC9
    Analyst --> UC10
    Analyst --> UC11
```

---

## 3. Class Diagram

The class diagram demonstrates Object-Oriented principles (Encapsulation, Inheritance, Association, Aggregation):

```mermaid
classDiagram
    class Area {
        -int areaId
        -String areaName
        -String wardLocation
        -int population
        +getAreaId() int
        +getAreaName() String
        +getWardLocation() String
        +getPopulation() int
        +setPopulation(int) void
        +toString() String
    }

    class Vehicle {
        -int vehicleId
        -String vehicleNumber
        -String vehicleType
        -double capacityKg
        -String status
        +getVehicleId() int
        +getVehicleNumber() String
        +getVehicleType() String
        +getCapacityKg() double
        +getStatus() String
        +setStatus(String) void
        +toString() String
    }

    class CollectionStatus {
        <<enumeration>>
        SCHEDULED
        IN_PROGRESS
        COMPLETED
        CANCELLED
    }

    class WasteCategory {
        <<enumeration>>
        BIODEGRADABLE
        RECYCLABLE
        NON_RECYCLABLE
        HAZARDOUS
    }

    class WasteRecord {
        -int recordId
        -int taskId
        -WasteCategory category
        -double quantityKg
        -String recordedAt
        +getRecordId() int
        +getTaskId() int
        +getCategory() WasteCategory
        +getQuantityKg() double
        +toString() String
    }

    class CollectionTask {
        -int taskId
        -int areaId
        -int vehicleId
        -String scheduledDate
        -CollectionStatus status
        -List~WasteRecord~ wasteRecords
        +getTaskId() int
        +getAreaId() int
        +getVehicleId() int
        +getScheduledDate() String
        +getStatus() CollectionStatus
        +setStatus(CollectionStatus) void
        +addWasteRecord(WasteRecord) void
        +getTotalWasteKg() double
        +toString() String
    }

    class WasteManagementService {
        -AreaDAO areaDAO
        -VehicleDAO vehicleDAO
        -CollectionDAO collectionDAO
        +registerArea(Area) boolean
        +registerVehicle(Vehicle) boolean
        +scheduleCollection(int, int, String) CollectionTask
        +updateTaskStatus(int, CollectionStatus) boolean
        +recordWasteCollection(int, WasteCategory, double) boolean
        +checkVehicleCapacityWarning(int) double
    }

    class ReportService {
        +getAreaWiseSummary() List
        +getVehicleUtilizationReport() List
        +getCompletionMetrics() Map
    }

    Area "1" <-- "0..*" CollectionTask : assigned to
    Vehicle "1" <-- "0..*" CollectionTask : operated by
    CollectionTask "1" *-- "0..*" WasteRecord : contains
    CollectionTask o-- CollectionStatus : has status
    WasteRecord o-- WasteCategory : has category
    WasteManagementService --> Area : manages
    WasteManagementService --> Vehicle : manages
    WasteManagementService --> CollectionTask : orchestrates
    ReportService --> WasteManagementService : queries metrics
```

---

## 4. Sequence Diagram: Waste Collection & Overload Verification

```mermaid
sequenceDiagram
    autonumber
    actor Driver as Vehicle Operator
    participant UI as Console Application
    participant Service as WasteManagementService
    participant DAO as CollectionDAO
    participant DB as SQL Database

    Driver->>UI: Select "Record Waste" (Task ID, Category, Quantity)
    UI->>Service: recordWasteCollection(taskId, category, quantityKg)
    Service->>DAO: getTaskById(taskId)
    DAO->>DB: SELECT * FROM collection_tasks WHERE task_id = ?
    DB-->>DAO: Task Record
    DAO-->>Service: Task Object
    Service->>DAO: getVehicleById(task.getVehicleId())
    DAO->>DB: SELECT * FROM vehicles WHERE vehicle_id = ?
    DB-->>DAO: Vehicle Record
    DAO-->>Service: Vehicle Object
    Service->>Service: Calculate current payload + new quantity
    alt Payload exceeds Vehicle Capacity
        Service-->>UI: Warning: Vehicle capacity exceeded! (Overload Flagged)
    end
    Service->>DAO: insertWasteRecord(record)
    DAO->>DB: INSERT INTO waste_records (...)
    DB-->>DAO: Success
    DAO-->>Service: true
    Service-->>UI: Waste logged successfully
    UI-->>Driver: Display confirmation & payload utilization %
```
