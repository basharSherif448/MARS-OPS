package agents;
import events.*;
import facilities.*;
import missions.*;
public class MedicalOfficer extends Human {

    public MedicalOfficer(String name, String identificationNumber, String healthStatus, 
                          Location currentLocation, String[] skills) {
        super(name, identificationNumber, healthStatus, currentLocation, skills);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (!isAvailable()) {
            return false;
        }
        return (mission instanceof RescueMission);
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        if (!isAvailable()) {
            return false;
        }
        return (emergency instanceof MedicalEmergency);
    }

    public void treat(Human patient) {
        if (patient != null) {
            patient.setHealthStatus("HEALTHY");
            increaseFatigue(10.0);
        }
    }
}