
package events;

import facilities.Location;
import missions.MarsColony;

public abstract class Emergency {
    private String id;
    private int severity;
    private EmergencyStatus status = EmergencyStatus.ACTIVE;
    private Location location;
    private String unresolvedReason;
    protected MarsColony marsColony;

    public Emergency(String id, int severity, EmergencyStatus status, Location location, MarsColony marsColony) {
        this.id = id;
        this.severity = severity;
        this.status = status;
        this.location = location;
        this.unresolvedReason = null;
        this.marsColony = marsColony;
    }

    public String getId() { return id; }
    public int getSeverity() { return severity; }
    public EmergencyStatus getStatus() { return status; }
    public void setStatus(EmergencyStatus status) { this.status = status; }
    public Location getLocation() { return location; }
    public String getUnresolvedReason() { return unresolvedReason; }
    public void setUnresolvedReason(String unresolvedReason) { this.unresolvedReason = unresolvedReason; }

    public void resolve() {
        if (this.getStatus() == EmergencyStatus.RESOLVED) {
            System.out.println("Emergency is already resolved");
        } else {
            setStatus(EmergencyStatus.RESOLVED);
            setUnresolvedReason(null);
        }
    }

    public abstract boolean respond();

    public void markUnresolved(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            System.out.println("Reason is invalid");
        } else {
            setStatus(EmergencyStatus.UNRESOLVED);
            setUnresolvedReason(reason);
            System.out.println("Marked unresolved for " + this.getId());
        }
    }
}

