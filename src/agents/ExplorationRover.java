package agents;

import events.*;
import facilities.*;
import missions.*;

public class ExplorationRover extends Robot {

    public ExplorationRover(Location currentLocation, String healthStatus, double batteryLevel) {
        super(currentLocation, healthStatus, batteryLevel);
    }

    @Override
    public boolean canPerform(Mission mission) {
        return (mission instanceof ExplorationMission) && this.isAvailable();
    }

    @Override
    public boolean canHandle(Emergency emergency) {
        return false;
    }

    private java.util.List<Equipment> carriedEquipment = new java.util.ArrayList<>();

    public void loadEquipment(Equipment eq) {
        if (eq != null) {
            this.carriedEquipment.add(eq);
        }
    }

    public java.util.List<Equipment> getCarriedEquipment() {
        return carriedEquipment;
    }

    public void explore() {
        if (isFunctional()) {
            drainBattery(20.0);
        }
    }
}