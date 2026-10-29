import agents.*;
import facilities.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("   MARS-OPS INTEGRATION TEST: AGENTS & FACILITIES    ");
        System.out.println("====================================================\n");

        int passed = 0;
        int failed = 0;

        // ---------------------------------------------------------
        // 1. Locations & Facilities Hierarchy
        // ---------------------------------------------------------
        try {
            Location surface = new MartianSurface();
            Habitat hab = new Habitat("Main Habitat Alpha", 500);

            if ("Martian Surface".equals(surface.getName()) &&
                "Main Habitat Alpha".equals(hab.getName()) &&
                hab.isOperational()) {
                System.out.println("[PASS] 1. Location and Facility initialization.");
                passed++;
            } else {
                System.err.println("[FAIL] 1. Location and Facility initialization check failed.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 1. Exception: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 2. Equipment Aggregation & Usability State
        // ---------------------------------------------------------
        Equipment drill = null;
        try {
            drill = new Equipment("Core Drill D-1", EquipmentType.TOOLKIT);
            if (drill.isUsable() && drill.getType() == EquipmentType.TOOLKIT) {
                System.out.println("[PASS] 2. Equipment creation and initial operational state.");
                passed++;
            } else {
                System.err.println("[FAIL] 2. Equipment state incorrect.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 2. Exception: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 3. Engineer Repairs Equipment & Damaged Facility
        // ---------------------------------------------------------
        try {
            Laboratory lab = new Laboratory("Astrobiology Lab", 300);
            Engineer eng = new Engineer("Mark Watney", "ENG-007", "HEALTHY", lab, new String[]{"ELECTRICAL", "MECHANICAL"});

            lab.setOperational(false);
            if (drill != null) {
                drill.damage();
            }

            eng.repair(lab);
            if (drill != null) {
                eng.repairEquipment(drill);
            }

            boolean drillRepaired = (drill == null) || drill.isUsable();
            if (lab.isOperational() && drillRepaired && eng.getFatigueLevel() > 0.0) {
                System.out.println("[PASS] 3. Engineer repaired facility and equipment (fatigue increased).");
                passed++;
            } else {
                System.err.println("[FAIL] 3. Engineer repair logic failed.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 3. Exception: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 4. MedicalOfficer Treating an Injured Human Agent
        // ---------------------------------------------------------
        try {
            Location surface = new MartianSurface();
            Scientist sci = new Scientist("Dr. Stone", "SCI-042", "HEALTHY", surface, new String[]{"SURFACE_ANALYSIS"});
            MedicalOfficer medic = new MedicalOfficer("Dr. McCoy", "MED-001", "HEALTHY", surface, new String[]{"TRAUMA"});

            sci.setHealthStatus("INJURED");
            boolean unavailableWhenInjured = !sci.isAvailable();

            medic.treat(sci);
            boolean healthyAfterTreatment = "HEALTHY".equalsIgnoreCase(sci.getHealthStatus()) && sci.isAvailable();
            boolean fatigueIncreased = medic.getFatigueLevel() > 0.0;

            if (unavailableWhenInjured && healthyAfterTreatment && fatigueIncreased) {
                System.out.println("[PASS] 4. MedicalOfficer treated injured crew member back to available.");
                passed++;
            } else {
                System.err.println("[FAIL] 4. Medical treat mechanics failed. Details: " +
                    "unavailableWhenInjured=" + unavailableWhenInjured + 
                    ", healthyAfterTreatment=" + healthyAfterTreatment + 
                    ", fatigueIncreased=" + fatigueIncreased);
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 4. Exception: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 5. ExplorationRover: Equipment Transport & Exploration
        // ---------------------------------------------------------
        try {
            Location field = new MartianSurface();
            ExplorationRover rover = new ExplorationRover(field, "OPERATIONAL", 100.0);
            Equipment scanner = new Equipment("Spectrometer", EquipmentType.EXPLORATION_KIT);

            rover.loadEquipment(scanner);
            rover.explore();

            if (rover.getCarriedEquipment().contains(scanner) && rover.getBatteryLevel() < 100.0) {
                System.out.println("[PASS] 5. ExplorationRover loaded equipment and explored (battery drained).");
                passed++;
            } else {
                System.err.println("[FAIL] 5. ExplorationRover mechanics failed.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 5. Exception: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 6. RepairRobot Restoring an Inactive Facility
        // ---------------------------------------------------------
        try {
            PowerStation solarArray = new PowerStation("Primary Solar Grid", 200);
            RepairRobot bot = new RepairRobot(solarArray, "OPERATIONAL", 100.0);

            solarArray.setOperational(false);
            bot.repair(solarArray);

            if (solarArray.isOperational() && bot.getBatteryLevel() < 100.0) {
                System.out.println("[PASS] 6. RepairRobot repaired facility and consumed battery.");
                passed++;
            } else {
                System.err.println("[FAIL] 6. RepairRobot mechanics failed.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 6. Exception: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 7. Drone Scouting
        // ---------------------------------------------------------
        try {
            Location ridge = new MartianSurface();
            Drone drone = new Drone(ridge, "OPERATIONAL", 50.0);

            drone.scout();

            if (drone.getBatteryLevel() < 50.0) {
                System.out.println("[PASS] 7. Drone scouted terrain and consumed battery.");
                passed++;
            } else {
                System.err.println("[FAIL] 7. Drone scout mechanics failed.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 7. Exception: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 8. ResourceManager & AllocationPolicy Consumption
        // ---------------------------------------------------------
        try {
            StorageFacility storage = new StorageFacility("Central Depo", 1000);
            storage.getStore().add(ResourceType.OXYGEN, 300);
            storage.getStore().add(ResourceType.WATER, 200);

            List<Facility> facilityList = new ArrayList<>();
            facilityList.add(storage);

            AllocationPolicy policy = new StorageFirstPolicy();
            ResourceManager manager = new ResourceManager(facilityList, policy);

            int totalO2 = manager.getTotal(ResourceType.OXYGEN);

            Map<ResourceType, Integer> order = new HashMap<>();
            order.put(ResourceType.OXYGEN, 100);

            boolean supplyOk = manager.canSupply(order);
            ResourceManager.Result result = manager.consume(order);
            int remainingO2 = manager.getTotal(ResourceType.OXYGEN);

            if (totalO2 == 300 && supplyOk && result.isSuccess() && remainingO2 == 200) {
                System.out.println("[PASS] 8. ResourceManager verified totals and successfully drained resources.");
                passed++;
            } else {
                System.err.println("[FAIL] 8. ResourceManager consumption mechanics failed.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 8. Exception: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // Summary
        // ---------------------------------------------------------
        System.out.println("\n====================================================");
        System.out.println("   TOTAL RESULTS: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("====================================================");
    }
}