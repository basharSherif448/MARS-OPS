package colony.events;
import facilities.Habitat;
import agents.Human;
import agents.Robot;

public class OxygenLeakEmergency extends Emergency {
    private  Habitat affectedHabitat ;
    private double oxygenLossAmount;
    private boolean initialOxygenLossApplied;
    public OxygenLeakEmergency(String id, int severity, EmergencyState state, Habitat affectedHabitat, double oxygenLossAmount, MarsColony marsColony) {
        super(id, severity, state, affectedHabitat, marsColony); // location هو affectedHabitat
        this.affectedHabitat = affectedHabitat;
        this.oxygenLossAmount = oxygenLossAmount;
    }
    @Override
    public boolean respond() {
        if (super.getState() != EmergencyState.ACTIVE|| affectedHabitat == null) {
            return false;
        }
        if (!initialOxygenLossApplied) {
            affectedHabitat.reduceOxygen(oxygenLossAmount);
            initialOxygenLossApplied = true;
        }
        Human humanResponder = findAvailableHumanRepairer();
        Robot robotResponder = findAvailableRobotRepairer();

        if (humanResponder == null || robotResponder == null) {
            failEmergency("Insufficient responders ");
            return false;
        }
        humanResponder.setloaction(affectedHabitat);
        robotResponder.setloaction(affectedHabitat);
        if (!colonyHasEnoughResourcesForRepair()) {
            failEmergency("Insufficient resources ");
            return false;
        }
        consumeRepairResources();
        boolean repair= performRepairLogic(humanResponder, robotResponder);
        if (repair) {
            this.resolve();// to update the statue
            return true;
        }
        else  {
            failEmergency("Repair execution failed during the operation.");

            return false;
        }



    }
    private void failEmergency(String reason) {
        affectedHabitat.reduceOxygen(oxygenLossAmount);


        this.markUnresolved(reason);
    }

    private Human getAvailableHumanResponder() {
        for (Agent agent : this.marsColony.getAgents()) {
            if (agent instanceof Human && agent.isAvailable() && agent.canHandle(this)) {
                return (Human) agent;
            }
        }
        return null;
    }

    private Robot getAvailableRobotResponder() {
        for (Agent agent : this.marsColony.getAgents()) {
            if (agent instanceof Robot && agent.isAvailable() && agent.canHandle(this)) {
                return (Robot) agent;
            }
        }
        return null;
    }

    private boolean hasEnoughRepairResources() {
        double availableParts = this.marsColony.getTotalResource("SpareParts");
        return availableParts >= 2.0;
    }

    private void consumeRepairResources() {
       marsColony.getResourceManager().consume("SpareParts", 2.0);
    }

    private boolean executeRepair(Human h, Robot r) {

        return true;
    }


}
