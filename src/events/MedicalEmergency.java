package events;

import agents.Agent;
import agents.Human;
import facilities.ResourceType;
import colony.missions.MarsColony;

import java.util.EnumMap;
import java.util.Map;

public class MedicalEmergency extends Emergency {
    private Human patient;

    public MedicalEmergency(String id, int severity, EmergencyStatus status, Human patient, MarsColony marsColony) {
        super(id, severity, status, patient.getLocation(), marsColony);
        this.patient = patient;
    }

    public Human getPatient() {
        return patient;
    }

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
        return this.marsColony.getResourceManager().getTotal(ResourceType.MEDICAL_SUPPLIES) >= 1;
    }

    private void consumeMedicalSupplies() {
        Map<ResourceType, Integer> req = new EnumMap<>(ResourceType.class);
        req.put(ResourceType.MEDICAL_SUPPLIES, 1);
        this.marsColony.getResourceManager().consume(req);
    }
}