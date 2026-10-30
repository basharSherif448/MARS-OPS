package events;
import agents.Agent;
import colony.agents.ExplorationRover;
import facilities.ResourceType;
import colony.missions.MarsColony;

import java.util.EnumMap;
import java.util.Map;

public class RoverBreakdownEmergency extends Emergency {
    private ExplorationRover rover;

    public RoverBreakdownEmergency(String id, int severity, EmergencyStatus status, ExplorationRover rover, MarsColony marsColony) {
        super(id, severity, status, rover.getLocation(), marsColony);
        this.rover = rover;
    }

    public ExplorationRover getRover() {
        return rover;
    }

    @Override
    public boolean respond() {
        if (super.getStatus() != EmergencyStatus.ACTIVE || rover == null) {
            return false;
        }

        rover.setAvailable(false);

        Agent repairer = null;

        for (Agent agent : this.marsColony.getAgents()) {
            if (agent.isAvailable() && agent.canHandle(this)) {
                repairer = agent;
                break;
            }
        }

        if (repairer == null) {
            this.markUnresolved("No available repair units for broken rover");
            return false;
        }

        repairer.setLocation(rover.getLocation());

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
        return this.marsColony.getResourceManager().getTotal(ResourceType.SPARE_PARTS) >= 2;
    }

    private void consumeRepairParts() {
        Map<ResourceType, Integer> req = new EnumMap<>(ResourceType.class);
        req.put(ResourceType.SPARE_PARTS, 2);
        this.marsColony.getResourceManager().consume(req);
    }
}