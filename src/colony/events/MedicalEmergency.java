package colony.events;

public class MedicalEmergency extends Emergency {
    private Human  patient ;
    public MedicalEmergency(String id, int severity, EmergencyState state, String requiredSkill,Human  patient) {
        super(id, severity, state, requiredSkill);
        this.patient = patient;

    }

    @Override
    public boolean respond() {

    }
}
