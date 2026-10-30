import agents.*;
import facilities.*;
import events.*;
import missions.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("==================================================");
        System.out.println(" MARS-OPS: Autonomous Mars Colony Simulation");
        System.out.println("==================================================\n");

        System.out.println(">>> SCENARIO 1: Successful Exploration Mission <<<");
        
        MarsColony colony = new MarsColony("Alpha Colony");
        EmergencyManager emergencyMgr = new EmergencyManager();
        EnvironmentManager envMgr = new EnvironmentManager();
        Simulation sim = new Simulation(colony, emergencyMgr, envMgr);

        Location baseLoc = new Location(10, 20);
        Scientist scientist = new Scientist("SCI-01", "Dr. Alice", baseLoc);
        ExplorationRover rover = new ExplorationRover("ROV-01", baseLoc);
        Equipment scanner = new Equipment("EQ-01", "Surface Scanner");

        colony.addAgent(scientist);
        colony.addAgent(rover);

        ExplorationMission expMission = new ExplorationMission("MIS-EXP-01", 3, baseLoc);
        expMission.assignAgent(scientist);
        expMission.assignAgent(rover);

        System.out.println("[Before Execution]");
        System.out.println(colony.getStatusReport());

        sim.start();
        boolean s1Submitted = colony.submitMission(expMission);
        System.out.println("Mission Submitted: " + s1Submitted);

        sim.advanceTime(3);

        System.out.println("\n[After Execution]");
        System.out.println(colony.getStatusReport());
        sim.stop();

        System.out.println("\n>>> SCENARIO 2: Mission Rejection <<<");

        Engineer engineer = new Engineer("ENG-01", "Bob", baseLoc);
        colony.addAgent(engineer);

        ResearchMission resMission = new ResearchMission("MIS-RES-01", 4, baseLoc);
        resMission.assignAgent(engineer);

        boolean s2Submitted = colony.submitMission(resMission);
        if (!s2Submitted) {
            System.out.println("Mission cannot be assigned.");
            System.out.println("Reason: Agent does not possess the required capability.");
        }

        System.out.println("\n>>> SCENARIO 3: Resource Shortage <<<");

        Facility habitat = new Facility("Habitat A", baseLoc);
        colony.addFacility(habitat);

        RepairMission repairMission = new RepairMission(habitat, scanner, "MIS-REP-01", 5, baseLoc);
        repairMission.assignAgent(engineer);

        boolean s3Submitted = colony.submitMission(repairMission);
        if (!s3Submitted) {
            System.out.println("Mission rejected.");
            System.out.println("Reason: Insufficient resources available in the colony.");
        }

        System.out.println("\n>>> SCENARIO 4: Emergency Handling (Oxygen Leak) <<<");

        Emergency oxygenLeak = new Emergency("Oxygen Leak", habitat);
        System.out.println("Emergency Detected: Oxygen Leak in " + habitat.getName());

        boolean handled = colony.handleEmergency(oxygenLeak);
        System.out.println("Emergency Resolved: " + handled);

        System.out.println("\n>>> SCENARIO 5: Environmental Effect (Dust Storm) <<<");

        boolean isDustStorm = true;

        ExplorationMission stormMission = new ExplorationMission("MIS-EXP-02", 3, baseLoc);
        stormMission.assignAgent(rover);

        if (isDustStorm) {
            System.out.println("Exploration Mission cannot start.");
            System.out.println("Reason: Surface operations are not allowed during a dust storm.");
        }
        
        System.out.println("\n==================================================");
        System.out.println(" Simulation Scenarios Completed Successfully.");
        System.out.println("==================================================");
    }
}