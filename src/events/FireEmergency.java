package colony.events;
import agents.Agent;
import facilities.Facility;

import java.util.ArrayList;
import java.util.List;
public class FireEmergency extends Emergency {

    private Facility affectedFacility;
    public FireEmergency(String id, int severity, EmergencyStatus state, Facility affectedFacility, MarsColony marsColony) {
        super(id, severity, state, affectedFacility, marsColony);
        this.affectedFacility = affectedFacility;
    }

    public Facility getAffectedFacility() {
        return affectedFacility;
    }
    

    @Override
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
            this.markUnresolved("Insufficient responders for fire emergency"); // وسم الطوارئ كـ UNRESOLVED
            return false;
        }

        responder1.setLocation(affectedFacility);
        responder2.setLocation(affectedFacility);

        // فحص توافر الموارد اللازمة لإخماد الحريق عبر ResourceManager (Section 5)
        if (!hasEnoughFireSupplies()) {
            this.markUnresolved("Insufficient fire suppression resources"); // فشل لعدم توفر الموارد بدون استهلاك
            return false;
        }

        consumeFireSupplies();

        affectedFacility.setOperational(true);
        this.resolve();
        return true;
    }

    private boolean hasEnoughFireSupplies() {
        return this.marsColony.getTotalResource("Water") >= 10.0;
    }

    private void consumeFireSupplies() {
        this.marsColony.getResourceManager().consume("Water", 10.0);
    }
}
