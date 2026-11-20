"""
ER Diagram Generator for Municipal Waste Collection Management System
Author: DHARANI K K (Register No.: 23)
Department of Information Technology, Velalar College of Engineering and Technology
Subject: 25ITT31 - Database Management Systems
"""

import os
import matplotlib.pyplot as plt
import matplotlib.patches as patches

def draw_er_diagram():
    fig, ax = plt.subplots(figsize=(16, 11), dpi=300)
    ax.set_xlim(0, 100)
    ax.set_ylim(0, 100)
    ax.axis('off')

    # Background color
    fig.patch.set_facecolor('#ffffff')
    ax.set_facecolor('#ffffff')

    # Title & Header
    ax.text(50, 97, "ENTITY-RELATIONSHIP (ER) DIAGRAM", 
            fontsize=18, fontweight='bold', ha='center', va='center', color='#0f766e', family='sans-serif')
    ax.text(50, 94, "Municipal Waste Collection Management and Environmental Data Analysis System (Scenario 23)", 
            fontsize=12, fontweight='semibold', ha='center', va='center', color='#334155', family='sans-serif')
    ax.text(50, 91.5, "Author: DHARANI K K  |  Register No.: 23  |  Class: 25IT3A&B  |  VCET Erode - 12", 
            fontsize=10, ha='center', va='center', color='#64748b', family='sans-serif')

    # Color Palette
    entity_bg = '#e0f2fe'      # light blue
    entity_border = '#0284c7'  # sky 600
    rel_bg = '#fef3c7'         # amber 100
    rel_border = '#d97706'     # amber 600
    attr_bg = '#f1f5f9'        # slate 100
    attr_border = '#64748b'    # slate 500
    pk_bg = '#dcfce7'          # green 100
    pk_border = '#16a34a'      # green 600
    text_color = '#0f172a'

    def draw_entity(x, y, w, h, name):
        rect = patches.FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.5",
                                      facecolor=entity_bg, edgecolor=entity_border, linewidth=2.2, zorder=2)
        ax.add_patch(rect)
        ax.text(x + w/2, y + h/2, name, fontsize=11, fontweight='bold', ha='center', va='center', color=text_color, zorder=3)

    def draw_relationship(cx, cy, w, h, name):
        pts = [[cx, cy + h/2], [cx + w/2, cy], [cx, cy - h/2], [cx - w/2, cy]]
        poly = patches.Polygon(pts, closed=True, facecolor=rel_bg, edgecolor=rel_border, linewidth=2, zorder=2)
        ax.add_patch(poly)
        ax.text(cx, cy, name, fontsize=8.5, fontweight='bold', ha='center', va='center', color='#78350f', zorder=3)

    def draw_attribute(cx, cy, rx, ry, name, is_pk=False, is_fk=False):
        bg = pk_bg if is_pk else attr_bg
        border = pk_border if is_pk else attr_border
        lw = 1.8 if is_pk else 1.2
        ellipse = patches.Ellipse((cx, cy), rx*2, ry*2, facecolor=bg, edgecolor=border, linewidth=lw, zorder=2)
        ax.add_patch(ellipse)
        style = 'underline' if is_pk else 'normal'
        lbl = f"{name} (PK)" if is_pk else (f"{name} (FK)" if is_fk else name)
        ax.text(cx, cy, lbl, fontsize=7.5, fontweight='bold' if is_pk else 'normal',
                ha='center', va='center', color=text_color, zorder=3)

    # 1. ENTITIES
    # RESIDENTIAL_AREAS
    draw_entity(10, 72, 18, 7, "RESIDENTIAL_AREAS")
    # VEHICLES
    draw_entity(72, 72, 18, 7, "VEHICLES")
    # COLLECTION_TASKS
    draw_entity(41, 48, 18, 7, "COLLECTION_TASKS")
    # WASTE_CATEGORIES
    draw_entity(10, 16, 18, 7, "WASTE_CATEGORIES")
    # WASTE_RECORDS
    draw_entity(72, 16, 18, 7, "WASTE_RECORDS")

    # 2. RELATIONSHIPS (Diamonds)
    # Area - Scheduled_For - Task
    draw_relationship(28, 59, 14, 6, "SCHEDULED\nFOR")
    # Vehicle - Operated_By - Task
    draw_relationship(72, 59, 14, 6, "OPERATED\nBY")
    # Task - Contains - Waste_Record
    draw_relationship(50, 32, 14, 6, "CONTAINS")
    # Category - Classified_As - Waste_Record
    draw_relationship(50, 19.5, 14, 6, "CLASSIFIED\nAS")

    # 3. CONNECTING LINES FOR RELATIONSHIPS (with Cardinality Labels)
    # Area to Relationship to Task
    ax.plot([19, 28], [72, 62], color='#0284c7', lw=1.8, zorder=1)
    ax.plot([28, 41], [56, 52], color='#0284c7', lw=1.8, zorder=1)
    ax.text(21, 68, "1", fontsize=10, fontweight='bold', color='#0369a1')
    ax.text(36, 54, "N", fontsize=10, fontweight='bold', color='#0369a1')

    # Vehicle to Relationship to Task
    ax.plot([81, 72], [72, 62], color='#0284c7', lw=1.8, zorder=1)
    ax.plot([72, 59], [56, 52], color='#0284c7', lw=1.8, zorder=1)
    ax.text(78, 68, "1", fontsize=10, fontweight='bold', color='#0369a1')
    ax.text(63, 54, "N", fontsize=10, fontweight='bold', color='#0369a1')

    # Task to Contains to Waste_Record
    ax.plot([50, 50], [48, 35], color='#0284c7', lw=1.8, zorder=1)
    ax.plot([50, 72], [29, 21], color='#0284c7', lw=1.8, zorder=1)
    ax.text(51.5, 42, "1", fontsize=10, fontweight='bold', color='#0369a1')
    ax.text(64, 26, "N", fontsize=10, fontweight='bold', color='#0369a1')

    # Category to Classified_As to Waste_Record
    ax.plot([28, 43], [19.5, 19.5], color='#0284c7', lw=1.8, zorder=1)
    ax.plot([57, 72], [19.5, 19.5], color='#0284c7', lw=1.8, zorder=1)
    ax.text(32, 21, "1", fontsize=10, fontweight='bold', color='#0369a1')
    ax.text(66, 21, "N", fontsize=10, fontweight='bold', color='#0369a1')

    # 4. ATTRIBUTES (Ellipses)
    # Area Attributes
    draw_attribute(7, 85, 5, 2.2, "area_id", is_pk=True)
    draw_attribute(19, 86, 5.5, 2.2, "area_name")
    draw_attribute(29, 83, 5.5, 2.2, "ward_location")
    draw_attribute(7, 65, 5, 2.2, "population")

    ax.plot([7, 13], [82.8, 79], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([19, 19], [83.8, 79], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([29, 25], [80.8, 79], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([7, 12], [67.2, 72], color='#94a3b8', lw=1.2, zorder=1)

    # Vehicle Attributes
    draw_attribute(71, 85, 5.5, 2.2, "vehicle_id", is_pk=True)
    draw_attribute(83, 86, 6, 2.2, "vehicle_number")
    draw_attribute(93, 82, 5.5, 2.2, "vehicle_type")
    draw_attribute(93, 73, 5.5, 2.2, "capacity_kg")
    draw_attribute(93, 65, 5.5, 2.2, "operational_status")

    ax.plot([71, 75], [82.8, 79], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([83, 83], [83.8, 79], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([93, 88], [79.8, 78], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([87.5, 90], [75.5, 74], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([87.5, 87.5], [67.2, 72], color='#94a3b8', lw=1.2, zorder=1)

    # Task Attributes
    draw_attribute(36, 40, 5, 2.2, "task_id", is_pk=True)
    draw_attribute(27, 45, 5, 2.2, "area_id", is_fk=True)
    draw_attribute(63, 45, 5.5, 2.2, "vehicle_id", is_fk=True)
    draw_attribute(50, 60, 5.5, 2.2, "scheduled_date")
    draw_attribute(33, 52, 4.5, 2.2, "status")

    ax.plot([36, 44], [42.2, 48], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([32, 41], [46, 49], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([59, 58], [47, 50], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([50, 50], [57.8, 55], color='#94a3b8', lw=1.2, zorder=1)

    # Category Attributes
    draw_attribute(8, 29, 6, 2.2, "category_id", is_pk=True)
    draw_attribute(22, 29, 6, 2.2, "category_name")
    draw_attribute(9, 6, 5.5, 2.2, "description")

    ax.plot([8, 13], [26.8, 23], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([22, 22], [26.8, 23], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([9, 14], [8.2, 16], color='#94a3b8', lw=1.2, zorder=1)

    # Waste Record Attributes
    draw_attribute(93, 27, 5.5, 2.2, "record_id", is_pk=True)
    draw_attribute(80, 29, 5, 2.2, "task_id", is_fk=True)
    draw_attribute(92, 17, 5.5, 2.2, "category_id", is_fk=True)
    draw_attribute(93, 8, 5.5, 2.2, "quantity_kg")
    draw_attribute(79, 7, 5.5, 2.2, "recorded_at")

    ax.plot([93, 88], [24.8, 23], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([80, 80], [26.8, 23], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([90, 86.5], [19.5, 19.5], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([93, 87], [10.2, 16], color='#94a3b8', lw=1.2, zorder=1)
    ax.plot([79, 79], [9.2, 16], color='#94a3b8', lw=1.2, zorder=1)

    # 5. LEGEND BOX
    leg_box = patches.FancyBboxPatch((3, 90), 30, 8, boxstyle="round,pad=0.4",
                                     facecolor='#f8fafc', edgecolor='#cbd5e1', linewidth=1, zorder=2)
    ax.add_patch(leg_box)
    ax.text(4, 96, "NOTATION LEGEND:", fontsize=8.5, fontweight='bold', color='#1e293b')

    # Entity legend item
    e_rect = patches.Rectangle((4, 93), 2.5, 1.5, facecolor=entity_bg, edgecolor=entity_border, linewidth=1.2)
    ax.add_patch(e_rect)
    ax.text(7.5, 93.7, "Entity Set (Strong)", fontsize=7.5, color='#334155')

    # Relationship legend item
    r_pts = [[19, 94.5], [20.5, 93.7], [19, 93], [17.5, 93.7]]
    r_poly = patches.Polygon(r_pts, closed=True, facecolor=rel_bg, edgecolor=rel_border, linewidth=1)
    ax.add_patch(r_poly)
    ax.text(21.5, 93.7, "Relationship", fontsize=7.5, color='#334155')

    # PK legend item
    pk_el = patches.Ellipse((4.5, 91.5), 2.5, 1.2, facecolor=pk_bg, edgecolor=pk_border, linewidth=1)
    ax.add_patch(pk_el)
    ax.text(7.5, 91.2, "Primary Key (PK)", fontsize=7.5, color='#334155')

    # Attr legend item
    at_el = patches.Ellipse((19, 91.5), 2.5, 1.2, facecolor=attr_bg, edgecolor=attr_border, linewidth=1)
    ax.add_patch(at_el)
    ax.text(21.5, 91.2, "Attribute", fontsize=7.5, color='#334155')

    plt.tight_layout()
    
    # Save destinations
    destinations = [
        os.path.join(os.path.dirname(__file__), "docs", "er_diagram.png"),
        os.path.join(os.path.dirname(__file__), "web-app", "static", "images", "er_diagram.png"),
        os.path.join(os.path.dirname(__file__), "python-analysis", "visualizations", "er_diagram.png")
    ]
    for d in destinations:
        os.makedirs(os.path.dirname(d), exist_ok=True)
        plt.savefig(d, dpi=300, bbox_inches='tight')
        print(f"[SUCCESS] Saved ER diagram image: {d}")

    plt.close()

if __name__ == "__main__":
    draw_er_diagram()
