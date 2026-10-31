package events;

import agents.Agent;
import agents.Human;
import missions.MarsColony;

public class MedicalEmergency extends Emergency {
    private Human patient;

    public MedicalEmergency(String id, int severity, EmergencyStatus status, Human patient, MarsColony marsColony) {
        super(id, severity, status, patient.getLocation(), marsColony);
        this.patient = patient;
    }

    public Human getPatient() { return patient; }

    @Override
    public boolean respond() {
        if (super.getStatus() != EmergencyStatus.ACTIVE || patient == null) {
            return false;
        }

        Agent medicalOfficer = null;

        for (Agent agent : this.marsColony.getAgents()) {
            if (agent.isAvailable() && agent.canHandle(this)) {
                medicalOfficer = agent;
                break;
            }
        }

        if (medicalOfficer == null) {
            this.markUnresolved("No available medical officer for patient");
            return false;
        }

        medicalOfficer.setLocation(patient.getLocation());

        if (!hasEnoughMedicalSupplies()) {
            this.markUnresolved("Insufficient medical supplies in colony");
            return false;
        }

        consumeMedicalSupplies();

        patient.setHealthStatus("HEALTHY");

        this.resolve();
        return true;
    }

    private boolean hasEnoughMedicalSupplies() {
        return this.marsColony.getTotalResource("MedicalSupplies") >= 1.0;
    }

    // FIX: was canConsume(...) -> now really consumes
    private void consumeMedicalSupplies() {
        if (this.marsColony.getResourceManager() != null) {
            this.marsColony.getResourceManager().consume("MedicalSupplies", 1.0);
        }
    }
}
