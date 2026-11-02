package missions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import agents.Agent;
import events.EmergencyManager;
import events.EnvironmentManager;
import facilities.Facility;
import facilities.ResourceManager;
import facilities.ResourceType;
import facilities.StorageFirstPolicy;

public class MarsColony {

    private String name;
    private List<Agent> agents = new ArrayList<>();
    private List<Facility> facilities = new ArrayList<>();
    private List<Mission> missions = new ArrayList<>();
    private Mission activeMission;
    private ResourceManager resourceManager;
    private EnvironmentManager environmentManager;
    private EmergencyManager emergencyManager;

    public MarsColony(String name) {
        this.name = name;
        this.resourceManager = new ResourceManager(this.facilities, new StorageFirstPolicy());
    }

    public void addAgent(Agent agent) {
        if (agent != null) {
            this.agents.add(agent);
        }
    }

    public void addFacility(Facility facility) {
        if (facility != null) {
            this.facilities.add(facility);
        }
    }

    public boolean submitMission(Mission mission) {
        if (mission == null) {
            return false;
        }

        // Rule 1: Check active mission
        if (getActiveMission() != null && 
            (activeMission.getStatus() == MissionStatus.IN_PROGRESS || activeMission.getStatus() == MissionStatus.ASSIGNED)) {
            mission.fail("Another mission is already active.");
            this.missions.add(mission);
            return false;
        }

        // Environmental check
        if (environmentManager != null && environmentManager.blocks(mission)) {
            mission.fail("Surface operations are not allowed during a dust storm.");
            this.missions.add(mission);
            return false;
        }

        // Rule 2: Agent capabilities and availability
        if (mission.getAssignedAgents() == null || mission.getAssignedAgents().isEmpty()) {
            mission.fail("No agents assigned to the mission.");
            this.missions.add(mission);
            return false;
        }

        for (Agent agent : mission.getAssignedAgents()) {
            if (!agent.canPerform(mission)) {
                mission.fail("Agent does not possess the required capability.");
                this.missions.add(mission);
                return false;
            }
            if (!agent.isAvailable()) {
                mission.fail("Assigned agent is not available.");
                this.missions.add(mission);
                return false;
            }
        }

        if (!mission.canStart()) {
            mission.fail(mission.getFailureReason() != null ? mission.getFailureReason() : "Agent does not possess the required capability.");
            this.missions.add(mission);
            return false;
        }

        // Rule 3: Resource check
        if (resourceManager != null && !mission.getRequiredResources().isEmpty()) {
            if (!resourceManager.canSupply(mission.getRequiredResources())) {
                mission.fail("Insufficient resources: Required resources unavailable in colony.");
                this.missions.add(mission);
                return false;
            }
        }

        // Rule 5: Mission accepted -> becomes ASSIGNED
        this.activeMission = mission;
        this.missions.add(mission);
        return true;
    }

    public boolean startActiveMission() {
        if (activeMission != null && activeMission.getStatus() == MissionStatus.ASSIGNED) {
            if (resourceManager != null && !activeMission.getRequiredResources().isEmpty()) {
                if (!resourceManager.canSupply(activeMission.getRequiredResources())) {
                    activeMission.fail("Insufficient resources: Required resources unavailable in colony.");
                    return false;
                }
                resourceManager.consume(activeMission.getRequiredResources());
            }
            return activeMission.start();
        }
        return false;
    }

    public double getTotalResource(String name) {
        if (resourceManager != null) {
            return resourceManager.total(name);
        }
        if (name == null) {
            return 0.0;
        }
        double total = 0.0;
        for (Facility facility : facilities) {
            if (facility != null && facility.isOperational()) {               
                total += facility.getResourceQuantity(name);
            }      
        }
        return total;
    }
    
    public String getStatusReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Mars Colony Status Report: ").append(name).append(" ===\n");
        report.append("Total Agents: ").append(agents.size()).append("\n");
        report.append("Total Facilities: ").append(facilities.size()).append("\n");
        report.append("Total Missions Recorded: ").append(missions.size()).append("\n");
        Mission active = getActiveMission();
        report.append("Active Mission: ")
              .append(active != null ? active.getId() + " [" + active.getStatus() + "]" : "None")
              .append("\n");
        report.append("--- Colony-Wide Resources ---\n");
        if (resourceManager != null) {
            for (Map.Entry<ResourceType, Integer> entry : resourceManager.getTotals().entrySet()) {
                report.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        } else {
            for (ResourceType t : ResourceType.values()) {
                report.append("  ").append(t).append(": ").append((int) getTotalResource(t.name())).append("\n");
            }
        }

        return report.toString();
    }

    public String getName() {
        return name;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public Mission getActiveMission() {
        if (activeMission != null && (activeMission.getStatus() == MissionStatus.COMPLETED || activeMission.getStatus() == MissionStatus.FAILED)) {
            activeMission = null;
        }
        return activeMission;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public EnvironmentManager getEnvironmentManager() {
        return environmentManager;
    }

    public void setEnvironmentManager(EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    public EmergencyManager getEmergencyManager() {
        return emergencyManager;
    }

    public void setEmergencyManager(EmergencyManager emergencyManager) {
        this.emergencyManager = emergencyManager;
    }
}
