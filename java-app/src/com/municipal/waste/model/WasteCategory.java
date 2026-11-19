package com.municipal.waste.model;

/**
 * Standard classification categories for municipal solid waste.
 * Author: DHARANI K K (Register No.: 23)
 */
public enum WasteCategory {
    BIODEGRADABLE(1, "Biodegradable (Organic/Kitchen)"),
    RECYCLABLE(2, "Recyclable (Paper/Plastic/Metal)"),
    NON_RECYCLABLE(3, "Non-Recyclable (Inert/Debris)"),
    HAZARDOUS(4, "Hazardous (E-waste/Chemical)");

    private final int id;
    private final String displayName;

    WasteCategory(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static WasteCategory fromId(int id) {
        for (WasteCategory cat : values()) {
            if (cat.id == id) return cat;
        }
        return BIODEGRADABLE;
    }
}
