package events;

import missions.MarsColony;

import java.util.ArrayList;
import java.util.List;

public class EmergencyManager {
    private List<Emergency> activeEmergencies;
    private MarsColony marsColony;

    public EmergencyManager(MarsColony marsColony) {
        this.activeEmergencies = new ArrayList<>();
        this.marsColony = marsColony;
    }

    public void report(Emergency e) {
        if (e == null) {
            System.out.println("Emergency is null");
        } else if (activeEmergencies.contains(e)) {
            System.out.println("Emergency already active.");
        } else if (e.getStatus() != EmergencyStatus.ACTIVE) {
            System.out.println("Emergency not active.");
        } else {
            activeEmergencies.add(e);
        }
    }
    //
    public void update() {
        List<Emergency> copy = new ArrayList<>(activeEmergencies);
        for (Emergency emergency : copy) {
            if (emergency.getStatus() != EmergencyStatus.ACTIVE) {
                activeEmergencies.remove(emergency);
            }
        }
    }

    //
    public void update(int units) {
        update();
    }

    public boolean respond(Emergency e) {
        if (e == null || !activeEmergencies.contains(e) || e.getStatus() != EmergencyStatus.ACTIVE) {
            return false;
        }
        return e.respond();
    }

    public List<Emergency> getActiveEmergencies() { return activeEmergencies; }
}