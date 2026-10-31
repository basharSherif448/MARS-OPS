package events;

import agents.Agent;
import facilities.Habitat;
import missions.MarsColony;

public class OxygenLeakEmergency extends Emergency {
    private Habitat affectedHabitat;
    private double oxygenLossAmount;
    private boolean initialOxygenLossApplied;

    public OxygenLeakEmergency(String id, int severity, EmergencyStatus status, Habitat affectedHabitat, double oxygenLossAmount, MarsColony marsColony) {
        super(id, severity, status, affectedHabitat, marsColony);
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

        Agent responder1 = null;
        Agent responder2 = null;

        for (Agent agent : this.marsColony.getAgents()) {
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
            failEmergency("Insufficient available responders");
            return false;
        }

        responder1.setLocation(affectedHabitat);
        responder2.setLocation(affectedHabitat);

        if (!hasEnoughRepairResources()) {
            failEmergency("Insufficient repair resources");
            return false;
        }

        consumeRepairResources();

        this.resolve();
        return true;
    }

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

