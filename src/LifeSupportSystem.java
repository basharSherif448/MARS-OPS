package colony.facilities;

public class LifeSupportSystem implements Repairable {
    private boolean leaking;

    public boolean isLeaking() { return leaking; }
    public void startLeak() { leaking = true; }
    @Override public String getName() { return "LifeSupportSystem"; }
    @Override public boolean isDamaged() { return leaking; }
    @Override public void repair() { leaking = false; }
    @Override public String toString() { return leaking ? "LEAKING" : "NOMINAL"; }
}
