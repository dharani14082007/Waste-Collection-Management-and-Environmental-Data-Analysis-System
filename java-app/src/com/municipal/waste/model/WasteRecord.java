package com.municipal.waste.model;

/**
 * Domain entity representing a categorized waste entry for a collection task.
 * Author: DHARANI K K (Register No.: 23)
 */
public class WasteRecord {
    private int recordId;
    private int taskId;
    private WasteCategory category;
    private double quantityKg;
    private String recordedAt;

    public WasteRecord(int recordId, int taskId, WasteCategory category, double quantityKg, String recordedAt) {
        this.recordId = recordId;
        this.taskId = taskId;
        this.category = category;
        this.quantityKg = quantityKg;
        this.recordedAt = recordedAt;
    }

    public WasteRecord(int taskId, WasteCategory category, double quantityKg) {
        this(0, taskId, category, quantityKg, null);
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getTaskId() {
        return taskId;
    }

    public WasteCategory getCategory() {
        return category;
    }

    public double getQuantityKg() {
        return quantityKg;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    @Override
    public String toString() {
        return String.format("   -> %-35s : %,8.2f kg", category.getDisplayName(), quantityKg);
    }
}
