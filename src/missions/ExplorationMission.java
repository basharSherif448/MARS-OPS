package missions;

import agents.Agent;
import agents.ExplorationRover;
import facilities.Equipment;
import facilities.Location;
import java.util.ArrayList;
import java.util.List;

public class ExplorationMission extends Mission {
    private List<Equipment> requiredEquipment;

    public ExplorationMission(String id, int duration, Location target, List<Equipment> requiredEquipment) {
        super(id, duration, target);
        this.requiredEquipment = (requiredEquipment != null) ? new ArrayList<>(requiredEquipment) : new ArrayList<>();
    }

    public List<Equipment> getRequiredEquipment() {
        return requiredEquipment;
    }

    @Override
    public boolean canStart() {
        if (getAssignedAgents() == null || getAssignedAgents().isEmpty()) {
            return false;
        }

        // Refuse to start if required equipment is unusable/broken
        if (requiredEquipment != null) {
            for (Equipment eq : requiredEquipment) {
                if (eq == null || !eq.isUsable()) {
                    return false;
                }
            }
        }

        for (Agent agent : getAssignedAgents()) {
            if (!agent.isAvailable() || !agent.canPerform(this)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void execute() {
        for (Agent agent : getAssignedAgents()) {
            if (agent instanceof ExplorationRover) {
                ((ExplorationRover) agent).explore();
            }
        }
    }
}