package agents;
import missions.*;
import facilities.*;
import events.*;
public class Engineer extends Human {

    public Engineer(String name, String identificationNumber, String healthStatus, 
                    Location currentLocation, String[] skills) {
        super(name, identificationNumber, healthStatus, currentLocation, skills);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (!isAvailable()) {
            return false;
        }
        return (mission instanceof RepairMission);
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        if (!isAvailable()) {
            return false;
        }
        return (emergency instanceof OxygenLeakEmergency) || (emergency instanceof FireEmergency);
    }

    public void repair(Facility facility) {
        if (facility != null) {
            facility.setOperational(true);
            increaseFatigue(20.0);
        }
    }

    public void repairEquipment(Equipment eq) {
        if (eq != null) {
            eq.repair();
            increaseFatigue(10.0);
        }
    }
}