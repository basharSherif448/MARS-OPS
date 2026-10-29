package agents;


import java.util.Arrays;

public abstract class Human implements Agent {
    private String name;
    private String identificationNumber;
    private String healthStatus;
    private Location currentLocation;
    private String[] skills;
    private double fatigueLevel;
    private boolean available;

    public Human(String name, String identificationNumber, String healthStatus, 
                 Location currentLocation, String[] skills) {
        
       
        if (name == null || name.isEmpty()) {
            this.name = "Unknown Human";
        } else {
            this.name = name;
        }

      
        if (identificationNumber == null || identificationNumber.isEmpty()) {
            this.identificationNumber = "ID-UNASSIGNED";
        } else {
            this.identificationNumber = identificationNumber.trim();
        }

        
        if (healthStatus == null || healthStatus.isEmpty()) {
            this.healthStatus = "HEALTHY";
        } else {
            this.healthStatus = healthStatus;
        }

        this.currentLocation = currentLocation;
      if (skills != null) {
        this.skills = skills;      
    } else {
        this.skills = new String[0]; 
    }
        this.fatigueLevel = 0.0;
        this.available = true;
    }

    @Override
    public abstract boolean canPerform(Mission mission);

    @Override
    public abstract boolean canHandle(Emergency emergency);

    @Override
    public boolean isAvailable() {
        return this.available  && this.healthStatus!="INJURED"  && this.fatigueLevel < 100.0;
    }

    @Override
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public Location getLocation() {
        return this.currentLocation;
    }

    @Override
    public void setLocation(Location location) {
        this.currentLocation = location;
    }

   public void increaseFatigue(double amount) {
    if(amount>=0)
         this.fatigueLevel += amount;
}

   
    
    public String getName() { return name; }
    public String getIdentificationNumber() { return identificationNumber; }
    
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { 
        if (healthStatus != null && !healthStatus.isEmpty()) {
                  this.healthStatus = healthStatus;
        }
    }
    
    public double getFatigueLevel() { return fatigueLevel; }
    public String[] getSkills() { 
        return skills;

     }
}