package colony.events;
import colony.agents.Agent;
import colony.facilities.Facility;

import java.util.ArrayList;
import java.util.List;
public class FireEmergency extends Emergency {

    private Facility affectedFacility;
    public FireEmergency(String id, int severity, EmergencyState state, Facility affectedFacility, MarsColony marsColony) {
        super(id, severity, state, affectedFacility, marsColony);
        this.affectedFacility = affectedFacility;
    }
    
    @Override
    public boolean respond() {
        if ( super.getState() != EmergencyState.ACTIVE ||  affectedFacility == null ) {
            return false;
        }
        List<Agent> requiredResponders = getAvailableFireResponders();
        if (requiredResponders == null || requiredResponders.isEmpty()) {
            this.markUnresolved("Insufficient fire-response personnel or medical officers.");
            return false;
        }
        for (Agent responder : requiredResponders) {
            responder.setLocation(affectedFacility);
        }
        if (!hasEnoughFirefightingResources()) {
            this.markUnresolved("Insufficient resources .");
            return false;
        }
        consumeFirefightingResources();
        boolean firefightingSuccess = executeFirefighting(requiredResponders);



        if (firefightingSuccess) {
            applyFireConsequences(true);
            this.resolve();
            return true;
        } else {
            applyFireConsequences(false);
            this.markUnresolved("Firefighting failed. The laboratory is severely compromised.");
            return false;
        }
    }
    private void applyFireConsequences(boolean isResolved) {
        if (isResolved) {

            damageSomeEquipment(2);
            increaseOxygenConsumption(10.0);
        } else {

            affectedFacility.setOperational(false); // not availble
            damageAllEquipment();
            increaseOxygenConsumption(50.0);
        }
    }
    private List<Agent> getAvailableFireResponders() {
        List<Agent> team = new ArrayList<>();
        for (Agent agent : colony.getAgents()) {
            if (agent.isAvailable() && agent.canHandle(this)) {
                team.add(agent);
            }
        }
        return team;
    }








    }
}
