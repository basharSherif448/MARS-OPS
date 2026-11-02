package missions;

import events.EmergencyManager;
import events.EnvironmentManager;

public class Simulation {

    private MarsColony colony;
    private long currentTime;
    private EmergencyManager emergencyManager;
    private EnvironmentManager environmentManager;
    private boolean isRunning;

    public Simulation(MarsColony colony, EmergencyManager emergencyManager, EnvironmentManager environmentManager) {
        this.colony = colony;
        this.currentTime = 0;
        this.emergencyManager = emergencyManager;
        this.environmentManager = environmentManager;
        this.isRunning = false;

        if (this.colony != null) {
            if (this.colony.getEmergencyManager() == null) {
                this.colony.setEmergencyManager(emergencyManager);
            }
            if (this.colony.getEnvironmentManager() == null) {
                this.colony.setEnvironmentManager(environmentManager);
            }
        }
    }

    public void start() {
        this.isRunning = true;
        System.out.println("Simulation started.");
    }

    public void stop() {
        this.isRunning = false;
        System.out.println("Simulation stopped.");
    }

    public void advanceTime(int units) {
        if (!isRunning) {
            System.out.println("Cannot advance time: Simulation is not running.");
            return;
        }
        this.currentTime += units;
        if (colony != null) {
            Mission activeMission = colony.getActiveMission();
            if (activeMission != null) {
                if (activeMission.getStatus() == MissionStatus.ASSIGNED) {
                    colony.startActiveMission();
                }
                if (activeMission.getStatus() == MissionStatus.IN_PROGRESS) {
                    if (environmentManager != null && environmentManager.blocks(activeMission)) {
                        activeMission.fail("Environmental condition caused active mission to fail.");
                    } else {
                        activeMission.updateProgress(units);
                    }
                }
            }
        }
        if (emergencyManager != null) {
            emergencyManager.update(units);
        }

        if (environmentManager != null) {
            environmentManager.update(units);
        }
    }

    public MarsColony getColony() {
        return colony;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public EmergencyManager getEmergencyManager() {
        return emergencyManager;
    }

    public EnvironmentManager getEnvironmentManager() {
        return environmentManager;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
