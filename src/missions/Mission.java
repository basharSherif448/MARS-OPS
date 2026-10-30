package missions;

import java.util.ArrayList;
import java.util.List;

import agents.Agent;
import agents.ExplorationRover;
import facilities.Location;
import facilities.Equipment;

public abstract class Mission {

    private String id;
    private int duration;
    private int elapsedUnits = 0;
    private MissionStatus status = MissionStatus.ASSIGNED;
    private List<Agent> assignedAgents = new ArrayList<>();
    private Location target;
    private String failureReason;

    public Mission(String id, int duration, Location target) {
        this.id = id;
        this.duration = duration;
        this.target = target;
    }

    public abstract boolean canStart();

    public boolean start() {
        if (canStart()) {
            this.status = MissionStatus.IN_PROGRESS;
            this.elapsedUnits = 0;
            return true;
        } else {
            fail("Conditions not met to start mission.");
            return false;
        }
    }

    public void assignAgents(List<Agent> agents) {
        this.assignedAgents = agents;
        for (Agent agent : agents) {
            agent.setAvailable(false);
        }
    }

    public void updateProgress(int units) {
        if (this.status == MissionStatus.IN_PROGRESS) {
            this.elapsedUnits += units;
            if (this.elapsedUnits >= this.duration) {
                complete();
            }
        }
    }

    public void complete() {
        this.status = MissionStatus.COMPLETED;
        for (Agent agent : assignedAgents) {
            agent.setAvailable(true);
        }
    }

    public void fail(String reason) {
        this.status = MissionStatus.FAILED;
        this.failureReason = reason;
        for (Agent agent : assignedAgents) {
            agent.setAvailable(true);
        }
    }

    public String getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public int getElapsedUnits() {
        return elapsedUnits;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public List<Agent> getAssignedAgents() {
        return assignedAgents;
    }

    public Location getTarget() {
        return target;
    }

    public String getFailureReason() {
        return failureReason;
    }

}
