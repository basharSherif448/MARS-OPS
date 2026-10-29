package colony.events;

import agents.Agent;
import agents.Human;
import agents.Robot;

import java.util.ArrayList;
import java.util.List;
// import colony.missions.MarsColony;

public class EmergencyManager   {
    private List<Emergency> activeEmergencies;
    private Marscolony marscolony;

    EmergencyManager(MarsColony marsColony) {

        this.activeEmergencies = new ArrayList<>();
        this.marsColony = marsColony;
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
            else if( emergency.getState() == EmergencyState.UNRESOLVED ) {
                continue;
            }
            else if( emergency.getState() == EmergencyState.ACTIVE ) {
                continue;
            }
        }



    }
    public boolean respond(Emergency e) {

        if (e == null || !activeEmergencies.contains(e) || e.getState() != EmergencyState.ACTIVE) {
            return false;
        }

        List<Agent> allAgents = marsColony.getAgents();
        List<Agent> capableResponders = new ArrayList<>();


        for (Agent agent : allAgents) {
            if (agent.isAvailable() && agent.canHandle(e)) {
                capableResponders.add(agent);
            }
        }

        if (!hasRequiredResponders(e, capableResponders)) {

            e.markUnresolved("Insufficient available or capable responders.");
            return false;
        }

        for (Agent responder : capableResponders) {
            responder.setLocation(e.getLocation());
        }


        return e.respond();
    }


    private boolean hasRequiredResponders(Emergency e, List<Agent> responders) {
        if (e instanceof OxygenLeakEmergency) {
            boolean hasHuman = false;
            boolean hasRobot = false;
            for (Agent a : responders) {
                if (a instanceof Human) hasHuman = true;
                if (a instanceof Robot) hasRobot = true;
            }
            return hasHuman && hasRobot;
        }
        else if (e instanceof FireEmergency) {
            return responders.size() >= 2;
        }
        return !responders.isEmpty();
    }

    }

}
