package agents;



public class Drone extends Robot {

    public Drone(Location currentLocation, String healthStatus, double batteryLevel) {
        super(currentLocation, healthStatus, batteryLevel);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (!isAvailable()) {
            return false;
        }
        return (mission instanceof ExplorationMission);
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        return false;
    }

    public void scout() {
        if (isAvailable()) {
            drainBattery(10.0);
        }
    }
}