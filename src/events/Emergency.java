package events;

import facilities.Location;
import missions.MarsColony;

public abstract class  Emergency {
    private String id ;
    private int severity ;
    private EmergencyStatus state = EmergencyStatus.ACTIVE  ;
    private Location location ;
    private String  unresolvedReason;
    protected MarsColony marsColony;
    Emergency(String id, int severity, EmergencyStatus state, Location location, MarsColony marsColony) {
        this.id = id;
        this.severity = severity;
        this.state = state;
        this.location = location;
        this.unresolvedReason = null;
        this.marsColony = marsColony;
    }
    public String getId() {
    return id;
    }
    public int getSeverity() {
        return severity;
    }

    public  void updateState( EmergencyStatus state ) {
        this.state = state;
    }

    public void setStatus( EmergencyStatus state ) {
        this.state = state;
    }
    public EmergencyStatus getStatus(  ) {
        return state;
    }
    public void setUnresolvedReason( String unresolvedReason) {
       this.unresolvedReason = unresolvedReason;
    }
    public void resolve(){
     if (this.getStatus() == EmergencyStatus.RESOLVED){
         System.out.println("Emergency is already resolved");
     }
     else
         {
          setStatus(EmergencyStatus.RESOLVED);
          setUnresolvedReason(null);
         }
    }
    public abstract boolean respond();
    public void markUnresolved(String reason ) {
        if (reason == null || reason.trim().isEmpty()) {
            System.out.println("reason is invalid");
        } else {
            setStatus(EmergencyStatus.UNRESOLVED);
            setUnresolvedReason(reason);
            System.out.println("marked unresolved for " + this.getId());
        }
    }


}
