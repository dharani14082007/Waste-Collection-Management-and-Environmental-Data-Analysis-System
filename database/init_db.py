import sqlite3
import os

DB_PATH = os.path.join(os.path.dirname(__file__), "waste_management.db")
SCHEMA_PATH = os.path.join(os.path.dirname(__file__), "schema.sql")
DATA_PATH = os.path.join(os.path.dirname(__file__), "sample_data.sql")

def initialize_database():
    if os.path.exists(DB_PATH):
        os.remove(DB_PATH)
    
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    
    with open(SCHEMA_PATH, 'r', encoding='utf-8') as f:
        schema_sql = f.read()
        cursor.executescript(schema_sql)
        
    with open(DATA_PATH, 'r', encoding='utf-8') as f:
        data_sql = f.read()
        cursor.executescript(data_sql)
        
    conn.commit()
    
    # Verify counts
    cursor.execute("SELECT COUNT(*) FROM residential_areas")
    areas_count = cursor.fetchone()[0]
    cursor.execute("SELECT COUNT(*) FROM vehicles")
    vehicles_count = cursor.fetchone()[0]
    cursor.execute("SELECT COUNT(*) FROM collection_tasks")
    tasks_count = cursor.fetchone()[0]
    cursor.execute("SELECT COUNT(*) FROM waste_records")
    records_count = cursor.fetchone()[0]
    
    conn.close()
    print(f"Database successfully initialized at: {DB_PATH}")
    print(f"Summary: {areas_count} areas, {vehicles_count} vehicles, {tasks_count} tasks, {records_count} waste records.")

if __name__ == "__main__":
    initialize_database()
