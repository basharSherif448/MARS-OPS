package colony.events;

import facilities.Location;
//import colony.missions.MarsColony;

public abstract class  Emergency {
    private String id ;
    private int severity ;
    private EmergencyState state = EmergencyState.ACTIVE  ;
    private Location location ;
    private String  unresolvedReason;
    protected MarsColony marsColony;
    Emergency(String id, int severity, EmergencyState state, Location location, MarsColony marsColony) {
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
    public EmergencyState getState() {
        return state;
    }
    public  void updateState( EmergencyState state ) {
        this.state = state;
    }

    public void setStatus( EmergencyState state ) {
        this.state = state;
    }
    public void setUnresolvedReason( String unresolvedReason) {
       this.unresolvedReason = unresolvedReason;
    }
    public void resolve(){
     if (this.getState() == EmergencyState.RESOLVED){
         System.out.println("Emergency is already resolved");
     }
     else
         {
          setStatus(EmergencyState.RESOLVED);
          setUnresolvedReason(null);
         }
    }
    public abstract boolean respond();
    public void markUnresolved(String reason ) {
        if (reason == null || reason.trim().isEmpty()) {
            System.out.println("reason is invalid");
        } else {
            setStatus(EmergencyState.RESOLVED);
            setUnresolvedReason(reason);
            System.out.println("marked unresolved for " + this.getId());
        }
    }


}
