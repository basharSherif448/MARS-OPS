package agents;



public class RepairRobot extends Robot {

    public RepairRobot(Location currentLocation, String healthStatus, double batteryLevel) {
        super(currentLocation, healthStatus, batteryLevel);
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
        if (facility != null && isAvailable()) {
            facility.setOperational(true);
            drainBattery(15.0);
        }
    }

    public void repairEquipment(Equipment eq) {
        if (eq != null && isAvailable()) {
            eq.repair();
            drainBattery(10.0);
        }
    }
}