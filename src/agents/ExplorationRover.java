package agents;

import java.util.ArrayList;
import java.util.List;
import facilities.*;
import missions.*;
import events.*;

public class ExplorationRover extends Robot {
    private List<Equipment> carriedEquipment;

    public ExplorationRover(Location currentLocation, String healthStatus, double batteryLevel) {
        super(currentLocation, healthStatus, batteryLevel);
        this.carriedEquipment = new ArrayList<>();
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

    public void loadEquipment(Equipment eq) {
        if (eq != null) {
            this.carriedEquipment.add(eq);
        }
    }

    public void explore() {
        if (getBatteryLevel() > 0.0) {
            drainBattery(20.0);
        }
    }

    public List<Equipment> getCarriedEquipment() {
        return carriedEquipment;
    }
}