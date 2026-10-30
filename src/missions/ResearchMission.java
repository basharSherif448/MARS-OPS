package missions;

import java.util.ArrayList;
import java.util.List;

import agents.Agent;
import agents.Scientist;
import facilities.Equipment;
import facilities.Location;

public class ResearchMission extends Mission {

    private String experimentName;
    private List<Equipment> requiredEquipment = new ArrayList<>();

    public ResearchMission(String id, int duration, Location target, String experimentName, List<Equipment> requiredEquipment) {
        super(id, duration, target);
        this.experimentName = experimentName;
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
            if (agent instanceof Scientist) {
                flag = true;
            }
        }
        return flag;
    }

    public void execute() {
        if (getStatus() == MissionStatus.IN_PROGRESS) {
            System.out.println("Executing research experiment: " + experimentName);
        }
    }

    public String getExperimentName() {
        return experimentName;
    }

    public List<Equipment> getRequiredEquipment() {
        return requiredEquipment;
    }
}
