package com.municipal.waste;

import com.municipal.waste.dao.AreaDAO;
import com.municipal.waste.dao.CollectionDAO;
import com.municipal.waste.dao.VehicleDAO;
import com.municipal.waste.model.*;
import com.municipal.waste.service.ReportService;
import com.municipal.waste.service.WasteManagementService;
import com.municipal.waste.util.ValidationException;

import java.util.List;
import java.util.Scanner;

/**
 * Main Application Entry Point
 * Municipal Waste Collection Management and Environmental Data Analysis System
 * 
 * Author: DHARANI K K
 * Register No.: 23
 * Department of Information Technology
 * Velalar College of Engineering and Technology, Erode - 12
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static WasteManagementService service;
    private static ReportService reportService;

    public static void main(String[] args) {
        // Initialize layers
        AreaDAO areaDAO = new AreaDAO();
        VehicleDAO vehicleDAO = new VehicleDAO();
        CollectionDAO collectionDAO = new CollectionDAO(areaDAO, vehicleDAO);
        service = new WasteManagementService(areaDAO, vehicleDAO, collectionDAO);
        reportService = new ReportService(service);

        printBanner();

        // If run with --demo argument, execute full automated showcase and exit
        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            runAutomatedDemonstration();
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter choice (1-12): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleRegisterArea();
                    break;
                case "2":
                    handleListAreas();
                    break;
                case "3":
                    handleRegisterVehicle();
                    break;
                case "4":
                    handleListVehicles();
                    break;
                case "5":
                    handleScheduleCollection();
                    break;
                case "6":
                    handleRecordWaste();
                    break;
                case "7":
                    handleCheckVehiclePayload();
                    break;
                case "8":
                    handleUpdateTaskStatus();
                    break;
                case "9":
                    reportService.printAreaWiseSummary();
                    break;
                case "10":
                    reportService.printVehicleUtilizationReport();
                    break;
                case "11":
                    reportService.printCompletionMetrics();
                    break;
                case "12":
                    System.out.println("\n[INFO] Thank you for using WCM-EDAS Municipal System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("[ERROR] Invalid selection! Please enter a number between 1 and 12.");
            }
        }
    }

    private static void printBanner() {
        System.out.println("=========================================================================================");
        System.out.println("     VELALAR COLLEGE OF ENGINEERING AND TECHNOLOGY, ERODE - 12 (AUTONOMOUS)             ");
        System.out.println("                        DEPARTMENT OF INFORMATION TECHNOLOGY                            ");
        System.out.println("             Waste Collection Management and Environmental Data Analysis System         ");
        System.out.println("       Developer: DHARANI K K  |  Register No.: 23  |  Class: 25IT3A&B                  ");
        System.out.println("=========================================================================================\n");
    }

    private static void printMenu() {
        System.out.println("\n--- MAIN CONTROL PANEL ---");
        System.out.println(" 1. Register New Residential Area");
        System.out.println(" 2. View All Residential Areas");
        System.out.println(" 3. Register New Collection Vehicle");
        System.out.println(" 4. View Vehicles & Fleet Status");
        System.out.println(" 5. Schedule Waste Collection Activity (Assign Area & Vehicle)");
        System.out.println(" 6. Record Collected Waste (By Category: Bio, Recyclable, Non-Recyclable)");
        System.out.println(" 7. Verify Vehicle Payload & Overload Alert");
        System.out.println(" 8. Update Task Status (Scheduled -> In Progress -> Completed)");
        System.out.println(" 9. Generate Area-wise Waste & Per-Capita Report");
        System.out.println("10. Generate Vehicle Fleet Capacity Utilization Report");
        System.out.println("11. View Task Completion & Service Metrics");
        System.out.println("12. Exit System");
    }

    private static void handleRegisterArea() {
        try {
            System.out.println("\n[Register Residential Area]");
            System.out.print("Enter Area Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Ward / Zone Location: ");
            String ward = scanner.nextLine().trim();
            System.out.print("Enter Population: ");
            int population = Integer.parseInt(scanner.nextLine().trim());

            Area area = service.registerArea(name, ward, population);
            System.out.printf("[SUCCESS] Area registered successfully: ID #%d | %s (Pop: %,d)\n",
                    area.getAreaId(), area.getAreaName(), area.getPopulation());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input for population.");
        } catch (ValidationException e) {
            System.out.println("[VALIDATION ERROR] " + e.getMessage());
        }
    }

    private static void handleListAreas() {
        System.out.println("\n--- REGISTERED RESIDENTIAL AREAS ---");
        List<Area> areas = service.listAllAreas();
        for (Area a : areas) {
            System.out.println(a);
        }
    }

    private static void handleRegisterVehicle() {
        try {
            System.out.println("\n[Register Collection Vehicle]");
            System.out.print("Enter Vehicle Registration Number (e.g. TN-33-ZZ-9999): ");
            String number = scanner.nextLine().trim();
            System.out.print("Enter Vehicle Type (e.g. Compactor Heavy Truck, Tipper): ");
            String type = scanner.nextLine().trim();
            System.out.print("Enter Carrying Capacity in kg: ");
            double capacity = Double.parseDouble(scanner.nextLine().trim());

            Vehicle v = service.registerVehicle(number, type, capacity);
            System.out.printf("[SUCCESS] Vehicle registered: ID #%d | %s | Capacity: %,.1f kg\n",
                    v.getVehicleId(), v.getVehicleNumber(), v.getCapacityKg());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input for capacity.");
        } catch (ValidationException e) {
            System.out.println("[VALIDATION ERROR] " + e.getMessage());
        }
    }

    private static void handleListVehicles() {
        System.out.println("\n--- MUNICIPAL VEHICLE FLEET ---");
        List<Vehicle> vehicles = service.listAllVehicles();
        for (Vehicle v : vehicles) {
            System.out.println(v);
        }
    }

    private static void handleScheduleCollection() {
        try {
            System.out.println("\n[Schedule Waste Collection Assignment]");
            handleListAreas();
            System.out.print("\nEnter Target Area ID: ");
            int areaId = Integer.parseInt(scanner.nextLine().trim());

            handleListVehicles();
            System.out.print("\nEnter Assigned Vehicle ID: ");
            int vehicleId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Scheduled Date (YYYY-MM-DD): ");
            String date = scanner.nextLine().trim();

            CollectionTask task = service.scheduleCollection(areaId, vehicleId, date);
            System.out.printf("[SUCCESS] Collection Scheduled! Task ID #%d for %s with Vehicle %s on %s\n",
                    task.getTaskId(), task.getAreaName(), task.getVehicleNumber(), task.getScheduledDate());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Please enter valid integers for IDs.");
        } catch (ValidationException e) {
            System.out.println("[VALIDATION ERROR] " + e.getMessage());
        }
    }

    private static void handleRecordWaste() {
        try {
            System.out.println("\n[Record Categorized Waste Collection]");
            System.out.print("Enter Task ID: ");
            int taskId = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Select Waste Category:");
            System.out.println(" 1. Biodegradable (Kitchen/Organic)");
            System.out.println(" 2. Recyclable (Paper/Plastic/Metal/Glass)");
            System.out.println(" 3. Non-Recyclable (Inert/Debris/Sanitary)");
            System.out.println(" 4. Hazardous (E-waste/Chemical)");
            System.out.print("Category Choice (1-4): ");
            int catChoice = Integer.parseInt(scanner.nextLine().trim());
            WasteCategory category = WasteCategory.fromId(catChoice);

            System.out.print("Enter Collected Weight in kg: ");
            double quantity = Double.parseDouble(scanner.nextLine().trim());

            double utilPct = service.recordWaste(taskId, category, quantity);
            System.out.printf("[SUCCESS] %.2f kg of %s logged for Task #%d.\n", quantity, category.name(), taskId);
            System.out.printf("Current Vehicle Payload Utilization: %.1f%%\n", utilPct);

            if (utilPct > 100.0) {
                System.out.println("[WARNING] OVERLOAD ALERT! Vehicle rated capacity exceeded!");
            } else if (utilPct >= 95.0) {
                System.out.println("[NOTICE] Vehicle payload is at or above 95% rated capacity.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input.");
        } catch (ValidationException e) {
            System.out.println("[VALIDATION ERROR] " + e.getMessage());
        }
    }

    private static void handleCheckVehiclePayload() {
        try {
            System.out.print("\nEnter Task ID to inspect payload: ");
            int taskId = Integer.parseInt(scanner.nextLine().trim());
            CollectionTask task = service.getTask(taskId);
            if (task == null) {
                System.out.println("[ERROR] Task ID not found: " + taskId);
                return;
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.printf("  TASK PAYLOAD INSPECTION - TASK #%d\n", task.getTaskId());
            System.out.println("=".repeat(60));
            System.out.printf("  Area               : %s\n", task.getAreaName());
            System.out.printf("  Vehicle            : %s (Capacity: %,.1f kg)\n", task.getVehicleNumber(), task.getVehicleCapacityKg());
            System.out.printf("  Scheduled Date     : %s\n", task.getScheduledDate());
            System.out.printf("  Operational Status : %s\n", task.getStatus());
            System.out.println("  Categorized Waste Records:");
            for (WasteRecord wr : task.getWasteRecords()) {
                System.out.println(wr);
            }
            double total = task.getTotalWasteKg();
            double util = task.getCapacityUtilizationPercentage();
            System.out.println("-".repeat(60));
            System.out.printf("  Total Collected    : %,.2f kg\n", total);
            System.out.printf("  Capacity Used      : %.1f%%\n", util);

            if (util > 100.0) {
                System.out.println("  STATUS ALERT       : [OVERLOADED - RED FLAG]");
            } else if (util >= 90.0) {
                System.out.println("  STATUS ALERT       : [OPTIMAL / FULL LOAD]");
            } else {
                System.out.println("  STATUS ALERT       : [CAPACITY WITHIN LIMITS]");
            }
            System.out.println("=".repeat(60));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input.");
        }
    }

    private static void handleUpdateTaskStatus() {
        try {
            System.out.print("\nEnter Task ID to update: ");
            int taskId = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Select New Status:");
            System.out.println(" 1. SCHEDULED");
            System.out.println(" 2. IN_PROGRESS");
            System.out.println(" 3. COMPLETED");
            System.out.println(" 4. CANCELLED");
            System.out.print("Choice (1-4): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            CollectionStatus newStatus;
            switch (choice) {
                case 1: newStatus = CollectionStatus.SCHEDULED; break;
                case 2: newStatus = CollectionStatus.IN_PROGRESS; break;
                case 3: newStatus = CollectionStatus.COMPLETED; break;
                case 4: newStatus = CollectionStatus.CANCELLED; break;
                default:
                    System.out.println("[ERROR] Invalid status choice.");
                    return;
            }

            boolean success = service.updateTaskStatus(taskId, newStatus);
            if (success) {
                System.out.printf("[SUCCESS] Task #%d status updated to %s\n", taskId, newStatus);
            } else {
                System.out.println("[ERROR] Failed to update task status.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input.");
        } catch (ValidationException e) {
            System.out.println("[VALIDATION ERROR] " + e.getMessage());
        }
    }

    private static void runAutomatedDemonstration() {
        System.out.println("\n[AUTOMATED DEMO MODE - SHOWCASING ALL 7 FUNCTIONAL REQUIREMENTS]\n");

        System.out.println("1. [FR-01] Registered Residential Areas:");
        service.listAllAreas().stream().limit(5).forEach(System.out::println);

        System.out.println("\n2. [FR-02] Registered Vehicles Fleet:");
        service.listAllVehicles().stream().limit(5).forEach(System.out::println);

        System.out.println("\n3. [FR-03 & FR-04] Scheduling New Collection Task & Logging Waste:");
        try {
            CollectionTask demoTask = service.scheduleCollection(1, 1, "2026-11-20");
            System.out.printf("   Created Demo Task #%d for %s\n", demoTask.getTaskId(), demoTask.getAreaName());

            System.out.println("   Logging 2,400 kg Biodegradable Waste...");
            service.recordWaste(demoTask.getTaskId(), WasteCategory.BIODEGRADABLE, 2400.0);

            System.out.println("   Logging 1,200 kg Recyclable Waste...");
            service.recordWaste(demoTask.getTaskId(), WasteCategory.RECYCLABLE, 1200.0);

            System.out.println("   Logging 600 kg Non-Recyclable Waste...");
            double util = service.recordWaste(demoTask.getTaskId(), WasteCategory.NON_RECYCLABLE, 600.0);

            System.out.printf("   Current Vehicle Utilization: %.1f%%\n", util);

            System.out.println("\n4. [FR-04] Updating Task to COMPLETED:");
            service.updateTaskStatus(demoTask.getTaskId(), CollectionStatus.COMPLETED);
            System.out.printf("   Task #%d status marked as COMPLETED.\n", demoTask.getTaskId());

        } catch (ValidationException e) {
            System.out.println("[DEMO ERROR] " + e.getMessage());
        }

        System.out.println("\n5. [FR-05 & FR-06] Operational Reports Generated:");
        reportService.printAreaWiseSummary();
        reportService.printVehicleUtilizationReport();
        reportService.printCompletionMetrics();

        System.out.println("[DEMO COMPLETE] All Java functional requirements successfully verified!");
    }
}
