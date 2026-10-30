package events;

import agents.Agent;
import facilities.Facility;
import facilities.ResourceType;
import colony.missions.MarsColony;

import java.util.EnumMap;
import java.util.Map;

public class FireEmergency extends Emergency {
    private Facility affectedFacility;

    public FireEmergency(String id, int severity, EmergencyStatus status, Facility affectedFacility, MarsColony marsColony) {
        super(id, severity, status, affectedFacility, marsColony);
        this.affectedFacility = affectedFacility;
    }

    public Facility getAffectedFacility() {
        return affectedFacility;
    }

    @Override
    public boolean respond() {
        if (super.getStatus() != EmergencyStatus.ACTIVE || affectedFacility == null) {
            return false;
        }

        affectedFacility.setOperational(false);

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
            this.markUnresolved("Insufficient responders for fire emergency");
            return false;
        }

        responder1.setLocation(affectedFacility);
        responder2.setLocation(affectedFacility);

        if (!hasEnoughFireSupplies()) {
            this.markUnresolved("Insufficient fire suppression resources");
            return false;
        }

        consumeFireSupplies();

        affectedFacility.setOperational(true);
        this.resolve();
        return true;
    }

    private boolean hasEnoughFireSupplies() {
        return this.marsColony.getResourceManager().getTotal(ResourceType.WATER) >= 10;
    }

    private void consumeFireSupplies() {
        Map<ResourceType, Integer> req = new EnumMap<>(ResourceType.class);
        req.put(ResourceType.WATER, 10);
        this.marsColony.getResourceManager().consume(req);
    }
}