package agents;



public class Scientist extends Human {

    public Scientist(String name, String identificationNumber, String healthStatus, 
                     Location currentLocation, String[] skills) {
        super(name, identificationNumber, healthStatus, currentLocation, skills);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (!isAvailable()) {
            return false;
        }

        
        if (mission instanceof ResearchMission) {
            return true;
        }

    
        if (mission instanceof ExplorationMission) {
            for (String skill : getSkills()) {
                if ("SURFACE_ANALYSIS".equalsIgnoreCase(skill)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        // Scientists do not respond to physical/structural or medical emergencies
        return false;
    }

    public void performResearch() {
        increaseFatigue(15.0);
    }
}