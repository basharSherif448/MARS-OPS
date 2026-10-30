import agents.*;
import facilities.*;
import missions.*;

import java.util.ArrayList;
import java.util.List;

public class MissionTestRunner {
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("   MARS-OPS 3-PACKAGE INTEGRATION TEST SUITE        ");
        System.out.println("   (agents + facilities + missions)                 ");
        System.out.println("====================================================\n");

        int passed = 0;
        int failed = 0;

        // ---------------------------------------------------------
        // 1. ExplorationMission: Assignment, Start, and Execution
        // ---------------------------------------------------------
        try {
            Location surface = new MartianSurface();
            ExplorationRover rover = new ExplorationRover(surface, "OPERATIONAL", 100.0);
            List<Equipment> kit = new ArrayList<>();
            kit.add(new Equipment("Field Scanner", EquipmentType.EXPLORATION_KIT));

            ExplorationMission expMission = new ExplorationMission("EXP-01", 3, surface, kit);

            List<Agent> team = new ArrayList<>();
            team.add(rover);
            expMission.assignAgents(team);

            boolean started = expMission.start();
            boolean inProgress = (expMission.getStatus() == MissionStatus.IN_PROGRESS);
            boolean roverUnavailable = !rover.isAvailable();

            expMission.execute(); // Rover explores: battery drains
            boolean batteryDrained = rover.getBatteryLevel() < 100.0;

            expMission.updateProgress(3); // Completes mission
            boolean completed = (expMission.getStatus() == MissionStatus.COMPLETED);
            boolean roverReleased = rover.isAvailable();

            if (started && inProgress && roverUnavailable && batteryDrained && completed && roverReleased) {
                System.out.println("[PASS] 1. ExplorationMission lifecycle & Rover autonomous work verified.");
                passed++;
            } else {
                System.err.println("[FAIL] 1. ExplorationMission checks failed: started=" + started +
                        ", inProgress=" + inProgress + ", roverUnavailable=" + roverUnavailable +
                        ", batteryDrained=" + batteryDrained + ", completed=" + completed +
                        ", roverReleased=" + roverReleased);
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 1. ExplorationMission exception: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        // ---------------------------------------------------------
        // 2. RepairMission: Facility and Equipment Restoration
        // ---------------------------------------------------------
        try {
            PowerStation station = new PowerStation("Aux Power Substation", 300);
            station.setOperational(false);

            Equipment tool = new Equipment("Welding Rig", EquipmentType.TOOLKIT);
            tool.damage();

            RepairMission repairMission = new RepairMission(station, tool, "REP-01", 2, station);

            Engineer eng = new Engineer("Bob", "ENG-099", "HEALTHY", station, new String[]{"WELDING"});
            List<Agent> team = new ArrayList<>();
            team.add(eng);

            repairMission.assignAgents(team);
            boolean started = repairMission.start();

            repairMission.execute(); // repairs facility and equipment

            boolean facilityRestored = station.isOperational();
            boolean toolRestored = tool.isUsable();

            repairMission.updateProgress(2);
            boolean completed = (repairMission.getStatus() == MissionStatus.COMPLETED);

            if (started && facilityRestored && toolRestored && completed) {
                System.out.println("[PASS] 2. RepairMission restored damaged facility and equipment.");
                passed++;
            } else {
                System.err.println("[FAIL] 2. RepairMission failed: started=" + started +
                        ", facilityRestored=" + facilityRestored +
                        ", toolRestored=" + toolRestored +
                        ", completed=" + completed);
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 2. RepairMission exception: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        // ---------------------------------------------------------
        // 3. Validation: Mission Rejects Ineligible Agents
        // ---------------------------------------------------------
        try {
            Location surface = new MartianSurface();
            MedicalOfficer medic = new MedicalOfficer("Dr. Crusher", "MED-002", "HEALTHY", surface, new String[]{"SURGERY"});

            // ExplorationMission requires an ExplorationRover
            ExplorationMission expMission = new ExplorationMission("EXP-INVALID", 2, surface, null);
            List<Agent> wrongTeam = new ArrayList<>();
            wrongTeam.add(medic);
            expMission.assignAgents(wrongTeam);

            boolean couldStart = expMission.start();
            boolean failedStatus = (expMission.getStatus() == MissionStatus.FAILED);

            if (!couldStart && failedStatus) {
                System.out.println("[PASS] 3. Mission correctly rejected non-compliant agent roster.");
                passed++;
            } else {
                System.err.println("[FAIL] 3. Mission started with ineligible agents.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 3. Ineligible roster exception: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 4. MarsColony: Multi-Package Registry & Active Mission Flow
        // ---------------------------------------------------------
        try {
            MarsColony colony = new MarsColony("Ares Base Prime");

            Habitat hab = new Habitat("Living Dome 1", 500);
            hab.getStore().add(ResourceType.OXYGEN, 150);
            colony.addFacility(hab);

            Location surface = new MartianSurface();
            RepairRobot bot = new RepairRobot(surface, "OPERATIONAL", 100.0);
            colony.addAgent(bot);

            PowerStation solar = new PowerStation("Solar Array Alpha", 200);
            solar.setOperational(false);
            colony.addFacility(solar);

            RepairMission mission = new RepairMission(solar, null, "COLONY-REP-01", 1, solar);
            List<Agent> team = new ArrayList<>();
            team.add(bot);
            mission.assignAgents(team);

            boolean submitted = colony.submitMission(mission);
            boolean activeMatches = (colony.getActiveMission() == mission);

            String report = colony.getStatusReport();
            boolean reportValid = report.contains("Ares Base Prime") && report.contains("COLONY-REP-01");

            if (submitted && activeMatches && reportValid) {
                System.out.println("[PASS] 4. MarsColony registered entities and managed active mission lifecycle.");
                passed++;
            } else {
                System.err.println("[FAIL] 4. MarsColony integration failed: submitted=" + submitted +
                        ", activeMatches=" + activeMatches + ", reportValid=" + reportValid);
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] 4. MarsColony exception: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        System.out.println("\n====================================================");
        System.out.println("   TOTAL RESULTS: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("====================================================");
    }
}