package agents;
import facilities.*;
import events.*;
import missions.*;
public abstract class Human implements Agent {
    private String name;
    private String identificationNumber;
    private String healthStatus;
    private Location currentLocation;
    private String[] skills;
    private double fatigueLevel;
    private boolean available;

    public Human(String name, String identificationNumber, String healthStatus, 
                 Location currentLocation, String[] skills)
     {
        this.name = name;
        this.identificationNumber = identificationNumber;
        
        if (healthStatus == null || healthStatus.isEmpty()) {
            this.healthStatus = "HEALTHY";
        } else {
            this.healthStatus = healthStatus.toUpperCase();
        }

        this.currentLocation = currentLocation;
         if (skills != null) {
        this.skills = skills;      
    } else {
        this.skills = new String[0]; 
        this.fatigueLevel = 0.0;
        this.available = true;
    }
}

    @Override
    public abstract boolean canPerform(Mission mission);

    @Override
    public abstract boolean canHandle(Emergency emergency);

   
   @Override
public boolean isAvailable() {
    return "HEALTHY".equalsIgnoreCase(healthStatus) && fatigueLevel < 100.0;
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

    public void increaseFatigue(double amount) {
        if (amount > 0.0) {
            this.fatigueLevel = this.fatigueLevel + amount;
            if (this.fatigueLevel > 100.0) {
                this.fatigueLevel = 100.0;
            }
        }
    }

    public String getName() { return name; }
    public String getIdentificationNumber() { return identificationNumber; }
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) {
        if (healthStatus != null && !healthStatus.isEmpty()) {
            this.healthStatus = healthStatus.toUpperCase();
        }
    }
    public String[] getSkills() { return skills; }

    public double getFatigueLevel() 
    { 
        return fatigueLevel;
     }
}