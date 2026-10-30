package events;

import missions.MarsColony;

public class EnvironmentalCondition {
    private EnvironmentType type;
    private double severity;
    private int durationRemaining;
    private boolean active;

    public EnvironmentalCondition(EnvironmentType type, double severity, int durationRemaining) {
        this.type = type;
        this.severity = severity;
        this.durationRemaining = durationRemaining;
        this.active = true;
    }

    public EnvironmentType getType() { return type; }
    public double getSeverity() { return severity; }
    public int getDurationRemaining() { return durationRemaining; }
    public boolean isActive() { return active; }

    public void apply(MarsColony colony) {
        if (!active) return;
    }

    public void update(int units) {
        if (!active) return;
        this.durationRemaining -= units;
        if (this.durationRemaining <= 0) {
            this.durationRemaining = 0;
            this.active = false;
        }
    }
}
