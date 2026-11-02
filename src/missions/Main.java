package missions;

import agents.*;
import events.*;
import facilities.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("                   MARS-OPS SIMULATOR DEMONSTRATION SUITE                       ");
        System.out.println("================================================================================\n");

        // =============================================================================
        // SCENARIO 1: Successful Exploration (Lab Spec Section 12, Scenario 1)
        // Create a scientist, an exploration rover, appropriate equipment, and an
        // exploration mission. The mission should execute successfully. Show state before & after.
        // =============================================================================
        System.out.println("================================================================================");
        System.out.println("SCENARIO 1: Successful Exploration");
        System.out.println("================================================================================");

        MarsColony colony1 = new MarsColony("Ares Base Alpha");
        Location surface = new MartianSurface();
        Habitat hab1 = new Habitat("Living Module 1", 500);
        hab1.getStore().add(ResourceType.OXYGEN, 300);
        colony1.addFacility(hab1);

        Scientist scientist = new Scientist("Dr. Evelyn Reed", "SCI-101", "HEALTHY", surface, new String[]{"SURFACE_ANALYSIS"});
        ExplorationRover rover = new ExplorationRover(surface, "OPERATIONAL", 100.0);
        colony1.addAgent(scientist);
        colony1.addAgent(rover);

        List<Equipment> equipList = new ArrayList<>();
        Equipment coreDrill = new Equipment("Surface Core Drill", EquipmentType.EXPLORATION_KIT);
        equipList.add(coreDrill);
        rover.loadEquipment(coreDrill);

        ExplorationMission expMission = new ExplorationMission("EXP-001", 3, surface, equipList);
        List<Agent> expTeam = new ArrayList<>();
        expTeam.add(scientist);
        expTeam.add(rover);
        expMission.assignAgents(expTeam);

        System.out.println("[State BEFORE Execution]");
        System.out.println("  Scientist   : " + scientist.getName() + " | Health: " + scientist.getHealthStatus() + 
                           " | Fatigue: " + scientist.getFatigueLevel() + "% | Available: " + scientist.isAvailable());
        System.out.println("  Rover       : Battery: " + rover.getBatteryLevel() + "% | Health: " + rover.getHealthStatus() + 
                           " | Available: " + rover.isAvailable());
        System.out.println("  Equipment   : " + coreDrill.getName() + " | Usable: " + coreDrill.isUsable());
        System.out.println("  Mission     : ID: " + expMission.getId() + " | Status: " + expMission.getStatus());
        System.out.println("  Colony Active Mission: " + (colony1.getActiveMission() != null ? colony1.getActiveMission().getId() : "None"));

        // Submit to colony
        boolean submitted = colony1.submitMission(expMission);
        System.out.println("\nSubmitting mission to colony: " + (submitted ? "ACCEPTED" : "REJECTED"));
        System.out.println("Colony Active Mission: " + colony1.getActiveMission().getId() + " [" + colony1.getActiveMission().getStatus() + "]");

        // Start and execute mission
        boolean started = expMission.start();
        System.out.println("Starting mission: " + (started ? "IN_PROGRESS" : "FAILED"));
        System.out.println("  Rover available during mission: " + rover.isAvailable());
        System.out.println("  Scientist available during mission: " + scientist.isAvailable());

        expMission.execute(); // Rover explores: battery drains
        System.out.println("Executing mission steps...");
        expMission.updateProgress(3); // Progress by 3 units to complete

        System.out.println("\n[State AFTER Execution]");
        System.out.println("  Mission     : ID: " + expMission.getId() + " | Status: " + expMission.getStatus());
        System.out.println("  Scientist   : Health: " + scientist.getHealthStatus() + " | Available: " + scientist.isAvailable());
        System.out.println("  Rover       : Battery: " + rover.getBatteryLevel() + "% | Available: " + rover.isAvailable());
        System.out.println("  Colony Active Mission: " + (colony1.getActiveMission() != null ? colony1.getActiveMission().getId() : "None"));
        System.out.println("Scenario 1 Completed Successfully.\n");

        // =============================================================================
        // SCENARIO 2: Mission Rejection (Lab Spec Section 12, Scenario 2)
        // Submit a mission with an unsuitable agent. The mission must fail immediately
        // with a meaningful reason and must never become active.
        // =============================================================================
        System.out.println("================================================================================");
        System.out.println("SCENARIO 2: Mission Rejection (Ineligible Agent)");
        System.out.println("================================================================================");

        MarsColony colony2 = new MarsColony("Ares Base Beta");
        Engineer unsuitedEngineer = new Engineer("Bob Builder", "ENG-002", "HEALTHY", surface, new String[]{"WELDING"});
        colony2.addAgent(unsuitedEngineer);

        ExplorationMission rejectedMission = new ExplorationMission("EXP-REJECT", 2, surface, equipList);
        List<Agent> invalidTeam = new ArrayList<>();
        invalidTeam.add(unsuitedEngineer); // Engineer cannot perform ExplorationMission
        rejectedMission.assignAgents(invalidTeam);

        boolean subResult = colony2.submitMission(rejectedMission);
        if (!subResult) {
            System.out.println("Mission cannot be assigned.");
            System.out.println("Reason: " + rejectedMission.getFailureReason());
        }
        System.out.println("Mission Status: " + rejectedMission.getStatus());
        System.out.println("Colony Active Mission: " + (colony2.getActiveMission() != null ? colony2.getActiveMission().getId() : "None (Never became active)"));
        System.out.println("Scenario 2 Completed Successfully.\n");

        // =============================================================================
        // SCENARIO 3: Resource Shortage (Lab Spec Section 12, Scenario 3)
        // Attempt to execute a mission when the required resources are insufficient.
        // Rejects operation without consuming unavailable resources or negative values.
        // =============================================================================
        System.out.println("================================================================================");
        System.out.println("SCENARIO 3: Resource Shortage");
        System.out.println("================================================================================");

        MarsColony colony3 = new MarsColony("Ares Base Gamma");
        StorageFacility storage3 = new StorageFacility("Central Depo", 500);
        storage3.getStore().add(ResourceType.SPARE_PARTS, 2); // Colony only has 2 spare parts
        colony3.addFacility(storage3);

        Engineer eng3 = new Engineer("Alice", "ENG-003", "HEALTHY", storage3, new String[]{"ELECTRONICS"});
        colony3.addAgent(eng3);

        PowerStation damagedPower = new PowerStation("Solar Farm", 300);
        damagedPower.setOperational(false);
        colony3.addFacility(damagedPower);

        // Mission requires 10 spare parts, colony only has 2
        RepairMission expensiveRepair = new RepairMission(damagedPower, null, "REP-EXPENSIVE", 2, damagedPower);
        expensiveRepair.addRequiredResource(ResourceType.SPARE_PARTS, 10);
        List<Agent> engTeam = new ArrayList<>();
        engTeam.add(eng3);
        expensiveRepair.assignAgents(engTeam);

        System.out.println("Initial Colony Spare Parts: " + (int) colony3.getTotalResource("SpareParts"));
        System.out.println("Mission Requires          : 10 Spare Parts");

        boolean resourceSubmitted = colony3.submitMission(expensiveRepair);
        System.out.println("Mission Submission Result : " + (resourceSubmitted ? "ACCEPTED" : "REJECTED"));
        System.out.println("Failure Reason Recorded   : \"" + expensiveRepair.getFailureReason() + "\"");
        System.out.println("Colony Spare Parts After  : " + (int) colony3.getTotalResource("SpareParts") + " (Unchanged, no negative values)");
        System.out.println("Colony Active Mission     : " + (colony3.getActiveMission() != null ? colony3.getActiveMission().getId() : "None"));
        System.out.println("Scenario 3 Completed Successfully.\n");

        // =============================================================================
        // SCENARIO 4: Emergency Handling (Lab Spec Section 12, Scenario 4)
        // Simulate an oxygen leak:
        // 1. Detect emergency. 2. Locate affected facility. 3. Identify suitable responders.
        // 4. Perform response. 5. Consume required resources. 6. Update facility state.
        // =============================================================================
        System.out.println("================================================================================");
        System.out.println("SCENARIO 4: Emergency Handling (Oxygen Leak)");
        System.out.println("================================================================================");

        MarsColony colony4 = new MarsColony("Ares Base Delta");
        EmergencyManager emergencyMgr = new EmergencyManager(colony4);
        colony4.setEmergencyManager(emergencyMgr);

        Habitat habitatAlpha = new Habitat("Habitat Alpha", 400);
        habitatAlpha.getStore().add(ResourceType.OXYGEN, 200);
        colony4.addFacility(habitatAlpha);

        StorageFacility emergencyStorage = new StorageFacility("Supply Depot", 500);
        emergencyStorage.getStore().add(ResourceType.SPARE_PARTS, 5); // 5 spare parts in colony
        colony4.addFacility(emergencyStorage);

        Engineer responderEngineer = new Engineer("Sarah Connor", "ENG-009", "HEALTHY", surface, new String[]{"LIFE_SUPPORT"});
        RepairRobot responderRobot = new RepairRobot(surface, "OPERATIONAL", 100.0);
        colony4.addAgent(responderEngineer);
        colony4.addAgent(responderRobot);

        System.out.println("Initial Habitat Oxygen : " + habitatAlpha.getOxygen() + " units");
        System.out.println("Initial Spare Parts    : " + (int) colony4.getTotalResource("SpareParts") + " units");

        // 1. Detect emergency & 2. Locate affected facility
        System.out.println("\n[Step 1 & 2] Emergency Detected: Oxygen leak of 50 units in Habitat Alpha!");
        OxygenLeakEmergency leak = new OxygenLeakEmergency("EMG-O2-001", 4, EmergencyStatus.ACTIVE, habitatAlpha, 50.0, colony4);
        emergencyMgr.report(leak);

        // 3. Identify suitable responders
        System.out.println("[Step 3] Checking agent capabilities for OxygenLeakEmergency:");
        System.out.println("  Engineer can handle leak? " + responderEngineer.canHandle(leak));
        System.out.println("  RepairRobot can handle leak? " + responderRobot.canHandle(leak));

        // 4. Perform response & 5. Consume resources & 6. Update facility state
        System.out.println("[Step 4 & 5 & 6] Dispatching responders to Habitat Alpha and repairing leak...");
        boolean resolved = emergencyMgr.respond(leak);

        System.out.println("Emergency Response Success : " + resolved);
        System.out.println("Emergency Status           : " + leak.getStatus());
        System.out.println("Habitat Oxygen Remaining   : " + habitatAlpha.getOxygen() + " units (50 deducted)");
        System.out.println("Colony Spare Parts Left    : " + (int) colony4.getTotalResource("SpareParts") + " units (2 consumed for repair)");
        System.out.println("Responders New Location    : " + responderEngineer.getLocation().getName());
        System.out.println("Habitat Life Support State : " + habitatAlpha.getLifeSupport());
        System.out.println("Scenario 4 Completed Successfully.\n");

        // =============================================================================
        // SCENARIO 5: Environmental Effect (Lab Spec Section 12, Scenario 5)
        // Introduce an environmental condition (dust storm) and demonstrate that it affects
        // an operation (prevents exploration mission from starting).
        // =============================================================================
        System.out.println("================================================================================");
        System.out.println("SCENARIO 5: Environmental Effect (Dust Storm)");
        System.out.println("================================================================================");

        MarsColony colony5 = new MarsColony("Ares Base Epsilon");
        EnvironmentManager envMgr = new EnvironmentManager();
        colony5.setEnvironmentManager(envMgr);

        ExplorationRover stormRover = new ExplorationRover(surface, "OPERATIONAL", 100.0);
        colony5.addAgent(stormRover);

        ExplorationMission stormMission = new ExplorationMission("EXP-STORM", 3, surface, equipList);
        List<Agent> stormTeam = new ArrayList<>();
        stormTeam.add(stormRover);
        stormMission.assignAgents(stormTeam);

        // Introduce dust storm
        System.out.println("Introducing environmental condition: DUST_STORM (Severity 5.0, Duration 3 units)...");
        EnvironmentalCondition storm = new EnvironmentalCondition(EnvironmentType.DUST_STORM, 5.0, 3);
        envMgr.addCondition(storm);

        System.out.println("Active environmental conditions: " + envMgr.getActiveConditions().size());
        System.out.println("Attempting to submit surface exploration mission during dust storm...");

        boolean stormSubmitted = colony5.submitMission(stormMission);
        if (!stormSubmitted) {
            System.out.println("Exploration Mission cannot start.");
            System.out.println("Reason: " + stormMission.getFailureReason());
        }
        System.out.println("Mission Status: " + stormMission.getStatus());
        System.out.println("Colony Active Mission: " + (colony5.getActiveMission() != null ? colony5.getActiveMission().getId() : "None"));
        System.out.println("Scenario 5 Completed Successfully.\n");

        // =============================================================================
        // ADDITIONAL EDGE CASES (Edge cases from original test suite)
        // =============================================================================
        System.out.println("================================================================================");
        System.out.println("ADDITIONAL SYSTEM VALIDATIONS & EDGE CASES");
        System.out.println("================================================================================");

        // Edge Case 1: Depleted Battery Rover
        ExplorationRover deadRover = new ExplorationRover(surface, "OPERATIONAL", 0.0);
        ExplorationMission deadMission = new ExplorationMission("MIS-DEAD", 2, surface, equipList);
        List<Agent> deadTeam = new ArrayList<>();
        deadTeam.add(deadRover);
        deadMission.assignAgents(deadTeam);
        boolean deadStarted = deadMission.start();
        System.out.println("1. Depleted Battery Rover: canPerform=" + deadRover.canPerform(deadMission) + 
                           ", start()=" + deadStarted + ", status=" + deadMission.getStatus());

        // Edge Case 2: Double-booking busy agent
        ExplorationRover busyRover = new ExplorationRover(surface, "OPERATIONAL", 100.0);
        ExplorationMission activeM = new ExplorationMission("MIS-ACTIVE", 5, surface, equipList);
        ExplorationMission secondM = new ExplorationMission("MIS-SECOND", 5, surface, equipList);
        List<Agent> teamActive = new ArrayList<>();
        teamActive.add(busyRover);
        activeM.assignAgents(teamActive);
        activeM.start();

        List<Agent> teamSecond = new ArrayList<>();
        teamSecond.add(busyRover);
        secondM.assignAgents(teamSecond);
        boolean secondStarted = secondM.start();
        System.out.println("2. Double-booking Busy Agent: second start()=" + secondStarted + 
                           ", rover available=" + busyRover.isAvailable() + ", second status=" + secondM.getStatus());

        // Edge Case 3: Simulation Time Advancement & Progress Clamping
        Simulation sim = new Simulation(colony1, emergencyMgr, envMgr);
        sim.start();
        sim.advanceTime(2);
        System.out.println("3. Simulation Time Advanced: Current Time = " + sim.getCurrentTime());

        System.out.println("\n================================================================================");
        System.out.println("                   ALL DEMONSTRATION SCENARIOS COMPLETED                        ");
        System.out.println("================================================================================");
    }
}