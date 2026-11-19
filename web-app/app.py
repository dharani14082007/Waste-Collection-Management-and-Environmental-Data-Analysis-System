"""
Flask Web Application for Municipal Waste Collection Management System
Author: DHARANI K K (Register No.: 23)
Department of Information Technology, Velalar College of Engineering and Technology
"""

import os
import sqlite3
from flask import Flask, render_template, request, jsonify, send_from_directory

app = Flask(__name__, template_folder='templates', static_folder='static')

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.path.join(BASE_DIR, "..", "database", "waste_management.db")

def get_db():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/static/images/<path:filename>')
def serve_image(filename):
    return send_from_directory(os.path.join(BASE_DIR, 'static', 'images'), filename)

# ----------------- REST API Endpoints -----------------

@app.route('/api/dashboard', methods=['GET'])
def get_dashboard_kpis():
    conn = get_db()
    c = conn.cursor()

    c.execute("SELECT COUNT(*) FROM residential_areas")
    total_areas = c.fetchone()[0]

    c.execute("SELECT COUNT(*) FROM vehicles")
    total_vehicles = c.fetchone()[0]

    c.execute("SELECT COUNT(*) FROM collection_tasks")
    total_tasks = c.fetchone()[0]

    c.execute("SELECT COUNT(*) FROM collection_tasks WHERE status = 'COMPLETED'")
    completed_tasks = c.fetchone()[0]

    c.execute("SELECT COALESCE(SUM(quantity_kg), 0) FROM waste_records")
    total_waste = round(c.fetchone()[0], 2)

    c.execute("""
        SELECT c.category_name, COALESCE(SUM(r.quantity_kg), 0) as total_kg
        FROM waste_categories c
        LEFT JOIN waste_records r ON c.category_id = r.category_id
        GROUP BY c.category_id, c.category_name
    """)
    category_summary = [dict(row) for row in c.fetchall()]

    c.execute("""
        SELECT a.area_name, a.population, COALESCE(SUM(r.quantity_kg), 0) as total_kg
        FROM residential_areas a
        LEFT JOIN collection_tasks t ON a.area_id = t.area_id
        LEFT JOIN waste_records r ON t.task_id = r.task_id
        GROUP BY a.area_id, a.area_name
        ORDER BY total_kg DESC
        LIMIT 6
    """)
    top_areas = [dict(row) for row in c.fetchall()]

    conn.close()
    return jsonify({
        'total_areas': total_areas,
        'total_vehicles': total_vehicles,
        'total_tasks': total_tasks,
        'completed_tasks': completed_tasks,
        'completion_rate': round((completed_tasks / total_tasks * 100) if total_tasks else 0, 1),
        'total_waste_kg': total_waste,
        'categories': category_summary,
        'top_areas': top_areas
    })

@app.route('/api/areas', methods=['GET', 'POST'])
def handle_areas():
    conn = get_db()
    c = conn.cursor()

    if request.method == 'POST':
        data = request.json or {}
        name = data.get('area_name', '').strip()
        ward = data.get('ward_location', '').strip()
        try:
            pop = int(data.get('population', 0))
        except ValueError:
            return jsonify({'error': 'Invalid population number'}), 400

        if not name or not ward or pop <= 0:
            return jsonify({'error': 'Area name, ward location, and positive population are required'}), 400

        c.execute("INSERT INTO residential_areas (area_name, ward_location, population) VALUES (?, ?, ?)",
                  (name, ward, pop))
        conn.commit()
        new_id = c.lastrowid
        conn.close()
        return jsonify({'message': 'Area registered successfully', 'area_id': new_id}), 201

    c.execute("SELECT area_id, area_name, ward_location, population FROM residential_areas ORDER BY area_id")
    areas = [dict(row) for row in c.fetchall()]
    conn.close()
    return jsonify(areas)

@app.route('/api/vehicles', methods=['GET', 'POST'])
def handle_vehicles():
    conn = get_db()
    c = conn.cursor()

    if request.method == 'POST':
        data = request.json or {}
        number = data.get('vehicle_number', '').strip()
        v_type = data.get('vehicle_type', '').strip()
        try:
            capacity = float(data.get('capacity_kg', 0))
        except ValueError:
            return jsonify({'error': 'Invalid capacity value'}), 400

        if not number or not v_type or capacity <= 0:
            return jsonify({'error': 'Vehicle number, type, and positive capacity are required'}), 400

        try:
            c.execute("INSERT INTO vehicles (vehicle_number, vehicle_type, capacity_kg, operational_status, last_maintenance) VALUES (?, ?, ?, 'AVAILABLE', DATE('now'))",
                      (number, v_type, capacity))
            conn.commit()
            new_id = c.lastrowid
            conn.close()
            return jsonify({'message': 'Vehicle registered successfully', 'vehicle_id': new_id}), 201
        except sqlite3.IntegrityError:
            conn.close()
            return jsonify({'error': 'Vehicle registration number already exists'}), 400

    c.execute("SELECT vehicle_id, vehicle_number, vehicle_type, capacity_kg, operational_status, last_maintenance FROM vehicles ORDER BY vehicle_id")
    vehicles = [dict(row) for row in c.fetchall()]
    conn.close()
    return jsonify(vehicles)

@app.route('/api/tasks', methods=['GET', 'POST'])
def handle_tasks():
    conn = get_db()
    c = conn.cursor()

    if request.method == 'POST':
        data = request.json or {}
        area_id = data.get('area_id')
        vehicle_id = data.get('vehicle_id')
        sched_date = data.get('scheduled_date', '').strip()

        if not area_id or not vehicle_id or not sched_date:
            return jsonify({'error': 'Area ID, Vehicle ID, and scheduled date are required'}), 400

        c.execute("INSERT INTO collection_tasks (area_id, vehicle_id, scheduled_date, status) VALUES (?, ?, ?, 'SCHEDULED')",
                  (area_id, vehicle_id, sched_date))
        conn.commit()
        new_id = c.lastrowid
        conn.close()
        return jsonify({'message': 'Collection scheduled successfully', 'task_id': new_id}), 201

    c.execute("""
        SELECT 
            t.task_id, t.scheduled_date, t.status,
            a.area_id, a.area_name, a.ward_location,
            v.vehicle_id, v.vehicle_number, v.vehicle_type, v.capacity_kg,
            COALESCE(SUM(r.quantity_kg), 0) AS total_collected_kg,
            ROUND((COALESCE(SUM(r.quantity_kg), 0) / v.capacity_kg) * 100, 1) AS utilization_pct
        FROM collection_tasks t
        JOIN residential_areas a ON t.area_id = a.area_id
        JOIN vehicles v ON t.vehicle_id = v.vehicle_id
        LEFT JOIN waste_records r ON t.task_id = r.task_id
        GROUP BY t.task_id
        ORDER BY t.task_id DESC
    """)
    tasks = [dict(row) for row in c.fetchall()]
    conn.close()
    return jsonify(tasks)

@app.route('/api/tasks/<int:task_id>/status', methods=['POST'])
def update_task_status(task_id):
    data = request.json or {}
    new_status = data.get('status', '').strip().upper()
    if new_status not in ['SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED']:
        return jsonify({'error': 'Invalid status'}), 400

    conn = get_db()
    c = conn.cursor()
    c.execute("UPDATE collection_tasks SET status = ? WHERE task_id = ?", (new_status, task_id))
    conn.commit()
    conn.close()
    return jsonify({'message': f'Status updated to {new_status}'})

@app.route('/api/waste', methods=['POST'])
def record_waste():
    data = request.json or {}
    task_id = data.get('task_id')
    category_id = data.get('category_id')
    try:
        quantity = float(data.get('quantity_kg', 0))
    except ValueError:
        return jsonify({'error': 'Invalid quantity value'}), 400

    if not task_id or not category_id or quantity <= 0:
        return jsonify({'error': 'Task ID, Category ID, and positive quantity are required'}), 400

    conn = get_db()
    c = conn.cursor()

    # Insert waste record
    c.execute("INSERT INTO waste_records (task_id, category_id, quantity_kg) VALUES (?, ?, ?)",
              (task_id, category_id, quantity))

    # Auto transition to IN_PROGRESS if scheduled
    c.execute("UPDATE collection_tasks SET status = 'IN_PROGRESS' WHERE task_id = ? AND status = 'SCHEDULED'",
              (task_id,))
    conn.commit()

    # Check vehicle capacity utilization
    c.execute("""
        SELECT v.capacity_kg, SUM(r.quantity_kg) as current_load
        FROM collection_tasks t
        JOIN vehicles v ON t.vehicle_id = v.vehicle_id
        JOIN waste_records r ON t.task_id = r.task_id
        WHERE t.task_id = ?
        GROUP BY v.capacity_kg
    """, (task_id,))
    row = c.fetchone()
    conn.close()

    if row:
        cap = row['capacity_kg']
        load = row['current_load']
        util_pct = round((load / cap) * 100, 1)
        overload = util_pct > 100.0
        warning = f"OVERLOAD WARNING! Vehicle load ({load} kg) exceeds rated capacity ({cap} kg)!" if overload else None
        return jsonify({
            'message': 'Waste recorded successfully',
            'current_load_kg': load,
            'capacity_kg': cap,
            'utilization_pct': util_pct,
            'is_overloaded': overload,
            'warning': warning
        }), 201

    return jsonify({'message': 'Waste recorded successfully'}), 201

@app.route('/api/reports/area-summary', methods=['GET'])
def report_area_summary():
    conn = get_db()
    c = conn.cursor()
    c.execute("SELECT * FROM v_area_waste_summary ORDER BY total_waste_kg DESC")
    rows = [dict(r) for r in c.fetchall()]
    conn.close()
    return jsonify(rows)

@app.route('/api/reports/vehicle-utilization', methods=['GET'])
def report_vehicle_utilization():
    conn = get_db()
    c = conn.cursor()
    c.execute("SELECT * FROM v_vehicle_utilization ORDER BY utilization_percentage DESC")
    rows = [dict(r) for r in c.fetchall()]
    conn.close()
    return jsonify(rows)

@app.route('/api/charts/trends', methods=['GET'])
def chart_trends():
    conn = get_db()
    c = conn.cursor()
    c.execute("""
        SELECT 
            t.scheduled_date,
            ROUND(SUM(r.quantity_kg), 2) AS total_kg,
            ROUND(SUM(CASE WHEN r.category_id = 1 THEN r.quantity_kg ELSE 0 END), 2) AS bio_kg,
            ROUND(SUM(CASE WHEN r.category_id = 2 THEN r.quantity_kg ELSE 0 END), 2) AS rec_kg,
            ROUND(SUM(CASE WHEN r.category_id = 3 THEN r.quantity_kg ELSE 0 END), 2) AS non_rec_kg,
            ROUND(SUM(CASE WHEN r.category_id = 4 THEN r.quantity_kg ELSE 0 END), 2) AS haz_kg
        FROM collection_tasks t
        JOIN waste_records r ON t.task_id = r.task_id
        GROUP BY t.scheduled_date
        ORDER BY t.scheduled_date ASC
    """)
    rows = [dict(r) for r in c.fetchall()]
    conn.close()
    return jsonify(rows)

if __name__ == '__main__':
    print("[INFO] Starting Municipal Waste Management Web Server on http://localhost:5000 ...")
    app.run(host='0.0.0.0', port=5000, debug=True)
