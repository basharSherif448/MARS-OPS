package colony.events;

public class RoverBreakdownEmergency extends Emergency {
    private rover ExplorationRover;
    RoverBreakdownEmergency(String id, int severity, EmergencyState state, String requiredSkill, rover ExplorationRover) {

        super(id, severity, state, requiredSkill);
        this.ExplorationRover = ExplorationRover;
    }
    @Override
    public boolean respond() {
        return false;
    }
}
