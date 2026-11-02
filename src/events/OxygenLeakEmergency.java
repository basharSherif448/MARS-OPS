package events;

import agents.Agent;
import facilities.Habitat;
import missions.MarsColony;

public class OxygenLeakEmergency extends Emergency {
    private Habitat affectedHabitat;
    private double oxygenLossAmount;
    private boolean initialOxygenLossApplied;
    // in our logic Leaks do not drain oxygen continuously over time so i made a falg here to consum one time only

    public OxygenLeakEmergency(String id, int severity, EmergencyStatus status, Habitat affectedHabitat, double oxygenLossAmount, MarsColony marsColony) {
        super(id, severity, status, affectedHabitat, marsColony);
        // the locatoin is the same location of the Habitat
        this.affectedHabitat = affectedHabitat;
        this.oxygenLossAmount = oxygenLossAmount;
        this.initialOxygenLossApplied = false;
    }

    @Override
    public boolean respond() {
        if (super.getStatus() != EmergencyStatus.ACTIVE || affectedHabitat == null) {
            return false;
        }

        if (!initialOxygenLossApplied) {
            affectedHabitat.reduceOxygen(oxygenLossAmount);
            initialOxygenLossApplied = true;
        }
        // the number of responder is diffrenent in each
        Agent responder1 = null;
        Agent responder2 = null;

        for (Agent agent : this.marsColony.getAgents()) {
            // we here applying poly we donot chech the type
            // it will be available if it is not part of an active mission and is in usable condition
            if (agent.isAvailable() && agent.canHandle(this)) {
                if (responder1 == null) {
                    responder1 = agent;
                } else if (responder2 == null) {
                    responder2 = agent;
                    break;
                }
            }
        }

        if (responder1 == null || responder2 == null) {
            //failEmergency i applyed here because the mission of Emergency is failed
            failEmergency("Insufficient available responders");
            return false;
        }
        // we can chage any to any location to hadel the missions

        responder1.setLocation(affectedHabitat);
        responder2.setLocation(affectedHabitat);

        if (!hasEnoughRepairResources()) {
            //failEmergency i applyed here because the mission of Emergency is failed
            failEmergency("Insufficient repair resources");
            return false;
        }

        consumeRepairResources();

        this.resolve();
        return true;
    }
   // to consum again the same amount because it is unresolved
    private void failEmergency(String reason) {
        affectedHabitat.reduceOxygen(oxygenLossAmount);
        this.markUnresolved(reason);
    }

    private boolean hasEnoughRepairResources() {
        return this.marsColony.getTotalResource("SpareParts") >= 2.0;
    }

    // FIX: was canConsume(...) (check only) -> now really consumes the spare parts
    private void consumeRepairResources() {
        if (this.marsColony.getResourceManager() != null) {
            this.marsColony.getResourceManager().consume("SpareParts", 2.0);
        }
    }
}

