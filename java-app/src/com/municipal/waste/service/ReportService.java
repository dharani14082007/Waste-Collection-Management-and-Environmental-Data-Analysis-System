package com.municipal.waste.service;

import com.municipal.waste.model.*;

import java.util.*;

/**
 * Service generating formatted reports for municipal administration review.
 * Author: DHARANI K K (Register No.: 23)
 */
public class ReportService {

    private final WasteManagementService service;

    public ReportService(WasteManagementService service) {
        this.service = service;
    }

    public void printAreaWiseSummary() {
        List<Area> areas = service.listAllAreas();
        List<CollectionTask> tasks = service.listAllTasks();

        System.out.println("\n" + "=".repeat(95));
        System.out.println("                 MUNICIPAL REPORT: RESIDENTIAL AREA WASTE GENERATION SUMMARY");
        System.out.println("=".repeat(95));
        System.out.printf("%-5s | %-28s | %-22s | %-10s | %-14s | %-10s\n",
                "ID", "Area Name", "Ward Location", "Population", "Total Waste(kg)", "Per-Capita");
        System.out.println("-".repeat(95));

        double grandTotal = 0.0;
        int totalPopulation = 0;

        for (Area area : areas) {
            double areaWaste = 0.0;
            for (CollectionTask task : tasks) {
                if (task.getAreaId() == area.getAreaId()) {
                    areaWaste += task.getTotalWasteKg();
                }
            }
            double perCapita = (area.getPopulation() > 0) ? (areaWaste / area.getPopulation()) : 0.0;
            grandTotal += areaWaste;
            totalPopulation += area.getPopulation();

            System.out.printf("%-5d | %-28s | %-22s | %,10d | %,14.2f | %,10.4f\n",
                    area.getAreaId(), area.getAreaName(), area.getWardLocation(), 
                    area.getPopulation(), areaWaste, perCapita);
        }

        System.out.println("=".repeat(95));
        System.out.printf(" TOTAL POPULATION: %,d citizens  |  TOTAL MUNICIPAL WASTE COLLECTED: %,.2f kg\n",
                totalPopulation, grandTotal);
        System.out.println("=".repeat(95) + "\n");
    }

    public void printVehicleUtilizationReport() {
        List<Vehicle> vehicles = service.listAllVehicles();
        List<CollectionTask> tasks = service.listAllTasks();

        System.out.println("\n" + "=".repeat(105));
        System.out.println("                    MUNICIPAL REPORT: VEHICLE FLEET CAPACITY UTILIZATION");
        System.out.println("=".repeat(105));
        System.out.printf("%-4s | %-15s | %-22s | %-10s | %-8s | %-14s | %-12s | %-10s\n",
                "ID", "Vehicle Number", "Type", "Capacity", "Trips", "Avg Load(kg)", "Avg Util %", "Status");
        System.out.println("-".repeat(105));

        for (Vehicle v : vehicles) {
            int tripCount = 0;
            double cumulativeLoad = 0.0;

            for (CollectionTask t : tasks) {
                if (t.getVehicleId() == v.getVehicleId()) {
                    tripCount++;
                    cumulativeLoad += t.getTotalWasteKg();
                }
            }

            double avgLoad = (tripCount > 0) ? (cumulativeLoad / tripCount) : 0.0;
            double avgUtil = (v.getCapacityKg() > 0) ? (avgLoad / v.getCapacityKg()) * 100.0 : 0.0;

            System.out.printf("%-4d | %-15s | %-22s | %,8.1f kg | %-8d | %,14.2f | %10.1f %% | %-10s\n",
                    v.getVehicleId(), v.getVehicleNumber(), v.getVehicleType(), 
                    v.getCapacityKg(), tripCount, avgLoad, avgUtil, v.getOperationalStatus());
        }
        System.out.println("=".repeat(105) + "\n");
    }

    public void printCompletionMetrics() {
        List<CollectionTask> tasks = service.listAllTasks();

        int scheduled = 0, inProgress = 0, completed = 0, cancelled = 0;
        for (CollectionTask t : tasks) {
            switch (t.getStatus()) {
                case SCHEDULED: scheduled++; break;
                case IN_PROGRESS: inProgress++; break;
                case COMPLETED: completed++; break;
                case CANCELLED: cancelled++; break;
            }
        }
        int total = tasks.size();
        double completionRate = (total > 0) ? (completed * 100.0 / total) : 0.0;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("          TASK COMPLETION & SERVICE EFFICIENCY METRICS");
        System.out.println("=".repeat(60));
        System.out.printf("  Total Assigned Schedules    : %d\n", total);
        System.out.printf("  Completed Collections       : %d (%.1f%%)\n", completed, completionRate);
        System.out.printf("  In-Progress Collections     : %d\n", inProgress);
        System.out.printf("  Scheduled (Pending Dispatch): %d\n", scheduled);
        System.out.printf("  Cancelled Schedules         : %d\n", cancelled);
        System.out.println("=".repeat(60) + "\n");
    }
}
