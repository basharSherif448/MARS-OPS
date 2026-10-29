package colony.facilities;

public class CommunicationStation extends Facility {
    public CommunicationStation(String name, int capacity) { super(name, capacity); }
    @Override public boolean providesCommunication() { return isOperational(); }
}
