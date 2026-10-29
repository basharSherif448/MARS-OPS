package agents;



public abstract class Robot implements Agent {
    private double batteryLevel;
    private String healthStatus;
    private Location currentLocation;
    private boolean available;

    public Robot(Location currentLocation, String healthStatus, double batteryLevel) {
        if (batteryLevel < 0.0) {
            this.batteryLevel = 0.0;
        } else if (batteryLevel > 100.0) {
            this.batteryLevel = 100.0;
        } else {
            this.batteryLevel = batteryLevel;
        }

        this.currentLocation = currentLocation;

        if (healthStatus == null || healthStatus.isEmpty()) {
            this.healthStatus = "OPERATIONAL";
        } else {
            this.healthStatus = healthStatus.toUpperCase();
        }

        this.available = true;
    }

    @Override
    public abstract boolean canPerform(Mission mission);

    @Override
    public abstract boolean canHandle(Emergency emergency);

    @Override
    public boolean isAvailable() {
        return this.available 
                && !"DAMAGED".equalsIgnoreCase(this.healthStatus) 
                && !"DESTROYED".equalsIgnoreCase(this.healthStatus) 
                && this.batteryLevel > 0.0;
    }

    @Override
    public Location getLocation() {
        return this.currentLocation;
    }

    @Override
    public void setLocation(Location location) {
        this.currentLocation = location;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void drainBattery(double amount) {
        if (amount > 0.0) {
            this.batteryLevel = this.batteryLevel - amount;
            if (this.batteryLevel < 0.0) {
                this.batteryLevel = 0.0;
            }
        }
    }

    public double getBatteryLevel() { return batteryLevel; }
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) {
        if (healthStatus != null && !healthStatus.isEmpty()) {
            this.healthStatus = healthStatus.toUpperCase();
        }
    }
}