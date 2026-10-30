package events;

import agents.Agent;
import facilities.Habitat;
import facilities.ResourceType;
import colony.missions.MarsColony;

import java.util.EnumMap;
import java.util.Map;

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
        return this.marsColony.getResourceManager().getTotal(ResourceType.SPARE_PARTS) >= 2;
    }

    private void consumeRepairResources() {
        Map<ResourceType, Integer> req = new EnumMap<>(ResourceType.class);
        req.put(ResourceType.SPARE_PARTS, 2);
        this.marsColony.getResourceManager().consume(req);
    }
}



