package agents;
import facilities.Location;
import events.Emergency;

public interface Agent {
    boolean canPerform(Mission mission);
    boolean canHandle(Emergency emergency);
    boolean isAvailable();
    Location getLocation();
    void setLocation(Location location);
}