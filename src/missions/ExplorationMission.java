package missions;

import java.util.ArrayList;
import java.util.List;

import agents.Agent;
import agents.ExplorationRover;
import facilities.Location;
import facilities.Equipment;

public class ExplorationMission extends Mission {

    private Location destination;
    private List<Equipment> requiredEquipment = new ArrayList<>();

    public ExplorationMission(String id, int duration, Location destination, List<Equipment> requiredEquipment) {
        super(id, duration, destination);
        this.destination = destination;
        if (requiredEquipment != null) {
            this.requiredEquipment = requiredEquipment;
        }
    }

    @Override
    public boolean canStart() {
        if (getAssignedAgents() == null || getAssignedAgents().isEmpty()) {
            return false;
        }
        boolean flag = false;
        for (Agent agent : getAssignedAgents()) {
            if (!agent.canPerform(this)) {
                return false;
            }
            if (agent instanceof ExplorationRover) {
                flag = true;
            }
        }
        return flag;
    }

    public void execute() {
        if (getStatus() == MissionStatus.IN_PROGRESS) {
            for (Agent agent : getAssignedAgents()) {
                agent.setLocation(destination);
                if (agent instanceof ExplorationRover) {
                    ((ExplorationRover) agent).explore();
                }
            }
        }
    }

    public Location getDestination() {
        return destination;
    }

    public List<Equipment> getRequiredEquipment() {
        return requiredEquipment;
    }
}
