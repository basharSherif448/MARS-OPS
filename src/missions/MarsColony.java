package missions;

import java.util.ArrayList;
import java.util.List;

import agents.Agent;
import facilities.Facility;

public class MarsColony {

    private String name;
    private List<Agent> agents = new ArrayList<>();
    private List<Facility> facilities = new ArrayList<>();
    private List<Mission> missions = new ArrayList<>();
    private Mission activeMission;

    public MarsColony(String name) {
        this.name = name;
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
        if (activeMission != null && activeMission.getStatus() == MissionStatus.IN_PROGRESS) {
            return false;
        }
        if (mission.start()) {
            this.activeMission = mission;
            this.missions.add(mission);
            return true;
        }
        return false;
    }

    public double getTotalResource(String name) {
        if (name == null) {
            return 0.0;
        }
        double total = 0.0;
        for (Facility facility : facilities){
            if(facility != null && facility.isOperational()){               
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
        report.append("Active Mission: ")
              .append(activeMission != null ? activeMission.getId() + " [" + activeMission.getStatus() + "]" : "None")
              .append("\n");

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
        return activeMission;
    }
}
