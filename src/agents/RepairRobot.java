package agents;

import events.*;
import facilities.*;
import missions.*;

public class RepairRobot extends Robot {

    public RepairRobot(Location currentLocation, String healthStatus, double batteryLevel) {
        super(currentLocation, healthStatus, batteryLevel);
    }

    @Override
    public boolean canPerform(Mission mission) {
        return (mission instanceof RepairMission) && this.isAvailable();
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        return emergency instanceof RoverBreakdownEmergency 
            || emergency instanceof OxygenLeakEmergency;
    }

    public void repairFacility(Facility facility) {
        if (isFunctional() && facility != null) {
            facility.setOperational(true);
            drainBattery(15.0);
        }
    }

    public void repairEquipment(Equipment equipment) {
        if (isFunctional() && equipment != null) {
            equipment.repair();
            drainBattery(10.0);
        }
    }
}