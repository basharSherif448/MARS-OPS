package agents;

import events.*;
import facilities.*;
import missions.*;

public class Engineer extends Human {

    public Engineer(String name, String id, String healthStatus, Location currentLocation, String[] specializations) {
        super(name, id, healthStatus, currentLocation, specializations);
    }

    @Override
    public boolean canPerform(Mission mission) {
        return (mission instanceof RepairMission) && this.isAvailable();
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        return emergency instanceof FireEmergency 
            || emergency instanceof OxygenLeakEmergency 
            || emergency instanceof RoverBreakdownEmergency;
    }
}