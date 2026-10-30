package missions;

import agents.Agent;
import agents.Engineer;
import agents.RepairRobot;
import facilities.Facility;
import facilities.Equipment;
import facilities.Location;

public class RepairMission extends Mission {

    private Facility targetFacility;
    private Equipment targetEquipment;

    public RepairMission(Facility targetFacility, Equipment targetEquipment, String id, int duration, Location target) {
        super(id, duration, target);
        this.targetFacility = targetFacility;
        this.targetEquipment = targetEquipment;
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
            if (agent instanceof Engineer || agent instanceof RepairRobot) {
                flag = true;
            }
        }
        return flag;
    }

    public void execute() {
        if (getStatus() == MissionStatus.IN_PROGRESS) {
            if (targetFacility != null) {
                targetFacility.setOperational(true);
            }
            if (targetEquipment != null) {
                targetEquipment.setOperational(true);
            }
        }
    }

    public Facility getTargetFacility() {
        return targetFacility;
    }

    public Equipment getTargetEquipment() {
        return targetEquipment;
    }
}
