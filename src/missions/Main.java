package missions;

import agents.*;
import facilities.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Location l1 = new MartianSurface();
        ExplorationRover rover = new ExplorationRover(l1, "OPERATIONAL", 100.0);
       
        System.out.println("Rover Battery: " + rover.getBatteryLevel());
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("Surface Core Drill", EquipmentType.EXPLORATION_KIT));
        
        ExplorationMission m1 = new ExplorationMission("1234", 3, l1, equipment);
        System.out.println("Required Equip: " + m1.getRequiredEquipment());
        System.out.println("Initial Status: " + m1.getStatus());
        
        List<Agent> team1 = new ArrayList<>();
        team1.add(rover);
        m1.assignAgents(team1);
        System.out.println("Rover can perform mission? " + rover.canPerform(m1));
        System.out.println("Rover available before start: " + rover.isAvailable());

        boolean started = m1.start();
        System.out.println("3. start()      : Result = " + started + ", Status = " + m1.getStatus());
        
        m1.execute();
        System.out.println("4. execute()    : Rover battery after explore = " + rover.getBatteryLevel() + "%");
        
        m1.updateProgress(2);
        System.out.println("5. Progress +2  : Elapsed = " + m1.getElapsedUnits() + "/" + m1.getDuration() + 
                           ", Status = " + m1.getStatus());
        
        m1.updateProgress(1);
        System.out.println("6. Progress +1  : Elapsed = " + m1.getElapsedUnits() + "/" + m1.getDuration() + 
                           ", Status = " + m1.getStatus());
        
        System.out.println("Final Status    : " + m1.getStatus());
        System.out.println("7. Post-Complete: Rover available = " + rover.isAvailable());

        // -------------------------------------------------------------
        // SCENARIO 2: Failed Mission Lifecycle (Ineligible Agent)
        // -------------------------------------------------------------
        System.out.println("\n--- Scenario 2: Failed Mission Lifecycle ---");
        Scientist sci = new Scientist("Dr. Stone", "SCI-01", "HEALTHY", l1, new String[]{"GEOLOGY"});
        System.out.println("Scientist Health: " + sci.getHealthStatus());
        System.out.println("Scientist canPerform(m1)? " + sci.canPerform(m1));
      
        ExplorationMission mission2 = new ExplorationMission("MIS-002", 2, l1, equipment);
        System.out.println("1. Instantiated : Status = " + mission2.getStatus());
        
        List<Agent> team2 = new ArrayList<>();
        team2.add(sci);
        mission2.assignAgents(team2);
        System.out.println("2. Agents Bound : Scientist available = " + sci.isAvailable());

        boolean startedFail = mission2.start();
        System.out.println("3. start()      : Result = " + startedFail + ", Status = " + mission2.getStatus());
        System.out.println("4. Failure Info : Reason = \"" + mission2.getFailureReason() + "\"");
        System.out.println("5. Post-Failure : Scientist available = " + sci.isAvailable());

        // -------------------------------------------------------------
        // SCENARIO 3: RepairMission (Facility + Tool Restoration)
        // -------------------------------------------------------------
        System.out.println("\n--- Scenario 3: RepairMission Lifecycle ---");
        PowerStation powerStation = new PowerStation("Solar Array Alpha", 250);
        powerStation.setOperational(false);

        Equipment weldingTorch = new Equipment("Arc Welder", EquipmentType.TOOLKIT);
        weldingTorch.damage();

        System.out.println("Initial Facility Status : Operational = " + powerStation.isOperational());
        System.out.println("Initial Equipment Status: Usable = " + weldingTorch.isUsable());

        Engineer eng = new Engineer("Bob", "ENG-007", "HEALTHY", powerStation, new String[]{"ELECTRICAL"});
        RepairMission repairMission = new RepairMission(powerStation, weldingTorch, "REP-101", 2, powerStation);

        List<Agent> repairTeam = new ArrayList<>();
        repairTeam.add(eng);
        repairMission.assignAgents(repairTeam);

        boolean repairStarted = repairMission.start();
        System.out.println("1. start()      : Result = " + repairStarted + ", Status = " + repairMission.getStatus());

        repairMission.execute();
        System.out.println("2. execute()    : Facility Operational = " + powerStation.isOperational() + 
                           ", Equipment Usable = " + weldingTorch.isUsable());

        repairMission.updateProgress(2);
        System.out.println("3. Progress +2  : Status = " + repairMission.getStatus());
        System.out.println("4. Engineer available post-repair = " + eng.isAvailable());

        // -------------------------------------------------------------
        // SCENARIO 4: MarsColony Orchestration
        // -------------------------------------------------------------
        System.out.println("\n--- Scenario 4: MarsColony Orchestration ---");
        MarsColony colony = new MarsColony("Ares Base Prime");

        Habitat habitat = new Habitat("Main Habitat Dome", 400);
        habitat.getStore().add(ResourceType.OXYGEN, 100);
        colony.addFacility(habitat);
        colony.addFacility(powerStation);

        RepairRobot bot = new RepairRobot(l1, "OPERATIONAL", 100.0);
        colony.addAgent(bot);
        colony.addAgent(eng);

        RepairMission colonyMission = new RepairMission(powerStation, null, "COL-REP-01", 1, powerStation);
        List<Agent> autoTeam = new ArrayList<>();
        autoTeam.add(bot);
        colonyMission.assignAgents(autoTeam);

        boolean submitted = colony.submitMission(colonyMission);
        System.out.println("Mission Submitted to Colony : " + submitted);
        System.out.println("Active Colony Mission Matches: " + (colony.getActiveMission() == colonyMission));
        System.out.println("Colony Status Report:\n" + colony.getStatusReport());

        // Scenario 5: Dead battery rover cannot start mission
// -------------------------------------------------------------
        // SCENARIO 5: Edge Case - Depleted Battery Rover
        // -------------------------------------------------------------
        System.out.println("\n--- Scenario 5: Depleted Battery Rover ---");
        ExplorationRover deadRover = new ExplorationRover(l1, "OPERATIONAL", 0.0);
        ExplorationMission deadMission = new ExplorationMission("MIS-DEAD", 2, l1, equipment);
        List<Agent> deadTeam = new ArrayList<>();
        deadTeam.add(deadRover);
        deadMission.assignAgents(deadTeam);

        boolean deadStarted = deadMission.start();
        System.out.println("1. Dead rover canPerform: " + deadRover.canPerform(deadMission));
        System.out.println("2. Dead rover start()   : Result = " + deadStarted + ", Status = " + deadMission.getStatus());

        // -------------------------------------------------------------
        // SCENARIO 6: Edge Case - Agent Concurrency / Double Booking
        // -------------------------------------------------------------
        System.out.println("\n--- Scenario 6: Double Booking Busy Agent ---");
        ExplorationRover busyRover = new ExplorationRover(l1, "OPERATIONAL", 100.0);
        ExplorationMission activeM = new ExplorationMission("MIS-ACTIVE", 5, l1, equipment);
        ExplorationMission secondM = new ExplorationMission("MIS-SECOND", 5, l1, equipment);

        List<Agent> teamActive = new ArrayList<>();
        teamActive.add(busyRover);
        activeM.assignAgents(teamActive);
        activeM.start();

        List<Agent> teamSecond = new ArrayList<>();
        teamSecond.add(busyRover);
        secondM.assignAgents(teamSecond);

        boolean secondStarted = secondM.start();
        System.out.println("1. Second mission start(): Result = " + secondStarted + ", Status = " + secondM.getStatus());
        System.out.println("2. Rover still busy in first mission: " + !busyRover.isAvailable());

        // -------------------------------------------------------------
        // SCENARIO 7: Edge Case - Overshoot Elapsed Progress
        // -------------------------------------------------------------
        System.out.println("\n--- Scenario 7: Elapsed Progress Overshoot ---");
        ExplorationRover overshootRover = new ExplorationRover(l1, "OPERATIONAL", 100.0);
        ExplorationMission overshootM = new ExplorationMission("MIS-OVER", 2, l1, equipment);
        List<Agent> overshootTeam = new ArrayList<>();
        overshootTeam.add(overshootRover);
        overshootM.assignAgents(overshootTeam);
        overshootM.start();

        overshootM.updateProgress(10); // Exceeds duration of 2
        System.out.println("1. Overshoot Status: " + overshootM.getStatus());
        System.out.println("2. Elapsed Units   : " + overshootM.getElapsedUnits() + "/" + overshootM.getDuration());
        System.out.println("3. Rover released  : " + overshootRover.isAvailable());



    }
}