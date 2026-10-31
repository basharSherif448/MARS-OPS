package missions;

import agents.*;
import java.util.ArrayList;
import java.util.List;
import facilities.*;
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
        if (assignedAgents != null) {
            for (Agent agent : assignedAgents) {
                agent.setAvailable(false);
            }
        }
        return true;
    } else {
        // Just record failure status and reason; do NOT release agents here
        this.status = MissionStatus.FAILED;
        this.failureReason = "Conditions not met to start mission.";
        return false;
    }
}

    public void assignAgents(List<Agent> agents) {
    this.assignedAgents = agents;
}

   public void updateProgress(int units) {
        if (this.status == MissionStatus.IN_PROGRESS) {
            this.elapsedUnits += units;
            if (this.elapsedUnits >= this.duration) {
                this.elapsedUnits = this.duration; // Manual clamping to total duration
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
    boolean wasInProgress = (this.status == MissionStatus.IN_PROGRESS);
    this.status = MissionStatus.FAILED;
    this.failureReason = reason;
    
    // Only release agents if they were actually locked during IN_PROGRESS
    if (wasInProgress && assignedAgents != null) {
        for (Agent agent : assignedAgents) {
            agent.setAvailable(true);
        }
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
