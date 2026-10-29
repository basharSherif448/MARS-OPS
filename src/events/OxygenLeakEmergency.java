package colony.events;
import agents.Agent;
import facilities.Habitat;
import agents.Human;
import agents.Robot;

public class OxygenLeakEmergency extends Emergency {
    private  Habitat affectedHabitat ;
    private int oxygenLossAmount;
    private boolean initialOxygenLossApplied;
    public OxygenLeakEmergency(String id, int severity, EmergencyStatus state, Habitat affectedHabitat, int oxygenLossAmount, MarsColony marsColony) {
        super(id, severity, state, affectedHabitat, marsColony); // location هو affectedHabitat
        this.affectedHabitat = affectedHabitat;
        this.oxygenLossAmount = oxygenLossAmount;
        this.initialOxygenLossApplied = false;
    }
    @Override
    public boolean respond() {
        if (super.getStatus() != EmergencyStatus.ACTIVE || affectedHabitat == null) {
            return false;
        }

        // خصم الأوكسجين المبدئي
        if (!initialOxygenLossApplied) {
            affectedHabitat.loseOxygen(oxygenLossAmount);
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

        responder1.setLocation(affectedHabitat.);
        responder2.setLocation(affectedHabitat);


        if (!hasEnoughRepairResources()) {
            failEmergency("Insufficient repair resources");
            return false;
        }


        consumeRepairResources();

        boolean repairSuccess = executeRepair(responder1, responder2);
        if (repairSuccess) {
            this.resolve();
            return true;
        } else {
            failEmergency("Repair execution failed during operation.");
            return false;
        }
    }

    private void failEmergency(String reason) {
        affectedHabitat.loseOxygen(oxygenLossAmount);
        this.markUnresolved(reason);
    }

    private boolean hasEnoughRepairResources() {
        double availableParts = this.marsColony.getTotalResource("SpareParts");
        return availableParts >= 2.0;
    }

    private void consumeRepairResources() {
        this.marsColony.getResourceManager().consume("SpareParts", 2.0);
    }

    private boolean executeRepair(Agent a1, Agent a2) {
        return true;
    }
}






