package agents;
import missions.*;
import facilities.*;
import events.*;

public interface Agent {
    boolean canPerform(Mission mission);
    boolean canHandle(Emergency emergency);
    boolean isAvailable();
    Location getLocation();
    void setLocation(Location location);
}