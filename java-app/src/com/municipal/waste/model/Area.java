package com.municipal.waste.model;

/**
 * Domain entity representing a residential area within the municipality.
 * Author: DHARANI K K (Register No.: 23)
 */
public class Area {
    private int areaId;
    private String areaName;
    private String wardLocation;
    private int population;

    public Area(int areaId, String areaName, String wardLocation, int population) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.wardLocation = wardLocation;
        this.population = population;
    }

    public Area(String areaName, String wardLocation, int population) {
        this(0, areaName, wardLocation, population);
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getWardLocation() {
        return wardLocation;
    }

    public void setWardLocation(String wardLocation) {
        this.wardLocation = wardLocation;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    @Override
    public String toString() {
        return String.format("[ID: %-3d] %-30s | %-25s | Pop: %,d", 
                areaId, areaName, wardLocation, population);
    }
}
