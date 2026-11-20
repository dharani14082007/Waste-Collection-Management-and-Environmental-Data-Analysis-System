# Entity-Relationship (ER) Diagram Specification
## Municipal Waste Collection Management and Environmental Data Analysis System
**Author:** DHARANI K K  
**Register No.:** 23  
**Class:** 25IT3A&B  
**Institution:** Velalar College of Engineering and Technology, Erode - 12  
**Department:** Department of Information Technology  
**Subject Code & Title:** 25ITT31 - Database Management Systems  
**Assessment Scenario:** Scenario 23  

---

## 1. Visual ER Diagram

![Entity-Relationship Diagram](er_diagram.png)

*(A high-resolution version is rendered above and also available at [`docs/er_diagram.png`](er_diagram.png) and within the Web Dashboard).*

---

## 2. Mermaid Crow's Foot ER Diagram

```mermaid
erDiagram
    RESIDENTIAL_AREAS ||--o{ COLLECTION_TASKS : "receives (1:N)"
    VEHICLES ||--o{ COLLECTION_TASKS : "operated_by (1:N)"
    COLLECTION_TASKS ||--|{ WASTE_RECORDS : "contains (1:N)"
    WASTE_CATEGORIES ||--o{ WASTE_RECORDS : "classified_as (1:N)"

    RESIDENTIAL_AREAS {
        int area_id PK "Unique identifier for area"
        string area_name "Name of municipal zone"
        string ward_location "Ward number and zone"
        int population "Citizen headcount (>0)"
        datetime created_at "Registration timestamp"
    }

    VEHICLES {
        int vehicle_id PK "Unique identifier for vehicle"
        string vehicle_number UK "RTO Registration (e.g. TN-33-AX-1011)"
        string vehicle_type "Compactor, Tipper, Electric Cart"
        decimal capacity_kg "Certified load rating in kg (>0)"
        string operational_status "AVAILABLE, ON_DUTY, MAINTENANCE"
        date last_maintenance "Last service check date"
    }

    COLLECTION_TASKS {
        int task_id PK "Unique task schedule sequence"
        int area_id FK "References residential_areas(area_id)"
        int vehicle_id FK "References vehicles(vehicle_id)"
        date scheduled_date "Scheduled execution date"
        string status "SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED"
        datetime created_at "Schedule creation timestamp"
    }

    WASTE_CATEGORIES {
        int category_id PK "Category classification key"
        string category_name UK "Biodegradable, Recyclable, Non-Recyclable, Hazardous"
        string description "Category sorting criteria and items"
    }

    WASTE_RECORDS {
        int record_id PK "Unique waste measurement record"
        int task_id FK "References collection_tasks(task_id)"
        int category_id FK "References waste_categories(category_id)"
        decimal quantity_kg "Weighed waste load in kilograms (>=0)"
        datetime recorded_at "Weighment timestamp"
    }
```

---

## 3. Structural Components of the ER Model

### 3.1 Entity Sets

1. **`RESIDENTIAL_AREAS` (Strong Entity)**:
   - Represents the physical residential neighborhoods and administrative wards under municipal coverage.
   - **Key Attribute**: `area_id` (Primary Key).
   - **Descriptive Attributes**: `area_name`, `ward_location`, `population`.

2. **`VEHICLES` (Strong Entity)**:
   - Represents the municipal sanitation fleet deployed for waste collection and transport.
   - **Key Attribute**: `vehicle_id` (Primary Key), `vehicle_number` (Candidate / Alternate Key - Unique).
   - **Descriptive Attributes**: `vehicle_type`, `capacity_kg`, `operational_status`, `last_maintenance`.

3. **`COLLECTION_TASKS` (Strong Entity)**:
   - Represents the operational assignment and scheduling of a specific vehicle to a residential zone for a specific date.
   - **Key Attribute**: `task_id` (Primary Key).
   - **Foreign Keys**: `area_id`, `vehicle_id`.
   - **Descriptive Attributes**: `scheduled_date`, `status`, `created_at`.

4. **`WASTE_CATEGORIES` (Strong Entity / Lookup Domain)**:
   - Represents standard waste classification streams per environmental solid waste management rules.
   - **Key Attribute**: `category_id` (Primary Key), `category_name` (Unique).
   - **Descriptive Attribute**: `description`.

5. **`WASTE_RECORDS` (Composite Entity / Weak Associative Entity)**:
   - Represents individual weighed loads of segregated waste collected during a specific task trip.
   - **Key Attribute**: `record_id` (Primary Key).
   - **Foreign Keys**: `task_id`, `category_id`.
   - **Descriptive Attributes**: `quantity_kg`, `recorded_at`.

---

## 4. Relationship Sets and Cardinality Constraints

| Relationship Name | Connecting Entities | Cardinality Ratio | Participation Constraint | Description |
|:---|:---|:---:|:---:|:---|
| **`SCHEDULED_FOR`** | `RESIDENTIAL_AREAS` $\rightarrow$ `COLLECTION_TASKS` | **1 : N** | Partial (Area) / Total (Task) | One residential area can have many collection tasks over time; every task must be assigned to exactly one area. |
| **`OPERATED_BY`** | `VEHICLES` $\rightarrow$ `COLLECTION_TASKS` | **1 : N** | Partial (Vehicle) / Total (Task) | One vehicle can execute multiple collection tasks across different dates; each task must be operated by one vehicle. |
| **`CONTAINS`** | `COLLECTION_TASKS` $\rightarrow$ `WASTE_RECORDS` | **1 : N** | Partial (Task) / Total (Record) | A completed task contains multiple waste records (for segregated categories); each waste record belongs to one task. |
| **`CLASSIFIED_AS`** | `WASTE_CATEGORIES` $\rightarrow$ `WASTE_RECORDS` | **1 : N** | Partial (Category) / Total (Record) | One waste category classifies multiple historical records across tasks; each record has exactly one category. |

---

## 5. Participation Constraints & Business Rules

1. **Mandatory Relationships**:
   - A `COLLECTION_TASK` cannot exist without both a valid `RESIDENTIAL_AREA` and a valid `VEHICLE` (Enforced via `NOT NULL` Foreign Keys and `ON DELETE CASCADE`).
   - A `WASTE_RECORD` cannot exist without a parent `COLLECTION_TASK` and a valid `WASTE_CATEGORY`.
2. **Vehicle Payload Integrity**:
   - The sum of `quantity_kg` across all `WASTE_RECORDS` belonging to a `task_id` must be monitored against the assigned `VEHICLE.capacity_kg`.
   - If $\sum \text{quantity\_kg} > \text{capacity\_kg}$, the system flags an **Overload Alert**.
3. **Domain Constraints**:
   - `population > 0`
   - `capacity_kg > 0`
   - `quantity_kg >= 0`
   - `status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')`

---

## 6. Relational Mapping Table

| Relation Name | Primary Key (PK) | Foreign Keys (FK) | Candidate Keys | Check Constraints |
|:---|:---|:---|:---|:---|
| `residential_areas` | `area_id` | *None* | `area_name, ward_location` | `population > 0` |
| `vehicles` | `vehicle_id` | *None* | `vehicle_number` | `capacity_kg > 0` |
| `waste_categories` | `category_id` | *None* | `category_name` | *None* |
| `collection_tasks` | `task_id` | `area_id` $\rightarrow$ `residential_areas`,<br>`vehicle_id` $\rightarrow$ `vehicles` | *None* | `status IN (...)` |
| `waste_records` | `record_id` | `task_id` $\rightarrow$ `collection_tasks`,<br>`category_id` $\rightarrow$ `waste_categories` | *None* | `quantity_kg >= 0` |
