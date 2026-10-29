package colony.events;

import java.util.ArrayList;
import java.util.List;


public class EmergencyManager   {
    private List<Emergency> activeEmergencies;
    private Marscolony marscolony;

    EmergencyManager() {
        this.activeEmergencies = new ArrayList<>();
    }
    public void report(Emergency e ) {
        if( e == null ) {
            System.out.println("Emergency report: null" );

        }
        else if ( activeEmergencies.contains(e) )
        {
            System.out.println("Emergency report: Emergency already active.");
        }
        else if ( e.getState() != EmergencyState.ACTIVE ){
            System.out.println("Emergency report: Emergency not active.");
        }
        else {
            activeEmergencies.add(e);
        }
    }
    public void update(){
        List<Emergency> EMERGENCYS =new ArrayList<>( activeEmergencies);
        for (Emergency emergency : EMERGENCYS) {
            if( emergency.getState() == EmergencyState.RESOLVED ) {
                activeEmergencies.remove(emergency);
            }
            else if( emergency.getState() == EmergencyState.UNHANDLED ) {
                continue;
            }
            else if( emergency.getState() == EmergencyState.ACTIVE ) {
                continue;
            }
        }



    }

    public boolean respond( Emergency   e) {
        if( e == null ) {
            System.out.println("Emergency report: null" );
            return false;

        }
        if ( e.getState() != EmergencyState.ACTIVE )
        {
            System.out.println("Emergency report: Emergency not active.");
            return false;
        }
        List<Agent> Agents = marscolony.getAgents();
        List<Agent> capableResponders = new ArrayList<>();
        for (Agent agent : Agents) {
            if (agent.isAvailable() && agent.canHandle(e)) {
                capableResponders.add(agent);
            }
        }
        int requiredRespondersCount = getRequiredResponders(e);

        if (capableResponders.size() < requiredRespondersCount) {
            e.markUnresolved("Insufficient count ");
            return false;
        }


        boolean isResponseSuccessful = e.respond();


        if (isResponseSuccessful) {
              return true;
        } else {
            e.markUnresolved("Response execution failed.");
            return false;
        }

    }

}
