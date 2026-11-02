package events;

import agents.Agent;
import agents.ExplorationRover;
import missions.MarsColony;

public class RoverBreakdownEmergency extends Emergency {
    private ExplorationRover rover;

    public RoverBreakdownEmergency(String id, int severity, EmergencyStatus status, ExplorationRover rover, MarsColony marsColony) {
        //"A rover that breaks down remains where it broke down while in the fire the place is the facility
        super(id, severity, status, rover.getLocation(), marsColony);
        this.rover = rover;
    }

    public ExplorationRover getRover() { return rover; }

    @Override
    public boolean respond() {
        // The unresolved emergency is not retried automaticall so we here check the Status
        if (super.getStatus() != EmergencyStatus.ACTIVE || rover == null) {
            return false;
        }

        rover.setAvailable(false);
        // the number of responder is diffrenent in each emergency
        // me deciection here to get one responder in RoverBreakdownEmergency and RoverBreakdownEmergency
        // and in the FireEmergency,FireEmergency tow responder
        Agent repairer = null;

        for (Agent agent : this.marsColony.getAgents()) {
            // Emergencies do not interrupt active missions so we check if it a not active to get it or it in a mession
            if (agent.isAvailable() && agent.canHandle(this)) {
                repairer = agent;
                break;
            }
        }

        if (repairer == null) {
            this.markUnresolved("No available repairers for broken rover");
            return false;
        }

        repairer.setLocation(rover.getLocation());

        // to save a resources all or no thing and we check the available
        if (!hasEnoughRepairParts()) {
            this.markUnresolved("Insufficient spare parts to repair rover");
            return false;
        }

        consumeRepairParts();

        rover.setAvailable(true);
        this.resolve();
        return true;
    }

    private boolean hasEnoughRepairParts() {
        return this.marsColony.getTotalResource("SpareParts") >= 2.0;
    }

    //
    private void consumeRepairParts() {
        if (this.marsColony.getResourceManager() != null) {
            this.marsColony.getResourceManager().consume("SpareParts", 2.0);
        }
    }
}
