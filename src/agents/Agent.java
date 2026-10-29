package agents;



public interface Agent {
    boolean canPerform(Mission mission);
    boolean canHandle(Emergency emergency);
    boolean isAvailable();
    void setAvailable(boolean available);
    Location getLocation();
    void setLocation(Location location);
}