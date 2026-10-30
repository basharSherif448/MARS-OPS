package events;

import agents.Agent;
import colony.missions.MarsColony;

import java.util.ArrayList;
import java.util.List

public class EmergencyManager   {
    private List<Emergency> activeEmergencies;
    private Marscolony marscolony;

    EmergencyManager(MarsColony marsColony) {

        this.activeEmergencies = new ArrayList<>();
        this.marsColony = marsColony;
    }

    public List<Emergency> getActiveEmergencies() {
        return activeEmergencies;
    }

    public void report(Emergency e ) {
        if( e == null ) {
            System.out.println("Emergency  is null" );

        }
        else if ( activeEmergencies.contains(e) )
        {
            System.out.println(" Emergency already active.");
        }
        else if ( e.getStatus() != EmergencyStatus.ACTIVE ){
            System.out.println(" Emergency not active.");
        }
        else {
            activeEmergencies.add(e);
        }
    }
    public void update(){
        List<Emergency> EMERGENCYS =new ArrayList<>( activeEmergencies);// to avoid edit the main array
        for (Emergency emergency : EMERGENCYS) {
            if( emergency.getStatus() == EmergencyStatus.RESOLVED ) {
                activeEmergencies.remove(emergency);
            }
            else if( emergency.getStatus() == EmergencyStatus.UNRESOLVED ) {
                continue;
            }
            else if( emergency.getStatus() == EmergencyStatus.ACTIVE ) {
                continue;
            }
        }



    }
    public boolean respond(Emergency e) {

        if (e == null || !activeEmergencies.contains(e) || e.getStatus() != EmergencyStatus.ACTIVE) {
            return false;
        }

        return e.respond();
    }

    }



}
