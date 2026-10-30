package missions;

import java.util.ArrayList;
import java.util.List;

import agents.Agent;
import agents.Drone;
import agents.MedicalOfficer;
import facilities.Location;

public class RescueMission extends Mission {

    private Agent strandedEntity;

    public RescueMission(Agent strandedEntity, String id, int duration, Location target) {
        super(id, duration, target);
        this.strandedEntity = strandedEntity;
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
            if (agent instanceof MedicalOfficer || agent instanceof Drone) {
                flag = true;
            }
        }
        return flag;
    }

    public void execute() {
        if (getStatus() == MissionStatus.IN_PROGRESS) {
            if (strandedEntity != null) {
                strandedEntity.setLocation(getTarget());
                strandedEntity.setAvailable(true);
            }
        }
    }

    public Agent getStrandedEntity() {
        return strandedEntity;
    }
}
