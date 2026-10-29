package agents;
import events.*;
import facilities.*;
import missions.*;
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
            for (String s : getSkills()) {
                if ("SURFACE_ANALYSIS".equalsIgnoreCase(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        return false;
    }

    public void performResearch() {
        increaseFatigue(15.0);
    }
}