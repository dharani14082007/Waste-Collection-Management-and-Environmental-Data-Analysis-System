"""
Municipal Waste Collection Management and Environmental Data Analysis System
Author: DHARANI K K (Register No.: 23)
Department of Information Technology, Velalar College of Engineering and Technology
Subject: 25ITC31 - Foundations of Data Science
"""

import os
import sqlite3
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Configure visual style
plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')
plt.rcParams['font.sans-serif'] = 'DejaVu Sans'
plt.rcParams['axes.edgecolor'] = '#cccccc'
plt.rcParams['axes.linewidth'] = 0.8

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.path.join(BASE_DIR, "..", "database", "waste_management.db")
OUTPUT_DIR = os.path.join(BASE_DIR, "visualizations")
os.makedirs(OUTPUT_DIR, exist_ok=True)

print("=" * 80)
print("     MUNICIPAL SOLID WASTE DATA SCIENCE & VISUALIZATION PIPELINE")
print("     Author: DHARANI K K | Register No: 23 | Class: 25IT3A&B")
print("=" * 80)

# ==============================================================================
# 1. DATA EXTRACTION FROM DATABASE (Unit 2: File Handling & Dataset Exploration)
# ==============================================================================
print("\n[STEP 1] Extracting Data from SQLite Database...")
conn = sqlite3.connect(DB_PATH)

query_full = """
SELECT 
    t.task_id,
    t.scheduled_date,
    t.status AS task_status,
    a.area_id,
    a.area_name,
    a.ward_location,
    a.population,
    v.vehicle_id,
    v.vehicle_number,
    v.vehicle_type,
    v.capacity_kg AS vehicle_capacity,
    c.category_id,
    c.category_name,
    r.record_id,
    r.quantity_kg
FROM collection_tasks t
JOIN residential_areas a ON t.area_id = a.area_id
JOIN vehicles v ON t.vehicle_id = v.vehicle_id
LEFT JOIN waste_records r ON t.task_id = r.task_id
LEFT JOIN waste_categories c ON r.category_id = c.category_id
"""

df_raw = pd.read_sql_query(query_full, conn)
conn.close()

print(f"-> Successfully extracted {len(df_raw)} records from database.")
print(df_raw.head(3))

# ==============================================================================
# 2. DATA PREPROCESSING & CLEANING (Unit 4: Data Quality & Transformation)
# ==============================================================================
print("\n[STEP 2] Performing Data Cleaning & Transformation...")

# Check missing values
missing_counts = df_raw.isnull().sum()
print("Missing values per column:\n", missing_counts[missing_counts > 0])

# Impute / drop if necessary (completed tasks should have records)
df = df_raw.dropna(subset=['quantity_kg']).copy()

# Type conversion
df['scheduled_date'] = pd.to_datetime(df['scheduled_date'])
df['quantity_kg'] = df['quantity_kg'].astype(float)
df['population'] = df['population'].astype(int)
df['vehicle_capacity'] = df['vehicle_capacity'].astype(float)

# Duplicate checking
duplicates = df.duplicated(subset=['record_id']).sum()
print(f"-> Duplicate records detected: {duplicates}")

# Outlier Detection on Waste Quantity using IQR Method
Q1 = df['quantity_kg'].quantile(0.25)
Q3 = df['quantity_kg'].quantile(0.75)
IQR = Q3 - Q1
lower_bound = max(0, Q1 - 1.5 * IQR)
upper_bound = Q3 + 1.5 * IQR
outliers = df[(df['quantity_kg'] < lower_bound) | (df['quantity_kg'] > upper_bound)]
print(f"-> IQR Bounds: [{lower_bound:.2f}, {upper_bound:.2f}] kg | Outliers flagged: {len(outliers)}")

# Feature Engineering: Compute Per-Capita and Utilization Ratio
df['per_capita_waste'] = df['quantity_kg'] / df['population']

# ==============================================================================
# 3. STATISTICAL SUMMARY (Unit 1 & 3: Averages, Variability & Aggregations)
# ==============================================================================
print("\n[STEP 3] Descriptive Statistics & Group Summaries:")
desc = df[['quantity_kg', 'population', 'vehicle_capacity']].describe()
print(desc)

# Calculate Z-Scores for Waste Quantity
df['quantity_zscore'] = (df['quantity_kg'] - df['quantity_kg'].mean()) / df['quantity_kg'].std()

# Category Pivot Table
pivot_category = pd.pivot_table(
    df, 
    values='quantity_kg', 
    index='area_name', 
    columns='category_name', 
    aggfunc='sum', 
    fill_value=0
)
print("\nArea-Wise Category Waste Pivot Table (kg):")
print(pivot_category.head())

# Save preprocessed dataset to CSV
csv_out = os.path.join(BASE_DIR, "preprocessed_waste_data.csv")
df.to_csv(csv_out, index=False)
print(f"\n-> Cleaned preprocessed data saved to: {csv_out}")

# ==============================================================================
# 4. EXPLORATORY DATA ANALYSIS & VISUALIZATION (Unit 5: EDA & Matplotlib/Seaborn)
# ==============================================================================
print("\n[STEP 4] Generating Visualizations...")

# ------------------------------------------------------------------------------
# Chart 1: Area-wise Waste Generation (Horizontal Bar Chart)
# ------------------------------------------------------------------------------
plt.figure(figsize=(10, 6))
area_totals = df.groupby('area_name')['quantity_kg'].sum().sort_values(ascending=True)
colors = plt.cm.viridis(np.linspace(0.3, 0.9, len(area_totals)))
bars = plt.barh(area_totals.index, area_totals.values, color=colors, edgecolor='black', alpha=0.85)

for bar in bars:
    w = bar.get_width()
    plt.text(w + 150, bar.get_y() + bar.get_height()/2, f'{w:,.0f} kg', va='center', fontsize=9, fontweight='bold')

plt.title("Residential Area-wise Total Waste Generation\nErode Municipal Corporation Zones", fontsize=14, fontweight='bold', pad=15)
plt.xlabel("Total Waste Collected (Kilograms)", fontsize=11, fontweight='bold')
plt.ylabel("Residential Area / Ward", fontsize=11, fontweight='bold')
plt.xlim(0, max(area_totals.values) * 1.15)
plt.tight_layout()
c1_path = os.path.join(OUTPUT_DIR, "1_area_wise_waste_distribution.png")
plt.savefig(c1_path, dpi=300)
plt.close()
print(f"  [Saved] {c1_path}")

# ------------------------------------------------------------------------------
# Chart 2: Categorical Segregation Breakdown (Donut Chart)
# ------------------------------------------------------------------------------
plt.figure(figsize=(8, 7))
cat_totals = df.groupby('category_name')['quantity_kg'].sum()
category_colors = ['#2ca02c', '#1f77b4', '#7f7f7f', '#d62728'] # Green, Blue, Grey, Red

wedges, texts, autotexts = plt.pie(
    cat_totals.values,
    labels=cat_totals.index,
    autopct='%1.1f%%',
    pctdistance=0.75,
    colors=category_colors,
    startangle=140,
    textprops={'fontsize': 10, 'fontweight': 'bold'},
    wedgeprops=dict(width=0.45, edgecolor='white', linewidth=2)
)

for autotext in autotexts:
    autotext.set_color('white')

plt.title("Municipal Solid Waste Segregation Proportions\n(Biodegradable vs. Recyclable vs. Non-Recyclable vs. Hazardous)", fontsize=13, fontweight='bold', pad=15)
plt.tight_layout()
c2_path = os.path.join(OUTPUT_DIR, "2_waste_category_breakdown.png")
plt.savefig(c2_path, dpi=300)
plt.close()
print(f"  [Saved] {c2_path}")

# ------------------------------------------------------------------------------
# Chart 3: Vehicle Capacity Utilization Distribution (Seaborn Box Plot)
# ------------------------------------------------------------------------------
# Group by task to get total payload per trip
trip_payloads = df.groupby(['task_id', 'vehicle_number', 'vehicle_type', 'vehicle_capacity'])['quantity_kg'].sum().reset_index()
trip_payloads['utilization_pct'] = (trip_payloads['quantity_kg'] / trip_payloads['vehicle_capacity']) * 100

plt.figure(figsize=(10, 6))
sns.boxplot(x='vehicle_type', y='utilization_pct', data=trip_payloads, palette='Set2', width=0.5)
sns.stripplot(x='vehicle_type', y='utilization_pct', data=trip_payloads, color='black', alpha=0.6, jitter=0.2, size=7)

plt.axhline(100, color='red', linestyle='--', linewidth=1.5, label='100% Rated Capacity (Overload Threshold)')
plt.axhline(90, color='orange', linestyle=':', linewidth=1.2, label='90% Target Utilization')

plt.title("Vehicle Fleet Payload Utilization Distribution by Vehicle Class", fontsize=13, fontweight='bold', pad=15)
plt.xlabel("Vehicle Category", fontsize=11, fontweight='bold')
plt.ylabel("Payload Utilization Percentage (%)", fontsize=11, fontweight='bold')
plt.legend(loc='lower right', frameon=True)
plt.tight_layout()
c3_path = os.path.join(OUTPUT_DIR, "3_vehicle_capacity_utilization.png")
plt.savefig(c3_path, dpi=300)
plt.close()
print(f"  [Saved] {c3_path}")

# ------------------------------------------------------------------------------
# Chart 4: Daily Collection Trends (Multi-Category Time Series Line Chart)
# ------------------------------------------------------------------------------
daily_cat = df.groupby(['scheduled_date', 'category_name'])['quantity_kg'].sum().unstack(fill_value=0)

plt.figure(figsize=(11, 6))
for cat in daily_cat.columns:
    plt.plot(daily_cat.index.strftime('%b %d'), daily_cat[cat], marker='o', linewidth=2.2, label=cat)

plt.title("Daily Waste Collection Trends by Category (November 2026)", fontsize=13, fontweight='bold', pad=15)
plt.xlabel("Collection Date", fontsize=11, fontweight='bold')
plt.ylabel("Total Collected (Kilograms)", fontsize=11, fontweight='bold')
plt.grid(True, linestyle='--', alpha=0.6)
plt.legend(title="Waste Category", frameon=True)
plt.tight_layout()
c4_path = os.path.join(OUTPUT_DIR, "4_daily_collection_trends.png")
plt.savefig(c4_path, dpi=300)
plt.close()
print(f"  [Saved] {c4_path}")

# ------------------------------------------------------------------------------
# Chart 5: Correlation: Residential Population vs. Daily Waste Generation
# ------------------------------------------------------------------------------
area_agg = df.groupby(['area_name', 'population'])['quantity_kg'].sum().reset_index()

plt.figure(figsize=(9, 6))
sns.regplot(
    x='population', 
    y='quantity_kg', 
    data=area_agg, 
    scatter_kws={'s': 90, 'color': '#1f77b4', 'alpha': 0.8},
    line_kws={'color': '#d62728', 'linewidth': 2}
)

corr = area_agg['population'].corr(area_agg['quantity_kg'])

for _, row in area_agg.iterrows():
    plt.annotate(
        row['area_name'].split()[0], 
        (row['population'], row['quantity_kg']),
        textcoords="offset points", 
        xytext=(0, 8), 
        ha='center', 
        fontsize=8
    )

plt.title(f"Correlation Analysis: Residential Population vs. Total Waste\nPearson Correlation Coefficient r = {corr:.3f}", fontsize=13, fontweight='bold', pad=15)
plt.xlabel("Residential Area Population (Number of Citizens)", fontsize=11, fontweight='bold')
plt.ylabel("Total Waste Collected (Kilograms)", fontsize=11, fontweight='bold')
plt.tight_layout()
c5_path = os.path.join(OUTPUT_DIR, "5_population_vs_waste_correlation.png")
plt.savefig(c5_path, dpi=300)
plt.close()
print(f"  [Saved] {c5_path}")

# ------------------------------------------------------------------------------
# Chart 6: Task Completion & Operational Status Overview
# ------------------------------------------------------------------------------
conn = sqlite3.connect(DB_PATH)
df_status = pd.read_sql_query("SELECT status, COUNT(*) AS task_count FROM collection_tasks GROUP BY status", conn)
conn.close()

plt.figure(figsize=(8, 5))
status_colors = {'COMPLETED': '#2ca02c', 'IN_PROGRESS': '#ff7f0e', 'SCHEDULED': '#1f77b4', 'CANCELLED': '#d62728'}
bar_colors = [status_colors.get(s, '#7f7f7f') for s in df_status['status']]

bars = plt.bar(df_status['status'], df_status['task_count'], color=bar_colors, width=0.5, edgecolor='black', alpha=0.85)

for bar in bars:
    y = bar.get_height()
    plt.text(bar.get_x() + bar.get_width()/2, y + 0.3, f'{int(y)} tasks', ha='center', fontsize=10, fontweight='bold')

plt.title("Collection Activity Operational Status Overview", fontsize=13, fontweight='bold', pad=15)
plt.xlabel("Operational Lifecycle Status", fontsize=11, fontweight='bold')
plt.ylabel("Number of Scheduled Tasks", fontsize=11, fontweight='bold')
plt.ylim(0, max(df_status['task_count']) * 1.25)
plt.tight_layout()
c6_path = os.path.join(OUTPUT_DIR, "6_collection_status_overview.png")
plt.savefig(c6_path, dpi=300)
plt.close()
print(f"  [Saved] {c6_path}")

print("\n" + "=" * 80)
print("     ALL 6 VISUALIZATIONS GENERATED AND SAVED SUCCESSFULLY!")
print("=" * 80)
